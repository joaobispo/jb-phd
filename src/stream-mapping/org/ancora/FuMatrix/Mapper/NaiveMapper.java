/*
 *  Copyright 2010 Ancora Research Group.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

package org.ancora.FuMatrix.Mapper;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.ancora.IntermediateRepresentation.Operand;
import org.ancora.IntermediateRepresentation.OperandType;
import org.ancora.IntermediateRepresentation.Operation;
import org.ancora.IntermediateRepresentation.OperationType;
import org.ancora.IntermediateRepresentation.Ssa;
import org.ancora.FuMatrix.Architecture.Fu;
import org.ancora.FuMatrix.Architecture.Area;
import org.ancora.FuMatrix.Architecture.FuCoor;
import org.ancora.FuMatrix.Architecture.FuOutputSignal;
import org.ancora.FuMatrix.Utils.AvaliabilityTable;
import org.ancora.FuMatrix.Utils.MoveService;
import org.ancora.FuMatrix.Utils.RegDefinitionsTable;
import org.ancora.IntermediateRepresentation.Operands.InternalData;
import org.ancora.IntermediateRepresentation.Operations.Move;

/**
 * First version of the mapper, without discerning between arithmetic and
 * memory operations;
 *
 * @author Joao Bispo
 */
public class NaiveMapper implements GeneralMapper {

   /**
    * Creates a new NaiveMapper with default max load/store of 1 per line
    * and infine columns for general lines
    */
   public NaiveMapper() {
      // Initialize avaliability tables
      avaliabilityTables = new EnumMap<Area, AvaliabilityTable>(Area.class);
      for(int i=0; i<defaultAreas.length; i++) {
         Area area = defaultAreas[i];
         avaliabilityTables.put(area, new AvaliabilityTable(defaultAreasSize[i]));
      }

      mappedOps = new ArrayList<Fu>();
      definitions = new RegDefinitionsTable();

      lastLineWithStore = -1;
      lastLineWithConditional = -1;
      maxCommDistance = DEFAULT_MAX_COMM_DISTANCE;

      useConditionalExitLimit = true;

//      replacedInputs = new HashMap<Integer, Operand>();
   }



   public boolean accept(Operation operation) {
   // Copy operation     
      operation = operation.copy();

      // Ignore nops
      if(operation.getType() == OperationType.Nop) {
         return true;
      }

      // Temporary solution?
      Area area = Area.getArea((OperationType) operation.getType());

      // Placement
      FuCoor coor = getCoordinate(operation, area);
      if(coor == null) {
         return false;
      }

     
      boolean success = insertMoves(operation, coor.getLine());
      if(!success) {
         Logger.getLogger(NaiveMapper.class.getName()).
                 warning("Mapping failed: could not insert the needed MOVE operations.");
         return false;
      }


      insertOperation(operation, coor);
      
      return true;
   }
   

   /**
    * Given an operation, returns the first line where it can be mapped.
    * 
    * @param operation
    * @return
    */
   private int calculateLine(Operation operation) {
      // Calculate line if is load

      if(operation.getType() == OperationType.MemoryLoad) {
         return getLineLoad(operation);
      }

      if(operation.getType() == OperationType.MemoryStore) {
         return getLineStore(operation);
      }
      
      if(operation.getType() == OperationType.ConditionalExit) {
         return getLineConditionalExit(operation);
      }

      // Calculate line if any other operation
      return getLineGeneral(operation);
      
   }

   /**
    * The given Line is assured to be the first avaliable
    * 
    * @param operation
    * @param line
    * @return
    */
   private int reserveColumn(Area area, int line) {
      int column = -1;

      column = avaliabilityTables.get(area).addColToLine(line);

      if(column == -1) {
         Logger.getLogger(NaiveMapper.class.getName()).
                 warning("Could not reserve column. Make sure the given line has " +
                 "columns avaliable.");
      }

      return column;
   }

   private int getLineLoad(Operation operation) {
      int lineIfInputs = getLineAccordingToInputs(operation);
      // Load has to be put after store
      int lineIfLastStore = lastLineWithStore + 1;
      // Check which is bigger: line or lastStoreLine + 1
      int possibleLine = Math.max(lineIfInputs, lineIfLastStore);

      // Get first avaliable line
      //return memoryLines.getFirstAvaliableFrom(possibleLine);
      return avaliabilityTables.get(Area.memory).getFirstAvaliableFrom(possibleLine);
   }

   private int getLineStore(Operation operation) {
      int lineIfInputs = getLineAccordingToInputs(operation);
      // Store has to be put after store
      int lineIfLastStore = lastLineWithStore + 1;
      int lineIfLastConditional = lastLineWithConditional + 1;
      // Check which is bigger: line or lastStoreLine + 1
      int possibleLine = Math.max(lineIfInputs, lineIfLastStore);
      possibleLine = Math.max(possibleLine, lineIfLastConditional);

      // Get first avaliable line
      //int firstAvaliableLine = memoryLines.getFirstAvaliableFrom(possibleLine);
      int firstAvaliableLine = avaliabilityTables.get(Area.memory).
              getFirstAvaliableFrom(possibleLine);
      // Update last store line
      updateLastLineWithStore(firstAvaliableLine);

      return firstAvaliableLine;
   }


   private int getLineConditionalExit(Operation operation) {
      int lineIfInputs = getLineAccordingToInputs(operation);
      // Conditional has to be put in line with store or last conditional
      int lineIfLastStore = lastLineWithStore;
      int lineIfLastConditional = lastLineWithConditional;
      // Check which is bigger: line or lastStoreLine
      int possibleLine = Math.max(lineIfInputs, lineIfLastStore);
      possibleLine = Math.max(lineIfInputs, lineIfLastConditional);
      // Get first avaliable line
      //return generalLines.getFirstAvaliableFrom(possibleLine);
      updateLastLineWithConditional(possibleLine);

      
      return avaliabilityTables.get(Area.general).
              getFirstAvaliableFrom(possibleLine);
   }



   private int getLineGeneral(Operation operation) {
      // Get line according to inputs
      int inputsLine = getLineAccordingToInputs(operation);

      // Get first avaliable line from this line
      //inputsLine = generalLines.getFirstAvaliableFrom(inputsLine);
      inputsLine = avaliabilityTables.get(Area.general).
              getFirstAvaliableFrom(inputsLine);


      return inputsLine;
   }


   private int getLineAccordingToInputs(Operation operation) {
      int maxLine = -1;

      // Check inputs
      List<Integer> lines = getInputLines(operation);
      for (Integer line : lines) {
         maxLine = Math.max(maxLine, line);
      }

      // This is the first line where we can put the operation, according to
      // position of inputs.

      return maxLine + 1;
   }


   /**
    * For each input, insert the needed move operations from the input to the output.
    *
    * @param operation
    * @param line
    */
   private boolean insertMoves(Operation operation, int line) {
//      replacedInputs = new HashMap<Integer, Operand>();
      if(MoveService.isMaxCommDistanceInfinite(maxCommDistance)) {
         return true;
      }

      int moveAddress = operation.getAddress();


      // For each input, verify if there is a path from the output to the input
      for(int i=0; i<operation.getInputs().size(); i++) {
         Operand input = operation.getInputs().get(i);
         if(input.getType() != OperandType.internalData) {
            continue;
         }

         int bits = input.getBits();
         //String moveOpName = Ssa.buildSsaName("moveReg"+i, 0);
         String moveOpName = Ssa.buildSsaName(MoveService.MOVE_REG_PREFIX+i, 0);

         // Get position of register definition producer
         String registerName = Ssa.getOriginalName(input.getName());
         int sourceLine = definitions.getLine(registerName);

         // Check if there is a path from sourceline to line
         if(MoveService.canLinesCommunicate(sourceLine, line, maxCommDistance)) {
            continue;
         }

         List<FuCoor> moveCoordinates = MoveService.buildPath(sourceLine, line,
                 maxCommDistance, avaliabilityTables);

         if(moveCoordinates == null) {
            Logger.getLogger(NaiveMapper.class.getName()).
                    warning("Cannot insert moves");
            return false;
         }

         // For each move, insert an operation
         boolean firstTime = true;
         Operand moveOperand = new InternalData(moveOpName, bits);
         for(FuCoor moveCoor : moveCoordinates) {
            Operation moveOperation = null;
            if(firstTime) {
               moveOperation = new Move(moveAddress, input, moveOperand);
               firstTime = false;
            } else {
               moveOperation = new Move(moveAddress, moveOperand, moveOperand);
            }
            insertOperation(moveOperation, moveCoor);
         }

         // Change input of operation to move

         operation.replaceInput(i, moveOperand);
//         replacedInputs.put(i, input);
      }

      return true;
   }

 private void insertOperation(Operation operation, FuCoor coor) {
      //System.err.println("Inserting Operation:");
      //System.err.println(operation.getFullOperation());
      // Process outputs
      registerOutputs(operation, coor);

      // Generate FU
      Fu newFu = Fu.buildFu(coor, operation, definitions);

      // Add FU to list
      mappedOps.add(newFu);
/*
      // Restore inputs of operation that were replaced with moves.
      for(Integer key : replacedInputs.keySet()) {
         operation.replaceInput(key, replacedInputs.get(key));
      }
 * 
 */
   }

   /**
    *
    * @param operation
    * @param area
    */
   private FuCoor getCoordinate(Operation operation, Area area) {
      int line = calculateLine(operation);
      if(line < 0) {
         Logger.getLogger(NaiveMapper.class.getName()).
                 warning("Mapping failed: could not found an avaliable line.");
         return null;
      }

      //int col = reserveColumn(operation, line);
      int col = reserveColumn(area, line);
      if(col < 0) {
         Logger.getLogger(NaiveMapper.class.getName()).
                 warning("Mapping failed: could not get column for line '"+line+"'.");
         return null;
      }



      // Build Fu Coordinate
      FuCoor coor = new FuCoor(col, line, area);
      return coor;
   }

   /**
    * Input position -> line correspondent to its output
    *
    * @param operation
    * @return
    */
   private List<Integer> getInputLines(Operation operation) {
      List<Integer> lines = new ArrayList<Integer>();
      for (int i=0; i<operation.getInputs().size(); i++) {
         Operand operand = operation.getInputs().get(i);

         // Ignore other types other than internal data
         if (operand.getType() != OperandType.internalData) {
            continue;
         }

         // Check distance between input/output
         // Get register
         String registerName = Ssa.getOriginalName(operand.getName());
         // Get line
         int outputLine = definitions.getLine(registerName);
         if(outputLine == -1) {
            //System.err.println("Op:"+operation.getFullOperation());
            continue;
         }

         lines.add(outputLine);
      }

      return lines;
   }


   public List<Fu> getMappedOps() {
      return mappedOps;
   }

   public void setAvaliabilityTableSize(int maxColumnSize, Area area) {
      avaliabilityTables.put(area, new AvaliabilityTable(maxColumnSize));
   }

      /**
    * Each output of the type 'internalData' need to be registred in the
    * definitions table.
    *
    * @param operation
    * @param coor
    */
   private void registerOutputs(Operation operation, FuCoor coor) {
      List<Operand> outputs = operation.getOutputs();
      for(int i=0; i<outputs.size(); i++) {
         Operand output = outputs.get(i);

         if(output.getType() != OperandType.internalData) {
            continue;
         }

         String registerName = Ssa.getOriginalName(output.getName());
         // Build FuOutput
         FuOutputSignal fuOutput = new FuOutputSignal(coor, i);

         // Update definitions
         definitions.put(registerName, fuOutput);

      }
   }

   public RegDefinitionsTable getDefinitions() {
      return definitions;
   }

   public int getLiveouts() {
      int liveouts = 0;

      for(String key : definitions.getDefinitions().keySet()) {
         if(key.startsWith(MoveService.MOVE_REG_PREFIX)) {
            continue;
         }
         liveouts++;
      }

      return liveouts;
   }

   public void setMaxCommDistance(int maxCommDistance) {
      this.maxCommDistance = maxCommDistance;
   }



   public void setMaxColGeneral(int maxColumns) {
      setAvaliabilityTableSize(maxColumns, Area.general);
   }

   public void setMaxColMemory(int maxColumns) {
      setAvaliabilityTableSize(maxColumns, Area.memory);
   }

   private void updateLastLineWithConditional(int possibleLine) {
      if(useConditionalExitLimit) {
         lastLineWithConditional = possibleLine;
      }
   }

   public void setUseConditionalExitLimit(boolean useConditionalExitLimit) {
      this.useConditionalExitLimit = useConditionalExitLimit;
   }

   private void updateLastLineWithStore(int firstAvaliableLine) {
      lastLineWithStore = firstAvaliableLine;
   }
   

   /**
    * INSTANCE VARIABLES
    */
   private List<Fu> mappedOps;
   // For each line, indicates which is the last occupied column
   private Map<Area, AvaliabilityTable> avaliabilityTables;
   // The areas considered for this mapper
   private static final Area[] defaultAreas = {Area.general, Area.memory};
   private static final int[] defaultAreasSize = {-1, 1};
   private static final int DEFAULT_MAX_COMM_DISTANCE = 1;

   // SSA InternalData Register Number -> FuOutput / Line
   private RegDefinitionsTable definitions;
   private int maxCommDistance;

   private int lastLineWithStore;
   private int lastLineWithConditional;

   private boolean useConditionalExitLimit;

}

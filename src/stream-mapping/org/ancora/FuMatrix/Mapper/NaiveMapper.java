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
import java.util.HashMap;
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
      avaliabilityTables = new EnumMap<Area, AvaliabilityTable>(Area.class);
      for(int i=0; i<defaultAreas.length; i++) {
         Area area = defaultAreas[i];
         avaliabilityTables.put(area, new AvaliabilityTable(defaultAreasSize[i]));
      }
      //generalLines = new AvaliabilityTable(-1);
      //memoryLines = new AvaliabilityTable(1);

      mappedOps = new ArrayList<Fu>();
      //definitions = new HashMap<String, FuOutput>();
      definitions = new RegDefinitionsTable();

      lastLineWithStore = -1;
      commMaxDistance = 0;

      replacedInputs = new HashMap<Integer, Operand>();
   }



   public boolean accept(Operation operation) {

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

      /*
      int line = calculateLine(operation);
      if(line < 0) {
         Logger.getLogger(NaiveMapper.class.getName()).
                 warning("Mapping failed: could not found an avaliable line.");
         return false;
      }

      //int col = reserveColumn(operation, line);
      int col = reserveColumn(area, line);
      if(col < 0) {
         Logger.getLogger(NaiveMapper.class.getName()).
                 warning("Mapping failed: could not get column for line '"+line+"'.");
         return false;
      }
      


      // Build Fu Coordinate
      FuCoor coor = new FuCoor(col, line, area);
*/
      // Can insert moves?
      //calcMoves(operation, line);

      //boolean success = insertMoves(operation, line);
      boolean success = insertMoves(operation, coor.getLine());
      if(!success) {
         Logger.getLogger(NaiveMapper.class.getName()).
                 warning("Mapping failed: could not insert the needed MOVE operations.");
         return false;
      }


      insertOperation(operation, coor);
      /*
      //System.err.println("Line:"+line);
      //System.err.println("Column:"+col);

      // Process outputs
      registerOutputs(operation, coor);

      Fu newFu = Fu.buildFu(coor, operation, definitions);
      //System.err.println("Coor Line:"+coor.getLine());
      //System.err.println("Coor Col:"+coor.getCol());
      mappedOps.add(newFu);
*/
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
   //private int reserveColumn(Operation operation, int line) {
   private int reserveColumn(Area area, int line) {
      int column = -1;
/*
      // Calculate col if memory operation
      if (operation.getType() == OperationType.MemoryLoad
              || operation.getType() == OperationType.MemoryStore) {
         column = memoryLines.addColToLine(line);
      } else {
         // Calculate line if any other operation
         //int firstAvaliableLine = generalLines.getFirstAvaliableFrom(line);
         column = generalLines.addColToLine(line);
      }
*/
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
      // Check which is bigger: line or lastStoreLine + 1
      int possibleLine = Math.max(lineIfInputs, lineIfLastStore);

      // Get first avaliable line
      //int firstAvaliableLine = memoryLines.getFirstAvaliableFrom(possibleLine);
      int firstAvaliableLine = avaliabilityTables.get(Area.memory).
              getFirstAvaliableFrom(possibleLine);
      // Update last store line
      lastLineWithStore = firstAvaliableLine;

      return firstAvaliableLine;
   }


   private int getLineConditionalExit(Operation operation) {
      int lineIfInputs = getLineAccordingToInputs(operation);
      // Conditional has to be put in line with store
      int lineIfLastStore = lastLineWithStore;
      // Check which is bigger: line or lastStoreLine
      int possibleLine = Math.max(lineIfInputs, lineIfLastStore);
      // Get first avaliable line
      //return generalLines.getFirstAvaliableFrom(possibleLine);
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
   //private Operation insertMoves(Operation operation, int line) {
   private boolean insertMoves(Operation operation, int line) {
   //private Map<Integer, Operand> insertMoves(Operation operation, int line) {
      replacedInputs = new HashMap<Integer, Operand>();
      //List<Fu> moveOperations = new ArrayList<Fu>();
      if(isMaxCommDistanceInfinite()) {
         return true;
         //return replacedInputs;
      }
      // Copy general table
      //AvaliabilityTable testTable = generalLines.copy();
      //AvaliabilityTable testTable = avaliabilityTables.get(Area.general).copy();

      // For now, Moves can only put moves on GeneralTable
      // !! This functionality was moved to method 'buildPath'
      //Area moveArea = Area.general;
      int moveAddress = operation.getAddress();

      //AvaliabilityTable generalTable = avaliabilityTables.get(moveArea);

      // For each input, verify if there is a path from the output to the input
      //for(Operand input : operation.getInputs()) {

      for(int i=0; i<operation.getInputs().size(); i++) {
         Operand input = operation.getInputs().get(i);
         if(input.getType() != OperandType.internalData) {
            continue;
         }

         int bits = input.getBits();
         String moveOpName = Ssa.buildSsaName("moveReg"+i, 0);

         // Get position of register definition producer
         String registerName = Ssa.getOriginalName(input.getName());
         int sourceLine = definitions.getLine(registerName);

         // Check if there is a path from sourceline to line
         //int distance = line-sourceLine;
         if(isCommReachable(sourceLine, line)) {
            continue;
         }

         //List<Integer> moveLines = buildPath(sourceLine, line, generalTable);
         List<FuCoor> moveCoordinates = buildPath(sourceLine, line, avaliabilityTables);

         if(moveCoordinates == null) {
            Logger.getLogger(NaiveMapper.class.getName()).
                    warning("Cannot insert moves");
            return false;
            //return null;
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
         replacedInputs.put(i, input);
         // Generate Move Operations
         //List<Fu> localMoveOperations = buildMoveOperations(input, moveLines);
         //moveOperations.addAll(localMoveOperations);
      }

      return true;
      //return replacedInputs;
   }

   /**
    * Input position -> line correspondent to its output
    *
    * @param operation
    * @return
    */
   /*
   private Map<Integer, Integer> getInputLines(Operation operation) {
      Map<Integer, Integer> lines = new HashMap<Integer, Integer>();
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
         Integer outputLine = definitions.getLine(registerName);
         if(outputLine == null) {
            Logger.getLogger(NaiveMapper.class.getName()).
                    warning("InternalData '"+operand.getName()+"' not defined yet.");
            continue;
         }

         lines.put(i, outputLine);
      }

      return lines;
   }
   */

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
            continue;
         }

         lines.add(outputLine);
      }

      return lines;
   }


   public List<Fu> getMappedOps() {
      return mappedOps;
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
   //private AvaliabilityTable generalLines;
   // Line, last occupied column
   //private AvaliabilityTable memoryLines;
   // SSA InternalData Register Number -> FuOutput / Line
   private RegDefinitionsTable definitions;
   private int commMaxDistance;

   private int lastLineWithStore;

   private Map<Integer, Operand> replacedInputs;

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

   public void setAvaliabilityTableSize(int maxColumnSize, Area area) {
      avaliabilityTables.put(area, new AvaliabilityTable(maxColumnSize));
   }
/*
   public void setMaxMemoryColSize(int maxMemoryColSize) {
      memoryLines = new AvaliabilityTable(maxMemoryColSize);
      //this.maxMemoryColSize = maxMemoryColSize;
   }

   public void setMaxGeneralColSize(int maxGeneralColSize) {
      generalLines = new AvaliabilityTable(maxGeneralColSize);
      //this.maxGeneralColSize = maxGeneralColSize;
   }
*/
  /*
   private List<Fu> calcMoves(Operation operation, int line) {
      List<Fu> moveOperations = new ArrayList<Fu>();
      if(isMaxCommDistanceInfinite()) {
         return moveOperations;
      }
      // Copy general table
      //AvaliabilityTable testTable = generalLines.copy();
      AvaliabilityTable testTable = avaliabilityTables.get(Area.general).copy();
      // For each input, verify if there is a path from the output to the input
      for(Operand input : operation.getInputs()) {
         if(input.getType() != OperandType.internalData) {
            continue;
         }
         
         // Get position of register definition producer
         String registerName = Ssa.getOriginalName(input.getName());
         int sourceLine = definitions.getLine(registerName);
         
         // Check if there is a path from sourceline to line
         //int distance = line-sourceLine;
         if(isCommReachable(sourceLine, line)) {
            continue;
         }
         
         //if(distance <= commMaxDistance) {
         //   continue;
         //}
         
         //System.err.println("Needs Moves:");
         //System.err.println("Source Line:"+sourceLine);
         //System.err.println("Destination Line:"+line);
         //System.err.println("BEGIN-------------------");
         List<Integer> moveLines = buildPath(sourceLine, line, testTable);
         //System.err.println("END-------------------");
         //System.err.println("Moves:"+moveLines);
         if(moveLines == null) {
            Logger.getLogger(NaiveMapper.class.getName()).
                    warning("Cannot insert moves");
            return null;
         }

         // Generate Move Operations
         List<Fu> localMoveOperations = buildMoveOperations(input, moveLines);
         moveOperations.addAll(localMoveOperations);
      }

      return moveOperations;
   }
*/
   private boolean isMaxCommDistanceInfinite() {
      if(commMaxDistance < 1) {
         return true;
      } else {
         return false;
      }
   }

   /*
   private boolean isCommReachable(int distance) {
      if(isMaxCommDistanceInfinite()) {
         return true;
      }

      if (distance < commMaxDistance) {
         return true;
      } else {
         return false;
      }
   }
    * 
    */

   /**
    *
    * @param sourceLine
    * @param destinationLine
    * @param testTable
    * @return a list of lines where MOVEs should be put, or null if a path could
    * not be done.
    */
   private List<FuCoor> buildPath(int sourceLine, int destinationLine, Map<Area,AvaliabilityTable> tables) {
      // Currently, MOVES only go along the general area
      // TODO: Generalize for all areas?
      Area defaultArea = Area.general;
      AvaliabilityTable testTable = tables.get(defaultArea);

      List<Integer> moveLines = new ArrayList<Integer>();
      List<Integer> moveColumns = new ArrayList<Integer>();

      //int distance = destinationLine - sourceLine - 1;
      int currentSource = sourceLine;
      int firstCandidateLine = sourceLine+1;
      //int distance = destinationLine - firstCandidateLine;
      //System.err.println("InitialDistance:"+distance);
      while(!isCommReachable(currentSource, destinationLine)) {
         //System.err.println("Current Source:"+currentSource);
         //System.err.println("First Candidate Line:"+firstCandidateLine);
         //System.err.println("Destination Line:"+destinationLine);
         // Get possible lines for the given max comm
         List<Integer> avaliableLines = testTable.getAvaliableLines(firstCandidateLine, commMaxDistance);
         if(avaliableLines.isEmpty()) {
            return null;
         }

         // Choose the line which has the least communication
         //System.err.println("Candidate Lines:"+avaliableLines);
         currentSource = testTable.lessOccupiedLine(avaliableLines);
         //System.err.println("New Source:"+currentSource);

         // Line for MOVE chosen. Add to test table and update current source and distance
         
         // DO NOT ADD TO TEST TABLE YET
         //testTable.addColToLine(currentSource);
         moveLines.add(currentSource);
         int moveColumn = testTable.addColToLine(currentSource);
         moveColumns.add(moveColumn);
         firstCandidateLine = currentSource+1;
         //distance = destinationLine - currentSource - 1;
         
      }

      // Build Coordinates
      List<FuCoor> fuCoordinates = new ArrayList<FuCoor>(moveLines.size());
      for(int i=0; i<moveLines.size(); i++) {
         fuCoordinates.add(new FuCoor(moveColumns.get(i), moveLines.get(i), defaultArea));
      }

      //return moveLines;
      return fuCoordinates;
   }

   /**
    * 
    * 
    * @param sourceLine
    * @param destinationLine
    * @return
    */
   private boolean isCommReachable(int sourceLine, int destinationLine) {
      if(isMaxCommDistanceInfinite()) {
         return true;
      }

      int maxLine = sourceLine + commMaxDistance;
      if(maxLine >= destinationLine) {
         return true;
      } else {
         return false;
      }
      //int distance = destinationLine - sourceLine;
      // MaxComm indicates to how many lines can the sourceLine communicate
   }

   private List<Fu> buildMoveOperations(Operand input, List<Integer> moveLines) {
      throw new UnsupportedOperationException("Not yet implemented");
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

      // Restore inputs of operation that were replaced with moves.
      for(Integer key : replacedInputs.keySet()) {
         operation.replaceInput(key, replacedInputs.get(key));
      }
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
}

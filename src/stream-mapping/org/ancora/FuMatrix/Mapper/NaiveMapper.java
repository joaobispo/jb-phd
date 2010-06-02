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
      generalLines = new AvaliabilityTable(-1);
      memoryLines = new AvaliabilityTable(1);

      mappedOps = new ArrayList<Fu>();
      //definitions = new HashMap<String, FuOutput>();
      definitions = new RegDefinitionsTable();

      lastLineWithStore = -1;
   }



   public void accept(Operation operation) {

      // Ignore nops
      if(operation.getType() == OperationType.Nop) {
         return;
      }

      // Placement
      int line = calculateLine(operation);
      if(line < 0) {
         Logger.getLogger(NaiveMapper.class.getName()).
                 warning("Could not get line:"+line);
      }
      //insertMoves(operation, line);
      int col = calculateColumn(operation, line);
      if(col < 0) {
         Logger.getLogger(NaiveMapper.class.getName()).
                 warning("Could not get column:"+col);
      }
      
      // Temporary solution?
      Area area = Area.getArea((OperationType) operation.getType());

      // Build Fu Coordinate
      FuCoor coor = new FuCoor(col, line, area);

      //System.err.println("Line:"+line);
      //System.err.println("Column:"+col);

      // Process outputs
      updateTables(operation, coor);

/*
      Map<FuOutput, FuInput> internalRouting = buildInternalRouting(operation, coor);
      Map<String, FuInput> externalRouting = buildExternalRouting(operation, coor);
      Map<String,FuOutput> currentLiveouts = buildLiveouts(operation, coor);

      Fu newFu = new Fu(coor, internalRouting, externalRouting, operation, currentLiveouts);
 *
 */
      Fu newFu = Fu.buildFu(coor, operation, definitions);
      //System.err.println("Coor Line:"+coor.getLine());
      //System.err.println("Coor Col:"+coor.getCol());
      mappedOps.add(newFu);
   }
   
   
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
    * Line is assured to be the first avaliable
    * 
    * @param operation
    * @param line
    * @return
    */
   private int calculateColumn(Operation operation, int line) {
      // Calculate col if memory operation
      if (operation.getType() == OperationType.MemoryLoad ||
              operation.getType() == OperationType.MemoryStore) {
         return memoryLines.addColToLine(line);
      }
       
      // Calculate line if any other operation
      //int firstAvaliableLine = generalLines.getFirstAvaliableFrom(line);
      return generalLines.addColToLine(line);
   }

   private int getLineLoad(Operation operation) {
      int lineIfInputs = getLineAccordingToInputs(operation);
      // Load has to be put after store
      int lineIfLastStore = lastLineWithStore + 1;
      // Check which is bigger: line or lastStoreLine + 1
      int possibleLine = Math.max(lineIfInputs, lineIfLastStore);

      // Get first avaliable line
      return memoryLines.getFirstAvaliableFrom(possibleLine);
   }

   private int getLineStore(Operation operation) {
      int lineIfInputs = getLineAccordingToInputs(operation);
      // Store has to be put after store
      int lineIfLastStore = lastLineWithStore + 1;
      // Check which is bigger: line or lastStoreLine + 1
      int possibleLine = Math.max(lineIfInputs, lineIfLastStore);

      // Get first avaliable line
      int firstAvaliableLine = memoryLines.getFirstAvaliableFrom(possibleLine);
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
      return generalLines.getFirstAvaliableFrom(possibleLine);
   }



   private int getLineGeneral(Operation operation) {
      // Get line according to inputs
      int inputsLine = getLineAccordingToInputs(operation);

      // Get first avaliable line from this line
      inputsLine = generalLines.getFirstAvaliableFrom(inputsLine);


      return inputsLine;
   }


   private int getLineAccordingToInputs(Operation operation) {
      int maxLine = -1;

      // Check inputs
      Map<Integer, Integer> lines = getInputLines(operation);
      for (Integer line : lines.values()) {
         maxLine = Math.max(maxLine, line);
      }
 /*
      for(Operand operand : operation.getInputs()) {
         // Ignore other types other than internal data
         if(operand.getType() != OperandType.internalData) {
            continue;
         }

         // Get register
         String registerName = Ssa.getOriginalName(operand.getName());
         // Get line
         //FuOutput fuOutput = definitions.get(registerName);
         Integer outputLine = definitionsLine.get(registerName);

         // If null, something went wrong.
         //if(fuOutput == null) {
         if(outputLine == null) {
            Logger.getLogger(NaiveMapper.class.getName()).
                    warning("InternalData '"+operand.getName()+"' not defined yet.");
            return -1;
         }

         //maxLine = Math.max(maxLine, fuOutput.getCoordinate().getLine());
         maxLine = Math.max(maxLine, outputLine);
      }
*/
      // This is the first line where we can put the operation, according to
      // position of inputs.

      return maxLine + 1;
   }


   /**
    * For each input, insert a move operation from the input to the output.
    *
    * @param operation
    * @param line
    */
   private void insertMoves(Operation operation, int line) {

   }

   /**
    * Input position -> line correspondent to its output
    *
    * @param operation
    * @return
    */
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
         //Integer outputLine = definitionsLine.get(registerName);
         //Integer outputLine = definitions.get(registerName).getCoordinate().getLine();
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

   /**
    * This method is not respecting Fu inputs order
    *
    * @param operation
    * @param coor
    * @return
    */
  /*
   private Map<FuOutput, FuInput> buildInternalRouting(Operation operation, FuCoor coor) {
      Map<FuOutput, FuInput> internalRoute = new HashMap<FuOutput, FuInput>();



      Map<Integer, Integer> lines = getInputLines(operation);

      //int counter = 0;
      for(Integer inputPosition : lines.keySet()) {
         // Build FuInput
         FuInput fuInput = new FuInput(coor, inputPosition);
         //counter++;

         String registerName = operation.getInputs().get(inputPosition).getName();
         // Get FuOutput
         FuOutput fuOutput = definitions.get(registerName);
         internalRoute.put(fuOutput, fuInput);
      }

      if(internalRoute.isEmpty()) {
         return null;
      }

      return internalRoute;
   }
*/
   public List<Fu> getMappedOps() {
      return mappedOps;
   }

   

   /**
    * INSTANCE VARIABLES
    */
   private List<Fu> mappedOps;
   // Line, last occupied column
   //private Map<Integer, Integer> generalLines;
   private AvaliabilityTable generalLines;
   // Line, last occupied column
   //private Map<Integer, Integer> memoryLines;
   private AvaliabilityTable memoryLines;
   // Register -> FuOutput
   //private Map<String, FuOutput> liveouts;
   // SSA InternalData Register Number -> FuOutput / Line
   //private Map<String, FuOutput> definitions;
   private RegDefinitionsTable definitions;
   //private Map<String, Integer> definitionsLine;

   private int lastLineWithStore;

   private void updateTables(Operation operation, FuCoor coor) {
      List<Operand> outputs = operation.getOutputs();
      for(int i=0; i<outputs.size(); i++) {
         Operand output = outputs.get(i);
         if(output.getType() != OperandType.internalData) {
            continue;
         }

         String registerName = Ssa.getOriginalName(output.getName());
         // Build FuOutput
         //FuOutput fuOutput = new FuOutput(coor, i);
         FuOutputSignal fuOutput = new FuOutputSignal(coor, i);

         // Update definitions
         //definitionsLine.put(registerName, coor.getLine());
         definitions.put(registerName, fuOutput);

         // Update liveouts -> The same as definitions!!
         //liveouts.put(registerName, fuOutput);
      }
   }

   public void setMaxMemoryColSize(int maxMemoryColSize) {
      memoryLines = new AvaliabilityTable(maxMemoryColSize);
      //this.maxMemoryColSize = maxMemoryColSize;
   }

   public void setMaxGeneralColSize(int maxGeneralColSize) {
      generalLines = new AvaliabilityTable(maxGeneralColSize);
      //this.maxGeneralColSize = maxGeneralColSize;
   }
/*
   private Map<String, FuInput> buildExternalRouting(Operation operation, FuCoor coor) {
      Map<String, FuInput> externalRoute = new HashMap<String, FuInput>();

      // Iterate over inputs search for LiveIns
      for(int i=0; i<operation.getInputs().size(); i++) {
         Operand operand = operation.getInputs().get(i);
         if(operand.getType() != OperandType.livein) {
            continue;
         }

         // Build FuInput
         FuInput fuInput = new FuInput(coor, i);

         externalRoute.put(operand.getName(), fuInput);
      }

      if(externalRoute.isEmpty()) {
         return null;
      }

      return externalRoute;
   }
*/
   /*
   private Map<String, FuOutput> buildLiveouts(Operation operation, FuCoor coor) {
      if(operation.getType() != OperationType.ConditionalExit) {
         return null;
      }

      // Make copy of current liveout table
      Map<String, FuOutput> currentLiveouts = new HashMap<String, FuOutput>();
//      for(String key : liveouts.keySet()) {
//         currentLiveouts.put(key, liveouts.get(key));
      for(String key : definitions.keySet()) {
         currentLiveouts.put(key, definitions.get(key));
      }

      return currentLiveouts;
   }
*/
   /*
   private Area getArea(Operation operation) {
       if (operation.getType() == OperationType.MemoryLoad ||
              operation.getType() == OperationType.MemoryStore) {
         return Area.memory;
      }

       return Area.general;
   }
    * 
    */






   

   /**
    * DEFINITIONS
    */
   //private int maxMemoryColSize = 1;
   //private int maxGeneralColSize = -1;





}

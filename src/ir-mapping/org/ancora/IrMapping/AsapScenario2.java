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

package org.ancora.IrMapping;

import org.ancora.IrMapping.Mapper;
import java.util.ArrayList;
import java.util.HashSet;
import org.ancora.IntermediateRepresentation.*;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
//import org.ancora.IntermediateRepresentation.Operands.MbImmutableTest;
//import org.ancora.IntermediateRepresentation.Operations.MbMemoryTest;
import org.ancora.IntermediateRepresentation.OperationType;

/**
 *
 * @author Joao Bispo
 */
public class AsapScenario2 implements Mapper {

   /**
    * Creates a MicroBlaze ILP Scenario 2 with MicroBlaze Immutable and Memory Tests
    */
   /*
   public AsapScenario2() {
      //this(new MbImmutableTest(), new MbMemoryTest());
      this(new MbMemoryTest());
   }
    */



//   public AsapScenario2(ImmutableTest immutableTest, MemoryTest memoryTest) {
   //public AsapScenario2(MemoryTest memoryTest) {
   public AsapScenario2() {
//      this.immutableTest = immutableTest;
      //this.memoryTest = memoryTest;
      //this.numLoads = numLoads;
      this.mapMoves = false;

   }

   public void processOperations(List<Operation> operations) {
      //reset();

      for(Operation operation : operations) {
         if(operation.getType() == OperationType.Nop) {
            continue;
         }

         /*
         if(!mapMoves) {
            if(operation.getType() == OperationType.Move) {
               continue;
            }
         }
          */

         // Given the inputs, find the lower possible line where the operation
         // can be put.
         int lowestOperandsLine = processInputs(operation.getInputs());
         //System.out.println("Operation:"+operation);
         //System.out.println("Inputs:"+operation.getInputs());
         //System.out.println("Outputs:"+operation.getOutputs());

         // Calculate operation line, taking into account that it can be a
         // memory operation
         int operationLine  = calculateLineWithMemoryOperations(operation, lowestOperandsLine);
         // Destination line will be lowestLine + 1

         processOutputs(operation.getOutputs(), operationLine);

         // Update number of lines
         usedLines = Math.max(usedLines, operationLine);
         // Update mappedOps
         mappedOps++;
         // Add operation to mapping table
         updateMapping(operation, operationLine);
      }
   }

   private int processInputs(List<Operand> operands) {
      int lowestLine = 0;
      for(Operand operand : operands) {
         // Check if operand is immutable
//         if(immutableTest.isImmutable(operand)) {
         if(operand.isImmutable()) {
            continue;
         }

         // Get line where this operand was created
         //String operandRepresentation = operand.toString();
         String operandRepresentation = operand.toString();
         //String operandRepresentation = operand.getValue();
         Integer line = dataLines.get(operandRepresentation);

         // Check if Live-In
         if(line == null) {
            line = 0;
            dataLines.put(operandRepresentation, line);
            updateLiveins(operandRepresentation);
            //System.out.println("Added Live-In:"+operandRepresentation);
         }

         lowestLine = Math.max(lowestLine, line);
      }

      return lowestLine;
   }

   private int calculateLineWithMemoryOperations(Operation operation, int lowestOperandsLine) {
      if(!IrUtils.isMemoryOperation(operation)) {
         return lowestOperandsLine + 1;
      }


      int operationLine = Math.max(lowestOperandsLine, lastLineWithStore);

      if(IrUtils.isLoad(operation)) {
         operationLine = Math.max(lowestOperandsLine, lastLineWithStore);
      }

      // Put operation on the line below
      operationLine++;

      // Only update last line with memory operation if it is a store
      //if(memoryTest.isStore(operation)) {
      if(IrUtils.isStore(operation)) {
         lastLineWithStore = operationLine;
      }
      
      return operationLine;
   }

   private void processOutputs(List<Operand> operands, int operationLine) {
      for(Operand operand : operands) {
         // Check if operand is immutable
//         if(immutableTest.isImmutable(operand)) {
         if(operand.isImmutable()) {
            continue;
         }

         // Update consumer table
         //String operandRepresentation = operand.toString();
         //String operandRepresentation = operand.getValue();
         String operandRepresentation = operand.toString();
         dataLines.put(operandRepresentation, operationLine);

         //Update Liveouts
         updateLiveouts(operandRepresentation);
      }
   }

   private void updateLiveouts(String operandRepresentation) {
      // Remove SSA suffix
      int separatorIndex = operandRepresentation.lastIndexOf(".");
      if (separatorIndex != -1) {
         operandRepresentation = operandRepresentation.substring(0, separatorIndex);
      }
      liveOuts.add(operandRepresentation);
   }

   private void updateLiveins(String operandRepresentation) {
      // Remove SSA suffix
      int separatorIndex = operandRepresentation.lastIndexOf(".");
      if (separatorIndex != -1) {
         operandRepresentation = operandRepresentation.substring(0, separatorIndex);
      }
      liveIns.add(operandRepresentation);
   }

   public void reset() {

      mappedOps = 0;
      usedLines = 0;
      lastLineWithStore = 0;
      dataLines = new Hashtable<String, Integer>();

      liveIns = new HashSet<String>();
      liveOuts = new HashSet<String>();

      mapping = new Hashtable<Integer, List<Operation>>();
   }


   public int getLiveIns() {
//      System.out.println("Liveins:"+liveIns);
      return liveIns.size();
   }

   public int getLiveOuts() {
     /*
      int liveOuts = 0;
      //Set<String> liveOuts = new HashSet<String>();

      for(String key : dataLines.keySet()) {
         int linePos = dataLines.get(key);
         // A line bigger than 0 means it was written
         if(linePos > 0) {
            liveOuts++;
         }
      }
*/
//      System.out.println("Liveouts:"+liveOuts);
      return liveOuts.size();
   }

   public double getIlp() {
      return (double)mappedOps/(double)usedLines;
   }

   public int getNumberOfOps() {
      return mappedOps;
   }

   public int getNumberOfLines() {
      return usedLines;
   }


   public void printStats() {
      StringBuilder builder = new StringBuilder();

      builder.append("ILP:"+getIlp()+"\n");
      builder.append("Live-Ins:"+getLiveIns()+"\n");
      builder.append("Live-Outs:"+getLiveOuts()+"\n");


      System.out.println(builder);
   }

   private void updateMapping(Operation operation, int operationLine) {
      List<Operation> operations = mapping.get(operationLine);
      if(operations == null) {
         operations = new ArrayList<Operation>();
         mapping.put(operationLine, operations);
      }

      operations.add(operation);
   }

   public Map<Integer, List<Operation>> getMapping() {
      return mapping;
   }

   /**
    * TODO: Consider if this is part of Mapper interface.
    * @param mapMoves
    */
   public void setMapMoves(boolean mapMoves) {
      this.mapMoves = mapMoves;
   }

   

   /**
    * INSTANCE VARIABLES
    */
   private boolean mapMoves;
   //private int numLoads;
   private int mappedOps;
   private int usedLines;
   private int lastLineWithStore;

   private Map<String,Integer> dataLines;

   //private Set<Integer> liveIns;
   //private Set<Integer> liveOuts;
   private Set<String> liveIns;
   private Set<String> liveOuts;

   //private ImmutableTest immutableTest;
   //private MemoryTest memoryTest;

   private Map<Integer, List<Operation>> mapping;









}

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

package org.ancora.StreamTransform;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.ancora.IntermediateRepresentation.Operand;
import org.ancora.IntermediateRepresentation.OperandType;
import org.ancora.IntermediateRepresentation.Operation;
import org.ancora.IntermediateRepresentation.Operations.MemoryLoad;
import org.ancora.IntermediateRepresentation.Operations.MemoryStore;
import org.ancora.IntermediateRepresentation.Operations.Nop;
import org.ancora.IntermediateRepresentation.OperationType;
import org.ancora.IntermediateRepresentation.Ssa;
import org.ancora.StreamTransform.DataStructures.MemoryTable;

/**
 *
 * @author Joao Bispo
 */
public class RemoveInternalLoads2 extends StreamTransformation {

   public RemoveInternalLoads2() {
      literalRegisters = new HashMap<String, Operand>();
      memTable = new MemoryTable();

      loadCounter = 0;
      storeCounter = 0;
      totalLoads = 0;
   }



   @Override
   public String getName() {
      return "Remove Internal Loads";
   }



// TODO: Use substitute table
   @Override
   public Operation transform(Operation operation) {
      if(operation.getType() == OperationType.Nop) {
         return operation;
      }

         substituteRegisterForLiterals(operation, literalRegisters);

         unregisterOutputs(operation, literalRegisters);

         if(operation.getType() == OperationType.MemoryLoad) {

            totalLoads++;
            MemoryLoad load = (MemoryLoad)operation;
            //Operand internalData = memTable.getOperand(load.getAddress(), load.getInput1(), load.getInput2(), load.getOutput());
            Operand internalData = memTable.getOperand(load.getInput1(), load.getInput2());

            if(internalData == null) {
               // Can use the output of this load in next loads
               // TODO: put in another transformation?
 //              memTable.updateTable(load.getInput1(), load.getInput2(), load.getOutput(), false);
 
               //String address = memTable.getAddress(load.getInput1(), load.getInput2());
//               System.err.println("Stored Load with address "+address+" and operand "+load.getOutput());


               return operation;
            }

            /*
            if(!literalRegisters.containsKey(Ssa.getOriginalName(internalData.getName()))) {
               System.err.println("InternalData:"+internalData.getName());
               System.err.println("Substituting:"+load.getOutput().getName());
               System.err.println("Table:"+literalRegisters);
               return operation;
            }
             * 
             */


            operationFrequency.addOperation(operation);

            loadCounter++;
            String registerName = Ssa.getOriginalName(load.getOutput().getName());
            literalRegisters.put(registerName, internalData);
//            literalRegisters.put(load.getOutput().toString(), internalData);
            return new Nop(operation);
         }


         if(operation.getType() == OperationType.MemoryStore) {
            MemoryStore store = (MemoryStore)operation;

            boolean success = memTable.updateTable(store.getOperand1(), store.getOperand2(), store.getContentsToStore(), true);
            if(success) {
               //updateStats(operation);
               storeCounter++;
            }

         }

      


      float loadRatio = 0;
      if(totalLoads != 0) {
         loadRatio = ((float)loadCounter / (float)totalLoads) * 100;
      }

      return operation;
      //System.out.println("Stored "+storeCounter+" operands.");
      //System.out.println("Can remove "+loadCounter+" loads.");
      //System.out.println("Can remove "+loadRatio+" of loads.");

   }

   private void substituteRegisterForLiterals(Operation operation,
           Map<String, Operand> literalRegisters) {

      // Check inputs, if an input matches an element from the table,
      //substitute it for the corresponding operand

      List<Operand> operands = operation.getInputs();
      for(int i=0; i<operands.size(); i++) {
         if(operands.get(i).getType() != OperandType.internalData) {
            continue;
         }

         //String registerName = operands.get(i).getName();
         //if(operands.get(i).getType() == OperandType.internalData) {
            String registerName = Ssa.getOriginalName(operands.get(i).getName());
         //}

         Operand operand = literalRegisters.get(registerName);

         //Operand operand = literalRegisters.get(operands.get(i).toString());
         if(operand != null) {
            operands.set(i, operand);

         }
      }

   }


      /**
    * Removes from the table any references to the outputs of the given operation.
    *
    * <p> An operand is removed from the table by inserting a 'null' as its value.
    * When checking if there is a mapping between an operand name and another operand,
    * instead of 'containsKey', one should get the operand associated with the key
    * and test for null.
    *
    * @param operation
    */
   private void unregisterOutputs(Operation operation, Map<String, Operand> literalRegisters) {
      List<Operand> outputs = operation.getOutputs();
      for(int i=0; i<outputs.size(); i++) {
         Operand output = outputs.get(i);
/*
         if(output.getType() != OperandType.internalData) {
            continue;
         }
*/
         String registerName = Ssa.getOriginalName(output.getName());
         if(literalRegisters.containsKey(registerName)) {
            //literalRegisters.remove(registerName);
            literalRegisters.put(registerName, null);
            //System.err.println("Removed "+registerName);
         }

      }
   }

   /**
    * INSTANCE VARIABLES
    */



      Map<String, Operand> literalRegisters = new HashMap<String, Operand>();
      MemoryTable memTable = new MemoryTable();

      int loadCounter = 0;
      int storeCounter = 0;
      int totalLoads = 0;



}

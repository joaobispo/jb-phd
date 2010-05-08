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

package org.ancora.IntermediateRepresentation.Transformations;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import org.ancora.IntermediateRepresentation.Operand;
import org.ancora.IntermediateRepresentation.Operands.Literal;
import org.ancora.IntermediateRepresentation.OperandType;
import org.ancora.IntermediateRepresentation.Operation;
import org.ancora.IntermediateRepresentation.Operations.MemoryLoad;
import org.ancora.IntermediateRepresentation.Operations.MemoryStore;
import org.ancora.IntermediateRepresentation.Operations.Nop;
import org.ancora.IntermediateRepresentation.OperationType;
import org.ancora.IntermediateRepresentation.Transformation;

/**
 *
 * @author Joao Bispo
 */
public class RemoveInternalLoads implements Transformation {

   @Override
   public String toString() {
      return "Remove Internal Loads";
   }



   public List<Operation> transform(List<Operation> operations) {
      Map<String, Operand> literalRegisters = new Hashtable<String, Operand>();
      MemoryTable memTable = new MemoryTable();

      int loadCounter = 0;
      int storeCounter = 0;
      int totalLoads = 0;
      for(int i=0; i<operations.size(); i++) {
         Operation operation = operations.get(i);
         
         substituteRegisterForLiterals(operation, literalRegisters);


         if(operation.getType() == OperationType.MemoryLoad) {
            totalLoads++;
            MemoryLoad load = (MemoryLoad)operation;
            Operand internalData = memTable.getOperand(load.getAddress(), load.getInput1(), load.getInput2(), load.getOutput());
            //System.out.println("Operand:"+load.getInput1());
            if(internalData == null) {
               continue;
            }

            loadCounter++;
            literalRegisters.put(load.getOutput().toString(), internalData);
            operations.set(i, new Nop(operation));
            System.out.println("Removed operation:"+operation.getFullOperation());
         }


         if(operation.getType() == OperationType.MemoryStore) {
            MemoryStore store = (MemoryStore)operation;
            boolean success = memTable.updateTable(store.getOperand1(), store.getOperand2(), store.getContentsToStore());
            if(success) {
               storeCounter++;
            }
            //memTable.getOperand(load.getInput1(), load.getInput2());
            //System.out.println("Operand:"+load.getInput1());
         }

      }


      float loadRatio = 0;
      if(totalLoads != 0) {
         loadRatio = ((float)loadCounter / (float)totalLoads) * 100;
      }
      System.out.println("Stored "+storeCounter+" operands.");
      System.out.println("Can remove "+loadCounter+" loads.");
      System.out.println("Can remove "+loadRatio+" of loads.");

      return operations;
   }

   private void substituteRegisterForLiterals(Operation operation,
           Map<String, Operand> literalRegisters) {

      // Check inputs, if an input matches an element from the table,
      //substitute it for the corresponding operand

      List<Operand> operands = operation.getInputs();
      for(int i=0; i<operands.size(); i++) {
         Operand operand = literalRegisters.get(operands.get(i).toString());
         if(operand != null) {
            operands.set(i, operand);
            //System.out.println("SUBSTITUTED!");
         }
      }

   }


    /**
     * INNER CLASS
     */
   class MemoryTable {

      public MemoryTable() {
         bases = new Hashtable<String, Operand>();
      }

      public String getAddress(Operand base, Operand offset) {
         // Check if first is Literal and second is Operand
         boolean LD = base.getType() == OperandType.literal &&
                 offset.getType() == OperandType.internalData;

         if(LD) {
            // Swap
            Operand temp = base;
            base = offset;
            offset = temp;
         }

         // Check if is Operand and other is Literal
         boolean DL = base.getType() == OperandType.internalData &&
                 offset.getType() == OperandType.literal;

         if(DL) {
            //String address = base.toString() + "-" + Literal.getInteger((Literal)offset);
            String address = base.toString() + "-" + Literal.getInteger(offset);
            return address;
         }


         //System.out.println("Don't know this case: base ("+base+") and offset ("+offset+")");
         return null;
      }

      public boolean updateTable(Operand base, Operand offset, Operand content) {
         String address = getAddress(base, offset);
         
         if(address == null) {
            // Store to an unknown position. Flushing tables.
            bases = new Hashtable<String, Operand>();
//            System.out.println("Store to unknown position ("+base+" + "+offset+"). " +
//                    "Flushed memory tables.");
            return false;
         }

         bases.put(address, content);
         //System.out.println("Stored '"+content+"'.");
         return true;
      }

      public Operand getOperand(int opAddress, Operand base, Operand offset, Operand output) {
         String address = getAddress(base, offset);

         if(address == null) {
            //System.out.println("Load from unknown position ("+base+" + "+offset+").");
            return null;
         }
         
         Operand value = bases.get(address);
         if(value == null) {
         //   System.out.println(opAddress+": Load from known position, but value is not in table. Output " +
         //           "put in table.");
            updateTable(base, offset, output);
         }

         return value;
     
      }

      /**
       * INSTANCE VARIABLES
       */
      private Map<String, Operand> bases;
   }
}

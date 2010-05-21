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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.ancora.IntermediateRepresentation.Operand;
import org.ancora.IntermediateRepresentation.Operands.Literal;
import org.ancora.IntermediateRepresentation.OperandType;
import org.ancora.IntermediateRepresentation.Operation;
import org.ancora.IntermediateRepresentation.Operations.MemoryLoad;
import org.ancora.IntermediateRepresentation.Operations.MemoryStore;
import org.ancora.IntermediateRepresentation.Operations.Nop;
import org.ancora.IntermediateRepresentation.OperationType;
import org.ancora.IntermediateRepresentation.Transformation;
import org.ancora.IntermediateRepresentation.Transformations.Utils.MemoryTable;

/**
 *
 * @author Joao Bispo
 */
public class RemoveInternalLoads2 extends Transformation {

   @Override
   public String toString() {
      return "Remove Internal Loads";
   }


// TODO: Use substitute table
   public void transform(List<Operation> operations) {
      Map<String, Operand> literalRegisters = new HashMap<String, Operand>();
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
            //Operand internalData = memTable.getOperand(load.getAddress(), load.getInput1(), load.getInput2(), load.getOutput());
            Operand internalData = memTable.getOperand(load.getInput1(), load.getInput2());

            if(internalData == null) {
               // Can use the output of this load in next loads
               // TODO: put in another transformation?
               memTable.updateTable(load.getInput1(), load.getInput2(), load.getOutput(), false);
               String address = memTable.getAddress(load.getInput1(), load.getInput2());
//               System.err.println("Stored Load with address "+address+" and operand "+load.getOutput());
               continue;
            }

            updateStats(operation);

            loadCounter++;
            literalRegisters.put(load.getOutput().toString(), internalData);
//            System.err.println("Will substitute "+load.getOutput().toString()+"for "+internalData);
            operations.set(i, new Nop(operation));
            //System.out.println("Removed operation:"+operation.getFullOperation());
         }


         if(operation.getType() == OperationType.MemoryStore) {
            MemoryStore store = (MemoryStore)operation;
            boolean success = memTable.updateTable(store.getOperand1(), store.getOperand2(), store.getContentsToStore(), true);
//            System.err.println("Table updated via store: "+store.getOperand1() +","+store.getOperand2() + " -> "+store.getContentsToStore());
            if(success) {
               //updateStats(operation);
               storeCounter++;
            }

         }

      }


      float loadRatio = 0;
      if(totalLoads != 0) {
         loadRatio = ((float)loadCounter / (float)totalLoads) * 100;
      }
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
         Operand operand = literalRegisters.get(operands.get(i).toString());
         if(operand != null) {
//            System.err.println("Substituted "+operands.get(i)+" for "+operand);
            operands.set(i, operand);

            //System.out.println("SUBSTITUTED!");
         }
      }

   }

}

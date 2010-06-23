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
import java.util.Map;
import org.ancora.IntermediateRepresentation.Operand;
import org.ancora.IntermediateRepresentation.Operation;
import org.ancora.IntermediateRepresentation.OperationType;
import org.ancora.IntermediateRepresentation.Operations.MemoryLoad;
import org.ancora.IntermediateRepresentation.Operations.MemoryStore;
import org.ancora.StreamTransform.DataStructures.MemoryAddress;

/**
 *
 * @author Joao Bispo
 */
public class MemTest extends StreamTransformation {

   public MemTest() {
      memAddrLoads = new HashMap<String, Integer>();
      memAddrStores = new HashMap<String, Integer>();
   }



   @Override
   public Operation transform(Operation operation) {
      Map<String, Integer> table;
      Operand op1;
      Operand op2;

      if(OperationType.MemoryLoad == operation.getType()) {
         table = memAddrLoads;
         MemoryLoad memLoad = ((MemoryLoad)operation);
         op1 = memLoad.getInput1();
         op2 = memLoad.getInput2();
      } else if(OperationType.MemoryStore == operation.getType()) {
         table = memAddrStores;
         MemoryStore memStore = ((MemoryStore)operation);
         op1 = memStore.getOperand1();
         op2 = memStore.getOperand2();
      } else {
         return operation;
      }


         MemoryAddress memAddr = new MemoryAddress(op1, op2);
         String key = memAddr.getAddrType();
         Integer value = table.get(key);
         if(value == null) {
            value = 0;
         }
         value++;
         table.put(key, value);

         return operation;
   }

   @Override
   public String getName() {
      return "MemTest";
   }

   public Map<String, Integer> getMemAddrLoads() {
      return memAddrLoads;
   }

   public Map<String, Integer> getMemAddrStores() {
      return memAddrStores;
   }

   

   private Map<String, Integer> memAddrLoads;
   private Map<String, Integer> memAddrStores;

}

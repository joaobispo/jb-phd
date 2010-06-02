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

package org.ancora.StreamTransform.Stats;

import java.util.EnumMap;
import java.util.Map;
import org.ancora.IntermediateRepresentation.Operation;
import org.ancora.IntermediateRepresentation.OperationType;

/**
 *
 * @author Joao Bispo
 */
public class OperationFrequency {

   public OperationFrequency() {
      stats = new EnumMap<OperationType, Integer>(OperationType.class);
   }

   /**
    * Use this method to keep track of the operations changed by this
    * transformation.
    *
    * @param operation an IR operation
    */
   public void addOperation(Operation operation) {
      // Add operation to table
      Integer value = stats.get((OperationType) operation.getType());
      if (value == null) {
         value = 0;
      }
      value++;
      stats.put((OperationType) operation.getType(), value);
   }

   public void addOperations(OperationFrequency operationFrequency) {
      Map<OperationType, Integer> alienStats = operationFrequency.stats;
      for(OperationType key : alienStats.keySet()) {
         Integer plusValue = alienStats.get(key);
         Integer currentValue = stats.get(key);

         if(currentValue == null) {
            currentValue = 0;
         }

         currentValue += plusValue;

         stats.put(key, plusValue);
      }
   }

   public Map<OperationType, Integer> getFrequencyTable() {
      return stats;
   }



   /**
    * INSTANCE VARIABLES
    */
   private Map<OperationType, Integer> stats;
}

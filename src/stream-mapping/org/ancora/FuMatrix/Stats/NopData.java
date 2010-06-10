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

package org.ancora.FuMatrix.Stats;

import java.util.List;
import org.ancora.IntermediateRepresentation.Operation;
import org.ancora.IntermediateRepresentation.OperationType;

/**
 * Contains data about NOPs in operations.
 *
 * @author Joao Bispo
 */
public class NopData {

   public NopData() {
   }

   public void addMapping(List<Operation> operations, int repetitions) {
      long localNops = 0l;
      for (Operation operation : operations) {
         if (operation.getType() == OperationType.Nop) {
            localNops++;
         }
      }
      mappedNops += localNops;
      executedNops += localNops * repetitions;
      mappings++;
      totalIterations += repetitions;
   }

   public long getExecutedNops() {
      return executedNops;
   }

   public long getMappedNops() {
      return mappedNops;
   }

   public long getMappings() {
      return mappings;
   }

   public long getTotalIterations() {
      return totalIterations;
   }

   

   /**
    * INSTANCE VARIABLES
    */
   private long executedNops;
   private long mappedNops;
   private long mappings;
   private long totalIterations;
}

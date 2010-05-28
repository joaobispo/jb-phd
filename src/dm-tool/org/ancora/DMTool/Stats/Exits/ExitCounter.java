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

package org.ancora.DMTool.Stats.Exits;

import java.util.List;
import org.ancora.IntermediateRepresentation.Operation;
import org.ancora.IntermediateRepresentation.OperationType;

/**
 * Counts the number of side-exits inside a list of operations.
 *
 * @author Joao Bispo
 */
public class ExitCounter {

   public ExitCounter() {
      numberExits = 0l;
      maxExits = 0;
      numberBlocks = 0l;
   }

   public void processOperations(List<Operation> operations) {
      int numberOfBlockExits = 0;
      for(Operation operation : operations) {
         if(operation.getType() == OperationType.ConditionalExit) {
            numberOfBlockExits++;
         }
      }
      numberBlocks++;
      maxExits = Math.max(maxExits, numberOfBlockExits);
      numberExits+= numberOfBlockExits;
   }

   public long getNumberExits() {
      return numberExits;
   }

   public int getMaxExits() {
      return maxExits;
   }

   public long getNumberBlocks() {
      return numberBlocks;
   }

   
   public double getExitAverage() {
      return (double)numberExits / (double)numberBlocks;
   }

   private long numberExits;
   private int maxExits;
   private long numberBlocks;
}

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

package org.ancora.DMTool.Stats.BlockSize;


import java.util.List;
import org.ancora.InstructionBlock.InstructionBlock;
import org.ancora.IntermediateRepresentation.Operation;
import org.ancora.IntermediateRepresentation.OperationType;

/**
 * Counts the number of side-exits inside a list of operations.
 *
 * @author Joao Bispo
 */
public class BlockSizeCounter {

   public BlockSizeCounter() {
      maxBlockSize = 0;
      minBlockSize = Integer.MAX_VALUE;
      blockSizeAcc = 0l;
      numberOfBlocks = 0l;
   }

   public void processBlock(List<Operation> operations) {
      int numberOfInstructions = 0;
      for(Operation operation : operations) {
         if(operation.getType() == OperationType.Nop) {
            continue;
         }

         numberOfInstructions++;
      }

      if(numberOfInstructions < 5) {
         return;
      }

      maxBlockSize = Math.max(maxBlockSize, numberOfInstructions);
      minBlockSize = Math.min(minBlockSize, numberOfInstructions);
      blockSizeAcc += numberOfInstructions;
      numberOfBlocks++;
   }

   @Override
   public String toString() {
      StringBuilder builder = new StringBuilder();

      builder.append("Before Average Block Size:");
      builder.append(getAverageSize());
      builder.append("\n");

      builder.append("Before Max Block Size:");
      builder.append(getMaxBlockSize());
      builder.append("\n");

      builder.append("Before Min Block Size:");
      builder.append(getMinBlockSize());
      builder.append("\n");

      return builder.toString();
   }

   public double getAverageSize() {
      return (double)blockSizeAcc / (double)numberOfBlocks;
   }

   public int getMaxBlockSize() {
      return maxBlockSize;
   }

   public int getMinBlockSize() {
      return minBlockSize;
   }

   public long getNumberOfBlocks() {
      return numberOfBlocks;
   }

   

   private int maxBlockSize;
   private int minBlockSize;
   private long blockSizeAcc;
   private long numberOfBlocks;

}

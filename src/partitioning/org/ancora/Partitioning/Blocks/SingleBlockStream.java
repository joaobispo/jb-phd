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

package org.ancora.Partitioning.Blocks;

import org.ancora.InstructionBlock.InstructionBlock;
import org.ancora.InstructionBlock.InstructionBusReader;
import org.ancora.Partitioning.Partitioner;

/**
 * Block Stream with a single InstructionBlock.
 *
 * TODO: Check situations where only one instruction block is used and do not
 * implement BlockStream; treat the single block diferently.
 *
 * @author Joao Bispo
 */
public class SingleBlockStream implements BlockStream {

   public SingleBlockStream(InstructionBlock block) {
      this.block = block;
      blockUsed = false;
   }

   public String getPartitionerName() {
      return null;
   }

   public InstructionBlock nextBlock() {
      if(!blockUsed) {
         blockUsed = true;
         return block;
      }

      return null;
   }

   private InstructionBlock block;
   private boolean blockUsed;

   public long getTotalInstructions() {
      return block.getTotalInstructions();
   }

   public Partitioner getPartitioner() {
      return null;
   }

   public InstructionBusReader getInstructionBusReader() {
      return null;
   }

}

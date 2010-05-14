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

package org.ancora.Partitioning.Tools;

import java.util.HashSet;
import java.util.Set;
import org.ancora.InstructionBlock.InstructionBlock;
import org.ancora.InstructionBlock.InstructionBlockListener;
import org.ancora.InstructionBlock.InstructionBlockProducer;
import org.ancora.InstructionBlock.InstructionBlockProducerSkeleton;

/**
 * Forward Instruction Blocks only if they have not appeard before.
 *
 * @author Joao Bispo
 */
public class UniqueBlocks implements InstructionBlockListener, InstructionBlockProducer {

   public UniqueBlocks() {
      this.blockIds = new HashSet<Integer>();
      this.producer = new UniqueBlocksProducer();
   }

   public void addListener(InstructionBlockListener listener) {
      producer.addListener(listener);
   }

   public void accept(InstructionBlock instructionBlock) {
      // Forward instruction block only if they have not appeard before.
      int blockId = instructionBlock.getId();
      if(!blockIds.contains(blockId)) {
         blockIds.add(blockId);
         producer.sendBlock(instructionBlock);
      }
   }

   public void flush() {
      producer.endProcessing();
   }


   /**
    * INSTANCE VARIABLES
    */
   private Set<Integer> blockIds;
   private UniqueBlocksProducer producer;


   class UniqueBlocksProducer extends InstructionBlockProducerSkeleton {
      public void sendBlock(InstructionBlock block) {
         noticeListeners(block);
      }

      public void endProcessing() {
         flushListeners();
      }
   }

}

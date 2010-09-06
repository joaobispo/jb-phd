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

package org.ancora.Partitioning.Services;

import org.ancora.InstructionBlock.InstructionBlock;
import org.ancora.InstructionBlock.InstructionBlockListener;
import org.ancora.InstructionBlock.InstructionBlockProducer;
import org.ancora.InstructionBlock.InstructionBlockProducerSkeleton;

/**
 * Forward Instruction Blocks if the number of repetitions is equal or above
 * a certain threshold. Drops blocks which have a lower number of repetitions.
 *
 * @author Joao Bispo
 */
public class Selector implements InstructionBlockListener, InstructionBlockProducer {

   public Selector(int repetitionThreshold) {
      this.repetitionThreshold = repetitionThreshold;
      this.producer = new SelectorProducer();
   }

   public void addListener(InstructionBlockListener listener) {
      producer.addListener(listener);
   }

   public void accept(InstructionBlock instructionBlock) {
      // Forward instruction block if the number of repetitions is equal or above
      // a certain threshold
      if(instructionBlock.getRepetitions() >= repetitionThreshold) {
         producer.sendBlock(instructionBlock);
      }
   }

   public void flush() {
      producer.endProcessing();
   }


   /**
    * INSTANCE VARIABLES
    */
   private int repetitionThreshold;
   private SelectorProducer producer;


   class SelectorProducer extends InstructionBlockProducerSkeleton {
      public void sendBlock(InstructionBlock block) {
         noticeListeners(block);
      }

      public void endProcessing() {
         flushListeners();
      }
   }

}

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

import org.ancora.InstructionBlock.InstructionBlock;
import org.ancora.InstructionBlock.InstructionBlockListener;
import org.ancora.InstructionBlock.InstructionBlockProducer;
import org.ancora.InstructionBlock.InstructionBlockProducerSkeleton;

/**
 *
 * @author Joao Bispo
 */
public class Gatherer implements InstructionBlockListener, InstructionBlockProducer {

   public Gatherer() {
      producer = new GathererProducer();
      //listener = new GathererListener();
      reset();

   }

   /*
   public InstructionBlockListener getListener() {
      return listener;
   }
    */

   public void addListener(InstructionBlockListener listener) {
      producer.addListener(listener);
   }

   public void accept(InstructionBlock instructionBlock) {
      if(currentInstructionBlock == null) {
         startNewBlock(instructionBlock);
         return;
      }

      // Check if the blocks are the same
      if(instructionBlock.getId() == currentInstructionBlock.getId()) {
         repetitions++;
         totalInstructions += instructionBlock.getTotalInstructions();
      } else {
         // If not, complete an InstructionBlock and make this current
         completeBlock();
         startNewBlock(instructionBlock);
      }
   }

   private void completeBlock() {
      // Build new InstructionBlock
      InstructionBlock newBlock = new InstructionBlock(currentInstructionBlock.getInstructions(),
              repetitions, currentInstructionBlock.getId(), totalInstructions);

      // Notify producer
      producer.sendBlock(newBlock);
      reset();
   }

   public void flush() {
      if(currentInstructionBlock != null) {
         completeBlock();
      }

      producer.endProcessing();
   }

   private void startNewBlock(InstructionBlock instructionBlock) {
      currentInstructionBlock = instructionBlock;
      repetitions = 1;
      totalInstructions = instructionBlock.getTotalInstructions();
   }

   private void reset() {
      currentInstructionBlock = null;
      repetitions = -1;
      totalInstructions = 0l;
   }

   /**
    * INSTANCE VARIABLES
    */
   private InstructionBlock currentInstructionBlock;
   private int repetitions;
   private long totalInstructions;

   private GathererProducer producer;




   //private GathererListener listener;

/*
   class GathererListener implements InstructionBlockListener {

      public void accept(InstructionBlock instructionBlock) {
         throw new UnsupportedOperationException("Not supported yet.");
      }

      public void flush() {
         throw new UnsupportedOperationException("Not supported yet.");
      }


   }
*/



   class GathererProducer extends InstructionBlockProducerSkeleton {
      public void sendBlock(InstructionBlock block) {
         noticeListeners(block);
      }

      public void endProcessing() {
         flushListeners();
      }
   }




}

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

package org.ancora.InstructionBlock;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Joao Bispo
 */
public abstract class InstructionBlockProducerSkeleton implements InstructionBlockProducer {

   public InstructionBlockProducerSkeleton() {
      consumers = new HashSet<InstructionBlockListener>();
   }


   /**
    * Adds an InstructionBlockListener.
    *
    * @param listener
    */
   public void addListener(InstructionBlockListener listener) {
      consumers.add(listener);
   }

   protected void noticeListeners(InstructionBlock instructionBlock) {
      for(InstructionBlockListener consumer : consumers) {
         consumer.accept(instructionBlock);
      }
   }

   protected void flushListeners() {
      for(InstructionBlockListener consumer : consumers) {
         consumer.flush();
      }
   }


   /**
    * INSTANCE VARIABLES
    */
   private Set<InstructionBlockListener> consumers;
}

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

package org.ancora.Partitioning.deprecated;

import org.ancora.InstructionBlock.GenericInstruction;
import org.ancora.InstructionBlock.InstructionBlock;
import org.ancora.InstructionBlock.InstructionBlockListener;
import org.ancora.Partitioning.Partitioner;
import org.ancora.Partitioning.Tools.Gatherer;

/**
 *
 * @author Joao Bispo
 */
public class IterativePartitioner {

   public IterativePartitioner(Partitioner partitioner, boolean useGatherer) {
      this.partitioner = partitioner;
      this.listener = new IterativeBlockListener();

      InstructionBlockListener partitionListener = null;

      if(useGatherer) {
         gatherer = new Gatherer();
         gatherer.addListener(listener);

         partitionListener = gatherer;
      } else {
         gatherer = null;
         partitionListener = listener;
      }

      partitioner.addListener(partitionListener);
   }

   public InstructionBlock acceptInstruction(GenericInstruction instruction) {
      partitioner.acceptInstruction(instruction);
      return listener.retrive();
   }

   public InstructionBlock lastInstruction() {
      partitioner.flush();
      return listener.retrive();
   }


   private Partitioner partitioner;
   private IterativeBlockListener listener;
   private Gatherer gatherer;

   class IterativeBlockListener implements InstructionBlockListener {

      public IterativeBlockListener() {
         block = null;
      }


      public void accept(InstructionBlock instructionBlock) {
         block = instructionBlock;
      }

      public void flush() {
         // Do Nothing
      }

      public InstructionBlock retrive() {
         InstructionBlock newBlock = null;
         if(block != null) {
            newBlock = block;
            block = null;
         }

         return newBlock;
      }

      private InstructionBlock block;

   }
}

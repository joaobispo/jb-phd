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

import org.ancora.Partitioning.deprecated.IterativePartitioner;
import org.ancora.InstructionBlock.GenericInstruction;
import org.ancora.InstructionBlock.InstructionBlock;
import org.ancora.InstructionBlock.InstructionBusReader;
import org.ancora.Partitioning.Partitioner;

/**
 *
 * @author Joao Bispo
 */
public class PartitionerBlockReader implements BlockReader {

   public PartitionerBlockReader(InstructionBusReader busReader, Partitioner partitioner, boolean useGatherer) {
      this.busReader = busReader;
      this.iterativeInterface = new IterativePartitioner(partitioner, useGatherer);
   }


   public InstructionBlock nextBlock() {
      // Read instructions until an instruction block is done
      while(true) {
         GenericInstruction instruction = busReader.nextInstruction();

         // Instruction is null, we have reached the end of the stream
         if(instruction == null) {
            return iterativeInterface.lastInstruction();
         }

         // Else, keep feeding partitioner until an instruction block gets out
         InstructionBlock block = iterativeInterface.acceptInstruction(instruction);
         if(block != null) {
            return block;
         }
      }
   }



   /**
    * INSTANCE VARIABLES
    */
   private InstructionBusReader busReader;
   private IterativePartitioner iterativeInterface;
}

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

package org.ancora.Partitioning;

import java.util.HashSet;
import java.util.Set;
import org.ancora.InstructionBlock.GenericInstruction;
import org.ancora.InstructionBlock.InstructionBlock;
import org.ancora.InstructionBlock.InstructionBlockListener;
import org.ancora.InstructionBlock.InstructionBlockProducerSkeleton;
import org.ancora.InstructionBlock.InstructionBusReader;

/**
 * Generates InstructionBlocks from Generic Instructions
 *
 * @author Joao Bispo
 */
public abstract class Partitioner extends InstructionBlockProducerSkeleton {


   /**
    * @return the name of this partitioner
    */
   public abstract String getName();

   protected abstract void acceptInstruction(GenericInstruction instruction);

   protected abstract void flush();


   public void run(InstructionBusReader reader) {
      GenericInstruction instruction = reader.nextInstruction();
      while(instruction != null) {
         acceptInstruction(instruction);
         instruction = reader.nextInstruction();
      }
      flush();
   }

}

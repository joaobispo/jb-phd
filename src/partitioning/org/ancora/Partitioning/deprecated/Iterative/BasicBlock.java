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

package org.ancora.Partitioning.deprecated.Iterative;

import java.util.ArrayList;
import java.util.List;
import org.ancora.InstructionBlock.GenericInstruction;
import org.ancora.InstructionBlock.InstructionBlock;

/**
 * PartitionerProducer of BasicBlocks.
 *
 * @author Joao Bispo
 */
public abstract class BasicBlock extends Partitioner {


   public BasicBlock() {
      currentInstructions = new ArrayList<GenericInstruction>();
   }

   
   /**
    * CAUTION: In architectures with delay slots, the instruction where the
    * control flow jumps might not be the branch instruction itself.
    * 
    * @param instruction
    * @return true if the this instruction represents a jump in the control flow.
    */
   protected abstract boolean isJumpInstruction(GenericInstruction instruction);

   @Override
   public String getName() {
      return NAME;
   }

   @Override
   public InstructionBlock acceptInstruction(GenericInstruction instruction) {
       // Add instruction to current block of instructions
      currentInstructions.add(instruction);

      // Check if instruction is a branch
      //if(jumpFilter.accept(instruction)) {
      if(isJumpInstruction(instruction)) {
         return completeBasicBlock();
      }

      return null;
   }

   @Override
   public InstructionBlock lastInstruction() {
      return completeBasicBlock();
   }



   private InstructionBlock completeBasicBlock() {
      if(currentInstructions.isEmpty()) {
         return null;
      }

      // Basic Block can be identified by the address of its first instruction
      int id = currentInstructions.get(0).getAddress();
      int repetitions = 1;
      
      // Build Instruction Block
      InstructionBlock iBlock = new InstructionBlock(currentInstructions, repetitions, id, currentInstructions.size());

      // Clean current instructions
      currentInstructions = new ArrayList<GenericInstruction>();

      return iBlock;
   }


   /**
    * INSTANCE VARIABLES
    */
   private List<GenericInstruction> currentInstructions;
   //private InstructionFilter jumpFilter;

   public static final String NAME = "BasicBlock";
}

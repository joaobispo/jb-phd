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

import org.ancora.Partitioning.Tools.InstructionFilter;
import java.util.ArrayList;
import java.util.List;
import org.ancora.InstructionBlock.GenericInstruction;
import org.ancora.InstructionBlock.InstructionBlock;

/**
 * Partitioner of BasicBlocks.
 *
 * @author Joao Bispo
 */
public class BasicBlock extends Partitioner {

   /**
    * Needs an InstructionFilter which indicates if an instruction represents
    * a jump or a branch.
    *
    * @param jumpFilter
    */
   public BasicBlock(InstructionFilter jumpFilter) {
      currentInstructions = new ArrayList<GenericInstruction>();
      this.jumpFilter = jumpFilter;
   }


   @Override
   public String getName() {
      return NAME;
   }

   @Override
   protected void acceptInstruction(GenericInstruction instruction) {
       // Add instruction to current block of instructions
      currentInstructions.add(instruction);

      // Check if instruction is a branch
      if(jumpFilter.accept(instruction)) {
         completeBasicBlock();
      }

   }

   @Override
   protected void flush() {
      completeBasicBlock();
      flushListeners();
   }

   private void completeBasicBlock() {
      if(currentInstructions.size() == 0) {
         return;
      }

      // Basic Block can be identified by the address of its first instruction
      int id = currentInstructions.get(0).getAddress();
      int repetitions = 1;
      
      // Build Instruction Block
      InstructionBlock iBlock = new InstructionBlock(currentInstructions, repetitions, id);

      noticeListeners(iBlock);

      // Clean current instructions
      currentInstructions = new ArrayList<GenericInstruction>();
   }


   /**
    * INSTANCE VARIABLES
    */
   private List<GenericInstruction> currentInstructions;
   private InstructionFilter jumpFilter;

   public static final String NAME = "BasicBlock";
}

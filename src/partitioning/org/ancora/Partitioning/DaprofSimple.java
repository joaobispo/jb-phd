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

import org.ancora.Partitioning.Partitioner;
import org.ancora.Partitioning.Tools.InstructionFilter;
import java.util.ArrayList;
import java.util.List;
import org.ancora.InstructionBlock.GenericInstruction;
import org.ancora.InstructionBlock.InstructionBlock;
import org.ancora.SharedLibrary.BitUtils;

/**
 * Partitioner of BasicBlocks.
 *
 * @author Joao Bispo
 */
public class DaprofSimple extends Partitioner {


   public DaprofSimple() {
      currentInstructions = new ArrayList<GenericInstruction>();
      backwardBranchMaxOffset = DEFAULT_MAX_OFFSET;
      useDaprofId = false;
      //useDaprofId = true;
      lastAddress = -1;
      useBranchLimit = true;
   }

   public void setBackwardBranchMaxOffset(int backwardBranchMaxOffset) {
      this.backwardBranchMaxOffset = backwardBranchMaxOffset;
   }

   public int getBackwardBranchMaxOffset() {
      return backwardBranchMaxOffset;
   }

   public void setUseDaprofId(boolean useDaprofId) {
      this.useDaprofId = useDaprofId;
   }

   
   /**
    * CAUTION: In architectures with delay slots, the instruction where the
    * control flow jumps might not be the branch instruction itself.
    * 
    * @param instruction
    * @return true if the this instruction represents a jump in the control flow.
    */
   //protected abstract boolean isShortBackwardBranch(GenericInstruction instruction, int nextInstAddress);
   private boolean isShortBackwardBranch(int currentAddress, int lastInstAddress) {
      int offset = currentAddress - lastInstAddress;
      //System.err.println("Current:"+currentAddress);
      //System.err.println("last:"+lastInstAddress);
      //System.err.println("Offset:"+offset);
      if(offset >= 0) {
      //if(offset <= 0) {
         return false;
      }

      if(useBranchLimit && (Math.abs(offset) > backwardBranchMaxOffset)) {
         return false;
      }

      return true;
   }


   @Override
   public String getName() {
      return NAME;
   }

   @Override
   public void acceptInstruction(GenericInstruction instruction) {
      // Initial case
      if(lastAddress == -1) {
         currentInstructions.add(instruction);
         lastAddress = instruction.getAddress();
         return;
      }



      // Check if instruction is a branch
      //if(jumpFilter.accept(instruction)) {
      if(isShortBackwardBranch(instruction.getAddress(), lastAddress)) {
         completeBasicBlock();
      }

     // Add instruction to current block of instructions
      currentInstructions.add(instruction);
      lastAddress = instruction.getAddress();
   }

   @Override
   public void flush() {
      completeBasicBlock();
      flushListeners();
   }

   private void completeBasicBlock() {
      if(currentInstructions.isEmpty()) {
         return;
      }

      // According to the paper, the Daprof block can be identified by
      // the address of the first instruction and by the number of instructions
      // the block has, but I think there can be ambiguities.
      int originalId = BitUtils.superFastHash(currentInstructions.get(0).getAddress(), 32);
      originalId = BitUtils.superFastHash(currentInstructions.size(), originalId);


      // As an alternative, I'm calculating an hash with the values of
      // the addresses of all instructions in the block.
      int completeId = 32;
      for(GenericInstruction instruction : currentInstructions) {
         completeId = BitUtils.superFastHash(instruction.getAddress(), completeId);
      }

      int repetitions = 1;
      int id = 0;
      if(useDaprofId) {
         id = originalId;
      } else {
         id = completeId;
      }

      // Build Instruction Block
      InstructionBlock iBlock = new InstructionBlock(currentInstructions, repetitions, id);

      noticeListeners(iBlock);

      // Clean current instructions
      currentInstructions = new ArrayList<GenericInstruction>();
   }

   public void setUseBranchLimit(boolean useBranchLimit) {
      this.useBranchLimit = useBranchLimit;
   }



   /**
    * INSTANCE VARIABLES
    */
   private List<GenericInstruction> currentInstructions;
   private int backwardBranchMaxOffset;
   private boolean useDaprofId;
   private int lastAddress;

   private boolean useBranchLimit;

   public static final String NAME = "DaprofSimple";
   public static final int DEFAULT_MAX_OFFSET = 1024;
}

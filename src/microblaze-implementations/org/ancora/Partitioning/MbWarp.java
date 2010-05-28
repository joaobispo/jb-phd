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

import org.ancora.Partitioning.deprecated.DaprofSuperBlock;
import java.util.logging.Logger;
import org.ancora.InstructionBlock.GenericInstruction;
import org.ancora.InstructionBlock.MbBlockUtils;
import org.ancora.MicroBlaze.InstructionName;
import org.ancora.MicroBlaze.InstructionProperties;
import org.ancora.SharedLibrary.DataStructures.PushingQueue;

/**
 *
 * @author Joao Bispo
 */
public class MbWarp extends Warp {



   public MbWarp() {
      super();
      isDelaySlot = false;
      wasJumpInstruction = false;
   }

   @Override
   protected boolean lastInstructionWasJump(GenericInstruction instruction) {
      boolean result = wasJumpInstruction;

      wasJumpInstruction = isJump(instruction);
      lastInstruction = instruction;
      return result;
   }


   
   protected boolean isJump(GenericInstruction instruction) {
      // Check if we are on a delay slot
      if(isDelaySlot) {
         isDelaySlot = false;
         return true;
      }

      // Cast Generic to MicroBlaze
      InstructionName instName = MbBlockUtils.getInstructionName(instruction);
      if(instName == null) {
         Logger.getLogger(MbBasicBlock.class.getName()).
                 warning("Could not get MicroBlaze Instruction Name from instruction '"+
                 instruction+"'");
         return false;
      }

      // Check if it is a jump instruction
      if(InstructionProperties.JUMP_INSTRUCTIONS.contains(instName)) {
         // Check if it has delay slot
         if(InstructionProperties.INSTRUCTIONS_WITH_DELAY_SLOT.contains(instName)) {
            isDelaySlot = true;
            return false;
         } else {
            return true;
         }

      }

      return false;
   }


   /**
    * INSTANCE VARIABLES
    */
   private boolean isDelaySlot;
   private boolean wasJumpInstruction;
   private GenericInstruction lastInstruction;

   @Override
   protected void resetJumpInstruction() {
      isDelaySlot = false;
   }
}

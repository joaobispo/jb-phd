/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.ancora.Partitioning;

import org.ancora.Partitioning.Tools.InstructionFilter;
import org.ancora.InstructionBlock.GenericInstruction;
import org.ancora.InstructionBlock.MbInstruction;
import org.ancora.MicroBlaze.InstructionName;
import org.ancora.MicroBlaze.InstructionProperties;

/**
 * Indicates if an instruction represents a jump. Takes into account MicroBlaze
 * delay slots.
 *
 * @author Joao Bispo
 */
public class MbJumpFilter implements InstructionFilter {

   public MbJumpFilter() {
      isDelaySlot = false;
   }



   public boolean accept(GenericInstruction instruction) {
      // Check if we are on a delay slot
      if(isDelaySlot) {
         isDelaySlot = false;
         return true;
      }

      // Cast Generic to MicroBlaze
      MbInstruction mbInst = (MbInstruction) instruction;
      InstructionName instName = mbInst.getInstructionName();

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
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.ancora.InstructionBlock;

import org.ancora.InstructionBlock.GenericInstruction;
import org.ancora.MicroBlaze.InstructionName;

/**
 *
 * @author Joao Bispo
 */
public class MbInstruction implements GenericInstruction {

   public MbInstruction(int address, String instruction, InstructionName instructionName) {
      this.address = address;
      this.instruction = instruction;
      this.instructionName = instructionName;
   }



   public int getAddress() {
      return address;
   }

   public String getInstruction() {
      return instruction;
   }

   @Override
   public String toString() {
      return address + SEPARATOR + instruction;
   }

   public InstructionName getInstructionName() {
      return instructionName;
   }



   /**
    * INSTANCE VARIABLES
    */
   private int address;
   private String instruction;
   private InstructionName instructionName;

   public static final String SEPARATOR = " ";
}

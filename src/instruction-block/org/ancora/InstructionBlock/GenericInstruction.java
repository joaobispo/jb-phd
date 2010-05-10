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


/**
 * Represents a generic instruction
 *
 * @author Joao Bispo
 */
public class GenericInstruction {

   public GenericInstruction(int address, String instruction) {
      this.instruction = instruction;
      this.address = address;
   }



   /**
    * @return the address of this instruction
    */
   public int getAddress() {
      return address;
   }

   /**
    * @return a string representation of this instruction
    */
   public String getInstruction() {
      return instruction;
   }

   public String toLine() {
      return address + SEPARATOR + instruction;
   }

   public static GenericInstruction fromLine(String line) {
      /// Split the trace instruction in parts
      int whiteSpaceIndex = line.indexOf(SEPARATOR);

      /// Get Address
      String addressString = line.substring(0, whiteSpaceIndex);

      // Parse to integer
      int instructionAddress = Integer.valueOf(addressString);

      /// Get Instruction
      String instruction = line.substring(whiteSpaceIndex).trim();

      return new GenericInstruction(instructionAddress, instruction);
   }

   @Override
   public String toString() {
      return toLine();
   }



   /**
    * INSTANCE VARIABLES
    */
   private String instruction;
   private int address;

   /**
    * DEFINITIONS
    */
   public static final String SEPARATOR = " ";
}

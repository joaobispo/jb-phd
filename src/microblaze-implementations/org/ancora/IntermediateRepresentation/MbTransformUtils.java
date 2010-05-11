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

package org.ancora.IntermediateRepresentation;

import java.util.List;
import org.ancora.IntermediateRepresentation.Operands.MbRegister;
import org.ancora.MicroBlaze.InstructionName;
import org.ancora.MicroBlaze.InstructionProperties;
import org.ancora.MicroBlaze.Definitions;

/**
 *
 * @author Joao Bispo
 */
public class MbTransformUtils {

   public static Operand createCarryOperand() {
      return new MbRegister(Definitions.CARRY_REGISTER, null, Definitions.BITS_CARRY);
   }

   /**
    * Calculates the next address.
    *
    * @param operations
    * @param i
    * @return
    */
   public static int calculateNextAddress(List<Operation> operations, int i,
           InstructionName instructionName) {

      // Usually, the index of the next instruction would be i+1
      int nextInstructionIndex = i + 1;

      // Check if instruction has delay slot
      if (InstructionProperties.INSTRUCTIONS_WITH_DELAY_SLOT.contains(instructionName)) {
         nextInstructionIndex++;
      }

      if (nextInstructionIndex >= operations.size()) {
         nextInstructionIndex = 0;
      }

      return operations.get(nextInstructionIndex).getAddress();
   }

   /**
    * 
    * @param instructionName
    * @return the number of delay slots of an MicroBlaze instruction.
    */
   public static int getDelaySlots(InstructionName instructionName) {
      if (InstructionProperties.INSTRUCTIONS_WITH_DELAY_SLOT.contains(instructionName)) {
         return 1;
      } else {
         return 0;
      }
   }


}

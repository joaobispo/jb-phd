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
import java.util.logging.Logger;
import org.ancora.IntermediateRepresentation.Operands.Literal;
import org.ancora.IntermediateRepresentation.Operands.MbImm;
import org.ancora.IntermediateRepresentation.Operands.MbRegister;
import org.ancora.MicroBlaze.InstructionName;
import org.ancora.MicroBlaze.InstructionProperties;
import org.ancora.MicroBlaze.Definitions;

/**
 *
 * @author Joao Bispo
 */
public class MbTransformUtils {

   /**
    * If an operand can be transformed into literal, a new operand Literal operand is
    * returned. If not, returns null.
    *
    * @param operand
    * @return
    */
//   public static Literal transformOperandToLiteral(Operand operand) {
      // Check if Literal from Intermediate Representation
/*
      Literal lit = TransformUtils.transformOperandToLiteral(operand);
      if(lit != null) {
         return lit;
      }
*/
      // Check if Literal
  /*
      if(operand.getType() == OperandType.literal) {
         return (Literal) operand;
      }
   */
/*
      // Check if it is a MicroBlaze immediate value
      Integer immValue = MbImm.getImmValue(operand);

//      if(operand.getType() == MbOperandType.immediate) {
//               return new Literal(Literal.LiteralType.integer, operand.getValue(),
//                 Definitions.BITS_IMMEDIATE);
//      }
      if(immValue != null) {
         return new Literal(Literal.LiteralType.integer, String.valueOf(immValue),
                 Definitions.BITS_IMMEDIATE);
      }

      Integer regValue = MbRegister.getRegValue(operand);
      //if(operand.getType() == MbOperandType.register) {
      if(regValue != null) {
         // Check if is register zero
//         if(isRegisterZero(operand.getValue())) {
         if(regValue == 0) {
            return new Literal(Literal.LiteralType.integer, "0", BITS_ZERO);
         }
         else {
            return null;
         }
      }

      Logger.getLogger(MbTransformUtils.class.getName()).
              warning("Could not find case to transform operand '"+operand+"'.");
      return null;
   }
*/
   public static Operand createCarryOperand() {
      //return new MbOperand(MbOperandType.register, Definitions.CARRY_REGISTER, Definitions.BITS_CARRY);
      return new MbRegister(Definitions.CARRY_REGISTER, null, Definitions.BITS_CARRY);
   }
/*
   private static boolean isRegisterZero(String value) {
      return value.equals(INTEGER_VALUE_ZERO);
   }
*/
   /**
    * Supports Literal, MbImm
    * @param operand
    * @return
    */
/*
   public static Integer getIntegerValue(Operand operand) {
      // First, transform operand to literal
      Literal literal = transformOperandToLiteral(operand);
      if(literal == null) {
         return null;
      }

      return Literal.getInteger((Operand)literal);
*/
      /*
      Integer integer = null;

      // Check if operand is intermediate representation literal
      integer = TransformUtils.getIntegerValue(operand);
      //integer = Literal.getInteger(operand);
      if(integer != null) {
         return integer;
      }

      integer = MbImm.getImmValue(operand);
      if(integer != null) {
         return integer;
      }

      Logger.getLogger(MbTransformUtils.class.getName()).
              warning("Method not defined for operand '"+operand+"'");
      return null;
       */
//   }

   /**
    * Calculates the next address.
    * TODO: Public Static Candidate
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

   public static int getDelaySlots(InstructionName instructionName) {
      if (InstructionProperties.INSTRUCTIONS_WITH_DELAY_SLOT.contains(instructionName)) {
         return 1;
      } else {
         return 0;
      }
   }

   //private static final String INTEGER_VALUE_ZERO = "0";
   //private static final int BITS_ZERO = 0;
}

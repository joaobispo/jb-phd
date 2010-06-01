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

package org.ancora.IntermediateRepresentation.Operands;

import org.ancora.IntermediateRepresentation.MbOperandType;
import org.ancora.IntermediateRepresentation.Operand;
import org.ancora.MicroBlaze.Definitions;

/**
 *
 * @author Joao Bispo
 */
public class MbImm extends Operand {


   public MbImm(int value) {
      this(value, Definitions.BITS_IMMEDIATE);
   }

   public MbImm(int value, int bits) {
      this.bits = bits;
      this.value = value;
   }

   @Override
   public Enum getType() {
      return MbOperandType.MbImm;
   }

   @Override
   public int getBits() {
      return bits;
   }

   @Override
   public boolean isImmutable() {
      return true;
   }

   @Override
   public Operand copy() {
      return new MbImm(value, bits);
   }

   @Override
   public String toString() {
      return getName();
   }

   @Override
   public String getName() {
      return "imm."+value;
   }



   public static Integer getImmValue(Operand operand) {
      // Check if MbImm
      if(operand.getType() != MbOperandType.MbImm) {
         return null;
      }

      return ((MbImm)operand).value;
   }

   /**
    * INSTANCE VARIABLES
    */
   private int value;
   private int bits;
   
}

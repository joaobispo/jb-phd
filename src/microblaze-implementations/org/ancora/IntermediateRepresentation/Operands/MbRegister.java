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
public class MbRegister extends Operand {

   public MbRegister(String name, Integer value, int bits) {
      this.name = name;
      this.value = value;
      this.bits = bits;
   }


   /**
    * New MbRegister with default bit-size (32)
    * @param value
    */
   public MbRegister(String name, int value) {
      this(name, value, Definitions.BITS_REGISTER);
   }

   /*
   public MbRegister(int value, int bits) {
      this.value = value;
      this.bits = bits;
   }
    */
    



   @Override
   public Enum getType() {
      return MbOperandType.MbRegister;
   }

   @Override
   public int getBits() {
      return bits;
   }

   @Override
   public boolean isImmutable() {
      return false;
   }

   @Override
   public Operand copy() {
      return new MbRegister(name, value, bits);
   }

   @Override
   public String toString() {
      //return "reg."+value;
      return name;
   }

   public static Integer getRegValue(Operand operand) {
      // Check if MbRegister
      if(operand.getType() != MbOperandType.MbRegister) {
         return null;
      }

      return ((MbRegister)operand).value;
   }


   /**
    * INSTANCE VARIABLES
    */
   private String name;
   private Integer value;
   private int bits;
}

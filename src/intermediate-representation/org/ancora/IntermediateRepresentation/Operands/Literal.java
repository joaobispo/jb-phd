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

import org.ancora.IntermediateRepresentation.OperandType;
import org.ancora.IntermediateRepresentation.Operand;

/**
 *
 * @author Joao Bispo
 */
public class Literal extends Operand {

   public Literal(LiteralType literalType, String value, int bits) {
      this.value = value;
      this.literalType = literalType;
      this.bits = bits;
   }


   /*
   @Override
   public String getValue() {
      return value;
   }
    */

   @Override
   public Enum getType() {
      return OperandType.literal;
   }

   /*
   public LiteralType getLiteralType() {
      return literalType;
   }
    */

   @Override
   public int getBits() {
      return bits;
   }

   @Override
   public Operand copy() {
      Literal newLiteral = new Literal(literalType, value, bits);
      newLiteral.setPrefix(prefix);
      return newLiteral;
   }

   @Override
   public boolean isImmutable() {
      return true;
   }

   @Override
   public String toString() {
      return literalType.name()+"."+value;
   }

   /**
    * Checks if Operand is of type literal. If not, returns null.
    * Then, checks if is a Literal of type integer. If not, returns null.
    * Otherwise, returns an integer representing this literal.
    * @param operand
    * @return
    */
   public static Integer getInteger(Operand operand) {
   //public static Integer getInteger(Literal operand) {
      if(operand.getType() != OperandType.literal) {
         return null;
      }

      Literal litOp = (Literal)operand;
      if(litOp.literalType != LiteralType.integer) {
         return null;
      }

      return Integer.valueOf(litOp.value);
   }
   

   public enum LiteralType{
      integer;
   }

   /**
    * INSTANCE VARIABLES
    */
   private String value;
   private int bits;
   private LiteralType literalType;
}

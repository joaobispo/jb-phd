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
public class InternalData extends Operand  {

   public InternalData(String name, int bits) {
      this.name = name;
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
      return OperandType.internalData;
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
   public InternalData copy() {
      return new InternalData(name, bits);
   }

   @Override
   public String toString() {
      return name;
   }

   public String getName() {
      return name;
   }

   

   /**
    * INSTANCE VARIABLES
    */
   private String name;
   private int bits;


}

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

package org.ancora.StreamTransform.DataStructures;

import java.util.logging.Logger;
import org.ancora.IntermediateRepresentation.Operand;
import org.ancora.IntermediateRepresentation.OperandType;
import org.ancora.IntermediateRepresentation.OperationType;

/**
 *
 * @author Joao Bispo
 */
public class MemoryAddress {

   public MemoryAddress(Operand op1, Operand op2) {
      // Check priorities
      int op1P = getPriority((OperandType)op1.getType());
      int op2P = getPriority((OperandType)op2.getType());

      if(op1P > op2P) {
         base = op2;
         offset = op1;
      } else {
         base = op1;
         offset = op2;
      }
   }

   public String getAddrType() {
      return base.getType().name() + "-" + offset.getType().name();
   }

   public static int getPriority(OperandType type) {
      switch(type) {
         case livein:
            return 1;
         case internalData:
            return 2;
         case literal:
            return 3;
         default:
            Logger.getLogger(MemoryAddress.class.getName()).
                    warning("Case not defined:"+type);
            return -1;
      }
   }

   public Operand getBase() {
      return base;
   }

   public Operand getOffset() {
      return offset;
   }



   /**
    * INSTANCE VARIABLES
    */
   private Operand base;
   private Operand offset;
   /*
   private AddrType type;

   public enum AddrType {
      LiveLiteral,
      LiveOperand,
      LiveLive,
      LiteralLiteral,
      OperandLiteral,
      OperandOperand;
   }
    *
    */
}

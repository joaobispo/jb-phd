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

package org.ancora.IntermediateRepresentation.Operations;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.ancora.IntermediateRepresentation.OperationType;
import org.ancora.IntermediateRepresentation.Operand;
import org.ancora.IntermediateRepresentation.Operands.Literal;
import org.ancora.IntermediateRepresentation.Operation;
import org.ancora.IntermediateRepresentation.OperationService;
import org.ancora.SharedLibrary.BitUtils;

/**
 * <p><b>Inputs:</b>
 * <br>input1 - first operand.
 * <br>input2 - second operand.
 *
 * <p><b>Outputs:</b>
 * <br>output - result.
 *
 * <p><b>Parameters:</b>
 * <br>operation - logic operation to perform on inputs.
 *
 *  <b>Description</b>: Performs a logic operation on inputs and stores the
 * result on the output.
 *
 * @author Joao Bispo
 */
public class Logic extends Operation {

   public Logic(int address, Operand input1, Operand input2, Operand output1,
           LogicOperation operation) {

      super(address);

      //this.input1 = input1;
      //this.input2 = input2;
      //this.output1 = output1;
      this.operation = operation;
      //this.signed = signed;

      connectToInput(input1);
      connectToInput(input2);
      connectToOutput(output1);
   }


   @Override
   public Enum getType() {
      return OperationType.Logic;
   }

   @Override
   public String getName() {
      //return "ir-"+operation.name();
      return operation.name();
   }

   @Override
   public String toString() {
      return getName();
   }
   
   /*
   @Override
   public String getValue() {
      return operation.name();
   }
    */


   @Override
   public boolean hasSideEffects() {
      return false;
   }

   
   public Operand getInput1() {
      //return input1;
      return getInputs().get(0);
   }

   public Operand getInput2() {
      //return input2;
      return getInputs().get(1);
   }

   public Operand getOutput1() {
      //return output1;
      return getOutputs().get(0);
   }

   public LogicOperation getOperation() {
      return operation;
   }

   


   /**
    * INSTANCE VARIABLES
    */
   //private Operand input1;
   //private Operand input2;
   //private Operand output1;
   private Logic.LogicOperation operation;

   @Override
   public Operation copy() {
      return new Logic(getAddress(), getInput1().copy(), getInput2().copy(),
              getOutput1().copy(), operation);
   }

   @Override
   public List<Operand> resolveWhenLiteralInputs() {
      // Check if inputs are literals
      if(!OperationService.hasLiteralInputs(this)) {
         return null;
      }

      int input1 = Literal.getInteger(getInput1());
      int input2 = Literal.getInteger(getInput2());

      int result = operation.resolve(input1, input2);

      // Literals inputs. Prepare return list.
      List<Operand> resultOperands = new ArrayList<Operand>();
      resultOperands.add(Literal.newIntegerLiteral(result, getOutput1().getBits()));
      
      return resultOperands;
   }
   //private boolean signed;



   public enum LogicOperation {

      and,
      andn,
      or,
      xor,
      mbCompareSigned,
      mbCompareUnsigned,
      barrelShiftRightLogical,
      barrelShiftRightArithmetical,
      barrelShiftLeftLogical;

      private int resolve(int input1, int input2) {
         switch (this) {
            case and:
               return input1 & input2;
            case andn:
               return input1 & ~input2;
            case or:
               return input1 | input2;
            case xor:
               return input1 ^ input2;
            case mbCompareSigned:
               int result = input2 + ~input1 + 1;
               boolean aBiggerThanB = input1 > input2;
               // Change MSB to reflect relation
               if (aBiggerThanB) {
                  return BitUtils.setBit(31, result);
               } else {
                  return BitUtils.clearBit(31, result);
               }
            case mbCompareUnsigned:
               result = input2 + ~input1 + 1;
               aBiggerThanB = BitUtils.unsignedComp(input1, input2);
               // Change MSB to reflect relation
               if (aBiggerThanB) {
                  return BitUtils.setBit(31, result);
               } else {
                  return BitUtils.clearBit(31, result);
               }
            case barrelShiftLeftLogical:
               return input1 << input2;
            case barrelShiftRightArithmetical:
               return input1 >> input2;
            case barrelShiftRightLogical:
               return input1 >>> input2;
            default:
               Logger.getLogger(Logic.class.getName()).
                       warning("Case not defined:" + this);
               return 0;
         }

      }
   }
}

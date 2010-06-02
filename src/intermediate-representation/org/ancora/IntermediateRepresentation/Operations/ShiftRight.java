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

import org.ancora.IntermediateRepresentation.OperationType;
import org.ancora.IntermediateRepresentation.Operand;
import org.ancora.IntermediateRepresentation.Operation;

/**
 * <p><b>Inputs:</b>
 * <br>input1 - first operand.
 * <br>carryIn - carry bit (optional).
 *
 * <p><b>Outputs:</b>
 * <br>output - result.
 * <br>carryOut - carry bit.
 *
 * <p><b>Parameters:</b>
 * <br>operation - kind of shift to perform on input.
 *
 *  <b>Description</b>: Performs a shift right operation on the input and stores
 * the result on the output and the least significant bit coming out the shift
 * chain is put on the Carry flag.
 *
 * @author Joao Bispo
 */
public class ShiftRight extends Operation {

   public ShiftRight(int address, ShiftRight.Op operation, Operand input,
           Operand output, Operand carryIn, Operand carryOut) {
      super(address);

      this.operation = operation;
      //this.input = input;
      //this.output = output;
      //this.carryIn = carryIn;
      //this.carryOut = carryOut;

      // Connect Inputs
      connectToInput(input);
      if(carryIn == null) {
         hasCarryIn = false;
      } else {
         connectToInput(carryIn);
         hasCarryIn = true;
      }

      // Connect Outputs
      connectToOutput(output);
      if(carryOut == null) {
         hasCarryOut = false;
      } else {
         connectToOutput(carryOut);
         hasCarryOut = true;
      }
   }



   @Override
   public Enum getType() {
      return OperationType.ShiftRight;
   }

   @Override
   public String getName() {
      return operation.name();
   }

   @Override
   public String toString() {
      return getName();
   }

   @Override
   public boolean hasSideEffects() {
      return false;
   }

   public Operand getInput() {
      //return input;
      return getInputs().get(0);
   }

   public Operand getOutput() {
      //return output;
       return getOutputs().get(0);
   }

   public Op getOperation() {
      return operation;
   }

   public Operand getCarryIn() {
      if (hasCarryIn) {
         return getInputs().get(1);
      } else {
         return null;
      }
      //return carryIn;
   }

   public Operand getCarryOut() {
      if (hasCarryOut) {
         return getOutputs().get(1);
      } else {
         return null;
      }
      //return carryOut;
   }

   

   public enum Op {
      shiftRightArithmetic,
      shiftRightWithCarry,
      shiftRightLogical;
   }

   /**
    * INSTANCE VARIABLES
    */
   //private Operand input;
   //private Operand output;
   //private Operand carryIn;
   //private Operand carryOut;
   private boolean hasCarryIn;
   private boolean hasCarryOut;

   private ShiftRight.Op operation;
}

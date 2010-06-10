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

   public ShiftRight(int address, ShiftRight.ShiftRightOp operation, Operand input,
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
         Logger.getLogger(ShiftRight.class.getName()).
                 warning("Carry out == null. This operation should always have a carry out.");
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

   public ShiftRightOp getOperation() {
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

   @Override
   public Operation copy() {
            Operand newCarryIn = null;
      if(getCarryIn() != null) {
         newCarryIn = getCarryIn().copy();
      }
      Operand newCarryOut = null;
      if(getCarryOut() != null) {
         newCarryOut = getCarryOut().copy();
      }
      return new ShiftRight(getAddress(), operation, getInput().copy(), getOutput().copy(),
              newCarryIn, newCarryOut);
   }

   @Override
   public List<Operand> resolveWhenLiteralInputs() {
      // Check if inputs are literals
      if(!OperationService.hasLiteralInputs(this)) {
         return null;
      }

      // Literals inputs. Prepare return list.
      List<Operand> resultOperands = operation.resolve(getInputs());    

      return resultOperands;
   }




   public enum ShiftRightOp {
      shiftRightArithmetic,
      shiftRightWithCarry,
      shiftRightLogical;


      private List<Operand> resolve(List<Operand> inputs) {
         int value = Literal.getInteger(inputs.get(0));
         Integer carryInValue = null;
         if(inputs.size() > 1) {
            carryInValue = Literal.getInteger(inputs.get(1));
         }

         // Get bit 0
         int msBit = getMsBit(value, carryInValue);

         // Shift value right by 1
         int newValue = value >> 1;
         // Set the most significant bit
         BitUtils.setBit(msBit, newValue);

         // Build value
         Literal valueOperand = Literal.newIntegerLiteral(newValue, inputs.get(0).getBits());
         // Build carry-out
         Literal carryOut = Literal.newIntegerLiteral(BitUtils.getBit(0, value), 1);

         List<Operand> equivalentOperands = new ArrayList<Operand>();
         equivalentOperands.add(valueOperand);
         equivalentOperands.add(carryOut);

         return equivalentOperands;
      }

      private int getMsBit(int value, Integer carryIn) {
         switch(this) {
            case shiftRightArithmetic:
               return BitUtils.getBit(31, value);
            case shiftRightLogical:
               return 0;
            case shiftRightWithCarry:
               if (carryIn == null) {
                  Logger.getLogger(ShiftRightOp.class.getName()).
                          warning("ShiftRightWithCarry without CarryIn!");
                  return 0;
               }
               return carryIn;
            default:
               Logger.getLogger(ShiftRightOp.class.getName()).
                       warning("Case not defined: "+this);
               return 0;
         }
      }
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

   private ShiftRight.ShiftRightOp operation;
}

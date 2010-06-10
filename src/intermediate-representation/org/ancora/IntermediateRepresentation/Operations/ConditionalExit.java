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

/**
 * <p><b>Inputs:</b>
 * <br>input1 - value to be compared to zero.
 * <br>input2 - offset of the next address.
 *
 * <p><b>Parameters:</b>
 * <br>currentAddress - address of the equivalent instruction.
 * <br>comparisonOperation - equal, notEqual, greater, greaterOrEqual, less,
 * lessOrEqual.
 * <br>supposedJumpAddress - address of the next instruction in the block.
 * <br>delaySlots – number of delay slots of this jump.
 * <br>numberOfExit – identifies the exit.
 *
 * <b>Description</b>: Input1 is compared to 0, according to a specific
 * operation (equal, greater, etc...). The address of the next instruction
 * depends on this result: if true, the address is currentAddress + input2; if
 * false, the address is currentAddress + 4. If this address is the same as the
 * supposedJumpAddress, the hardware execution can continue. Otherwise, it must
 * terminate.
 *
 * @author Joao Bispo
 */
public class ConditionalExit extends Operation {


   public ConditionalExit(int address, ConditionalExit.ZeroCondition op, int nextAddress,
           int delaySlots, int numberOfExit, Operand input1, Operand input2) {
      super(address);
      this.op = op;
      //this.nextTraceInstOffset = nextTraceInstOffset;
      this.supposedJumpAddress = nextAddress;
      this.delaySlots = delaySlots;
      //this.input1 = input1;
      //this.input2 = input2;

      connectToInput(input1);
      connectToInput(input2);
   }


   @Override
   public Enum getType() {
      return OperationType.ConditionalExit;
   }

   @Override
   public boolean hasSideEffects() {
      //return true;
      return false;
   }

   @Override
   public String getName() {
      return "conditionalExit."+op.name();
   }

   @Override
   public String toString() {
      return getName();
   }



public Operand getInput(ConditionalExit.Input input) {
   switch(input) {
      case valueToBeComparedToZero:
         return getInputs().get(0);
      case offset:
         return getInputs().get(1);
      default:
         System.out.println("ConditionalExit problem.");
         return null;
   }
}

public int getCurrentAddress(){
   return getAddress();
}

public ZeroCondition getOperation() {
   return op;
}

public int getSupposedJumpAddress() {
   return supposedJumpAddress;
}

   public int getDelaySlots() {
      return delaySlots;
   }

   public int getNumberOfExit() {
      return numberOfExit;
   }

   @Override
   public List<Operand> resolveWhenLiteralInputs() {
      Operand testOperand = getInput(Input.valueToBeComparedToZero);
      Integer testValue = Literal.getInteger(testOperand);

      if(testValue == null) {
         return null;
      }

      boolean testResult = op.test(testValue);

      // If test result is true, check if offset is literal
      Integer offsetValue = null;

      if(testResult) {
         Operand offset = getInput(Input.offset);
         //Integer offsetValue = Literal.getInteger(offset);
         offsetValue = Literal.getInteger(offset);
         if(offsetValue == null) {
            return null;
         }

         /*
         // Check if supposed jump address is equal to currentPC + offset
         int branchTarget = getCurrentAddress() + offsetValue;
         if(getSupposedJumpAddress() != branchTarget) {
            Logger.getLogger(ConditionalExit.class.getName()).
                    warning("Jump mismatch: Next trace instruction ("+getSupposedJumpAddress()+
                    ") different from branch target ("+branchTarget+")");
         }

         return new ArrayList<Operand>();
*/
      }
      else {
         offsetValue = 4 + (4*getDelaySlots());
      }

      // Check if supposed jump address is equal to currentPC + offset
      int branchTarget = getCurrentAddress() + offsetValue;

      if (getSupposedJumpAddress() != branchTarget) {
         Logger.getLogger(ConditionalExit.class.getName()).
                 warning("Jump mismatch: Next trace instruction (" + getSupposedJumpAddress()
                 + ") different from branch target (" + branchTarget + ")");
      }

      return new ArrayList<Operand>();

      //System.err.println("Conditional Exit which can be resolved?");
      //System.err.println(getFullOperation());

      /*
      boolean testResult = testCondition();
      return super.resolveWhenLiteralInputs();
       *
       */
   }



   /**
    * INSTANCE VARIABLES
    */
   private ConditionalExit.ZeroCondition op;
   //private int nextTraceInstOffset;
   private int supposedJumpAddress;
   private int delaySlots;
   //private Operand input1;
   //private Operand input2;
   private int numberOfExit;

   @Override
   public Operation copy() {
      return new ConditionalExit(getAddress(), op, supposedJumpAddress, delaySlots,
              numberOfExit, getInput(Input.valueToBeComparedToZero).copy(),
              getInput(Input.offset).copy());
   }


public enum Input {
   valueToBeComparedToZero,
   offset
}

   public enum ZeroCondition {
      equal,
      notEqual,
      greater,
      greaterOrEqual,
      less,
      lessOrEqual,;

      private boolean test(Integer testValue) {
         switch(this) {
            case equal:
               return testValue == 0;
            case notEqual:
               return testValue != 0;
            case greater:
               return testValue > 0;
            case greaterOrEqual:
               return testValue >= 0;
            case less:
               return testValue < 0;
            case lessOrEqual:
               return testValue <= 0;
            default:
               Logger.getLogger(ZeroCondition.class.getName()).
                       warning("Case not defined: "+this);
               return false;
         }
      }
   }
}

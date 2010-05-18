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
 * <br>input1 - offset of the next address.
 *
 * <p><b>Parameters:</b>
 * <br>baseAddress - base address of the next instruction.
 * <br>supposedJumpAddress - address of the next instruction in the block.
 *
 * <b>Description</b>: The next address is calculated as baseAddress + input1.
 * If the next address is equal to supposedJumpAddress, the hardware execution
 * can continue. Otherwise, it must terminate.
 *
 * @author Joao Bispo
 */
public class UnconditionalExit extends Operation {

   public UnconditionalExit(int address, int baseAddress, 
           int supposedJumpAddress, int delaySlots, Operand input1, Operand output1) {
      super(address);
      this.baseAddress = baseAddress;
      this.supposedJumpAddress = supposedJumpAddress;
      this.delaySlots = delaySlots;
//      this.input1 = input1;

      connectToInput(input1);
      if(output1 != null) {
         connectToOutput(output1);
      }
   }

   @Override
   public String toString() {
      return "UnconditionalExit";
   }



   @Override
   public Enum getType() {
      return OperationType.UnconditionalExit;
   }

   @Override
   public boolean hasSideEffects() {
//      return false;
      return true;
   }

   public Operand getInput1() {
      //return input1;
      return getInputs().get(0);
   }

   public Operand getOutput1() {
      //return input1;
      if(getOutputs().isEmpty()) {
         return null;
      }

      return getOutputs().get(0);
   }

   public int getLinkingAddress() {
      return getAddress();
   }

   public int getBaseAddress() {
      return baseAddress;
   }

   public int getSupposedJumpAddress() {
      return supposedJumpAddress;
   }

   public int getDelaySlots() {
      return delaySlots;
   }

   


   /**
    * INSTANCE VARIABLES
    */
   private int baseAddress;
   private int supposedJumpAddress;
   private int delaySlots;
   //private Operand input1;
}

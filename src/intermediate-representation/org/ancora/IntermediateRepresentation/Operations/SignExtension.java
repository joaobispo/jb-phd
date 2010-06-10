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
import org.ancora.IntermediateRepresentation.OperationType;
import org.ancora.IntermediateRepresentation.Operand;
import org.ancora.IntermediateRepresentation.Operands.Literal;
import org.ancora.IntermediateRepresentation.Operation;
import org.ancora.IntermediateRepresentation.OperationService;
import org.ancora.SharedLibrary.BitUtils;

/**
 * <p><b>Inputs:</b>
 * <br>input1 - first operand.
 *
 * <p><b>Outputs:</b>
 * <br>output - result.
 *
 * <p><b>Parameters:</b>
 * <br>inputBits - the sign extension will be based on how many bits of the input.
 *
 *  <b>Description</b>: Performs a sign extension on the input and
 * stores the result on the output.
 *
 * @author Joao Bispo
 */
public class SignExtension extends Operation {

   public SignExtension(int address, Operand input1, Operand output1,
           int extensionSizeInBits) {

      super(address);

      //this.input1 = input1;
      //this.output = output1;
      this.extensionSizeInBits = extensionSizeInBits;

      connectToInput(input1);
      connectToOutput(output1);
   }

   @Override
   public String getName() {
      return "SignExtension "+extensionSizeInBits;
   }

   @Override
   public String toString() {
      return getName();
   }


   @Override
   public Enum getType() {
      return OperationType.SignExtension;
   }

   @Override
   public boolean hasSideEffects() {
      return false;
//      return true;
   }

   public Operand getInput1() {
      //return input1;
      return getInputs().get(0);
   }

   public Operand getOutput() {
      //return output;
      return getOutputs().get(0);
   }

   public int getExtensionSizeInBits() {
      return extensionSizeInBits;
   }



   

   /**
    * INSTANCE VARIABLES
    */
   //private Operand input1;
   //private Operand output;
   private int extensionSizeInBits;

   @Override
   public Operation copy() {
      return new SignExtension(getAddress(), getInput1().copy(), getOutput().copy(),
              extensionSizeInBits);
   }

   @Override
   public List<Operand> resolveWhenLiteralInputs() {
      // Check if inputs are literals
      if(!OperationService.hasLiteralInputs(this)) {
         return null;
      }

      int input1 = Literal.getInteger(getInput1());

      int newResult = input1;
      int msbValue = BitUtils.getBit(extensionSizeInBits-1, input1);
      // Replicate bit on the rest of newResult bits
      for(int i=extensionSizeInBits; i<31; i++) {
         if(msbValue == 0) {
            newResult = BitUtils.clearBit(i, newResult);
         } else {
            newResult = BitUtils.setBit(i, newResult);
         }
         
      }

      //System.err.println("Operation "+getAddress()+": Sign Extension "+extensionSizeInBits);
      //System.err.println("Original: "+input1+"("+Integer.toBinaryString(input1)+")");
      //System.err.println("New: "+newResult+"("+Integer.toBinaryString(newResult)+")");

      // Literals inputs. Prepare return list.
      List<Operand> resultOperands = new ArrayList<Operand>();
      resultOperands.add(Literal.newIntegerLiteral(newResult, getOutput().getBits()));

      return resultOperands;
   }


}

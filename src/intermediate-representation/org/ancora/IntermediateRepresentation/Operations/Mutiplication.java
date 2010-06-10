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

/**
 * <p><b>Inputs:</b>
 * <br>input1 - first operand.
 * <br>input2 - second operand.
 *
 * <p><b>Outputs:</b>
 * <br>output - result.
 *
 *
 *  <b>Description</b>: Performs a multiply operation on inputs and stores the
 * result on the output. If the multiplication results has more bits than the
 * output operand, the most significant bits of the result are discarded.
 *
 *
 * @author Joao Bispo
 */
public class Mutiplication extends Operation {

   public Mutiplication(int address, Operand input1, Operand input2, Operand output1) {

      super(address);

//      this.input1 = input1;
//      this.input2 = input2;
//      this.output = output1;

      connectToInput(input1);
      connectToInput(input2);
      connectToOutput(output1);
   }

   @Override
   public Enum getType() {
      return OperationType.Multiplication;
   }

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

   public Operand getOutput() {
      //return output;
      return getOutputs().get(0);
   }


   @Override
   public String getName() {
      return "mul";
   }

   @Override
   public String toString() {
      return getName();
   }

   @Override
   public Operation copy() {
      return new Mutiplication(getAddress(), getInput1().copy(), getInput2().copy(),
              getOutput().copy());
   }

   @Override
   public List<Operand> resolveWhenLiteralInputs() {
      // Check if inputs are literals
      if(!OperationService.hasLiteralInputs(this)) {
         return null;
      }

      int input1 = Literal.getInteger(getInput1());
      int input2 = Literal.getInteger(getInput2());

      int result = input1 * input2;

      // Literals inputs. Prepare return list.
      List<Operand> resultOperands = new ArrayList<Operand>();
      resultOperands.add(Literal.newIntegerLiteral(result, getOutput().getBits()));

      return resultOperands;
   }
   

   /**
    * INSTANCE VARIABLES
    */
   //private Operand input1;
   //private Operand input2;
   //private Operand output;

}

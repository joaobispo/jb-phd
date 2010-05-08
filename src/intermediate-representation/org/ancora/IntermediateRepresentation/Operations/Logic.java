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
           Op operation) {

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
   public String toString() {
      //return "ir-"+operation.name();
      return operation.name();
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

   public Op getOperation() {
      return operation;
   }

   


   /**
    * INSTANCE VARIABLES
    */
   //private Operand input1;
   //private Operand input2;
   //private Operand output1;
   private Logic.Op operation;
   //private boolean signed;


   public enum Op {
      and,
      andn,
      or,
      xor,
      mbCompareSigned,
      mbCompareUnsigned,
      barrelShiftRightLogical,
      barrelShiftRightArithmetical,
      barrelShiftLeftLogical;
   }
}

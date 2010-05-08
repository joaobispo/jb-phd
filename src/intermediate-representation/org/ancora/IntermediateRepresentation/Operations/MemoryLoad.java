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
 * <br>bytes - size of the load.
 *
 *  <b>Description</b>: Load the contents from the location that results from
 * adding the contents of input1 and input2. The data is placed in output.
 * The memory location is aligned acording to the number of bytes indicated by
 * 'bytes'.
 *
 * @author Joao Bispo
 */
public class MemoryLoad extends Operation {

   public MemoryLoad(int address, Operand input1, Operand input2, Operand output, int bytes) {
      super(address);


      this.bytes = bytes;

      connectToInput(input1);
      connectToInput(input2);
      connectToOutput(output);

      //this.input1 = getInputs().get(0);
      //this.input2 = getInputs().get(1);
      //this.output = getOutputs().get(0);
   }



   @Override
   public Enum getType() {
      return OperationType.MemoryLoad;
   }

   @Override
   public boolean hasSideEffects() {
      return true;
   }

   public int getBytes() {
      return bytes;
   }

   public Operand getInput1() {
      return getInputs().get(0);
      //return input1;
   }

   public Operand getInput2() {
      return getInputs().get(1);
      //return input2;
   }

   public Operand getOutput() {
      return getOutputs().get(0);
      //return output;
   }

   @Override
   public String toString() {
      return "load "+(bytes*8);
   }



   /**
    * INSTANCE VARIABLES
    */
   //private Operand input1;
   //private Operand input2;
   //private Operand output;
   private int bytes;
}

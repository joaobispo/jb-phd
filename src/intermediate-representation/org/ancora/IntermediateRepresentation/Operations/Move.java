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
import org.ancora.IntermediateRepresentation.Operation;

/**
 * <p><b>Inputs:</b>
 * <br>input1 - first operand.
 *
 * <p><b>Outputs:</b>
 * <br>output - result.
 *
 *  <b>Description</b>: Moves the contents of the input to the output.
 *
 * @author Joao Bispo
 */
public class Move extends Operation {

   public Move(int address, Operand input1, Operand output1) {
      super(address);

      connectToInput(input1);
      connectToOutput(output1);
   }

   public Move(int address, List<Operand> inputs, List<Operand> outputs) {
      super(address);

      if(inputs.size() != outputs.size()) {
         Logger.getLogger(Move.class.getName()).
                 warning("Inputs size ("+inputs.size()+") different from outputs size ("+outputs.size()+")");
      }

      for(Operand input : inputs) {
         connectToInput(input);
      }
      for(Operand output : outputs) {
         connectToOutput(output);
      }
   }

   @Override
   public String getName() {
      return "Move";
   }

   @Override
   public String toString() {
      return getName();
   }


   @Override
   public Enum getType() {
      return OperationType.Move;
   }

   @Override
   public boolean hasSideEffects() {
      return false;
   }


   public Operand getInput1() {
      //return input1;
      return getInputs().get(0);
   }

   public Operand getOutput1() {
      //return output;
      return getOutputs().get(0);
   }

   @Override
   public Operation copy() {
      List<Operand> originalInputs = getInputs();
      List<Operand> inputs = new ArrayList<Operand>();
      for(Operand originalInput : originalInputs) {
         inputs.add(originalInput.copy());
      }
      
      List<Operand> originalOutputs = getOutputs();
      List<Operand> outputs = new ArrayList<Operand>();
      for(Operand originalOutput : originalOutputs) {
         outputs.add(originalOutput.copy());
      }

      //return new Move(getAddress(), getInput1().copy(), getOutput1().copy());
      return new Move(getAddress(), inputs, outputs);
   }

}

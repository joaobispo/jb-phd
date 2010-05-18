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

package org.ancora.IntermediateRepresentation.Transformations.MicroblazeGeneral;

import java.util.List;
import org.ancora.IntermediateRepresentation.Operand;
import org.ancora.IntermediateRepresentation.Operands.InternalData;
import org.ancora.IntermediateRepresentation.MbOperandType;
import org.ancora.IntermediateRepresentation.Operation;
import org.ancora.IntermediateRepresentation.Transformation;

/**
 * Transforms MbRegisters into Internal Data
 *
 * @author Joao Bispo
 */
public class TransformRegistersToInternalData implements Transformation {

   public List<Operation> transform(List<Operation> operations) {
      for(int i=0; i<operations.size(); i++) {
         Operation operation = operations.get(i);

         List<Operand> inputs = operation.getInputs();
         for(int j = 0; j<inputs.size(); j++) {
            Operand input = inputs.get(j);

            if(input.getType() != MbOperandType.MbRegister) {
               continue;
            }

            String registerName = input.toString();
            Operand newOperand = new InternalData(registerName, input.getBits());
            newOperand.setPrefix(input.getPrefix());
            inputs.set(j, newOperand);
         }

         List<Operand> outputs = operation.getOutputs();
         for(int j = 0; j<outputs.size(); j++) {
            Operand output = outputs.get(j);

            if(output.getType() != MbOperandType.MbRegister) {
               continue;
            }

            String registerName = output.toString();
            Operand newOperand = new InternalData(registerName, output.getBits());
            outputs.set(j, newOperand);
         }
      }

      return operations;
   }

}

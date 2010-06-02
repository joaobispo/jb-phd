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

import java.util.BitSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.ancora.IntermediateRepresentation.Operand;
import org.ancora.IntermediateRepresentation.Operands.InternalData;
import org.ancora.IntermediateRepresentation.MbOperandType;
import org.ancora.IntermediateRepresentation.Operands.LiveIn;
import org.ancora.IntermediateRepresentation.Operation;
import org.ancora.IntermediateRepresentation.Transformation;

/**
 * Transforms MbRegisters into Internal Data  and calculates
 * the live-ins, transforming live-in operands as LiveIns.
 *
 * @author Joao Bispo
 */
public class TransformRegisters extends Transformation {

   public void transform(List<Operation> operations) {

      Set<String> definedRegisters = new HashSet<String>();

      for(int i=0; i<operations.size(); i++) {
         Operation operation = operations.get(i);

         // Process Inputs
         List<Operand> inputs = operation.getInputs();
         for(int j = 0; j<inputs.size(); j++) {
            Operand input = inputs.get(j);

            if(input.getType() != MbOperandType.MbRegister) {
               continue;
            }

            String registerName = input.getName();

            // Check if livein
            Operand newOperand;
            if(!definedRegisters.contains(registerName)) {
               newOperand = new LiveIn(registerName, input.getBits());
            } else {
               newOperand = new InternalData(registerName, input.getBits());
            }

            //String registerName = input.toString();
            //Operand newOperand = new InternalData(registerName, input.getBits());
            newOperand.setPrefix(input.getPrefix());
            inputs.set(j, newOperand);
         }

         // Process Outputs
         List<Operand> outputs = operation.getOutputs();
         for(int j = 0; j<outputs.size(); j++) {
            Operand output = outputs.get(j);

            if(output.getType() != MbOperandType.MbRegister) {
               continue;
            }


            String registerName = output.getName();

            // Cancel as LiveIns
            definedRegisters.add(registerName);

            Operand newOperand = new InternalData(registerName, output.getBits());
            outputs.set(j, newOperand);
         }
      }

   }

}

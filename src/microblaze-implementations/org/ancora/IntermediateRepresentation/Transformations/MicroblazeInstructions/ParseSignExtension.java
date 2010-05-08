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

package org.ancora.IntermediateRepresentation.Transformations.MicroblazeInstructions;

import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import org.ancora.IntermediateRepresentation.Operand;
import org.ancora.IntermediateRepresentation.Operation;
import org.ancora.IntermediateRepresentation.Operations.Logic;
import org.ancora.IntermediateRepresentation.Operations.MbOperation;
import org.ancora.IntermediateRepresentation.Operations.SignExtension;
import org.ancora.MicroBlaze.InstructionName;
import org.ancora.IntermediateRepresentation.Transformation;

/**
 *
 * @author Joao Bispo
 */
public class ParseSignExtension implements Transformation {

   @Override
   public String toString() {
      return "ParseSignExtension";
   }



   public List<Operation> transform(List<Operation> operations) {
      for(int i=0; i<operations.size(); i++) {
         Operation operation = operations.get(i);

        // Check if MicroBlaze Operation
        MbOperation signExtOp = MbOperation.getMbOperation(operation);
        if(signExtOp == null) {
           continue;
        }

        // Check if it is an unconditional compare
        Integer extensionSize =
                instructionProperties.get(signExtOp.getMbType());
        if(extensionSize == null) {
           continue;
        }

         Operand input1 = signExtOp.getInputs().get(0).copy();
         Operand output = signExtOp.getOutputs().get(0).copy();

         Operation newOperation = new SignExtension(signExtOp.getAddress(),
                 input1, output, extensionSize);

        // Replace old operation
        operations.set(i, newOperation);
      }

      return operations;
   }

   /**
    * INSTANCE VARIABLES
    */
      private static final Map<InstructionName, Integer> instructionProperties;
   static {
      Map<InstructionName, Integer> aMap = new Hashtable<InstructionName, Integer>();

      aMap.put(InstructionName.sext8, 8);
      aMap.put(InstructionName.sext16, 16);

      instructionProperties = Collections.unmodifiableMap(aMap);
   }

}

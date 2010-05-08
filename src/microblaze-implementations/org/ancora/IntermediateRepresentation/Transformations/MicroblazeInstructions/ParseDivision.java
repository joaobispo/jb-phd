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
import org.ancora.IntermediateRepresentation.Operations.Division;
import org.ancora.IntermediateRepresentation.Operations.MbOperation;
import org.ancora.MicroBlaze.InstructionName;
import org.ancora.IntermediateRepresentation.Transformation;

/**
 *
 * @author Joao Bispo
 */
public class ParseDivision implements Transformation {

   @Override
   public String toString() {
      return "ParseDivision";
   }



   public List<Operation> transform(List<Operation> operations) {
      for(int i=0; i<operations.size(); i++) {
         Operation operation = operations.get(i);

        // Check if MicroBlaze Operation
        MbOperation divisionOp = MbOperation.getMbOperation(operation);
        if(divisionOp == null) {
           continue;
        }

        // Check if it is a division
        Division.Op divisionOperation =
                instructionProperties.get(divisionOp.getMbType());
        if(divisionOperation == null) {
           continue;
        }

         Operand input1 = divisionOp.getInputs().get(0).copy();
         Operand input2 = divisionOp.getInputs().get(1).copy();
         Operand output = divisionOp.getOutputs().get(0).copy();

         Operation newOperation = new Division(divisionOp.getAddress(),
                 input1, input2, output, divisionOperation);

        // Replace old operation
        operations.set(i, newOperation);
      }

      return operations;
   }

   /**
    * INSTANCE VARIABLES
    */
      private static final Map<InstructionName, Division.Op> instructionProperties;
   static {
      Map<InstructionName, Division.Op> aMap = new Hashtable<InstructionName, Division.Op>();

      aMap.put(InstructionName.idiv, Division.Op.mbIntegerDivisionSigned);
      aMap.put(InstructionName.idivu, Division.Op.mbIntegerDivisionUnsigned);
 
      instructionProperties = Collections.unmodifiableMap(aMap);
   }


}

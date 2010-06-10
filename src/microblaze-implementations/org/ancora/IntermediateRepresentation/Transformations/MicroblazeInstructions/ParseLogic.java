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
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.ancora.IntermediateRepresentation.Operand;
import org.ancora.IntermediateRepresentation.Operation;
import org.ancora.IntermediateRepresentation.Operations.Logic;
import org.ancora.IntermediateRepresentation.Operations.MbOperation;
import org.ancora.MicroBlaze.InstructionName;
import org.ancora.IntermediateRepresentation.Transformation;

/**
 *
 * @author Joao Bispo
 */
public class ParseLogic extends Transformation {

   @Override
   public String toString() {
      return "ParseLogic";
   }



   public void transform(List<Operation> operations) {
      for(int i=0; i<operations.size(); i++) {
         Operation operation = operations.get(i);

        // Check if MicroBlaze Operation
        MbOperation logicOp = MbOperation.getMbOperation(operation);
        if(logicOp == null) {
           continue;
        }

        // Check if it is an unconditional compare
        Logic.LogicOperation logicOperation =
                instructionProperties.get(logicOp.getMbType());
        if(logicOperation == null) {
           continue;
        }

         Operand input1 = logicOp.getInputs().get(0).copy();
         Operand input2 = logicOp.getInputs().get(1).copy();
         Operand output = logicOp.getOutputs().get(0).copy();

         Operation newOperation = new Logic(logicOp.getAddress(),
                 input1, input2, output, logicOperation);

        // Replace old operation
        operations.set(i, newOperation);
      }

   }

   /**
    * INSTANCE VARIABLES
    */
      private static final Map<InstructionName, Logic.LogicOperation> instructionProperties;
   static {
      Map<InstructionName, Logic.LogicOperation> aMap = new EnumMap<InstructionName, Logic.LogicOperation>(InstructionName.class);

      aMap.put(InstructionName.and, Logic.LogicOperation.and);
      aMap.put(InstructionName.andi, Logic.LogicOperation.and);
      aMap.put(InstructionName.andn, Logic.LogicOperation.andn);
      aMap.put(InstructionName.andni, Logic.LogicOperation.andn);
      aMap.put(InstructionName.cmp, Logic.LogicOperation.mbCompareSigned);
      aMap.put(InstructionName.cmpu, Logic.LogicOperation.mbCompareUnsigned);
      aMap.put(InstructionName.or, Logic.LogicOperation.or);
      aMap.put(InstructionName.ori, Logic.LogicOperation.or);
      aMap.put(InstructionName.xor, Logic.LogicOperation.xor);
      aMap.put(InstructionName.xori, Logic.LogicOperation.xor);
      aMap.put(InstructionName.bsrl, Logic.LogicOperation.barrelShiftRightLogical);
      aMap.put(InstructionName.bsra, Logic.LogicOperation.barrelShiftRightArithmetical);
      aMap.put(InstructionName.bsll, Logic.LogicOperation.barrelShiftLeftLogical);
      aMap.put(InstructionName.bsrli, Logic.LogicOperation.barrelShiftRightLogical);
      aMap.put(InstructionName.bsrai, Logic.LogicOperation.barrelShiftRightArithmetical);
      aMap.put(InstructionName.bslli, Logic.LogicOperation.barrelShiftLeftLogical);

      instructionProperties = Collections.unmodifiableMap(aMap);
   }

}

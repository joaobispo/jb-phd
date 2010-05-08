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
import org.ancora.IntermediateRepresentation.Operations.MbOperation;
import org.ancora.IntermediateRepresentation.Operations.UnconditionalExit;
import org.ancora.MicroBlaze.InstructionName;
import org.ancora.IntermediateRepresentation.MbTransformUtils;
import org.ancora.IntermediateRepresentation.Operands.MbImm;
import org.ancora.IntermediateRepresentation.Transformation;

/**
 *
 * @author Joao Bispo
 */
public class ParseReturnSubroutine implements Transformation {

   @Override
   public String toString() {
      return "ParseReturnFromSubroutine";
   }



   public List<Operation> transform(List<Operation> operations) {
      for(int i=0; i<operations.size(); i++) {
         Operation operation = operations.get(i);

        // Check if MicroBlaze Operation
        MbOperation rstdOp = MbOperation.getMbOperation(operation);
        if(rstdOp == null) {
           continue;
        }

        // Check if it is a rstd
        if(rstdOp.getMbType() != InstructionName.rtsd) {
           continue;
        }

         Operand input1 = rstdOp.getInputs().get(0).copy();

         //int baseAddress = MbTransformUtils.getIntegerValue(rstdOp.getInputs().get(1));
         int baseAddress = MbImm.getImmValue(rstdOp.getInputs().get(1));
         int supposedJumpAddress = MbTransformUtils.calculateNextAddress(operations, i,
                rstdOp.getMbType());
         int delaySlots = 1;

         Operation newOperation = new UnconditionalExit(rstdOp.getAddress(),
                 baseAddress, supposedJumpAddress, delaySlots, input1);

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

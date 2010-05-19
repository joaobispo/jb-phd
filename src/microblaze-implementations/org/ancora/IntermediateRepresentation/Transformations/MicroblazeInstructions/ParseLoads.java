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
import org.ancora.IntermediateRepresentation.Operations.MbOperation;
import org.ancora.IntermediateRepresentation.Operations.MemoryLoad;
import org.ancora.MicroBlaze.InstructionName;
import org.ancora.IntermediateRepresentation.Transformation;

/**
 *
 * @author Joao Bispo
 */
public class ParseLoads extends Transformation {

   @Override
   public String toString() {
      return "ParseLoads";
   }


   public void transform(List<Operation> operations) {
      for (int i = 0; i < operations.size(); i++) {
                  Operation operation = operations.get(i);

        // Check if MicroBlaze Operation
        MbOperation loadOp = MbOperation.getMbOperation(operation);
        if(loadOp == null) {
           continue;
        }

        // Check if it is an unconditional compare
        Integer bytes =
                instructionProperties.get(loadOp.getMbType());
        if(bytes == null) {
           continue;
        }

        Operand input1 = operation.getInputs().get(0).copy();
        Operand input2 = operation.getInputs().get(1).copy();
        Operand output = operation.getOutputs().get(0).copy();

        input1.setPrefix("base-");
        input2.setPrefix("offset-");
        
        MemoryLoad newOp = new MemoryLoad(loadOp.getAddress(), input1, input2,
                output, bytes);

        operations.set(i, newOp);
      }

   }

   /**
    * INSTANCE VARIABLES
    */
   private static final Map<InstructionName, Integer> instructionProperties;
   static {
      Map<InstructionName, Integer> aMap = new EnumMap<InstructionName, Integer>(InstructionName.class);

      aMap.put(InstructionName.lbu, 1);
      aMap.put(InstructionName.lbui, 1);
      aMap.put(InstructionName.lhu, 2);
      aMap.put(InstructionName.lhui, 2);
      aMap.put(InstructionName.lw, 4);
      aMap.put(InstructionName.lwi, 4);

      instructionProperties = Collections.unmodifiableMap(aMap);
   }

}

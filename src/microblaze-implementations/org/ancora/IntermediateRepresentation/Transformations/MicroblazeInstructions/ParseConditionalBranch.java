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

import static org.ancora.IntermediateRepresentation.Operations.ConditionalExit.Op.*;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.ancora.IntermediateRepresentation.Operation;
import org.ancora.IntermediateRepresentation.Operations.ConditionalExit;
import org.ancora.IntermediateRepresentation.Operations.MbOperation;
import org.ancora.MicroBlaze.InstructionName;
import org.ancora.IntermediateRepresentation.MbTransformUtils;
import org.ancora.IntermediateRepresentation.Transformation;

/**
 *
 * @author Joao Bispo
 */
public class ParseConditionalBranch extends Transformation {

   public void transform(List<Operation> operations) {

      for(int i=0; i<operations.size(); i++) {
        // Check if MicroBlaze Operation
        MbOperation branchOp = MbOperation.getMbOperation(operations.get(i));
        if(branchOp == null) {
           continue;
        }

        // Check if it is a conditional compare
        ConditionalExit.Op compareOperation = instructionProperties.get(branchOp.getMbType());
        if(compareOperation == null) {
           continue;
        }

        // Check if it has delay slots
        int delaySlots = MbTransformUtils.getDelaySlots(branchOp.getMbType());

        // Calculate nextAddress
        int nextAddress = MbTransformUtils.calculateNextAddress(operations, i, branchOp.getMbType());
        // Calculate offset
        //int offset = nextAddress - branchOp.getAddress();

        ConditionalExit cexit = new ConditionalExit(branchOp.getAddress(),
                compareOperation, nextAddress, delaySlots, 
                branchOp.getInputs().get(0).copy(), branchOp.getInputs().get(1).copy());


        operations.set(i, cexit);

      }

   }

   @Override
   public String toString() {
      return "ParseConditionalBranches";
   }

   /**
    * INSTANCE VARIABLES
    */
   private static final Map<InstructionName, ConditionalExit.Op> instructionProperties;
   static {
      Map<InstructionName, ConditionalExit.Op> aMap = new EnumMap<InstructionName, ConditionalExit.Op>(InstructionName.class);

      aMap.put(InstructionName.beq, equal);
      aMap.put(InstructionName.beqd, equal);
      aMap.put(InstructionName.beqi, equal);
      aMap.put(InstructionName.beqid, equal);

      aMap.put(InstructionName.bge, greaterOrEqual);
      aMap.put(InstructionName.bged, greaterOrEqual);
      aMap.put(InstructionName.bgei, greaterOrEqual);
      aMap.put(InstructionName.bgeid, greaterOrEqual);

      aMap.put(InstructionName.bgt, greater);
      aMap.put(InstructionName.bgtd, greater);
      aMap.put(InstructionName.bgti, greater);
      aMap.put(InstructionName.bgtid, greater);

      aMap.put(InstructionName.ble, lessOrEqual);
      aMap.put(InstructionName.bled, lessOrEqual);
      aMap.put(InstructionName.blei, lessOrEqual);
      aMap.put(InstructionName.bleid, lessOrEqual);

      aMap.put(InstructionName.blt, less);
      aMap.put(InstructionName.bltd, less);
      aMap.put(InstructionName.blti, less);
      aMap.put(InstructionName.bltid, less);

      aMap.put(InstructionName.bne, notEqual);
      aMap.put(InstructionName.bned, notEqual);
      aMap.put(InstructionName.bnei, notEqual);
      aMap.put(InstructionName.bneid, notEqual);


      instructionProperties = Collections.unmodifiableMap(aMap);
   }

}

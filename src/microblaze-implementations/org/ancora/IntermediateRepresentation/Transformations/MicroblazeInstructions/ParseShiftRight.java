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
import org.ancora.IntermediateRepresentation.Operations.ShiftRight;
import org.ancora.MicroBlaze.InstructionName;
import org.ancora.IntermediateRepresentation.MbTransformUtils;
import org.ancora.IntermediateRepresentation.Transformation;

/**
 *
 * @author Joao Bispo
 */
public class ParseShiftRight extends Transformation {

   @Override
   public String toString() {
      return "ParseShiftRight";
   }

   public void transform(List<Operation> operations) {
      for(int i=0; i<operations.size(); i++) {
         // Check if MicroBlaze operation
        MbOperation shiftOperation = MbOperation.getMbOperation(operations.get(i));
        if(shiftOperation == null) {
           continue;
        }

        // Get arithmetic operation
        ShiftProperties props = instructionProperties.get(shiftOperation.getMbType());
        if(props == null) {
           continue;
        }

        ShiftRight.Op op = props.operation;
        Operand input = shiftOperation.getInputs().get(0).copy();
        Operand output = shiftOperation.getOutputs().get(0).copy();
        Operand carryOut = MbTransformUtils.createCarryOperand();
        Operand carryIn;
        if(props.hasCarryIn) {
           carryIn = MbTransformUtils.createCarryOperand();
        } else {
           carryIn = null;
        }

        ShiftRight newOp = new ShiftRight(shiftOperation.getAddress(),
                ShiftRight.Op.shiftRightLogical, input, output, carryIn, carryOut);

        operations.set(i, newOp);
      }

   }

   /**
    * INNER CLASS
    */

   static class ShiftProperties {

      public ShiftProperties(ShiftRight.Op operation, boolean hasCarryIn) {
         this.operation = operation;
         this.hasCarryIn = hasCarryIn;
      }

      /**
       * INSTANCE VARIABLES
       */
      private ShiftRight.Op operation;
      private boolean hasCarryIn;
   }

   private static final Map<InstructionName, ShiftProperties> instructionProperties;
   static {
      Map<InstructionName, ShiftProperties> aMap = new EnumMap<InstructionName, ShiftProperties>(InstructionName.class);

      aMap.put(InstructionName.sra, new ShiftProperties(ShiftRight.Op.shiftRightArithmetic, false));
      aMap.put(InstructionName.src, new ShiftProperties(ShiftRight.Op.shiftRightWithCarry, true));
      aMap.put(InstructionName.srl, new ShiftProperties(ShiftRight.Op.shiftRightLogical, false));


      instructionProperties = Collections.unmodifiableMap(aMap);
   }

}

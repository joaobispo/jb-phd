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
import org.ancora.IntermediateRepresentation.Operation;
import org.ancora.IntermediateRepresentation.Operands.MbImm;
import org.ancora.IntermediateRepresentation.Operands.MbRegister;
import org.ancora.IntermediateRepresentation.Transformation;

/**
 *
 * @author Joao Bispo
 */
public class RegisterZeroToImm extends Transformation {

   @Override
   public String toString() {
      return "RegisterZeroToImm";
   }

   /**
    * Changes occurences of Register 0 to Immediate 0.
    *
    * <p>Changes input operations.
    * 
    * @param operations
    * @return
    */
   public void transform(List<Operation> operations) {

      for(int i=0; i<operations.size(); i++) {
         Operation operation = operations.get(i);

         // Check inputs
         List<Operand> inputs = operation.getInputs();
         for(int j=0; j<inputs.size(); j++) {
            Operand operand = inputs.get(j);

            Operand zeroImm = getImmZero(operand);

            if(zeroImm != null) {
               // Replace input
               operation.replaceInput(j, zeroImm);
            }
         }

         // Check outputs
         List<Operand> outputs = operation.getOutputs();
         for(int j=0; j<outputs.size(); j++) {
            Operand operand = outputs.get(j);

            Operand zeroImm = getImmZero(operand);

            if(zeroImm != null) {
               // Replace input
               operation.replaceOutput(j, zeroImm);
            }
         }
      }

   }


   private Operand getImmZero(Operand operand) {
      // Try to get reg value from operand
      Integer regValue = MbRegister.getRegValue(operand);
      // If regValue == null, this is not a MbRegister
      if (regValue == null) {
         return null;
      }

      // We want regValue == 0
      if (regValue != 0) {
         return null;
      }

      // Create an Immediate type with value 0
      return new MbImm(0, BITS_ZERO);
   }

   public static final int BITS_ZERO = 1;

}

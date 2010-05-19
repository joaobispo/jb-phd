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
import org.ancora.IntermediateRepresentation.Operands.Literal;
import org.ancora.IntermediateRepresentation.Operands.MbImm;
import org.ancora.IntermediateRepresentation.Transformation;

/**
 * Transforms MicroBlaze Immediate operands to Literal Operands
 *
 * @author Joao Bispo
 */
public class TransformImmToLiterals extends Transformation {
   
   @Override
   public String toString() {
      return "TransformImmToLiterals";
   }

   /**
    * Transforms MicroBlaze Immediate operands to General Literal Operands
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

            Literal literal = getLiteralFromImm(operand);

            if(literal != null) {
               // Replace input
               operation.replaceInput(j, literal);
            }
         }

         // Check outputs
         List<Operand> outputs = operation.getOutputs();
         for(int j=0; j<outputs.size(); j++) {
            Operand operand = outputs.get(j);

            Literal literal = getLiteralFromImm(operand);

            if(literal != null) {
               // Replace input
               operation.replaceOutput(j, literal);
            }
         }
      }

   }

   private Literal getLiteralFromImm(Operand operand) {
      // Check if it is a MicroBlaze immediate value
      Integer immValue = MbImm.getImmValue(operand);

      if(immValue == null) {
         return null;
      }

      Literal newLiteral = new Literal(Literal.LiteralType.integer, String.valueOf(immValue),
              operand.getBits());
      newLiteral.setPrefix(operand.getPrefix());
      return newLiteral;
   }


}

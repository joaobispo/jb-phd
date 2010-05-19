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

package org.ancora.IntermediateRepresentation.Transformations;

import java.util.List;
import org.ancora.IntermediateRepresentation.Operand;
import org.ancora.IntermediateRepresentation.Operation;
import org.ancora.IntermediateRepresentation.Operations.ArithmeticWithCarry;
import org.ancora.IntermediateRepresentation.Operations.Nop;
import org.ancora.IntermediateRepresentation.OperationType;
import org.ancora.IntermediateRepresentation.Transformation;
import org.ancora.IntermediateRepresentation.Transformations.Utils.SubstituteTable;

/**
 *
 * @author Joao Bispo
 */
public class ResolveNeutralInput extends Transformation {

   @Override
   public String toString() {
      return "Resolve Neutral Input";
   }



   public void transform(List<Operation> operations) {
      SubstituteTable resolvedOperandsMap = new SubstituteTable();
      for(int i=0; i<operations.size(); i++) {
         Operation operation = operations.get(i);
         resolvedOperandsMap.processOperation(operation);
         // Resolve Operation
         List<Operand> resolvedOperands = resolveOperation(operation);

         if(resolvedOperands == null) {
            continue;
         }
         
         // Add operands to the table
         resolvedOperandsMap.updateOutputs(operation, resolvedOperands);
         
         // Remove instruction if it has not side-effects
         if(!operation.hasSideEffects()) {
            operations.set(i, new Nop(operation));
         }

         updateStats(operation);
      }

   }

   /**
    * TODO: Add more transformations
    * @param operation
    * @return
    */
   private List<Operand> resolveOperation(Operation operation) {
      switch((OperationType)operation.getType()) {
         case IntegerArithmeticWithCarry:
            return ((ArithmeticWithCarry)operation).resolveNeutralInput();
         default:
            return null;
      }
   }
   
}

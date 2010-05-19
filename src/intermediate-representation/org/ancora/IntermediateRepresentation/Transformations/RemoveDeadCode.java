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
import java.util.logging.Logger;
import org.ancora.IntermediateRepresentation.Operand;
import org.ancora.IntermediateRepresentation.Operation;
import org.ancora.IntermediateRepresentation.Operations.Nop;
import org.ancora.IntermediateRepresentation.OperationType;
import org.ancora.IntermediateRepresentation.Operations.Logic;
import org.ancora.IntermediateRepresentation.Operations.UnconditionalExit;
import org.ancora.IntermediateRepresentation.Transformation;

/**
 *
 * @author Joao Bispo
 */
public class RemoveDeadCode extends Transformation {


   @Override
   public String toString() {
      return "Remove Dead Code";
   }



   public void transform(List<Operation> operations) {

      for(int i=0; i<operations.size(); i++) {
         Operation operation = operations.get(i);

         // Check if it not a not already
         if(operation.getType() == OperationType.Nop) {
            continue;
         }

         // Check if operation has side-effects
         if(operation.hasSideEffects()) {
            continue;
         }

         boolean hasMutableOutputs = hasMutableOutputs(operation);

         if(hasMutableOutputs) {
            continue;
         }

         // If it has not side-effect and its outputs are not mutable,
         // remove instruction.
         operations.set(i, new Nop(operation));

         // Check if it is an operation other than OR
         if (operation.getType() == OperationType.Logic) {
            if (((Logic) operation).getOperation() != Logic.Op.or) {
               Logger.getLogger(RemoveDeadCode.class.getName()).
                       warning("Removing logic operation which is not OR:"
                       + ((Logic) operation).getOperation());
            }
         } else {
            Logger.getLogger(RemoveDeadCode.class.getName()).
                    warning("Removing operation which is not logic:"
                    + ((Logic) operation).getOperation());
         }

         updateStats(operation);
      }
   }

   private boolean isDeadBranch(Operation operation) {
      switch ((OperationType) operation.getType()) {
         case UnconditionalExit:
            return ((UnconditionalExit)operation).isDeadBranch();
         default:
            return false;
      }
   }

   private boolean hasMutableOutputs(Operation operation) {
      // Check if it has no outputs
      if(operation.getOutputs().isEmpty()) {
         return false;
      }

      // Check if the outputs are all immutable
      for(Operand output : operation.getOutputs()) {
         if(!output.isImmutable()) {
            return true;
         }
      }

      return false;
   }
   
}

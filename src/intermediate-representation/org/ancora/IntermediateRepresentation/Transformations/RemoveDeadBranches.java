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
import org.ancora.IntermediateRepresentation.Operation;
import org.ancora.IntermediateRepresentation.Operations.Nop;
import org.ancora.IntermediateRepresentation.OperationType;
import org.ancora.IntermediateRepresentation.Operations.UnconditionalExit;
import org.ancora.IntermediateRepresentation.Transformation;

/**
 *
 * @author Joao Bispo
 */
public class RemoveDeadBranches extends Transformation {


   @Override
   public String toString() {
      return "Remove Dead Branches";
   }



   public void transform(List<Operation> operations) {

      for(int i=0; i<operations.size(); i++) {
         Operation operation = operations.get(i);

         boolean isDeadBranch = isDeadBranch(operation);

         if(!isDeadBranch) {
            continue;
         }

         // If is dead branch, remove instruction.
         operations.set(i, new Nop(operation));


         updateStats(operation);
      }
   }

   private boolean isDeadBranch(Operation operation) {
      switch ((OperationType) operation.getType()) {
         case UnconditionalExit:
            System.err.println("Not working now");
//            return ((UnconditionalExit)operation).isDeadBranch();
         default:
            return false;
      }
   }
   
}

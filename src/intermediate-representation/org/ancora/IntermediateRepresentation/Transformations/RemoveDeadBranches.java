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

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.ancora.IntermediateRepresentation.Operand;
import org.ancora.IntermediateRepresentation.Operands.Literal;
import org.ancora.IntermediateRepresentation.Operation;
import org.ancora.IntermediateRepresentation.Operations.ArithmeticWithCarry;
import org.ancora.IntermediateRepresentation.Operations.Nop;
import org.ancora.IntermediateRepresentation.OperationType;
import org.ancora.IntermediateRepresentation.Operations.UnconditionalExit;
import org.ancora.IntermediateRepresentation.Transformation;
import org.ancora.IntermediateRepresentation.Transformations.Utils.SubstituteTable;

/**
 *
 * @author Joao Bispo
 */
public class RemoveDeadBranches implements Transformation {

   public RemoveDeadBranches() {
      stats = new EnumMap<OperationType, Integer>(OperationType.class);
   }



   @Override
   public String toString() {
      return "Remove Dead Branches";
   }



   public List<Operation> transform(List<Operation> operations) {


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

      return operations;
   }

   private boolean isDeadBranch(Operation operation) {
      switch ((OperationType) operation.getType()) {
         case UnconditionalExit:
            return ((UnconditionalExit)operation).isDeadBranch();
         default:
            return false;
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
            return ((ArithmeticWithCarry)operation).resolveLiterals();
            //return resolveIntegerArithmeticWithCarry((ArithmeticWithCarry)operation);
            //return ArithmeticWithCarry.resolve((ArithmeticWithCarry)operation);
            //return ArithmeticWithCarry.resolveNeutral((ArithmeticWithCarry)operation);
         case UnconditionalExit:
            return resolveUnconditionalExit((UnconditionalExit)operation);
         default:
            return null;
      }
   }

   

   private List<Operand> resolveUnconditionalExit(UnconditionalExit unconditionalExit) {
      // Check if it performs linking
      if(unconditionalExit.getOutput1() == null) {
         return null;
      }
      
      List<Operand> operands = new ArrayList<Operand>();
      int value = unconditionalExit.getLinkingAddress();
      int bits = unconditionalExit.getOutput1().getBits();
      Literal literal = new Literal(Literal.LiteralType.integer, Integer.toString(value), bits);
      
      operands.add(literal);
      return operands;
   }

   public Map<OperationType, Integer> getStats() {
      return stats;
   }

   private void updateStats(Operation operation) {
      // Add operation to table
      Integer value = stats.get((OperationType) operation.getType());
      if (value == null) {
         value = 0;
      }
      value++;
      stats.put((OperationType) operation.getType(), value);
   }

   /**
    * INSTANCE VARIABLES
    */
    private Map<OperationType, Integer> stats;




}

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.ancora.IntermediateRepresentation.Operand;
import org.ancora.IntermediateRepresentation.Operands.Literal;
import org.ancora.IntermediateRepresentation.Operation;
import org.ancora.IntermediateRepresentation.Operations.ArithmeticWithCarry;
import org.ancora.IntermediateRepresentation.Operations.Nop;
import org.ancora.IntermediateRepresentation.OperationType;
import org.ancora.IntermediateRepresentation.Operations.UnconditionalExit;
import org.ancora.IntermediateRepresentation.Transformation;

/**
 *
 * @author Joao Bispo
 */
public class PropagateConstants implements Transformation {

   public PropagateConstants() {
      stats = new EnumMap<OperationType, Integer>(OperationType.class);
   }



   @Override
   public String toString() {
      return "Propagate Constants";
   }



   public List<Operation> transform(List<Operation> operations) {
      Map<String, Operand> resolvedOperandsMap = new HashMap<String, Operand>();

      for(int i=0; i<operations.size(); i++) {
         Operation operation = operations.get(i);
         substituteResolvedOperands(operation, resolvedOperandsMap);

         // Resolve Operation
         List<Operand> resolvedOperands = resolveOperation(operation);

         if(resolvedOperands == null) {
            continue;
         }

         // Check if list is the same size as outputs of operation
         int opOutSize = operation.getOutputs().size();
         int resolvedSize = resolvedOperands.size();
         if(opOutSize != resolvedSize) {
            Logger.getLogger(PropagateConstants.class.getName()).
                    warning("Size of resolved operands ("+resolvedSize+") mismatches " +
                    "size of output operands ("+opOutSize+") after resolving operation '"+
                    operation.getType()+"'");
         }

         // Add operands to the table
         for(int j=0; j<resolvedOperands.size(); j++) {
            //Literal literal = resolvedOperands.get(j);
            Operand resolvedOperand = resolvedOperands.get(j);
            //resolvedOperandsMap.put(operation.getOutputs().get(j).toString(), Literal.getInteger(literal));
            resolvedOperandsMap.put(operation.getOutputs().get(j).toString(), resolvedOperand);
         }
         
         // Remove instruction if it has not side-effects
         if(!operation.hasSideEffects()) {
            operations.set(i, new Nop(operation));
         }

         updateStats(operation);
      }

      return operations;
   }

   private void substituteResolvedOperands(Operation operation,
           Map<String, Operand> resolvedOperands) {

      // Check inputs, if an input matches an element from the table,
      //substitute it for a literal
      List<Operand> operands = operation.getInputs();
      for(int i=0; i<operands.size(); i++) {
         //Integer literalValue = resolvedOperands.get(operands.get(i).toString());
         Operand resolvedOperand = resolvedOperands.get(operands.get(i).toString());
         //if(literalValue != null) {
         if(resolvedOperand == null) {
            continue;
         }

//         int bits = operands.get(i).getBits();
//         Literal newLiteral = new Literal(Literal.LiteralType.integer,
//                 literalValue.toString(), bits);

         operation.replaceInput(i, resolvedOperand.copy());
         
      }

      // Check if outputs matches an element from the table. In that case,
      // remove element from the table.
      List<Operand> outputs = operation.getOutputs();
      for(int i=0; i<outputs.size(); i++) {

         String key = outputs.get(i).toString();
         // TODO: CHOICE, REMOVE OR INSERT NULL?
         resolvedOperands.remove(key);
         //resolvedOperands.put(key, null);

         //Integer literalValue = resolvedOperands.get(key);
         
         //if(literalValue != null) {
         //   Integer previousValue = resolvedOperands.remove(key);
            //System.out.println("Removed key '"+key+"' with value "+previousValue+".");
         //}
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
            //return resolveIntegerArithmeticWithCarry((ArithmeticWithCarry)operation);
            return ArithmeticWithCarry.resolve((ArithmeticWithCarry)operation);
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

   private boolean areImmutable(List<Operand> operands) {
      for(Operand operand : operands) {
         if(operand != null) {
            if(!operand.isImmutable()) {
               return false;
            }
         }
      }

      return true;
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

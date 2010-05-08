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
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import org.ancora.IntermediateRepresentation.Operand;
import org.ancora.IntermediateRepresentation.Operands.Literal;
import org.ancora.IntermediateRepresentation.Operation;
import org.ancora.IntermediateRepresentation.Operations.ArithmeticWithCarry;
import org.ancora.IntermediateRepresentation.Operations.Nop;
import org.ancora.IntermediateRepresentation.OperationType;
import org.ancora.IntermediateRepresentation.Transformation;

/**
 *
 * @author Joao Bispo
 */
public class PropagateConstants implements Transformation {

   @Override
   public String toString() {
      return "Propagate Constants";
   }



   public List<Operation> transform(List<Operation> operations) {
      Map<String, Integer> literalRegisters = new Hashtable<String, Integer>();

      for(int i=0; i<operations.size(); i++) {
         Operation operation = operations.get(i);
         substituteRegisterForLiterals(operation, literalRegisters);

         // Resolve Operation
         List<Literal> outputs = resolveOperation(operation);

         if(outputs == null) {
            continue;
         }

         // Add literals to the table
         for(int j=0; j<outputs.size(); j++) {
            Literal literal = outputs.get(j);
            literalRegisters.put(operation.getOutputs().get(j).toString(), Literal.getInteger(literal));
         }

         
         // Remove instruction
         operations.set(i, new Nop(operation));
      }

      return operations;
   }

   private void substituteRegisterForLiterals(Operation operation,
           Map<String, Integer> literalRegisters) {

      // Check inputs, if an input matches an element from the table,
      //substitute it for a literal
      List<Operand> operands = operation.getInputs();
      for(int i=0; i<operands.size(); i++) {
         Integer literalValue = literalRegisters.get(operands.get(i).toString());
         if(literalValue != null) {
            int bits = operands.get(i).getBits();
            //System.out.println("Register substituted by Literal because of Constant Propagation. Address "+operation.getAddress());
            //System.out.println("Before:"+operation.getInputs());
            Literal newLiteral = new Literal(Literal.LiteralType.integer,
                    literalValue.toString(), bits);
            //operands.set(i, newLiteral);
            operation.replaceInput(i, newLiteral);
            //System.out.println("After:"+operation.getInputs());
         }
      }

      // Check if outputs matches an element from the table. In that case,
      // remove element from the table.
      List<Operand> outputs = operation.getOutputs();
      for(int i=0; i<outputs.size(); i++) {

         String key = outputs.get(i).toString();
         Integer literalValue = literalRegisters.get(key);
         if (operation.getAddress() == 2228) {
            //System.out.println("ENTERED");
            //System.out.println("Key:" + key);
            //System.out.println("Table:" + literalRegisters);
         }

         if(literalValue != null) {
            Integer previousValue = literalRegisters.remove(key);
            //System.out.println("Removed key '"+key+"' with value "+previousValue+".");
         }
      }
   }

   /**
    * TODO: Add more transformations
    * @param operation
    * @return
    */
   private List<Literal> resolveOperation(Operation operation) {
      switch((OperationType)operation.getType()) {
         case IntegerArithmeticWithCarry:
            return resolveIntegerArithmeticWithCarry((ArithmeticWithCarry)operation);

         default:
            return null;
      }
   }

   private List<Literal> resolveIntegerArithmeticWithCarry(ArithmeticWithCarry arithmeticWithCarry) {
      List<Literal> literals = new ArrayList<Literal>();

      // Calculate value
      Integer resultValue = arithmeticWithCarry.resolveOutput();
      if (resultValue == null) {
         return null;
      }
      Literal resultLiteral = new Literal(Literal.LiteralType.integer,
              resultValue.toString(), arithmeticWithCarry.getOutput().getBits());

      literals.add(resultLiteral);


      // Check if it has carry out
      Integer carryOutValue = arithmeticWithCarry.resolveCarryOut();
      if (carryOutValue == null) {
         return literals;
      }
      Literal resultCarry = new Literal(Literal.LiteralType.integer,
              carryOutValue.toString(), arithmeticWithCarry.getCarryOut().getBits());

      literals.add(resultCarry);



      return literals;
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

    
}

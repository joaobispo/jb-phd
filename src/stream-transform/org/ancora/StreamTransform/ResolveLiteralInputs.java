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

package org.ancora.StreamTransform;

import java.util.List;
import java.util.logging.Logger;
import org.ancora.IntermediateRepresentation.Operand;
import org.ancora.IntermediateRepresentation.Operation;
import org.ancora.IntermediateRepresentation.Operations.Nop;
import org.ancora.StreamTransform.DataStructures.SubstituteTable;

/**
 *
 * @author Joao Bispo
 */
public class ResolveLiteralInputs extends StreamTransformation  {

   @Override
   public String toString() {
      return getName();
   }

   public ResolveLiteralInputs() {
      resolvedOperandsMap = new SubstituteTable();
   }



/*
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
 *
 */

   /**
    * TODO: Add more transformations
    * @param operation
    * @return
    */
   /*
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
    *
    */

   
/*
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
 * 
 */

   @Override
   public Operation transform(Operation operation) {
      // Substitute current inputs for possible already found literals
      resolvedOperandsMap.substituteInputs(operation);
      // Unregister outputs of current operation
      resolvedOperandsMap.unregisterOutputs(operation);

         // Resolve Operation
         List<Operand> resolvedOperands = operation.resolveWhenLiteralInputs();

         // Cannot resolve operation. No changes.
         if(resolvedOperands == null) {
            return operation;
         }

         // Current operation can be resolved;
         // Stats
         operationFrequency.addOperation(operation);

         // Update table with the equivalent values for the current outputs
         resolvedOperandsMap.registerOutputs(operation.getOutputs(), resolvedOperands);

         // Since the result of the operation are literals, it can be safely
         // removed.
         if(operation.hasSideEffects()) {
            Logger.getLogger(ResolveLiteralInputs.class.getName()).
                    warning("Removing operation ('"+operation.getType()+"') which " +
                    "has side effects!");
         }

         //System.err.println("Operation Resolved:"+operation.getFullOperation());
         //System.err.println(resolvedOperandsMap);

         return new Nop(operation);
   }

   @Override
   public String getName() {
      return NAME;
   }

   /**
    * INSTANCE VARIABLES
    */
    private SubstituteTable resolvedOperandsMap;

   public final static String NAME = "Resolve Literal Inputs";

}

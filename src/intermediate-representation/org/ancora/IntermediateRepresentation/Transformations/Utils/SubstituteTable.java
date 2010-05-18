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

package org.ancora.IntermediateRepresentation.Transformations.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.ancora.IntermediateRepresentation.Operand;
import org.ancora.IntermediateRepresentation.Operation;

/**
 *
 * @author Joao Bispo
 */
public class SubstituteTable {

   public SubstituteTable() {
      resolvedOperandsMap = new HashMap<String, Operand>();
   }

   /**
    * Substitutes operand inputs of the operation using information collected
    * so far. Automatically updates the table using information about the outputs
    * of the given operation.
    *
    * @param operation
    */
   public void processOperation(Operation operation) {
      // Check inputs, if an input matches an element from the table,
      //substitute it for the corresponding operand
      List<Operand> inputs = operation.getInputs();
      for(int i=0; i<inputs.size(); i++) {
         // Get operand for the corresponding input
         Operand resolvedOperand = resolvedOperandsMap.get(inputs.get(i).toString());
         if(resolvedOperand == null) {
            continue;
         }

         // Replace operand, if found.
         //System.err.println("Replaced Operand: "+operation.getInputs().get(i)+" for "+resolvedOperand);
         operation.replaceInput(i, resolvedOperand.copy());
      }

      // Check if outputs matches an element from the table. In that case,
      // remove element from the table.
      List<Operand> outputs = operation.getOutputs();
      for(int i=0; i<outputs.size(); i++) {

         String key = outputs.get(i).toString();
         // TODO: CHOICE, REMOVE OR INSERT NULL?
         resolvedOperandsMap.remove(key);
      }
   }

   /**
    * 
    * @param operation
    * @param outputOperands
    */
   public void updateOutputs(Operation operation, List<Operand> outputOperands) {
      // Check if list is the same size as outputs of operation
      int originalSize = operation.getOutputs().size();
      int newOutputSize = outputOperands.size();
      if (originalSize != newOutputSize) {
         Logger.getLogger(SubstituteTable.class.getName()).
                 warning("Size of resolved operands (" + newOutputSize + ") mismatches "
                 + "size of output operands (" + originalSize + ") after resolving operation '"
                 + operation.getType() + "'");
         return;
      }

      for (int j = 0; j < outputOperands.size(); j++) {
         Operand newOutput = outputOperands.get(j);
         resolvedOperandsMap.put(operation.getOutputs().get(j).toString(), newOutput);
      }
   }

   /**
    * INSTANCE VARIABLES
    */
   Map<String, Operand> resolvedOperandsMap;
}

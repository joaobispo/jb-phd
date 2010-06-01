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
      lastVersionOnTable = new HashMap<String, String>();
   }

   /**
    * Substitutes operand inputs of the operation using information collected
    * so far. Automatically updates the table using information about the outputs
    * of the given operation.
    *
    * @param operation
    */
   public void processOperation(Operation operation) {
 //              System.err.println(operation.getFullOperation());

      // Check inputs, if an input matches an element from the table,
      //substitute it for the corresponding operand
      List<Operand> inputs = operation.getInputs();
      for(int i=0; i<inputs.size(); i++) {


         // Get operand for the corresponding input
         Operand resolvedOperand = resolvedOperandsMap.get(inputs.get(i).toString());
         if(resolvedOperand == null) {
            continue;
         }

         // Check if input corresponds to last version on table
         Operand input = inputs.get(i);
         String register = getRegister(input.toString());
         String lastVersion = lastVersionOnTable.get(register);
         String thisVersion = getVersion(input.toString());
         if(!lastVersion.equals(thisVersion)) {
            System.err.println("Last version is '"+lastVersion+"'; want version '"+thisVersion+"'");
         }

         // Replace operand, if found.
         //System.err.println("Replaced Operand: "+operation.getInputs().get(i)+" for "+resolvedOperand);
         operation.replaceInput(i, resolvedOperand.copy());
      }

      // Check if outputs matches an element from the table. In that case,
      // remove element from the table.
      // In SSA format, this is not needed.
      List<Operand> outputs = operation.getOutputs();
      for(int i=0; i<outputs.size(); i++) {

         String key = outputs.get(i).toString();
         // TODO: CHOICE, REMOVE OR INSERT NULL?
         if(resolvedOperandsMap.containsKey(key)) {
            resolvedOperandsMap.remove(key);
            System.err.println("Removing. Key:"+key);
            //System.err.println(operation.getFullOperation());
         }
         
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

         // Get version of operand to be substituted
         String operandName = operation.getOutputs().get(j).toString();
         String version = getVersion(operandName);
         String register = getRegister(operandName);
         lastVersionOnTable.put(register, version);
      }
   }

   // TODO
   // Place where these names are formed should be in a single class
   private String getRegister(String operandName) {
      int separatorIndex = operandName.indexOf(".");
      return operandName.substring(0, separatorIndex);
   }

   private String getVersion(String operandName) {
      int separatorIndex = operandName.indexOf(".");
      return operandName.substring(separatorIndex+1);
   }

   /**
    * INSTANCE VARIABLES
    */
   Map<String, Operand> resolvedOperandsMap;

   // Exp
   Map<String, String> lastVersionOnTable;
}

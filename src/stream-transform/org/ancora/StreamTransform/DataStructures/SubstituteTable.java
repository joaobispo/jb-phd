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

package org.ancora.StreamTransform.DataStructures;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.ancora.IntermediateRepresentation.Operand;
import org.ancora.IntermediateRepresentation.OperandType;
import org.ancora.IntermediateRepresentation.Operation;
import org.ancora.IntermediateRepresentation.Ssa;

/**
 * Contains information about what operands can be substituted by which other
 * operands.
 *
 * <p> The table only stores InternalData operands and assumes they are in SSA
 * format.
 *
 * @author Joao Bispo
 */
public class SubstituteTable {

   public SubstituteTable() {
      equivalentOperandsMap = new HashMap<String, Operand>();
      versionOnTable = new HashMap<String, Integer>();
   }

   /**
    * Substitutes InternalData input operands. If an input operand is found in
    * the table, substitutes it for the corresponding mapping in the table.
    * 
    * @param operation
    */
   public void substituteInputs(Operation operation) {

      List<Operand> inputs = operation.getInputs();
      for(int i=0; i<inputs.size(); i++) {
         Operand input = inputs.get(i);
         // Check if internaldata
         if(input.getType() != OperandType.internalData) {
            continue;
         }

         // Check if there is a mapping for this operand
         //System.err.println("Getting original name for "+input.getName());
         String registerName = Ssa.getOriginalName(input.getName());
         Operand equivalentOperand = equivalentOperandsMap.get(registerName);
         if(equivalentOperand == null) {
            continue;
         }

         versionCheck(input.getName());

         // Replace operand
         operation.replaceInput(i, equivalentOperand.copy());
      }

   }

   /**
    * Removes from the table any references to the outputs of the given operation.
    *
    * <p> An operand is removed from the table by inserting a 'null' as its value.
    * When checking if there is a mapping between an operand name and another operand,
    * instead of 'containsKey', one should get the operand associated with the key
    * and test for null.
    *
    * @param operation
    */
   public void unregisterOutputs(Operation operation) {
      List<Operand> outputs = operation.getOutputs();
      for(int i=0; i<outputs.size(); i++) {
         Operand output = outputs.get(i);

         String registerName = Ssa.getOriginalName(output.getName());
         if(equivalentOperandsMap.containsKey(registerName)) {
            equivalentOperandsMap.put(registerName, null);
         }
         
      }
   }

   /**
    * Substitutes operand inputs of the operation using information collected
    * so far. Automatically updates the table using information about the outputs
    * of the given operation.
    *
    * @param operation
    */
   /*
   public void processOperation(Operation operation) {

      // Check inputs, if an input matches an element from the table,
      //substitute it for the corresponding operand
      List<Operand> inputs = operation.getInputs();
      for(int i=0; i<inputs.size(); i++) {


         // Get operand for the corresponding input
         Operand resolvedOperand = equivalentOperandsMap.get(inputs.get(i).toString());
         if(resolvedOperand == null) {
            continue;
         }

         // Check if input corresponds to last version on table
         Operand input = inputs.get(i);
         String register = getRegister(input.toString());
         String lastVersion = versionOnTable.get(register);
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
         if(equivalentOperandsMap.containsKey(key)) {
            equivalentOperandsMap.remove(key);
            System.err.println("Removing. Key:"+key);
            //System.err.println(operation.getFullOperation());
         }
         
      }
   }
    *
    */

   /**
    * Includes in the table a mapping between the given originalOutputs and
    * the equivalentOutputs.
    * 
    * @param originalOutputs
    * @param equivalentOutputs
    */
   public void registerOutputs(List<Operand> originalOutputs, List<Operand> equivalentOutputs) {
      // Check if they are the same size
      if(originalOutputs.size() != equivalentOutputs.size()) {
         Logger.getLogger(SubstituteTable.class.getName()).
                 warning("Size of original operands (" + originalOutputs.size() +
                 ") mismatches size of equivalent operands (" + equivalentOutputs.size() + ")");
         return;
      }

      for (int i = 0; i < originalOutputs.size(); i++) {
         Operand originalOutput = originalOutputs.get(i);
         Operand equivalentOutput = equivalentOutputs.get(i);

         String registerName = Ssa.getOriginalName(originalOutput.getName());
         equivalentOperandsMap.put(registerName, equivalentOutput);

         // Check if operand is InternalData
         if(originalOutputs.get(i).getType() != OperandType.internalData) {
            Logger.getLogger(SubstituteTable.class.getName()).
                    warning("Adding an operand of type "+originalOutputs.get(i).getType()+
                    " to SubstitutleTable.");
         }

         // Update check table
         int version = Ssa.getVersion(originalOutput.getName());
         //int version = Ssa.getVersion(registerName);
         versionOnTable.put(registerName, version);
      }
      
   }

   /**
    * 
    * @param operation
    * @param outputOperands
    */
   /*
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
         equivalentOperandsMap.put(operation.getOutputs().get(j).toString(), newOutput);

         // Get version of operand to be substituted
         String operandName = operation.getOutputs().get(j).toString();
         String version = getVersion(operandName);
         String register = getRegister(operandName);
         versionOnTable.put(register, version);
      }
   }
    *
    */


   /**
    * Checks if the given input name version corresponds to the version put on
    * the table.
    *
    * @param name
    */
   private boolean versionCheck(String inputName) {
      String registerName = Ssa.getOriginalName(inputName);
      int tableVersion = versionOnTable.get(registerName);
      int inputVersion = Ssa.getVersion(inputName);

      if (tableVersion != inputVersion) {
         Logger.getLogger(SubstituteTable.class.getName()).
                 warning("SSA version mismatch: version on table is '" + tableVersion + "'; input version is '" + inputVersion + "'");
         return false;
      }

      return true;
   }

   @Override
   public String toString() {
      return equivalentOperandsMap.toString();
   }



   /**
    * INSTANCE VARIABLES
    */
   Map<String, Operand> equivalentOperandsMap;

   // Check
   Map<String, Integer> versionOnTable;

}

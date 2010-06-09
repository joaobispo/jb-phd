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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.ancora.IntermediateRepresentation.Operand;
import org.ancora.IntermediateRepresentation.Operands.InternalData;
import org.ancora.IntermediateRepresentation.OperandType;
import org.ancora.IntermediateRepresentation.Operation;
import org.ancora.IntermediateRepresentation.Ssa;

/**
 * Changes the name of mutable operands, giving them versions.
 *
 * @author Joao Bispo
 */
public class SingleStaticAssignment extends StreamTransformation {

   public SingleStaticAssignment() {
      versionTable = new HashMap<String, Integer>();
   }

   @Override
   public String getName() {
      return NAME;
   }

   @Override
   public String toString() {
      return getName();
   }


   @Override
   public Operation transform(Operation operation) {

      processInputs(operation);
      processOutputs(operation);
// TODO: It would be interesting to have stats about the max number of versions.

      return operation;
   }

   private void processInputs(Operation operation) {
         List<Operand> inputs = operation.getInputs();

         for(int j=0; j<inputs.size(); j++) {
            Operand input = inputs.get(j);

            // SSA is only over internal data. Check if a mutable kind of
            // data as went undetected
            if(input.getType() != OperandType.internalData) {
               if(!input.isImmutable()) {
                  Logger.getLogger(SingleStaticAssignment.class.getName()).
                          warning("Mutable input of type '"+input.getType()+"' bypassed SSA transformation.");
               }
               continue;
            }

            InternalData iData = (InternalData)input;

            // Get version
            Integer version = versionTable.get(iData.getName());
            if(version == null) {
               version = 0;
               versionTable.put(iData.getName(), version);
            }

            // Get SSA name
            String ssaName = Ssa.buildSsaName(iData.getName(), version);
            // New InternalData
            InternalData newData = new InternalData(ssaName, iData.getBits());
            newData.setPrefix(iData.getPrefix());
            
            // Substitute
            inputs.set(j, newData);
         }
   }


   private void processOutputs(Operation operation) {
      // Update table and outputs
      List<Operand> outputs = operation.getOutputs();
      for (int j = 0; j < outputs.size(); j++) {

         Operand output = outputs.get(j);
         if (output.getType() != OperandType.internalData) {
            if (!output.isImmutable()) {
               Logger.getLogger(SingleStaticAssignment.class.getName()).
                       warning("Mutable output of type '" + output.getType() + "' bypassed SSA transformation.");
            }
            continue;
         }

         InternalData iData = (InternalData) output;
         String registerName = iData.getName();

         // Update version
         Integer version = versionTable.get(registerName);
         if (version == null) {
            version = 0;
         }
         version++;
         versionTable.put(registerName, version);

         // New name
         String ssaName = Ssa.buildSsaName(registerName, version);
         // New InternalData
         InternalData newData = new InternalData(ssaName, iData.getBits());
         // Substitute
         outputs.set(j, newData);
      }
   }

   /**
    *
    * @param operations
    */
   public static void transform(List<Operation> operations) {
      // Create SSA StreamTransform
      StreamTransformation ssaTransf = new SingleStaticAssignment();
      for(Operation operation : operations) {
         ssaTransf.transform(operation);
      }
   }

   /**
    * INSTANCE VARIABLES
    */

   /**
    * Register Name -> Version
    * Ex.: "r2" -> "3"
    */
   private Map<String, Integer> versionTable;

   public static final String NAME = "Single Static Assignment";



}

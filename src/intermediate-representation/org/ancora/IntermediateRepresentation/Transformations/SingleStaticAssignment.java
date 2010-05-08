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

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import org.ancora.IntermediateRepresentation.Operand;
import org.ancora.IntermediateRepresentation.Operands.InternalData;
import org.ancora.IntermediateRepresentation.OperandType;
import org.ancora.IntermediateRepresentation.Operation;
import org.ancora.IntermediateRepresentation.Transformation;

/**
 * Changes the name of muttable operands, giving them versions.
 *
 * @author Joao Bispo
 */
public class SingleStaticAssignment implements Transformation {

   @Override
   public String toString() {
      return "Single Static Assignment";
   }



   public List<Operation> transform(List<Operation> operations) {
      //List<Operation> newList = new ArrayList<Operation>();

      Map<String, Integer> variablesVersion = new Hashtable<String, Integer>();
//      Map<String, InternalData> operandsTable = new Hashtable<String, InternalData>();
//      Operation start = new Control(-1, Control.Op.start);
//      newList.add(start);

      for(int i=0; i<operations.size(); i++) {
         Operation operation = operations.get(i);
//System.out.println("Operation Before:"+operation.getFullOperation());
         // Update inputs
         List<Operand> inputs = operation.getInputs();
//         System.out.println("Inputs Before:"+inputs);
         for(int j=0; j<inputs.size(); j++) {
            Operand input = inputs.get(j);
            if(input.getType() != OperandType.internalData) {
//               System.out.println("Type:"+input.getType());
               continue;
            }

            InternalData iData = (InternalData)input;

            // Get version
            Integer version = variablesVersion.get(iData.getName());
            if(version == null) {
               version = 0;
               variablesVersion.put(iData.getName(), version);
            }

            // New name
            String newName = iData.getName() + "." + version;
            // New InternalData
            InternalData newData = new InternalData(newName, iData.getBits());
            // Substitute
            inputs.set(j, newData);
//            System.out.println("Old data:"+iData);
//            System.out.println("New data:"+newData);
         }
//         System.out.println("Inputs After:"+inputs);


         // Update table and outputs
         List<Operand> outputs = operation.getOutputs();
         for(int j=0; j<outputs.size(); j++) {
             Operand output = outputs.get(j);
             if(output.getType() != OperandType.internalData) {
               continue;
            }

            InternalData iData = (InternalData)output;
            // Update version
            Integer version = variablesVersion.get(iData.getName());
            if(version == null) {
               version = 0;
            }
            version++;
            variablesVersion.put(iData.getName(), version);

            // New name
            String newName = iData.getName() + "." + version;
            // New InternalData
            InternalData newData = new InternalData(newName, iData.getBits());
            // Substitute
            outputs.set(j, newData);
            
         }
//         System.out.println("Operation After:"+operation.getFullOperation());
         operations.set(i, operation);
      }

      return operations;
   }

}

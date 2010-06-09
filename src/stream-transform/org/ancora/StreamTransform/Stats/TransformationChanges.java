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

package org.ancora.StreamTransform.Stats;

import java.util.HashMap;
import java.util.Map;
import org.ancora.IntermediateRepresentation.OperationType;

/**
 * Keeps tracks of the totals for the StreamTransformations OperationFrequency.
 *
 * @author Joao Bispo
 */
public class TransformationChanges {

   public TransformationChanges() {
      operationFrequency = new HashMap<String, OperationFrequency>();
   }

   public Map<String, OperationFrequency> getTotalFrequencies() {
      return operationFrequency;
   }

   public void addOperationFrequency(String transformationName, OperationFrequency frequencies) {
      OperationFrequency currentFrequencies = this.operationFrequency.get(transformationName);

      if(currentFrequencies == null) {
         currentFrequencies = new OperationFrequency();
      }
      currentFrequencies.addOperations(frequencies);

      operationFrequency.put(transformationName, currentFrequencies);
   }

   public void addOperationFrequency(TransformationChanges anotherTotal) {
      Map<String, OperationFrequency> partialAcc = anotherTotal.getTotalFrequencies();
      for(String key : partialAcc.keySet()) {
         OperationFrequency partialFrequency = partialAcc.get(key);
         addOperationFrequency(key, partialFrequency);
      }
   }

   public String buildStatsString() {
      StringBuilder builder = new StringBuilder();
      for (String key : operationFrequency.keySet()) {
         Map<OperationType, Integer> frequencies = operationFrequency.get(key).getFrequencyTable();

         if (frequencies.size() > 0) {
            builder.append("Transformation '");
            builder.append(key);
            builder.append(":\n");

            builder.append(frequencies.toString());
            builder.append("\n");
         } else {
            builder.append("No operations removed by '");
            builder.append(key);
            builder.append("'\n");
         }
      }

      return builder.toString();
   }

   @Override
   public String toString() {
      return buildStatsString();
   }



   /**
    * INSTANCE VARIABLES
    */
   private Map<String, OperationFrequency> operationFrequency;


}

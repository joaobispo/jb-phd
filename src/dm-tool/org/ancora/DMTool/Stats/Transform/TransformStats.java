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

package org.ancora.DMTool.Stats.Transform;

import java.util.EnumMap;
import java.util.Map;
import org.ancora.IntermediateRepresentation.OperationType;
import org.ancora.IntermediateRepresentation.Transformation;
import org.ancora.IntermediateRepresentation.Transformations.PropagateConstants;

/**
 *
 * @author Joao Bispo
 */
public class TransformStats {

   public TransformStats() {
       //constantPropStats = new EnumMap<OperationType, Integer>(OperationType.class);
   }

   /*
   public void collectStats(Transformation t) {
      // Check classes
      if(t.getClass() == PropagateConstants.class) {
         collectPropagateConstants((PropagateConstants)t);
         return;
      }
   }
    *
    */

   public void showStats(Transformation t) {
     // Check classes
      if(t.getClass() == PropagateConstants.class) {
         showPropagateConstants((PropagateConstants)t);
         return;
      }
   }

   private void showPropagateConstants(PropagateConstants propagateConstants) {
      System.err.println("Constant Propagation Transformation:");
      System.err.println(propagateConstants.getStats());
   }

   /**
    * INSTANCE VARIABLES
    */
    //private Map<OperationType, Integer> constantPropStats;





}

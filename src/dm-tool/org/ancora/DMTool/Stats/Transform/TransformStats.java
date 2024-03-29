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

import java.util.Map;
import org.ancora.IntermediateRepresentation.OperationType;
import org.ancora.IntermediateRepresentation.Transformation;

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

      if (t.getStats().size() > 0) {
         System.err.println("Transformation '" + t.toString() + "':");
         System.err.println(t.getStats());
      } else {
         System.err.println("No operations removed by '" + t.toString() + "'");
      }
     // Check classes
      /*
      if(t.getClass() == ResolveLiteralInputs.class) {
         showStats(((ResolveLiteralInputs)t).toString(), ((ResolveLiteralInputs)t).getStats());
         return;
      }

      if(t.getClass() == ResolveNeutralInput.class) {
         showStats(((ResolveNeutralInput)t).toString(), ((ResolveNeutralInput)t).getStats());
         return;
      }

      if(t.getClass() == RemoveDeadBranches.class) {
         showStats(((RemoveDeadBranches)t).toString(), ((RemoveDeadBranches)t).getStats());
         return;
      }
*/

   }

   /*
   private void showTransfWithStats(ResolveLiteralInputs propagateConstants) {
      System.err.println("'"+propagateConstants.toString()+"' Transformation:");
      System.err.println(propagateConstants.getStats());
   }

   private void showStats(Map<OperationType, Integer> stats) {
      throw new UnsupportedOperationException("Not yet implemented");
   }
   */

   private void showStats(String transformName, Map<OperationType, Integer> stats) {
      System.err.println("Transformation '"+transformName+"':");
      System.err.println(stats);
   }

   /**
    * INSTANCE VARIABLES
    */
    //private Map<OperationType, Integer> constantPropStats;





}

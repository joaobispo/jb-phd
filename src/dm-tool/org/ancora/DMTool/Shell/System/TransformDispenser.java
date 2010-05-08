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

package org.ancora.DMTool.Shell.System;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.ancora.SharedLibrary.Preferences.EnumPreferences;
import org.ancora.IntermediateRepresentation.Transformations.PropagateConstants;
import org.ancora.IntermediateRepresentation.Transformations.RemoveInternalLoads;
import org.ancora.IntermediateRepresentation.Transformations.SingleStaticAssignment;
import org.ancora.IntermediateRepresentation.Transformation;

/**
 *
 * @author Joao Bispo
 */
public class TransformDispenser {

   public static Transformation[] getCurrentTransformations() {

      String transformOptions = prefs.getPreference(GeneralPreferences.transformOptions).toLowerCase();
      String separator = " ";
      // Split transformations
      String[] transformations = transformOptions.split(separator);

      List<Integer> indexes = new ArrayList<Integer>();
      for(int i=0; i<transformations.length; i++) {
         String transformation = transformations[i];
         if(transfOptions.containsKey(transformation)) {
            indexes.add(i);
         } else {
            Logger.getLogger(TransformDispenser.class.getName()).
                    info("Could not find transformation '"+transformation+"'.");
         }
      }

      // Build return array
      Transformation[] transf = new Transformation[indexes.size()];
      for(int i=0; i<indexes.size(); i++) {
         transf[i] = transfOptions.get(transformations[indexes.get(i)]);
      }

      return transf;

   }

   /**
    * VARIABLES
    */
   private static final EnumPreferences prefs = GeneralPreferences.getPreferences();
   //private static final InstructionFilter MICROBLAZE_JUMP_FILTER = new MbJumpFilter();
   
   private static final Map<String, Transformation> transfOptions;
   static {
      Map<String, Transformation> aMap =
              new Hashtable<String, Transformation>();

      aMap.put(Options.PropagateConstants.toLowerCase(), new PropagateConstants());
      aMap.put(Options.SingleStaticAssignment.toLowerCase(), new SingleStaticAssignment());
      aMap.put(Options.RemoveInternalLoads.toLowerCase(), new RemoveInternalLoads());


      transfOptions = Collections.unmodifiableMap(aMap);
   }


   /**
    * ENUM
    */
   public enum TransformOption {
      PropagateConstants,
      SingleStaticAssignment,
      RemoveInternalLoads;
   }

   public interface Options {
      String PropagateConstants = "ConstantPropagation";
      String SingleStaticAssignment = "SSA";
      String RemoveInternalLoads = "spilling-loads";
   }
}

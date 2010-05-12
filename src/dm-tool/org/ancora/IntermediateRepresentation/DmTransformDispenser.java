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

package org.ancora.IntermediateRepresentation;

import org.ancora.DMTool.deprecated.Preference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.ancora.DMTool.Settings.Options;
import org.ancora.DMTool.Settings.Options.OptionName;
import org.ancora.DMTool.Utils.ShellUtils;
import org.ancora.SharedLibrary.Preferences.EnumPreferences;
import org.ancora.IntermediateRepresentation.Transformations.PropagateConstants;
import org.ancora.IntermediateRepresentation.Transformations.RemoveInternalLoads;
import org.ancora.IntermediateRepresentation.Transformations.SingleStaticAssignment;
import org.ancora.IntermediateRepresentation.Transformation;
import org.ancora.Shared.EnumUtilsAppend;

/**
 *
 * @author Joao Bispo
 */
public class DmTransformDispenser {

   public static List<Transformation> getCurrentTransformations() {
      
      // Get transformations
      String transformationString = Options.optionsTable.get(OptionName.ir_options);
      List<String> transformationList = ShellUtils.splitCommand(transformationString);

      List<Transformation> transfs = new ArrayList<Transformation>();
      for(String transf : transformationList) {
         // Get enum
         TransformationName transformation = transformations.get(transf);
         if(transformation == null) {
            Logger.getLogger(DmTransformDispenser.class.getName()).
                    info("Could not find transformation '"+transf+"'.");
            continue;
         }

         transfs.add(transformation.getTransformation());
      }

      return transfs;
/*
      String mapperName = Options.optionsTable.get(OptionName.mapping_mapper);

      String transformOptions = prefs.getPreference(Preference.transformOptions).toLowerCase();
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
*/
   }

   /**
    * VARIABLES
    */
   //private static final EnumPreferences prefs = Preference.getPreferences();
   //private static final InstructionFilter MICROBLAZE_JUMP_FILTER = new MbJumpFilter();
   /*
   private static final Map<String, Transformation> transfOptions;
   static {
      Map<String, Transformation> aMap =
              new Hashtable<String, Transformation>();

      aMap.put(Options.PropagateConstants.toLowerCase(), new PropagateConstants());
      aMap.put(Options.SingleStaticAssignment.toLowerCase(), new SingleStaticAssignment());
      aMap.put(Options.RemoveInternalLoads.toLowerCase(), new RemoveInternalLoads());


      transfOptions = Collections.unmodifiableMap(aMap);
   }
*/

   /**
    * ENUM
    */
   /*
   public enum TransformOption {
      PropagateConstants,
      SingleStaticAssignment,
      RemoveInternalLoads;
   }
*/
   /*
   public interface Options {
      String PropagateConstants = "ConstantPropagation";
      String SingleStaticAssignment = "SSA";
      String RemoveInternalLoads = "spilling-loads";
   }
    *
    */

   public static Map<String, TransformationName> transformations =
           EnumUtilsAppend.buildMap(TransformationName.values());


   /**
    * TRANSFORMATION
    */
   public static enum TransformationName {

      PropagateConstants("constant-propagation"),
      RemoveInternalLoads("remove-loads");

      private TransformationName(String transformationName) {
         this.transformationName = transformationName;
      }

      @Override
      public String toString() {
         return transformationName;
      }

      public String getTransformationName() {
         return transformationName;
      }


      public Transformation getTransformation() {
         switch (this) {
            case PropagateConstants:
               return new PropagateConstants();
            case RemoveInternalLoads:
               return new RemoveInternalLoads();
            default:
               Logger.getLogger(DmTransformDispenser.class.getName()).
                       warning("Case not defined: '" + this);
               return null;
         }
      }
      private String transformationName;
   }
}

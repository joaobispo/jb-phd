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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.ancora.DMTool.Settings.Options;
import org.ancora.DMTool.Settings.Options.OptionName;
import org.ancora.DMTool.Utils.ShellUtils;
import org.ancora.Shared.EnumUtilsAppend;

/**
 *
 * @author Joao Bispo
 */
public class DmStreamTransformDispenser {

   public static List<StreamTransformation> getCurrentTransformations() {
      
      // Get transformations
      String transformationString = Options.optionsTable.get(OptionName.ir_options);
      List<String> transformationList = ShellUtils.splitCommand(transformationString);

      List<StreamTransformation> transfs = new ArrayList<StreamTransformation>();

      // Add obligatory transformations
      // NO - we want to map it before the transformations, for comparison.
      //transfs.add(TransformationName.getParseTransformation());

      for(String transf : transformationList) {
         // Get enum
         TransformationName transformation = transformations.get(transf);
         if(transformation == null) {
            Logger.getLogger(DmStreamTransformDispenser.class.getName()).
                    info("Could not find transformation '"+transf+"'.");
            continue;
         }

         transfs.add(transformation.getTransformation());
      }

      return transfs;

   }

   public static Map<String, TransformationName> transformations =
           EnumUtilsAppend.buildMap(TransformationName.values());


   /**
    * TRANSFORMATIONS
    */
   public static enum TransformationName {
      dummy("dummy");
/*
      ResolveLiteralInputs("resolve-inputs-lit"),
      ResolveNeutralInputs("resolve-inputs-neutral"),
      RemoveInternalLoads("remove-redundant-loads"),
      RemoveDeadBranches("remove-dead-branches"),
      RemoveDeadCode("remove-dead-code");
*/
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

      public static StreamTransformation getParseTransformation() {
         return new SingleStaticAssignment();
      }
      public StreamTransformation getTransformation() {
         switch (this) {
            /*
            case ResolveLiteralInputs:
               return new ResolveLiteralInputs();
            case ResolveNeutralInputs:
               return new ResolveNeutralInput();
            case RemoveInternalLoads:
               //return new RemoveInternalLoadsOld();
               return new RemoveInternalLoads2();
            case RemoveDeadBranches:
               return new RemoveDeadBranches();
            case RemoveDeadCode:
               return new RemoveDeadCode();
             *
             */
            default:
               Logger.getLogger(DmStreamTransformDispenser.class.getName()).
                       warning("Case not defined: '" + this);
               return null;
         }
      }
      private String transformationName;
   }
}

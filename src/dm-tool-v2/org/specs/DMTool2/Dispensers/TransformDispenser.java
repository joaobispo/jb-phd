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

package org.specs.DMTool2.Dispensers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.ancora.IntermediateRepresentation.Operation;
import org.ancora.SharedLibrary.EnumUtils;
import org.ancora.StreamTransform.MemTest;
import org.ancora.StreamTransform.RemoveInternalLoads2;
import org.ancora.StreamTransform.RemoveR0Or;
import org.ancora.StreamTransform.ResolveLiteralInputs;
import org.ancora.StreamTransform.Stats.TransformationChanges;
import org.ancora.StreamTransform.StreamTransformation;
import org.specs.DMTool2.CommandParser;
import org.specs.DMTool2.Settings.Option;
import org.specs.DMTool2.Settings.Settings;

/**
 *
 * @author Joao Bispo
 */
public class TransformDispenser {

   public static List<StreamTransformation> getCurrentTransformations() {
      return getTransformations(TransformationSet.set_current);
   }


   public static List<StreamTransformation> getTransformations(TransformationSet transfSet) {
      
      // Get transformations
      String transformationString = Settings.optionsTable.get(transfSet.getOption());
      //String transformationString = Options.optionsTable.get(OptionName.stream_transformations);
      List<String> transformationList = CommandParser.splitCommand(transformationString);

      List<StreamTransformation> transfs = new ArrayList<StreamTransformation>();

      for(String transf : transformationList) {
         // Get enum
         TransformationName transformation = transformations.get(transf);
         if(transformation == null) {
            Logger.getLogger(TransformDispenser.class.getName()).
                    warning("Could not find transformation '"+transf+"'.");
            continue;
         }

         transfs.add(transformation.getTransformation());
      }

      return transfs;

   }

   public static TransformationChanges applyCurrentTransformations(List<Operation> operations) {
      return applyTransformations(operations, TransformationSet.set_current);
   }

   public static TransformationChanges applyPreTransformations(List<Operation> operations) {
      return applyTransformations(operations, TransformationSet.set_pre);
   }
   
   private static TransformationChanges applyTransformations(List<Operation> operations, TransformationSet transSet) {
      TransformationChanges totalFrequencies = new TransformationChanges();

      //List<StreamTransformation> transf = DmStreamTransformDispenser.getCurrentTransformations();
      List<StreamTransformation> transf = TransformDispenser.getTransformations(transSet);
      for (StreamTransformation t : transf) {
         for (int i = 0; i < operations.size(); i++) {
            operations.set(i, t.transform(operations.get(i)));
         }
         totalFrequencies.addOperationFrequency(t.getName(), t.getOperationFrequency());
         if(t.getName().equals((new MemTest()).getName())) {
            System.err.println("Loads:");
            System.err.println(((MemTest)t).getMemAddrLoads());
            System.err.println("Stores:");
            System.err.println(((MemTest)t).getMemAddrStores());
         }
      }

      return totalFrequencies;
   }



   public static Map<String, TransformationName> transformations =
           EnumUtils.buildMap(TransformationName.values());


   /**
    * TRANSFORMATIONS
    */
   public static enum TransformationName {
      removeR0Or("remove-or"),
      resolveWhenInputLiterals("resolve-inputs"),
      memTest("mem-test"),
      spill("spill");

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
       
      
      public StreamTransformation getTransformation() {
         switch (this) {
            case removeR0Or:
               return new RemoveR0Or();
            case resolveWhenInputLiterals:
               return new ResolveLiteralInputs();
            case memTest:
               return new MemTest();
            case spill:
               return new RemoveInternalLoads2();

            default:
               Logger.getLogger(TransformDispenser.class.getName()).
                       warning("Case not defined: '" + this);
               return null;
         }
      }
      private String transformationName;
   }

      public static enum TransformationSet {
      set_pre(TransformOption.pre),
      set_current(TransformOption.current);

      private TransformationSet(Option option) {
         this.option = option;
      }

      public Option getOption() {
         return option;
      }

      private final Option option;
   }
}

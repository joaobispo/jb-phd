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

package org.ancora.DMTool.Shell;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.ancora.DMTool.Shell.System.Executable;
import org.ancora.DMTool.Shell.System.GeneralPreferences;
import org.ancora.DMTool.Utils.EnumUtils;
import org.ancora.SharedLibrary.Preferences.EnumPreferences;

/**
 *
 * @author Joao Bispo
 */
public class Set implements Executable {

   public Set() {
      prefs = GeneralPreferences.getPreferences();
   }



   public boolean execute(List<String> args) {
      if(args.size() < 2) {
         Logger.getLogger(Set.class.getName()).
                 info("Too few arguments for 'set' ("+args.size()+"). Minimum is 2.");
         return false;
      }

      // Get preference
      String prefString = args.get(0).toLowerCase();
      //GeneralPreferences prefEnum = EnumUtils.valueOf(GeneralPreferences.class, prefString);
      GeneralPreferences prefEnum = settings.get(prefString);

      if(prefEnum == null) {
          Logger.getLogger(Set.class.getName()).
                  info("'"+args.get(0)+"' is not a valid setting. Avaliable:");
         //logger.info("");
         for(String gPref : keys) {
             Logger.getLogger(Set.class.getName()).
                     info("- "+gPref);
         }
         /*
         for(GeneralPreferences gPref : GeneralPreferences.values()) {
             Logger.getLogger(Set.class.getName()).
                     info("- "+gPref.name());
         }
          */
         return false;
      }

      // Get value

      String value = args.get(1);
      // Special case: transform-options
      if(prefEnum == GeneralPreferences.transformOptions) {
         StringBuilder builder = new StringBuilder();
         builder.append(value);
         for(int i=2; i<args.size(); i++) {
            builder.append(" ");
            builder.append(args.get(i));
         }
         value = builder.toString();
      }


      // Introduce value
      return updatePreferences(prefEnum, value);

      // Update preferences
      //prefs.putPreference(prefEnum, value);

      //return true;
   }

   private boolean updatePreferences(GeneralPreferences preference, String value) {

      // TODO: PARSE VALUES?
     prefs.putPreference(preference, value);
     return true;
   }

   private EnumPreferences prefs;

   /**
    * Options names
    */
    private static final Map<String, GeneralPreferences> settings;
   static {
      Map<String, GeneralPreferences> aMap = new Hashtable<String, GeneralPreferences>();

      aMap.put(Options.outputFolder.toLowerCase(), GeneralPreferences.outputFolder);
      aMap.put(Options.partitioner.toLowerCase(), GeneralPreferences.partitioner);

      aMap.put(Options.megablockMaxPatternSize.toLowerCase(), GeneralPreferences.megablockMaxPatternSize);

      aMap.put(Options.extensionBlock.toLowerCase(), GeneralPreferences.blockExtension);
      aMap.put(Options.extensionElf.toLowerCase(), GeneralPreferences.elfExtension);
      aMap.put(Options.extensionTrace.toLowerCase(), GeneralPreferences.traceExtension);

      aMap.put(Options.busSelectorThreshold.toLowerCase(), GeneralPreferences.selectorThreshold);
      aMap.put(Options.busUseGatherer.toLowerCase(), GeneralPreferences.useGatherer);
      aMap.put(Options.busUseSelector.toLowerCase(), GeneralPreferences.useSelector);

      aMap.put(Options.mapper.toLowerCase(), GeneralPreferences.mapper);
      aMap.put(Options.transformOptions.toLowerCase(), GeneralPreferences.transformOptions);
      aMap.put(Options.transformWriteDot.toLowerCase(), GeneralPreferences.transformWriteDot);

      settings = Collections.unmodifiableMap(aMap);
   }

   private static final List<String> keys;
   static {
      List<String> aSet = new ArrayList<String>();

      aSet.add(Options.outputFolder);
      aSet.add(Options.partitioner);
      aSet.add(Options.mapper);
      aSet.add(Options.megablockMaxPatternSize);
      aSet.add(Options.extensionBlock);
      aSet.add(Options.extensionElf);
      aSet.add(Options.extensionTrace);
      aSet.add(Options.busSelectorThreshold);
      aSet.add(Options.busUseGatherer);
      aSet.add(Options.busUseSelector);
      aSet.add(Options.transformWriteDot);
      aSet.add(Options.transformOptions);

      keys = Collections.unmodifiableList(aSet);
   }
   public interface Options {
      String outputFolder = "outputFolder";
      String partitioner = "partitioner";
      String mapper = "mapper";

      String megablockMaxPatternSize = "megablock-maxPatternSize";

      String extensionBlock = "extension-block";
      String extensionTrace = "extension-trace";
      String extensionElf = "extension-elf";

      String busSelectorThreshold = "bus-selectorThreshold";
      String busUseGatherer = "bus-useGatherer";
      String busUseSelector = "bus-useSelector";

      String transformWriteDot = "transform-writeDot";
      String transformOptions = "transform-options";

   }

}

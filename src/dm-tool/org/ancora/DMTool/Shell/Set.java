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
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.ancora.DMTool.Settings.Options;
import org.ancora.DMTool.Settings.Options.OptionName;
import org.ancora.DMTool.Shell.System.Executable;
import org.ancora.DMTool.deprecated.Preference;
import org.ancora.DMTool.Shell.Shell.Command;
import org.ancora.DMTool.Utils.ShellUtils;
import org.ancora.Shared.EnumUtilsAppend;
import org.ancora.SharedLibrary.Preferences.EnumPreferences;

/**
 *
 * @author Joao Bispo
 */
public class Set implements Executable {

   public Set() {
      optionsValues = Options.optionsTable;
      optionsNames = EnumUtilsAppend.buildMap(OptionName.values());
      //prefs = Preference.getPreferences();
   }



   public boolean execute(List<String> args) {
      if(args.size() < 2) {
         Logger.getLogger(Set.class.getName()).
                 info("Too few arguments for 'set' ("+args.size()+"). Minimum is 2.");
         return false;
      }

      // Get option
      OptionName optionName = optionsNames.get(args.get(0));

      // Get preference
      //String prefString = args.get(0).toLowerCase();
      //Preference prefEnum = settings.get(prefString);

      //if(prefEnum == null) {
      if(optionName == null) {
          Logger.getLogger(Set.class.getName()).
                  info("'"+args.get(0)+"' is not a valid setting. Type "+
                  Command.help+" "+Help.HelpArgument.setoptions+" to see avaliable settings.");
          /*
         for(String gPref : keys) {
             Logger.getLogger(Set.class.getName()).
                     info("- "+gPref);
         }
           *
           */
         return false;
      }

      String value = args.get(1);
      // Concatenate remainging arguments into a single string
      if(args.size() > 2) {
         StringBuilder builder = new StringBuilder();
         builder.append(value);
         for(int i=1; i<args.size(); i++) {
            builder.append(ShellUtils.COMMAND_SEPARATOR);
            builder.append(args.get(i));
         }
         value = builder.toString();
      }

      // Introduce value
      optionsValues.put(optionName, value);
      return true;
      //return updatePreferences(prefEnum, value);

   }

   /*
   private boolean updatePreferences(Preference preference, String value) {

      // TODO: PARSE VALUES?
     prefs.putPreference(preference, value);
     return true;
   }
    *
    */

   Map<OptionName, String> optionsValues;
   Map<String, OptionName> optionsNames;
/*
   private EnumPreferences prefs;

   ///
    // Options names
    //
    private static final Map<String, Preference> settings;
   static {
      Map<String, Preference> aMap = new Hashtable<String, Preference>();

      aMap.put(Options.outputFolder.toLowerCase(), Preference.outputFolder);
      aMap.put(Options.partitioner.toLowerCase(), Preference.partitioner);

      aMap.put(Options.megablockMaxPatternSize.toLowerCase(), Preference.megablockMaxPatternSize);

      aMap.put(Options.extensionBlock.toLowerCase(), Preference.blockExtension);
      aMap.put(Options.extensionElf.toLowerCase(), Preference.elfExtension);
      aMap.put(Options.extensionTrace.toLowerCase(), Preference.traceExtension);

      aMap.put(Options.busSelectorThreshold.toLowerCase(), Preference.selectorThreshold);
      aMap.put(Options.busUseGatherer.toLowerCase(), Preference.useGatherer);
      aMap.put(Options.busUseSelector.toLowerCase(), Preference.useSelector);

      aMap.put(Options.mapper.toLowerCase(), Preference.mapper);
      aMap.put(Options.transformOptions.toLowerCase(), Preference.transformOptions);
      aMap.put(Options.transformWriteDot.toLowerCase(), Preference.transformWriteDot);

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
*/
}

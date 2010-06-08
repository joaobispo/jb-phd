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

import org.ancora.DMTool.System.Interfaces.Executable;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.ancora.DMTool.Settings.Options;
import org.ancora.DMTool.Settings.Options.OptionName;

/**
 *
 * @author Joao Bispo
 */
public class ShowOptions implements Executable {

   public ShowOptions() {
      logger = Logger.getLogger(ShowOptions.class.getName());
      //prefs = Preference.getPreferences();
   }



   public boolean execute(List<String> arguments) {

      //logger.info("Current values for program options:");
      logger.info("\nCurrent settings:");

      Map<OptionName, String> optionsTable = Options.optionsTable;

      for(OptionName option : OptionName.values()) {
         String value = optionsTable.get(option);
         String message = option.getOptionName() + " = '" + value+"'";
         logger.info(message);
      }
      /*
      for(Preference gPref : Preference.values()) {
         String value = prefs.getPreference(gPref);
         String message = gPref.name() + " - " + value;
         logger.info(message);
      }
       *
       */

      return true;
   }

   /**
    * INSTANCE VARIABLES
    */
   private Logger logger;
   //private EnumPreferences prefs;
}

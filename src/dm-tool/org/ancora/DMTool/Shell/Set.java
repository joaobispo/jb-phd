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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ancora.DMTool.Settings.Options;
import org.ancora.DMTool.Settings.Options.OptionName;
import org.ancora.DMTool.Settings.Settings;
import org.ancora.DMTool.Shell.Shell.Command;
import org.ancora.DMTool.System.Services.ShellUtils;
import org.ancora.SharedLibrary.EnumUtils;
import org.ancora.SharedLibrary.LoggingUtils;

/**
 *
 * @author Joao Bispo
 */
public class Set implements Executable {

   public Set() {
      optionsValues = Options.optionsTable;
      optionsNames = EnumUtils.buildMap(OptionName.values());
   }



   public boolean execute(List<String> args) {
      int minimumArgs = 1;
      if(args.size() < minimumArgs) {
         Logger.getLogger(Set.class.getName()).
                 warning("Too few arguments for 'set' ("+args.size()+"). Minimum is "+
                 minimumArgs+".");
         return false;
      }

      // Get option
      OptionName optionName = optionsNames.get(args.get(0));


      if(optionName == null) {
          Logger.getLogger(Set.class.getName()).
                  warning("'"+args.get(0)+"' is not a valid setting. Type "+
                  Command.help+" "+Help.HelpArgument.setoptions+" to see avaliable settings.");
         
         return false;
      }

      String value = null;
      if(args.size() == 1) {
         value = "";
      } else {
         value = args.get(1);
      }
      
      // Concatenate remainging arguments into a single string
      if(args.size() > 2) {
         StringBuilder builder = new StringBuilder();
         builder.append(value);
         for(int i=2; i<args.size(); i++) {
            builder.append(ShellUtils.COMMAND_SEPARATOR);
            builder.append(args.get(i));
         }
         value = builder.toString();
      }

      // Introduce value
      optionsValues.put(optionName, value);

      // Special cases
      specialCases(optionName);

      return true;

   }


   Map<OptionName, String> optionsValues;
   Map<String, OptionName> optionsNames;

   private void specialCases(OptionName optionName) {
      // If loggerlevel, reset logger
      if(optionName.equals(OptionName.general_loggerlevel)) {
         Level level = Settings.getLoggerLevel();
         LoggingUtils.setLevel(level);
      }
   }

}

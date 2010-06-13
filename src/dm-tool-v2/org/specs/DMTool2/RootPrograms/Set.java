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

package org.specs.DMTool2.RootPrograms;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ancora.SharedLibrary.EnumUtils;
import org.ancora.SharedLibrary.LoggingUtils;
import org.specs.DMTool2.CommandParser;
import org.specs.DMTool2.Dispensers.PartitionerOption;
import org.specs.DMTool2.Program;
import org.specs.DMTool2.Settings.GeneralOption;
import org.specs.DMTool2.Settings.Option;
import org.specs.DMTool2.Settings.OptionTable;
import org.specs.DMTool2.Settings.ProgramName;
import org.specs.DMTool2.Settings.Settings;
import org.specs.DMTool2.TraceCoverage.TraceCoverageOption;

/**
 *
 * @author Joao Bispo
 */
public class Set implements Program {

   public Set() {
      optionsTable = Settings.optionsTable;
      // Put here all the options which should be used
      options = new HashMap<String, Option>();
      options.putAll(EnumUtils.buildMap(GeneralOption.values()));
      options.putAll(EnumUtils.buildMap(PartitionerOption.values()));
      options.putAll(EnumUtils.buildMap(TraceCoverageOption.values()));
      
   }

   public boolean isDead() {
      // Is always alive.
      return false;
   }

   public ProgramName getProgramName() {
      return ProgramName.set;
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
      Option option = options.get(args.get(0));

      if(option == null) {
          Logger.getLogger(Set.class.getName()).
                  warning("'"+args.get(0)+"' is not a valid setting.");
                  //warning("'"+args.get(0)+"' is not a valid setting. Type "+
                  //Command.help+" "+Help.HelpArgument.setoptions+" to see avaliable settings.");
         
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
            builder.append(CommandParser.COMMAND_SEPARATOR);
            builder.append(args.get(i));
         }
         value = builder.toString();
      }

      // Introduce value
      optionsTable.putOption(option, value);

      // Special cases
      specialCases(option);

      return true;

   }


   OptionTable optionsTable;
   Map<String, Option> options;

   private void specialCases(Option option) {
      // If loggerlevel, reset logger
      String optionName = OptionTable.buildOptionName(option);
      String loggerOpName = OptionTable.buildOptionName(GeneralOption.logger_level);
      if(optionName.equals(loggerOpName)) {
         Level level = Settings.getLoggerLevel();
         LoggingUtils.setLevel(level);
      }
   }



}

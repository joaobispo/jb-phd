/*
 *  Copyright 2010 SPECS Research Group.
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

package org.specs.DMTool2.Settings;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Joao Bispo
 */
public class OptionTable {

   public OptionTable() {
      optionsTable = new HashMap<String, String>();
   }


   public String get(Option option) {
      // Build option name
      String optionName = buildOptionName(option);

      // Check if option is already on the table
      String optionValue = optionsTable.get(optionName);
      if(optionValue != null) {
         return optionValue;
      }

      // Get default value and store on table
      optionValue = option.getDefaultValue();
      optionsTable.put(optionName, optionValue);

      return optionValue;
   }

   public String putOption(Option option, String value) {
      // Build option name
      String optionName = buildOptionName(option);
      return optionsTable.put(optionName, value);
   }


   public static String buildOptionName(Option option) {
      return option.getPrefix() + Option.SEPARATOR + option.getOptionName();
   }

   /**
    * INSTANCE VARIABLES
    */
   private Map<String, String> optionsTable;

   
}

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

/**
 *
 * @author Joao Bispo
 */
public enum GeneralOption implements Option{

   input_path("input",".\\"),
   output_path("output-folder",".\\"),
   csv_foldername("csv-foldername","csv"),
   csv_filename_prefix("csv-filename-prefix",""),
   elf_extension("elf-extention","elf"),
   trace_extension("trace-extention","trace"),
   block_extension("block-extention","block"),
   csv_extension("csv-extention","csv"),
   logger_level("logger-level","ALL");
   

   
   private GeneralOption(String optionName, String defaultValue) {
      this.optionName = optionName;
      this.defaultValue = defaultValue;
   }


   
   public String getPrefix() {
      return prefix;
   }

   public String getDefaultValue() {
      return defaultValue;
   }

   public String getOptionName() {
      return optionName;
   }

   public String getCompleteName() {
      return OptionTable.buildOptionName(this);
   }

   @Override
   public String toString() {
      return getCompleteName();
   }



   private final String optionName;
   private final String defaultValue;
   private static final String prefix = "general";
}

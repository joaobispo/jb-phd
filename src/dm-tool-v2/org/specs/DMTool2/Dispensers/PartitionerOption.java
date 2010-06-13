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

package org.specs.DMTool2.Dispensers;

import org.specs.DMTool2.Settings.Option;
import org.specs.DMTool2.Settings.OptionTable;

/**
 *
 * @author Joao Bispo
 */
public enum PartitionerOption implements Option {

   current_partitioner("current-partitioner",""),
   use_warp_branch_limit("use-warp-branch-limit","false"),
   megablock_max_pattern_size("megablock_max_pattern_size", "32"),
   group_blocks("group-blocks","true"),
   filter_by_repetitions("filter-by-repetitions","false"),
   filter_identical("filter-identical","false"),
   repetition_threshold("repetition-threshold","2");


   private PartitionerOption(String optionName, String defaultValue) {
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
   private static final String prefix = "partition";

}

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

package org.ancora.DMTool.Settings;

import java.util.HashMap;
import java.util.Map;

/**
 * Options supported by the program.
 *
 * @author Joao Bispo
 */
public class Options {
      public static final String general_outputfolder = "general.outputfolder";

      public static final String partition_partitioner = "partition.partitioner";
      public static final String partition_repetitionthreshold = "partition.repetitionthreshold";
      public static final String partition_groupblocks = "partition.groupblocks";
      public static final String partition_filterbyrepetitions = "partition.filterbyrepetitions";
      public static final String partition_megablockmaxpatternsize = "partition.megablockmaxpatternsize";

      public static final String extension_block = "extension.block";
      public static final String extension_trace = "extension.trace";
      public static final String extension_elf = "extension.elf";

      public static final String mapping_mapper = "mapping.mapper";

      public static final String ir_writedot = "ir.writedot";
      public static final String ir_options = "ir.options";


   public static final Map<String, String> optionsTable;

   static {
      Map<String, String> aMap = new HashMap<String, String>();

      aMap.put(Options.partition_repetitionthreshold, "2");
      aMap.put(Options.partition_groupblocks, "true");
      aMap.put(Options.partition_filterbyrepetitions, "false");
      aMap.put(Options.partition_partitioner, "BasicBlock");
      aMap.put(Options.partition_megablockmaxpatternsize, "32");

      aMap.put(Options.general_outputfolder, "");

      aMap.put(Options.extension_block, "block");
      aMap.put(Options.extension_elf, "elf");
      aMap.put(Options.extension_trace, "trace");

      aMap.put(Options.mapping_mapper, "");

      aMap.put(Options.ir_options, "");
      aMap.put(Options.ir_writedot, "false");

      optionsTable = aMap;
   }
}

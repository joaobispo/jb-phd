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

import java.util.EnumMap;
import java.util.Map;
import org.ancora.DMTool.Dispensers.DmPartitionerDispenser;

/**
 * Options supported by the program.
 *
 * @author Joao Bispo
 */
public class Options {

   /**
    * OPTIONS NAMES
    */
   public static enum OptionName {

       general_outputfolder("general.outputfolder",""),
       general_input("general.input",""),
       general_loggerlevel("general.loggerlevel","ALL"),
       general_csvfilebasename("general.csvfilebasename","csvfile"),
       general_optimizationstring("general.optimizationstring",""),

      partition_partitioner("partition.partitioner",DmPartitionerDispenser.PartitionerName.MbBasicBlock.getDmPartitionerName()),
      partition_repetitionthreshold("partition.repetitionthreshold","2"),
      partition_groupblocks("partition.groupblocks","true"),
      partition_filterbyrepetitions("partition.filterbyrepetitions","false"),
      partition_filteridenticalblocks("partition.filteridenticalblocks","false"),
      partition_megablockmaxpatternsize("partition.megablockmaxpatternsize","32"),
      partition_warpusebranchlimit("partition.warpusebranchlimit","true"),
      //partition_daprofuseoriginalidmethod("partition.daprofuseoriginalidmethod","true"),

      extension_block("extension.block","block"),
      extension_trace("extension.trace","trace"),
      extension_elf("extension.elf","elf"),
      extension_dot("extension.dot","dotty"),
      extension_csv("extension.csv","csv"),

      mapping_mapper("mapping.mapper",""),
      mapping_stream_mapper("mapping.streammapper",""),
      mapping_max_comm_distance("mapping.maxcommdistance","0"),
      mapping_max_col_general("mapping.maxcolgeneral","-1"),
      mapping_max_col_memory("mapping.maxcolmemory","1"),

      ir_writedot("ir.writedot","false"),
      ir_options("ir.options","");

//      blocks_writeblocks("blocks.writeblock", "true");

      private OptionName(String optionName, String defaultValue) {
         this.optionName = optionName;
         this.defaultValue = defaultValue;
      }

      @Override
      public String toString() {
         return optionName;
      }

      public String getDefaultValue() {
         return defaultValue;
      }

      public String getOptionName() {
         return optionName;
      }


      private final String optionName;
      private final String defaultValue;
   }    


   /**
    * OPTIONS TABLE
    */
   public static final Map<OptionName, String> optionsTable;
   static {
      Map<OptionName, String> aMap = new EnumMap<OptionName, String>(OptionName.class);

      for (OptionName name : OptionName.values()) {
         aMap.put(name, name.getDefaultValue());
      }

      optionsTable = aMap;
   }
}

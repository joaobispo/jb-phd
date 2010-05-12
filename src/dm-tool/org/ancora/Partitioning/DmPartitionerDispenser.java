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

package org.ancora.Partitioning;

import java.util.Map;
import java.util.logging.Logger;
import org.ancora.DMTool.Settings.Options;
import org.ancora.DMTool.Settings.Options.OptionName;
import org.ancora.Shared.EnumUtilsAppend;
import org.ancora.SharedLibrary.ParseUtils;

/**
 * Uses the current settings to determine which is the appropriate Partitioner.
 *
 * @author Joao Bispo
 */
public class DmPartitionerDispenser {

   public static Partitioner getCurrentPartitioner() {
      //Get name of the current partitioner
      String partitionerName = Options.optionsTable.get(OptionName.partition_partitioner);
      //String partitionerName = Options.optionsTable.get(Options.partition_partitioner);

      // Get the correspondent enum
      PartitionerName partitioner = partitioners.get(partitionerName);

      if(partitioner == null) {
         Logger.getLogger(DmPartitionerDispenser.class.getName()).
                 info("Partitioner '"+partitionerName+"' not found.");
         return null;
      }

      return partitioner.getPartitioner();
   }

   /**
    * PARTITIONERS
    */
   public static enum PartitionerName {

      MbBasicBlock("mb-basicblock"),
      MbSuperBlock("mb-superblock"),
      MbMegaBlock("mb-megablock");

      private PartitionerName(String partitionerName) {
         this.partitionerName = partitionerName;
      }

      @Override
      public String toString() {
         return partitionerName;
      }

      public String getPartitionerName() {
         return partitionerName;
      }
      

      public Partitioner getPartitioner() {
         switch (this) {
            case MbBasicBlock:
               return MbPartitionerDispenser.getMbBasicBlock();
            case MbSuperBlock:
               return MbPartitionerDispenser.getMbSuperBlock();
            case MbMegaBlock:
               MegaBlock mb = MbPartitionerDispenser.getMbMegaBlock();
               // Get max pattern size
               String maxPatternString = Options.optionsTable.get(OptionName.partition_megablockmaxpatternsize);
               int maxPatternSize = ParseUtils.parseInt(maxPatternString);
               mb.setMaxPatternSize(maxPatternSize);
               return mb;
            default:
               Logger.getLogger(DmPartitionerDispenser.class.getName()).
                       warning("Case not defined: '" + this);
               return null;
         }
      }
      private String partitionerName;
   }


   public static Map<String, PartitionerName> partitioners =
           EnumUtilsAppend.buildMap(PartitionerName.values());
   /*
    static {
      Map<String, Partitioner> aMap = new HashMap<String, Partitioner>();

      for(Partitioner part : Partitioner.values()) {
         aMap.put(part.partitionerName, part);
      }

      partitioners = Collections.unmodifiableMap(aMap);
   }
    *
    */
}

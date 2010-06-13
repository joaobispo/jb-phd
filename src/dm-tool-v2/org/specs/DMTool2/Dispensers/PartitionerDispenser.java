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

package org.specs.DMTool2.Dispensers;

import java.util.Map;
import java.util.logging.Logger;
import org.ancora.Partitioning.MbPartitionerDispenser;
import org.ancora.Partitioning.MbWarp;
import org.ancora.Partitioning.MegaBlock;
import org.ancora.Partitioning.Partitioner;
import org.ancora.SharedLibrary.EnumUtils;
import org.ancora.SharedLibrary.ParseUtils;
import org.specs.DMTool2.Settings.Settings;

/**
 * Uses the current settings to determine which is the appropriate Partitioner.
 *
 * @author Joao Bispo
 */
public class PartitionerDispenser {

   
   public static Partitioner getCurrentPartitioner() {
      //Get name of the current partitioner
      String partitionerName = Settings.optionsTable.getOption(PartitionerOption.current_partitioner);
      
      return getPartitioner(partitionerName);
   }
    

   public static Partitioner getPartitioner(String partitionerName) {
       // Get the correspondent enum
      PartitionerName partitioner = partitioners.get(partitionerName);

      if(partitioner == null) {
         Logger.getLogger(PartitionerDispenser.class.getName()).
                 warning("Partitioner '"+partitionerName+"' not found.");
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
      MbMegaBlock("mb-megablock"),
      Warp("mb-warp");

      private PartitionerName(String partitionerName) {
         this.partitionerName = partitionerName;
      }

      @Override
      public String toString() {
         return partitionerName;
      }

      public String getDmPartitionerName() {
         return partitionerName;
      }

      public String getPartitionerName() {
         return getPartitioner().getName();
      }
      

      public Partitioner getPartitioner() {
         switch (this) {
            case MbBasicBlock:
               return MbPartitionerDispenser.getMbBasicBlock();
            case MbSuperBlock:
               return MbPartitionerDispenser.getMbSuperBlock();
            case MbMegaBlock:
               return getMegablock();
            case Warp:
               return getWarp();
            default:
               Logger.getLogger(PartitionerDispenser.class.getName()).
                       warning("Case not defined: '" + this);
               return null;
         }
      }
      private String partitionerName;

      private Partitioner getWarp() {
         MbWarp warp = new MbWarp();
         Boolean useLimit = Boolean.parseBoolean(Settings.optionsTable.getOption(PartitionerOption.use_warp_branch_limit));
         warp.setUseBranchLimit(useLimit);
         //Boolean useOriginalIdMethod = Boolean.parseBoolean(Options.optionsTable.get(OptionName.partition_daprofuseoriginalidmethod));
         //daprof.setUseDaprofId(useOriginalIdMethod);
         return warp;
      }

  
      private Partitioner getMegablock() {
         MegaBlock mb = MbPartitionerDispenser.getMbMegaBlock();
         // Get max pattern size
         String maxPatternString = Settings.optionsTable.getOption(PartitionerOption.megablock_max_pattern_size);
         int maxPatternSize = ParseUtils.parseInt(maxPatternString);
         mb.setMaxPatternSize(maxPatternSize);
         return mb;
      }
   }


   public static Map<String, PartitionerName> partitioners =
           EnumUtils.buildMap(PartitionerName.values());

}

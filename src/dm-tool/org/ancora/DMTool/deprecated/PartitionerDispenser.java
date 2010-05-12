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

package org.ancora.DMTool.deprecated;

import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Logger;
import org.ancora.DMTool.Settings.Preference;
import org.ancora.Partitioning.BasicBlock;
import org.ancora.Partitioning.MegaBlock;
import org.ancora.Partitioning.Partitioner;
import org.ancora.Partitioning.SuperBlock;
import org.ancora.Partitioning.Tools.InstructionFilter;
//import org.ancora.Partitioning.MbJumpFilter;
import org.ancora.SharedLibrary.ParseUtils;
import org.ancora.SharedLibrary.Preferences.EnumPreferences;

/**
 *
 * @author Joao Bispo
 */
public class PartitionerDispenser {
/*
   public static Partitioner getCurrentPartitioner() {

      String partitionerName = prefs.getPreference(Preference.partitioner).toLowerCase();
      PartitionerOption partitionerOption = partitionerOptions.get(partitionerName);

      if(partitionerOption == null) {
         Logger.getLogger(PartitionerDispenser.class.getName()).
                 info("Partitioner '" + partitionerName + "' not defined. Avaliable options:");
         for (PartitionerOption option : PartitionerOption.values()) {
            Logger.getLogger(PartitionerDispenser.class.getName()).
                    info("- " + option.name());
         }
         return null;
      }

      if(partitionerOption == PartitionerOption.BasicBlock) {
         return new BasicBlock(MICROBLAZE_JUMP_FILTER);
      }

      if(partitionerOption == PartitionerOption.SuperBlock) {
         return new SuperBlock(MICROBLAZE_JUMP_FILTER);
      }

      if(partitionerOption == PartitionerOption.MegaBlock) {
         int maxPatternSize = ParseUtils.parseInt(prefs.getPreference(Preference.megablockMaxPatternSize));
         return new MegaBlock(MICROBLAZE_JUMP_FILTER, maxPatternSize);
      }

      Logger.getLogger(PartitionerDispenser.class.getName()).
              info("Case not defined for partitionerOption '" + partitionerOption + "'");
      return null;
   }
*/
   /**
    * VARIABLES
    */
   private static final EnumPreferences prefs = Preference.getPreferences();
   //private static final InstructionFilter MICROBLAZE_JUMP_FILTER = new MbJumpFilter();
   
   private static final Map<String, PartitionerDispenser.PartitionerOption> partitionerOptions;
   static {
      Map<String, PartitionerDispenser.PartitionerOption> aMap =
              new Hashtable<String, PartitionerDispenser.PartitionerOption>();

      aMap.put(Options.basicBlock.toLowerCase(), PartitionerOption.BasicBlock);
      aMap.put(Options.superBlock.toLowerCase(), PartitionerOption.SuperBlock);
      aMap.put(Options.megaBlock.toLowerCase(), PartitionerOption.MegaBlock);


      partitionerOptions = Collections.unmodifiableMap(aMap);
   }

   /*
   private static final Map<PartitionerOption, Partitioner> partitioners;
   static {
      Map<PartitionerOption, Partitioner> aMap =
              new Hashtable<PartitionerOption, Partitioner>();

      aMap.put(Options.basicBlock.toLowerCase(), PartitionerOption.BasicBlock);
      aMap.put(Options.superBlock.toLowerCase(), PartitionerOption.SuperBlock);
      aMap.put(Options.megaBlock.toLowerCase(), PartitionerOption.MegaBlock);


      partitioners = Collections.unmodifiableMap(aMap);
   }
    */

   /**
    * ENUM
    */
   public enum PartitionerOption {
      BasicBlock,
      SuperBlock,
      MegaBlock;
   }

   public interface Options {
      String basicBlock = "BasicBlock";
      String superBlock = "SuperBlock";
      String megaBlock = "MegaBlock";
   }
}

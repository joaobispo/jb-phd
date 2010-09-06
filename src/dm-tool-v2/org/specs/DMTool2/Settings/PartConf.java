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

import java.util.logging.Logger;
import org.ancora.Partitioning.PartitioningConfig;
import org.ancora.SharedLibrary.ParseUtils;
import org.specs.DMTool2.Dispensers.PartitionerOption;

/**
 *
 * @author Joao Bispo
 */
public enum PartConf {

   currentSettings,
   traceCoverage,
   blockSize;

   public PartitioningConfig getConfig() {
      // Default
      // Setup worker
      boolean useGatherer = false;
      boolean useSelector = false;
      boolean useUniqueFilter = false;
      int selectorThreshold = 1;


      switch (this) {
         case currentSettings:
            useGatherer = Boolean.parseBoolean(Settings.optionsTable.get(PartitionerOption.group_blocks));
            useSelector = Boolean.parseBoolean(Settings.optionsTable.get(PartitionerOption.filter_by_repetitions));
            useUniqueFilter = Boolean.parseBoolean(Settings.optionsTable.get(PartitionerOption.filter_identical));
            selectorThreshold = ParseUtils.parseInt(Settings.optionsTable.get(PartitionerOption.repetition_threshold));
         case traceCoverage:
            useGatherer = true;
            useSelector = false;
            useUniqueFilter = false;
            selectorThreshold = 0;
            break;
         case blockSize:
            useGatherer = true;
            useSelector = true;
            useUniqueFilter = false;
            selectorThreshold = ParseUtils.parseInt(Settings.optionsTable.get(PartitionerOption.repetition_threshold));
            break;
         default:
            Logger.getLogger(PartConf.class.getName()).
                    warning("Case not defined: " + this);
            // Do nothing
            break;
      }

      return new PartitioningConfig(useGatherer, useSelector, useUniqueFilter, selectorThreshold);
   }
}

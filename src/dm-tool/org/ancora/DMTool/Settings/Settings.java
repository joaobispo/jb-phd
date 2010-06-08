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

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ancora.DMTool.Settings.Options.OptionName;
import org.ancora.DMTool.Dispensers.DmPartitionerDispenser;
import org.ancora.Partitioning.Partitioner;
import org.ancora.Partitioning.Tools.BlockWorker;
import org.ancora.SharedLibrary.IoUtils;
import org.ancora.SharedLibrary.ParseUtils;

/**
 * Get parsed settings values.
 *
 * @author Joao Bispo
 */
public class Settings {

   public static Partitioner getPartitioner() {
      return DmPartitionerDispenser.getCurrentPartitioner();
   }

   public static void autoSet(String className, OptionName option, String value) {
      Logger.getLogger(className).
              warning("AutoSetting '"+option+"' to '"+value+"'");
      Options.optionsTable.put(option, value);
   }

   public static void setupBlockWorker(BlockWorker worker) {
      
      Map<OptionName, String> options = Options.optionsTable;
      
      // Setup worker
      boolean useGatherer = Boolean.parseBoolean(options.get(OptionName.partition_groupblocks));
      boolean useSelector = Boolean.parseBoolean(options.get(OptionName.partition_filterbyrepetitions));
      boolean useUniqueFilter = Boolean.parseBoolean(options.get(OptionName.partition_filteridenticalblocks));
      int selectorThreshold = ParseUtils.parseInt(options.get(OptionName.partition_repetitionthreshold));

      worker.setUseGatherer(useGatherer);
      worker.setUseSelector(useSelector);
      worker.setUseUniqueFilter(useUniqueFilter);
      worker.setSelectorRepThreshold(selectorThreshold);

      worker.init();
   }

   public static Level getLoggerLevel() {
      String loggerLevel = Options.optionsTable.get(OptionName.general_loggerlevel).toUpperCase();
      Level defaultLevel = Level.ALL;
      Level level = defaultLevel;
      // Parse Logger Level
      try {
         level = Level.parse(loggerLevel);
      } catch(IllegalArgumentException ex) {
         Logger.getLogger(Settings.class.getName()).
                 info("Could not parse logger level '"+loggerLevel+"'. " +
                 "Setting level to '"+defaultLevel+"'");
      }

      return level;
   }

   public static List<File> getInputFiles() {
      return getInputFiles(new HashSet<String>());
   }

   public static List<File> getInputFiles(Set<String> supportedExtensions) {
      // Get input path
      String input = Options.optionsTable.get(OptionName.general_input);
      File file = new File(input);

      if(!file.exists()) {
         Logger.getLogger(Settings.class.getName()).
                 warning("Input '"+input+"' does not exist.");
         return null;
      }

      // Get files
      //List<File> inputFiles;

      // Check if it is only one file
      if(file.isFile()) {
         List<File> inputFiles = new ArrayList<File>(1);
         inputFiles.add(file);
         return inputFiles;
      }

      // Check if there are extensions to filter
      if(supportedExtensions.isEmpty()) {
         return IoUtils.getFilesRecursive(file);
      }

      // Return files filtered by extension
      return IoUtils.getFilesRecursive(file, supportedExtensions);
   }


   public static File getCsvFile(String suffix) {
      String outputFoldername = Options.optionsTable.get(OptionName.general_outputfolder);
      File outputFolder = new File(outputFoldername, DEFAULT_CSV_FOLDER);
      // Check if it exists
      if (!outputFolder.exists()) {
         // Try to create it
         if (!outputFolder.mkdirs()) {
            Logger.getLogger(Settings.class.getName()).
                    warning("Could not create folder '" + outputFolder.getPath() + "'");
            return null;
         }
      }


      String csvFilename = Options.optionsTable.get(OptionName.general_csvfilebasename) +
              "-"+suffix+IoUtils.DEFAULT_EXTENSION_SEPARATOR+OptionName.extension_csv;

      return new File(outputFolder, csvFilename);
   }



   public static final String DEFAULT_CSV_FOLDER = "csv";
}

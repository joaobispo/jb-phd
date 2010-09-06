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

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ancora.SharedLibrary.IoUtils;

/**
 *
 * @author Joao Bispo
 */
public class Settings {

   // Options table shared between the program
   public static final OptionTable optionsTable = new OptionTable();

   /**
    * 
    * @return all the ELF and TRACE files found in the input path
    */
   public static List<File> getExecutableInputFiles() {
      String elfExtension = Settings.optionsTable.get(GeneralOption.elf_extension);
      String traceExtension = Settings.optionsTable.get(GeneralOption.trace_extension);
      
      Set<String> supportedExtensions = new HashSet<String>();
      supportedExtensions.add(elfExtension);
      supportedExtensions.add(traceExtension);

      // Get files
      return  Settings.getInputFiles(supportedExtensions);
   }

   public static List<File> getInputFiles(Set<String> supportedExtensions) {
      // Get input path
      String input = Settings.optionsTable.get(GeneralOption.input_path);
      File file = new File(input);

      if(!file.exists()) {
         Logger.getLogger(Settings.class.getName()).
                 warning("Input '"+input+"' does not exist.");
         return null;
      }

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

   public static File getCsvFile(String filename) {
      String outputFoldername = Settings.optionsTable.get(GeneralOption.output_path);
      String csvFoldername = Settings.optionsTable.get(GeneralOption.csv_foldername);
      File outputFolder = new File(outputFoldername, csvFoldername);

      // Check if it exists
      if (!outputFolder.exists()) {
         // Try to create it
         if (!outputFolder.mkdirs()) {
            Logger.getLogger(Settings.class.getName()).
                    warning("Could not create folder '" + outputFolder.getPath() + "'");
            return null;
         }
      }

      String csvFilenamePrefix = Settings.optionsTable.get(GeneralOption.csv_filename_prefix);
      if(csvFilenamePrefix.length() > 0) {
         csvFilenamePrefix = csvFilenamePrefix + "-";
      }
      String csvExtension = Settings.optionsTable.get(GeneralOption.csv_extension);

      String csvFilename = csvFilenamePrefix+filename+IoUtils.DEFAULT_EXTENSION_SEPARATOR+csvExtension;
      return new File(outputFolder, csvFilename);
   }

   /*
   public static void setupBlockWorker(BlockWorker worker, Context context) {
      // Default
      // Setup worker
      boolean useGatherer = Boolean.parseBoolean(Settings.optionsTable.get(PartitionerOption.group_blocks));
      boolean useSelector = Boolean.parseBoolean(Settings.optionsTable.get(PartitionerOption.filter_by_repetitions));
      boolean useUniqueFilter = Boolean.parseBoolean(Settings.optionsTable.get(PartitionerOption.filter_identical));
      int selectorThreshold = ParseUtils.parseInt(Settings.optionsTable.get(PartitionerOption.repetition_threshold));


      switch(context) {
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
            // Do nothing
            break;
      }


      worker.setUseGatherer(useGatherer);
      worker.setUseSelector(useSelector);
      worker.setUseUniqueFilter(useUniqueFilter);
      worker.setSelectorRepThreshold(selectorThreshold);

      worker.init();
   }
*/
    public static Level getLoggerLevel() {
      String loggerLevel = Settings.optionsTable.get(GeneralOption.logger_level).toUpperCase();
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
}

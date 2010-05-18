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

package org.ancora.DMTool.Shell;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;
import org.ancora.DMTool.Settings.Options;
import org.ancora.DMTool.Settings.Options.OptionName;
import org.ancora.DMTool.Settings.Settings;
import org.ancora.DMTool.Shell.System.Executable;
import org.ancora.InstructionBlock.BlockIO;
import org.ancora.Partitioning.Blocks.BlockStream;
import org.ancora.InstructionBlock.DmBlockUtils;
import org.ancora.InstructionBlock.InstructionBlock;
import org.ancora.SharedLibrary.IoUtils;
import org.ancora.SharedLibrary.ParseUtils;

/**
 * Partitions elf and trace files into InstructionBlocks, and writes them to
 * the disk.
 *
 * @author Joao Bispo
 */
public class WriteBlocks implements Executable {




   private void setup() {
      blockExtension = Options.optionsTable.get(OptionName.extension_block);
      elfExtension = Options.optionsTable.get(OptionName.extension_elf);
      traceExtension = Options.optionsTable.get(OptionName.extension_trace);
      outputFolder = Options.optionsTable.get(OptionName.general_outputfolder);
   }

   public boolean execute(List<String> arguments) {
      setup();

      /*
      if(arguments.size() < 1) {
         Logger.getLogger(WriteBlocks.class.getName()).
         info("Too few arguments for '"+Command.extractblocks+"' ("+arguments.size()+"). Minimum is 1:");
         Logger.getLogger(WriteBlocks.class.getName()).
         info(Command.extractblocks+" <folder/file>");
         return false;
      }
       *
       */

/*
      // Check file/folder
      File file = new File(arguments.get(0));
      if(!file.exists()) {
         Logger.getLogger(WriteBlocks.class.getName()).
                 info("Path '"+arguments.get(0)+"' does not exist.");
         return false;
      }

      // Get files
      List<File> inputFiles;
      if(file.isFile()) {
         inputFiles = new ArrayList<File>(1);
         inputFiles.add(file);
      } else {
         java.util.Set<String> supportedExtensions = new HashSet<String>();
         supportedExtensions.add(elfExtension);
         supportedExtensions.add(traceExtension);
         inputFiles = IoUtils.getFilesRecursive(file, supportedExtensions);
      }
      */
      List<File> inputFiles = Settings.getInputFiles();

      Logger.getLogger(WriteBlocks.class.getName()).
              info("Found "+inputFiles.size()+" files.");

      processFiles(inputFiles);


      return true;
   }

   /**
    * For each file, partitions the file into instruction blocks and
    * writes the block in a special folder.
    * 
    * @param inputFiles
    */
   private void processFiles(List<File> inputFiles) {
      for (File file : inputFiles) {
         Logger.getLogger(WriteBlocks.class.getName()).
                 info("Processing file '" + file.getName() + "'...");

         String baseFilename = ParseUtils.removeSuffix(file.getName(), IoUtils.DEFAULT_EXTENSION_SEPARATOR);
         String baseFoldername = outputFolder + DEFAULT_FOLDER_SEPARATOR +
                 DEFAULT_BLOCK_FOLDER + DEFAULT_FOLDER_SEPARATOR + baseFilename;
         File baseFolder = IoUtils.safeFolder(baseFoldername);
         if (baseFolder == null) {
            Logger.getLogger(WriteBlocks.class.getName()).
                    info("Skipping file '" + file.getName() + "'");
            continue;
         }

         // Get BlockStream
         BlockStream blockStream = DmBlockUtils.getBlockStream(file);
         InstructionBlock block = blockStream.nextBlock();
         // Start counter
         int counter = 0;
         while (block != null) {
            String blockFilename = baseFilename + "-" + counter
                    + IoUtils.DEFAULT_EXTENSION_SEPARATOR + blockExtension;
            File newBlockFile = new File(baseFolder, blockFilename);
            BlockIO.toFile(newBlockFile, block);

            // Increment counter
            counter++;
            block = blockStream.nextBlock();
         }
         
      }
   }

   /**
    * INSTANCE VARIABLES
    */
      private String traceExtension;
   private String elfExtension;
   private String blockExtension;
   private String outputFolder;

      public static final String DEFAULT_BLOCK_FOLDER = "block";
      public static final String DEFAULT_FOLDER_SEPARATOR = "/";


}

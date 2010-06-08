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

import org.ancora.DMTool.System.Interfaces.Executable;
import java.io.File;
import java.util.List;
import java.util.logging.Logger;
import org.ancora.DMTool.Settings.Options;
import org.ancora.DMTool.Settings.Options.OptionName;
import org.ancora.DMTool.Settings.Settings;
import org.ancora.Partitioning.Blocks.BlockStream;
import org.ancora.DMTool.System.Services.DmBlockUtils;
import org.ancora.InstructionBlock.InstructionBlock;
import org.ancora.DMTool.System.Services.DmDottyUtils;
import org.ancora.Partitioning.Tools.DottyBlock;
import org.ancora.SharedLibrary.IoUtils;
import org.ancora.SharedLibrary.ParseUtils;

/**
 * Reads elf and trace files, partitions the complete application into
 * InstructionBlocks and writes a DOT representation of the trace.
 *
 * @author Joao Bispo
 */
public class DotTrace implements Executable {



   private void setup() {
      outputFolder = Options.optionsTable.get(OptionName.general_outputfolder);
   }

   public boolean execute(List<String> arguments) {
      setup();

      List<File> inputFiles = Settings.getInputFiles();

      Logger.getLogger(DotTrace.class.getName()).
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
         Logger.getLogger(DotTrace.class.getName()).
                 info("Processing file '" + file.getName() + "'...");

         String baseFilename = ParseUtils.removeSuffix(file.getName(), IoUtils.DEFAULT_EXTENSION_SEPARATOR);
         String baseFoldername = outputFolder + DEFAULT_FOLDER_SEPARATOR +
                 DEFAULT_BLOCK_FOLDER + DEFAULT_FOLDER_SEPARATOR + baseFilename;
         File baseFolder = IoUtils.safeFolder(baseFoldername);
         if (baseFolder == null) {
            Logger.getLogger(DotTrace.class.getName()).
                    info("Skipping file '" + file.getName() + "'");
            continue;
         }

         DottyBlock dottyBlock = new DottyBlock();

         // Get BlockStream
         BlockStream blockStream = DmBlockUtils.getBlockStream(file);
         InstructionBlock block = blockStream.nextBlock();

         while (block != null) {
            dottyBlock.addBlock(block);
            block = blockStream.nextBlock();
         }

  
         String partitionerName = Settings.getPartitioner().getName();
         File dotFile = DmDottyUtils.getDottyFile(baseFilename, baseFilename+"-"+partitionerName+"-trace");
         IoUtils.write(dotFile, dottyBlock.generateDot());

      }
   }

   /**
    * INSTANCE VARIABLES
    */
   private String outputFolder;

   public static final String DEFAULT_BLOCK_FOLDER = "block";
   public static final String DEFAULT_FOLDER_SEPARATOR = "/";


}

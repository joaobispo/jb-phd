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

package org.specs.DMTool2.BlockSize;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.ancora.InstructionBlock.InstructionBlock;
import org.ancora.Partitioning.Blocks.BlockStream;
import org.ancora.Partitioning.Partitioner;
import org.ancora.Partitioning.PartitioningService;
import org.specs.DMTool2.Dispensers.BlockDispenser;
import org.specs.DMTool2.Dispensers.PartitionerDispenser;
import org.specs.DMTool2.Program;
import org.specs.DMTool2.Settings.PartConf;
import org.specs.DMTool2.Settings.ProgramName;
import org.specs.DMTool2.Settings.Settings;

/**
 *
 * @author Joao Bispo
 */
public class BlockSize implements Program {

   public BlockSize() {
      isDead = false;
      partitioners = new ArrayList<Partitioner>();
   }



   public boolean execute(List<String> arguments) {
      if (arguments.size() < 1) {
         Logger.getLogger(BlockSize.class.getName()).
                 warning("Too few arguments for '" + getProgramName() + "' (" + arguments.size() + "). Minimum is 1:");
         Logger.getLogger(BlockSize.class.getName()).
                 warning(getProgramName() + " add <partitioner>");
         Logger.getLogger(BlockSize.class.getName()).
                 warning(getProgramName() + " run");
         return false;
      }

      // Get Command
      BlockSizeCommand command = BlockSizeCommand.getBlockSizeCommand(arguments.get(0));
      if (command == null) {
         Logger.getLogger(BlockSize.class.getName()).
                 warning("Command '" + arguments.get(0) + "' not recognized. Avaliable commands: " + Arrays.toString(BlockSizeCommand.values()));
         return false;
      }

      // Execute 'add'
      if (command == BlockSizeCommand.add) {
         // Check if there is another argument
         if (arguments.size() < 2) {
            Logger.getLogger(BlockSize.class.getName()).
                    warning("Too few arguments for '" + getProgramName() + " " + command + "' (" + arguments.size() + "). Minimum is 2:");
            Logger.getLogger(BlockSize.class.getName()).
                    warning(getProgramName() + " " + command + " <partitioner>");
            return false;
         }

         addPartitioner(arguments.get(1));
         return true;
      }

      if(command != BlockSizeCommand.run) {
          Logger.getLogger(BlockSize.class.getName()).
                    warning("Unsupported command: "+command);
          return false;
      }

      // Get files
      List<File> inputFiles = Settings.getExecutableInputFiles();

      Logger.getLogger(BlockSize.class.getName()).
              info("Processing "+inputFiles.size()+" files using "+partitioners.size()+" partitioners.");

      processPartitionersSingle(partitioners, inputFiles);

      // Run is done. Mark this program as dead
      isDead = true;

      return true;
   }

   public boolean isDead() {
      return isDead;
   }

   public ProgramName getProgramName() {
      return ProgramName.blockSize;
   }

   private void addPartitioner(String partitionerName) {
      Partitioner partitioner = PartitionerDispenser.getPartitioner(partitionerName);
      if (partitioner != null) {
         partitioners.add(partitioner);
      }
   }


   private void processPartitionersSingle(List<Partitioner> partitioners, List<File> inputFiles) {
      // Declare stats table
      Map<String, BlockSizeData> mainTable = new HashMap<String, BlockSizeData>();

      // Iterate over partitioners
      for (int i=0; i<partitioners.size(); i++) {
         Partitioner partitioner = partitioners.get(i);
         Logger.getLogger(BlockSize.class.getName()).
                 warning("Using Partitioner '" + partitioner.getName() + "'");

         // If partitioner is not thrown away, program will run out of memory.
         partitioners.set(i, null);

         // Declare new BlockSizeData
         BlockSizeData stats = new BlockSizeData();
         mainTable.put(partitioner.getName(), stats);

         // Iterate over files
         for (File file : inputFiles) {
            BlockSizeData localStats = new BlockSizeData();

            Logger.getLogger(BlockSize.class.getName()).
                    warning("Processing file '" + file.getName() + "'...");

            // Get BlockStream
            PartitioningService pService = new PartitioningService(partitioner, PartConf.blockSize.getConfig());
            BlockStream blockStream = BlockDispenser.getBlockStream(file, pService);
            //BlockStream blockStream = BlockDispenser.getBlockStream(file, partitioner, Context.blockSize);

            // Get first block and partitioner
            InstructionBlock currentBlock;
            // Read all blocks
            while ((currentBlock = blockStream.nextBlock()) != null) {
               localStats.addBlock(currentBlock);
            }

            // Check if instruction totals are the same
            //long totalInsts = blockStream.getTotalInstructions();
            long totalInsts = blockStream.getBusReaderInstructions();
            long statsInst = localStats.getTotalInstructions();
            if (statsInst != totalInsts) {
               Logger.getLogger(BlockSize.class.getName()).
                       warning("TraceCoverage instructions does not add up: Trace(" + totalInsts + ") "
                       + "vs. '" + partitioner + "' Stats (" + statsInst + ")");
            }

            stats.addData(localStats);

         }
      }

      writeCsvAverage(mainTable);
   }

   private void writeCsvAverage(Map<String, BlockSizeData> mainTable) {
      String csvFilename = CSV_PREFIX + "-partitioners-average";
      // Build csv file object
      File ratioFile = Settings.getCsvFile(csvFilename);
      System.err.println("Writing file '" + ratioFile.getName() + "'");

      // Calculate the block sizes for each partitioner.
      BlockSizeProcess.csvStart(ratioFile);

      List<String> keys = new ArrayList(mainTable.keySet());
      Collections.sort(keys);
      for (String partitionerName : keys) {
         BlockSizeProcess.csvAppend(ratioFile, partitionerName, mainTable.get(partitionerName));
      }

   }

   /**
    * INSTANCE VARIABLES
    */
   private boolean isDead;
   private List<Partitioner> partitioners;

public static final String CSV_PREFIX = "block-size";

}

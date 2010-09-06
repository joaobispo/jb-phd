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

import org.ancora.Partitioning.Blocks.BlockStream;
import java.io.File;
import java.util.logging.Logger;
import org.ancora.InstructionBlock.BlockIO;
import org.ancora.InstructionBlock.DtoolTraceBusReader;
import org.ancora.InstructionBlock.ElfBusReader;
import org.ancora.InstructionBlock.InstructionBlock;
import org.ancora.InstructionBlock.InstructionBusReader;
import org.ancora.Partitioning.Blocks.SingleBlockStream;
import org.ancora.Partitioning.Blocks.TraceBlockStream;
import org.ancora.Partitioning.PartitioningService;
import org.ancora.SharedLibrary.IoUtils;
import org.specs.DMTool2.Settings.GeneralOption;
import org.specs.DMTool2.Settings.Settings;

/**
 *
 * @author Joao Bispo
 */
public class BlockDispenser {
/*
   public static BlockStream getBlockStream(File file) {
      return getBlockPack(file).getBlockStream();
   }
*/
 
 
   /**
    * Transforms a File into a BlockStream, using the settings of the program.
    * @param file
    * @return
    */
   
   public static BlockStream getBlockStream(File file, PartitioningService partitioningService) {
   //public static BlockStream getBlockStream(File file, Partitioner partitioner, PartitioningConfig config) {
   //public static BlockStream getBlockStream(File file, Partitioner partitioner, Context context) {
       // Determine file extension and determine type of file
      String filename = file.getName();
      int separatorIndex = filename.lastIndexOf(IoUtils.DEFAULT_EXTENSION_SEPARATOR);
      String extension = filename.substring(separatorIndex+1);

      String blockExtension = Settings.optionsTable.get(GeneralOption.block_extension);
      if(extension.equals(blockExtension)) {
         InstructionBlock block = BlockIO.fromFile(file);
         return new SingleBlockStream(block);
      }

      InstructionBusReader busReader = null;

      String elfExtension = Settings.optionsTable.get(GeneralOption.elf_extension);
      if(extension.equals(elfExtension)) {
         String systemConfig = "./Configuration Files/systemconfig.xml";
         busReader = ElfBusReader.createElfReader(systemConfig, file.getAbsolutePath());
      }

      String traceExtension = Settings.optionsTable.get(GeneralOption.trace_extension);
      if(extension.equals(traceExtension)) {
         busReader = DtoolTraceBusReader.createTraceReader(file);
      }

      // If we got a BusReader, setup BlockWorker.
      if (busReader != null) {
         BlockStream worker = new TraceBlockStream(partitioningService, busReader);
         //Settings.setupBlockWorker(worker, context);
         return worker;
      }


      // Not of the type expected
      Logger.getLogger(BlockDispenser.class.getName()).
              warning("Could not process file with extension '"+extension+"'.");
      return null;
   }

    /**
    * Transforms a File into a BlockStream, using the settings of the program.
    * @param file
    * @return
    */
   /*
   public static BlockStream getMultiBlockStream(File file, List<Partitioner> partitioners, Context context) {
       // Determine file extension and determine type of file
      String filename = file.getName();
      int separatorIndex = filename.lastIndexOf(IoUtils.DEFAULT_EXTENSION_SEPARATOR);
      String extension = filename.substring(separatorIndex+1);

      InstructionBusReader busReader = null;

      String elfExtension = Settings.optionsTable.get(GeneralOption.elf_extension);
      if(extension.equals(elfExtension)) {
         String systemConfig = "./Configuration Files/systemconfig.xml";
         busReader = ElfBusReader.createElfReader(systemConfig, file.getAbsolutePath());
      }

      String traceExtension = Settings.optionsTable.get(GeneralOption.trace_extension);
      if(extension.equals(traceExtension)) {
         busReader = DtoolTraceBusReader.createTraceReader(file);
      }

      // Setup BlockWorkers in BlockStream.
      if (busReader != null) {
         // Create partitioners
         List<Partitioner> realPartitioners = new ArrayList<Partitioner>();
         for(Partitioner partitioner : partitioners) {
            realPartitioners.add(partitioner);
         }
         MultiBlockStream stream = new MultiBlockStream(realPartitioners, busReader);
         for(BlockWorker worker : stream.getWorkers()) {
            Settings.setupBlockWorker(worker, context);
         }
         return stream;
      }


      // Not of the type expected
      Logger.getLogger(BlockDispenser.class.getName()).
              warning("Could not process file with extension '"+extension+"'. Only supports elf and trace.");
      return null;
   }
*/
   public enum Context {
      currentSettings,
      traceCoverage,
      blockSize;
   }
}

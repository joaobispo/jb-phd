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

package org.ancora.DMTool.System.Services;

import org.ancora.Partitioning.Blocks.SingleBlockStream;
import org.ancora.Partitioning.Blocks.BlockStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.ancora.DMTool.Settings.Options;
import org.ancora.DMTool.Settings.Options.OptionName;
import org.ancora.DMTool.Settings.Settings;
import org.ancora.DMTool.Dispensers.DmPartitionerDispenser.PartitionerName;
import org.ancora.InstructionBlock.BlockIO;
import org.ancora.DMTool.System.DataStructures.DmBlockPack;
import org.ancora.InstructionBlock.DtoolTraceBusReader;
import org.ancora.InstructionBlock.ElfBusReader;
import org.ancora.InstructionBlock.InstructionBlock;
import org.ancora.InstructionBlock.InstructionBusReader;
import org.ancora.Partitioning.Partitioner;
import org.ancora.Partitioning.Tools.BlockWorker;
import org.ancora.Partitioning.Tools.BlockWorkerStream;
import org.ancora.Partitioning.Tools.MultiBlockStream;
import org.ancora.SharedLibrary.IoUtils;

/**
 *
 * @author Joao Bispo
 */
public class DmBlockUtils {

   public static BlockStream getBlockStream(File file) {
      return getBlockPack(file).getBlockStream();
   }

   /**
    * Transforms a File into a BlockStream, using the settings of the program.
    * @param file
    * @return
    */
   public static DmBlockPack getBlockPack(File file) {
       // Determine file extension and determine type of file
      String filename = file.getName();
      int separatorIndex = filename.lastIndexOf(IoUtils.DEFAULT_EXTENSION_SEPARATOR);
      String extension = filename.substring(separatorIndex+1);

      String blockExtension = Options.optionsTable.get(OptionName.extension_block);
      if(extension.equals(blockExtension)) {
         InstructionBlock block = BlockIO.fromFile(file);
         return new DmBlockPack(new SingleBlockStream(block), null);
      }

      InstructionBusReader busReader = null;

      String elfExtension = Options.optionsTable.get(OptionName.extension_elf);
      if(extension.equals(elfExtension)) {
         String systemConfig = "./Configuration Files/systemconfig.xml";
         busReader = ElfBusReader.createElfReader(systemConfig, file.getAbsolutePath());
      }

      String traceExtension = Options.optionsTable.get(OptionName.extension_trace);
      if(extension.equals(traceExtension)) {
         busReader = DtoolTraceBusReader.createTraceReader(file);
      }

      // If we got a BusReader, setup BlockWorker.
      if (busReader != null) {
         Partitioner partitioner = Settings.getPartitioner();
         BlockWorkerStream worker = new BlockWorkerStream(partitioner, busReader);
         Settings.setupBlockWorker(worker);
         return new DmBlockPack(worker, busReader);
         //return worker;
      }


      // Not of the type expected
      Logger.getLogger(DmBlockUtils.class.getName()).
              warning("Could not process file with extension '"+extension+"'.");
      return null;
   }

    /**
    * Transforms a File into a BlockStream, using the settings of the program.
    * @param file
    * @return
    */
   public static BlockStream getMultiBlockStream(File file, List<PartitionerName> partitioners) {
       // Determine file extension and determine type of file
      String filename = file.getName();
      int separatorIndex = filename.lastIndexOf(IoUtils.DEFAULT_EXTENSION_SEPARATOR);
      String extension = filename.substring(separatorIndex+1);

      InstructionBusReader busReader = null;

      String elfExtension = Options.optionsTable.get(OptionName.extension_elf);
      if(extension.equals(elfExtension)) {
         String systemConfig = "./Configuration Files/systemconfig.xml";
         busReader = ElfBusReader.createElfReader(systemConfig, file.getAbsolutePath());
      }

      String traceExtension = Options.optionsTable.get(OptionName.extension_trace);
      if(extension.equals(traceExtension)) {
         busReader = DtoolTraceBusReader.createTraceReader(file);
      }

      // Setup BlockWorkers in BlockStream.
      if (busReader != null) {
         // Create partitioners
         List<Partitioner> realPartitioners = new ArrayList<Partitioner>();
         for(PartitionerName partitioner : partitioners) {
            realPartitioners.add(partitioner.getPartitioner());
         }
         MultiBlockStream stream = new MultiBlockStream(realPartitioners, busReader);
         for(BlockWorker worker : stream.getWorkers()) {
            Settings.setupBlockWorker(worker);
         }
         return stream;
      }


      // Not of the type expected
      Logger.getLogger(DmBlockUtils.class.getName()).
              warning("Could not process file with extension '"+extension+"'. Only supports elf and trace.");
      return null;
   }
}

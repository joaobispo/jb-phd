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
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;
import org.ancora.DMTool.Settings.Options;
import org.ancora.DMTool.Settings.Options.OptionName;
import org.ancora.DMTool.Settings.Settings;
import org.ancora.DMTool.Shell.System.Executable;
import org.ancora.InstructionBlock.DmBlockPack;
import org.ancora.InstructionBlock.DmBlockUtils;
import org.ancora.InstructionBlock.InstructionBlock;
import org.ancora.IntermediateRepresentation.DmTransformDispenser;
import org.ancora.IntermediateRepresentation.MbParser;
import org.ancora.IntermediateRepresentation.Operation;
import org.ancora.IntermediateRepresentation.Transformation;
import org.ancora.IrMapping.DmMapperDispenser;
import org.ancora.IrMapping.Mapper;
import org.ancora.Partitioning.Blocks.BlockStream;

/**
 *
 * @author Joao Bispo
 */
public class Simulate implements Executable {
   private void setup() {
      input = Options.optionsTable.get(OptionName.general_input);
            elfExtension = Options.optionsTable.get(OptionName.extension_elf);
      traceExtension = Options.optionsTable.get(OptionName.extension_trace);
      transf = DmTransformDispenser.getCurrentTransformations();
      mapper = DmMapperDispenser.getCurrentMapper();
   }

   public boolean execute(List<String> arguments) {
      setup();

      File file = new File(input);
      if(!file.exists()) {
         logger.info("Input '"+input+"' does not exist.");
         return false;
      }

      // Build list of file extensions
      java.util.Set<String> supportedExtensions = new HashSet<String>();
      supportedExtensions.add(elfExtension);
      supportedExtensions.add(traceExtension);

      // Get files
      List<File> inputFiles = Settings.getInputFiles(supportedExtensions);

      //logger.info("Found "+inputFiles.size()+" files.");
      logger.warning("Found "+inputFiles.size()+" files.");

      processFiles(inputFiles);


      return true;
   }

   private void processFiles(List<File> inputFiles) {
      for (File file : inputFiles) {
         //logger.info("Processing file '"+file.getName()+"'...");
         logger.warning("Processing file '" + file.getName() + "'...");

         // Get BlockStream and BusReader
         DmBlockPack blockPack = DmBlockUtils.getBlockPack(file);
         long processorInstructions = 0l;
         long hwCycles = 0l;
         int repetitionsThreshold = 2;

         BlockStream blockStream = blockPack.getBlockStream();
         InstructionBlock block = blockStream.nextBlock();

         while (block != null) {
            // Check repetitions of block
            int rep = block.getRepetitions();
            if (rep < repetitionsThreshold) {
               // Processor path
               processorInstructions += block.getTotalInstructions();
            } else {
               // Hw path
               // Transform Instruction Block into PureIR
               List<Operation> operations = MbParser.mbToPureIr(block);

               if (operations == null) {
                  continue;
               }


               // Transform
               for (Transformation t : transf) {
                  t.transform(operations);
               }
               
               // Map
               mapper.reset();
               mapper.processOperations(operations);

               // Get cycles
               hwCycles += mapper.getNumberOfLines();

               block = blockStream.nextBlock();
            }
         }

         // Calculate speed-up of program
         long totalNormalCycles = blockPack.getInstructionBusReader().getCycles();
         double cpi = totalNormalCycles / blockPack.getInstructionBusReader().getInstructions();

         long processorCycles = (long) Math.ceil((double)processorInstructions * cpi);
         long totalSimCycles = processorCycles + hwCycles;

         double speedup = (double) totalNormalCycles / (double) totalSimCycles;
         System.err.println("Speed-up:"+speedup);
         
      }
   }

   /**
    * INSTANCE VARIABLES
    */
   private static final Logger logger = Logger.getLogger(Simulate.class.getName());
   private String input;
      private String traceExtension;
   private String elfExtension;
   private List<Transformation> transf;
   private Mapper mapper;



}

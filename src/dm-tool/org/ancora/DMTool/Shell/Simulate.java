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
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;
import org.ancora.DMTool.Settings.Options;
import org.ancora.DMTool.Settings.Options.OptionName;
import org.ancora.DMTool.Settings.Settings;
import org.ancora.DMTool.System.DataStructures.DmBlockPack;
import org.ancora.DMTool.System.Services.DmBlockUtils;
import org.ancora.InstructionBlock.InstructionBlock;
import org.ancora.DMTool.Dispensers.DmTransformDispenser;
import org.ancora.IntermediateRepresentation.MbParser;
import org.ancora.IntermediateRepresentation.Operation;
import org.ancora.IntermediateRepresentation.Transformation;
import org.ancora.DMTool.Dispensers.DmMapperDispenser;
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
//      logger.warning("Found "+inputFiles.size()+" files.");
      logger.warning("Found "+inputFiles.size()+" files. Using partitioner "+Settings.getPartitioner().getName());

      processFiles(inputFiles);


      return true;
   }

   private void processFiles(List<File> inputFiles) {

      double speedupAvg = 0d;
      long globalNormalCycles = 0l;
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

         long totalProcessedInstructions = 0l;
         while (block != null) {
            totalProcessedInstructions += block.getTotalInstructions();

            // Check repetitions of block
            int rep = block.getRepetitions();
            if (rep < repetitionsThreshold) {
               // Processor path
               processorInstructions += block.getTotalInstructions();
            } else {
               // Hw path
               // Transform Instruction Block into PureIR
               //List<Operation> operations = MbParser.mbToPureIr(block);
               List<Operation> operations = MbParser.mbToIrBlock(block).getOperations();

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

               long commCost = mapper.getLiveIns() + mapper.getLiveOuts();

               // Get cycles
               hwCycles += mapper.getNumberOfLines() * block.getRepetitions();
               hwCycles += commCost;

            }
            
               block = blockStream.nextBlock();
         }

         // Calculate speed-up of program
         long traceCycles = blockPack.getInstructionBusReader().getCycles();
         long traceInstructions = blockPack.getInstructionBusReader().getInstructions();
         double cpi = (double)traceCycles / (double)traceInstructions;

         if(traceInstructions != totalProcessedInstructions) {
            Logger.getLogger(Simulate.class.getName()).
                    warning("DTool simulation instructions ("+traceInstructions+") different " +
                    "from instructions fed to dynamic mapping simulation ("+totalProcessedInstructions+")");
         }

         long processorCycles = (long) Math.ceil((double)processorInstructions * cpi);
         long totalSimCycles = processorCycles + hwCycles;

         double speedup = (double) traceCycles / (double) totalSimCycles;
 /*
         System.err.println("Normal Instructions:"+traceInstructions);
         System.err.println("Normal Cycles:"+traceCycles);
         System.err.println("Normal CPI:"+cpi);
         System.err.println("MB Instructions:"+processorInstructions);
         System.err.println("MB Cycles:"+processorCycles);
         System.err.println("HW Cycles:"+hwCycles);
  * 
  */
         System.err.println("Speed-up:"+speedup);
         speedupAvg += speedup;

         globalNormalCycles+=traceCycles;
      }

      System.err.println("\nAverage Speed-up:"+(speedupAvg/inputFiles.size()));
      System.err.println("Global Normal Cycles:"+globalNormalCycles);
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

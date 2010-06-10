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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;
import org.ancora.DMTool.Settings.Options;
import org.ancora.DMTool.Settings.Options.OptionName;
import org.ancora.DMTool.Settings.Settings;
import org.ancora.DMTool.Dispensers.DmStreamMapperDispenser;
import org.ancora.DMTool.Dispensers.DmStreamTransformDispenser;
import org.ancora.FuMatrix.Mapper.GeneralMapper;
import org.ancora.FuMatrix.Stats.MapperData;
import org.ancora.DMTool.System.DataStructures.DmBlockPack;
import org.ancora.DMTool.System.Services.DmBlockUtils;
import org.ancora.InstructionBlock.InstructionBlock;
//import org.ancora.DMTool.Dispensers.DmTransformDispenser;
import org.ancora.DMTool.Simulation.DmSimulateFile;
import org.ancora.DMTool.Simulation.SimulationCalcs;
import org.ancora.DMTool.Simulation.SimulationData;
import org.ancora.IntermediateRepresentation.MbParser;
import org.ancora.IntermediateRepresentation.Operation;
import org.ancora.IntermediateRepresentation.Transformation;
import org.ancora.Partitioning.Blocks.BlockStream;
import org.ancora.SharedLibrary.IoUtils;

/**
 *
 * @author Joao Bispo
 */
public class StreamSimulate implements Executable {
   private void setup() {
      input = Options.optionsTable.get(OptionName.general_input);
            elfExtension = Options.optionsTable.get(OptionName.extension_elf);
      traceExtension = Options.optionsTable.get(OptionName.extension_trace);
//      transf = DmTransformDispenser.getCurrentTransformations();
//      transf = DmStreamTransformDispenser.getCurrentTransformations();
      //mapper = DmStreamMapperDispenser.getCurrentMapper();
      mapper = null;
   }

   public boolean execute(List<String> arguments) {
      setup();

      File file = new File(input);
      if(!file.exists()) {
         logger.warning("Input '"+input+"' does not exist.");
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

      //processFiles(inputFiles);
      processFiles2(inputFiles);


      return true;
   }

   private void processFiles2(List<File> inputFiles) {
      // Optimization
      String optimizationLevel = Options.optionsTable.get(OptionName.general_optimizationstring);
      String transformations = "NoTrans";
//      if(DmTransformDispenser.getCurrentTransformations().size() > 0) {
      if(DmStreamTransformDispenser.getCurrentTransformations().size() > 0) {
         transformations = "WithTransf";
      }

      String runName = optimizationLevel+"-"+transformations;
      List<Double> speedups = new ArrayList<Double>();

      double speedupAcc = 0d;
      long globalNormalCycles = 0l;
      for (File file : inputFiles) {
         logger.warning("Processing file '" + file.getName() + "'...");

         // Get BlockStream and BusReader
         DmBlockPack blockPack = DmBlockUtils.getBlockPack(file);
         BlockStream blockStream = blockPack.getBlockStream();

         DmSimulateFile simulator = new DmSimulateFile(blockStream);
         simulator.runSimulation();
         SimulationData simData = simulator.getSimulationData();
         SimulationCalcs simCalcs = new SimulationCalcs(simData, blockStream.getInstructionBusReader());


         double speedup = simCalcs.getSpeedUp();
         //System.err.println("Speed-up:"+speedup);
         speedupAcc += speedup;

         globalNormalCycles+=blockStream.getInstructionBusReader().getCycles();
         int failedMappings = simData.getFailedMappings();
         if(failedMappings > 0) {
            System.err.println("Failed "+failedMappings+" mappings.");
         }

         speedups.add(speedup);
      }

      System.err.println("Run:"+runName);
      System.err.println("\nAverage Speed-up:" + (speedupAcc / inputFiles.size()));
      System.err.println("Global Normal Cycles:" + globalNormalCycles);
      double avgSpeedup = speedupAcc / inputFiles.size();
      speedups.add(avgSpeedup);

      //writeToCsv(runName, speedups, inputFiles);
   }

   private void processFiles(List<File> inputFiles) {
      // Optimization
      String optimizationLevel = Options.optionsTable.get(OptionName.general_optimizationstring);
      String transformations = "NoTrans";
//      if(DmTransformDispenser.getCurrentTransformations().size() > 0) {
      if(DmStreamTransformDispenser.getCurrentTransformations().size() > 0) {
         transformations = "WithTransf";
      }

      String runName = optimizationLevel+"-"+transformations;
      List<Double> speedups = new ArrayList<Double>();

      double speedupAcc = 0d;
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
         int failedMappings = 0;
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
               /*
               for (Transformation t : transf) {
                  t.transform(operations);
               }
                *
                */

               // Map
               mapper = DmStreamMapperDispenser.getCurrentMapper();
               boolean sucess = true;
               for (Operation operation : operations) {
                  sucess = mapper.accept(operation);
                  if (!sucess) {
                     //Mapping failed: move block to processor
                     failedMappings++;
                     break;
                  }
               }

               if (sucess) {
                  // Show block
                  //System.err.println(block);
                  //StreamTransform.showFus(mapper.getMappedOps());
                  MapperData mapperData = MapperData.build(mapper);

                  //long commCost = mapper.getLiveIns() + mapper.getLiveOuts();
                  long commCost = mapperData.getLiveIns() + mapperData.getLiveOuts();

                  // Get cycles
                  //hwCycles += mapper.getNumberOfLines() * block.getRepetitions();
                  hwCycles += mapperData.getLines() * block.getRepetitions();
                  hwCycles += commCost;

                  int lines = mapperData.getLines();
                  int ops = mapperData.getOps();
                  int moves = mapperData.getMoves();

                  double ilpWithoutMoves = (double)ops / (double)lines;
                  double ilpWithMoves = (double)(ops+moves) / (double)lines;
                  //System.err.println("ILP sem Moves:"+ilpWithoutMoves);
                  //System.err.println("ILP com Moves:"+ilpWithMoves);
               } else {
                  // Processor path
                  processorInstructions += block.getTotalInstructions();
               }

            }

            block = blockStream.nextBlock();
         }

         // Calculate speed-up of program
         long traceCycles = blockPack.getInstructionBusReader().getCycles();
         long traceInstructions = blockPack.getInstructionBusReader().getInstructions();
         double cpi = (double)traceCycles / (double)traceInstructions;

         if(traceInstructions != totalProcessedInstructions) {
            Logger.getLogger(StreamSimulate.class.getName()).
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
         speedupAcc += speedup;

         globalNormalCycles+=traceCycles;
         if(failedMappings > 0) {
            System.err.println("Failed "+failedMappings+" mappings.");
         }

         speedups.add(speedup);
      }

      System.err.println("\nAverage Speed-up:"+(speedupAcc/inputFiles.size()));
      System.err.println("Global Normal Cycles:"+globalNormalCycles);
      double avgSpeedup = speedupAcc/inputFiles.size();
      speedups.add(avgSpeedup);

      writeToCsv(runName,speedups, inputFiles);
   }

   /**
    * INSTANCE VARIABLES
    */
   private static final Logger logger = Logger.getLogger(StreamSimulate.class.getName());
   private String input;
      private String traceExtension;
   private String elfExtension;
//   private List<Transformation> transf;
   private GeneralMapper mapper;

   private void writeToCsv(String runName, List<Double> speedups, List<File> inputFiles) {
      // Get filename
      File csvFile = Settings.getCsvFile("speeups");

      // Check if it exits
      if(!csvFile.exists()) {
         StringBuilder builder = new StringBuilder();
         for(File file : inputFiles) {
            String filename = file.getName();
            // Remove extension and optimization level
            filename = org.ancora.SharedLibrary.ParseUtils.removeSuffix(filename, ".");
            filename = org.ancora.SharedLibrary.ParseUtils.removeSuffix(filename, "-");
            builder.append("\t");
            builder.append(filename);
         }
         builder.append("\taverage");
         builder.append("\n");
         IoUtils.write(csvFile, builder.toString());
      }

      StringBuilder builder = new StringBuilder();
      // Write name of the run
      builder.append(runName);
      // Write results
      for(Double speedup : speedups) {
         builder.append("\t");
         builder.append(speedup);
      }
      builder.append("\n");
      IoUtils.append(csvFile, builder.toString());
   }



}

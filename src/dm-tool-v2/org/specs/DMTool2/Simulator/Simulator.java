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
package org.specs.DMTool2.Simulator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.ancora.Partitioning.Blocks.BlockStream;
import org.ancora.Partitioning.Partitioner;
import org.ancora.SharedLibrary.IoUtils;
import org.specs.DMTool2.Dispensers.BlockDispenser;
import org.specs.DMTool2.Dispensers.BlockDispenser.Context;
import org.specs.DMTool2.Dispensers.PartitionerDispenser;
import org.specs.DMTool2.Dispensers.TransformDispenser;
import org.specs.DMTool2.Program;
import org.specs.DMTool2.Settings.ProgramName;
import org.specs.DMTool2.Settings.Settings;

/**
 *
 * @author Joao Bispo
 */
public class Simulator implements Program {

   public Simulator() {
      isDead = false;
   }



   public boolean isDead() {
      return isDead;
   }

   public ProgramName getProgramName() {
      return ProgramName.simulator;
   }

   private void setup() {
//      input = Options.optionsTable.get(OptionName.general_input);
//      elfExtension = Options.optionsTable.get(OptionName.extension_elf);
//      traceExtension = Options.optionsTable.get(OptionName.extension_trace);
//      transf = DmTransformDispenser.getCurrentTransformations();
//      transf = DmStreamTransformDispenser.getCurrentTransformations();
      //mapper = DmStreamMapperDispenser.getCurrentMapper();
//      mapper = null;
   }

   public boolean execute(List<String> arguments) {
  //    setup();

      // Get files
      List<File> inputFiles = Settings.getExecutableInputFiles();

      //processFiles(inputFiles);
      processFiles2(inputFiles);

      // Mark as dead.
      isDead = true;
      return true;
   }

   private void processFiles2(List<File> inputFiles) {
      // Optimization
      String optimizationLevel = Settings.optionsTable.get(SimulatorOption.string_o_level);
      String transformations = "NoTrans";
//      if(DmTransformDispenser.getCurrentTransformations().size() > 0) {
      if (TransformDispenser.getCurrentTransformations().size() > 0) {
         transformations = "WithTransf";
      }

      String runName = optimizationLevel + "-" + transformations;
      List<Double> speedups = new ArrayList<Double>();

      double speedupAcc = 0d;
      long globalNormalCycles = 0l;
      int maxLineSize = 0;
      int maxLineSizeAcc = 0;
      int maxLineSizeCounter = 0;

      int maxNumberOfLines = 0;
      for (File file : inputFiles) {
         logger.warning("Processing file '" + file.getName() + "'...");

         // Get BlockStream and BusReader
         Partitioner partitioner = PartitionerDispenser.getCurrentPartitioner();
         BlockStream blockStream = BlockDispenser.getBlockStream(file, partitioner, Context.traceCoverage);


         //DmSimulateFile simulator = new DmSimulateFile(blockStream);
         SimulateSingleFile simulator = SimulateSingleFile.getCurrentSimulator(blockStream);

         simulator.runSimulation();
         SimulationData simData = simulator.getSimulationData();
         SimulationCalcs simCalcs = new SimulationCalcs(simData, blockStream.getInstructionBusReader());


         int maxLine = simData.getMaxMappedLineSize();
         maxNumberOfLines = Math.max(maxNumberOfLines, simData.getMaxMappedLines());
         System.err.println("Max # Lines:"+simData.getMaxMappedLines());
         System.err.println("Max Line Size:"+maxLine);
         maxLineSize = Math.max(maxLineSize, maxLine);
         maxLineSizeAcc += maxLine;
         maxLineSizeCounter++;

         double speedup = simCalcs.getSpeedUp();
         //System.err.println("Speed-up:"+speedup);
         speedupAcc += speedup;

         globalNormalCycles += blockStream.getInstructionBusReader().getCycles();
         int failedMappings = simData.getFailedMappings();
         if (failedMappings > 0) {
            System.err.println("Failed " + failedMappings + " mappings.");
         }

         speedups.add(speedup);
      }

      System.err.println("Run:" + runName);
      System.err.println("\nAverage Speed-up:" + (speedupAcc / inputFiles.size()));
      System.err.println("Global Normal Cycles:" + globalNormalCycles);
      double avgSpeedup = speedupAcc / inputFiles.size();
      speedups.add(avgSpeedup);

      System.err.println("Global Max Line Size:"+maxLineSize);
      System.err.println("Global Max Line Avg:"+((double)maxLineSizeAcc/(double)maxLineSizeCounter));
      System.err.println("Global Max # Line:"+maxNumberOfLines);

      writeToCsv(runName, speedups, inputFiles);
   }

   /**
    * INSTANCE VARIABLES
    */
   private boolean isDead;

   private static final Logger logger = Logger.getLogger(Simulator.class.getName());

   //private GeneralMapper mapper;

   private void writeToCsv(String runName, List<Double> speedups, List<File> inputFiles) {
      // Get filename
      File csvFile = Settings.getCsvFile("speeups");

      // Check if it exits
      if (!csvFile.exists()) {
         StringBuilder builder = new StringBuilder();
         for (File file : inputFiles) {
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
      for (Double speedup : speedups) {
         builder.append("\t");
         builder.append(speedup);
      }
      builder.append("\n");
      IoUtils.append(csvFile, builder.toString());
   }


}

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.ancora.DMTool.Settings.Options;
import org.ancora.DMTool.Settings.Options.OptionName;
import org.ancora.DMTool.Settings.Settings;
import org.ancora.DMTool.Shell.Shell.Command;
import org.ancora.DMTool.Shell.System.Executable;
import org.ancora.DMTool.Stats.TraceCoverage.TcBlocksizeData;
import org.ancora.DMTool.Stats.TraceCoverage.TcBlocksizeProcess;
import org.ancora.InstructionBlock.DmBlockUtils;
import org.ancora.InstructionBlock.InstructionBlock;
import org.ancora.Partitioning.Blocks.BlockStream;
import org.ancora.Partitioning.DmPartitionerDispenser;
import org.ancora.DMTool.Stats.TraceCoverage.TcData;
import org.ancora.DMTool.Stats.TraceCoverage.TcProcess;
import org.ancora.Partitioning.DmPartitionerDispenser.PartitionerName;

/**
 * Reads elf and trace files, applies multiple partitions to each file and
 * builds CSV files which maps the number of repetitions in the blocks to the
 * percentage of trace covered.
 *
 * @author Joao Bispo
 */
public class TraceCoverage implements Executable {

   private void setup() {
      elfExtension = Options.optionsTable.get(OptionName.extension_elf);
      traceExtension = Options.optionsTable.get(OptionName.extension_trace);
   }

   public boolean execute(List<String> arguments) {
      setup();

       if(arguments.size() < 1) {
         Logger.getLogger(TraceCoverage.class.getName()).
         warning("Too few arguments for '"+Command.tracecoverage+"' ("+arguments.size()+"). Minimum is 1:");
         Logger.getLogger(TraceCoverage.class.getName()).
         warning(Command.tracecoverage+" <partitioner1> <partitioner2> ...");
         return false;
      }

      // Get partitioners in arguments
      List<PartitionerName> partitioners = parsePartitioners(arguments);

      // Build list of file extensions
      java.util.Set<String> supportedExtensions = new HashSet<String>();
      supportedExtensions.add(elfExtension);
      supportedExtensions.add(traceExtension);

      // Get files
      List<File> inputFiles = Settings.getInputFiles(supportedExtensions);

      Logger.getLogger(TraceCoverage.class.getName()).
              info("Processing "+inputFiles.size()+" files using "+partitioners.size()+" partitioners.");

      processPartitioners(partitioners, inputFiles);

      return true;
   }

   private List<PartitionerName> parsePartitioners(List<String> arguments) {
      List<PartitionerName> partitioners = new ArrayList<PartitionerName>();

      for(String argument : arguments) {
         PartitionerName partitioner = DmPartitionerDispenser.partitioners.get(argument);
         if(partitioner != null) {
            partitioners.add(partitioner);
         }
      }

      return partitioners;
   }



   private void processPartitioners(List<PartitionerName> partitioners, List<File> inputFiles) {
      // Initialize main table
      Map<String, List<TcData>> mainTable =
              new HashMap<String, List<TcData>>();
      for (PartitionerName part : partitioners) {
         mainTable.put(part.getPartitionerName(), new ArrayList<TcData>());
      }

      // Calculate maximum number of repetitions
      int maxRep = 0;
      long maxBlockSize = 0;

      // Iterate over files
      for (File file : inputFiles) {
         Logger.getLogger(WriteBlocks.class.getName()).
                 warning("Processing file '" + file.getName() + "'...");

         // Get MultiBlockStream
         BlockStream blockStream = DmBlockUtils.getMultiBlockStream(file, partitioners);

         // Initialize stats gatherer
         Map<String, TcData> stats = new HashMap();
         for (PartitionerName part : partitioners) {
            stats.put(part.getPartitionerName(), new TcData(file.getName()));
         }

         // Get first block and partitioner
         InstructionBlock currentBlock = blockStream.nextBlock();
         String currentPartitioner = blockStream.getPartitionerName();

         // Read all blocks
         while (currentBlock != null) {
            stats.get(currentPartitioner).addBlock(currentBlock);

            maxRep = Math.max(maxRep, currentBlock.getRepetitions());
            maxBlockSize = Math.max(maxBlockSize, currentBlock.getTotalInstructions());

            currentBlock = blockStream.nextBlock();
            currentPartitioner = blockStream.getPartitionerName();
         }

         // Check if instruction totals are the same
         long totalInsts = blockStream.getTotalInstructions();
         for(String key : stats.keySet()) {
            long statsInst = stats.get(key).getTotalInstructions();
            if(statsInst != totalInsts) {
               Logger.getLogger(TraceCoverage.class.getName()).
                       warning("TraceCoverage instructions does not add up: Trace(" + totalInsts + ") " +
                 "vs. '"+key+"' Stats (" + statsInst + ")");
               continue;
            }
         }

         // Add stats to the main table
         for(String key : stats.keySet()) {
            mainTable.get(key).add(stats.get(key));
         }
      }

      // Process stats according to each partitioner
      maxRep++;
      processStats(partitioners, mainTable, maxRep);
//      maxBlockSize++;
//      processBlocksizeStats(partitioners, mainTable, maxBlockSize);
   }

   private void estimateSize(Map<String, List<TcData>> mainTable) {
      long stringCounter =0l;
      long longCouter = 0l;
      long intCouter = 0l;
      
      for(String key1 : mainTable.keySet()) {
         stringCounter+=key1.length();
         for(TcData traceCov : mainTable.get(key1)) {
            for(Integer key2 : traceCov.getInstPerRepetitions().keySet()) {
               longCouter++;
               intCouter++;
            }
         }
      }

      System.err.println("String Chars:"+stringCounter);
      System.err.println("Longs:"+longCouter);
      System.err.println("Ints:"+intCouter);
   }

   private void processStats(List<PartitionerName> partitioners, Map<String, List<TcData>> mainTable, int maxRepetitions) {
      for(PartitionerName partitioner : partitioners) {
         // Build csv file object
         //File absNormFile = Settings.getCsvFile("absnorm-"+partitioner.getPartitionerName());
         File ratioFile = Settings.getCsvFile("ratio-"+partitioner.getPartitionerName());
         //TcProcess.csvStart(absNormFile, maxRepetitions);
         TcProcess.csvStart(ratioFile, maxRepetitions);

         // Collect totals
         long absTotals[] = new long[maxRepetitions];
         double ratioTotals[] = new double[maxRepetitions];

         // Calculate the instruction coverage for each file
         List<TcData> stats = mainTable.get(partitioner.getPartitionerName());
         for(TcData stat : stats) {
            // Get line with values for the number of instructions
            long[] absValues = TcProcess.getAbsLine(stat, maxRepetitions);
            // Calculate ratio
            double[] ratioValues = new double[maxRepetitions];
            long totalInst = stat.getTotalInstructions();
            for(int i=0; i<maxRepetitions; i++) {
               ratioValues[i] = (double)absValues[i] / (double)totalInst;

               // Add to totals
               ratioTotals[i] += ratioValues[i];
               absTotals[i] += absValues[i];
            }

            // Save data to the csv file
            //TcProcess.csvAppend(absNormFile, stat.getFilename(), absValues);
            TcProcess.csvAppend(ratioFile, stat.getFilename(), ratioValues);
         }

         // Calculate averages
         double iterations = stats.size();
         double absAvg[] = new double[maxRepetitions];
         double ratioAvg[] = new double[maxRepetitions];
         // Calculate max value from absAvg to use for normalization
         double absNormFactor = (double)absTotals[0] / iterations;

         for(int i=0; i<maxRepetitions; i++) {
            absAvg[i] = ((double) absTotals[i] / (double) iterations) / absNormFactor;
            ratioAvg[i] = ratioTotals[i] / iterations;
         }

         // Write csv
         //TcProcess.csvAppend(absNormFile, "absnorm-avg", absAvg);
         TcProcess.csvAppend(ratioFile, "absnorm-avg", absAvg);
         TcProcess.csvAppend(ratioFile, "ratio-avg", ratioAvg);
/*
         for(int i=0; i<maxRepetitions; i++) {
            ratioAverage[i] = ratioAverage[i] / stats.size();
         }
double ratioAverage[] = new double[maxRepetitions];
         System.err.println(Arrays.toString(ratioAverage));
 *
 */
      }
   }

   private void processBlocksizeStats(List<PartitionerName> partitioners, Map<String, List<TcBlocksizeData>> mainTable, int maxBlocksize) {
      for(PartitionerName partitioner : partitioners) {
         // Build csv file object
         //File absNormFile = Settings.getCsvFile("absnorm-"+partitioner.getPartitionerName());
         File ratioFile = Settings.getCsvFile("blocksize-"+partitioner.getPartitionerName());
         //TcProcess.csvStart(absNormFile, maxRepetitions);
         TcBlocksizeProcess.csvBlocksizeStart(ratioFile, maxBlocksize);

         // Scale size
         int scaleSize = TcBlocksizeProcess.createLogScale(10, maxBlocksize).size();
         // Collect totals
         long absTotals[] = new long[scaleSize];
         double ratioTotals[] = new double[scaleSize];


         // Calculate the instruction coverage for each file
         List<TcBlocksizeData> stats = mainTable.get(partitioner.getPartitionerName());
         for(TcBlocksizeData stat : stats) {
            // Get line with values for the number of instructions
            long[] absValues = TcBlocksizeProcess.getBlockLogLine(stat, maxBlocksize);
            // Calculate ratio
            double[] ratioValues = new double[scaleSize];
            long totalInst = stat.getTotalInstructions();
            for(int i=0; i<scaleSize; i++) {
               ratioValues[i] = (double)absValues[i] / (double)totalInst;

               // Add to totals
               ratioTotals[i] += ratioValues[i];
               absTotals[i] += absValues[i];
            }

            // Save data to the csv file
            //TcProcess.csvAppend(absNormFile, stat.getFilename(), absValues);
            TcProcess.csvAppend(ratioFile, stat.getFilename(), ratioValues);
         }

         // Calculate averages
         double iterations = stats.size();
         double absAvg[] = new double[scaleSize];
         double ratioAvg[] = new double[scaleSize];
         // Calculate max value from absAvg to use for normalization
         double absNormFactor = (double)absTotals[0] / iterations;

         for(int i=0; i<scaleSize; i++) {
            absAvg[i] = ((double) absTotals[i] / (double) iterations) / absNormFactor;
            ratioAvg[i] = ratioTotals[i] / iterations;
         }

         // Write csv
         //TcProcess.csvAppend(absNormFile, "absnorm-avg", absAvg);
         TcProcess.csvAppend(ratioFile, "absnorm-avg", absAvg);
         TcProcess.csvAppend(ratioFile, "ratio-avg", ratioAvg);

      }
   }

   /**
    * INSTANCE VARIABLES
    */
   private String traceExtension;
   private String elfExtension;

}

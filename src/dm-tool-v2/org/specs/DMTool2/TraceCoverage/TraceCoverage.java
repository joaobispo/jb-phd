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

package org.specs.DMTool2.TraceCoverage;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.ancora.InstructionBlock.InstructionBlock;
import org.ancora.Partitioning.Blocks.BlockStream;
import org.ancora.Partitioning.Partitioner;
import org.specs.DMTool2.Dispensers.BlockDispenser;
import org.specs.DMTool2.Dispensers.BlockDispenser.Context;
import org.specs.DMTool2.Dispensers.PartitionerDispenser;
import org.specs.DMTool2.Program;
import org.specs.DMTool2.Settings.ProgramName;
import org.specs.DMTool2.Settings.Settings;

/**
 * Reads elf and trace files, applies multiple partitions to each file and
 * builds CSV files which maps the number of repetitions in the blocks to the
 * percentage of trace covered.
 *
 * @author Joao Bispo
 */
public class TraceCoverage implements Program {

   public TraceCoverage() {
      isDead = false;
      partitioners = new ArrayList<Partitioner>();
   }



   public boolean isDead() {
      return isDead;
   }

   public ProgramName getProgramName() {
      return ProgramName.traceCoverage;
   }

   public boolean execute(List<String> arguments) {
     

       if(arguments.size() < 1) {
         Logger.getLogger(TraceCoverage.class.getName()).
         warning("Too few arguments for '"+getProgramName()+"' ("+arguments.size()+"). Minimum is 1:");
         Logger.getLogger(TraceCoverage.class.getName()).
         warning(getProgramName()+" add <partitioner>");
         Logger.getLogger(TraceCoverage.class.getName()).
                 warning(getProgramName()+" run");
         return false;
      }

      // Get Command
      TraceCommand command = TraceCommand.getTraceCommand(arguments.get(0));
      if (command == null) {
         Logger.getLogger(TraceCoverage.class.getName()).
                 warning("Command '" + arguments.get(0) + "' not recognized. Avaliable commands: " + Arrays.toString(TraceCommand.values()));
         return false;
      }

      // Execute 'add'
      if (command == TraceCommand.add) {
         // Check if there is another argument
         if (arguments.size() < 2) {
            Logger.getLogger(TraceCoverage.class.getName()).
                    warning("Too few arguments for '" + getProgramName() + " " + command + "' (" + arguments.size() + "). Minimum is 2:");
            Logger.getLogger(TraceCoverage.class.getName()).
                    warning(getProgramName() + " " + command + " <partitioner>");
            return false;
         }

         addPartitioner(arguments.get(1));
         return true;
      }

      if(command != TraceCommand.run) {
          Logger.getLogger(TraceCoverage.class.getName()).
                    warning("Unsupported command: "+command);
          return false;
      }

      // Get files
      List<File> inputFiles = Settings.getExecutableInputFiles();

      Logger.getLogger(TraceCoverage.class.getName()).
              info("Processing "+inputFiles.size()+" files using "+partitioners.size()+" partitioners.");

      processPartitionersSingle(partitioners, inputFiles);

      // Run is done. Mark this program as dead
      isDead = true;

      return true;
   }

   private void addPartitioner(String partitionerName) {
      Partitioner partitioner = PartitionerDispenser.getPartitioner(partitionerName);
      if(partitioner != null) {
         partitioners.add(partitioner);
      }
   }

   private void processPartitionersSingle(List<Partitioner> partitioners, List<File> inputFiles) {
      // Initialize main table
      Map<String, List<TcData>> mainTable =
              new HashMap<String, List<TcData>>();
      for (Partitioner part : partitioners) {
         mainTable.put(part.getName(), new ArrayList<TcData>());
      }

      // Calculate maximum number of repetitions
      int maxRep = 0;
      long maxBlockSize = 0;

      List<String> partitionerNames = new ArrayList<String>();

      // Iterate over partitioners
      for (int i=0; i<partitioners.size(); i++) {
         Partitioner partitioner = partitioners.get(i);
         Logger.getLogger(TraceCoverage.class.getName()).
                 warning("Using Partitioner '" + partitioner.getName() + "'");

         partitionerNames.add(partitioner.getName());
         // If partitioner is not thrown away, program will run out of memory.
         partitioners.set(i, null);

         // Iterate over files
         for (File file : inputFiles) {
            Logger.getLogger(TraceCoverage.class.getName()).
                    warning("Processing file '" + file.getName() + "'...");

            // Get BlockStream
            BlockStream blockStream = BlockDispenser.getBlockStream(file, partitioner, Context.traceCoverage);

            // Initialize stats gatherer
            TcData stats = new TcData(file.getName());
            /*
            Map<Partitioner, TcData> stats = new HashMap();
            for (Partitioner part : partitioners) {
               stats.put(part, new TcData(file.getName()));
            }
             *
             */

            // Get first block and partitioner
            InstructionBlock currentBlock = blockStream.nextBlock();
            //Partitioner currentPartitioner = blockStream.getPartitioner();

            // Read all blocks
            while (currentBlock != null) {
               stats.addBlock(currentBlock);

               maxRep = Math.max(maxRep, currentBlock.getRepetitions());
               maxBlockSize = Math.max(maxBlockSize, currentBlock.getTotalInstructions());

               currentBlock = blockStream.nextBlock();
               //currentPartitioner = blockStream.getPartitioner();
            }

            // Check if instruction totals are the same
            long totalInsts = blockStream.getTotalInstructions();
            long statsInst = stats.getTotalInstructions();
            if (statsInst != totalInsts) {
               Logger.getLogger(TraceCoverage.class.getName()).
                       warning("TraceCoverage instructions does not add up: Trace(" + totalInsts + ") "
                       + "vs. '" + partitioner + "' Stats (" + statsInst + ")");

            }
            // Add stats to the main table
            mainTable.get(partitioner.getName()).add(stats);
         }
      }
      // Process stats according to each partitioner
      maxRep++;
      writeCsvAverage(partitionerNames, mainTable, maxRep);
//      processStats2(partitioners, mainTable, maxRep);
//      maxBlockSize++;
//      processBlocksizeStats(partitioners, mainTable, maxBlockSize);
   }

   private void processPartitioners(List<Partitioner> partitioners, List<File> inputFiles) {
      // Initialize main table
      Map<Partitioner, List<TcData>> mainTable =
              new HashMap<Partitioner, List<TcData>>();
      for (Partitioner part : partitioners) {
         mainTable.put(part, new ArrayList<TcData>());
      }

      // Calculate maximum number of repetitions
      int maxRep = 0;
      long maxBlockSize = 0;

      // Iterate over files
      for (File file : inputFiles) {
         Logger.getLogger(TraceCoverage.class.getName()).
                 warning("Processing file '" + file.getName() + "'...");

         // Get MultiBlockStream
         BlockStream blockStream = BlockDispenser.getMultiBlockStream(file, partitioners, Context.traceCoverage);

         // Initialize stats gatherer
         Map<Partitioner, TcData> stats = new HashMap();
         for (Partitioner part : partitioners) {
            stats.put(part, new TcData(file.getName()));
         }

         // Get first block and partitioner
         InstructionBlock currentBlock = blockStream.nextBlock();
         Partitioner currentPartitioner = blockStream.getPartitioner();

         // Read all blocks
         while (currentBlock != null) {
            stats.get(currentPartitioner).addBlock(currentBlock);

            maxRep = Math.max(maxRep, currentBlock.getRepetitions());
            maxBlockSize = Math.max(maxBlockSize, currentBlock.getTotalInstructions());

            currentBlock = blockStream.nextBlock();
            currentPartitioner = blockStream.getPartitioner();
         }

         // Check if instruction totals are the same
         long totalInsts = blockStream.getTotalInstructions();
         for(Partitioner key : stats.keySet()) {
            long statsInst = stats.get(key).getTotalInstructions();
            if(statsInst != totalInsts) {
               Logger.getLogger(TraceCoverage.class.getName()).
                       warning("TraceCoverage instructions does not add up: Trace(" + totalInsts + ") " +
                 "vs. '"+key+"' Stats (" + statsInst + ")");
               continue;
            }
         }

         // Add stats to the main table
         for(Partitioner key : stats.keySet()) {
            mainTable.get(key).add(stats.get(key));
         }
      }

      // Process stats according to each partitioner
      maxRep++;
      writeCsvIndividual(partitioners, mainTable, maxRep);
//      processStats2(partitioners, mainTable, maxRep);
//      maxBlockSize++;
//      processBlocksizeStats(partitioners, mainTable, maxBlockSize);
   }

   private void processStats(List<Partitioner> partitioners, Map<Partitioner, List<TcData>> mainTable, int maxRepetitions) {
      for(Partitioner partitioner : partitioners) {
         String csvFilename = CSV_PREFIX + "-" + partitioner.getName();
         // Build csv file object
         File ratioFile = Settings.getCsvFile(csvFilename);

         TcProcess.csvStart(ratioFile, maxRepetitions);

         // Collect totals
         long absTotals[] = new long[maxRepetitions];
         double ratioTotals[] = new double[maxRepetitions];

         // Calculate the instruction coverage for each file
         List<TcData> stats = mainTable.get(partitioner);
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
         //TcProcess.csvAppend(ratioFile, "absnorm-avg", absAvg);
         TcProcess.csvAppend(ratioFile, "ratio-avg", ratioAvg);

      }
   }

   /**
    * Writes one CSV for the entire run, with the averages for each partitioner.
    *
    * @param partitioners
    * @param mainTable
    * @param maxRepetitions
    */
   private void writeCsvAverage(List<String> partitionerNames, Map<String, List<TcData>> mainTable, int maxRepetitions) {
               String csvSuffix = Settings.optionsTable.getOption(TraceCoverageOption.csv_suffix);
         if(csvSuffix.length() > 0) {
            csvSuffix = "-"+csvSuffix;
         }
   
      String csvFilename = CSV_PREFIX + "-partitioners-average"+csvSuffix;;
         // Build csv file object
         File ratioFile = Settings.getCsvFile(csvFilename);
         System.err.println("Writing file '"+ratioFile.getName()+"'");

         // Calculate the instruction coverage for each file
         //List<TcData> stats = mainTable.get(partitioner);
         // Get MasterLine
         List<Integer> masterLine = TcProcess.getMasterLine(mainTable, maxRepetitions);

         TcProcess.csvStart(ratioFile, masterLine);

      // Collect totals
      //long absTotals[] = new long[masterLine.size()];
      

      //System.err.println("Partitioner Names:"+partitionerNames);
      for (String partitionerName : partitionerNames) {
         double ratioTotals[] = new double[masterLine.size()];
         // Calculate Partitioner Average
         List<TcData> stats = mainTable.get(partitionerName);
         for (TcData stat : stats) {
            // Get line with values for the number of instructions
            List<Long> absValues = TcProcess.getAbsReduxLine(stat, maxRepetitions, masterLine);
            // Calculate ratio
            double[] ratioValues = new double[masterLine.size()];
            long totalInst = stat.getTotalInstructions();

            for (int i = 0; i < masterLine.size(); i++) {
               ratioValues[i] = (double) absValues.get(i) / (double) totalInst;

               // Add to totals
               ratioTotals[i] += ratioValues[i];
               //absTotals[i] += absValues.get(i);
            }

            // Save data to the csv file
            //TcProcess.csvAppend(ratioFile, stat.getFilename(), ratioValues);
         }

         // Calculate averages
         double iterations = stats.size();
         //double absAvg[] = new double[masterLine.size()];
         double ratioAvg[] = new double[masterLine.size()];
         // Calculate max value from absAvg to use for normalization
         //double absNormFactor = (double) absTotals[0] / iterations;

         for (int i = 0; i < masterLine.size(); i++) {
            //absAvg[i] = ((double) absTotals[i] / (double) iterations) / absNormFactor;
            ratioAvg[i] = ratioTotals[i] / iterations;
         }

         // Write csv
         //TcProcess.csvAppend(ratioFile, "absnorm-avg", absAvg);
         TcProcess.csvAppend(ratioFile, partitionerName, ratioAvg);
      }
   }

   /**
    * Writes one CSV for each partitioner, each CSV has a line for each processed file.
    * 
    * @param partitioners
    * @param mainTable
    * @param maxRepetitions
    */
      private void writeCsvIndividual(List<Partitioner> partitioners, Map<Partitioner, List<TcData>> mainTable, int maxRepetitions) {
      for(Partitioner partitioner : partitioners) {
         String csvSuffix = Settings.optionsTable.getOption(TraceCoverageOption.csv_suffix);
         if(csvSuffix.length() > 0) {
            csvSuffix = "-"+csvSuffix;
         }

         String csvFilename = CSV_PREFIX + "-" + partitioner.getName()+csvSuffix;

         // Build csv file object
         File ratioFile = Settings.getCsvFile(csvFilename);

         

         // Calculate the instruction coverage for each file
         List<TcData> stats = mainTable.get(partitioner);
         // Get MasterLine
         List<Integer> masterLine = TcProcess.getMasterLine(stats, maxRepetitions);

         TcProcess.csvStart(ratioFile, masterLine);

         // Collect totals
         long absTotals[] = new long[masterLine.size()];
         double ratioTotals[] = new double[masterLine.size()];


         //System.err.println("MasterLine:"+masterLine);

         for(TcData stat : stats) {
            //System.err.println("Stat:"+stat);
            // Get line with values for the number of instructions
            //long[] absValues = TcProcess.getAbsLine(stat, maxRepetitions);
            List<Long> absValues = TcProcess.getAbsReduxLine(stat, maxRepetitions, masterLine);
            //System.err.println("Processing:"+absValues);
            // Calculate ratio
            //List<Double> ratioValuesList = new ArrayList<Double>();
            double[] ratioValues = new double[masterLine.size()];
            long totalInst = stat.getTotalInstructions();

            for(int i=0; i<masterLine.size(); i++) {
               ratioValues[i] = (double)absValues.get(i) / (double)totalInst;

               // Add to totals
               ratioTotals[i] += ratioValues[i];
               absTotals[i] += absValues.get(i);
            }
            

            // Save data to the csv file
            TcProcess.csvAppend(ratioFile, stat.getFilename(), ratioValues);
         }

         // Calculate averages
         double iterations = stats.size();
         double absAvg[] = new double[masterLine.size()];
         double ratioAvg[] = new double[masterLine.size()];
         // Calculate max value from absAvg to use for normalization
         double absNormFactor = (double)absTotals[0] / iterations;

         for(int i=0; i<masterLine.size(); i++) {
            absAvg[i] = ((double) absTotals[i] / (double) iterations) / absNormFactor;
            ratioAvg[i] = ratioTotals[i] / iterations;
         }

         // Write csv
         //TcProcess.csvAppend(ratioFile, "absnorm-avg", absAvg);
         TcProcess.csvAppend(ratioFile, "ratio-avg", ratioAvg);
      }
   }


   /**
    * INSTANCE VARIABLES
    */
   //private String traceExtension;
   //private String elfExtension;
   private boolean isDead;
   private List<Partitioner> partitioners;

   public static final String CSV_PREFIX = "trace-coverage";


}

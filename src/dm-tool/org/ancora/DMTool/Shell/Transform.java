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
import org.ancora.DMTool.Shell.System.Executable;
import org.ancora.IrMapping.DmMapperDispenser;
import org.ancora.DMTool.Settings.Options;
import org.ancora.DMTool.Settings.Options.OptionName;
import org.ancora.DMTool.Settings.Settings;
import org.ancora.DMTool.Shell.System.Transform.OperationListStats;
import org.ancora.DMTool.Stats.DataProcess;
import org.ancora.DMTool.Stats.DataProcessDouble;
import org.ancora.DMTool.Stats.LongTransformDataSingle;
import org.ancora.DMTool.Stats.LongTransformDataTotal;
import org.ancora.DMTool.Stats.Transform.TransformStats;
import org.ancora.IntermediateRepresentation.DmTransformDispenser;
import org.ancora.Partitioning.Blocks.BlockStream;
import org.ancora.InstructionBlock.DmBlockUtils;
import org.ancora.InstructionBlock.InstructionBlock;
import org.ancora.IntermediateRepresentation.MbParser;
import org.ancora.IrMapping.Tools.Dotty;
import org.ancora.IrMapping.Mapper;
import org.ancora.IntermediateRepresentation.Operation;
import org.ancora.IntermediateRepresentation.OperationType;
import org.ancora.SharedLibrary.IoUtils;
import org.ancora.SharedLibrary.ParseUtils;
import org.ancora.IntermediateRepresentation.Transformation;
import org.ancora.IrMapping.DmDottyUtils;

/**
 *
 * @author Joao Bispo
 */
public class Transform implements Executable {

   public Transform() {
      logger = Logger.getLogger(Transform.class.getName());

     transformStats = new TransformStats();

   }

   private void setup() {
      blockExtension = Options.optionsTable.get(OptionName.extension_block);
      elfExtension = Options.optionsTable.get(OptionName.extension_elf);
      traceExtension = Options.optionsTable.get(OptionName.extension_trace);
      mapper = DmMapperDispenser.getCurrentMapper();
      transf = DmTransformDispenser.getCurrentTransformations();
      writeDot = Boolean.parseBoolean(Options.optionsTable.get(OptionName.ir_writedot));

      input = Options.optionsTable.get(OptionName.general_input);
   }



   public boolean execute(List<String> arguments) {
      setup();

      /*
      if(arguments.size() < 1) {
         logger.info("Too few arguments for 'transform' ("+arguments.size()+"). Minimum is 1:");
         logger.info(Command.transform+" <folder/file>");
         return false;
      }
       *
       */


      // Check file/folder
//      File file = new File(arguments.get(0));
      File file = new File(input);
      if(!file.exists()) {
         //logger.info("Input '"+arguments.get(0)+"' does not exist.");
         logger.info("Input '"+input+"' does not exist.");
         return false;
      }

      // Build list of file extensions
      java.util.Set<String> supportedExtensions = new HashSet<String>();
      supportedExtensions.add(blockExtension);
      supportedExtensions.add(elfExtension);
      supportedExtensions.add(traceExtension);

      // Get files
      List<File> inputFiles = Settings.getInputFiles(supportedExtensions);

      /*
      // Get files
      List<File> inputFiles;
      if(file.isFile()) {
         inputFiles = new ArrayList<File>(1);
         inputFiles.add(file);
      } else {
         java.util.Set<String> supportedExtensions = new HashSet<String>();
         supportedExtensions.add(blockExtension);
         supportedExtensions.add(elfExtension);
         supportedExtensions.add(traceExtension);
         inputFiles = IoUtils.getFilesRecursive(file, supportedExtensions);
      }
*/

      logger.info("Found "+inputFiles.size()+" files.");

      processFiles(inputFiles);


      return true;
   }

   private void processFiles(List<File> inputFiles) {
      //List<OperationListStats> statsBefore = new ArrayList<OperationListStats>();
      //List<OperationListStats> statsAfter = new ArrayList<OperationListStats>();
      LongTransformDataTotal totalBefore = new LongTransformDataTotal();
      LongTransformDataTotal totalAfter = new LongTransformDataTotal();
      //long totalInstructions = 0;

      long totalProcessedOperations = 0l;
      long totalNops = 0l;

      for(File file : inputFiles) {
         //logger.info("Processing file '"+file.getName()+"'...");
         logger.warning("Processing file '"+file.getName()+"'...");
         String baseFilename = ParseUtils.removeSuffix(file.getName(), IoUtils.DEFAULT_EXTENSION_SEPARATOR);

         // Get BlockStream
         BlockStream blockStream = DmBlockUtils.getBlockStream(file);
         InstructionBlock block = blockStream.nextBlock();
         // Start counter
         int counter = 0;
         while(block != null) {
             String blockName = baseFilename+"-"+counter;
             logger.warning("Processing Block '"+blockName);
//             logger.warning("Processing block '"+blockName);
//            logger.info("Block "+counter+", "+block.getRepetitions()+" repetitions.");

            // Transform Instruction Block into PureIR
            List<Operation> operations = MbParser.mbToPureIr(block);

            if(operations == null) {
               continue;
            }

            // Get stats before transformations
            // Map
            mapper.reset();
            mapper.processOperations(operations);

            // Get stats after transformation
            LongTransformDataSingle beforeData = DataProcess.collectTransformData(mapper, block.getRepetitions());

//            OperationListStats beforeTransf = OperationListStats.buildStats(operations, mapper,
//                    block.getRepetitions(), blockName);
/*
            // Show operations before
            System.out.println("BEFORE OPERATIONS:");
            for(Operation operation : operations) {
               System.out.println(operation.getFullOperation());
            }
  */
            // Write DOT Before
            if(writeDot) {
               File dotFile = DmDottyUtils.getDottyFile(baseFilename, blockName+"-before");
               DmDottyUtils.writeDot(operations, dotFile);
            }


            // Transform

            for(Transformation t : transf) {
               // Show transformations
//               System.out.println("Transformation:"+t);
               t.transform(operations);
            }

            // Operation Stats
            totalProcessedOperations += operations.size();
            for(Operation operation : operations) {
               if(operation.getType() == OperationType.Nop) {
                  totalNops++;
               }
            }

            // Map
            mapper.reset();
            mapper.processOperations(operations);

            // Get stats after transformation
            LongTransformDataSingle afterData = DataProcess.collectTransformData(mapper, block.getRepetitions());

            //OperationListStats afterTransf = OperationListStats.buildStats(operations, mapper,
            //        block.getRepetitions(), blockName);

//            showStats(beforeTransf, afterTransf);
//            statsBefore.add(beforeTransf);
//            statsAfter.add(afterTransf);
            totalBefore.addValues(beforeData);
            totalAfter.addValues(afterData);
            /*
            System.out.println("AFTER OPERATIONS:");
            for(Operation operation : operations) {
               System.out.println(operation.getFullOperation());
            }
             */


            // Write DOT After
            if(writeDot) {
               File dotFile = DmDottyUtils.getDottyFile(baseFilename, blockName+"-after");
               DmDottyUtils.writeDot(operations, dotFile);
            }
            // Increment counter
            counter++;
            block = blockStream.nextBlock();
         }
      }

      // Collect transformation stats
      for (Transformation t : transf) {
         transformStats.showStats(t);
      }

      //showStatsAverage(statsAfter.size(), OperationListStats.calcAverage("Avg Before", statsBefore), OperationListStats.calcAverage("Avg After", statsAfter));
      //System.out.println("\nProcessed "+totalInstructions+" instructions.");
      System.err.println("\nTotals, analysed "+totalAfter.getDataCounter()+" blocks.");
      DataProcess.showTransformDataChanges(totalBefore.getTotalData(), totalAfter.getTotalData());
      System.err.println("\nAverage per block:");
      DataProcessDouble.showTransformDataChanges(totalBefore.getAverageData(), totalAfter.getAverageData());

      System.err.println("\nInserted NOPs:"+totalNops +"("+(double)totalNops/(double)totalProcessedOperations+")");
   }


/*
   private BlockStream getBlockStream(File file) {
       // Determine file extension and determine type of file
      String filename = file.getName();
      int separatorIndex = filename.lastIndexOf(IoUtils.DEFAULT_EXTENSION_SEPARATOR);
      String extension = filename.substring(separatorIndex+1);

      if(extension.equals(blockExtension)) {
         InstructionBlock block = BlockIO.fromFile(file);
         return new SingleBlockStream(block);
      }

      InstructionBusReader busReader = null;

      if(extension.equals(elfExtension)) {
         String systemConfig = "./Configuration Files/systemconfig.xml";
         busReader = ElfBusReader.createElfReader(systemConfig, file.getAbsolutePath());
      }

      if(extension.equals(traceExtension)) {
         busReader = DtoolTraceBusReader.createTraceReader(file);
      }

      if (busReader != null) {
         Partitioner partitioner = Settings.getPartitioner();
         BlockWorker worker = new BlockWorker(partitioner, busReader);
         Settings.setupBlockWorker(worker);
         return worker;
      }


      // Not of the type expected
      logger.warning("Could not process file with extension '"+extension+"'.");
      return null;
   }
*/
   private static void showStats(OperationListStats beforeTransf, OperationListStats afterTransf) {
      // only show after stats that change
//      String[] param = {"CommCosts", "Cpl", "Ilp", "Operations", "MbOperations"};
      String[] param = {"CommCosts", "Cpl", "Ilp", "Operations"};
      String[] before = {String.valueOf(beforeTransf.getCommunicationCost()),
        String.valueOf(beforeTransf.getCpl()),
        String.valueOf(beforeTransf.getIlp()),
        String.valueOf(beforeTransf.getNumberOfOperations()),
//        String.valueOf(beforeTransf.getNumberOfMbOps())
      };
      String[] after = {String.valueOf(afterTransf.getCommunicationCost()),
        String.valueOf(afterTransf.getCpl()),
        String.valueOf(afterTransf.getIlp()),
        String.valueOf(afterTransf.getNumberOfOperations()),
//        String.valueOf(afterTransf.getNumberOfMbOps())
      };


      //System.out.println("Before:");
      //System.out.println(beforeTransf);

      System.out.println("Changes:");
      boolean noChanges = true;
      for (int i = 0; i < param.length; i++) {
         if (!before[i].equals(after[i])) {
            noChanges = false;
            System.out.println(param[i]+":"+before[i]+"->"+after[i]+";");
         }
      }

      if(noChanges) {
         System.out.println("None");
      }

      System.out.println("------------------------");
   }

   private static void showStatsAverage(int size, OperationListStats beforeTransf, OperationListStats afterTransf) {

      System.out.println("\nChanges in total, analysed "+size+" blocks.");

      String[] param = {"CommCosts", "Cpl", "Ilp", "Operations"};
      String[] before = {String.valueOf(beforeTransf.getCommunicationCost()),
        String.valueOf(beforeTransf.getCpl()),
        String.valueOf(beforeTransf.getIlp()),
        String.valueOf(beforeTransf.getNumberOfOperations()),

      };
      String[] after = {String.valueOf(afterTransf.getCommunicationCost()),
        String.valueOf(afterTransf.getCpl()),
        String.valueOf(afterTransf.getIlp()),
        String.valueOf(afterTransf.getNumberOfOperations()),

      };


      //System.out.println("Changes:");
      boolean noChanges = true;
      for (int i = 0; i < param.length; i++) {
         if (!before[i].equals(after[i])) {
            noChanges = false;
            System.out.println(param[i]+":"+before[i]+"->"+after[i]+";");
         }
      }

      if(noChanges) {
         System.out.println("None");
      }

      System.out.println("------------------------");
   }

   private void writeDot(List<Operation> operations, File dotFile) {
   //private void writeDot(List<Operation> operations, String baseFilename, int index) {
/*
      File folder = IoUtils.safeFolder("dot/"+baseFilename);
      String filename = baseFilename + "-" + index + ".dot";
      File dotFile = new File(folder, filename);
*/
      
      // Processing on list ended. Removed nops before printing
      List<Operation> ops = Dotty.removeNops(operations);

      // Connect
      ops = Dotty.connectOperations(operations);

      IoUtils.write(dotFile, Dotty.generateDot(ops));
   }

   private void collectStats(Transformation t) {
      throw new UnsupportedOperationException("Not yet implemented");
   }

   /**
    * INSTANCE VARIABLES
    */
   private Logger logger;
   private String traceExtension;
   private String elfExtension;
   private String blockExtension;
   private String input;
   private Mapper mapper;
   //private Transformation[] transf;
   private List<Transformation> transf;
   private boolean writeDot;

   // STATS
   private TransformStats transformStats;






}

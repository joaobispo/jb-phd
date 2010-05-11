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
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;
import org.ancora.DMTool.deprecated.Configuration.Definitions;
import org.ancora.DMTool.Settings.Preference;
import org.ancora.DMTool.Shell.System.Executable;
import org.ancora.DMTool.Shell.System.MapperDispenser;
import org.ancora.DMTool.deprecated.PartitionerDispenser;
import org.ancora.DMTool.Shell.System.Transform.OperationListStats;
import org.ancora.DMTool.Shell.System.TransformDispenser;
import org.ancora.DMTool.deprecated.TraceProcessor.TraceProcessorWorker;
import org.ancora.DMTool.deprecated.TransformUtils;
import org.ancora.InstructionBlock.BlockIO;
import org.ancora.InstructionBlock.BlockStream;
import org.ancora.InstructionBlock.InstructionBlock;
import org.ancora.InstructionBlock.InstructionBusReader;
import org.ancora.InstructionBlock.ElfBusReader;
import org.ancora.InstructionBlock.SingleBlockStream;
import org.ancora.Partitioning.Partitioner;
import org.ancora.IrMapping.Tools.Dotty;
import org.ancora.IrMapping.Mapper;
import org.ancora.IntermediateRepresentation.Operation;
import org.ancora.SharedLibrary.IoUtils;
import org.ancora.SharedLibrary.ParseUtils;
import org.ancora.SharedLibrary.Preferences.EnumPreferences;
import org.ancora.IntermediateRepresentation.Transformation;
import org.ancora.Partitioning.Tools.BlockWorker;

/**
 *
 * @author Joao Bispo
 */
public class Transform implements Executable {

   public Transform() {
      logger = Logger.getLogger(Transform.class.getName());



   }

   private void setup() {
      EnumPreferences prefs = Preference.getPreferences();
      blockExtension = prefs.getPreference(Preference.blockExtension);
      elfExtension = prefs.getPreference(Preference.elfExtension);
      traceExtension = prefs.getPreference(Preference.traceExtension);
      mapper = MapperDispenser.getCurrentMapper();
      transf = TransformDispenser.getCurrentTransformations();
      writeDot = Boolean.parseBoolean(prefs.getPreference(Preference.transformWriteDot));
   }



   public boolean execute(List<String> arguments) {
      setup();

      if(arguments.size() < 1) {
         logger.info("Too few arguments for 'transform' ("+arguments.size()+"). Minimum is 1:");
         logger.info("transform <folder/file>");
         return false;
      }


      // Check file/folder
      File file = new File(arguments.get(0));
      if(!file.exists()) {
         logger.info("Path '"+arguments.get(0)+"' does not exist.");
         return false;
      }

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
         //inputFiles = IoUtilsAppend.getFilesRecursive(file);
         inputFiles = IoUtils.getFilesRecursive(file, supportedExtensions);
      }

      logger.info("Found "+inputFiles.size()+" files.");

//      processFiles(inputFiles);
      processFiles2(inputFiles);


      return true;
   }

   private void processFiles2(List<File> inputFiles) {
      List<OperationListStats> statsBefore = new ArrayList<OperationListStats>();
      List<OperationListStats> statsAfter = new ArrayList<OperationListStats>();

      for(File file : inputFiles) {
         logger.info("Processing file '"+file.getName()+"'...");
         String baseFilename = ParseUtils.removeSuffix(file.getName(), Definitions.EXTENSION_SEPARATOR);

         // Get BlockStream
         BlockStream blockStream = getBlockStream(file);
         InstructionBlock block = blockStream.nextBlock();
         // Start counter
         int counter = 0;
         while(block != null) {
             String blockName = baseFilename+"-"+counter;
            logger.info("Block "+counter+", "+block.getRepetitions()+" repetitions.");

            // Transform Instruction Block into PureIR
            List<Operation> operations = TransformUtils.mbToPureIr(block);

            if(operations == null) {
               continue;
            }

            // Get stats before transformations
            OperationListStats beforeTransf = OperationListStats.buildStats(operations, mapper,
                    block.getRepetitions(), blockName);
/*
            // Show operations before
            System.out.println("BEFORE OPERATIONS:");
            for(Operation operation : operations) {
               System.out.println(operation.getFullOperation());
            }
  */
            // Write DOT Before
            if(writeDot) {
               File folder = IoUtils.safeFolder("dot/"+baseFilename);
               String filename = baseFilename + "-" + counter + "-before.dot";
               writeDot(operations, new File(folder, filename));
            }


            // Transform
            for(Transformation t : transf) {
               // Show transformations
               System.out.println("Transformation:"+t);
               //operations = t.transform(operations);
               t.transform(operations);
            }


            // Get stats after transformation
            OperationListStats afterTransf = OperationListStats.buildStats(operations, mapper,
                    block.getRepetitions(), blockName);

            showStats(beforeTransf, afterTransf);
            statsBefore.add(beforeTransf);
            statsAfter.add(afterTransf);
            /*
            System.out.println("AFTER OPERATIONS:");
            for(Operation operation : operations) {
               System.out.println(operation.getFullOperation());
            }
             */


            // Write DOT After
            if(writeDot) {
               File folder = IoUtils.safeFolder("dot/"+baseFilename);
               String filename = baseFilename + "-" + counter + "-after.dot";
               writeDot(operations, new File(folder, filename));
            }
            // Increment counter
            counter++;
         }
      }
   }

   
   private void processFiles(List<File> inputFiles) {
      List<OperationListStats> statsBefore = new ArrayList<OperationListStats>();
      List<OperationListStats> statsAfter = new ArrayList<OperationListStats>();

      for(File file : inputFiles) {
         logger.info("Processing file '"+file.getName()+"'...");
         String baseFilename = ParseUtils.removeSuffix(file.getName(), Definitions.EXTENSION_SEPARATOR);


         // Get InstructionBlocks
         List<InstructionBlock> blocks = getBlocks(file);
         for(int i=0; i<blocks.size(); i++) {
            String blockName = baseFilename+"-"+i;

            InstructionBlock block = blocks.get(i);
            logger.info("Block "+i+", "+block.getRepetitions()+" repetitions.");

            // Transform Instruction Block into PureIR
            List<Operation> operations = TransformUtils.mbToPureIr(block);

            if(operations == null) {
               continue;
            }

            // Get stats before transformations
            OperationListStats beforeTransf = OperationListStats.buildStats(operations, mapper,
                    block.getRepetitions(), blockName);
/*
            // Show operations before
            System.out.println("BEFORE OPERATIONS:");
            for(Operation operation : operations) {
               System.out.println(operation.getFullOperation());
            }
  */
            // Write DOT Before
            if(writeDot) {
               File folder = IoUtils.safeFolder("dot/"+baseFilename);
               String filename = baseFilename + "-" + i + "-before.dot";
               writeDot(operations, new File(folder, filename));
            }


            // Transform
            for(Transformation t : transf) {
               // Show transformations
               System.out.println("Transformation:"+t);
               //operations = t.transform(operations);
               t.transform(operations);
            }


            // Get stats after transformation
            OperationListStats afterTransf = OperationListStats.buildStats(operations, mapper,
                    block.getRepetitions(), blockName);

            showStats(beforeTransf, afterTransf);
            statsBefore.add(beforeTransf);
            statsAfter.add(afterTransf);
            /*
            System.out.println("AFTER OPERATIONS:");
            for(Operation operation : operations) {
               System.out.println(operation.getFullOperation());
            }
             */
             

            // Write DOT After
            if(writeDot) {
               File folder = IoUtils.safeFolder("dot/"+baseFilename);
               String filename = baseFilename + "-" + i + "-after.dot";
               writeDot(operations, new File(folder, filename));
            }


         }
         

      }

      // Calculate average
//      System.out.println("Average Stats Before Transformations:");
//      OperationListStats.calcAverage("Avg Before", statsBefore);
//      System.out.println("Average Stats After Transformations:");
//      OperationListStats.calcAverage("Avg After", statsAfter);
      showStatsAverage(statsAfter.size(), OperationListStats.calcAverage("Avg Before", statsBefore), OperationListStats.calcAverage("Avg After", statsAfter));
   }

   private List<InstructionBlock> getBlocks(File file) {

      // Determine file extension and determine type of file
      String filename = file.getName();
      int separatorIndex = filename.lastIndexOf(Definitions.EXTENSION_SEPARATOR);
      String extension = filename.substring(separatorIndex+1);

      if(extension.equals(blockExtension)) {
         return TransformUtils.blockLoader(file);
      }

      if(extension.equals(elfExtension)) {
         String systemConfig = "./Configuration Files/systemconfig.xml";
         InstructionBusReader busReader = ElfBusReader.createElfReader(systemConfig, file.getAbsolutePath());
         TraceProcessorWorker worker = getProcessorWorker(busReader);
         return worker.processTrace(busReader);
      }

      if(extension.equals(traceExtension)) {
         InstructionBusReader busReader = MbTraceReader.createTraceReader(file);
         TraceProcessorWorker worker = getProcessorWorker(busReader);
         return worker.processTrace(busReader);
      }

      // Not of the type expected
      logger.warning("Could not process file with extension '"+extension+"'.");
      return new ArrayList<InstructionBlock>();
   }

   private BlockStream getBlockStream(File file) {
       // Determine file extension and determine type of file
      String filename = file.getName();
      int separatorIndex = filename.lastIndexOf(Definitions.EXTENSION_SEPARATOR);
      String extension = filename.substring(separatorIndex+1);

      if(extension.equals(blockExtension)) {
         InstructionBlock block = BlockIO.fromFile(file);
         return new SingleBlockStream(block);
      }

      if(extension.equals(elfExtension)) {
         String systemConfig = "./Configuration Files/systemconfig.xml";
         InstructionBusReader busReader = ElfBusReader.createElfReader(systemConfig, file.getAbsolutePath());
         return new BlockWorker(null, busReader)

         TraceProcessorWorker worker = getProcessorWorker(busReader);
         return worker.processTrace(busReader);
      }

      if(extension.equals(traceExtension)) {
         InstructionBusReader busReader = MbTraceReader.createTraceReader(file);
         TraceProcessorWorker worker = getProcessorWorker(busReader);
         return worker.processTrace(busReader);
      }

      // Not of the type expected
      logger.warning("Could not process file with extension '"+extension+"'.");
      return new ArrayList<InstructionBlock>();
   }

   private TraceProcessorWorker getProcessorWorker(InstructionBusReader busReader) {
      EnumPreferences prefs = Preference.getPreferences();

      // Get the partitioner
      Partitioner partitioner = PartitionerDispenser.getCurrentPartitioner();
      TraceProcessorWorker worker = new TraceProcessorWorker(partitioner);

      // Setup worker
      boolean useGatherer = Boolean.parseBoolean(prefs.getPreference(Preference.useGatherer));
      boolean useSelector = Boolean.parseBoolean(prefs.getPreference(Preference.useSelector));
      int selectorThreshold = ParseUtils.parseInt(prefs.getPreference(Preference.selectorThreshold));

      worker.setUseGatherer(useGatherer);
      worker.setUseSelector(useSelector);
      worker.setSelectorRepThreshold(selectorThreshold);

      return worker;
   }

   private TraceProcessorWorker getBlockWorker(InstructionBusReader busReader) {
      EnumPreferences prefs = Preference.getPreferences();

      // Get the partitioner
      Partitioner partitioner = PartitionerDispenser.getCurrentPartitioner();
      TraceProcessorWorker worker = new TraceProcessorWorker(partitioner);

      // Setup worker
      boolean useGatherer = Boolean.parseBoolean(prefs.getPreference(Preference.useGatherer));
      boolean useSelector = Boolean.parseBoolean(prefs.getPreference(Preference.useSelector));
      int selectorThreshold = ParseUtils.parseInt(prefs.getPreference(Preference.selectorThreshold));
      
      worker.setUseGatherer(useGatherer);
      worker.setUseSelector(useSelector);
      worker.setSelectorRepThreshold(selectorThreshold);

      return worker;
   }

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

   /**
    * INSTANCE VARIABLES
    */
   private Logger logger;
   private String traceExtension;
   private String elfExtension;
   private String blockExtension;
   private Mapper mapper;
   private Transformation[] transf;
   private boolean writeDot;






}

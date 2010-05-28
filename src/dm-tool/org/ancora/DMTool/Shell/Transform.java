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


      // Check file/folder
      File file = new File(input);
      if(!file.exists()) {
         logger.warning("Input '"+input+"' does not exist.");
         return false;
      }

      // Build list of file extensions
      java.util.Set<String> supportedExtensions = new HashSet<String>();
      supportedExtensions.add(blockExtension);
      supportedExtensions.add(elfExtension);
      supportedExtensions.add(traceExtension);

      // Get files
      List<File> inputFiles = Settings.getInputFiles(supportedExtensions);


      logger.info("Found "+inputFiles.size()+" files.");

      processFiles(inputFiles);


      return true;
   }

   private void processFiles(List<File> inputFiles) {
      LongTransformDataTotal totalBefore = new LongTransformDataTotal();
      LongTransformDataTotal totalAfter = new LongTransformDataTotal();

      long totalProcessedOperations = 0l;
      long totalNops = 0l;

      for(File file : inputFiles) {
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



            // Write DOT Before
            if(writeDot) {
               File dotFile = DmDottyUtils.getDottyFile(baseFilename, blockName+"-before");
               DmDottyUtils.writeBlockDot(operations, dotFile);
            }


            // Transform
            for(Transformation t : transf) {
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

            totalBefore.addValues(beforeData);
            totalAfter.addValues(afterData);


            // Write DOT After
            if(writeDot) {
               File dotFile = DmDottyUtils.getDottyFile(baseFilename, blockName+"-after");
               DmDottyUtils.writeBlockDot(operations, dotFile);
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

      System.err.println("\nTotals, analysed "+totalAfter.getDataCounter()+" blocks.");
      DataProcess.showTransformDataChanges(totalBefore.getTotalData(), totalAfter.getTotalData());
      System.err.println("\nAverage per block:");
      DataProcessDouble.showTransformDataChanges(totalBefore.getAverageData(), totalAfter.getAverageData());

      System.err.println("\nInserted NOPs:"+totalNops +"("+(double)totalNops/(double)totalProcessedOperations+")");
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

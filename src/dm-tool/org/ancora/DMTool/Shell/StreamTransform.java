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
import org.ancora.DMTool.Stats.BlockSize.BlockSizeCounter;
import org.ancora.DMTool.Stats.LongTransformDataTotal;
import org.ancora.Partitioning.Blocks.BlockStream;
import org.ancora.DMTool.System.Services.DmBlockUtils;
import org.ancora.InstructionBlock.InstructionBlock;
import org.ancora.IntermediateRepresentation.MbParser;
import org.ancora.IntermediateRepresentation.Operation;
import org.ancora.IntermediateRepresentation.OperationType;
import org.ancora.SharedLibrary.IoUtils;
import org.ancora.SharedLibrary.ParseUtils;
import org.ancora.DMTool.System.Services.DmDottyUtils;
import org.ancora.DMTool.Dispensers.DmStreamMapperDispenser;
import org.ancora.FuMatrix.Architecture.Fu;
import org.ancora.FuMatrix.Architecture.FuCoor;
import org.ancora.FuMatrix.Architecture.FuOutputSignal;
import org.ancora.FuMatrix.Architecture.Signal;
import org.ancora.FuMatrix.Architecture.Signal.SignalType;
import org.ancora.FuMatrix.Mapper.GeneralMapper;
import org.ancora.DMTool.Dispensers.DmStreamTransformDispenser;
import org.ancora.FuMatrix.Stats.IterationData;
import org.ancora.FuMatrix.Stats.MapperData;
import org.ancora.FuMatrix.Stats.NopData;
import org.ancora.StreamTransform.RemoveR0Or;
import org.ancora.StreamTransform.SingleStaticAssignment;
import org.ancora.StreamTransform.Stats.TransformationChanges;

/**
 *
 * @author Joao Bispo
 */
public class StreamTransform implements Executable {

   public StreamTransform() {
   }

   private void setup() {
      blockExtension = Options.optionsTable.get(OptionName.extension_block);
      elfExtension = Options.optionsTable.get(OptionName.extension_elf);
      traceExtension = Options.optionsTable.get(OptionName.extension_trace);
      //transf = DmTransformDispenser.getCurrentTransformations();
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
      NopData nopsBefore = new NopData();
      NopData nopsAfter = new NopData();
      //long totalNopsBefore = 0l;
      //long totalNopsAfter = 0l;

      BlockSizeCounter blockSizeCounterAfter = new BlockSizeCounter();
      BlockSizeCounter blockSizeCounterBefore = new BlockSizeCounter();


      // Stats
      TransformationChanges totalFrequencies = new TransformationChanges();
      IterationData totalIterationDataBefore = new IterationData();
      IterationData totalIterationDataAfter = new IterationData();

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
//             logger.warning("Processing Block '"+blockName+"'");
//            System.err.println(block.toString());
//            logger.info("Block "+counter+", "+block.getRepetitions()+" repetitions.");

            // Transform Instruction Block into PureIR
            //List<Operation> operations = MbParser.mbToPureIr(block);
            List<Operation> operations = MbParser.mbToOperations(block);

            if(operations == null) {
               continue;
            }

            // Transform to SSA
            SingleStaticAssignment.transform(operations);
            // Apply pre-transformations, before collecting data
            DmStreamTransformDispenser.applyPreTransformations(operations);
            //RemoveR0Or.transform(operations);

            // Get stats before transformations
            // Map
            GeneralMapper beforeMapper = DmStreamMapperDispenser.applyCurrentMapper(operations);
            if (beforeMapper == null) {
               Logger.getLogger(StreamTransform.class.getName()).
                       warning("Could not map block '" + blockName + "' before "
                       + "transformations.");

            counter++;
            block = blockStream.nextBlock();
            continue;
            }
            // Get stats before transformation
            IterationData localBefore = IterationData.build(MapperData.build(beforeMapper), block.getRepetitions());
            totalIterationDataBefore.addIterationData(localBefore);

            // Count nops before transformation
            nopsBefore.addMapping(operations, block.getRepetitions());
            /*
            long localNops = 0l;
            for (Operation operation : operations) {
               if (operation.getType() == OperationType.Nop) {
                  localNops++;
               }
            }
            localNops = localNops * block.getRepetitions();
            totalNopsBefore += localNops;
             *
             */
 
       

            // Show Mapping
            testMapping(beforeMapper.getMappedOps());

            
            blockSizeCounterBefore.processBlock(operations);

            // Write DOT Before
            if(writeDot) {
               File dotFile = DmDottyUtils.getDottyFile(baseFilename, blockName+"-before");
               DmDottyUtils.writeBlockDot(operations, dotFile);
            }


            // Transform
            TransformationChanges blockTotalFrequencies = DmStreamTransformDispenser.applyCurrentTransformations(operations);
            totalFrequencies.addOperationFrequency(blockTotalFrequencies);

            

            // Operation Stats
            totalProcessedOperations += operations.size();
            for(Operation operation : operations) {
               if(operation.getType() == OperationType.Nop) {
                  //System.err.println("Nopped:"+((Nop)operation).getOperation().getFullOperation());
                  totalNops++;
               }
            }
            // Count nops after transformation
            /*
            localNops = 0l;
            for (Operation operation : operations) {
               if (operation.getType() == OperationType.Nop) {
                  localNops++;
               }
            }
            totalNopsAfter += localNops * block.getRepetitions();
*/
            nopsAfter.addMapping(operations, block.getRepetitions());

            // Map
            GeneralMapper afterMapper = DmStreamMapperDispenser.applyCurrentMapper(operations);
            if (afterMapper == null) {
               Logger.getLogger(StreamTransform.class.getName()).
                       warning("Could not map block '" + blockName + "' after "
                       + "transformations.");
                           // Increment counter
            counter++;
            block = blockStream.nextBlock();
               continue;
            }
            // Get stats after transformation
            IterationData localAfter = IterationData.build(MapperData.build(afterMapper), block.getRepetitions());
            totalIterationDataAfter.addIterationData(localAfter);

           

            //System.err.println("Testing Mapping After Transformations:");
            testMapping(afterMapper.getMappedOps());
            //testMapping(beforeMapper.getMappedOps());



            blockSizeCounterAfter.processBlock(operations);

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

      // Show transformation stats
      System.err.println("Transformations Stats:");
      System.err.println(totalFrequencies.toString());
      /*
      for (Transformation t : transf) {
         transformStats.showStats(t);
      }
       *
       */

      //System.err.println("\nTotals, analysed "+totalAfter.getDataCounter()+" blocks.");
      //DataProcess.showTransformDataChanges(totalBefore.getTotalData(), totalAfter.getTotalData());
      //System.err.println("\nAverage per block:");
      //DataProcessDouble.showTransformDataChanges(totalBefore.getAverageData(), totalAfter.getAverageData());

      System.err.println("\nInserted NOPs:"+totalNops +"("+(double)totalNops/(double)totalProcessedOperations+")");
      long transfNopsMapped = nopsAfter.getMappedNops() - nopsBefore.getMappedNops();
      long transfNopsExecuted = nopsAfter.getExecutedNops() - nopsBefore.getExecutedNops();
      //totalNopsAfter - totalNopsBefore;
      System.err.println("Mapped NOPs:"+transfNopsMapped+"("+(double)transfNopsMapped/(double)totalIterationDataBefore.getTotalMappedOps()+")");
      System.err.println("Executed NOPs:"+transfNopsExecuted+"("+(double)transfNopsExecuted/(double)totalIterationDataBefore.getTotalExecutedOps()+")");

/*
      System.err.println("NOPs Before:"+totalNopsBefore);
      System.err.println("NOPs After:"+totalNopsAfter);
      System.err.println("Transformation NOPs:"+nopsByTransformations);
      System.err.println("Executed Ops Before:"+totalIterationDataBefore.getTotalExecutedOps());
      System.err.println("Executed Ops After:"+totalIterationDataAfter.getTotalExecutedOps());
      System.err.println("Executed Ops Difference:"+(totalIterationDataBefore.getTotalExecutedOps()-totalIterationDataAfter.getTotalExecutedOps()));
*/

      System.err.println("Block Size Before:");
      System.err.println(blockSizeCounterBefore.toString());

      System.err.println("Block Size After:");
      System.err.println(blockSizeCounterAfter.toString());


      //System.err.println("Before Average Block Size:"+blockSizeCounterBefore.getAverageSize());
      //System.err.println("Before Max Block Size:"+blockSizeCounterBefore.getMaxBlockSize());

      //System.err.println("After Average Block Size:"+blockSizeCounterAfter.getAverageSize());
      //System.err.println("After Max Block Size:"+blockSizeCounterAfter.getMaxBlockSize());

      System.err.println("Iteration Before:");
      System.err.println(totalIterationDataBefore.averagesString());
      System.err.println("Iteration After:");
      System.err.println(totalIterationDataAfter.averagesString());
   }


   /**
    * INSTANCE VARIABLES
    */
   private static final Logger logger = Logger.getLogger(StreamTransform.class.getName());
   private String traceExtension;
   private String elfExtension;
   private String blockExtension;
   private String input;
   //private StreamMapper mapper;
   //private Transformation[] transf;
   //private List<Transformation> transf;
   private boolean writeDot;

   public static void showFus(List<Fu> mappedOps) {
         // Build matrix
         List<List<String>> matrix = new ArrayList<List<String>>();

         int maxCol = 0;
         for (int i = 0; i < mappedOps.size(); i++) {
            Fu fu = mappedOps.get(i);
            // Get line
            FuCoor coor = fu.getCoordinate();
            int line = coor.getLine();
            if(line == matrix.size()) {
               matrix.add(new ArrayList<String>());
            }
            
            List<String> operationLine = matrix.get(line);
            /*
            if(operationLine == null) {
               operationLine = new ArrayList<Operation>();
            }
             */
            // Check column - Ignore this for now.
            /*
            if(coor.getCol() != operationLine.size()) {
               System.err.println("Col '"+coor.getCol()+"' diferent from size '"+operationLine.size()+"'");
            }
             *
             */
            operationLine.add(fu.getOperationName());
            maxCol = Math.max(maxCol, operationLine.size());
            //System.err.println(buildMatrixString(matrix));
         }


         System.err.println("Mapping:");
         System.err.println(buildMatrixString(matrix));
         System.err.println("Max Lines:"+matrix.size());
         System.err.println("Max Cols:"+maxCol);
//         System.err.println("Max Area Size:"+(maxCol*matrix.size()));
   }

   public static String buildMatrixString(List<List<String>> matrix) {
      StringBuilder builder = new StringBuilder();
      for(int i=0; i<matrix.size(); i++) {
         builder.append("Line ");
         builder.append(i);
         builder.append(":");

         for(String operation : matrix.get(i)) {
            builder.append(" '");
            builder.append(operation.toString());
            builder.append("'");
         }
         builder.append("\n");
      }

      return builder.toString();
   }

   private void testMapping(List<Fu> mappedOps) {
      // TEST 1: Communications in the same line
      for(Fu fu : mappedOps) {
         int fuLine = fu.getCoordinate().getLine();
         for(Signal signal : fu.getInputs()) {
            if(signal.getType() == SignalType.fuOutput) {
               int inputLine = ((FuOutputSignal)signal).getCoordinate().getLine();
               if(inputLine >= fuLine) {
                  Logger.getLogger(StreamTransform.class.getName()).
                          warning("Failed test 1. Fu '"+fu+"' on line "+fuLine+" " +
                          "receives input from Fu on line '"+inputLine+"'");
                  return;
               }
            }
         }
      }

      // TEST 2: Every load must come after the last store (most conservative assumption)
      int lineOfLastStore = -1;
      for(Fu fu : mappedOps) {
         OperationType opType = fu.getOperationType();
         if(opType.isStore()) {
            lineOfLastStore = fu.getCoordinate().getLine();
         } else if(opType.isLoad()) {
            int loadLine = fu.getCoordinate().getLine();
            if(loadLine <= lineOfLastStore) {
               Logger.getLogger(StreamTransform.class.getName()).
                       warning("Failed test 2. Fu '" + fu + "' on line " + loadLine + " "
                       + "loads after store on line '" + lineOfLastStore + "'");
               return;
            }
         }
      }

   }

   // STATS
   //private TransformStats transformStats;

}

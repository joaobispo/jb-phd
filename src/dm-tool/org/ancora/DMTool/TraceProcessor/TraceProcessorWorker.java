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

package org.ancora.DMTool.TraceProcessor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.ancora.DMTool.TransformStudy.NamedBlock;
import org.ancora.InstructionBlock.InstructionBlock;
import org.ancora.InstructionBlock.InstructionBlockProducer;
import org.ancora.InstructionBlock.InstructionBusReader;
import org.ancora.InstructionBlock.Listeners.InstructionBlockCollector;
import org.ancora.InstructionBlock.Listeners.InstructionBlockStats;
import org.ancora.InstructionBlock.MbInstructionBlockWriter;
import org.ancora.InstructionBlock.MbTraceReader;
import org.ancora.Partitioning.BasicBlock;
import org.ancora.Partitioning.MbJumpFilter;
import org.ancora.Partitioning.MegaBlock;
import org.ancora.Partitioning.Partitioner;
import org.ancora.Partitioning.SuperBlock;
import org.ancora.Partitioning.Tools.Gatherer;
import org.ancora.Partitioning.Tools.InstructionFilter;
import org.ancora.Partitioning.Tools.Selector;
import org.ancora.MicroBlaze.Trace.TraceUtils;

/**
 * Given a trace, returns a list of InstructionBlocks.
 * Can be setup in a number of ways.
 *
 * TODO: Move this class to InstructionBlock package
 *
 * @author Joao Bispo
 */
public class TraceProcessorWorker {

   public TraceProcessorWorker(Partitioner partitioner) {
      // Default partitioner: basicblock
      //partitionerType = PartitionerType.BasicBlock;
      this.partitioner = partitioner;
      useGatherer = false;
      useSelector = false;
      selectorRepThreshold = 1;
//      useWriter = false;
   }



   //public List<NamedBlock> processTrace(String baseFilename, InstructionBusReader busReader) {
   public List<InstructionBlock> processTrace(InstructionBusReader busReader) {
      //InstructionBlockProducer lastProducer = setupObjects(trace);
//      InstructionBlockProducer lastProducer = setupObjects(baseFilename);
      InstructionBlockProducer lastProducer = setupObjects();

      // Using MicroBlaze Trace Reader by default
      //InstructionBusReader busReader = MbTraceReader.createTraceReader(trace);
      InstructionBlockStats ibStats = new InstructionBlockStats();
      InstructionBlockCollector collector = new InstructionBlockCollector();

      // Connect stats to partitionerand
      partitioner.addListener(ibStats);
      // Connect collector to end of the line
      lastProducer.addListener(collector);
      // Process trace
      partitioner.run(busReader);

      // Do some basic checks
      checkInstructionNumber(busReader, ibStats);


      // Return instruction blocks
      return collector.getBlocks();
      /*
      List<NamedBlock> blocks = new ArrayList<NamedBlock>();
      int counter = 0;
      for(InstructionBlock block : collector.getBlocks()) {
         // Build name
         String blockName = baseFilename+"-"+counter;
         blocks.add(new NamedBlock(block, blockName));
         counter++;
      }

      return blocks;
       */
   }
   // IDEIA: Manter booleans para configurar o "run", e quando se faz run,
   // Constroi objectos e faz ligações.

   // Returns: List of InstructionBlocks

   // Opções:
   // Partitioner - fornecido pelo user
   // Gatherer - on/off
   // Selector - on/off
   // Write Blocks - on/off

   private InstructionBlockProducer setupObjects() {
   //private InstructionBlockProducer setupObjects(String baseFilename) {
   //private InstructionBlockProducer setupObjects(File trace) {
      InstructionBlockProducer lastProducer;

      // Setup Partitioner
      /*
      switch(partitionerType) {
         case BasicBlock:
            partitioner = new BasicBlock(DEFAULT_JUMP_FILTER);
            break;
         case SuperBlock:
            partitioner = new SuperBlock(DEFAULT_JUMP_FILTER);
            break;
         case MegaBlock:
            partitioner = new MegaBlock(DEFAULT_JUMP_FILTER, maxSuperBlockPatternSize);
            break;
         default:
            Logger.getLogger(TraceProcessorWorker.class.getName()).
                    warning("Partitioner '"+partitionerType+"' not defined. Using default " +
                    "partitioner '"+partitioner.getName()+"'");
      }
       */
      lastProducer = partitioner;

      // Setup Gatherer
      if(useGatherer) {
         gatherer = new Gatherer();
         lastProducer.addListener(gatherer);
         lastProducer = gatherer;
      }

      // Setup Selector
      if(useSelector) {
         // Check for use of Gatherer
         if(!useGatherer) {
            Logger.getLogger(TraceProcessorWorker.class.getName()).
                    warning("Using Selector but not Gatherer; the objective of Selector " +
                    "is to filter blocks with repetitions below a certain number, which are found " +
                    "by Gatherer. Otherwise, all instruction blocks have repetition of value '1'.");
         }
         selector = new Selector(selectorRepThreshold);
         lastProducer.addListener(selector);
         lastProducer = selector;
      }

      // Setup Writer
      /*
      if(useWriter) {
        //          String EXTENSION_SEPARATOR = ".";
        // int lastIndexOfSeparator = trace.getName().lastIndexOf(EXTENSION_SEPARATOR);
        // String baseFilename = trace.getName().substring(0, lastIndexOfSeparator);
         ibWriter = new MbInstructionBlockWriter(baseFilename);
         //ibWriter = new MbInstructionBlockWriter(trace.getName());
         lastProducer.addListener(ibWriter);
      }
       */

      return lastProducer;
   }

   private boolean checkInstructionNumber(InstructionBusReader busReader, InstructionBlockStats ibStats) {
      // Check if Partitioned Instructions Add Up
      long blockInst = ibStats.getTotalInstructions();
      long traceInst = busReader.getInstructions();

      if (blockInst != traceInst) {
         Logger.getLogger(TraceProcessorWorker.class.getName()).
                 warning("Total instructions does not add up: Trace(" + traceInst + ") " +
                 "vs. Partitioner(" + blockInst + ")");
         return false;
      }

      return true;

   }

   public void setUseGatherer(boolean useGatherer) {
      this.useGatherer = useGatherer;
   }

   public void setUseSelector(boolean useSelector) {
      this.useSelector = useSelector;
   }

   public void setSelectorRepThreshold(int selectorRepThreshold) {
      this.selectorRepThreshold = selectorRepThreshold;
   }




   /**
    * INSTANCE VARIABLES
    */
   //private Partitioner partitioner;
   private Gatherer gatherer;
   private Selector selector;
   //private MbInstructionBlockWriter ibWriter;

   // Choices
   private Partitioner partitioner;
   //private PartitionerType partitionerType;
   //private int maxSuperBlockPatternSize;
   private boolean useGatherer;
   private boolean useSelector;
   private int selectorRepThreshold;
   //boolean useWriter;

   //private InstructionFilter DEFAULT_JUMP_FILTER = new MbJumpFilter();



/*
   public enum PartitionerType {
      BasicBlock,
      SuperBlock,
      MegaBlock;
   }
 */
}

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

package org.ancora.Partitioning.Tools;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.ancora.Partitioning.Blocks.BlockStream;
import org.ancora.InstructionBlock.GenericInstruction;
import org.ancora.InstructionBlock.InstructionBlock;
import org.ancora.InstructionBlock.InstructionBlockProducer;
import org.ancora.InstructionBlock.InstructionBusReader;
import org.ancora.InstructionBlock.Listeners.InstructionBlockCollector;
import org.ancora.InstructionBlock.Listeners.InstructionBlockStats;
import org.ancora.Partitioning.Partitioner;

/**
 * Given a trace, returns a list of InstructionBlocks.
 * Can be setup in a number of ways.
 *
 * TODO: Move this class to InstructionBlock package
 *
 * @author Joao Bispo
 */
public class BlockWorkerStream implements BlockStream, BlockWorker {

   public BlockWorkerStream(Partitioner partitioner, InstructionBusReader busReader) {
      this.partitioner = partitioner;
      useGatherer = false;
      useSelector = false;
      useUniqueFilter = false;
      selectorRepThreshold = 1;

      this.busReader = busReader;

     
   }

   public void init() {
      blocks = new ArrayList<InstructionBlock>();
      collector = new InstructionBlockCollector();
      ibStats = new InstructionBlockStats();
      flushed = false;

      InstructionBlockProducer lastProducer = setupObjects();
      // Using MicroBlaze Trace Reader by default
      //InstructionBusReader busReader = MbTraceReader.createTraceReader(trace);
      //InstructionBlockStats ibStats = new InstructionBlockStats();
      //InstructionBlockCollector collector = new InstructionBlockCollector();

      // Connect stats to partitionerand
      partitioner.addListener(ibStats);
      // Connect collector to end of the line
      lastProducer.addListener(collector);
   }

   public InstructionBlock nextBlock() {
      // Feed instructions until a block appear in the collector
      while(true) {
         GenericInstruction inst = busReader.nextInstruction();

         // This means the stream has ended
         if(inst == null) {
            if(!flushed) {
               // Flush partitioner
               partitioner.flush();
               // Add InstructionBlocks from collector
               blocks.addAll(collector.popBlocks());
               // Do some basic checks
      checkInstructionNumber(busReader, ibStats);
               flushed = true;
            }
            // Pop a block from the list
            return popBlock();
         }

         // Feed inst to partitioner
         partitioner.acceptInstruction(inst);
         // Add collector to current blocks
         blocks.addAll(collector.popBlocks());

         // Continue until there is something to collect
         InstructionBlock block = popBlock();
         if(block != null) {
            return block;
         }
      }
      
   }

   private InstructionBlock popBlock() {
      if(blocks.isEmpty()) {
         return null;
      }

      return blocks.remove(0);
   }
/*
   public List<InstructionBlock> processTrace(InstructionBusReader busReader) {
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

   }
 *
 */
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
            Logger.getLogger(BlockWorkerStream.class.getName()).
                    warning("Using Selector but not Gatherer; the objective of Selector " +
                    "is to filter blocks with repetitions below a certain number, which are found " +
                    "by Gatherer. Otherwise, all instruction blocks have repetition of value '1'.");
         }
         selector = new Selector(selectorRepThreshold);
         lastProducer.addListener(selector);
         lastProducer = selector;
      }

      // Setup Unique Filter
      if(useUniqueFilter) {
         filterUnique = new UniqueBlocks();
         lastProducer.addListener(filterUnique);
         lastProducer = filterUnique;
      }

      return lastProducer;
   }

   private boolean checkInstructionNumber(InstructionBusReader busReader, InstructionBlockStats ibStats) {
      // Check if Partitioned Instructions Add Up
      long blockInst = ibStats.getTotalInstructions();
      long traceInst = busReader.getInstructions();

      if (blockInst != traceInst) {
         Logger.getLogger(BlockWorkerStream.class.getName()).
                 warning("Total instructions does not add up: Trace(" + traceInst + ") " +
                 "vs. Partitioner(" + blockInst + ")");
         return false;
      } else {
         Logger.getLogger(BlockWorkerStream.class.getName()).
                 info("Processed "+blockInst+" instructions.");
      }
      /*
      else {
         System.out.println("Checks ok: Trace(" + traceInst + ") " +
                 "vs. Partitioner(" + blockInst + ")");
      }
*/
      return true;

   }

   public void setUseGatherer(boolean useGatherer) {
      this.useGatherer = useGatherer;
   }

   public void setUseUniqueFilter(boolean useUniqueFilter) {
      this.useUniqueFilter = useUniqueFilter;
   }

   public void setUseSelector(boolean useSelector) {
      this.useSelector = useSelector;
   }

   public void setSelectorRepThreshold(int selectorRepThreshold) {
      this.selectorRepThreshold = selectorRepThreshold;
   }


   public String getPartitionerName() {
      return partitioner.getName();
   }

      public long getTotalInstructions() {
      return busReader.getInstructions();
   }

   /**
    * INSTANCE VARIABLES
    */
   private Gatherer gatherer;
   private Selector selector;
   private UniqueBlocks filterUnique;
   InstructionBlockCollector collector;
   InstructionBlockStats ibStats;


   // Choices
   private Partitioner partitioner;
   private boolean useGatherer;
   private boolean useSelector;
   private boolean useUniqueFilter;
   private int selectorRepThreshold;

   private InstructionBusReader busReader;

   private List<InstructionBlock> blocks;

   private boolean flushed;






}

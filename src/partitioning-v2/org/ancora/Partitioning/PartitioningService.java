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

package org.ancora.Partitioning;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.ancora.InstructionBlock.GenericInstruction;
import org.ancora.InstructionBlock.InstructionBlock;
import org.ancora.InstructionBlock.InstructionBlockProducer;
import org.ancora.InstructionBlock.Listeners.InstructionBlockCollector;
import org.ancora.InstructionBlock.Listeners.InstructionBlockStats;
import org.ancora.Partitioning.Services.Gatherer;
import org.ancora.Partitioning.Services.Selector;
import org.ancora.Partitioning.Services.UniqueBlocks;

/**
 * Given a trace, returns a list of InstructionBlocks.
 * Can be setup in a number of ways.
 *
 * TODO: Move this class to InstructionBlock package
 *
 * @author Joao Bispo
 */
public class PartitioningService {

   public PartitioningService(Partitioner partitioner, PartitioningConfig config) {
      this.partitioner = partitioner;
      this.config = config;
      /*
      useGatherer = false;
      useSelector = false;
      useUniqueFilter = false;
      selectorRepThreshold = 1;
       * 
       */
     init();
   }

   /*
   public String getPartitionerName() {
      return partitioner.getName();
   }
    * 
    */

   private void init() {
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

   public void flush() {
      if(!flushed) {
         // Flush partitioner
         partitioner.flush();
         // Add InstructionBlocks from collector
         blocks.addAll(collector.popBlocks());
         // Do some basic checks
//         checkInstructionNumber(busReader, ibStats);
         flushed = true;
      }
   }

   public List<InstructionBlock> popBlocks() {
      if(blocks.isEmpty()) {
         return blocks;
      }

      // Return a new list, discard current and create new
      List<InstructionBlock> temp = blocks;
      blocks = new ArrayList<InstructionBlock>();
      return temp;
   }



   public void acceptInstruction(GenericInstruction inst) {
      if(inst == null) {
         Logger.getLogger(PartitioningService.class.getName()).
                 warning("Giving a null instruction to worker.");
         return;
      }

      // Feed inst to partitioner
      partitioner.acceptInstruction(inst);
      // Add collector to current blocks
      blocks.addAll(collector.popBlocks());

      
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

      lastProducer = partitioner;

      // Setup Gatherer
      if(config.useGatherer()) {
         gatherer = new Gatherer();
         lastProducer.addListener(gatherer);
         lastProducer = gatherer;
      }

      // Setup Selector
      if(config.useSelector()) {
         // Check for use of Gatherer
         if(!config.useGatherer()) {
            Logger.getLogger(PartitioningService.class.getName()).
                    warning("Using Selector but not Gatherer; the objective of Selector " +
                    "is to filter blocks with repetitions below a certain number, which are found " +
                    "by Gatherer. Otherwise, all instruction blocks have repetition of value '1'.");
         }
         selector = new Selector(config.getSelectorRepThreshold());
         lastProducer.addListener(selector);
         lastProducer = selector;
      }

      // Setup Unique Filter
      if(config.useUniqueFilter()) {
         filterUnique = new UniqueBlocks();
         lastProducer.addListener(filterUnique);
         lastProducer = filterUnique;
      }

      return lastProducer;
   }

   public long getTotalInstructions() {
      return ibStats.getTotalInstructions();
   }
   /*
   public boolean checkInstructionNumber(long traceInst) {
      // Check if Partitioned Instructions Add Up
      long blockInst = ibStats.getTotalInstructions();
      //long traceInst = busReader.getInstructions();

      if (blockInst != traceInst) {
         Logger.getLogger(PartitioningService.class.getName()).
                 warning("Total instructions does not add up: Trace(" + traceInst + ") " +
                 "vs. Partitioner(" + blockInst + ")");
         return false;
      } else {
         Logger.getLogger(PartitioningService.class.getName()).
                 info("Processed "+blockInst+" instructions.");
      }

      return true;

   }
    *
    */




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
   private PartitioningConfig config;


   private List<InstructionBlock> blocks;

   private boolean flushed;

   public Partitioner getPartitioner() {
      return partitioner;
   }



}

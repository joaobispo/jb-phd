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
import org.ancora.InstructionBlock.GenericInstruction;
import org.ancora.Partitioning.Blocks.BlockStream;
import org.ancora.InstructionBlock.InstructionBlock;
import org.ancora.InstructionBlock.InstructionBusReader;
import org.ancora.Partitioning.Partitioner;

/**
 *
 * @author Joao Bispo
 */
public class MultiBlockStream implements BlockStream {

   public MultiBlockStream(List<Partitioner> partitioners, InstructionBusReader busReader) {
      //this.partitioners = partitioners;
      this.busReader = busReader;
      currentPartitioner = null;

      blocks = new ArrayList<InstructionBlock>();
      flushed = false;

      partitionerNames = new ArrayList<String>();

      workers = new ArrayList<ManualBlockWorker>();
      for(Partitioner partitioner : partitioners) {
         workers.add(new ManualBlockWorker(partitioner));
      }

   }
//      checkInstructionNumber(busReader, ibStats);


   public InstructionBlock nextBlock() {
      // Feed instructions until a block appear in the collector
      while(true) {
         GenericInstruction inst = busReader.nextInstruction();
//         System.err.println("Feeding inst "+inst);
         // This means the stream has ended
         if (inst == null) {
            if (!flushed) {
               // Flush all workers
               for (ManualBlockWorker worker : workers) {
                  worker.flush();
                  addBlocks(worker);
                  // For each added
                  worker.checkInstructionNumber(busReader.getInstructions());
               }
               flushed = true;
            }

            // Pop a block from the list
            return popBlock();
         }
         // Feed inst to partitioners
         else {
            for(ManualBlockWorker worker : workers) {
               worker.processInstruction(inst);
               addBlocks(worker);
            }
         }

         // Continue until there is something to collect
         InstructionBlock block = popBlock();
         if(block != null) {
            return block;
         }
      }
   }

   public long getTotalInstructions() {
      return busReader.getInstructions();
   }

   public String getPartitionerName() {
      return currentPartitioner;
   }

   public List<ManualBlockWorker> getWorkers() {
      return workers;
   }

   private void addBlocks(ManualBlockWorker worker) {
      List<InstructionBlock> newBlocks = worker.popBlocks();
      blocks.addAll(newBlocks);
//      System.err.println(newBlocks.size() " new blocks from "+worker.getPartitionerName());
      // For each block added, add a partitioner name
      for(int i=0; i<newBlocks.size(); i++) {
         partitionerNames.add(worker.getPartitionerName());
      }
   }

   private InstructionBlock popBlock() {
      if(blocks.isEmpty()) {
         return null;
      }

      currentPartitioner = partitionerNames.remove(0);
      return blocks.remove(0);
   }

   /*
   private String popName() {
      if(partitionerNames.isEmpty()) {
         return null;
      }

      return partitionerNames.remove(0);
   }
    *
    */
   
   /**
    * INSTANCE VARIABLES
    */
   private List<ManualBlockWorker> workers;
   //private List<Partitioner> partitioners;
   private InstructionBusReader busReader;
   

   private List<InstructionBlock> blocks;
   private List<String> partitionerNames;
   private String currentPartitioner;

   private boolean flushed;



}

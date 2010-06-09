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

package org.ancora.DMTool.Simulation;

import java.util.List;
import org.ancora.FuMatrix.Architecture.Fu;
import org.ancora.FuMatrix.Stats.MapperData;
import org.ancora.InstructionBlock.InstructionBlock;

/**
 *
 * @author Joao Bispo
 */
public class SimulationData {

   public SimulationData() {
      totalSeenInstructions = 0l;
      processorExecutedInstructions = 0l;
      hardwareExecutedCycles = 0l;

      failedMappings = 0;

      communicationsCost = 0l;
      totalMappings = 0l;
      totalMappedLines = 0l;
      totalMappedOps = 0l;
      totalMappedMoves = 0l;

//      maxLineSize = 0;
//      maxLineOps = 0;

   }

   public long getCommunicationsCost() {
      return communicationsCost;
   }



   public long getTotalSeenInstructions() {
      return totalSeenInstructions;
   }

   public long getHardwareExecutedCycles() {
      return hardwareExecutedCycles;
   }

   public int getFailedMappings() {
      return failedMappings;
   }


/*
   public int getMaxLineOps() {
      return maxLineOps;
   }
*/
   /*
   public int getMaxLineSize() {
      return maxLineSize;
   }
    * 
    */

   public long getProcessorExecutedInstructions() {
      return processorExecutedInstructions;
   }

   public long getTotalMappedLines() {
      return totalMappedLines;
   }

   public long getTotalMappedMoves() {
      return totalMappedMoves;
   }

   public long getTotalMappedOps() {
      return totalMappedOps;
   }

   public long getTotalMappings() {
      return totalMappings;
   }

   void updateProcessorPath(InstructionBlock block) {
      processorExecutedInstructions += block.getTotalInstructions();
      totalSeenInstructions += block.getTotalInstructions();
   }

   void signalMappingFailure() {
      failedMappings++;
   }


   void updateHwPath(MapperData mapperData, InstructionBlock block) {
      long commCost = mapperData.getLiveIns() + mapperData.getLiveOuts();

      // Get cycles
      hardwareExecutedCycles += mapperData.getLines() * block.getRepetitions();
      hardwareExecutedCycles += commCost;

      totalMappings++;
      totalMappedLines += mapperData.getLines();
      totalMappedOps += mapperData.getOps();
      totalMappedMoves += mapperData.getMoves();

   }


   /**
    * INSTANCE VARIABLES
    */
   private long totalSeenInstructions;
   private long processorExecutedInstructions;
   private long hardwareExecutedCycles;
   private int failedMappings;

   private long communicationsCost;
   private long totalMappings;
   private long totalMappedLines;
   private long totalMappedOps;
   private long totalMappedMoves;

   //private int maxLineSize;
   //private int maxLineOps;
   //private int maxLineMoves;





}

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

import java.util.logging.Logger;
import org.ancora.InstructionBlock.InstructionBusReader;

/**
 *
 * @author Joao Bispo
 */
public class SimulationCalcs {

   public SimulationCalcs(SimulationData simData, InstructionBusReader busReader) {
      this.simData = simData;
      this.busReader = busReader;
   }

   public double getInstructionBusCpi() {
      long traceCycles = busReader.getCycles();
      long traceInstructions = busReader.getInstructions();
      return (double)traceCycles / (double)traceInstructions;
   }
   /**
    * Compares the number of instructions processed by simulation with the number of
    * instructions read in the instruction bus.
    * 
    * @return true if it passes the tests, false otherwise
    */
   public boolean check() {
      // TEST 1
      if(busReader.getInstructions() != simData.getTotalSeenInstructions()) {
            Logger.getLogger(SimulationCalcs.class.getName()).
                    warning("DTool simulation instructions ("+busReader.getInstructions()+") different " +
                    "from instructions fed to dynamic mapping simulation ("+simData.getTotalSeenInstructions()+")");
            return false;
         }

      return true;
   }


   public long getProcessorCycles() {
      return (long) Math.ceil((double)simData.getProcessorExecutedInstructions() * getInstructionBusCpi());
   }

   public long getSimulationCycles() {
      return getProcessorCycles() + simData.getHardwareExecutedCycles();
   }

   public double getSpeedUp() {
      return (double) busReader.getCycles() / (double) getSimulationCycles();
   }

   /**
    * INSTANCE VARIABLES
    */
   private SimulationData simData;
   private InstructionBusReader busReader;



}

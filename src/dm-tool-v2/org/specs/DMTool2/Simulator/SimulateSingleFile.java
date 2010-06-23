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

package org.specs.DMTool2.Simulator;

import java.util.List;
import java.util.logging.Logger;
import org.ancora.FuMatrix.Mapper.GeneralMapper;
import org.ancora.FuMatrix.Stats.MapperData;
import org.ancora.InstructionBlock.InstructionBlock;
import org.ancora.IntermediateRepresentation.MbParser;
import org.ancora.IntermediateRepresentation.Operation;
import org.ancora.Partitioning.Blocks.BlockStream;
import org.ancora.SharedLibrary.ParseUtils;
import org.ancora.StreamTransform.SingleStaticAssignment;
import org.specs.DMTool2.Dispensers.MapperDispenser;
import org.specs.DMTool2.Dispensers.TransformDispenser;
import org.specs.DMTool2.Settings.Settings;

/**
 *
 * @author Joao Bispo
 */
public class SimulateSingleFile {

   /**
    * Creates a simulator from a block stream
    * @param blockStream
    */
   public SimulateSingleFile(BlockStream blockStream) {
      this.blockStream = blockStream;
      simData = new SimulationData();
      repetitionsThreshold = DEFAULT_REPETITIONS_THRESHOLD;
   }

   public static SimulateSingleFile getCurrentSimulator(BlockStream blockStream) {
      String repetitionsString = Settings.optionsTable.get(SimulatorOption.iteration_threshold);
      int repetitions = ParseUtils.parseInt(repetitionsString);

      SimulateSingleFile simulator = new SimulateSingleFile(blockStream);
      simulator.setRepetitionsThreshold(repetitions);

      return simulator;
   }

   public void runSimulation() {
      InstructionBlock block = blockStream.nextBlock();
      while (block != null) {

         // Check repetitions of block
         int rep = block.getRepetitions();
         if (rep < repetitionsThreshold) {
            processorPath(block);
         } else {
            hwPath(block);
         }

         block = blockStream.nextBlock();
      }
   }

   private void processorPath(InstructionBlock block) {
      simData.updateProcessorPath(block);
   }

   private void hwPath(InstructionBlock block) {
      // Hw path
      // Transform Instruction Block into PureIR
      //List<Operation> operations = MbParser.mbToIrBlock(block).getOperations();
      List<Operation> operations = MbParser.mbToOperations(block);
      // Put in SSA
      SingleStaticAssignment.transform(operations);

      if (operations == null) {
         Logger.getLogger(SimulateSingleFile.class.getName()).
                 warning("Could not parse instruction block '" + block.getId() + "'.");
         processorPath(block);
         return;
      }


      // Transform
      //DmTransformDispenser.applyCurrentTransformations(operations);
      // Apply all transformations
      TransformDispenser.applyPreTransformations(operations);
      TransformDispenser.applyCurrentTransformations(operations);

      // Map
      GeneralMapper mapper = MapperDispenser.applyCurrentMapper(operations);
      if (mapper == null) {
         simData.signalMappingFailure();
         // Mapping failed. Follow processor path
         processorPath(block);
         return;
      }



      // Show block
      MapperData mapperData = MapperData.build(mapper);
      simData.updateHwPath(mapperData, block);

   }



   public void setRepetitionsThreshold(int repetitionsThreshold) {
      this.repetitionsThreshold = repetitionsThreshold;
   }

   public SimulationData getSimulationData() {
      return simData;
   }



   /**
    * INSTANCE VARIABLES
    */
   private BlockStream blockStream;
   private SimulationData simData;
   private int repetitionsThreshold;

   public final static int DEFAULT_REPETITIONS_THRESHOLD = 2;

   
}

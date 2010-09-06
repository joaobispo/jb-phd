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

package org.ancora.Partitioning.Blocks;

import org.ancora.InstructionBlock.InstructionBlock;
import org.ancora.InstructionBlock.InstructionBusReader;
import org.ancora.Partitioning.Partitioner;

/**
 * Using an InstructionBusReader to read instructions and a Partitioner, forms
 * InstructionBlocks, until it reaches the end of the stream.
 *
 * @author Joao Bispo
 */
public interface BlockStream {

   /**
    * 
    * @return an instruction block, or null if it has reached the end of stream
    */
   InstructionBlock nextBlock();

   //String getPartitionerName();

   //long getTotalInstructions();
   long getPartitionerInstructions();
   long getBusReaderInstructions();
   Partitioner getPartitioner();
   InstructionBusReader getInstructionBusReader();
}

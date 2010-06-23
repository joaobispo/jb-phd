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

import org.ancora.Partitioning.Partitioner;
import org.ancora.Partitioning.BasicBlock;
import java.util.ArrayList;
import java.util.List;
import org.ancora.InstructionBlock.GenericInstruction;
import org.ancora.InstructionBlock.InstructionBlock;
import org.ancora.InstructionBlock.InstructionBlockListener;
import org.ancora.SharedLibrary.BitUtils;

/**
 *
 * @author Joao Bispo
 */
public class SuperBlock extends Partitioner {

   public SuperBlock(BasicBlock basicBlock) {
      //this.bbPartitioner = new BasicBlock(jumpFilter);
      this.bbPartitioner = basicBlock;
      this.sbBuilder = new SuperBlockBuilder();

      this.bbPartitioner.addListener(sbBuilder);
   }



   @Override
   public String getName() {
      return NAME;
   }

   @Override
   public void acceptInstruction(GenericInstruction instruction) {
      bbPartitioner.acceptInstruction(instruction);
   }

   @Override
   public void flush() {
      bbPartitioner.flush();
      this.sbBuilder = new SuperBlockBuilder();
   }


   /**
    * INSTANCE VARIABLES
    */

   private BasicBlock bbPartitioner;
   private SuperBlockBuilder sbBuilder;

   public static final String NAME = "SuperBlock";


   class SuperBlockBuilder implements InstructionBlockListener {

      public SuperBlockBuilder() {
         prepareStateForNewSuperBlock();
      }

      public void accept(InstructionBlock instructionBlock) {
         int addressOfCurrentBasicBlock = instructionBlock.getInstructions().get(0).getAddress();
         boolean forwardJump = addressOfCurrentBasicBlock > lastBasicBlockAddress;

         if (!forwardJump) {
            completeSuperBlock();
            prepareStateForNewSuperBlock();
         }

         updateCurrentSuperBlock(instructionBlock);
      }

      public void flush() {
         if (instructions != null) {
            completeSuperBlock();
         }

         flushListeners();
      }

      private void completeSuperBlock() {
         // Build new InstructionBlock
         InstructionBlock newBlock = new InstructionBlock(instructions, 1, hash, instructions.size());

         // Notice Listeners
         noticeListeners(newBlock);

         prepareStateForNewSuperBlock();
      }

      private void prepareStateForNewSuperBlock() {
         instructions = new ArrayList<GenericInstruction>();
         hash = HASH_INITIAL_VALUE;
         lastBasicBlockAddress = -1;
      }

      private void updateCurrentSuperBlock(InstructionBlock basicBlock) {
         List<GenericInstruction> instructionArray = basicBlock.getInstructions();
         // Add all instructions to SuperBlock
         instructions.addAll(instructionArray);

         // Update hash value
         hash = BitUtils.superFastHash(instructionArray.get(0).getAddress(), hash);

         //lastBasicBlockAddress = basicBlock.getStartAddress();
         //lastBasicBlockAddress = basicBlock.getLastAddress();
         // Determine last address
         lastBasicBlockAddress = instructionArray.get(instructionArray.size() - 1).getAddress();
      }

      // State
      private int lastBasicBlockAddress;
      private List<GenericInstruction> instructions;
      private int hash;
      // Constants
      private static final int HASH_INITIAL_VALUE = 4;

   }
}

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

package org.ancora.InstructionBlock.Listeners;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.ancora.InstructionBlock.InstructionBlock;
import org.ancora.InstructionBlock.InstructionBlockListener;

/**
 *
 * @author Joao Bispo
 */
public class InstructionBlockCollector implements InstructionBlockListener {

   public InstructionBlockCollector() {
      blocks = new ArrayList<InstructionBlock>();
   }



   public void accept(InstructionBlock instructionBlock) {
      blocks.add(instructionBlock);
   }

   public void flush() {
      // Do Nothing
   }

   public List<InstructionBlock> getBlocks() {
      return blocks;
   }

   /*
   public InstructionBlock popBlock() {
      if(blocks.isEmpty()) {
         return null;
      }

      if(blocks.size() == 1) {
         InstructionBlock block = blocks.get(0);
         blocks = new ArrayList<InstructionBlock>();
         return block;
      }

      Logger.getLogger(InstructionBlockCollector.class.getName()).
              warning("InstructionBlock list greater than 1 ("+blocks.size()+")");

      return blocks.remove(0);
   }
    *
    */
   
   public List<InstructionBlock> popBlocks() {
      if(blocks.isEmpty()) {
         return blocks;
      }

      List<InstructionBlock> tempBlocks = blocks;
      blocks = new ArrayList<InstructionBlock>();
      return tempBlocks;
      
   }

   /**
    * INSTANCE VARIABLES
    */
   private List<InstructionBlock> blocks;
}

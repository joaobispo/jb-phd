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

import java.util.List;
import java.util.ArrayList;
import org.ancora.InstructionBlock.InstructionBlock;
import org.ancora.InstructionBlock.InstructionBlockListener;

/**
 *
 * @author Joao Bispo
 */
public class InstructionBlockStats implements InstructionBlockListener {

   public InstructionBlockStats() {
      blockInstructions = new ArrayList<Integer>();
      blockRepetitions = new ArrayList<Integer>();
   }



   public void accept(InstructionBlock instructionBlock) {
      blockInstructions.add(instructionBlock.getInstructions().size());
      blockRepetitions.add(instructionBlock.getRepetitions());
   }

   public void flush() {
      // Do Nothing
   }

   public long getTotalInstructions() {
      long acc = 0;
      for(int i=0; i<blockInstructions.size(); i++) {
         acc += blockInstructions.get(i) * blockRepetitions.get(i);
      }

      return acc;
   }

   private List<Integer> blockInstructions;
   private List<Integer> blockRepetitions;
}

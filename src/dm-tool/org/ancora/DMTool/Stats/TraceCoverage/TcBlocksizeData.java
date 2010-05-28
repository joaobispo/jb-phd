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

package org.ancora.DMTool.Stats.TraceCoverage;

import java.util.HashMap;
import java.util.Map;
import org.ancora.InstructionBlock.InstructionBlock;

/**
 * Stores the number of instruction per repetition in InstructionBlocks
 *
 * @author Joao Bispo
 */
public class TcBlocksizeData {

   public TcBlocksizeData(String filename) {
      instPerBlockTotalSize = new HashMap<Long, Long>();
      totalInstructions = 0;
      this.filename = filename;
   }


   public void addBlock(InstructionBlock block) {
      long totalBlockSize = block.getTotalInstructions();
      Long blockInst = instPerBlockTotalSize.get(totalBlockSize);
      if(blockInst == null) {
         blockInst = 0l;
      }
      blockInst+= totalBlockSize;
      instPerBlockTotalSize.put(totalBlockSize, blockInst);
   }

   @Override
   public String toString() {
      //System.err.println("Inst:"+totalInstructions+". For "+filename);
      return instPerBlockTotalSize.toString();
   }

   public String getFilename() {
      return filename;
   }

   public long getTotalInstructions() {
      return totalInstructions;
   }


   public Map<Long, Long> getInstPerBlockTotalSize() {
      return instPerBlockTotalSize;
   }



   /**
    * INSTANCE VARIABLES
    */
   private Map<Long, Long> instPerBlockTotalSize;
   private long totalInstructions;
   private String filename;
}

/*
 *  Copyright 2010 SPECS Research Group.
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

package org.specs.DMTool2.BlockSize;

import java.util.HashMap;
import java.util.Map;
import org.ancora.InstructionBlock.InstructionBlock;

/**
 * Stores data related to BlockSize.
 *
 * @author Joao Bispo
 */
public class BlockSizeData {

   public BlockSizeData() {
      maxSize = 0;
      minSize = Integer.MAX_VALUE;
      sizeRepetitions = new HashMap<Integer, Integer>();
      numBlocks = 0;
   }

   void addBlock(InstructionBlock currentBlock) {
      int blockSize = currentBlock.getInstructions().size();

      maxSize = Math.max(maxSize, blockSize);
      minSize = Math.min(minSize, blockSize);

      addSizeRepetition(blockSize, currentBlock.getRepetitions());

      numBlocks++;
   }


   private void addSizeRepetition(int size, int repetition) {
      Integer repetitions = sizeRepetitions.get(size);
      if(repetitions == null) {
         repetitions = 0;
      }

      repetitions += repetition;
      sizeRepetitions.put(size, repetitions);
   }

   public void addData(BlockSizeData localStats) {
      maxSize = Math.max(maxSize, localStats.maxSize);
      minSize = Math.min(minSize, localStats.minSize);

      for (Integer size : localStats.sizeRepetitions.keySet()) {
         int repetition = localStats.sizeRepetitions.get(size);
         addSizeRepetition(size, repetition);
      }


      numBlocks += localStats.numBlocks;
   }

   public long getTotalInstructions() {
      long totalInstructions = 0l;

      for(Integer size : sizeRepetitions.keySet()) {
         int repetitions = sizeRepetitions.get(size);
         totalInstructions += repetitions * size;
      }

      return totalInstructions;
   }

   public int getMaxSize() {
      return maxSize;
   }

   public int getMinSize() {
      return minSize;
   }

   public double getWeightedAvg() {
      long totalRepetitions = 0l;
      long totalSize = 0l;

      for(Integer size : sizeRepetitions.keySet()) {
         int repetitions = sizeRepetitions.get(size);
         totalRepetitions += repetitions;
         totalSize += repetitions * size;
      }

      return (double) totalSize / (double) totalRepetitions;
   }

   /**
    * INSTANCE VARIABLES
    */
   private int maxSize;
   private int minSize;
   private Map<Integer,Integer> sizeRepetitions;
   private int numBlocks;





}

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

package org.ancora.FuMatrix.Stats;

/**
 * Contains data about mapping, having the number of repetitions into account.
 *
 * @author Joao Bispo
 */
public class IterationData {

   public IterationData() {
      totalIterations = 0l;
      totalExecutedLines = 0l;
      totalExecutedOps = 0l;
      //totalMoves = 0l;
      mappings = 0;
   }

   public static IterationData build(MapperData mapperData, int repetitions) {
      IterationData iterationData = new IterationData();

      iterationData.totalIterations = repetitions;
      iterationData.totalExecutedLines = mapperData.getLines() * repetitions;
      iterationData.totalExecutedOps = mapperData.getOps() * repetitions;
      iterationData.totalMappedOps = mapperData.getOps();
      //iterationData.totalMoves = mapperData.getMoves() * repetitions;
      iterationData.mappings = 1;

      return iterationData;
   }

   public void addIterationData(IterationData iterationData) {
      this.totalExecutedLines += iterationData.totalExecutedLines;
      this.totalExecutedOps += iterationData.totalExecutedOps;
      this.totalIterations += iterationData.totalIterations;
      this.mappings += iterationData.mappings;
      this.totalMappedOps += iterationData.totalMappedOps;
   }

   public long getTotalMappedOps() {
      return totalMappedOps;
   }

   public int getMappings() {
      return mappings;
   }

   public long getTotalExecutedLines() {
      return totalExecutedLines;
   }

   public long getTotalExecutedOps() {
      return totalExecutedOps;
   }

   public long getTotalIterations() {
      return totalIterations;
   }

   public double getAverageExecutedLines() {
      return (double)totalExecutedLines / (double)mappings;
   }

   public double getAverageExecutedOps() {
      return (double)totalExecutedOps / (double)mappings;
   }

   public double getAverageIterations() {
      return (double)totalIterations / (double)mappings;
   }

   public String averagesString() {
      StringBuilder builder = new StringBuilder();

      builder.append("Avg Executed Lines:");
      builder.append(getAverageExecutedLines());
      builder.append("\n");

      builder.append("Avg Executed Ops:");
      builder.append(getAverageExecutedOps());
      builder.append("\n");

      builder.append("Avg Iterations:");
      builder.append(getAverageIterations());
      builder.append("\n");

      return builder.toString();
   }

   @Override
   public String toString() {
      return averagesString();
   }

   

   /**
    * INSTANCE VARIABLES
    */
   private long totalIterations;
   private long totalExecutedLines;
   private long totalExecutedOps;
   private long totalMappedOps;
   //private long totalMoves;
   private int mappings;
}

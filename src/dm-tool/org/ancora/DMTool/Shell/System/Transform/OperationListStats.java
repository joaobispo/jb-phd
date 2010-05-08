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

package org.ancora.DMTool.Shell.System.Transform;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.ancora.IrMapping.Mapper;
import org.ancora.IntermediateRepresentation.Operation;

/**
 *
 * @author Joao Bispo
 */
public class OperationListStats {

   public static OperationListStats calcAverage(String name, List<OperationListStats> stats) {
      int numberOfLines = 0;
      int numberOfOperations = 0;
      int liveIns = 0;
      int liveOuts = 0;
      int repetitions = 0;

      //int size = stats.size();
      for(int i=0; i<stats.size(); i++) {
         liveIns += stats.get(i).liveins;
         liveOuts += stats.get(i).liveouts;
         repetitions += stats.get(i).repetitions;
         numberOfOperations += stats.get(i).numberOfOperations;
         numberOfLines += stats.get(i).numberOfLines;
      }

      return new OperationListStats(numberOfLines, numberOfOperations, liveIns, liveOuts, null, repetitions, name);

      /*
      double avgClp = (double)numberOfLines / (double)size;
      double avgIlp =  (double)numberOfOperations / (double)numberOfLines;
      double avgComm =  (double)(liveIns+liveOuts) / (double)size;
      double avgInstructions =  (double)(numberOfOperations) / (double)size;
      double avgRepetitions =  (double)(repetitions) / (double)size;
      
      System.out.println("Avg. CLP:"+avgClp);
      System.out.println("Avg. ILP:"+avgIlp);
      System.out.println("Avg. Comm:"+avgComm);
      System.out.println("Avg. Instructions per block:"+avgInstructions);
      System.out.println("Avg. Repetitions per block:"+avgRepetitions);
       */
   }

   private OperationListStats(int numberOfLines, int numberOfOperations, 
           int liveins, int liveouts, Map<Integer, List<Operation>> mapping,
           int repetitions, String name) {
      this.numberOfLines = numberOfLines;
      this.numberOfOperations = numberOfOperations;
      this.liveins = liveins;
      this.liveouts = liveouts;
      this.mapping = mapping;
      this.repetitions = repetitions;
      this.name = name;
   }


/*
public static Mapper getIlpStats(List<Operation> operations, Mapper ilpScene) {
      // Create data
      ilpScene.reset();
      ilpScene.processOperations(operations);

      return ilpScene;
}
 */


   public static OperationListStats buildStats(List<Operation> operations, Mapper ilpScene,
           int repetitions, String name) {
      // Create data
      ilpScene.reset();
      ilpScene.processOperations(operations);
/*
      int mbOp = 0;
      for(int i=0; i<operations.size(); i++) {
         MbOperation op = MbOperation.getMbOperation(operations.get(i));
         if(op != null) {
            mbOp++;
         }
      }
*/
      // Gather data
      return new OperationListStats(ilpScene.getNumberOfLines(), ilpScene.getNumberOfOps(),
              ilpScene.getLiveIns(), ilpScene.getLiveOuts(), ilpScene.getMapping(),
              repetitions, name);
      
   }

   public int getNumberOfLines() {
      return numberOfLines;
   }

   /**
    * ILP calculated as the number of operations / number of occupied lines
    * 
    * @return
    */
   public double getIlp() {
      return ((double)numberOfOperations/(double)numberOfLines);
   }

   /**
    * Critical Path Lenght calculated as the number of occupied lines.
    * @return
    */
   public int getCpl() {
      return numberOfLines;
   }

   /**
    *
    * @return number of operations
    */
   public int getNumberOfOperations() {
      return numberOfOperations;
   }

   /**
    * Calculated as the number of live-ins + number of live-outs.
    *
    * @return
    */
   public int getCommunicationCost() {
      return liveins + liveouts;
   }

   /*
   public int getNumberOfMbOps() {
      return numberOfMbOps;
   }
    */



   public String getMappingString() {
      StringBuilder builder = new StringBuilder();

      List<Integer> keys = new ArrayList<Integer>(mapping.size());
      keys.addAll(mapping.keySet());
      Collections.sort(keys);
 //     Set<Integer> keys = mapping.keySet();
 //     Collections.sort(keys);
      for(Integer key : keys) {
      //for(int i=0; i<mapping.size(); i++) {
         //int key = i+1;
         List<Operation> operations = mapping.get(key);
         // First operation
         //builder.append(operations.get(0).getValue());
         builder.append(operations.get(0));

         for(int j=1; j<operations.size(); j++) {
            builder.append(" | ");
            builder.append(operations.get(j));
            //builder.append(operations.get(j).getValue());
         }
         builder.append("\n");
      }
      

      return builder.toString();
   }

   @Override
   public String toString() {
      StringBuilder builder = new StringBuilder();

      builder.append("Block:"+name);
      builder.append("Repetitions:"+repetitions);
      builder.append("ILP:"+getIlp()+"\n");
      builder.append("CPL:"+getCpl()+"\n");
      builder.append("#Inst:"+getNumberOfOperations()+"\n");
      builder.append("CommunicationCosts:"+getCommunicationCost()+"\n");
      //builder.append("Number of MicroBlaze Ops:"+getNumberOfMbOps()+"\n");

      return builder.toString();
   }

            // Show Mapping
      //printMapping(ilpScene);
      //Map<Integer, List<Operation>> mapping = ilpScene.getMapping();

   /**
    * INSTANCE VARIABLES
    */
   private final int numberOfLines;
   private final int numberOfOperations;
   private final int liveins;
   private final int liveouts;
   private final Map<Integer, List<Operation>> mapping;
   private final int repetitions;
   private final String name;
   //private final int numberOfMbOps;
}

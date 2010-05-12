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

package org.ancora.DMTool.Stats;

import java.math.BigDecimal;
import java.math.MathContext;
import org.ancora.IrMapping.Mapper;

/**
 *
 * @author Joao Bispo
 */
public class DataProcess {

   public static LongTransformDataSingle collectTransformData(Mapper mapper, int repetitions) {
      // Create Data object
      LongTransformDataSingle data = new LongTransformDataSingle();

      // Gather data
      data.addValue(TransformParam.mappedOperations, mapper.getNumberOfOps());
      data.addValue(TransformParam.executedOperations, mapper.getNumberOfOps()*repetitions);
      data.addValue(TransformParam.mappedLines, mapper.getNumberOfLines());
      data.addValue(TransformParam.executedLines, mapper.getNumberOfLines()*repetitions);
      data.addValue(TransformParam.liveIns, mapper.getLiveIns());
      data.addValue(TransformParam.liveOuts, mapper.getLiveOuts());
      data.addValue(TransformParam.cycles, repetitions);

      return data;
   }

   public static void showTransformDataChanges(LongTransformDataSingle beforeTransf, LongTransformDataSingle afterTransf) {
       //String[] param = {"CommCosts", "Cpl", "Ilp", "Mapped Operations", "Executed Operations"};
       String[] param = {"CommCosts", "Mapped Lines", "Executed Lines", "Ilp", "Mapped Operations", "Executed Operations"};
      String[] before = {
         String.valueOf(getCommunicationCost(beforeTransf)),
        //String.valueOf(getCpl(beforeTransf)),
        String.valueOf(beforeTransf.getValue(TransformParam.mappedLines)),
        String.valueOf(beforeTransf.getValue(TransformParam.executedLines)),
        String.valueOf(getIlp(beforeTransf)),
        String.valueOf(beforeTransf.getValue(TransformParam.mappedOperations)),
        String.valueOf(beforeTransf.getValue(TransformParam.executedOperations)),

      };
      String[] after = {
                  String.valueOf(getCommunicationCost(afterTransf)),
//        String.valueOf(getCpl(afterTransf)),
        String.valueOf(afterTransf.getValue(TransformParam.mappedLines)),
        String.valueOf(afterTransf.getValue(TransformParam.executedLines)),
        String.valueOf(getIlp(afterTransf)),
        String.valueOf(afterTransf.getValue(TransformParam.mappedOperations)),
        String.valueOf(afterTransf.getValue(TransformParam.executedOperations)),

      };


      //System.out.println("Changes:");
      boolean noChanges = true;
      int changeCounter = 0;
      for (int i = 0; i < param.length; i++) {
         if (!before[i].equals(after[i])) {
            noChanges = false;
            BigDecimal ratio = (new BigDecimal(after[i])).divide(new BigDecimal(before[i]), MathContext.DECIMAL32);
            System.out.println(param[i]+":"+before[i]+"->"+after[i]+" ("+ratio+");");
            changeCounter++;
         }
      }

      if(noChanges) {
         System.out.println("None");
      } else {
         System.out.println("Changes in "+changeCounter+" parameters out of "+param.length+".");
      }
   }

   public static long getCommunicationCost(LongTransformDataSingle data) {
      return getCommunicationCost(data.getValue(TransformParam.liveIns),
              data.getValue(TransformParam.liveOuts));
   }

   public static long getCommunicationCost(long liveins, long liveouts) {
      return liveins + liveouts;
   }

   /**
    * ILP calculated as the number of operations / number of occupied lines
    *
    * @return
    */
   public static double getIlp(long numberOfOperations, long numberOfLines) {
      return ((double)numberOfOperations/(double)numberOfLines);
   }

   public static double getIlp(LongTransformDataSingle data) {
      return ((double)data.getValue(TransformParam.mappedOperations)/
              (double)data.getValue(TransformParam.mappedLines));
   }

   /**
    * Critical Path Lenght calculated as the number of occupied lines.
    * @return
    */
   public static long getCpl(long numberOfMappedLines) {
      return numberOfMappedLines;
   }
    
   public static long getCpl(LongTransformDataSingle data) {
      return getCpl(data.getValue(TransformParam.mappedLines));
   }


}

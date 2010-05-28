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

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.ancora.SharedLibrary.IoUtils;

/**
 *
 * @author Joao Bispo
 */
public class TcBlocksizeProcess {

      public static long[] getBlockLogLine(TcBlocksizeData stat, int maxBlockSize) {
   //public static Map<Integer, Long> getBlockLogLine(TcData stat, int maxBlockSize) {
      // Create key array
      List<Integer> basescale = createLogScale(10, maxBlockSize);
      int arraySize = basescale.size();

      // Create array
      //long[] results = new long[maxRepetitions];

      //Map<Integer, Long> table = stat.getInstPerRepetitions();
      Map<Long, Long> table = stat.getInstPerBlockTotalSize();

      // Get keys and order them
      //Collections.so
      List<Long> keys = new ArrayList<Long>(table.keySet());
      Collections.sort(keys);

      //List<Long> basescaleValues = new ArrayList<Long>();
      long[] basescaleValues = new long[arraySize];
      int originalKeysIndex = 0;

      int logKeysIndex = 0;
      int logScale = basescale.get(logKeysIndex);
      long currentValue = stat.getTotalInstructions();
      while(originalKeysIndex < keys.size()) {
         long originalScale = keys.get(originalKeysIndex);

         while(logScale < originalScale) {
            basescaleValues[logKeysIndex] = currentValue;
            logKeysIndex++;
            logScale = basescale.get(logKeysIndex);
         }

         currentValue -= table.get(originalScale);
         originalKeysIndex++;
      }

      // Add zeros to the values list until it is the same size as logscale
      //int diff = basescale.size() - basescaleValues.size();
      //for(int i=0; i<diff; i++) {
      //   basescaleValues.add(0l);
     // }

      //Map<Integer, Long> resultMap = new HashMap<Integer, Long>();
      //for(int i=0; i<basescale.size(); i++) {
      //   resultMap.put(basescale.get(i), basescaleValues.get(i));
      //}

      //return resultMap;
      return basescaleValues;
   }

   public static List<Integer> createLogScale(int base, int maxValue) {
      List<Integer> scale = new ArrayList<Integer>();
      int powerFactor = 0;
      int index = 0;
      // Add zero
      scale.add(index);

      while (index <= maxValue) {
         for (int i = 1; i < base; i++) {
            index = (int) (i * Math.pow(base, powerFactor));
            scale.add(index);
            if(index > maxValue) {
               break;
            }
            /*
            if(index <= maxValue) {
               scale.add(index);
            } else {
               continue;
            }
             *
             */
         }
         powerFactor++;
      }

      return scale;
   }

    public static boolean csvBlocksizeStart(File csvFile, int maxBlocksize) {
      // Create String
      StringBuilder builder = new StringBuilder();

      List<Integer> list = createLogScale(10, maxBlocksize);
      for(int i=0; i<list.size(); i++) {
         builder.append(",");
         builder.append(list.get(i));
      }
      builder.append("\n");

      // Create file
      return IoUtils.write(csvFile, builder.toString());
   }
}

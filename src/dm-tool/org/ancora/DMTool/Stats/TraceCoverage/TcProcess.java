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
public class TcProcess {

   public static long[] getAbsLine(TcData stat, int maxRepetitions) {
      // Create array
      long[] results = new long[maxRepetitions];

      Map<Integer, Long> table = stat.getInstPerRepetitions();

      // Get keys and order them
      //Collections.so
      List<Integer> keys = new ArrayList<Integer>(table.keySet());
      Collections.sort(keys);

      long total = stat.getTotalInstructions();
      int startRep = 0;
      for(int i=0; i<keys.size(); i++) {
         int endRep = keys.get(i);
         long inst = table.get(endRep);

         for(int j=startRep; j<endRep; j++) {
            results[j] = total;
         }

         // Update values
         total-=inst;
         startRep = endRep;
      }

      /*
      System.err.println("Table:");
      System.err.println(table);
      System.err.println("Result:");
      System.err.println(Arrays.toString(results));
       *
       */
      return results;
   }

   public static boolean csvStart(File csvFile, int maxRepetitions) {
      // Create String
      StringBuilder builder = new StringBuilder();

      for(int i=0; i<maxRepetitions; i++) {
         builder.append(",");
         builder.append(i);
      }
      builder.append("\n");

      // Create file
      return IoUtils.write(csvFile, builder.toString());
   };

   public static boolean csvAppend(File csvFile, String name, double[] values) {
      // Create String
      StringBuilder builder = new StringBuilder();

      builder.append(name);
      for(int i=0; i<values.length; i++) {
         builder.append(",");
         builder.append(values[i]);
      }
      builder.append("\n");

      // Append file
      return IoUtils.append(csvFile, builder.toString());
   }

   public static boolean csvAppend(File csvFile, String name, long[] values) {
      // Create String
      StringBuilder builder = new StringBuilder();

      builder.append(name);
      for(int i=0; i<values.length; i++) {
         builder.append(",");
         builder.append(values[i]);
      }
      builder.append("\n");

      // Append file
      return IoUtils.append(csvFile, builder.toString());
   }

}

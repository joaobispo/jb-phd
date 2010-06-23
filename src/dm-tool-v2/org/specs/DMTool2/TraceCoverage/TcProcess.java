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

package org.specs.DMTool2.TraceCoverage;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.ancora.Partitioning.Partitioner;
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
         //builder.append(",");
         builder.append(CSV_SEPARATOR);
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
         builder.append(CSV_SEPARATOR);
//         builder.append(",");
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
         builder.append(CSV_SEPARATOR);
//         builder.append(",");
         builder.append(values[i]);
      }
      builder.append("\n");

      // Append file
      return IoUtils.append(csvFile, builder.toString());
   }

   public static final String CSV_SEPARATOR = "\t";

   /**
    * Collect all individual numbers.
    *
    * @param stats
    * @param maxRepetitions
    * @return
    */
   public static List<Integer> getMasterLine(List<TcData> stats, int maxRepetitions) {
      Set<Integer> setRepetitions = new HashSet<Integer>();
      for(TcData stat : stats) {
         //setRepetitions.addAll(stat.getInstPerRepetitions().keySet());
         for(Integer i : stat.getInstPerRepetitions().keySet()) {
            setRepetitions.add(i-1);
            setRepetitions.add(i);
            setRepetitions.add(i+1);
         }
         
      }
      // Add limits
      setRepetitions.add(0);
      setRepetitions.add(maxRepetitions);
      List<Integer> returnList = new ArrayList<Integer>(setRepetitions);
      Collections.sort(returnList);

      return returnList;
   }


   public static List<Integer> getMasterLine(Map<String, List<TcData>> mainTable, int maxRepetitions) {
      Set<Integer> setRepetitions = new HashSet<Integer>();

      for(List<TcData> data : mainTable.values()) {
         setRepetitions.addAll(getMasterLine(data, maxRepetitions));
      }

      // Add limits
//      setRepetitions.add(0);
      setRepetitions.add(1);
      setRepetitions.add(maxRepetitions);
      List<Integer> returnList = new ArrayList<Integer>(setRepetitions);
      Collections.sort(returnList);

      return returnList;
   }

    public static List<Long> getAbsReduxLine(TcData stat, int maxRepetitions, List<Integer> masterLine) {
      // Create array
       List<Long> results = new ArrayList<Long>();
      //long[] results = new long[maxRepetitions];

      Map<Integer, Long> table = stat.getInstPerRepetitions();

      // Get keys and order them
      //Collections.so
      List<Integer> keys = new ArrayList<Integer>(table.keySet());

      Collections.sort(keys);
       //System.err.println("Keys:"+keys);

      long instructions = stat.getTotalInstructions();
      int masterLineIndex = 0;
      for(Integer key : keys) {
         while(masterLine.get(masterLineIndex) <= key) {
//         while(masterLine.get(masterLineIndex) < key) {
            results.add(instructions);
            masterLineIndex++;
         }
         // Update instruction number
         instructions -= table.get(key);
      }

      /*
      long total = stat.getTotalInstructions();
      //int startRep = 0;
      int masterLineCounter = 0;
      int startRep = masterLine.get(masterLineCounter);
      masterLineCounter++;
      for(int i=0; i<keys.size(); i++) {
         int endRep = keys.get(i);
         long inst = table.get(endRep);

         int j=startRep;
         while(j<endRep && masterLineCounter<masterLine.size()) {
//            System.err.println("J="+j);
 //           System.err.println("endRed="+endRep);
            results.add(total);
            j = masterLine.get(masterLineCounter);
            masterLineCounter++;
         }

         
         //for(int j=startRep; j<endRep; j++) {
         //   results[j] = total;
         //}
          
          

         // Update values
         total-=inst;
         startRep = endRep;
      }
*/
      // Add the remaining ones
      for(int i=masterLineIndex; i<masterLine.size(); i++) {
         results.add(0l);
      }

      /*
      System.err.println("Table:");
      System.err.println(table);
      System.err.println("Result:");
      System.err.println(Arrays.toString(results));
       *
       */
       //System.err.println("Masterline Size:"+masterLine.size());
       //System.err.println("Results Size:"+results.size());
      return results;
   }

   public static boolean csvStart(File csvFile, List<Integer> masterLine) {
      // Create String
      StringBuilder builder = new StringBuilder();

      //builder.append(CSV_SEPARATOR);
      //builder.append(0);
      for(int i=0; i<masterLine.size(); i++) {
         //builder.append(",");
         builder.append(CSV_SEPARATOR);
         builder.append(masterLine.get(i));
      }
      builder.append("\n");

      // Create file
      return IoUtils.write(csvFile, builder.toString());
   }


}

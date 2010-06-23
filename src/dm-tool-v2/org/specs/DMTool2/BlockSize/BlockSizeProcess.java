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

import java.io.File;
import org.ancora.SharedLibrary.IoUtils;

/**
 *
 * @author Joao Bispo
 */
public class BlockSizeProcess {

   public static boolean csvStart(File csvFile) {
      // Create String
      StringBuilder builder = new StringBuilder();

      for (Parameter param : Parameter.values()) {
         builder.append(CSV_SEPARATOR);
         builder.append(param.name());
      }

      builder.append("\n");

      // Create file
      return IoUtils.write(csvFile, builder.toString());
   }
   
   public static boolean csvAppend(File csvFile, String partitionerName, BlockSizeData data) {
      // Create String
      StringBuilder builder = new StringBuilder();

      builder.append(partitionerName);

      // MIN
      builder.append(CSV_SEPARATOR);
      builder.append(data.getMinSize());

      // MAX
      builder.append(CSV_SEPARATOR);
      builder.append(data.getMaxSize());

      // Weighted Avg
      builder.append(CSV_SEPARATOR);
      builder.append(data.getWeightedAvg());

      builder.append("\n");

      // Append file
      return IoUtils.append(csvFile, builder.toString());
   }

   public static final String CSV_SEPARATOR = "\t";


   enum Parameter {

      min,
      max,
      weightedAvg;
   }
}

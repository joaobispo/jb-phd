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

package org.ancora.FuMatrix.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 *
 * @author Joao Bispo
 */
public class AvaliabilityTable {

   /**
    * If maxColumnSize is 0 or less, it assumes the table has infinite columns.
    * 
    * @param maxColumnSize
    */
   public AvaliabilityTable(int maxColumnSize) {
      this.generalLines = new HashMap<Integer, Integer>();
      this.maxColumnSize = maxColumnSize;
      lastFullLine = -1;
   }

   /**
    * Adds another column to the given line.
    *
    * @param line
    * @return the number of the column if could be added to line, or -1 if
    * it could not
    */
   public int addColToLine(int line) {
      boolean hasSpace = lineHasSpace(line);
      if(!hasSpace) {
         System.err.println("NO SPACE!");
         return -1;
      }

      Integer col = getLastUsedColumn(line);
      col++;

      generalLines.put(line, col);

      // Check if line got full

      if(!lineHasSpace(line)) {
         if(line > lastFullLine) {
            lastFullLine = line;
         }
      }

      return col;

   }

   public int getFirstAvaliableFast(int inputsLine) {
      // If negative, size is infinite
      /*
      if(maxColumnSize < 0) {
         return inputsLine;
      }
       * 
       */
      if(hasInfiniteColumns()) {
         return inputsLine;
      }
/*
      if(maxColumnSize == 0) {
         Logger.getLogger(AvaliabilityTable.class.getName()).
                 warning("Max column size is 0. No avaliable lines.");
         return -1;
      }
*/
      if(inputsLine < lastFullLine) {
         inputsLine = lastFullLine + 1;
      }

      return inputsLine;
   }

   /**
    * If the given line is avaliable, returns that line. If not, returns the
    * first line which has a free column.
    * 
    * @param inputsLine
    */
   public int getFirstAvaliableFrom(int inputsLine) {
      if(hasInfiniteColumns()) {
         return inputsLine;
      }
      /*
      // If negative, size is infinite
      if(maxColumnSize < 0) {
         return inputsLine;
      }

      if(maxColumnSize == 0) {
         Logger.getLogger(AvaliabilityTable.class.getName()).
                 warning("Max column size is 0. No avaliable lines.");
         return -1;
      }
*/
      boolean hasSpace = lineHasSpace(inputsLine);
      /*
      // Check if line is avaliable
      Integer lastUsedColumn = generalLines.get(inputsLine);
      // Line hasn't been used yet
      if(lastUsedColumn == null) {
         lastUsedColumn = -1;
      }

      int newcolumn = lastUsedColumn + 1;
      if(newcolumn < maxColumnSize) {
         return inputsLine;
      }*/

      while(!hasSpace) {
         inputsLine++;
         hasSpace = lineHasSpace(inputsLine);
      }

  /*
      // Iterate over the column until there is an avaliable place
      while (newcolumn >= maxColumnSize) {
         inputsLine++;
         // Check if line is avaliable
         lastUsedColumn = generalLines.get(inputsLine);
         // Line hasn't been used yet
         if (lastUsedColumn == null) {
            lastUsedColumn = -1;
         }

         newcolumn = lastUsedColumn + 1;
      }
*/
      return inputsLine;
   }

   private int getLastUsedColumn(int line) {
      Integer lastUsedColumn = generalLines.get(line);
      if(lastUsedColumn == null) {
         return -1;
      }

      return lastUsedColumn;
   }

   private boolean lineHasSpace(int line) {
      if(hasInfiniteColumns()) {
         return true;
      }
      /*
      if(maxColumnSize < 0) {
         return true;
      }
       *
       */

      int col = getLastUsedColumn(line);
      int lastColIndex = maxColumnSize-1;
      //System.err.println("Line:"+line);
      //System.err.println("LastUsedCol:"+col);
      //System.err.println("MaxColSize:"+maxColumnSize);
      if(col < lastColIndex) {
         return true;
      } else {
         return false;
      }
   }

   /**
    * INSTANCE VARIABLES
    */
   // Line, last occupied column
   private Map<Integer, Integer> generalLines;
   // Max column size (negative number for infinite)
   private int maxColumnSize;

   private int lastFullLine;

   private boolean hasInfiniteColumns() {
      if(maxColumnSize < 1) {
         return true;
      } else {
         return false;
      }
   }



}

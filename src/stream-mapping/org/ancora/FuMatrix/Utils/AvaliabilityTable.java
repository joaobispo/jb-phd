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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

   public AvaliabilityTable copy() {
      AvaliabilityTable newTable = new AvaliabilityTable(maxColumnSize);

      newTable.lastFullLine = lastFullLine;

      Map<Integer, Integer> newMap = new HashMap<Integer, Integer>();
      for(Integer key : generalLines.keySet()) {
         newTable.generalLines.put(key, generalLines.get(key));
      }
      newTable.generalLines = newMap;

      return newTable;
   }

   public int getLastFullLine() {
      return lastFullLine;
   }

   

   /**
    * Adds another column to the given line.
    *
    * @param line
    * @return the number of the column if could be added to line, or -1 
    * otherwise
    */
   public int addColToLine(int line) {
      boolean hasSpace = lineHasSpace(line);
      if(!hasSpace) {
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

   /**
    * Given a range of lines, returns the lines which have avaliable columns.
    *
    * @param startLine
    * @param numberOfLines
    * @return
    */
   public List<Integer> getAvaliableLines(int startLine, int numberOfLines) {
      List<Integer> lines = new ArrayList<Integer>();

      for(int i=0; i<numberOfLines; i++) {


         int probeLine = startLine+i;
      //            System.err.println("Range-line:"+probeLine);
      //            System.err.println("Range-column:"+getLastUsedColumn(probeLine));
         if(lineHasSpace(probeLine)) {
            lines.add(probeLine);
         }
      }
      //System.err.println("Range-Result:"+lines);
      return lines;
   }

   /**
    * Given a list of lines, returns the line which has the least number of
    * columns occupied. If two lines have the same number of columns occupied,
    * it is given priority to the line with the highest number (which comes last).
    *
    * @param avaliableLines
    * @return the line which has the least columns. If two or more lines have
    * the same number of columns, returns the highest line.
    */
   public int lessOccupiedLine(List<Integer> avaliableLines) {
      int smallerColumnSize = Integer.MAX_VALUE;
      int chosenLine = -1;

      for(Integer line : avaliableLines) {
//         System.err.println("Size:"+generalLines.size());
//         System.err.println("Line:"+line);

         int currentColumnSize = getLastUsedColumn(line);
         if(currentColumnSize <= smallerColumnSize) {
            chosenLine = line;
         }
      }

      return chosenLine;
   }

   /*
   public int getColSize(int line) {
      Integer columnSize = generalLines.get(line);
      if (columnSize == null) {
         return 0;
      }

      return columnSize;
   }
    *
    */

}

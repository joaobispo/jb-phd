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
import java.util.List;
import java.util.Map;
import org.ancora.FuMatrix.Architecture.Area;
import org.ancora.FuMatrix.Architecture.FuCoor;

/**
 *
 * @author Joao Bispo
 */
public class MoveService {

   /**
    *
    * @param sourceLine
    * @param destinationLine
    * @param testTable
    * @return a list of lines where MOVEs should be put, or null if a path could
    * not be done.
    */
   public static List<FuCoor> buildPath(int sourceLine, int destinationLine, int maxCommDistance,
           Map<Area,AvaliabilityTable> tables) {
      // Currently, MOVES only go along the general area
      // TODO: Generalize for all areas?
      Area defaultArea = Area.general;
      AvaliabilityTable testTable = tables.get(defaultArea);

      List<Integer> moveLines = new ArrayList<Integer>();
      List<Integer> moveColumns = new ArrayList<Integer>();

      //int distance = destinationLine - sourceLine - 1;
      int currentSource = sourceLine;
      int firstCandidateLine = sourceLine+1;
      //int distance = destinationLine - firstCandidateLine;
      //System.err.println("InitialDistance:"+distance);
      while(!canLinesCommunicate(currentSource, destinationLine, maxCommDistance)) {
         //System.err.println("Current Source:"+currentSource);
         //System.err.println("First Candidate Line:"+firstCandidateLine);
         //System.err.println("Destination Line:"+destinationLine);
         // Get possible lines for the given max comm
         List<Integer> avaliableLines = testTable.getAvaliableLines(firstCandidateLine, maxCommDistance);
         if(avaliableLines.isEmpty()) {
            return null;
         }

         // Choose the line which has the least communication
         //System.err.println("Candidate Lines:"+avaliableLines);
         currentSource = testTable.lessOccupiedLine(avaliableLines);
         //System.err.println("New Source:"+currentSource);

         // Line for MOVE chosen. Add to test table and update current source and distance

         // DO NOT ADD TO TEST TABLE YET
         //testTable.addColToLine(currentSource);
         moveLines.add(currentSource);
         int moveColumn = testTable.addColToLine(currentSource);
         moveColumns.add(moveColumn);
         firstCandidateLine = currentSource+1;

      }

      // Build Coordinates
      List<FuCoor> fuCoordinates = new ArrayList<FuCoor>(moveLines.size());
      for(int i=0; i<moveLines.size(); i++) {
         fuCoordinates.add(new FuCoor(moveColumns.get(i), moveLines.get(i), defaultArea));
      }

      return fuCoordinates;
   }

   /**
    * Indicates if two lines can communicate without using 'move' operations.
    *
    * @param sourceLine
    * @param destinationLine
    * @return true if the given lines can communicate, false otherwise
    */
   public static boolean canLinesCommunicate(int sourceLine, int destinationLine, int maxCommDistance) {
      if(isMaxCommDistanceInfinite(maxCommDistance)) {
         return true;
      }

      int maxLine = sourceLine + maxCommDistance;
      if(maxLine >= destinationLine) {
         return true;
      } else {
         return false;
      }
      // MaxComm indicates to how many lines can the sourceLine communicate
   }

   /**
    * Indicates if the given maxCommDistance represents infinite distance (eg.:
    * any line can comunicate with any other line above it)
    *
    * @param maxCommDistance
    * @return true if maxCommDistance is equal or greater than 1, false otherwise.
    */
   public static boolean isMaxCommDistanceInfinite(int maxCommDistance) {
      if(maxCommDistance < 1) {
         return true;
      } else {
         return false;
      }
   }

   public static String MOVE_REG_PREFIX = "moveReg";
}

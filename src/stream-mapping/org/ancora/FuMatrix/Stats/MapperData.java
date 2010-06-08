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

import java.util.HashSet;
import java.util.Set;
import org.ancora.FuMatrix.Architecture.Fu;
import org.ancora.FuMatrix.Architecture.Signal;
import org.ancora.FuMatrix.Architecture.Signal.SignalType;
import org.ancora.FuMatrix.Mapper.GeneralMapper;
import org.ancora.IntermediateRepresentation.OperationType;

/**
 *
 * @author Joao Bispo
 */
public class MapperData {

   private MapperData(int liveIns, int liveOuts, int lines, int ops, int moves) {
      this.liveIns = liveIns;
      this.liveOuts = liveOuts;
      this.lines = lines;
      this.ops = ops;
      this.moves = moves;
   }

public static MapperData build(GeneralMapper mapper) {
   int liveIns = 0;
   int lines = 0;
   int ops = 0;
   int moves = 0;

      int liveOuts = mapper.getLiveouts();

   Set<String> liveInsNames = new HashSet<String>();
   for (Fu fu : mapper.getMappedOps()) {
      // Check type of op
      if (fu.getOperationType() == OperationType.Move) {
         moves++;
      } else {
         ops++;
      }

      // Check number of lines
      lines = Math.max(lines, fu.getCoordinate().getLine());

      for(Signal signal : fu.getInputs()) {
         if(signal.getType() == SignalType.livein) {
            liveInsNames.add(signal.getName());
         }
      }
   }

    // lines start index at zero. Add one to get the number of lines.
    lines++;
    liveIns = liveInsNames.size();

    return new MapperData(liveIns, liveOuts, lines, ops, moves);
}

   public int getLines() {
      return lines;
   }

   public int getLiveIns() {
      return liveIns;
   }

   public int getLiveOuts() {
      return liveOuts;
   }

   public int getMoves() {
      return moves;
   }

   public int getOps() {
      return ops;
   }



   private int liveIns;
   private int liveOuts;
   private int lines;
   private int ops;
   private int moves;
}

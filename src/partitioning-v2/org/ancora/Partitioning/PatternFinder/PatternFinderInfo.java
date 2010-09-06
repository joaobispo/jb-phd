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

package org.ancora.Partitioning.PatternFinder;

/**
 * Encapsulates the information extracted by PatternFinder
 *
 * @author Joao Bispo
 */
public class PatternFinderInfo {

   public PatternFinderInfo(int paternSize, PatternState patternState) {
      this.paternSize = paternSize;
      this.patternState = patternState;
   }

   public int getPaternSize() {
      return paternSize;
   }

   public PatternState getPatternState() {
      return patternState;
   }

   

   private final int paternSize;
   private final PatternState patternState;

   public enum PatternState {
      PATTERN_STOPED,
      PATTERN_STARTED,
      PATTERN_CHANGED_SIZES,
      PATTERN_UNCHANGED,
      PATTERN_NOT_FOUND
   }
}

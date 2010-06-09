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

package org.ancora.FuMatrix.Architecture;

/**
 *
 * @author Joao Bispo
 */
public class FuCoor {

   public FuCoor(int col, int line, Area area) {
      this.column = col;
      this.line = line;
      this.area = area;
   }

   public int getCol() {
      return column;
   }

   public int getLine() {
      return line;
   }

   public Area getArea() {
      return area;
   }



   public String getLineColString() {
      return area + SEPARATOR + line + SEPARATOR + column;
   }

   @Override
   public String toString() {
      return "col "+column +"; line "+line;
   }

   private int column;
   private int line;
   private Area area;

   public static final String SEPARATOR = ".";
}

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
public class FuOutputSignal extends Signal {

   public FuOutputSignal(FuCoor coor, int position) {
      this.coor = coor;
      this.position = position;
   }

   

   @Override
   public SignalType getType() {
      return SignalType.fuOutput;
   }

   @Override
   public String getName() {
      //return PREFIX+position+SEPARATOR+coor.getLineColString();
      return coor.getLineColString()+SEPARATOR+position;
   }

   public FuCoor getCoordinate() {
      return coor;
   }

   public int getPosition() {
      return position;
   }

   /**
    * INSTANCE VARIABLES
    */
   private FuCoor coor;
   private int position;

   public static final String PREFIX = "FuOut";
   public static final String SEPARATOR = "-";
}

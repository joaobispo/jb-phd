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

package org.ancora.DMTool.Stats;

import java.math.BigInteger;
import java.util.Map;

/**
 *
 * @author Joao Bispo
 */
public class BigIntTransformData {

   public BigIntTransformData() {
      for(Parameter param : Parameter.values()) {
         data.put(param, BigInteger.ZERO);
      }

      /*
      this.numberOfLines = new BigInteger("0");
      this.numberOfOperations = new BigInteger("0");
      this.liveins = liveins = new BigInteger("0");
      this.liveouts = liveouts = new BigInteger("0");
      this.repetitions = repetitions = new BigInteger("0");
       *
       */
   }

   /*
   public BigInteger getLiveins() {
      return liveins;
   }

   public BigInteger getLiveouts() {
      return liveouts;
   }

   public BigInteger getNumberOfLines() {
      return numberOfLines;
   }

   public BigInteger getNumberOfOperations() {
      return numberOfOperations;
   }

   public BigInteger getRepetitions() {
      return repetitions;
   }
    *
    */

   public void addValue(Parameter parameter, int value) {
      BigInteger oldValue = data.get(parameter);
      BigInteger newValue = oldValue.add(new BigInteger(Integer.toString(value)));

      // Check if BigInteger is justified
      int result = newValue.compareTo(new BigInteger(Long.toString(Long.MAX_VALUE)));
      if(result > 0) {
         System.out.println("BIG INTEGER JUSTIFIED FOR PARAMETER '"+parameter+"'");
      }

      data.put(parameter, newValue);
   }

   public BigInteger getValue(Parameter parameter) {
      return data.get(parameter);
   }

   /**
    * INSTANCE VARIABLES
    */
   /*
   private BigInteger numberOfLines;
   private BigInteger numberOfOperations;
   private BigInteger liveins;
   private BigInteger liveouts;
   private BigInteger repetitions;
*/
   Map<Parameter, BigInteger> data;

   /**
    * ENUM PROPERTIES
    */
   public enum Parameter {
      lines,
      operations,
      liveIns,
      liveOuts,
      repetitions;
/*
      private Parameter() {
         value = new BigInteger("0");
      }

      public void addValue(int addValue) {
         value = value.add(new BigInteger(Integer.toString(addValue)));
      }
      
      private BigInteger value;
 * 
 */
   }
}

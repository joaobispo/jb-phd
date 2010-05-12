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


/**
 *
 * @author Joao Bispo
 */
public class LongTransformDataTotal {

   public LongTransformDataTotal() {
      data = new LongTransformDataSingle();
      //dataWithRepetitions = new LongTransformDataSingle();
      dataCounter = 0;

   }

   public void addValues(LongTransformDataSingle data) {
      //long repetitions = data.getValue(Parameter.cycles);
      for(TransformParam param : TransformParam.values()) {

         /*
         long repetitionsMod = repetitions;
         if(param == Parameter.cycles) {
            repetitionsMod = 1;
         }
          *
          */

         long value = data.getValue(param);
         this.data.addValue(param, value);
         //this.dataWithRepetitions.addValue(param, value*repetitions);
      }
      dataCounter++;
   }

   /*
   public void addValue(Parameter parameter, long value) {
      Long oldValue = data.get(parameter);
      Long newValue = oldValue + value;

      data.put(parameter, newValue);
   }
    *
    */

   public LongTransformDataSingle getTotalData() {
      return data;
   }

   public DoubleTransformDataSingle getAverageData() {
      DoubleTransformDataSingle averageData = new DoubleTransformDataSingle();
      
      for(TransformParam param : TransformParam.values()) {
         long originalValue = data.getValue(param);
         Double average = (double)originalValue / (double)dataCounter;
         averageData.addValue(param, average);
      }
      
      return averageData;
   }



   /*
   public LongTransformDataSingle getTotalDataWithRepetitions() {
      return dataWithRepetitions;
   }
    *
    */

   /*
   public LongTransformDataSingle getAverageData() {
      LongTransformDataSingle averageData = new LongTransformDataSingle();

      long total =
      for(Parameter param : Parameter.values()) {
         long value = data.getValue(param);
         this.data.addValue(param, value);
         this.dataWithRepetitions.addValue(param, value*repetitions);
      }

      return dataWithRepetitions;
   }
    *
    */

   /*
   public Long getValue(Parameter parameter) {
      return data.getValue(parameter);
   }

   public Long getValueRepe(Parameter parameter) {
      return data.getValue(parameter);
   }
    *
    */

   /**
    * 
    * @return the number of times LongTransformData was added to this object.
    */
   public long getDataCounter() {
      return dataCounter;
   }



   /**
    * INSTANCE VARIABLES
    */
   LongTransformDataSingle data;
   private long dataCounter;


}

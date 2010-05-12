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

import java.util.EnumMap;
import java.util.Map;

/**
 *
 * @author Joao Bispo
 */
public class GeneralTransformDataSingle<T extends Number> {

   public GeneralTransformDataSingle() {
      data = new EnumMap<Parameter, T>(Parameter.class);
      Long zero = 0l;
      for(Parameter param : Parameter.values()) {
         data.put(param, (T)zero);
      }
      //dataCounter = 0;

   }

   /*
   public void addValues(LongTransformDataSingle data) {
      for(Parameter param : Parameter.values()) {
         long value = data.getValue(param);
         addValue(param, value);
      }
      dataCounter++;
   }
    *
    */

/*
   public void addValue(Parameter parameter, T value) {
      T oldValue = data.get(parameter);
      Long newValue = (T)oldValue + (T)value;

      data.put(parameter, (T)newValue);
   }
*/
   
   public T getValue(Parameter parameter) {
      return data.get(parameter);
   }

   /**
    * 
    * @return the number of times LongTransformData was added to this object.
    */
   /*
   public long getDataCounter() {
      return dataCounter;
   }
    *
    */



   /**
    * INSTANCE VARIABLES
    */
   Map<Parameter, T> data;
   //private long dataCounter;

   /**
    * ENUM PROPERTIES
    */
   public enum Parameter {
      mappedLines,
      executedLines,
      mappedOperations,
      executedOperations,
      liveIns,
      liveOuts,
      cycles;
   }
}

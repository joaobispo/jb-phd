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
public class DoubleTransformDataSingle {

   public DoubleTransformDataSingle() {
      data = new EnumMap<TransformParam, Double>(TransformParam.class);

      for(TransformParam param : TransformParam.values()) {
         data.put(param, 0d);
      }

   }

   
   public void addValue(TransformParam parameter, double value) {
      Double oldValue = data.get(parameter);
      Double newValue = oldValue + value;

      data.put(parameter, newValue);
   }

   public Double getValue(TransformParam parameter) {
      return data.get(parameter);
   }



   /**
    * INSTANCE VARIABLES
    */
   Map<TransformParam, Double> data;
}

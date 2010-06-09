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

package org.ancora.StreamTransform;

import org.ancora.IntermediateRepresentation.Operation;
import org.ancora.StreamTransform.Stats.OperationFrequency;

/**
 *
 * @author Joao Bispo
 */
public abstract class StreamTransformation {

  public abstract Operation transform(Operation operation);

  public abstract String getName();

   public StreamTransformation() {
      operationFrequency = new OperationFrequency();
   }


   public OperationFrequency getOperationFrequency() {
      return operationFrequency;
   }



   /**
    * INSTANCE VARIABLES
    */
    //private Map<OperationType, Integer> stats;
   /**
    * You can use this object to keep track of what and how operations where
    * modified by this transformation.
    */
    protected OperationFrequency operationFrequency;

}

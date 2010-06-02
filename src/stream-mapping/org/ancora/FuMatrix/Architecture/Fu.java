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

import java.util.Map;
import org.ancora.IntermediateRepresentation.Operation;

/**
 *
 * @author Joao Bispo
 */
public class Fu {

   public Fu(FuCoor coor, Map<FuOutput, FuInput> internalRouting, Map<String, FuInput> externalRouting, Operation operation, Map<String, FuOutput> liveouts, Area area) {
      this.coor = coor;
      this.internalRouting = internalRouting;
      this.externalRouting = externalRouting;
      this.operation = operation;
      this.liveouts = liveouts;
      this.area = area;
   }

   public FuCoor getCoordinate() {
      return coor;
   }

   public Map<String, FuInput> getExternalRouting() {
      return externalRouting;
   }

   public Map<FuOutput, FuInput> getInternalRouting() {
      return internalRouting;
   }

   public Map<String, FuOutput> getLiveouts() {
      return liveouts;
   }

   public Operation getOperation() {
      return operation;
   }



   /**
    * INSTANCE VARIABLES
    */
   private FuCoor coor;
   private Map<FuOutput, FuInput> internalRouting;
   private Map<String, FuInput> externalRouting;
   private Operation operation;
   private Map<String, FuOutput> liveouts;
   private Area area;

   public enum Area {
      general,
      memory;
   }
}

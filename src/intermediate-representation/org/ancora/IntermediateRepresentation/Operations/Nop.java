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

package org.ancora.IntermediateRepresentation.Operations;

import org.ancora.IntermediateRepresentation.OperationType;
import org.ancora.IntermediateRepresentation.Operation;

/**
 *
 * @author Joao Bispo
 */
public class Nop extends Operation {


   public Nop(Operation operation) {
      super(operation.getAddress());

      this.operation = operation;
      //this.name = name;
   }

   /*
   public Nop(int address, String name) {
      super(address);
      this.name = name;
   }
    */



   @Override
   public Enum getType() {
      return OperationType.Nop;
   }

   @Override
   public boolean hasSideEffects() {
      return false;
   }

   @Override
   public String getName() {
      //return "NOP "+name;
      //return "NOP";
      return "NOP "+operation.getFullOperation();
   }

   @Override
   public String toString() {
      return getName();
   }

   private Operation operation;

   @Override
   public Operation copy() {
      return new Nop(operation);
   }
   //private String name;

}

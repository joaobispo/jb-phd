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

package org.ancora.IntermediateRepresentation;

/**
 *
 * @author Joao Bispo
 */
public enum OperationType {

   IntegerArithmeticWithCarry,
   Control,
   MockOperation,
   Logic,
   //Exit,
   //Mux,
   ConditionalExit,
   UnconditionalExit,
   Nop,
   Division,
   SignExtension,
   MemoryLoad,
   MemoryStore,
   Multiplication,
   ShiftRight,
   Move;

      public boolean isMemoryOperation() {
      if (this.equals(MemoryLoad) || this.equals(MemoryStore)) {
         return true;
      }

      return false;
   }

   public boolean isLoad() {
      if(this.equals(MemoryLoad)) {
         return true;
      } else {
         return false;
      }
   }

   public boolean isStore() {
      if(this.equals(MemoryStore)) {
         return true;
      } else {
         return false;
      }
   }
}

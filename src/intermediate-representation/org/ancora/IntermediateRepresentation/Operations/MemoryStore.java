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
import org.ancora.IntermediateRepresentation.Operand;
import org.ancora.IntermediateRepresentation.Operation;

/**
 * <p><b>Inputs:</b>
 * <br>input1 - Contents to store.
 * <br>input2 - first operand.
 * <br>input3 - second operand.
 *
 * <p><b>Parameters:</b>
 * <br>bytes - size of the store.
 *
 *  <b>Description</b>: Stores the contents on input1 in the location that
 * results from adding the contents of input2 and input3.
 * The memory location is aligned acording to the number of bytes indicated by
 * 'bytes'.
 *
 * @author Joao Bispo
 */
public class MemoryStore extends Operation {

   public MemoryStore(int address, Operand input1, Operand input2, Operand input3, int bytes) {
      super(address);

      //this.input1 = input1;
      //this.input2 = input2;
      //this.input3 = input3;
      this.bytes = bytes;

      connectToInput(input1);
      connectToInput(input2);
      connectToInput(input3);
   }



   @Override
   public Enum getType() {
      return OperationType.MemoryStore;
   }

   @Override
   public boolean hasSideEffects() {
      return true;
   }

   public int getBytes() {
      return bytes;
   }


   public Operand getContentsToStore() {
      //return input1;
      return getInputs().get(0);
   }

   public Operand getOperand1() {
      //return input2;
      return getInputs().get(1);
   }

   public Operand getOperand2() {
      //return input3;
      return getInputs().get(2);
   }




   @Override
   public String toString() {
      return "store "+(bytes*8);
   }



   /**
    * INSTANCE VARIABLES
    */
   //private Operand input1;
   //private Operand input2;
   //private Operand input3;
   private int bytes;
}

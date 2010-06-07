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

import org.ancora.IntermediateRepresentation.MbOperationType;
import java.util.List;
import org.ancora.IntermediateRepresentation.Operand;
import org.ancora.IntermediateRepresentation.Operation;
import org.ancora.MicroBlaze.InstructionName;

/**
 *
 * @author Joao Bispo
 */
public class MbOperation extends Operation {

   public MbOperation(int address, InstructionName operationName, List<Operand> inputs, List<Operand> outputs) {
      super(address);
      this.instructionName = operationName;
      this.type = MbOperationType.MicroBlazeOperation;

      for(Operand input : inputs) {
         this.connectToInput(input);
      }

      for(Operand output : outputs) {
         this.connectToOutput(output);
      }
   }



   @Override
   public Enum getType() {
      return type;
   }

   @Override
   public String getName() {
      return "mb-"+instructionName.name();
   }

   @Override
   public String toString() {
      return getName();
   }

   /*
   @Override
   public String getValue() {
      return instructionName.name();
   }
    */



   @Override
   public boolean hasSideEffects() {
      // List of instructions with side-effects
      throw new UnsupportedOperationException("Not supported yet.");
   }

   public InstructionName getMbType() {
      return instructionName;
   }

   /**
    * @param operation
    * @return an MbOperation if the given operation is of the type
    * MbOperationType.MicroBlazeOperation. Null otherwise.
    */
   public static MbOperation getMbOperation(Operation operation) {
      if(operation.getType() == MbOperationType.MicroBlazeOperation) {
         return (MbOperation)operation;
      } else {
         return null;
      }
   }

   public static InstructionName getMbInstructionName(Operation operation) {
      if(operation.getType() == MbOperationType.MicroBlazeOperation) {
         return ((MbOperation)operation).getMbType();
      } else {
         return null;
      }
   }

   /**
    * INSTANCE VARIABLES
    */
   private InstructionName instructionName;
   private MbOperationType type;

   @Override
   public Operation copy() {
      return new MbOperation(getAddress(), instructionName, getInputs(), getOutputs());
   }





}

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

import java.util.List;
import org.ancora.IntermediateRepresentation.OperandType;
import org.ancora.IntermediateRepresentation.Operation;
import org.ancora.IntermediateRepresentation.OperationType;
import org.ancora.IntermediateRepresentation.Operations.Logic;
import org.ancora.IntermediateRepresentation.Operations.Nop;

/**
 * MicroBlaze inserts nops instructions in the format or r0, r0, r0. This
 * transformation removes them.
 *
 * @author Joao Bispo
 */
public class RemoveR0Or extends StreamTransformation {

   public RemoveR0Or() {
   }

   @Override
   public String getName() {
      return NAME;
   }

   @Override
   public String toString() {
      return getName();
   }

   public static void transform(List<Operation> operations) {
      // Create Remove R0 Or StreamTransform
      StreamTransformation orTransf = new RemoveR0Or();
      for(Operation operation : operations) {
         orTransf.transform(operation);
      }
   }

   @Override
   public Operation transform(Operation operation) {

      // Test if it is an or, and has all operands as literal 0
      if(operation.getType() != OperationType.Logic) {
         return operation;
      }
      //System.err.println("Logic Op:"+operation.getFullOperation());
      Logic logicOp = (Logic)operation;
      if(logicOp.getOutput1().getType() != OperandType.literal) {
         return operation;
      }

      //System.err.println("Nopping "+operation.getFullOperation());

      // Build nop
      operationFrequency.addOperation(operation);
      return new Nop(operation);
   }
   
   public static final String NAME = "Remove R0 OR";



}

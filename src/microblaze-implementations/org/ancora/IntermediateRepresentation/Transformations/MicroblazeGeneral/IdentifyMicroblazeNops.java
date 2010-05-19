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

package org.ancora.IntermediateRepresentation.Transformations.MicroblazeGeneral;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.ancora.IntermediateRepresentation.Operand;
import org.ancora.IntermediateRepresentation.Operation;
import org.ancora.IntermediateRepresentation.Operations.MbOperation;
import org.ancora.IntermediateRepresentation.Operations.Nop;
import org.ancora.MicroBlaze.InstructionName;
import org.ancora.IntermediateRepresentation.MbTransformUtils;
import org.ancora.IntermediateRepresentation.Operands.MbImm;
import org.ancora.IntermediateRepresentation.Transformation;

/**
 *
 * @author Joao Bispo
 */
public class IdentifyMicroblazeNops extends Transformation {

   /**
    * Detects and removes MicroBlaze nops (or r0, r0, r0)
    *
    * <p>Changes input operations.
    * 
    * @param operations
    * @return
    */
   public void transform(List<Operation> operations) {
   //public void transform(List<Operation> operations) {
      //List<Operation> newList = new ArrayList<Operation>(operations.size());
System.out.println("This transformation, "+toString()+", was disabled.");
      //for(Operation operation : operations) {
/*
      for(int i=0; i<operations.size(); i++) {
         Operation operation = operations.get(i);

         MbOperation mbOperation = MbOperation.getMbOperation(operation);
         if(mbOperation == null) {
            continue;
         }

         // Identify OR r0, r0, r0
         boolean isOr = mbOperation.getMbType() == InstructionName.or;
         if(!isOr) {
            continue;
         }

         //Integer outputValue = MbTransformUtils.getIntegerValue(mbOperation.getOutputs().get(0));
         Integer outputValue = MbImm.getImmValue(mbOperation.getOutputs().get(0));
         if(outputValue == null) {
            continue;
         }

         operations.set(i, new Nop(mbOperation));
         // Check if value == 0
         if(outputValue != 0) {
            Logger.getLogger(IdentifyMicroblazeNops.class.getName()).
                    warning("Removing an Or with output other than 0 ("+outputValue+")");
         }
*/

         /*
         //boolean remove = hasLiteralsAsOnlyOutput(operation.getOutputs());
         boolean nop = hasLiteralsAsOnlyOutput(operation.getOutputs());
         if(nop && !operation.hasSideEffects()) {
            operations.set(i, new Nop(operation.getAddress(), operation.toString()));
            // Check if is instruction other than OR:
            boolean isOr = ((MbOperation)operation).getMbType() == InstructionName.or;
            if(!isOr) {
               Logger.getLogger(IdentifyMicroblazeNops.class.getName()).
                       warning("Removed operation besides OR:"+operation.getAddress()+":"+operation);
            }
         }
          */
/*
         if(!remove) {
            newList.add(operation);
         } else {
            // Check if is instruction other than OR:
            boolean isOr = ((MbOperation)operation).getMbType() == InstructionName.or;
            if(!isOr) {
               Logger.getLogger(IdentifyMicroblazeNops.class.getName()).
                       warning("Removed operation besides OR:"+operation);
            }
         }
 */


//    }

      //return newList;
   }

/*
   private boolean hasLiteralsAsOnlyOutput(List<Operand> outputs) {
      // Check if all outputs are to register 0
      boolean isZero = false;

      // If it finds one "not zero", returns false.
      for(Operand operand : outputs) {
         isZero = isLiteral(operand);
         if(!isZero) {
            return false;
         }
      }

      return isZero;
   }
 */
/*
   private boolean isLiteral(Operand operand) {
      // Check for Literals
      Operand newOperand = MbTransformUtils.transformOperandToLiteral(operand);
      if (newOperand != null) {
         return true;
      } else {
         return false;
      }
   }
*/
   @Override
   public String toString() {
      //return RegisterZeroToLiteral.class.getName();
      return "IdentifyNops";
   }



}

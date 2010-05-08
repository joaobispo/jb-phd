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

package org.ancora.IntermediateRepresentation.Transformations.MicroblazeInstructions;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.ancora.IntermediateRepresentation.Operand;
import org.ancora.IntermediateRepresentation.Operands.Literal;
import org.ancora.IntermediateRepresentation.Operands.MbImm;
import org.ancora.IntermediateRepresentation.Operation;
import org.ancora.IntermediateRepresentation.Operations.MbOperation;
import org.ancora.IntermediateRepresentation.Operations.Nop;
import org.ancora.MicroBlaze.InstructionName;
import org.ancora.MicroBlaze.Definitions;
import org.ancora.SharedLibrary.ParseUtils;
import org.ancora.IntermediateRepresentation.MbTransformUtils;
import org.ancora.IntermediateRepresentation.MbOperationType;
import org.ancora.IntermediateRepresentation.Transformation;

/**
 * Fuses the value of IMM instructions to the immediate value of the next
 * instruction. 
 * 
 * <p>Because this transformation depends on the positions of the IMM in the
 * Microblaze instructions, this transformation must be done before the MbOperations
 * are changed to IR Operations.
 *
 * @author Joao Bispo
 */
public class RemoveImmInstruction implements Transformation {

   @Override
   public String toString() {
      return "RemoveImmInstruction";
   }


   public List<Operation> transform(List<Operation> operations) {
     //List<Operation> newList = new ArrayList<Operation>(operations.size());

     for(int i=0; i<operations.size(); i++) {
        Operation operation = operations.get(i);

        /*
        // Check if MicroBlaze operation
        MbOperation immOperation = MbOperation.getMbOperation(operations.get(i));
        if(immOperation == null) {
           continue;
        }*/

        // Check if IMM
        //if(immOperation.getMbType() != InstructionName.imm) {

        //operation.getType() == MbOperationType.MicroBlazeOperation
        InstructionName instName = MbOperation.getMbInstructionName(operation);
        //if(operation.getType() != InstructionName.imm) {
        if(instName != InstructionName.imm) {
           continue;
        }

        MbOperation immOperation = (MbOperation)operation;

        // Collect imm value from IMM operation
        //Integer upper16 = MbTransformUtils.getIntegerValue(immOperation.getInputs().get(0));
        Integer upper16 = MbImm.getImmValue(immOperation.getInputs().get(0));

        // Collect imm value and imm from next instruction
        Operation nextOperation = operations.get(i+1);
        if(nextOperation.getType() != MbOperationType.MicroBlazeOperation) {
           Logger.getLogger(RemoveImmInstruction.class.getName()).
                   warning("Next operation is of type '"+nextOperation.getType()
                   +"', instead of type "+MbOperationType.MicroBlazeOperation);
           Logger.getLogger(RemoveImmInstruction.class.getName()).
                   warning("Please, place this transformation before other transformations " +
                   "which change the MicroBlaze operations.");
           continue;
        }
        //MbOperation nextOperation = MbOperation.getMbOperation(operations.get(i+1));

        // Assume next operation is of type B
        List<Operand> inputs = nextOperation.getInputs();

        // Imm is always the last operand of a MicroBlaze instruction
        int immIndex = inputs.size()-1;
        Operand immOperand = inputs.get(immIndex);
        //int lower16 = MbTransformUtils.getIntegerValue(immOperand);
        int lower16 = MbImm.getImmValue(immOperand);
        int completeInt = fuseImm(upper16, lower16);

        // Replace Operand for a Literal
        MbImm newImm = new MbImm(completeInt, BITS_FUSED_IMM);
        nextOperation.replaceInput(immIndex, newImm);


        //inputs.set(immIndex, new Literal(Literal.LiteralType.integer,
        //        String.valueOf(completeInt), BITS_FUSED_IMM));


        operations.set(i, new Nop(immOperation));
     }

     // Add last operation
     //newList.add(operations.get(operations.size()-1));


     return operations;
     //return newList;
   }

   /**
    * Fuses the lower 16 bits of two ints.
    *
    * TODO: Verify correcteness.
    * <p>Ex.:
    * upper16 = 1001
    * lower16 = 101
    * result = 00000000000010010000000000000101
    *
    * @param upper16
    * @param lower16
    * @return
    */
   public static int fuseImm(int upper16, int lower16){
      // Mask the 16 bits of each one
      upper16 = upper16 & Integer.parseInt("0000FFFF", 16);
      lower16 = lower16 & Integer.parseInt("0000FFFF", 16);
      // Shift Upper16
      upper16 = upper16 << 16;
      // Merge
      return upper16 | lower16;
   }

   public static int BITS_FUSED_IMM = 32;


}

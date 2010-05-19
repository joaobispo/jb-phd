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

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.ancora.IntermediateRepresentation.Operand;
import org.ancora.IntermediateRepresentation.Operation;
import org.ancora.IntermediateRepresentation.Operations.MbOperation;
import org.ancora.IntermediateRepresentation.Operations.MockOperation;
import org.ancora.IntermediateRepresentation.Operations.UnconditionalExit;
import org.ancora.MicroBlaze.InstructionName;
import org.ancora.IntermediateRepresentation.MbTransformUtils;
import org.ancora.IntermediateRepresentation.Operands.MbImm;
import org.ancora.IntermediateRepresentation.Transformation;

/**
 *
 * @author Joao Bispo
 */
public class ParseUnconditionalBranches extends Transformation {

   public void transform(List<Operation> operations) {
      //List<Operation> newList = new ArrayList<Operation>();
      //Map<String, Integer> literalRegisters = new Hashtable<String, Integer>();

      for(int i=0; i<operations.size(); i++) {
         Operation operation = operations.get(i);
         //System.out.println("Address:"+operation.getAddress());
//         substituteRegisterForLiterals(operation, literalRegisters);

        // Check if MicroBlaze Operation
        MbOperation branchOp = MbOperation.getMbOperation(operation);
        if(branchOp == null) {
           //newList.add(operation);
           continue;
        }

        // Check if it is an unconditional compare
        UnconditionalProperties unconditionalProperties =
                instructionProperties.get(branchOp.getMbType());
        if(unconditionalProperties == null) {
           //newList.add(operations.get(i));
           continue;
        }

        if(checkSpecialCondition(branchOp)) {
           String name = "bralid rD, 0x8";
           operations.set(i, new MockOperation(branchOp.getAddress(), name));
           //newList.add(new MockOperation(branchOp.getAddress(), name));
           Logger.getLogger(ParseUnconditionalBranches.class.getName()).
                   warning("Found special case:"+name);
           continue;
        }

        // Calculate next address
        int nextSupposedAddress = MbTransformUtils.calculateNextAddress(operations, i,
                branchOp.getMbType());

        // Calculate base address
        int baseAddress;
        if(unconditionalProperties.isAbsolute) {
           baseAddress = 0;
        } else {
           baseAddress = branchOp.getAddress();
        }

        // Check if it has delay slots
        int delaySlots = MbTransformUtils.getDelaySlots(branchOp.getMbType());

        // Get input 1
        Operand input1 = branchOp.getInputs().get(0).copy();
        Operand output1 = null;

        if(unconditionalProperties.performsLinking) {
           //input1 = branchOp.getInputs().get(1);
            // Update table

           //literalRegisters.put(branchOp.getOutputs().get(0).toString(), branchOp.getAddress());
           output1 = branchOp.getOutputs().get(0).copy();

           //} else {
           //input1 = branchOp.getInputs().get(0);
        }

        // Check if input1 is literal
        /*
        Integer input1Value = MbTransformUtils.getIntegerValue(input1);
        if(input1Value != null) {
            // Confirm that next address is block is correct
           int calculatedJump = baseAddress + input1Value;
           if(!(nextSupposedAddress == calculatedJump)) {
              Logger.getLogger(ParseUnconditionalBranches.class.getName()).
                      warning("Unconditional branch mismatch: Calculated jump address " +
                      "("+calculatedJump+") different from next trace address ("+
                      nextSupposedAddress+")");
              System.out.println(branchOp+" "+branchOp.getInputs()+";"+branchOp.getOutputs());
              System.out.println("this address:"+branchOp.getAddress()+"; next address:"+nextSupposedAddress);
           }
           // Do not add instructions
           //operations.set(i, new Nop(branchOp.getAddress(), branchOp.toString()));
           operations.set(i, new Nop(branchOp));
           continue;
        }
         */

        // Create UnconditionalExit operation
        UnconditionalExit newOperation = new UnconditionalExit(branchOp.getAddress(),
                baseAddress, nextSupposedAddress, delaySlots, input1, output1);

        operations.set(i, newOperation);
        //newList.add(newOperation);
      }

   }
/*
   private void substituteRegisterForLiterals(Operation operation,
           Map<String, Integer> literalRegisters) {
      // Check inputs, if an input matches an element from the table,
      //substitute it for a literal
      List<Operand> operands = operation.getInputs();
      for(int i=0; i<operands.size(); i++) {
         Integer literalValue = literalRegisters.get(operands.get(i).toString());
         if(literalValue != null) {
            //System.out.println("Register substituted by Literal because of Uncondition Branch. Address "+operation.getAddress());
            //System.out.println("Before:"+operation.getInputs());
            Literal newLiteral = new Literal(Literal.LiteralType.integer,
                    literalValue.toString(), Definitions.BITS_REGISTER);
            operands.set(i, newLiteral);
            //Logger.getLogger(ParseUnconditionalBranches.class.getName())
            //System.out.println("After:"+operation.getInputs());
         }
      }

      // Check if outputs matches an element from the table. In that case,
      // remove element from the table.
      List<Operand> outputs = operation.getOutputs();
      for(int i=0; i<outputs.size(); i++) {

         String key = outputs.get(i).toString();
         Integer literalValue = literalRegisters.get(key);
         if (operation.getAddress() == 2228) {
            //System.out.println("ENTERED");
            //System.out.println("Key:" + key);
            //System.out.println("Table:" + literalRegisters);
         }

         if(literalValue != null) {
            Integer previousValue = literalRegisters.remove(key);
            //System.out.println("Removed key '"+key+"' with value "+previousValue+".");
         }
      }
   }
*/
   private boolean checkSpecialCondition(MbOperation branchOp) {
      // Check if is bralid instruction
      if(!(branchOp.getMbType() == InstructionName.bralid)) {
         return false;
      }

      // Check if it as a value
      //Integer value = MbTransformUtils.getIntegerValue(branchOp.getInputs().get(1));
      //System.out.println("MbOp:"+branchOp.getFullOperation());
      //System.out.println("MbOp:"+branchOp.getInputs());
      Integer value = MbImm.getImmValue(branchOp.getInputs().get(0));
      //Integer value = MbImm.getImmValue(branchOp.getInputs().get(1));
      if(value == null) {
         return false;
      }

      // Check if value is 8
      if(value == 8) {
         return true;
      } else {
         return false;
      }
   }

   @Override
   public String toString() {
      return "ParseUnconditionalBranches";
   }

   /**
    * INSTANCE VARIABLES
    */
   //Map<String, String> literalRegisters;

   /**
    * INNER CLASS
    */
   /**
    * INSTANCE VARIABLES
    */
   static class UnconditionalProperties {

      public UnconditionalProperties(boolean usesPcAsBase, boolean storesPcInInput1) {
         this.isAbsolute = usesPcAsBase;
         this.performsLinking = storesPcInInput1;
      }

      /**
       * INSTANCE VARIABLES
       */
      private boolean isAbsolute;
      private boolean performsLinking;
   }

   private static final Map<InstructionName, UnconditionalProperties> instructionProperties;
   static {
      Map<InstructionName, UnconditionalProperties> aMap = new EnumMap<InstructionName, UnconditionalProperties>(InstructionName.class);

      aMap.put(InstructionName.br, new UnconditionalProperties(false, false));
      aMap.put(InstructionName.bra, new UnconditionalProperties(true, false));
      aMap.put(InstructionName.brd, new UnconditionalProperties(false, false));
      aMap.put(InstructionName.brad, new UnconditionalProperties(true, false));
      aMap.put(InstructionName.brld, new UnconditionalProperties(false, true));
      aMap.put(InstructionName.brald, new UnconditionalProperties(true, true));

      aMap.put(InstructionName.bri, new UnconditionalProperties(false, false));
      aMap.put(InstructionName.brai, new UnconditionalProperties(true, false));
      aMap.put(InstructionName.brid, new UnconditionalProperties(false, false));
      aMap.put(InstructionName.braid, new UnconditionalProperties(true, false));
      aMap.put(InstructionName.brlid, new UnconditionalProperties(false, true));
      aMap.put(InstructionName.bralid, new UnconditionalProperties(true, true));

      instructionProperties = Collections.unmodifiableMap(aMap);
   }
}

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

import static org.ancora.IntermediateRepresentation.Operations.ArithmeticWithCarry.Op.*;

import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import org.ancora.IntermediateRepresentation.Operand;
import org.ancora.IntermediateRepresentation.Operation;
import org.ancora.IntermediateRepresentation.Operations.ArithmeticWithCarry;
import org.ancora.IntermediateRepresentation.Operations.MbOperation;
import org.ancora.MicroBlaze.InstructionName;
import org.ancora.IntermediateRepresentation.MbTransformUtils;
import org.ancora.IntermediateRepresentation.Transformation;

/**
 *
 * @author Joao Bispo
 */
public class ParseCarryArithmetic implements Transformation {

   @Override
   public String toString() {
      return "ParseCarryArithmetic";
   }

   public List<Operation> transform(List<Operation> operations) {
     for(int i=0; i<operations.size(); i++) {

        // Check if MicroBlaze operation
        MbOperation arithmeticOperation = MbOperation.getMbOperation(operations.get(i));
        if(arithmeticOperation == null) {
           continue;
        }

        InstructionName mbInstruction = arithmeticOperation.getMbType();

        // Get arithmetic operation
        ArithmeticWithCarry.Op irOperation =
                getArithmeticOperation(mbInstruction);

        // Check if Arithmetic with carry
        if(irOperation == null) {
           continue;
        }

        // Get CarryIn
        Operand carryIn = getCarryIn(mbInstruction);
        Operand carryOut = getCarryOut(mbInstruction);

        Operand input1 = arithmeticOperation.getInputs().get(0).copy();
        Operand input2 = arithmeticOperation.getInputs().get(1).copy();
        Operand output1 = arithmeticOperation.getOutputs().get(0).copy();

        Operation newOperation = new ArithmeticWithCarry(arithmeticOperation.getAddress(),
                irOperation, input1, input2, output1, carryIn, carryOut);

        // Replace old operation
        operations.set(i, newOperation);
        //System.out.println("Old Op:");
        //System.out.println(arithmeticOperation);
        //System.out.println("New Op:");
        //System.out.println(newOperation);
     }



     return operations;
   }

  



   /**
    * INSTANCE VARIABLES
    */
   static class ArithmeticProperties {

      public ArithmeticProperties(ArithmeticWithCarry.Op operation, boolean hasCarryIn, boolean hasCarryOut) {
         this.operation = operation;
         this.hasCarryIn = hasCarryIn;
         this.hasCarryOut = hasCarryOut;
      }

      /**
       * INSTANCE VARIABLES
       */
      private ArithmeticWithCarry.Op operation;
      private boolean hasCarryIn;
      private boolean hasCarryOut;
   }

   private static final Map<InstructionName, ArithmeticProperties> instructionProperties;
   static {
      Map<InstructionName, ArithmeticProperties> aMap = new Hashtable<InstructionName, ArithmeticProperties>();

      aMap.put(InstructionName.add, new ArithmeticProperties(add, false, true));
      aMap.put(InstructionName.addc, new ArithmeticProperties(add, true, true));
      aMap.put(InstructionName.addi, new ArithmeticProperties(add, false, true));
      aMap.put(InstructionName.addic, new ArithmeticProperties(add, true, true));
      aMap.put(InstructionName.addik, new ArithmeticProperties(add, false, false));
      aMap.put(InstructionName.addikc, new ArithmeticProperties(add, true, false));
      aMap.put(InstructionName.addk, new ArithmeticProperties(add, false, false));
      aMap.put(InstructionName.addkc, new ArithmeticProperties(add, true, false));

      aMap.put(InstructionName.rsub, new ArithmeticProperties(rsub, false, true));
      aMap.put(InstructionName.rsubc, new ArithmeticProperties(rsub, true, true));
      aMap.put(InstructionName.rsubi, new ArithmeticProperties(rsub, false, true));
      aMap.put(InstructionName.rsubic, new ArithmeticProperties(rsub, true, true));
      aMap.put(InstructionName.rsubik, new ArithmeticProperties(rsub, false, false));
      aMap.put(InstructionName.rsubikc, new ArithmeticProperties(rsub, true, false));
      aMap.put(InstructionName.rsubk, new ArithmeticProperties(rsub, false, false));
      aMap.put(InstructionName.rsubkc, new ArithmeticProperties(rsub, true, false));


      instructionProperties = Collections.unmodifiableMap(aMap);
   }

   // Definitions
   /*
   EnumSet<InstructionName> addInstructions = EnumSet.of(
           InstructionName.add,
           InstructionName.addc,
           InstructionName.addi,
           InstructionName.addic,
           InstructionName.addik,
           InstructionName.addikc,
           InstructionName.addk,
           InstructionName.addkc);

   EnumSet<InstructionName> subInstructions = EnumSet.of(
           InstructionName.rsub,
           InstructionName.rsubc,
           InstructionName.rsubi,
           InstructionName.rsubic,
           InstructionName.rsubik,
           InstructionName.rsubikc,
           InstructionName.rsubk,
           InstructionName.rsubkc);

    private static final EnumSet<InstructionName> hasCarryOut = EnumSet.of(
           InstructionName.add,
           InstructionName.addc,
           InstructionName.addi,
           InstructionName.addic,
           InstructionName.rsub,
           InstructionName.rsubc,
           InstructionName.rsubi,
           InstructionName.rsubic);

   private static final EnumSet<InstructionName> hasCarryIn = EnumSet.of(
           InstructionName.addc,
           InstructionName.addkc,
           InstructionName.addic,
           InstructionName.addikc,
           InstructionName.rsubc,
           InstructionName.rsubkc,
           InstructionName.rsubic,
           InstructionName.rsubikc
           );
    */

   /*
   private boolean isArithmeticWithCarry(InstructionName instructionName) {
      return addInstructions.contains(instructionName)
              || subInstructions.contains(instructionName);
   }
    */

   /**
    *
    * @param instructionName
    * @return null if is not an arithmetic operation with carry
    */

   private ArithmeticWithCarry.Op getArithmeticOperation(InstructionName instructionName) {
      ArithmeticProperties props = instructionProperties.get(instructionName);

      if(props == null) {
         return null;
      } else {
         return props.operation;
      }

      /*
      if(addInstructions.contains(instructionName)) {
         return ArithmeticWithCarry.Op.add;
      }

      if(subInstructions.contains(instructionName)) {
         return ArithmeticWithCarry.Op.rsub;
      }
       */

   }

   private Operand getCarryIn(InstructionName instructionName) {
      ArithmeticProperties props = instructionProperties.get(instructionName);

      if(props == null) {
         return null;
      }

      if(!props.hasCarryIn) {
         return null;
      }

      return MbTransformUtils.createCarryOperand();
   }

   private Operand getCarryOut(InstructionName instructionName) {
      ArithmeticProperties props = instructionProperties.get(instructionName);

      if(props == null) {
         return null;
      }

      if(!props.hasCarryOut) {
         return null;
      }

      return MbTransformUtils.createCarryOperand();
   }

}

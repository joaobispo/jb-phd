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

import java.util.ArrayList;
import java.util.List;
import org.ancora.IntermediateRepresentation.OperationType;
import java.util.logging.Logger;
import org.ancora.IntermediateRepresentation.Operand;
import org.ancora.IntermediateRepresentation.OperandType;
import org.ancora.IntermediateRepresentation.Operands.Literal;
import org.ancora.IntermediateRepresentation.Operation;
import org.ancora.IntermediateRepresentation.OperationService;

/**
 *
 * @author Joao Bispo
 */
public class ArithmeticWithCarry extends Operation {


   public ArithmeticWithCarry(int address, ArithmeticWithCarry.Op operation, Operand input1, Operand input2, Operand output1, Operand carryIn, Operand carryOut) {
      super(address);
      this.operation = operation;


      // Connect Inputs
      connectToInput(input1);
      connectToInput(input2);
      if(carryIn == null) {
         hasCarryIn = false;
      } else {
         connectToInput(carryIn);
         hasCarryIn = true;
      }

      // Connect Outputs
      connectToOutput(output1);
      if(carryOut == null) {
         hasCarryOut = false;
      } else {
         connectToOutput(carryOut);
         hasCarryOut = true;
      }
   }



   @Override
   public Enum getType() {
      return OperationType.IntegerArithmeticWithCarry;
   }

   @Override
   public String getName() {
      return operation.name();
   }

   @Override
   public String toString() {
      return getName();
   }


   @Override
   public boolean hasSideEffects() {
      return false;
   }

   /**
    *
    * @return
    */
   /*
   public Integer resolveInput1() {
      return Literal.getInteger(getInput1());
   }
    * 
    */

   /*
   public Integer resolveInput2() {

      return Literal.getInteger(getInput2());
   }
    *
    */
   
   public Operand getInput1() {
      return getInputs().get(0);
   }

   public Operand getInput2() {
      return getInputs().get(1);
   }


   public Operand getCarryIn() {
      if(hasCarryIn) {
         return getInputs().get(2);
      } else {
         return null;
      }
      //return carryIn;
   }

   public Operand getCarryOut() {
      if(hasCarryOut) {
         //return getInputs().get(1);
         return getOutputs().get(1);
      } else {
         return null;
      }
      //return carryOut;
   }

   public Operand getOutput() {
      return getOutputs().get(0);
      //return output1;
   }

   public Op getOperation() {
      return operation;
   }

   /**
    * It assumes the inputs are literals.
    *
    * @return
    */
   private Integer resolveOutput() {
      // Get first operand integer
//      Integer firstOperand = resolveInput1();
      Integer firstOperand = Literal.getInteger(getInput1());
      /*
      if(firstOperand == null) {
         return null;
      }
       *
       */

      // Get second operand
      //Integer secondOperand = resolveInput2();
      Integer secondOperand = Literal.getInteger(getInput2());
      /*
      if(secondOperand == null) {
         return null;
      }
       *
       */

      // Atributte default value to carry
      Integer carryInValue = null;
      if (operation == Op.add) {
         carryInValue = 0;
      } else if (operation == Op.rsub) {
         carryInValue = 1;
      }
      
      // Update value if there is a carry in
      if(hasCarryIn) {
         carryInValue = Literal.getInteger(getCarryIn());
         /*
         if(carryInValue == null) {
            return null;
         }
          * 
          */
      }

      if(operation == Op.add) {
         return firstOperand + secondOperand + carryInValue;
      } else if(operation == Op.rsub) {
         //return secondOperand - firstOperand + carryInValue;
         return secondOperand + ~firstOperand + carryInValue;
      }
      
      Logger.getLogger(ArithmeticWithCarry.class.getName()).
              warning("Not defined:"+operation);

      return null;
   }

   /**
    * Assumes that the inputs are literals and that there is a carry out.
    * 
    * @return
    */
   private Integer resolveCarryOut() {
      if(!hasCarryOut) {
         return null;
      }

      // Get first operand
      //Integer firstOperand = resolveInput1();
      Integer firstOperand = Literal.getInteger(getInput1());
      if(firstOperand == null) {
         return null;
      }

      // Get second operand
      //Integer secondOperand = resolveInput2();
      Integer secondOperand = Literal.getInteger(getInput2());
      if(secondOperand == null) {
         return null;
      }

      // Get carry
      Integer carryInValue = null;
      if (operation == Op.add) {
         carryInValue = 0;
      } else if (operation == Op.rsub) {
         carryInValue = 1;
      }

      //if(carryIn != null) {
      if(hasCarryIn) {
         carryInValue = Literal.getInteger(getCarryIn());
         if(carryInValue == null) {
            return null;
         }
      }

      if(operation == Op.add) {
         return OperationService.getCarryOutAdd(firstOperand, secondOperand, carryInValue);
      } else if(operation == Op.rsub) {
         return OperationService.getCarryOutRsub(firstOperand, secondOperand, carryInValue);
      }

      Logger.getLogger(ArithmeticWithCarry.class.getName()).
              warning("Not defined:"+operation);

      return null;
   }

   @Override
   public Operation copy() {
      Operand newCarryIn = null;
      if(getCarryIn() != null) {
         newCarryIn = getCarryIn().copy();
      }
      Operand newCarryOut = null;
      if(getCarryOut() != null) {
         newCarryOut = getCarryOut().copy();
      }
      return new ArithmeticWithCarry(getAddress(), operation, getInput1().copy(),
              getInput2().copy(), getOutput().copy(), newCarryIn, newCarryOut);
   }




   

   public enum Op {
      add,
      rsub;
   }

   /**
    * INSTANCE VARIABLES
    */
   private boolean hasCarryIn;
   private boolean hasCarryOut;

   private ArithmeticWithCarry.Op operation;



    /*
    public static List<Operand> resolve(ArithmeticWithCarry arithmeticWithCarry) {
      //List<Literal> literals = new ArrayList<Literal>();
      List<Operand> resolvedOperands = new ArrayList<Operand>();

      // Check if both literal
      resolvedOperands = resolveLiterals(arithmeticWithCarry);
      if(resolvedOperands != null) {
         return resolvedOperands;
      }

      // Check if one is neutral element
      resolvedOperands = resolveNeutral(arithmeticWithCarry);
      if(resolvedOperands != null) {
         return resolvedOperands;
      }

      // Nothing more to do
      return null;

   }
*/
  
    /**
     * Resolves Arithmetic Operations with carry
     *
     * @param arithmeticWithCarry
     * @return the resolved outputs of this operation, if it can be resolved, or
     * null if it can't.
     */
   @Override
    public List<Operand> resolveWhenLiteralInputs() {
      // Check if inputs are literals
      if(!OperationService.hasLiteralInputs(this)) {
         return null;
      }

      // Literals inputs. Prepare return list.
      List<Operand> resultOperands = new ArrayList<Operand>();

      // Calculate value
      Integer resultValue = resolveOutput();
      /*
      if (resultValue == null) {
         return null;
      }
       *
       */
/*
      Literal resultLiteral = new Literal(Literal.LiteralType.integer,
              resultValue.toString(), getOutput().getBits());
 *
 */
      Literal resultLiteral = Literal.newIntegerLiteral(resultValue, getOutput().getBits());

      resultOperands.add(resultLiteral);


      // If there is no carry out, return list as is.
      if(!hasCarryOut) {
         return resultOperands;
      }

      Integer carryOutValue = resolveCarryOut();
      /*
      if (carryOutValue == null) {
         return resultOperands;
      }
       *
       */

      Literal resultCarry = new Literal(Literal.LiteralType.integer,
              carryOutValue.toString(), getCarryOut().getBits());

      resultOperands.add(resultCarry);

      return resultOperands;
   }

   /**
    *
    * @param arithmeticWithCarry
    * @return the resolved outputs of this operation, if it can be resolved, or
    * null if it can't.
    */
   public List<Operand> resolveNeutralInput() {
      // Check if it is addition
      if (operation != Op.add) {
         return null;
      }

      // Check if one of the inputs is zero, and get the corresponding operand
      //if it has carry in, if it is zero.
      Operand nonNeutralOperand = getNonNeutralOperand();
      if (nonNeutralOperand == null) {
         return null;
      }

      /*
      if (arithmeticWithCarry.operation != Op.add) {
         System.err.println("SubOperation with zero:");
         System.err.println(arithmeticWithCarry.operation.toString());
         return null;
      }
       *
       */

      // Check if it has carry in, and if carry is neutral
      if (hasCarryIn) {
         Integer carryInValue = Literal.getInteger(getCarryIn());
         // Value of carry in is not known
         if (carryInValue == null) {
            return null;
         }
         // Value of carry is different than zero
         if (carryInValue != 0) {
            return null;
         }
      }

      List<Operand> resultOperands = new ArrayList<Operand>();
      resultOperands.add(nonNeutralOperand);

      // Check if there is a carry out
      if (hasCarryOut) {
         // Use literal 0 instead of carry out
         int bits = getCarryOut().getBits();
         resultOperands.add(new Literal(Literal.LiteralType.integer, "0", bits));
      }


      return resultOperands;

   }

      private Operand getNonNeutralOperand() {
      Operand in1 = getInput1();
      Operand in2 = getInput2();

      // Check if first is Literal and second is Operand
      boolean LD = in1.getType() == OperandType.literal
              && in2.getType() == OperandType.internalData;

      if (LD) {
         // Swap
         Operand temp = in1;
         in1 = in2;
         in2 = temp;
      }


      // Check if is Operand and other is Literal
      boolean DL = in1.getType() == OperandType.internalData
              && in2.getType() == OperandType.literal;

      if (!DL) {
         return null;
      }


      if (Literal.getInteger(in2) != 0) {
         return null;
      }

      return in1;

   }

    private static final long MASK_32_BITS = 0xFFFFFFFFL;
    private static final long MASK_BIT_33 = 0x100000000L;
}

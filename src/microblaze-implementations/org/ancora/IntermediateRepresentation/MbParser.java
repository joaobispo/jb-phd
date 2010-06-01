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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.ancora.InstructionBlock.GenericInstruction;
import org.ancora.InstructionBlock.InstructionBlock;
import org.ancora.InstructionBlock.MbBlockUtils;
import org.ancora.IntermediateRepresentation.Operands.MbImm;
import org.ancora.IntermediateRepresentation.Operands.MbRegister;
import org.ancora.IntermediateRepresentation.Operations.MbOperation;
import org.ancora.IntermediateRepresentation.Transformations.MicroblazeGeneral.*;
import org.ancora.IntermediateRepresentation.Transformations.MicroblazeInstructions.*;
import org.ancora.IntermediateRepresentation.Transformations.SingleStaticAssignment;
import org.ancora.MicroBlaze.ArgumentsProperties;
import org.ancora.MicroBlaze.ArgumentsProperties.ArgumentProperty;
import org.ancora.MicroBlaze.InstructionName;
import org.ancora.SharedLibrary.ParseUtils;

/**
 *
 * @author Joao Bispo
 */
public class MbParser {
   public static List<Operation> parseMbInstructions(List<GenericInstruction> instructions) {
      List<Operation> operations = new ArrayList(instructions.size());
      
      for(GenericInstruction instruction : instructions) {
         Operation op = parseMbInstruction(instruction);
         if(op != null) {
            operations.add(op);
         }
      }

      return operations;
   }

   public static Operation parseMbInstruction(GenericInstruction instruction) {
      // Parse arguments
      String[] arguments = parseArguments(instruction.getInstruction());

      // Get arguments properties
      InstructionName instructionName = MbBlockUtils.getInstructionName(instruction);
      ArgumentProperty[] argProps = ArgumentsProperties.getProperties(instructionName);

      // Check arguments properties have the same size as the arguments
      if(arguments.length != argProps.length) {
         Logger.getLogger(MbParser.class.getName()).
                 warning("Number of arguments ("+arguments.length+") different from " +
                 "the number of properties ("+argProps.length+") for instruction '"+
                 instructionName+"'. Returning null.");
         return null;
      }

      // For each argument, return the correct operand
      Operand[] operands = new Operand[arguments.length];
      for (int i = 0; i < arguments.length; i++) {
         //System.out.println("Arg:" + arguments[i]);
         //System.out.println("Prop:" + argProp[i]);
         operands[i] = parseMbArgument(arguments[i]);
      }

      // Build Input and Output Lists
      List<Operand> inputs = new ArrayList<Operand>();
      List<Operand> outputs = new ArrayList<Operand>();

      for(int i=0; i< argProps.length; i++) {
         if(argProps[i] == ArgumentProperty.read) {
            inputs.add(operands[i]);
         }

         if(argProps[i] == ArgumentProperty.write) {
            outputs.add(operands[i]);
         }
      }

      return new MbOperation(instruction.getAddress(), instructionName, inputs, outputs);
   }

   public static String[] parseArguments(String instruction) {
      int whiteSpaceIndex = ParseUtils.indexOfFirstWhiteSpace(instruction);
      String registersString = instruction.substring(whiteSpaceIndex).trim();

      String[] regs = registersString.split(",");
      for(int i=0; i<regs.length; i++) {
         regs[i] = regs[i].trim();
      }

      return regs;
   }

   public static Operand parseMbArgument(String argument) {
       // Check if register
      if(argument.startsWith(REGISTER_PREFIX)) {
         try {
         String stringValue = argument.substring(REGISTER_PREFIX.length());
         int value = Integer.parseInt(stringValue);
         return new MbRegister(argument, value);
         //return new MbOperand(Type.register, value, MbDefinitions.BITS_REGISTER);
         } catch(NumberFormatException ex) {
         Logger.getLogger(MbParser.class.getName()).
                 warning("Expecting an microblaze register (e.g., R3): '" + argument + "'.");
      }
      }

      // Check if integer immediate
      try {
         int value = Integer.parseInt(argument);
         return new MbImm(value);
         //return new MbOperand(Type.immediate, value, MbDefinitions.BITS_IMMEDIATE);
      } catch(NumberFormatException ex) {
         Logger.getLogger(MbParser.class.getName()).
                 warning("Expecting an integer immediate: '" + argument + "'.");
      }

      return null;
   }

   /**
    * Transforms a block of MicroBlaze Instructions into a list of IR Operations.
    * 
    * @param block
    * @return
    */
   public static List<Operation> mbToOperations(InstructionBlock block) {
      // Transform block in List of operations
      List<Operation> operations = MbParser.parseMbInstructions(block.getInstructions());

      // Transform operations in pure IR operations
      for(Transformation transf : microblazeTransformations) {
         transf.transform(operations);
         //operations = transf.transform(operations);
         // Update live-outs
      }

      if(!isPureIr(operations)) {
         return null;
      }

      return operations;
   }

   // Transforms an InstructionBlock into a Pure-IR list of Operations (already in SSA).
   //public static List<Operation> mbToPureIr(InstructionBlock block) {
   /**
    * Transforms a block of MicroBlaze Instructions into a IrBlock with IR 
    * Operations in SSA format.
    * 
    * @param block
    * @return
    */
   public static IrBlock mbToIrBlock(InstructionBlock block) {
      /*
      // Transform block in List of operations
      List<Operation> operations = MbParser.parseMbInstructions(block.getInstructions());

      // Transform operations in pure IR operations
      for(Transformation transf : microblazeTransformations) {
         transf.transform(operations);
         //operations = transf.transform(operations);
         // Update live-outs
      }

      if(!isPureIr(operations)) {
         return null;
      }

       *
       */
      List<Operation> operations = mbToOperations(block);

      if(operations == null) {
         return null;
      }

      IrBlock irBlock = new IrBlock(operations);

      (new SingleStaticAssignment()).transform(irBlock);

      return irBlock;
   }

   /**
    * TODO: Generalize this method and check for IR instead of others.
    * 
    * @param operations
    * @return true if there are no MicroBlaze operations nor operands
    */
   public static boolean isPureIr(List<Operation> operations) {
            // Check that there are no microblaze operations  nor operands
      for(Operation operation : operations) {
         if(MbOperation.getMbOperation(operation) != null) {
            Logger.getLogger(MbParser.class.getName()).
                    warning("Could not transform block of MicroBlaze instructions " +
                    "int a pure intermediate representation, due to operation '"+operation+"'");
            return false;
         }

         for(Operand operand : operation.getInputs()) {
            if(operand.getType() == MbOperandType.MbImm ||
                    operand.getType() == MbOperandType.MbRegister) {
                Logger.getLogger(MbParser.class.getName()).
                    warning("Could not transform block of MicroBlaze instructions " +
                    "int a pure intermediate representation, due to input operand '"+operand+"'");
            return false;
            }
         }

         for(Operand operand : operation.getOutputs()) {
            if(operand.getType() == MbOperandType.MbImm ||
                    operand.getType() == MbOperandType.MbRegister) {
                Logger.getLogger(MbParser.class.getName()).
                    warning("Could not transform block of MicroBlaze instructions " +
                    "int a pure intermediate representation, due to output operand '"+operand+"'");
            return false;
            }
         }
      }

      return true;
   }

   public static final Transformation[] microblazeTransformations = {
 //        new TransformImmToLiterals(),
 //        new RegisterZeroToImm(),

//         new RemoveImmInstruction(),
//         new IdentifyMicroblazeNops(),


         //Because this transformation depends on the positions of the IMM in the
         //Microblaze instructions, this transformation must be done before the MbOperations
         //are changed to IR Operations.

         new RemoveImmInstruction(),

         //new IdentifyMicroblazeNops(), //Maybe should wait until constant propagation?

         // Parse Mb Operations into IR Operations

         new ParseCarryArithmetic(),
         new ParseConditionalBranch(),
         new ParseUnconditionalBranches(),
         new ParseLogic(),
         new ParseDivision(),
         new ParseSignExtension(),
         new ParseReturnSubroutine(),
         new ParseLoads(),
         new ParseStores(),
         new ParseMultiplication(),
         new ParseShiftRight(),


         // Parse MbOperands and IR Operands
         new RegisterZeroToImm(),
         new TransformImmToLiterals(),
         new TransformRegistersToInternalData(),


         // Further transform the now pure-ir representation
         //new SingleStaticAssignment()
      };



   public static final String REGISTER_PREFIX = "r";
}

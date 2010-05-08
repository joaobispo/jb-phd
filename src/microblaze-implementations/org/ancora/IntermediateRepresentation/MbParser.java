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
import java.util.List;
import java.util.logging.Logger;
import org.ancora.InstructionBlock.GenericInstruction;
import org.ancora.InstructionBlock.MbInstruction;
import org.ancora.IntermediateRepresentation.Operands.MbImm;
import org.ancora.IntermediateRepresentation.Operands.MbRegister;
import org.ancora.IntermediateRepresentation.Operations.MbOperation;
import org.ancora.MicroBlaze.ArgumentsProperties;
import org.ancora.MicroBlaze.ArgumentsProperties.ArgumentProperty;
import org.ancora.SharedLibrary.ParseUtils;

/**
 *
 * @author Joao Bispo
 */
public class MbParser {
   public static List<Operation> parseMbInstructions(List<GenericInstruction> instructions) {
      List<Operation> operations = new ArrayList(instructions.size());
      
      for(GenericInstruction instruction : instructions) {
         Operation op = parseMbInstruction((MbInstruction) instruction);
         if(op != null) {
            operations.add(op);
         }
      }

      return operations;
   }

   public static Operation parseMbInstruction(MbInstruction mbInstruction) {
      // Parse arguments
      String[] arguments = parseArguments(mbInstruction.getInstruction());

      // Get arguments properties
      ArgumentProperty[] argProps = ArgumentsProperties.getProperties(mbInstruction.getInstructionName());

      // Check arguments properties have the same size as the arguments
      if(arguments.length != argProps.length) {
         Logger.getLogger(MbParser.class.getName()).
                 warning("Number of arguments ("+arguments.length+") different from " +
                 "the number of properties ("+argProps.length+") for instruction '"+
                 mbInstruction.getInstructionName()+"'. Returning null.");
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

      return new MbOperation(mbInstruction.getAddress(), mbInstruction.getInstructionName(), inputs, outputs);
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


   public static final String REGISTER_PREFIX = "r";
}

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

package org.ancora.DMTool.deprecated;

import org.ancora.IntermediateRepresentation.Transformations.MicroblazeInstructions.*;
import org.ancora.IntermediateRepresentation.Transformations.MicroblazeGeneral.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.ancora.InstructionBlock.InstructionBlock;
//import org.ancora.InstructionBlock.MbInstructionBlockWriter;
import org.ancora.IntermediateRepresentation.MbParser;
import org.ancora.IntermediateRepresentation.Operand;
import org.ancora.IntermediateRepresentation.MbOperandType;
import org.ancora.IntermediateRepresentation.Operation;
import org.ancora.IntermediateRepresentation.Operations.MbOperation;
import org.ancora.IntermediateRepresentation.Transformations.SingleStaticAssignment;
import org.ancora.IntermediateRepresentation.Transformation;

/**
 *
 * @author Joao Bispo
 */
public class TransformUtils {

   // Transforms an InstructionBlock into a Pure-IR list of Operations.
   public static List<Operation> mbToPureIr(InstructionBlock block) {
      // Transform block in List of operations
      List<Operation> operations = MbParser.parseMbInstructions(block.getInstructions());

      // Transform operations in pure IR operations
      for(Transformation transf : microblazeTransformations) {
         operations = transf.transform(operations);
      }

      // Check that there are no microblaze operations  nor operands
      for(Operation operation : operations) {
         if(MbOperation.getMbOperation(operation) != null) {
            Logger.getLogger(TransformUtils.class.getName()).
                    warning("Could not transform block of MicroBlaze instructions " +
                    "int a pure intermediate representation, due to operation '"+operation+"'");
            return null;
         }

         for(Operand operand : operation.getInputs()) {
            if(operand.getType() == MbOperandType.MbImm ||
                    operand.getType() == MbOperandType.MbRegister) {
                Logger.getLogger(TransformUtils.class.getName()).
                    warning("Could not transform block of MicroBlaze instructions " +
                    "int a pure intermediate representation, due to input operand '"+operand+"'");
            return null;
            }
         }

         for(Operand operand : operation.getOutputs()) {
            if(operand.getType() == MbOperandType.MbImm ||
                    operand.getType() == MbOperandType.MbRegister) {
                Logger.getLogger(TransformUtils.class.getName()).
                    warning("Could not transform block of MicroBlaze instructions " +
                    "int a pure intermediate representation, due to output operand '"+operand+"'");
            return null;
            }
         }
      }

      return operations;
   }

   
   // Applies transformations given in the array

   // TODO: Could be in package InstructionBlock
   /**
    * File is a instruction block.
    *
    * @param file
    * @param blocks
    */
   /*
   public static List<InstructionBlock> blockLoader(File file) {
   //private void addBlocksLoader(File file, List<NamedBlock> blocks) {
      List<InstructionBlock> list = new ArrayList<InstructionBlock>();

      InstructionBlock block = MbInstructionBlockWriter.loadInstructionBlock(file);
      if(block == null) {
         Logger.getLogger(TransformUtils.class.getName()).
                 warning("Could not load block file '"+file+"'.");
         return list;
      }


      list.add(block);
      return list;
    *
    */
      /*
      String blockName = file.getName();
      int separatorIndex = blockName.lastIndexOf(Definitions.EXTENSION_SEPARATOR);
      blockName = blockName.substring(0, separatorIndex);

      blocks.add(new NamedBlock(block, blockName));
       */
   //}

  

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
         new SingleStaticAssignment()
      };
}

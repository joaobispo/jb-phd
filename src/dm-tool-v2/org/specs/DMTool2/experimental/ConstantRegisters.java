/*
 *  Copyright 2010 SPECS Research Group.
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

package org.specs.DMTool2.experimental;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.ancora.FuMatrix.Stats.MapperData;
import org.ancora.InstructionBlock.InstructionBlock;
import org.ancora.IntermediateRepresentation.MbParser;
import org.ancora.IntermediateRepresentation.Operand;
import org.ancora.IntermediateRepresentation.OperandType;
import org.ancora.IntermediateRepresentation.Operation;
import org.specs.DMTool2.Simulator.SimulationData;

/**
 *
 * @author Joao Bispo
 */
public class ConstantRegisters {

   public static void testBlock(InstructionBlock block, MapperData mapperData) {

      //// IT SEEMS THAT LIVEIN MAPPERDATA IS NOT WORKING!
      //Set<String> liveIns1 = getLiveIns(block);
      //System.err.println("LiveIns1:"+liveIns1);

      Set<String> liveIns2 = getLiveInsUsingType(block);
      //System.err.println("LiveIns2:"+liveIns2);

      /*
      if(!liveIns1.equals(liveIns2)) {
         System.err.println("DIFFERENCE");
      }
       *
       */


       List<String> liveOuts = mapperData.getLiveOutsNames();
       // Iterate over liveins, do not add to constant if in liveOuts
       List<String> constant = new ArrayList<String>();
       for(String liveIn : liveIns2) {
          if(!liveOuts.contains(liveIn)) {
             constant.add(liveIn);
          }
       }

       //System.err.println("Constant Live-Ins:"+constant.size());
       //System.err.println(constant);
       
       if(!constant.isEmpty() && block.getInstructions().size() > 4) {
          System.err.println(constant);
          System.err.println(block);
       }

       if(!constant.isEmpty() && block.getInstructions().size() > 4) {
          //System.err.println("!");
       }

       //System.err.println("?");
/*
      for(String liveOut : mapperData.getLiveOutsNames()) {
         System.err.println("LiveOut:"+liveOut);
      }
*/

   }

   private static Set<String> getLiveIns(InstructionBlock block) {
      Set<String> writes = new HashSet<String>();
      Set<String> reads = new HashSet<String>();

      // Calculate live-ins:
      List<Operation> operations = MbParser.mbToOperations(block);
      for(Operation operation : operations) {
         // Iterate over inputs
         // If input is not in output table, is a live-in
         List<Operand> operands = operation.getInputs();
         for(Operand operand : operands) {
            String inputName = operand.getName();
            boolean isRegister = !inputName.startsWith("integer");
            boolean isWritten = writes.contains(inputName);
            if(isRegister && !isWritten) {
               reads.add(inputName);
            }
         }

         // Iterate over outputs
         operands = operation.getOutputs();
         for(Operand operand : operands) {
            writes.add(operand.getName());
            //System.err.println("Output:"+operand.getName());
         }
      }

      return reads;
   }

   private static Set<String> getLiveInsUsingType(InstructionBlock block) {
      Set<String> reads = new HashSet<String>();

      // Calculate live-ins:
      List<Operation> operations = MbParser.mbToOperations(block);
      for(Operation operation : operations) {
         // Iterate over inputs
         List<Operand> operands = operation.getInputs();
         for(Operand operand : operands) {
            if(operand.getType() == OperandType.livein) {
               reads.add(operand.getName());
            }
         }
      }

      return reads;
   }
}

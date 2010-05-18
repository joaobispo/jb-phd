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

package org.ancora.IrMapping.Tools;

import org.ancora.IntermediateRepresentation.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.ancora.IntermediateRepresentation.Operands.InternalData;
import org.ancora.IntermediateRepresentation.Operations.Control;
import org.ancora.IntermediateRepresentation.Operations.MockOperation;
//import org.ancora.IntermediateRepresentation.Operations.OperationType;

/**
 *
 * @author Joao Bispo
 */
public class Dotty {

   public static List<Operation> removeNops(List<Operation> operations) {
      List<Integer> nopIndexes = new ArrayList<Integer>();

      for(int i=0; i<operations.size(); i++) {
         if(operations.get(i).getType() == OperationType.Nop) {
            nopIndexes.add(i);
         }
      }

      // Remove nops in reversal order
      for(int i=nopIndexes.size()-1; i>=0; i--) {
         operations.remove((int)nopIndexes.get(i));
      }

      return operations;
   }


   public static String generateDot(List<Operation> operations) {
      // Collect Operands
      Set<Operand> operands = collectOperands(operations);

      StringBuilder builder = new StringBuilder();

      builder.append("digraph graphname {\n");
      declareOperations(operations, builder);
      declareOperands(operands, builder);
      builder.append("\n");

      for(Operation operation: operations) {
         /*
         if(operation.getType() == OperationType.Nop) {
            continue;
         }
          */

         for(Operand operand : operation.getOutputs()) {
            builder.append(operation.hashCode());
            builder.append(" -> ");
            builder.append(operand.hashCode());
            builder.append(";\n");
         }
      }

      for(Operand operand : operands) {
         for(Operation operation : operand.getConsumers()) {
            builder.append(operand.hashCode());
            builder.append(" -> ");
            builder.append(operation.hashCode());
            builder.append(";\n");
         }
      }

      //appendOperationOutputs(start, builder, operands, operations);
      builder.append("}\n");

      return builder.toString();
   }

   private static Set<Operand> collectOperands(List<Operation> operations) {
      Set<Operand> operands = new HashSet<Operand>();

      for(Operation operation : operations) {
         operands.addAll(operation.getInputs());
         operands.addAll(operation.getOutputs());
         //operands.addAll(operation.getParameters());
      }

      return operands;
   }

      private static void declareOperations(Collection<Operation> operations, StringBuilder builder) {
      for(Operation op : operations) {
         /*
         if(op.getType() == OperationType.Nop) {
            continue;
         }
          */

         builder.append(op.hashCode());
         builder.append("[label=\"");
         //builder.append(op.getType()+"-"+op.getValue());
         //builder.append(op.getValue());
         builder.append(op);
         builder.append("\"];\n");
      }
   }

   private static void declareOperands(Collection<Operand> operands, StringBuilder builder) {
      for(Operand op : operands) {
         builder.append(op.hashCode());
         builder.append("[label=\"");
         //builder.append(getOperandLabel(op));
         builder.append(op.getPrefix());
         builder.append(op);
         builder.append("\", shape=box];\n");
      }
   }

   public static Integer getVersion(String operandName) {
      String separator = ".";
      int index = operandName.lastIndexOf(separator);
      if(index == -1) {
         return null;
      }

      try {
         return Integer.parseInt(operandName.substring(index+1, operandName.length()));
      } catch (NumberFormatException ex) {
         return null;
      }
   }

   public static String getNameWithoutVersion(String operandName) {
      String separator = ".";
      int index = operandName.lastIndexOf(separator);
      if(index == -1) {
         return null;
      }

      return operandName.substring(0, index);
   }

    /**
    * Changes input list.
    *
    * @param operations
    * @return
    */
   public static List<Operation> connectOperations(List<Operation> operations) {
      Map<String, Operand> liveIns = new HashMap<String, Operand>();
      Map<String, Operand> liveOuts = new HashMap<String, Operand>();
      Map<String, Integer> liveOutsLastVersion = new HashMap<String, Integer>();

      List<Operation> newList = new ArrayList<Operation>();
      Map<String, Operand> operands = new HashMap<String, Operand>();
      //Map<String, Integer> variablesVersion = new Hashtable<String, Integer>();
      //Map<String, Operand> operandsTable = new Hashtable<String, Operand>();
      //Operation start = new Control(-1, Control.Op.start);
      //newList.add(start);

      for(int i=0; i<operations.size(); i++) {
         Operation operation = operations.get(i);
        
         Operation dotOp = new MockOperation(operation.getAddress(), operation.toString());
         newList.add(dotOp);

         List<Operand> inputs = operation.getInputs();
         for(int j=0; j<inputs.size(); j++) {
            Operand input = inputs.get(j);
            // Check if immutable
            if(input.isImmutable()) {
               dotOp.connectToInput(input.copy());
               continue;
            }

            Operand realInput = operands.get(input.toString());
            if(realInput == null) {
               realInput = input.copy();
               operands.put(input.toString(), realInput);
            }

            dotOp.connectToInput(realInput);

            // Check if immutable
           /*
            if(realInput.isImmutable()) {
               //dotOp.connectToInput(input.copy());
               continue;
            }
            */


            // Get version number
            Integer version = getVersion(realInput.toString());
            if(version == null) {
               System.out.println("Could not get version from input "+realInput);
            }
            
            // Add to live-ins if version is 0
            if(version == 0) {
               liveIns.put(realInput.toString(), realInput);
               //System.out.println("STORED LIVE IN");
               /*
               if(!liveIns.containsKey(realInput.toString())) {
                  liveIns.put(input.toString(), input);
               }
                */
            }

            /*
            //String inputValue = input.getValue();
            String inputName = input.toString();
            // Get input index from table
            Integer version = variablesVersion.get(inputName);
            Operand newInput = operandsTable.get(inputName);

            if(version == null) {
               version = 0;
               //variablesVersion.put(input.getValue(), version);
               variablesVersion.put(inputName, version);
               String opValue = inputName+"."+version;
               newInput = new InternalData(opValue, input.getBits());
               operandsTable.put(inputName, newInput);
               newInput.connectToProducer(start);
            }

            // Add new Inputs to new operation
            dotOp.connectToInput(newInput);
             */
         }

         List<Operand> outputs = operation.getOutputs();
         for(int j=0; j<outputs.size(); j++) {
            Operand output = outputs.get(j);

            // Check if immutable
            if(output.isImmutable()) {
               dotOp.connectToOutput(output.copy());
               continue;
            }

            Operand realOutput = operands.get(output.toString());
            if(realOutput == null) {
               realOutput = output.copy();
               operands.put(output.toString(), realOutput);
            }


            dotOp.connectToOutput(realOutput);



            //String outputValue = output.getValue();
            String outputName = realOutput.toString();

            // Check last version of this output
            Integer version = getVersion(outputName);
            String outputNameClean = getNameWithoutVersion(outputName);
            if(version == null) {
               System.out.println("Could not get version from output "+realOutput);
            }


            //Integer lastVersion = liveOutsLastVersion.get(outputName);
            Integer lastVersion = liveOutsLastVersion.get(outputNameClean);
            // There was not a last version
            if(lastVersion == null) {
               // This version becomes most recent version
               //liveOutsLastVersion.put(outputName, version);
               liveOutsLastVersion.put(outputNameClean, version);
               //liveOuts.put(outputName, realOutput);
               liveOuts.put(outputNameClean, realOutput);
               continue;
            }

            if(version > lastVersion) {
               //liveOutsLastVersion.put(outputName, version);
               liveOutsLastVersion.put(outputNameClean, version);
               //liveOuts.put(outputName, realOutput);
               liveOuts.put(outputNameClean, realOutput);
            }

            /*
             // Update output index from table
            Integer version = variablesVersion.get(outputName);
//            Operand newInput = operandsTable.get(outputValue);

            if(version == null) {
               version = 1;
            } else {
               version++;
            }

            // Create new operand
            variablesVersion.put(outputName, version);
            String opValue = outputName+"."+version;
            Operand newOutput = new InternalData(opValue, output.getBits());
            operandsTable.put(outputName, newOutput);

            // Add new Output to new operation
            dotOp.connectToOutput(newOutput);
             */
         }

      }


      newList.add(createStartNode(liveIns));
      newList.add(createEndNode(liveOuts));

      return newList;
   }

   /**
    * Changes input list.
    * 
    * @param operations
    * @return
    */
   public static List<Operation> versionAndconnectOperations(List<Operation> operations) {
      List<Operation> newList = new ArrayList<Operation>();
      Map<String, Integer> variablesVersion = new HashMap<String, Integer>();
      Map<String, Operand> operandsTable = new HashMap<String, Operand>();
      Operation start = new Control(-1, Control.Op.start);
      newList.add(start);

      for(int i=0; i<operations.size(); i++) {
         Operation operation = operations.get(i);
         /*
         if(operation.getType() == OperationType.Nop) {
            continue;
         }
          */

         //Operation dotOp = new MockOperation(operation.getAddress(), operation.getValue());
         Operation dotOp = new MockOperation(operation.getAddress(), operation.toString());
         newList.add(dotOp);

         List<Operand> inputs = operation.getInputs();
         for(int j=0; j<inputs.size(); j++) {
            Operand input = inputs.get(j);
            // Check if immutable
            if(input.isImmutable()) {
               dotOp.connectToInput(input.copy());
               continue;
            }

            //String inputValue = input.getValue();
            String inputName = input.toString();
            // Get input index from table
            Integer version = variablesVersion.get(inputName);
            Operand newInput = operandsTable.get(inputName);

            if(version == null) {
               version = 0;
               //variablesVersion.put(input.getValue(), version);
               variablesVersion.put(inputName, version);
               String opValue = inputName+"."+version;
               newInput = new InternalData(opValue, input.getBits());
               operandsTable.put(inputName, newInput);
               newInput.connectToProducer(start);
            }

            // Add new Inputs to new operation
            dotOp.connectToInput(newInput);
         }

         List<Operand> outputs = operation.getOutputs();
         for(int j=0; j<outputs.size(); j++) {
            Operand output = outputs.get(j);
            //String outputValue = output.getValue();
            String outputName = output.toString();

             // Update output index from table
            Integer version = variablesVersion.get(outputName);
//            Operand newInput = operandsTable.get(outputValue);

            if(version == null) {
               version = 1;
            } else {
               version++;
            }

            // Create new operand
            variablesVersion.put(outputName, version);
            String opValue = outputName+"."+version;
            Operand newOutput = new InternalData(opValue, output.getBits());
            operandsTable.put(outputName, newOutput);

            // Add new Output to new operation
            dotOp.connectToOutput(newOutput);
         }

      }

      return newList;
   }

   private static Operation createStartNode(Map<String, Operand> liveIns) {
      Operation start = new Control(-1, Control.Op.start);

      for(String key : liveIns.keySet()) {
         liveIns.get(key).connectToProducer(start);
      }

      return start;
   }

   private static Operation createEndNode(Map<String, Operand> liveOuts) {
      Operation end = new Control(-1, Control.Op.end);

      for(String key : liveOuts.keySet()) {
         liveOuts.get(key).connectToConsumer(end);
      }

      return end;
   }
}

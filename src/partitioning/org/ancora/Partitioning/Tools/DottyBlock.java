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

package org.ancora.Partitioning.Tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.ancora.InstructionBlock.GenericInstruction;
import org.ancora.InstructionBlock.InstructionBlock;

/**
 * Prints a graphical representation of the execution trace.
 *
 * @author Joao Bispo
 */
public class DottyBlock {

   public DottyBlock() {
      blockDeclaration = new StringBuilder();
      //connections = new ArrayList<String>();
      previousId = null;

      declaredBlocks = new HashSet<Integer>();
      declaredConnections = new HashMap<String, Integer>();
   }


   public String generateDot() {
      StringBuilder builder = new StringBuilder();

      builder.append("digraph graphname {\n");
      builder.append(blockDeclaration.toString());
      builder.append("\n");

      builder.append(buildConnections());
      /*
      for (String connection : connections) {
         builder.append(connection);
      }
       *
       */
      builder.append("}\n");

      return builder.toString();
   }

   public void addBlock(InstructionBlock block) {
      // Add declaration
      if(!declaredBlocks.contains(block.getId())) {
         declaredBlocks.add(block.getId());
         addDeclaration(block);
      }

      // Add connection
      if (previousId != null) {
         // Build connection representation
         //String connection = String.valueOf(previousId) + "-" + String.valueOf(block.getId());
         String connection = previousId + " -> " + block.getId();

         // Check if connection already in map
         if (!declaredConnections.containsKey(connection)) {
            declaredConnections.put(connection, 1);
            //addConnection(block.getId());
         } else {
            // Increment value
            Integer newValue = declaredConnections.get(connection)+1;
            declaredConnections.put(connection, newValue);
         }
      }

      previousId = block.getId();
   }

   private void addDeclaration(InstructionBlock block) {
         blockDeclaration.append(block.getId());
         blockDeclaration.append("[label=\"");
         //builder.append(op.getType()+"-"+op.getValue());
         //builder.append(op.getValue());
         blockDeclaration.append(buildLabel(block));
         blockDeclaration.append("\"");
         blockDeclaration.append(", shape=box");
         if(block.getRepetitions() > 1) {
            blockDeclaration.append(", style=filled fillcolor=\"lightblue\"");
         }
         blockDeclaration.append("];\n");
   }

   /*
   private void addConnection(int id) {
      String connection = previousId + " -> " + id + ";\n";
      connections.add(connection);
   }
    *
    */

   private String buildConnections() {
      StringBuilder builder = new StringBuilder();

      for(String key : declaredConnections.keySet()) {
         builder.append(key);
         // Get value
         Integer value = declaredConnections.get(key);
         //if(value > 1) {
            builder.append(" [label=");
            builder.append(value);
            builder.append("]");
         //}
         builder.append(";\n");
      }

      return builder.toString();
   }

   private String buildLabel(InstructionBlock block) {
      StringBuilder builder = new StringBuilder();

      //builder.append(block.getId());
      builder.append("instructions:");
      builder.append(block.getInstructions().size());
      builder.append("\\n");

      builder.append("repetitions:");
      builder.append(block.getRepetitions());
      builder.append("\\n");

      builder.append(buildAddressString(block));

      return builder.toString();
   }

   private String buildAddressString(InstructionBlock block) {
      StringBuilder builder = new StringBuilder();
      
      List<GenericInstruction> insts = block.getInstructions();
      int first = insts.get(0).getAddress();
      int previous = first;
      for(int i=1; i<insts.size(); i++) {
         int current = insts.get(i).getAddress();
         if(current - previous != 4) {
            builder.append(first);
            builder.append(" - ");
            builder.append(previous);
            builder.append("\\n");
            first = current;
         }
         previous = current;
      }

      builder.append(first);
      builder.append(" - ");
      builder.append(previous);
      builder.append("\\n");

      
      return builder.toString();
   }

   private StringBuilder blockDeclaration;
   //private List<String> connections;
   private Integer previousId;
   private Set<Integer> declaredBlocks;
   private Map<String, Integer> declaredConnections;




}

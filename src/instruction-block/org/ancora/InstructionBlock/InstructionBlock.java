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

package org.ancora.InstructionBlock;

import java.util.List;

/**
 * Block of GenericInstructions. Contains a list of GenericInstructions, an ID
 * and the number of times it repeats itself.
 *
 * @author Joao Bispo
 */
public class InstructionBlock {

   public InstructionBlock(List<GenericInstruction> instructions, int repetitions, int id, long totalInstructions) {
      this.instructions = instructions;
      this.repetitions = repetitions;
      this.id = id;
      this.totalInstructions = totalInstructions;
   }


   /**
    * @return the number of instructions in the InstructionBlock, multiplied by
    * the number of repetitions
    */
   public long getTotalInstructions() {
      return totalInstructions;
      //return instructions.size() * repetitions;
   }

   /**
    * @return a number which uniquely identifies this block of instructions.
    */
   public int getId() {
      return id;
   }

   public List<GenericInstruction> getInstructions() {
      return instructions;
   }

   public int getRepetitions() {
      return repetitions;
   }

   

   @Override
   public String toString() {
      StringBuilder builder = new StringBuilder();

      builder.append("Id:");
      builder.append(id);
      builder.append("; Repetitions:");
      builder.append(repetitions);
      builder.append("\n");

      for(GenericInstruction inst : instructions) {
         builder.append(inst);
         builder.append("\n");
      }


      return builder.toString();
   }

   




   /**
    * INSTANCE VARIABLES
    */
   private List<GenericInstruction> instructions;
   private long totalInstructions;
   private int repetitions;
   private int id;
}

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

package org.ancora.DMTool.TransformStudy;

import org.ancora.InstructionBlock.InstructionBlock;

/**
 *
 * @author Joao Bispo
 */
//public class NamedBlock extends InstructionBlock {
public class NamedBlock {

   /*
   public NamedBlock(String name,List<GenericInstruction> instructions, int repetitions, int id) {
      super(null, repetitions, id)
   }
    */



   public NamedBlock(InstructionBlock block, String name) {
      this.block = block;
      this.name = name;
   }

   public InstructionBlock getBlock() {
      return block;
   }

   public String getName() {
      return name;
   }

   

   /**
    * INSTANCE VARIABLES
    */
   private InstructionBlock block;
   private String name;
}

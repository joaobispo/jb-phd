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

package org.ancora.Partitioning.deprecated;

import org.ancora.InstructionBlock.ElfBusReader;
import org.ancora.InstructionBlock.GenericInstruction;
import org.ancora.InstructionBlock.InstructionBlock;
import org.ancora.InstructionBlock.InstructionBusReader;

/**
 *
 * @author Ancora Group <ancora.codigo@gmail.com>
 */
public class TestElfReader {

   /**
    * @param args the command line arguments
    */
   public static void main(String[] args) {

      //testPartitionerBlockReader();
      //testBlockWorter();
   /*
      //String systemConfigFile = "./systemconfig.xml";
      String systemConfigFile = "Configuration Files\\systemconfig.xml";
      String binaryFile = "aluno_adpcm_coder.elf";


      BasicBlock bb = new MbBasicBlockProducer();
      SuperBlock sb = new SuperBlock(bb);
      MegaBlock mb = new MegaBlock(sb, 32);
      InstructionBusReader reader = ElfBusReader.createElfReader(systemConfigFile, binaryFile);

      IterativePartitioner part = new IterativePartitioner(sb);

      GenericInstruction instruction = reader.nextInstruction();
      while (instruction != null) {
         InstructionBlock block = part.acceptInstruction(instruction);
         //System.out.println(instruction);
         if (block != null) {
            System.out.println(block);
         }
         instruction = reader.nextInstruction();
      }

      InstructionBlock block = part.lastInstruction();
      System.out.println(block);
*/
   }
/*
   private static void testPartitionerBlockReader() {
       String systemConfigFile = "Configuration Files\\systemconfig.xml";
      String binaryFile = "aluno_adpcm_coder.elf";


      BasicBlock bb = new MbBasicBlockProducer();
      SuperBlock sb = new SuperBlock(bb);
      MegaBlock mb = new MegaBlock(sb, 32);
      InstructionBusReader busReader = ElfBusReader.createElfReader(systemConfigFile, binaryFile);

      PartitionerBlockReader reader = new PartitionerBlockReader(busReader, mb, false);

      InstructionBlock block = reader.nextBlock();
      while (block != null) {
         System.out.println("Block:"+block);
         block = reader.nextBlock();
      }

      //System.out.println(block);
   }
*/


}

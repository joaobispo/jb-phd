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

package org.ancora.DMTool.deprecated.TraceProcessor;

import java.util.List;
import org.ancora.InstructionBlock.ElfBusReader;
import org.ancora.InstructionBlock.InstructionBlock;
import org.ancora.InstructionBlock.InstructionBusReader;
import org.ancora.Partitioning.MbBasicBlock;
import org.ancora.Partitioning.BasicBlock;
import org.ancora.Partitioning.MegaBlock;
import org.ancora.Partitioning.SuperBlock;


/**
 *
 * @author Ancora Group <ancora.codigo@gmail.com>
 */
public class Tester {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //testBlockWorker();
        testTraceProcessorWorker();
    }

    private static void testBlockWorker() {
      String systemConfigFile = "Configuration Files\\systemconfig.xml";
      //String binaryFile = "../data/elf/aluno_adpcm_coder.elf";
      String binaryFile = "../data/elf/quicksort-o2.elf";


      BasicBlock bb = new MbBasicBlock();
      SuperBlock sb = new SuperBlock(bb);
  //    MegaBlock mb = new MegaBlock(sb, 32);
      MegaBlock mb = new MegaBlock(sb);
      InstructionBusReader busReader = ElfBusReader.createElfReader(systemConfigFile, binaryFile);

      BlockWorker worker = new BlockWorker(mb, busReader);

      worker.setUseGatherer(true);
      worker.setUseSelector(false);
      worker.init();

      InstructionBlock block = worker.nextBlock();
      while(block != null) {
         //System.out.println(block);
         block = worker.nextBlock();
      }
   }

   private static void testTraceProcessorWorker() {
      String systemConfigFile = "Configuration Files\\systemconfig.xml";
      //String binaryFile = "../data/elf/aluno_adpcm_coder.elf";
      String binaryFile = "../data/elf/quicksort-o2.elf";


      BasicBlock bb = new MbBasicBlock();
      SuperBlock sb = new SuperBlock(bb);
//      MegaBlock mb = new MegaBlock(sb, 32);
      MegaBlock mb = new MegaBlock(sb);
      InstructionBusReader busReader = ElfBusReader.createElfReader(systemConfigFile, binaryFile);

      TraceProcessorWorker worker = new TraceProcessorWorker(mb);
      List<InstructionBlock> blocks = worker.processTrace(busReader);
   }

}

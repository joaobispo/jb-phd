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

import org.ancora.InstructionBlock.InstructionBusReader;
import org.ancora.InstructionBlock.GenericInstruction;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ancora.MicroBlaze.InstructionName;
import org.ancora.SharedLibrary.ParseUtils;
import system.Sys_Status;
import system.SysteM;
import system.SysteMException;
import system.memory.MemoryException;

/**
 *
 * @author Joao Bispo
 */
public class MbElfReader implements InstructionBusReader {

   public MbElfReader(SysteM system) {
      this.system = system;
      sys_status = Sys_Status.NORMAL;
      
   }

   public static MbElfReader createMbElfReader(String systemconfig_file, String binary_file) {
      SysteM system = null;
      try {
         system = new SysteM(systemconfig_file, binary_file, false, true);
      } catch (SysteMException ex) {
         Logger.getLogger(MbElfReader.class.getName()).
                 warning("Could not create system: "+ex.getMessage());
         Logger.getLogger(MbElfReader.class.getName()).
                 warning("Tried to execute file '"+binary_file+"'");

         return null;
      }

      return new MbElfReader(system);
   }

   

   public GenericInstruction nextInstruction() {

      if (sys_status != Sys_Status.NORMAL) {
         return null;
      }

      int pc = system.getCPUClass().getStageInstruction(3).getPC();
      int value = 0;

      try {
         value = system.getMemoryClass().getMemoryWord(pc);
      } catch (MemoryException ex) {
         Logger.getLogger(MbElfReader.class.getName()).
                 warning("Could not read program counter: " + ex.getMessage());
         return null;
      }

      String instruction = system.getCPUClass().getInstructionSetArchitectureClass().toString(value);
      sys_status = system.step();
      InstructionName instName = getInstructionName(instruction);
      return new MbInstruction(pc, instruction, instName);

   }

   private InstructionName getInstructionName(String instruction) {
      //System.out.println("Inst:"+instruction);
      /// Get InstructionName
      int whiteSpaceIndex = ParseUtils.indexOfFirstWhiteSpace(instruction.trim());
      String instNameString = instruction.substring(0, whiteSpaceIndex);
      InstructionName instName = InstructionName.getEnum(instNameString);

      return instName;
   }

   public long getCycles() {
      return system.getCycles();
   }

   public long getInstructions() {
      return system.getCPUClass().getNumberInstructions();
   }

   /**
    * INSTANCE VARIABLES
    */
   private int sys_status;
   private SysteM system;





}

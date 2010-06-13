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

package org.specs.DMTool2;

import org.specs.DMTool2.Settings.ProgramName;
import java.util.EnumMap;
import java.util.Map;
import java.util.logging.Logger;
import org.ancora.SharedLibrary.EnumUtils;

/**
 * Instantiates and contains the programs used by ToolsOS.
 *
 * @author Joao Bispo
 */
public class ProgramTable {

   public ProgramTable() {
      programTable = new EnumMap<ProgramName, Program>(ProgramName.class);
   }

   /**
    * @param programNameString
    * @return the program specified by programNameString, or null if the program
    * could not be found
    */
   public Program getProgram(String programNameString) {
      ProgramName programName = programNames.get(programNameString);

      if(programName == null) {
         Logger.getLogger(ProgramTable.class.getName()).
                 warning("Invalid command '"+programNameString+"'");
         return null;
      }

      // Check if program is already on the table
      Program program = programTable.get(programName);
      if(program != null) {
         // Check if program is not dead
         if(!program.isDead()) {
            return program;
         }
      }

      // Instantiate new program and add it to the table
      program = programName.newProgram();
      programTable.put(programName, program);

      return program;
   }


   /**
    * INSTANCE VARIABLES
    */
   // Maintains a state of the program
   private Map<ProgramName, Program> programTable;
   private Map<String, ProgramName> programNames = EnumUtils.buildMap(ProgramName.values());


}

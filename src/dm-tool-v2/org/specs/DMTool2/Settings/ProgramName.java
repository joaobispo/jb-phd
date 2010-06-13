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

package org.specs.DMTool2.Settings;

import java.util.logging.Logger;
import org.specs.DMTool2.Program;
import org.specs.DMTool2.RootPrograms.Exit;
import org.specs.DMTool2.RootPrograms.RunScript;
import org.specs.DMTool2.RootPrograms.Set;
import org.specs.DMTool2.TraceCoverage.TraceCoverage;

/**
 *
 * @author Joao Bispo
 */
public enum ProgramName {

   exit("exit"),
   runScript("runscript"),
   traceCoverage("trace-coverage"),
   set("set");

   private ProgramName(String programName) {
      this.programName = programName;
   }

   public Program newProgram() {
      switch(this) {
         case exit:
            return new Exit();
         case runScript:
            return new RunScript();
         case traceCoverage:
            return new TraceCoverage();
         case set:
            return new Set();
         default:
            Logger.getLogger(ProgramName.class.getName()).
                    warning("Case not defined: "+this.name());
            return null;
      }

   }

   public String getProgramName() {
      return programName;
   }

   /**
    * This method is necessary for mapping the name of the program to the enum,
    * when returning .values();
    * @return
    */
   @Override
   public String toString() {
      return programName;
   }



   

   private final String programName;
}

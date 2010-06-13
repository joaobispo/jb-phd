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

package org.specs.DMTool2.RootPrograms;

import java.util.List;
import java.util.logging.Logger;
import org.specs.DMTool2.Program;
import org.specs.DMTool2.Settings.ProgramName;

/**
 *
 * @author Joao Bispo
 */
public class RunScript implements Program {

      public boolean execute(List<String> arguments) {
      Logger.getLogger(RunScript.class.getName()).
              warning("Empty program class. Should not be executed.");
      return false;
   }

   public boolean isDead() {
      // Is stateless. Is always live.
      return false;
   }

   public ProgramName getProgramName() {
      return ProgramName.runScript;
   }

}

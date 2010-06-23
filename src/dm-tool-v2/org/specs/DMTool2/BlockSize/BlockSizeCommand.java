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

package org.specs.DMTool2.BlockSize;

import org.specs.DMTool2.TraceCoverage.*;
import java.util.Map;
import org.ancora.SharedLibrary.EnumUtils;

/**
 *
 * @author Joao Bispo
 */
public enum BlockSizeCommand {
   add("add"),
   run("run");

   private BlockSizeCommand(String commandName) {
      this.commandName = commandName;
   }

   public static BlockSizeCommand getBlockSizeCommand(String commandName) {
      return commandNames.get(commandName);
   }

   /**
    * This method is necessary for mapping the name of the program to the enum,
    * when returning .values();
    * @return
    */
   @Override
   public String toString() {
      return commandName;
   }

   private final String commandName;
   private static final Map<String, BlockSizeCommand> commandNames = EnumUtils.buildMap(BlockSizeCommand.values());
}

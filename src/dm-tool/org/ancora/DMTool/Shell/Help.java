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

package org.ancora.DMTool.Shell;

import java.util.List;
import org.ancora.DMTool.Shell.Shell.Command;
import org.ancora.DMTool.Shell.System.Executable;

/**
 *
 * @author Joao Bispo
 */
public class Help implements Executable {

   public boolean execute(List<String> arguments) {
       //logger.info("Supported commands:");
       System.out.println("Supported commands:");
      for(Command command : Command.values()) {
         String message = command.name() + " - " + helpMessage(command);
         //logger.info(message);
         System.out.println(message);
      }

      return true;
   }

    private String helpMessage(Command command) {
         switch (command) {
            case help:
               return "This help message";
            case exit:
               return "Exit the program";
            case set:
               return "Set the value of a particular option";
            case options:
               return "Show the current value of avaliable options";
            case transform:
               return "Study transformation effects on code. Supports traces, elfs and blocks.";
            default:
               return "Help message not defined";
         }
      }
}

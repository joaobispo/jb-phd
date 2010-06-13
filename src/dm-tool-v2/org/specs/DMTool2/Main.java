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

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;
import org.ancora.SharedLibrary.IoUtils;
import org.ancora.SharedLibrary.LineReader;
import org.ancora.SharedLibrary.LoggingUtils;

/**
 *
 * @author Joao Bispo <joao.bispo@gmail.com>
 */
public class Main {

   /**
    * @param args the command line arguments
    */
   public static void main(String[] args) {
      // Configure Logger to capture all output to console
      LoggingUtils.setupConsoleOnly();

      //Create a ToolsOS to receive the commands
      ToolsOS toolsOS = new ToolsOS();

      if (args.length > 0) {
         // Try to get script file
         List<String> arguments = Arrays.asList(args);
         toolsOS.runScript(arguments);
      } else {
         // Run the shell
         toolsOS.runShell();
      }
   }
/*
   private static void runScript(ToolsOS toolsOS, String[] args) {
      File scriptFile = IoUtils.existingFile(args[0]);
      if(scriptFile == null) {
         logger.warning("Script file '"+args[0]+"' not found. Terminating...");
         return;
      }



      LineReader lineReader = LineReader.createLineReader(scriptFile);
      String command = lineReader.nextLine();
      int lineCounter = 1;
      while(command != null) {
         boolean success = toolsOS.executeCommand(command);
         if(!success) {
            logger.warning("(Problems on line "+lineCounter+", file "+args[0]+")");
         }
         command = lineReader.nextLine();
         lineCounter++;
      }

   }

   private static void runShell() {
       // Initialize Scanner
       Scanner scanner = new Scanner(System.in);

       // Initialize ToolsOS
       ToolsOS toolsOS = new ToolsOS();

       // Show welcome message
       logger.info("ToolsOS Shell (MicroBlaze version)");

       // Start cycle
       while(true) {
          String command = scanner.nextLine();
          toolsOS.executeCommand(command);
       }
   }
   */

   // Logger
   //private static final Logger logger = Logger.getLogger(ToolsOS.class.getName());
}

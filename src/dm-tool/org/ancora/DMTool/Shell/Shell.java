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

import org.ancora.DMTool.System.Interfaces.Executable;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;
import org.ancora.DMTool.System.Services.ShellUtils;
import org.ancora.SharedLibrary.EnumUtils;
import org.ancora.SharedLibrary.IoUtils;
import org.ancora.SharedLibrary.LineReader;
import org.ancora.SharedLibrary.LoggingUtils;

/**
 *
 * @author Ancora Group <ancora.codigo@gmail.com>
 */
public class Shell {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
       // Configure Logger to capture all output to console
       LoggingUtils.setupConsoleOnly();

       if (args.length > 0) {
          // Try to get script file
          runScript(args);
       } else {
          // Run the shell
          runShell();
       }
    
    }

   private static void runScript(String[] args) {
      File scriptFile = IoUtils.existingFile(args[0]);
      if(scriptFile == null) {
         logger.warning("Script file '"+args[0]+"' not found. Terminating...");
         return;
      }

      LineReader lineReader = LineReader.createLineReader(scriptFile);
      String command = lineReader.nextLine();
      int lineCounter = 1;
      while(command != null) {
         boolean success = executeCommand(command);
         if(!success) {
            logger.warning("(Problems on line "+lineCounter+")");
         }
         command = lineReader.nextLine();
         lineCounter++;
      }

   }

   private static void runShell() {
       // Initialize Scanner
       Scanner scanner = new Scanner(System.in);

       // Show welcome message
       logger.info("Dynamic Mapping Shell (MicroBlaze version)");

       // Start cycle
       while(true) {
          String command = scanner.nextLine();
          executeCommand(command);
       }
   }

   private static boolean executeCommand(String command) {
      // Split String
      List<String> splitCommand = ShellUtils.splitCommand(command);

      // Check output
      //System.out.println(splitCommand);

      // Check if there is a command
      if(splitCommand.isEmpty()) {
         // Show current properties - dropped
         return true;
      }

      // Get Command
      Command commandEnum = EnumUtils.valueOf(Command.class, splitCommand.get(0));

      if(commandEnum == null) {
         logger.info("Invalid command '"+splitCommand.get(0)+"'");
         return false;
      }

      List<String> arguments = new ArrayList<String>();
      arguments.addAll(splitCommand.subList(1, splitCommand.size()));

      /// Check simple commands (exit, help, config, set)
      // Check simple commands (exit)
      if(commandEnum == Command.exit) {
         logger.info("Bye!");
         System.exit(0);
      }

      // Get Executable
      //Executable executable = executables.get(commandEnum);
      Executable executable = commandEnum.getExecutable();
      if(executable == null) {
         logger.warning("Executable for command '"+commandEnum+"' not found.");
      }

      return executable.execute(arguments);

   }

   /**
    * INSTANCE VARIABLES
    */
   private static final Logger logger = Logger.getLogger(Shell.class.getName());


   /**
    * ENUM
    */
   public enum Command {

      help,
      exit,
      set,
      options,
      transform,
      extractblocks,
      tracecoverage,
      simulate,
      dottrace,
      streamtransform,
      streamsimulate;

  
      public Executable getExecutable() {
         switch (this) {
            case set:
               return new Set();
            case help:
               return new Help();
            case options:
               return new ShowOptions();
            case transform:
               return new Transform();
            case extractblocks:
               return new WriteBlocks();
            case tracecoverage:
               return new TraceCoverage();
            case simulate:
               return new Simulate();
            case dottrace:
               return new DotTrace();
            case streamtransform:
               return new StreamTransform();
            case streamsimulate:
               return new StreamSimulate();
            default:
               Logger.getLogger(Command.class.getName()).
                       warning("Executable not defined for '" + this.name() + "'");
               return null;
         }
      }
       
   }

}

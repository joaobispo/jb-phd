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

import org.ancora.DMTool.Shell.System.Executable;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Logger;
import org.ancora.DMTool.Utils.ShellUtils;
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
         logger.info("Script file '"+args[0]+"' not found. Terminating...");
         return;
      }

      LineReader lineReader = LineReader.createLineReader(scriptFile);
      String command = lineReader.nextLine();
      int lineCounter = 1;
      while(command != null) {
         boolean success = executeCommand(command);
         if(!success) {
            logger.info("(Problems on line "+lineCounter+")");
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
      Executable executable = executables.get(commandEnum);
      if(executable == null) {
         logger.warning("Executable for command '"+commandEnum+"' not found.");
      }

      return executable.execute(arguments);
      /*
      if(commandEnum == Command.help) {
         showHelp();
         return true;
      }

      if(commandEnum == Command.set) {
         return executeSet(splitCommand);
      }

      if(commandEnum == Command.options) {
         return executeOptions();
      }

      Executable exec = commandEnum.getExecutable();
      if(exec != null) {
         return exec.execute(splitCommand);
      }
*/
      //return false;
   }
/*
   private static void showHelp() {
      logger.info("Supported commands:");
      for(Command command : Command.values()) {
         String message = command.name() + " - " + command.helpMessage();
         logger.info(message);
      }
   }
*/
   /*
   
   private static boolean executeSet(List<String> args) {
      EnumPreferences prefs = GeneralPreferences.getPreferences();

      if(args.size() < 2) {
         logger.info("Too few arguments for 'set' ("+args.size()+"). Minimum is 2.");
         return false;
      }

      // Get preference
      String prefString = args.get(0);
      GeneralPreferences prefEnum = EnumUtils.valueOf(GeneralPreferences.class, prefString);
      if(prefEnum == null) {
         logger.info("'"+args.get(0)+"' is not a valid setting. Avaliable:");
         //logger.info("");
         for(GeneralPreferences gPref : GeneralPreferences.values()) {
            logger.info("- "+gPref.name());
         }
         return false;
      }

      // Get value
      String value = args.get(1);

      // Update preferences
      prefs.putPreference(prefEnum, value);

      return true;
   }
*/
   /*
   private static boolean executeOptions() {
      EnumPreferences prefs = GeneralPreferences.getPreferences();

      logger.info("Current values for program options:");
      for(GeneralPreferences gPref : GeneralPreferences.values()) {
         String value = prefs.getPreference(gPref);
         String message = gPref.name() + " - " + value;
         logger.info(message);
      }

      return true;
   }
    */

   /**
    * INSTANCE VARIABLES
    */
   private static final Logger logger = Logger.getLogger(Shell.class.getName());

   private static final Map<Command, Executable> executables;
   static {
      Map<Command, Executable> aMap = new EnumMap<Command, Executable>(Command.class);

      aMap.put(Command.set, new Set());
      aMap.put(Command.transform, new Transform());
      aMap.put(Command.help, new Help());
      aMap.put(Command.options, new ShowOptions());

      executables = Collections.unmodifiableMap(aMap);
   }


   /**
    * ENUM
    */
   public enum Command {

      help,
      exit,
      set,
      options,
      transform;
/*
      public String helpMessage() {
         switch (this) {
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
*/
      /*
      public Executable getExecutable() {
         switch (this) {
            case transform:
               return new Transform();
            default:
               Logger.getLogger(Command.class.getName()).
                       warning("Executable not defined for '" + this.name() + "'");
               return null;
         }
      }
       */
   }





}

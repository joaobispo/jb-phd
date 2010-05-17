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
import java.util.logging.Logger;
import org.ancora.IrMapping.DmMapperDispenser.MapperName;
import org.ancora.DMTool.Settings.Options.OptionName;
import org.ancora.DMTool.Shell.Shell.Command;
import org.ancora.DMTool.Shell.System.Executable;
import org.ancora.IntermediateRepresentation.DmTransformDispenser.TransformationName;
import org.ancora.Partitioning.DmPartitionerDispenser.PartitionerName;
import org.ancora.SharedLibrary.EnumUtils;

/**
 *
 * @author Joao Bispo
 */
public class Help implements Executable {

   public boolean execute(List<String> arguments) {
      if(arguments.isEmpty()) {

      logger.info("\nType the following for more info:");
         noArgumentMessage();
         return true;
      }


      String argument = arguments.get(0);
      HelpArgument helpArgument = EnumUtils.valueOf(HelpArgument.class, argument);

      if(helpArgument == null) {
         logger.info("\nInvalid argument: '"+argument+"'. Type the following for more info:");
         noArgumentMessage();
         return true;
      }

      switch(helpArgument) {
         case commands:
            showCommandsHelp();
            return true;
         case partitioners:
            showPartitioners();
            return true;
         case setoptions:
            showSetOptions();
            return true;
         case transformations:
            showTransformations();
            return true;
         case mappers:
            showMappers();
            return true;
         default:
            logger.warning("Case not implemented: '"+helpArgument+"'");
            return false;
      }
      //logger.info("Supported commands:");
       //System.out.println("\nSupported commands:");
       

       /*
       for(Command command : Command.values()) {
         String message = command.name() + " - " + helpMessage(command);
         //logger.info(message);
         System.out.println(message);
      }
        *
        */

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

    private static final Logger logger = Logger.getLogger(Help.class.getName());

   private void noArgumentMessage() {
      String helpCommand = Command.help.name();

      for(HelpArgument arg : HelpArgument.values()) {
         String message = helpCommand + " " + arg.name();
         logger.info(message);
         //System.out.println(message);
      }
   }

   private void showCommandsHelp() {
      logger.info("\nAvaliable shell commands:");
      for(Command command : Command.values()) {
         logger.info(command.name() + " - "+ helpMessage(command));
      }
   }

   private void showPartitioners() {
      logger.info("\nAvaliable partitioners:");

      for(PartitionerName  partitioner : PartitionerName .values()) {
         logger.info(partitioner.getDmPartitionerName());
      }
   }

   private void showSetOptions() {
      logger.info("\nAvaliable options:");

      for(OptionName option : OptionName .values()) {
         logger.info(option.getOptionName());
      }
   }

   private void showTransformations() {
      logger.info("\nAvaliable transformations:");

      for(TransformationName transf : TransformationName .values()) {
         logger.info(transf.getTransformationName());
      }
   }

   private void showMappers() {
      logger.info("\nAvaliable Mappers:");

      for(MapperName mapper : MapperName .values()) {
         logger.info(mapper.getMapperName());
      }
   }


   /**
    * ENUM
    */
   enum HelpArgument {
      commands,
      setoptions,
      partitioners,
      transformations,
      mappers;
   }
}

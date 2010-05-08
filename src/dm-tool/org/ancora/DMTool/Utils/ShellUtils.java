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

package org.ancora.DMTool.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Joao Bispo
 */
public class ShellUtils {

   /**
    * TODO: Currently does not support arguments with spaces in the middle.
    * Change this to support paths which have spaces using "".
    * @param command
    * @return
    */
   public static List<String> splitCommand(String command) {
      // Trim string
      command = command.trim();
      // Check if it starts with comment
      if(command.startsWith(COMMENT)) {
         return new ArrayList<String>();
      }

      List<String> commands = new ArrayList<String>();

      while(command.length() > 0) {
         // Get indexes
         int spaceIndex = command.indexOf(COMMAND_SEPARATOR);
         int quoteIndex = command.indexOf(COMMAND_GATHERER);

         // Check which comes first
         if(spaceIndex == -1 && quoteIndex == -1) {
            commands.add(command);
            command = "";
         }

         if(spaceIndex < quoteIndex) {
            String argument = command.substring(0, spaceIndex);
            commands.add(argument);
            command = command.substring(spaceIndex+1).trim();


         } else {
            // Find second quote
            int quoteIndex2Increment = command.substring(quoteIndex+1).indexOf(COMMAND_GATHERER);
            if(quoteIndex2Increment == -1 && spaceIndex == -1) {
               // Capture last argument
               commands.add(command.trim());
               command = "";
            } else if(quoteIndex2Increment == -1 && spaceIndex != -1){
               String argument = command.substring(quoteIndex+1, spaceIndex);
               commands.add(argument);
               command = command.substring(spaceIndex+1);
            } else {
               //System.out.println("Quote:"+quoteIndex);
               //System.out.println("Quote2:"+quoteIndex2Increment);
               //System.out.println("Quote2 Real:"+(quoteIndex+quoteIndex2Increment+1));
               int quote2 = (quoteIndex+quoteIndex2Increment+1);
               String argument = command.substring(quoteIndex+1, quote2);
               commands.add(argument);
               command = command.substring(quote2+1);
            }
         }
      }

      /*
      String[] splitResult = command.split(COMMAND_SEPARATOR);
      for(String split : splitResult) {
         String cleanString = split.trim();
         if(cleanString.length() > 0) {
            commands.add(cleanString);
         }
      }
       */

      return commands;
   }

   public static final String COMMAND_SEPARATOR = " ";
   public static final String COMMAND_GATHERER = "\"";
   public static final String COMMENT = "//";
}

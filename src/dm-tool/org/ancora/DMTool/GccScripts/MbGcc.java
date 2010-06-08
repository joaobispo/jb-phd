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

package org.ancora.DMTool.GccScripts;

import org.ancora.DMTool.GccScripts.MenottiGccRuns;
import org.ancora.DMTool.GccScripts.DtoolGccRuns;
import org.ancora.DMTool.GccScripts.AdhocGccRuns;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.logging.Logger;
import org.ancora.SharedLibrary.DataStructures.MbGccRun;

/**
 *
 * @author Ancora Group <ancora.codigo@gmail.com>
 */
public class MbGcc {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

      buildMenottiElfs();
      //buildAdhocElfs();
      //buildDtooElfs();
   }
/*
   public static int runProcess(List<String> command, String workingDir) {
   //public static int runProcess(String program, String args, String workingDir) {
      int returnValue = -1;
      try {
         String commandString = getCommandString(command);
         System.out.println("Running: " + commandString + "  ...");
         //System.out.println("Running: " + command.get(0) + "  ...");
         //   List<String> command = new ArrayList<String>();
         //command.add(System.getenv("windir") +"\\system32\\"+"tree.com");
         //command.add("/A");

         ProcessBuilder builder = new ProcessBuilder(command);
         //ProcessBuilder builder = new ProcessBuilder(program, args);
         //Map<String, String> environ = builder.environment();
         builder.directory(new File(workingDir));

         //System.out.println("Directory : " + System.getenv("temp") );
         final Process process = builder.start();
         //InputStream is = ;
         //InputStreamReader isr = ;

         BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
         BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));

         String errline = null;
         String stdline = null;


         while ((errline = stdError.readLine()) != null ||
                 (stdline = stdInput.readLine()) != null) {
            if(errline!= null) {
               System.err.println(errline);
            }
            if(stdline != null) {
               System.out.println(stdline);
            }
         }

         
         returnValue = process.waitFor();
         System.out.println("Program terminated.");
      } catch (InterruptedException ex) {
         Logger.getLogger(MbGcc.class.getName()).
                 info("Program interrupted:"+ex.getMessage());
      } catch (IOException e) {
         Logger.getLogger(MbGcc.class.getName()).
                 info("IOException during program execution:"+e.getMessage());
      }
      
      return returnValue;
   }

   private static String getCommandString(List<String> command) {
      StringBuilder builder = new StringBuilder();

      builder.append(command.get(0));
      for(int i=1; i<command.size(); i++) {
         builder.append(" ");
         builder.append(command.get(i));
      }

      return builder.toString();
   }
*/
   private static void buildMenottiElfs() {
      for(MbGccRun run : MenottiGccRuns.getRuns()) {
         run.run();
       }
   }

   private static void buildAdhocElfs() {
      for(MbGccRun run : AdhocGccRuns.getRuns()) {
         run.run();
       }
   }

   private static void buildDtooElfs() {
       for(MbGccRun run : DtoolGccRuns.getRuns()) {
         run.run();
       }
   }
}

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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author Ancora Group <ancora.codigo@gmail.com>
 */
public class MbGcc {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
       /*
       String program = "mb-gcc";
        String inputFile = "adpcm_coder.c";
        String outputFlag = "-o";
        String outputFile = "adpcm_coder.elf";
        String optimization = "-O3";
        String otherFlags = "-Wall -g -mxl-barrel-shift -mno-xl-soft-div -mno-xl-soft-mul -xl-mode-executable";
        String[] otherFlagsArray = otherFlags.split(" ");

        List<String> command = new ArrayList<String>();
        command.add(program);
        command.add(inputFile);
        command.add(outputFlag);
        command.add(outputFile);
        command.add(optimization);
        for(String flag : otherFlagsArray) {
            command.add(flag);
         }
*/
        //String arguments = "adpcm_coder.c -o adpcm_coder.elf -O0 -Wall -g " +
        //        "-mxl-barrel-shift -mno-xl-soft-div -mno-xl-soft-mul -xl-mode-executable";
  //      String workDir = "E:/dmout/mbgcc/battery";

        //runProcess(command, workDir);

        //runProcess(program, arguments, workDir);

       for(GccRun run : MenottiGccRuns.getRuns()) {
         run.run();
       }
       
   }

   public static int runProcess(List<String> command, String workingDir) {
   //public static int runProcess(String program, String args, String workingDir) {
      int returnValue = -1;
      try {
         System.out.println("Running: " + command.get(0) + "  ...");
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

         String line;

         while ((line = stdInput.readLine()) != null) {
            System.out.println(line);
         }

         while ((line = stdError.readLine()) != null) {
            System.out.println(line);
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
}
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
public class GccRun {

   public GccRun(String outputFile, String[] inputFiles, String optimization, String[] otherFlags, String workingDir) {
      this.outputFile = outputFile;
      this.inputFiles = inputFiles;
      this.optimization = optimization;
      this.otherFlags = otherFlags;
      this.workingDir = workingDir;
   }

   public int run() {
       List<String> command = new ArrayList<String>();
        command.add(getProgram());
        for(String input : inputFiles) {
         command.add(input);
        }
        
        command.add(getOutputFlag());
        command.add(outputFile);
        command.add(optimization);
        for(String flag : otherFlags) {
            command.add(flag);
         }

        //String arguments = "adpcm_coder.c -o adpcm_coder.elf -O0 -Wall -g " +
        //        "-mxl-barrel-shift -mno-xl-soft-div -mno-xl-soft-mul -xl-mode-executable";
  //      String workDir = "E:/dmout/mbgcc/battery";

        return MbGcc.runProcess(command, workingDir);
   }

   @Override
   public String toString() {
      StringBuilder builder = new StringBuilder();

      builder.append("Output:");
      builder.append(outputFile);
      builder.append("\n");

      builder.append("Input:\n");
      for(String input : inputFiles) {
      builder.append(input);
      builder.append("\n");
      }

      builder.append("Optimization:");
      builder.append(optimization);
      builder.append("\n");

      builder.append("Flags:\n");
      for(String flag : otherFlags) {
      builder.append(flag);
      builder.append("\n");
      }



      return builder.toString();
   }



   /**
    * 
    * @return mb-gcc
    */
   public String getProgram() {
      return "mb-gcc";
   }

   /**
    * 
    * @return -o
    */
   public String getOutputFlag() {
      return "-o";
   }

   public String[] getInputFiles() {
      return inputFiles;
   }

   public String getOptimization() {
      return optimization;
   }

   public String[] getOtherFlags() {
      return otherFlags;
   }

   public String getOutputFile() {
      return outputFile;
   }
/*
   public void setInputFiles(String[] inputFiles) {
      this.inputFiles = inputFiles;
   }

   public void setOptimizations(String[] optimizations) {
      this.optimizations = optimizations;
   }

   public void setOtherFlags(String[] otherFlags) {
      this.otherFlags = otherFlags;
   }

   public void setOutputFile(String outputFile) {
      this.outputFile = outputFile;
   }
*/
   /**
    *
    * @return List of inputs for each run of the program.
    */
   //public abstract List<String[]> getInputFiles();
   //public abstract String[] getInputFiles();

   //public abstract List<String> getOutputFiles();
   //public abstract String getOutputFile();

   //public abstract String[] getOptimizations();

   //public abstract String[] getOtherFlags();



   /**
    * INSTANCE VARIABLES
    */
   String outputFile;
   String[] inputFiles;
   String optimization;
   String[] otherFlags;
   String workingDir;
}

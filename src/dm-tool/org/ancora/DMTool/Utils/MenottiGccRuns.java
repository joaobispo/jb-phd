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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.ancora.SharedLibrary.IoUtils;
import org.ancora.SharedLibrary.ParseUtils;

/**
 *
 * @author Joao Bispo
 */
public class MenottiGccRuns {

   public static List<GccRun> getRuns() {
      // Prepare flags
      String[] flagsArray = DEFAULT_FLAGS.split(" ");
      // Prepare optimizations
      String[] optArray = DEFAULT_OPTIMIZATIONS.split(" ");
      // Prepare files
      File[] files = (new File(DEFAULT_INPUT_FOLDER)).listFiles();
      //List<File> files = IoUtils.getFilesRecursive(new File(DEFAULT_INPUT_FOLDER));

      List<GccRun> runs = new ArrayList<GccRun>();
      for(File file : files) {
         for(String optimization : optArray) {
            String[] inputFiles = new String[1];
            inputFiles[0] = file.getName();

            String baseFilename = IoUtils.removeExtension(inputFiles[0], IoUtils.DEFAULT_EXTENSION_SEPARATOR);
            String parentInputFolder = (new File(DEFAULT_INPUT_FOLDER)).getParent();
            String outputFolder = parentInputFolder+"\\elf\\"+optimization.substring(1)+"\\";
            //String outputFolder = DEFAULT_INPUT_FOLDER+"\\..\\elf\\"+optimization.substring(1)+"\\";
            //String outputFile = baseFilename + optimization + ".elf";
            String outputFile = outputFolder + baseFilename + optimization + ".elf";

            runs.add(new GccRun(outputFile, inputFiles, optimization, flagsArray, DEFAULT_INPUT_FOLDER));
         }
      }

      return runs;
   }

   public static final String DEFAULT_FLAGS = "-Wall -g -mxl-barrel-shift -mno-xl-soft-div -mno-xl-soft-mul -xl-mode-executable";
   public static final String DEFAULT_OPTIMIZATIONS = "-O0 -O1 -O2 -O3";
   //public static final String DEFAULT_OPTIMIZATIONS = "-Os";
   //public static final String DEFAULT_INPUT_FOLDER = "I:\\Workspace\\Resources\\MicroBlaze Benchmarks\\05 - Test Battery 1 (Menotti) (C and Elf)\\c";
   public static final String DEFAULT_INPUT_FOLDER = "D:\\WorkSpace\\Resources\\Benchmarks\\Test Battery 1 (Menotti) (C and Elf)\\c";
}

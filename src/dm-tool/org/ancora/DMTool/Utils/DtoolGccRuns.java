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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.ancora.SharedLibrary.IoUtils;

/**
 *
 * @author Joao Bispo
 */
public class DtoolGccRuns {

   public static List<GccRun> getRuns() {
      // Prepare flags
      String[] flagsArray = DEFAULT_FLAGS.split(" ");
      // Prepare optimizations
      String[] optArray = DEFAULT_OPTIMIZATIONS.split(" ");
      // Prepare files
      //File[] files = (new File(DEFAULT_INPUT_FOLDER)).listFiles();
      //List<File> files = IoUtils.getFilesRecursive(new File(DEFAULT_INPUT_FOLDER));
      List<File> programFolders = getProgramFolders();

      //List<List<File>> programs = getPrograms();

      List<GccRun> runs = new ArrayList<GccRun>();
      
      for(File programFolder : programFolders) {
      //for(List<File> fileArray : programs) {
         for(String optimization : optArray) {
            // Get files
            String[] inputFiles = getProgramFiles(programFolder);

            //String baseFilename = IoUtils.removeExtension(inputFiles[0], IoUtils.DEFAULT_EXTENSION_SEPARATOR);
            String baseFilename = programFolder.getName();
            String parentInputFolder = programFolder.getParent();
            String outputFolder = parentInputFolder+"\\elf\\"+optimization.substring(1)+"\\";
            //String outputFolder = DEFAULT_INPUT_FOLDER+"\\..\\elf\\"+optimization.substring(1)+"\\";
            //String outputFile = baseFilename + optimization + ".elf";
            String outputFile = outputFolder + baseFilename + optimization + ".elf";
            //System.out.println("Program Folder:"+programFolder.getName());
            //System.out.println("Program Folder:"+programFolder.getAbsolutePath());
            runs.add(new GccRun(outputFile, inputFiles, optimization, flagsArray, programFolder.getAbsolutePath()));
         }
      }

      return runs;
   }

   /**
    * Structure: the input folder has a folder for each program;
    * @return
    */
   private static List<File> getProgramFolders() {
      List<File> programFolders = new ArrayList<File>();
      File inputFolder = new File(DEFAULT_INPUT_FOLDER);

      // For each program folder, collect .c and .h files
      for(File programFolder : inputFolder.listFiles()) {
         if(!programFolder.isDirectory()) {
            continue;
         }

         programFolders.add(programFolder);
      }

      return programFolders;
   }

   /**
    * Structure: the input folder has a folder for each program; the program
    * is composed by .c and .h files.
    * @return
    */
   private static List<List<File>> getPrograms() {
      List<List<File>> returnList = new ArrayList<List<File>>();

      File inputFolder = new File(DEFAULT_INPUT_FOLDER);

      Set<String> extensions = new HashSet<String>();
      extensions.add(C_EXTENSION);
      extensions.add(HEADER_EXTENSION);

      // Get program folders
      File[] programFolders = inputFolder.listFiles();
      // For each program folder, collect .c and .h files
      for(File programFolder : programFolders) {
         if(!programFolder.isDirectory()) {
            continue;
         }
         returnList.add(IoUtils.getFilesRecursive(programFolder, extensions));
      }

      return returnList;
   }

   private static String[] getProgramFiles(File programFolder) {
      Set<String> extensions = new HashSet<String>();
      extensions.add(C_EXTENSION);
      extensions.add(HEADER_EXTENSION);

      List<File> fileList = IoUtils.getFilesRecursive(programFolder, extensions);
      String[] programFiles = new String[fileList.size()];

      for(int i=0; i<programFiles.length; i++) {
         programFiles[i] = fileList.get(i).getName();
      }

      return programFiles;
   }


   public static final String DEFAULT_FLAGS = "-Wall -g -mxl-barrel-shift -mno-xl-soft-div -mno-xl-soft-mul -xl-mode-executable";
   public static final String DEFAULT_OPTIMIZATIONS = "-O0 -O1 -O2 -O3";
   //public static final String DEFAULT_OPTIMIZATIONS = "-Os";
   public static final String DEFAULT_INPUT_FOLDER = "I:\\Workspace\\Resources\\MicroBlaze Benchmarks\\07 - Test Battery 3 (Dtool) (C and Elf)\\c";

   public static final String C_EXTENSION = "c";
   public static final String HEADER_EXTENSION = "h";


}

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
import java.util.Set;


/**
 *
 * @author Joao Bispo
 */
public class IoUtilsAppend {

   public static String removeExtension(String filename, String separator) {
      int extIndex = filename.lastIndexOf(separator);
      return filename.substring(0, extIndex);
   }

   public static List<File> getFilesRecursive(File folder, Set<String> extensions) {
      List<File> fileList = new ArrayList<File>();

      for(String extension : extensions) {
         fileList.addAll(getFilesRecursive(folder, extension));
      }
      
      return fileList;
   }

      public static List<File> getFilesRecursive(File folder, String extension) {
      List<File> fileList = new ArrayList<File>();
      File[] files = folder.listFiles(new ExtensionFilter(extension));

      for(File file : files) {
         fileList.add(file);
      }

      files = folder.listFiles();
      for(File file : files) {
         if(file.isDirectory()) {
            fileList.addAll(getFilesRecursive(file, extension));
         }
      }

      return fileList;
   }
 

      public static List<File> getFilesRecursive(File folder) {
      List<File> fileList = new ArrayList<File>();
      File[] files = folder.listFiles();

      for(File file : files) {
         if(file.isFile()) {
            fileList.add(file);
         }
      }

      for(File file : files) {
         if(file.isDirectory()) {
            fileList.addAll(getFilesRecursive(file));
         }
      }

      return fileList;
   }

   /**
    * Returns files from a folder with a certain extension.
    *
    * @param folder
    * @param extension
    * @return
    */
      /*
   public static File[] getFiles(File folder, String extension) {
      return folder.listFiles(new ExtensionFilter(extension));
   }
       */
       
}

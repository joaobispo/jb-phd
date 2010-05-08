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

package org.ancora.DMTool.Configuration;

import org.ancora.DMTool.Shell.System.GeneralPreferences;
import java.util.logging.Logger;
import org.ancora.SharedLibrary.Preferences.EnumPreferences;

/**
 *
 * @author Joao Bispo
 */
public enum FileType {
   trace,
   elf,
   block;

   public static FileType getFileType(String extension) {
      EnumPreferences prefs = GeneralPreferences.getPreferences();

      if(prefs.getPreference(GeneralPreferences.blockExtension).equals(extension)) {
         return block;
      }

      if(prefs.getPreference(GeneralPreferences.elfExtension).equals(extension)) {
         return elf;
      }

      if(prefs.getPreference(GeneralPreferences.traceExtension).equals(extension)) {
         return trace;
      }

      return null;
   }

      public static String getExtension(FileType fileType) {
      GeneralPreferences pref = null;
      switch(fileType) {
         case block:
            pref = GeneralPreferences.blockExtension;
            break;
         case elf:
            pref = GeneralPreferences.elfExtension;
            break;
         case trace:
            pref = GeneralPreferences.traceExtension;
            break;
         default:
            Logger.getLogger(FileType.class.getName()).
                    warning("File type not defined: "+fileType.name());
            return null;
      }

      return GeneralPreferences.getPreferences().getPreference(pref);
   }

}

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

package org.ancora.DMTool.deprecated.Configuration;

import org.ancora.DMTool.deprecated.Preference;
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
      EnumPreferences prefs = Preference.getPreferences();

      if(prefs.getPreference(Preference.blockExtension).equals(extension)) {
         return block;
      }

      if(prefs.getPreference(Preference.elfExtension).equals(extension)) {
         return elf;
      }

      if(prefs.getPreference(Preference.traceExtension).equals(extension)) {
         return trace;
      }

      return null;
   }

      public static String getExtension(FileType fileType) {
      Preference pref = null;
      switch(fileType) {
         case block:
            pref = Preference.blockExtension;
            break;
         case elf:
            pref = Preference.elfExtension;
            break;
         case trace:
            pref = Preference.traceExtension;
            break;
         default:
            Logger.getLogger(FileType.class.getName()).
                    warning("File type not defined: "+fileType.name());
            return null;
      }

      return Preference.getPreferences().getPreference(pref);
   }

}

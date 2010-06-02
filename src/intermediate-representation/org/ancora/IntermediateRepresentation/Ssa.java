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

package org.ancora.IntermediateRepresentation;

import java.util.logging.Logger;

/**
 * Method for working with names in SSA form.
 *
 * @author Joao Bispo
 */
public class Ssa {

   public static String buildSsaName(String originalName, int version) {
      // Check if original name has no separator character
      if(originalName.contains(SEPARATOR)) {
         Logger.getLogger(Ssa.class.getName()).
                 warning("Original name ("+originalName+") contains separator character ("+SEPARATOR+")");
      }
      return originalName + SEPARATOR + Integer.toString(version);
   }

   public static String getOriginalName(String ssaName) {
      int separatorIndex = ssaName.lastIndexOf(SEPARATOR);
      if(separatorIndex == -1) {
         Logger.getLogger(Ssa.class.getName()).
                 warning("Could not find separator in ssaName '"+ssaName+"'.");
         return ssaName;
      }
      return ssaName.substring(0, separatorIndex);
   }

   public static int getVersion(String ssaName) {
      int separatorIndex = ssaName.lastIndexOf(SEPARATOR);
      String versionString = ssaName.substring(separatorIndex+1);

      int value = -1;
      try {
         value = Integer.parseInt(versionString);
      } catch(NumberFormatException ex) {
         Logger.getLogger(Ssa.class.getName()).
                 warning("Could not parse value '"+versionString+" in SSA name '"+ssaName+"'. Returning "+value);
      }

      return value;
   }

   public static final String SEPARATOR = ".";
}

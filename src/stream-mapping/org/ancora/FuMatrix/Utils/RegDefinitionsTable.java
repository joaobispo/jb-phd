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

package org.ancora.FuMatrix.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import org.ancora.FuMatrix.Architecture.FuOutputSignal;

/**
 * Maps Register names to internal signals in FuOutputSignal format.
 *
 * @author Joao Bispo
 */
public class RegDefinitionsTable {

   public RegDefinitionsTable() {
      definitions = new HashMap<String, FuOutputSignal>();
   }


   /**
    * Returns the FuOutputSignal correspondent to the place where the following 
    * register was defined last.
    * 
    * @param registerName
    * @return
    */
   public FuOutputSignal getOutputSignal(String registerName) {
      FuOutputSignal output = definitions.get(registerName);
       if(output == null) {
            Logger.getLogger(RegDefinitionsTable.class.getName()).
                    warning("InternalData '"+registerName+"' not defined yet.");
         }
      return output;
   }

   /**
    * @param registerName
    * @return the line where the following register was defined last, or -1 if
    * if there was no definition
    */
   public int getLine(String registerName) {
      FuOutputSignal output = getOutputSignal(registerName);
      if(output == null) {
         Logger.getLogger(RegDefinitionsTable.class.getName()).
                    warning("Could not get line. Returning -1.");
         return -1;
      }

      return output.getCoordinate().getLine();
   }

   public void put(String registerName, FuOutputSignal output) {
      definitions.put(registerName, output);
   }

   public RegDefinitionsTable copy() {
      RegDefinitionsTable newTable = new RegDefinitionsTable();
      for(String key : definitions.keySet()) {
         newTable.put(key, definitions.get(key));
      }

      return newTable;
   }

   public Map<String, FuOutputSignal> getDefinitions() {
      return definitions;
   }



   /**
    * INSTANCE VARIABLES
    */
   private Map<String, FuOutputSignal> definitions;
}

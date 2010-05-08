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

package org.ancora.DMTool.Shell.System;

import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Logger;
import org.ancora.IrMapping.Mapper;
import org.ancora.IrMapping.AsapScenario1;
import org.ancora.IrMapping.AsapScenario2;
import org.ancora.SharedLibrary.Preferences.EnumPreferences;

/**
 *
 * @author Joao Bispo
 */
public class MapperDispenser {

   public static Mapper getCurrentMapper() {

      String mapperName = prefs.getPreference(GeneralPreferences.mapper).toLowerCase();
      MapperOption mapperOption = mapperOptions.get(mapperName);

      if(mapperOption == null) {
         Logger.getLogger(MapperDispenser.class.getName()).
                 info("Mapper '" + mapperName + "' not defined. Avaliable options:");
         for (MapperOption option : MapperOption.values()) {
            Logger.getLogger(MapperDispenser.class.getName()).
                    info("- " + option.name());
         }
         return null;
      }

      if(mapperOption == MapperOption.AsapScenario1) {
         return new AsapScenario1();
      }

      if(mapperOption == MapperOption.AsapScenario2) {
         return new AsapScenario2();
      }


      Logger.getLogger(MapperDispenser.class.getName()).
              info("Case not defined for mapperOption '" + mapperOption + "'");
      return null;
   }

   /**
    * VARIABLES
    */
   private static final EnumPreferences prefs = GeneralPreferences.getPreferences();
   //private static final InstructionFilter MICROBLAZE_JUMP_FILTER = new MbJumpFilter();
   
   private static final Map<String, MapperDispenser.MapperOption> mapperOptions;
   static {
      Map<String, MapperDispenser.MapperOption> aMap =
              new Hashtable<String, MapperDispenser.MapperOption>();

      aMap.put(Options.AsapScenario1.toLowerCase(), MapperOption.AsapScenario1);
      aMap.put(Options.AsapScenario2.toLowerCase(), MapperOption.AsapScenario2);


      mapperOptions = Collections.unmodifiableMap(aMap);
   }


   /**
    * ENUM
    */
   public enum MapperOption {
      AsapScenario1,
      AsapScenario2;
   }

   public interface Options {
      String AsapScenario1 = "ASAP-Scenario1";
      String AsapScenario2 = "ASAP-Scenario2";
   }
}

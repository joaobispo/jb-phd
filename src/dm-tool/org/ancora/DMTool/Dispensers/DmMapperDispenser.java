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

package org.ancora.DMTool.Dispensers;

import java.util.Map;
import java.util.logging.Logger;
import org.ancora.DMTool.Settings.Options;
import org.ancora.DMTool.Settings.Options.OptionName;
import org.ancora.IrMapping.Mapper;
import org.ancora.IrMapping.AsapScenario1;
import org.ancora.IrMapping.AsapScenario1;
import org.ancora.IrMapping.AsapScenario2;
import org.ancora.IrMapping.AsapScenario2;
import org.ancora.IrMapping.Mapper;
import org.ancora.IrMapping.MapperWithMoves;
import org.ancora.SharedLibrary.EnumUtils;

/**
 *
 * @author Joao Bispo
 */
public class DmMapperDispenser {

   public static Mapper getCurrentMapper() {

      String mapperName = Options.optionsTable.get(OptionName.mapping_mapper);
      //String mapperName = prefs.getPreference(Preference.mapper).toLowerCase();
      MapperName mapper = mappers.get(mapperName);
      //MapperOption mapperOption = mapperOptions.get(mapperName);

      if(mapper == null) {
//      if(mapperOption == null) {
         Logger.getLogger(DmMapperDispenser.class.getName()).
                 warning("Mapper '" + mapperName + "' not found.");
         /*
         info("Mapper '" + mapperName + "' not found. Avaliable options:");
         for (MapperOption option : MapperOption.values()) {
            Logger.getLogger(DmMapperDispenser.class.getName()).
                    info("- " + option.name());
         }
          *
          */
         return null;
      }

      return mapper.getMapper();

      /*
      if(mapperOption == MapperOption.AsapScenario1) {
         return new AsapScenario1();
      }

      if(mapperOption == MapperOption.AsapScenario2) {
         return new AsapScenario2();
      }


      Logger.getLogger(DmMapperDispenser.class.getName()).
              info("Case not defined for mapperOption '" + mapperOption + "'");
      return null;
       * 
       */
   }

   /**
    * VARIABLES
    */
   //private static final EnumPreferences prefs = Preference.getPreferences();
   //private static final InstructionFilter MICROBLAZE_JUMP_FILTER = new MbJumpFilter();
   /*
   private static final Map<String, DmMapperDispenser.MapperOption> mapperOptions;
   static {
      Map<String, DmMapperDispenser.MapperOption> aMap =
              new HashMap<String, DmMapperDispenser.MapperOption>();

      aMap.put(Options.AsapScenario1.toLowerCase(), MapperOption.AsapScenario1);
      aMap.put(Options.AsapScenario2.toLowerCase(), MapperOption.AsapScenario2);


      mapperOptions = Collections.unmodifiableMap(aMap);
   }

*/
   /**
    * ENUM
    */
   /*
   public enum MapperOption {
      AsapScenario1,
      AsapScenario2;
   }
    *
    */

   /*
   public interface Options {
      String AsapScenario1 = "ASAP-Scenario1";
      String AsapScenario2 = "ASAP-Scenario2";
   }
    *
    */

public static Map<String, MapperName> mappers =
           EnumUtils.buildMap(MapperName.values());


   /**
    * MAPPERS
    */
   public static enum MapperName {

      AsapScenario1("asap-scenario1"),
      AsapScenario2("asap-scenario2"),
      MapperWithMoves("asap-moves");

      private MapperName(String mapperName) {
         this.mapperName = mapperName;
      }

      @Override
      public String toString() {
         return mapperName;
      }

      public String getMapperName() {
         return mapperName;
      }


      public Mapper getMapper() {
         switch (this) {
            case AsapScenario1:
               return new AsapScenario1();
            case AsapScenario2:
               return new AsapScenario2();
            case MapperWithMoves:
               return new MapperWithMoves();
            default:
               Logger.getLogger(DmMapperDispenser.class.getName()).
                       warning("Case not defined: '" + this);
               return null;
         }
      }
      private String mapperName;
   }
}

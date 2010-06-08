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

package org.ancora.FuMatrix.Mapper;

import java.util.Map;
import java.util.logging.Logger;
import org.ancora.DMTool.Settings.Options;
import org.ancora.DMTool.Settings.Options.OptionName;
import org.ancora.Shared.EnumUtilsAppend;


/**
 *
 * @author Joao Bispo
 */
public class DmStreamMapperDispenser {

   public static GeneralMapper getCurrentMapper() {

      String mapperNameString = Options.optionsTable.get(OptionName.mapping_stream_mapper);
      MapperName mapperName = mappers.get(mapperNameString);

      if(mapperName == null) {
         Logger.getLogger(DmStreamMapperDispenser.class.getName()).
                 warning("Mapper '" + mapperNameString + "' not found.");
         return null;
      }

      // Configure Mapper
      GeneralMapper mapper = mapperName.getMapper();
      mapper.setMaxColGeneral(Integer.parseInt(Options.optionsTable.get(OptionName.mapping_max_col_general)));
      mapper.setMaxColMemory(Integer.parseInt(Options.optionsTable.get(OptionName.mapping_max_col_memory)));
      mapper.setMaxCommDistance(Integer.parseInt(Options.optionsTable.get(OptionName.mapping_max_comm_distance)));
 
      return mapper;

   }

public static Map<String, MapperName> mappers =
           EnumUtilsAppend.buildMap(MapperName.values());


   /**
    * MAPPERS
    */
   public static enum MapperName {

      MapperNaive("naive-mapper");

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


      public GeneralMapper getMapper() {
         switch (this) {
            case MapperNaive:
               return new NaiveMapper();
            default:
               Logger.getLogger(DmStreamMapperDispenser.class.getName()).
                       warning("Case not defined: '" + this);
               return null;
         }
      }
      private String mapperName;
   }
}

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

package org.specs.DMTool2.Dispensers;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.ancora.FuMatrix.Mapper.GeneralMapper;
import org.ancora.FuMatrix.Mapper.NaiveMapper;
import org.ancora.IntermediateRepresentation.Operation;
import org.ancora.SharedLibrary.EnumUtils;
import org.specs.DMTool2.Settings.Settings;


/**
 *
 * @author Joao Bispo
 */
public class MapperDispenser {

   public static GeneralMapper getCurrentMapper() {

      String mapperNameString = Settings.optionsTable.get(MapperOption.current_mapper);
      MapperName mapperName = mappers.get(mapperNameString);

      if(mapperName == null) {
         Logger.getLogger(MapperDispenser.class.getName()).
                 warning("Mapper '" + mapperNameString + "' not found.");
         return null;
      }

      // Configure Mapper
      GeneralMapper mapper = mapperName.getMapper();
      mapper.setMaxColGeneral(Integer.parseInt(Settings.optionsTable.get(MapperOption.max_columns_alu)));
      mapper.setMaxColMemory(Integer.parseInt(Settings.optionsTable.get(MapperOption.max_columns_mem)));
      mapper.setMaxCommDistance(Integer.parseInt(Settings.optionsTable.get(MapperOption.max_comm_distance)));
      mapper.setUseConditionalExitLimit(Boolean.parseBoolean(Settings.optionsTable.get(MapperOption.use_conditional_limits)));
 
      return mapper;

   }

   /**
    *
    * @param operations
    * @return a GeneralMapper after mapping the given operations, or null if there
    * was an error while mapping.
    */
   public static GeneralMapper applyCurrentMapper(List<Operation> operations) {
      GeneralMapper mapper = MapperDispenser.getCurrentMapper();
      boolean sucess = true;
      for (Operation operation : operations) {
         sucess = mapper.accept(operation);
         if (!sucess) {
            return null;
         }
      }
      return mapper;
   }

public static Map<String, MapperName> mappers =
           EnumUtils.buildMap(MapperName.values());


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
               Logger.getLogger(MapperDispenser.class.getName()).
                       warning("Case not defined: '" + this);
               return null;
         }
      }
      private String mapperName;
   }
}

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

package org.ancora.InstructionBlock.deprecated;

import org.ancora.InstructionBlock.InstructionBlock;
import org.ancora.InstructionBlock.InstructionBlockListener;
import org.ancora.IrMapping.Mapper;

/**
 *
 * @author Joao Bispo
 */
public class MapperListener implements InstructionBlockListener {

   public MapperListener(Mapper mapper) {
      this.mapper = mapper;
   }


   public void accept(InstructionBlock instructionBlock) {
      mapper.processOperations(null);
      throw new UnsupportedOperationException("Not supported yet.");
   }

   public void flush() {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   /**
    * INSTANCE VARIABLES
    */
   private Mapper mapper;
}

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

package org.ancora.InstructionBlock;

import java.util.logging.Logger;
import org.ancora.MicroBlaze.InstructionName;

/**
 * Methods for parsing MicroBlaze information from InstructionBlock classes.
 *
 * @author Joao Bispo
 */
public class MbBlockUtils {

   public static InstructionName getInstructionName(GenericInstruction instruction) {
      return getInstructionName(instruction.getInstruction());
   }

   public static InstructionName getInstructionName(String instruction) {
      String whitespace = " ";
      int whiteSpaceIndex = instruction.indexOf(whitespace);
      if(whiteSpaceIndex == -1) {
         Logger.getLogger(MbBlockUtils.class.getName()).
                 warning("Could not find name separator '"+whitespace+"' in MicroBlaze Instruction: '"+instruction+"'.");
         return null;
      }
      String instNameString = instruction.substring(0, whiteSpaceIndex);
      InstructionName instName = InstructionName.getEnum(instNameString);

      return instName;
   }
}

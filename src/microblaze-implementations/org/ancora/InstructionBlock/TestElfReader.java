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

import org.ancora.InstructionBlock.InstructionBusReader;
import org.ancora.InstructionBlock.GenericInstruction;

/**
 *
 * @author Ancora Group <ancora.codigo@gmail.com>
 */
public class TestElfReader {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //String systemConfigFile = "./systemconfig.xml";
        String systemConfigFile = "Configuration Files\\systemconfig.xml";
        String binaryFile = "./elf/own_adpcm_coder.elf";

        InstructionBusReader reader = MbElfReader.createMbElfReader(systemConfigFile, binaryFile);

        GenericInstruction instruction = reader.nextInstruction();
        while(instruction != null) {
         System.out.println(instruction);
         instruction = reader.nextInstruction();
        }

    }

}

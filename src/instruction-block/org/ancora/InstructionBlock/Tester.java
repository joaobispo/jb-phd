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

import java.io.File;


/**
 *
 * @author Ancora Group <ancora.codigo@gmail.com>
 */
class Tester {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //System.out.println("Hello");
        testBlockIO();
    }

   private static void testBlockIO() {
      InstructionBlock block = BlockIO.fromFile(new File("data/adpcm-coder_trace_without_optimization-0.block"));
      BlockIO.toFile(new File("E:/block.txt"), block);
   }

}

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


/**
 * Object which listens other objects for InstructionBlock objects.
 * 
 * @author Joao Bispo
 */
public interface InstructionBlockListener {

   /**
    * Accepts an InstructionBlock.
    *
    * @param instructionBlock
    */
   public void accept(InstructionBlock instructionBlock);

   /**
    * Signals the end of a stream of data, finishes any pending processing.
    */
   public void flush();
}

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

package org.ancora.Partitioning;

/**
 * Returns partitioners for MicroBlaze instructions.
 *
 * @author Joao Bispo
 */
public class PartitionerDispenser {

   public static MbBasicBlock getMbBasicBlock() {
      return new MbBasicBlock();
   }

   public static SuperBlock getMbSuperBlock() {
      return new SuperBlock(new MbBasicBlock());
   }

   public static MegaBlock getMbMegaBlock(int maxPatternSize) {
       return new MegaBlock(new SuperBlock(new MbBasicBlock()), maxPatternSize);
   }

}

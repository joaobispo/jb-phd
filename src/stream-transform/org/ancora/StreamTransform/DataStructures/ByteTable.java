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

package org.ancora.StreamTransform.DataStructures;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 *VALUES ARE DUMMIES! NOT REALLY SPLITING VALUES YET!
 *
 * @author Joao Bispo
 */
public class ByteTable {

   public ByteTable() {
      byteTable = new HashMap<Integer, Integer>();
   }

   public void putMem(Integer address, Integer value, MemSize size) {
      // Split word into four
      put(address, size.getByteSize());
   }

   public Integer getMem(Integer address, MemSize size) {
      // Check if all have values
      return get(address, size.getByteSize());
   }

   private void put(Integer address, int numBytes) {
      // Split word into four
      Integer value = Integer.decode("0xF");
      for(int i=0; i<numBytes; i++) {
         byteTable.put(address+1, value);
      }
   }

   private Integer get(Integer address, int numBytes) {
      for(int i=0; i<numBytes; i++) {
         Integer value = byteTable.get(address+i);
         if(value == null) {
            //Logger.getLogger(ByteTable.class.getName()).
            //        warning("Address not in table:"+address+"+"+i);
            return null;
         }
      }

      return Integer.decode("0xF");
   }

   Map<Integer, Integer> byteTable;

   public enum MemSize {

      Byte(1),
      Half(2),
      Word(4);

      private MemSize(int byteSize) {
         this.byteSize = byteSize;
      }

      public int getByteSize() {
         return byteSize;
      }



      private int byteSize;
   }
}

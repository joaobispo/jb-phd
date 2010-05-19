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
package org.ancora.IntermediateRepresentation.Transformations.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import org.ancora.IntermediateRepresentation.Operand;
import org.ancora.IntermediateRepresentation.OperandType;
import org.ancora.IntermediateRepresentation.Operands.Literal;

/**
 *
 * @author Joao Bispo
 */
public class MemoryTable {

   public MemoryTable() {
      bases = new HashMap<String, Operand>();
      lastStoreType = AddressType.INVALID;
   }

   public AddressType calculateType(Operand base, Operand offset) {
      // Check if first is Literal and second is Operand
      boolean LD = base.getType() == OperandType.literal
              && offset.getType() == OperandType.internalData;

      if (LD) {
         return AddressType.LD;
      }

      // Check if is Operand and other is Literal
      boolean DL = base.getType() == OperandType.internalData
              && offset.getType() == OperandType.literal;

      if (DL) {
         return AddressType.DL;
      }

      boolean LL = base.getType() == OperandType.literal
              && offset.getType() == OperandType.literal;

      if (LL) {
         return AddressType.LL;
      }

      boolean DD = base.getType() == OperandType.internalData
              && offset.getType() == OperandType.internalData;

      if (DD) {
         return AddressType.DD;
      }

      System.err.println("Don't know this case: base (" + base + ") of type '"
              + base.getType() + "' and offset (" + offset + ") of type '" + offset.getType() + "'.");

      return null;
   }

   /**
    * If all is ok, always returns a valid String. If a null is returned, there
    * is a problem with the address definitions.
    * @param base
    * @param offset
    * @return
    */
   public String getAddress(Operand base, Operand offset) {
      // Get AddressType
      AddressType addrType = calculateType(base, offset);


      switch (addrType) {
         case LD:
            return getAddress(offset, base);
         case DL:
            return base.toString() + "-" + Literal.getInteger(offset);
         case LL:
            // Sum both literals
            String value = String.valueOf(Literal.getInteger(base) + Literal.getInteger(offset));
//                   System.err.println("Case LL:" + value);
            return value;
         case DD:
            String offsetString = offset.toString();
            String baseString = base.toString();
            String returnString = "";
            if (baseString.compareTo(offsetString) <= 0) {
               //return baseString + "-" + offsetString;
               returnString = baseString + "-" + offsetString;
            } else {
               //return offsetString + "-" + baseString;
               returnString = offsetString + "-" + baseString;
            }
            //               System.err.println("Case DD:" + returnString);
            return returnString;
         default:
            Logger.getLogger(MemoryTable.class.getName()).
                    warning("Case not defined:" + addrType);
            return null;
      }
   }

   //public boolean updateTable(Operand base, Operand offset, Operand content, boolean isStore) {
   public boolean updateTable(Operand base, Operand offset, Operand content, boolean isStore) {
      String address = getAddress(base, offset);
      
      AddressType type = calculateType(base, offset);
      String mem = "load";
      if(isStore) {
         mem = "store";
      }
//      System.err.println("Adding to table address "+address+" of type "+type+" from "+mem);

/*
      if (address == null) {
         if (isStore) {
            // Store to an unknown position. Flushing tables.
            bases = new HashMap<String, Operand>();
//            System.out.println("Store to unknown position ("+base+" + "+offset+"). " +
//                    "Flushed memory tables.");
         }
         return false;

      }
*/
      // Check if store is of same type of last store
      if (isStore) {
         AddressType addrType = calculateType(base, offset);
         // LD and DL are equivalent, transform the first into the second
         if (addrType == AddressType.LD) {
            addrType = AddressType.DL;
            Operand temp = base;
            base = offset;
            offset = temp;
         }

         boolean enterElse = addrType == AddressType.DD;
         if(includeDL) {
            enterElse = enterElse || addrType == AddressType.DL;
         }

         if (addrType != lastStoreType) {
            // Store to a different realm. Flushing tables.
            bases = new HashMap<String, Operand>();
 //              System.err.println("Changed store realm from "+lastStoreType+" to "+addrType);
            lastStoreType = addrType;
            lastBaseId = calculateBaseId(base, offset);
//         } else if(addrType == AddressType.DL || addrType == AddressType.DD) {
//         } else if(addrType == AddressType.DD) {
         } else if(enterElse) {
            // Check if base is the same
            String newBase = calculateBaseId(base, offset);
            if(!lastBaseId.equals(newBase)) {
               // Store inside the same realm, but can't prove it does not overlap.
               // Flushing tables.
               bases = new HashMap<String, Operand>();
 //              System.err.println("Store to the same realm, but cannot prove "+lastBaseId+" does not overlap "+address);
               lastBaseId = newBase;
            }
         }

         // Check base id
         // If ll, it's alright
         // If dl, check d


      }



      bases.put(address, content);
      //System.out.println("Stored '"+content+"'.");
      return true;
   }

   private String calculateBaseId(Operand base, Operand offset) {
      AddressType addrType = calculateType(base, offset);
      switch(addrType) {
         case LD:
            return calculateBaseId(offset, base);
         case DL:
            return base.toString();
         case LL:
            return "";
         case DD:
            return getAddress(base, offset);
         default:
            Logger.getLogger(MemoryTable.class.getName()).
                    warning("Case not defined:" + addrType);
            return null;
      }
   }

   /**
    * Justs reads an ope
    * @param opAddress
    * @param base
    * @param offset
    * @param output
    * @return
    */
   public Operand getOperand(Operand base, Operand offset) {
   //public Operand getOperand(String address) {
      String address = getAddress(base, offset);
      return bases.get(address);
   }
   /*
   public Operand getOperand(int opAddress, Operand base, Operand offset, Operand output) {
      String address = getAddress(base, offset);

//      if (address == null) {
         //System.out.println("Load from unknown position ("+base+" + "+offset+").");
         return null;
//      }

      Operand value = bases.get(address);
      if (value == null) {
         //   System.out.println(opAddress+": Load from known position, but value is not in table. Output " +
         //           "put in table.");
         updateTable(base, offset, output, false);
      }

      return value;

   }
    *
    *
    */

   /**
    * INSTANCE VARIABLES
    */
   private Map<String, Operand> bases;
   private AddressType lastStoreType;
   private String lastBaseId;
   private boolean includeDL = false;


   /**
    * INNER ENUM
    */
   public enum AddressType {
      LD,
      DL,
      DD,
      LL,
      INVALID;
   }
}

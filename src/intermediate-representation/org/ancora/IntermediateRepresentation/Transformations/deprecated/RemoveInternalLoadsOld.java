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

package org.ancora.IntermediateRepresentation.Transformations.deprecated;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.ancora.IntermediateRepresentation.Operand;
import org.ancora.IntermediateRepresentation.Operands.Literal;
import org.ancora.IntermediateRepresentation.OperandType;
import org.ancora.IntermediateRepresentation.Operation;
import org.ancora.IntermediateRepresentation.Operations.MemoryLoad;
import org.ancora.IntermediateRepresentation.Operations.MemoryStore;
import org.ancora.IntermediateRepresentation.Operations.Nop;
import org.ancora.IntermediateRepresentation.OperationType;
import org.ancora.IntermediateRepresentation.Transformation;

/**
 *
 * @author Joao Bispo
 */
public class RemoveInternalLoadsOld extends Transformation {

   @Override
   public String toString() {
      return "Remove Internal Loads Old";
   }


// TODO: Use substitute table
   public void transform(List<Operation> operations) {
      Map<String, Operand> literalRegisters = new HashMap<String, Operand>();
      MemoryTable memTable = new MemoryTable();

      int loadCounter = 0;
      int storeCounter = 0;
      int totalLoads = 0;
      for(int i=0; i<operations.size(); i++) {
         Operation operation = operations.get(i);
         
         substituteRegisterForLiterals(operation, literalRegisters);


         if(operation.getType() == OperationType.MemoryLoad) {
            totalLoads++;
            MemoryLoad load = (MemoryLoad)operation;
            Operand internalData = memTable.getOperand(load.getAddress(), load.getInput1(), load.getInput2(), load.getOutput());

            if(internalData == null) {
               continue;
            }
            updateStats(operation);

            loadCounter++;
            literalRegisters.put(load.getOutput().toString(), internalData);
            operations.set(i, new Nop(operation));
            //System.out.println("Removed operation:"+operation.getFullOperation());
         }


         if(operation.getType() == OperationType.MemoryStore) {
            MemoryStore store = (MemoryStore)operation;
            boolean success = memTable.updateTable(store.getOperand1(), store.getOperand2(), store.getContentsToStore(), true);
            if(success) {
               //updateStats(operation);
               storeCounter++;
            }

         }

      }


      float loadRatio = 0;
      if(totalLoads != 0) {
         loadRatio = ((float)loadCounter / (float)totalLoads) * 100;
      }
      System.out.println("Stored "+storeCounter+" operands.");
      System.out.println("Can remove "+loadCounter+" loads.");
      System.out.println("Can remove "+loadRatio+" of loads.");

   }

   private void substituteRegisterForLiterals(Operation operation,
           Map<String, Operand> literalRegisters) {

      // Check inputs, if an input matches an element from the table,
      //substitute it for the corresponding operand

      List<Operand> operands = operation.getInputs();
      for(int i=0; i<operands.size(); i++) {
         Operand operand = literalRegisters.get(operands.get(i).toString());
         if(operand != null) {
            operands.set(i, operand);
            //System.out.println("SUBSTITUTED!");
         }
      }

   }


    /**
     * INNER CLASS
     */
   class MemoryTable {

      public MemoryTable() {
         bases = new HashMap<String, Operand>();
         lastStoreType = AddressType.INVALID;
      }

      public AddressType calculateType(Operand base, Operand offset) {
         // Check if first is Literal and second is Operand
         boolean LD = base.getType() == OperandType.literal &&
                 offset.getType() == OperandType.internalData;

         if(LD) {
            return AddressType.LD;
         }

         // Check if is Operand and other is Literal
         boolean DL = base.getType() == OperandType.internalData &&
                 offset.getType() == OperandType.literal;

         if(DL) {
            return AddressType.DL;
         }

         boolean LL = base.getType() == OperandType.literal
                 && offset.getType() == OperandType.literal;

         if(LL) {
            return AddressType.LL;
         }

         boolean DD = base.getType() == OperandType.internalData
                 && offset.getType() == OperandType.internalData;

         if(DD) {
            return AddressType.DD;
         }

         System.err.println("Don't know this case: base ("+base+") of type '"+
                 base.getType()+"' and offset ("+offset+") of type '"+offset.getType()+"'.");
 
         return null;
      }
/*
      public String getAddress(Operand base, Operand offset) {
         // Check if first is Literal and second is Operand
         boolean LD = base.getType() == OperandType.literal &&
                 offset.getType() == OperandType.internalData;

         if(LD) {
            // Swap
            Operand temp = base;
            base = offset;
            offset = temp;
         }

         // Check if is Operand and other is Literal
         boolean DL = base.getType() == OperandType.internalData &&
                 offset.getType() == OperandType.literal;

         if(DL) {
            //String address = base.toString() + "-" + Literal.getInteger((Literal)offset);
            String address = base.toString() + "-" + Literal.getInteger(offset);
            return address;
         }

         boolean LL = base.getType() == OperandType.literal
                 && offset.getType() == OperandType.literal;

         if(LL) {
            // Sum both literals
            Integer baseInt = Literal.getInteger(base);
            Integer offsetInt = Literal.getInteger(offset);

            if(baseInt != null && offsetInt != null) {
               String value = String.valueOf(baseInt+offsetInt);
               System.err.println("Case LL:"+value);
               return value;
               //return String.valueOf(baseInt+offsetInt);
            }
         }

         boolean DD = base.getType() == OperandType.internalData
                 && offset.getType() == OperandType.internalData;

         if(DD) {
            // Reorder them so they are in canonical form
            List<String> addressString = new ArrayList<String>();
            addressString.add(base.toString());
            addressString.add(offset.toString());
            Collections.sort(addressString);

            String offsetString = offset.toString();
            String baseString = base.toString();
            String returnString = "";
            if(baseString.compareTo(offsetString) <= 0) {
               //return baseString + "-" + offsetString;
               returnString = baseString + "-" + offsetString;
            } else {
               //return offsetString + "-" + baseString;
               returnString = offsetString + "-" + baseString;
            }
            System.err.println("Case DD:"+returnString);
            return returnString;
         }

         System.err.println("Don't know this case: base ("+base+") and offset ("+offset+")");
         return null;
      }
*/

       public String getAddress(Operand base, Operand offset) {
          // Get AddressType
          AddressType addrType = calculateType(base, offset);
      /*
          if(addrType == null) {
             return null;
          }
       *
       */
          switch(addrType) {
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
                Logger.getLogger(RemoveInternalLoadsOld.class.getName()).
                        warning("Case not defined:"+addrType);
                return null;
          }
      }

      public boolean updateTable(Operand base, Operand offset, Operand content, boolean isStore) {
         String address = getAddress(base, offset);
         
         if(address == null) {
            if (isStore) {
               // Store to an unknown position. Flushing tables.
               bases = new HashMap<String, Operand>();
//            System.out.println("Store to unknown position ("+base+" + "+offset+"). " +
//                    "Flushed memory tables.");
            }
            return false;

         }

         // Check if store is of same type of last store
         
         if(isStore) {
            AddressType addrType = calculateType(base, offset);
            // LD and DL are equivalent, transform the first into the second
            if(addrType == AddressType.LD) {
               addrType = AddressType.DL;
            }

            if(addrType != lastStoreType) {
               // Store to a different realm. Flushing tables.
               bases = new HashMap<String, Operand>();
//               System.err.println("Changed store realm from "+lastStoreType+" to "+addrType);
               lastStoreType = addrType;
            }
         }
          
        

         bases.put(address, content);
         //System.out.println("Stored '"+content+"'.");
         return true;
      }

      public Operand getOperand(int opAddress, Operand base, Operand offset, Operand output) {
         String address = getAddress(base, offset);

         if(address == null) {
            //System.out.println("Load from unknown position ("+base+" + "+offset+").");
            return null;
         }
         
         Operand value = bases.get(address);
         if(value == null) {
         //   System.out.println(opAddress+": Load from known position, but value is not in table. Output " +
         //           "put in table.");
            updateTable(base, offset, output, false);
         }

         return value;
     
      }

      /**
       * INSTANCE VARIABLES
       */
      private Map<String, Operand> bases;
      private AddressType lastStoreType;

   }

      public enum AddressType {
         LD,
         DL,
         DD,
         LL,
         INVALID;
      }
}

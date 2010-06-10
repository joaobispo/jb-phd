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

package org.ancora.IntermediateRepresentation;

import java.util.logging.Logger;

/**
 * Utility methods related to operations.
 *
 * @author Joao Bispo
 */
public class OperationService {

   /**
    * @param operation
    * @return true if all inputs of operation are literals
    */
   public static boolean hasLiteralInputs(Operation operation) {
      for(Operand input : operation.getInputs()) {
         if(input.getType() != OperandType.literal) {
            return false;
         }
      }

      return true;
   }


    /**
     * Calculates the carryOut of the sum of rA with rB and carry.
     * Operation is rA + rB + carry.
     *
     * @param rA
     * @param rB
     * @param carry the carry from the previous operation. Should be 0 or 1.
     * @return 1 if there is carry out, or 0 if not.
     */
    public static int getCarryOutAdd(int rA, int rB, int carry) {
        if(carry != 0 && carry != 1) {
            Logger.getLogger(OperationService.class.getName()).
                    warning("Carry is different than 0 or 1 ("+
                    carry+")");
        }

        // Extend operands to long and mask them
        long lRa = rA & MASK_32_BITS;
        long lRb = rB & MASK_32_BITS;
        // Carry must be 0 or 1, it shouldn't need to be masked.
        long lCarry = carry;

        // Do the summation
        long result = lRa + lRb + lCarry;

        // Get the carry bit
        int carryOut = (int) ((result & MASK_BIT_33) >>> 32);
        return carryOut;
    }

    /**
     * Calculates the carryOut of the reverse subtraction of rA with rB and
     * carry. Operation is rB + ~rA + carry.
     *
     * @param rA
     * @param rB
     * @param carry the carry from the previous operation. Should be 0 or 1.
     * @return 1 if there is carry out, or 0 if not.
     */
    public static int getCarryOutRsub(int rA, int rB, int carry) {
        if(carry != 0 && carry != 1) {
            Logger.getLogger(OperationService.class.getName()).
                    warning("Carry is different than 0 or 1 ("+
                    carry+")");
        }

        // Extend operands to long and mask them
        long lRa = rA & MASK_32_BITS;
        long lRb = rB & MASK_32_BITS;
        // Carry must be 0 or 1, it shouldn't need to be masked.
        long lCarry = carry;

        // Do the summation
        long result = lRb + ~lRa + lCarry;

        // Get the carry bit
        int carryOut = (int) ((result & MASK_BIT_33) >>> 32);
        return carryOut;
    }
    
   private static final long MASK_32_BITS = 0xFFFFFFFFL;
   private static final long MASK_BIT_33 = 0x100000000L;
}

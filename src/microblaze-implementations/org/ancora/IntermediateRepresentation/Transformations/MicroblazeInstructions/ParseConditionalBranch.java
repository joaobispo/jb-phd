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

package org.ancora.IntermediateRepresentation.Transformations.MicroblazeInstructions;

import java.util.ArrayList;
import static org.ancora.IntermediateRepresentation.Operations.ConditionalExit.Op.*;

import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import org.ancora.IntermediateRepresentation.Operation;
import org.ancora.IntermediateRepresentation.Operations.ConditionalExit;
import org.ancora.IntermediateRepresentation.Operations.MbOperation;
import org.ancora.MicroBlaze.InstructionName;
import org.ancora.MicroBlaze.InstructionProperties;
import org.ancora.IntermediateRepresentation.MbTransformUtils;
import org.ancora.IntermediateRepresentation.Transformation;

/**
 *
 * @author Joao Bispo
 */
public class ParseConditionalBranch implements Transformation {

   public List<Operation> transform(List<Operation> operations) {
      //List<Operation> newList = new ArrayList<Operation>();
      //List<Operation> newList = new LinkedList<Operation>();

      for(int i=0; i<operations.size(); i++) {
        // Check if MicroBlaze Operation
        MbOperation branchOp = MbOperation.getMbOperation(operations.get(i));
        if(branchOp == null) {
           //newList.add(operations.get(i));
           continue;
        }

        // Check if it is a conditional compare
        ConditionalExit.Op compareOperation = instructionProperties.get(branchOp.getMbType());
        if(compareOperation == null) {
           //newList.add(operations.get(i));
           continue;
        }

        // Check if it has delay slots
        int delaySlots = MbTransformUtils.getDelaySlots(branchOp.getMbType());

        // Calculate nextAddress
        int nextAddress = MbTransformUtils.calculateNextAddress(operations, i, branchOp.getMbType());
        // Calculate offset
        //int offset = nextAddress - branchOp.getAddress();

        ConditionalExit cexit = new ConditionalExit(branchOp.getAddress(),
                compareOperation, nextAddress, delaySlots, 
                branchOp.getInputs().get(0).copy(), branchOp.getInputs().get(1).copy());


        operations.set(i, cexit);
        //newList.add(cexit);

        // Remove old operation
        /*
        System.out.println("before remove:"+operations.size());
        newList.remove(i);
        newList.addAll(i, newOperations);
        // Update i
        i += newOperations.size() - 1;
         */
      }

     return operations;
     //return newList;
   }
/*
   private ConditionalExit.Op getCompareOp(InstructionName instructionName) {
      CexitProperties props = instructionProperties.get(instructionName);
      if(props == null) {
         return null;
      } else {
         return props.operation;
      }
   }
*/

   /**
    * Calculates the next address.
    * TODO: Public Static Candidate
    *
    * @param operations
    * @param i
    * @return
    */
   /*
   private int calculateNextAddress(List<Operation> operations, int i,
           InstructionName instructionName) {

      // Usually, the index of the next instruction would be i+1
      int nextInstructionIndex = i + 1;

      // Check if instruction has delay slot
      if (InstructionProperties.INSTRUCTIONS_WITH_DELAY_SLOT.contains(instructionName)) {
         nextInstructionIndex++;
      }

      if (nextInstructionIndex >= operations.size()) {
         nextInstructionIndex = 0;
      }

      return operations.get(nextInstructionIndex).getAddress();
   }
    */


   
   
   @Override
   public String toString() {
      return "ParseConditionalBranches";
   }

   /**
    * INSTANCE VARIABLES
    */
   private static final Map<InstructionName, ConditionalExit.Op> instructionProperties;
   static {
      Map<InstructionName, ConditionalExit.Op> aMap = new Hashtable<InstructionName, ConditionalExit.Op>();

      aMap.put(InstructionName.beq, equal);
      aMap.put(InstructionName.beqd, equal);
      aMap.put(InstructionName.beqi, equal);
      aMap.put(InstructionName.beqid, equal);

      aMap.put(InstructionName.bge, greaterOrEqual);
      aMap.put(InstructionName.bged, greaterOrEqual);
      aMap.put(InstructionName.bgei, greaterOrEqual);
      aMap.put(InstructionName.bgeid, greaterOrEqual);

      aMap.put(InstructionName.bgt, greater);
      aMap.put(InstructionName.bgtd, greater);
      aMap.put(InstructionName.bgti, greater);
      aMap.put(InstructionName.bgtid, greater);

      aMap.put(InstructionName.ble, lessOrEqual);
      aMap.put(InstructionName.bled, lessOrEqual);
      aMap.put(InstructionName.blei, lessOrEqual);
      aMap.put(InstructionName.bleid, lessOrEqual);

      aMap.put(InstructionName.blt, less);
      aMap.put(InstructionName.bltd, less);
      aMap.put(InstructionName.blti, less);
      aMap.put(InstructionName.bltid, less);

      aMap.put(InstructionName.bne, notEqual);
      aMap.put(InstructionName.bned, notEqual);
      aMap.put(InstructionName.bnei, notEqual);
      aMap.put(InstructionName.bneid, notEqual);


      instructionProperties = Collections.unmodifiableMap(aMap);
   }





   

   /**
    * INNER CLASS
    */
   /*
   static class CexitProperties {

      public CexitProperties(ConditionalExit.Op operation, boolean hasDelaySlot) {
         this.operation = operation;
         this.hasDelaySlot = hasDelaySlot;
      }


*/
      /**
       * INSTANCE VARIABLES
       */
   /*
      private ConditionalExit.Op operation;
      private boolean hasDelaySlot;
   }
    */


}

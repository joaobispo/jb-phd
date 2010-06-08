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

package org.ancora.FuMatrix.Architecture;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.ancora.FuMatrix.Utils.RegDefinitionsTable;
import org.ancora.IntermediateRepresentation.Operand;
import org.ancora.IntermediateRepresentation.OperandType;
import org.ancora.IntermediateRepresentation.Operands.Literal;
import org.ancora.IntermediateRepresentation.Operation;
import org.ancora.IntermediateRepresentation.OperationType;
import org.ancora.IntermediateRepresentation.Ssa;

/**
 *
 * @author Joao Bispo
 */
public class Fu {

   /**
    * Builds a list the same size as operation inputs, and assigns a Signal to
    * each input.
    *
    * @param coor
    * @param operation
    * @param registerDefinitions
    * @return
    */
   private static List<Signal> buildInputs(FuCoor opCoor, Operation operation, 
           RegDefinitionsTable regDefinitions) {
     List<Signal> fuInputs = new ArrayList<Signal>();

     List<Operand> operands = operation.getInputs();
     for(int i=0; i<operands.size(); i++) {
        Operand operand = operation.getInputs().get(i);
        

        Signal newSignal = null;

        if(operand.getType() == OperandType.literal) {
           newSignal = new LiteralSignal(Literal.getInteger(operand));
        }

        if(operand.getType() == OperandType.livein) {
           newSignal = new LiveinSignal(operand.getName());
        }

        if(operand.getType() == OperandType.internalData) {
            // Check where this signal comes from
           String registerName = Ssa.getOriginalName(operand.getName());
           newSignal = regDefinitions.getOutputSignal(registerName);
        }

        if(newSignal == null) {
           Logger.getLogger(Fu.class.getName()).
                   warning("Case not defined:"+operand.getType());
           continue;
        }

     }

     return fuInputs;

   }

   /**
    * Only build live-outs if it is a possible exit.
    *
    * @param operation
    * @param registerDefinitions
    * @return
    */
   private static RegDefinitionsTable buildLiveouts(Operation operation, RegDefinitionsTable registerDefinitions) {
      if(operation.getType() == OperationType.ConditionalExit) {
         return registerDefinitions.copy();
      }

      return null;
   }

   //private Fu(FuCoor coor, List<Signal> inputs, String operationName, //Operation operation,
   private Fu(FuCoor coor, List<Signal> inputs, String operationName, OperationType operationType, //Operation operation,
           RegDefinitionsTable liveouts) {
      this.coor = coor;
      this.inputs = inputs;
      //this.internalRouting = internalRouting;
      //this.externalRouting = externalRouting;
      //this.operation = operation;
      this.operationName = operationName;
      this.operationType = operationType;
      this.liveouts = liveouts;
   }

   public static Fu buildFu(FuCoor coor, Operation operation,
           RegDefinitionsTable registerDefinitions) {
      // Get operation name
      String operationName = operation.getName();
      OperationType operationType = (OperationType) operation.getType();
      // Build inputs
      List<Signal> inputs = buildInputs(coor, operation, registerDefinitions);
      // Get Liveouts
      RegDefinitionsTable liveouts = buildLiveouts(operation, registerDefinitions);

      return new Fu(coor, inputs, operationName, operationType, liveouts);
   }

   public FuCoor getCoordinate() {
      return coor;
   }

   public List<Signal> getInputs() {
      return inputs;
   }

   /*
   public Map<String, FuInput> getExternalRouting() {
      return externalRouting;
   }

   public Map<FuOutput, FuInput> getInternalRouting() {
      return internalRouting;
   }
*/

   

   public RegDefinitionsTable getDefinitionsTable() {
      return liveouts;
   }

   public OperationType getOperationType() {
      return operationType;
   }

   
   public String getOperationName() {
      return operationName;
   }
    
    




   /**
    * INSTANCE VARIABLES
    */
   private FuCoor coor;
   //private Map<FuOutput, FuInput> internalRouting;
   //private Map<Integer, FuOutput> internalRouting;
   //private Map<String, FuInput> externalRouting;
   //private Map<Integer, String> externalRouting;
   private List<Signal> inputs;
   //private Operation operation;
   private String operationName;
   private OperationType operationType;
   //private Map<String, FuOutputSignal> liveouts;
   private RegDefinitionsTable liveouts;
   //private Area area;
}

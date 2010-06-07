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

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Joao Bispo
 */
public abstract class Operation {

   public Operation(int address) {
   //public Operation(OperationType type) {
      inputs = new ArrayList<Operand>();
      outputs = new ArrayList<Operand>();
      //parameters = new ArrayList<Operand>();
      this.address = address;
      //this.type = type;
   }

   public abstract Enum getType();
   public abstract String getName();
   public abstract Operation copy();
   //public abstract String getValue();

   /**
    *
    * @return true if the operation alters anything besides its list of outputs
    * (e.g., memory operations), false otherwise
    */
   public abstract boolean hasSideEffects();

   public List<Operand> getInputs() {
      return inputs;
   }

   /**
    * Adds the given operand as input of this object, and adds this operation
    * as a consumer of the operand.
    *
    * @param input
    */
   public void connectToInput(Operand input) {
      input.connectToConsumer(this);
   }

   /**
    * Replaces the given operand as input of this object, and adds this operation
    * as a consumer of the operand.
    *
    * @param input
    * @param position
    */
   public void replaceInput(int position, Operand input) {
      input.replaceInConsumer(position, this);
   }


   /**
    * Adds the given operand as input of this object, and adds this operation
    * as a consumer of the operand.
    *
    * @param input
    */
   /*
   public void connectToParameters(Operand param) {
      param.connectToParameter(this);
   }
    */

   /**
    * Adds the given operand as output of this object, and adds this operation
    * as a producer of the operand.
    *
    * @param input
    */
   public void connectToOutput(Operand output) {
      output.connectToProducer(this);
   }
   /*
   public void connectToOutput(Operand output, boolean showWarning) {
      output.connectToProducer(this, showWarning);
   }
    */

   /**
    * Replaces the given operand as output of this object, and adds this operation
    * as a producer of the operand.
    *
    * @param input
    * @param position
    */
   public void replaceOutput(int position, Operand output) {
      output.replaceInProducer(position, this);
   }

   public List<Operand> getOutputs() {
      return outputs;
   }

   /*
   public List<Operand> getParameters() {
      return parameters;
   }
    */

   public int getAddress() {
      return address;
   }



   public String getFullOperation() {
      StringBuilder builder = new StringBuilder();

      builder.append(address);
      builder.append(" ");
      builder.append(toString());
      for(Operand input : inputs) {
         builder.append(", ");
         builder.append(input);
      }
      builder.append("; ");

      if(outputs.size() > 0) {
         builder.append(outputs.get(0));
      }
      for(int i=1; i<outputs.size(); i++) {
         builder.append(", ");
         builder.append(outputs.get(i));
      }

      return builder.toString();
   }


   /*
   public String getType() {
      return type;
   }
    */
/*
   @Override
   public String toString() {
      StringBuilder builder = new StringBuilder();

      //builder.append(getType());
      //builder.append("-");
      builder.append(getValue());

      if(inputs.size() > 0) {
         builder.append(" in:");
         builder.append(inputs);
      }

      if(outputs.size() > 0) {
         builder.append(" out:");
         builder.append(outputs);
      }

      if(parameters.size() > 0) {
         builder.append(" param:");
         builder.append(parameters);
      }
      
      return builder.toString();
   }
*/


   /**
    * INSTANCE VARIABLES
    */
   private List<Operand> inputs;
   private List<Operand> outputs;
   //private List<Operand> parameters;
   private int address;
   //private OperationType type;
   //private String type;
}

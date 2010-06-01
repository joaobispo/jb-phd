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
import java.util.logging.Logger;

/**
 *
 * @author Joao Bispo
 */
public abstract class Operand {

   public Operand() {
      producer = null;
      consumers = new ArrayList<Operation>();
      prefix = "";
   }


   /*
   public Operand(OperandType type, String value) {
      producer = null;
      consumers = new ArrayList<Operation>();
      this.type = type;
      this.value = value;
   }
    */

   /*
   public abstract String getValue();
    */

   public abstract Enum getType();

   public abstract int getBits();

   public abstract String getName();

   /**
    *
    * @return true if this object represents an operand which cannot change its
    * contents. false otherwise.
    */
   public abstract boolean isImmutable();

   /**
    * Copies the operand, except for the producers and consumers.
    * 
    * @return
    */
   public abstract Operand copy();

   /**
    * Adds the given operation as producer of this object, and adds this operand
    * as an output of the operation.
    * 
    * @param producer
    */
   public void connectToProducer(Operation producer) {
   /*
      connectToProducer(producer, true);
   }
    
   public void connectToProducer(Operation producer, boolean showWarning) {
    */
      //if(this.producer != null && showWarning) {
      setProducer(producer);
      producer.getOutputs().add(this);
   }


   public void replaceInProducer(int position, Operation producer) {
      setProducer(producer);
      producer.getOutputs().set(position, this);
   }

   public Operation getProducer() {
      return producer;
   }

   public List<Operation> getConsumers() {
      return consumers;
   }

   /**
    * Adds the given operation as consumer of this object, and adds this operand
    * as an input of the operation.
    *
    * @param consumer
    */
   public void connectToConsumer(Operation consumer) {
      this.consumers.add(consumer);
      consumer.getInputs().add(this);
   }

   public void replaceInConsumer(int position, Operation consumer) {
      this.consumers.add(consumer);
      consumer.getInputs().set(position, this);
   }

   private void setProducer(Operation producer) {
      if (this.producer != null) {
         Logger.getLogger(Operand.class.getName()).
                 info("Replacing producer '" + this.producer + "' in operand '" + this + "' " +
                 "for producer '" + producer + "'.");
      }

      this.producer = producer;
   }

   public void setPrefix(String prefix) {
      this.prefix = prefix;
   }

   public String getPrefix() {
      return prefix;
   }

   

   /**
    * Adds this object as a parameter of the given parameter.
    *
    * <p>Parameters can only be of type Literal.
    *  
    * @param consumer
    */
   /*
   public void connectToParameter(Operation operation) {
      // Check that it is a literal
      if(getType() != OperandType.literal) {
         Logger.getLogger(Operand.class.getName()).
                 warning("Attempted to add a parameter which is not a literal: '"+this.toString()+"'");
         return;
      }

      this.consumers.add(operation);
      operation.getParameters().add(this);
   }
    */

   /*
   public String getValue() {
      return value;
   }
    */


   /*
   @Override
   public String toString() {
      return getType()+"."+getValue();
   }
    */

   /*
   @Override
   public String toString() {
      StringBuilder builder = new StringBuilder();

      if(producer != null) {
         builder.append(producer.getType());
      } else {
         builder.append("null");
      }
      builder.append(" -> (");
      builder.append(toString());
      builder.append(")");

      for(Operation op : consumers) {
         builder.append(",");
         builder.append(op.getType());
      }
      builder.append("\n");


      return builder.toString();
   }
    */



   /**
    * INSTANCE VARIABLES
    */
   private Operation producer;
   private List<Operation> consumers;

   protected String prefix;




   //private OperandType type;
   //private String value;
}

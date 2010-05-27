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

import java.io.File;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.ancora.SharedLibrary.IoUtils;
import org.ancora.SharedLibrary.LineReader;
import org.ancora.SharedLibrary.ParseUtils;

/**
 * Methods for saving and retriving InstructionBlock from/to files.
 *
 * @author Joao Bispo
 */
public class BlockIO {

   public static void toFile(File file, InstructionBlock iblock) {
      StringBuilder builder = new StringBuilder();

// Add parameters to file
      Map<Property, String> parameters = buildPropertiesTable(iblock);
      for (Property field : Property.values()) {
         builder.append(field.name());
         builder.append(PARAMETER_SEPARATOR);
         builder.append(parameters.get(field));
         builder.append(NEWLINE);
      }

      // Add instructions
      for (GenericInstruction instruction : iblock.getInstructions()) {
         builder.append(instruction.toLine());
         builder.append(NEWLINE);
      }

      IoUtils.write(file, builder.toString());
   }

   public static InstructionBlock fromFile(File file) {
      LineReader reader = LineReader.createLineReader(file);

      // 1. Get parameters
      Map<Property, String> parameters = getParameters(reader);
      if(parameters == null) {
         Logger.getLogger(BlockIO.class.getName()).
                    warning("Could not load file '"+file+"'.");
         return null;
      }

      int id = ParseUtils.parseInt(parameters.get(Property.id));
      int repetitions = ParseUtils.parseInt(parameters.get(Property.repetitions));
      long totalInstructions = ParseUtils.parseLong(parameters.get(Property.totalinstructions));


      // 2. Get Instructions
      List<GenericInstruction> instructions = getInstructions(reader);

      // Build Instruction Block
      return new InstructionBlock(instructions, repetitions, id, totalInstructions);
   }

    /**
    *
    * @param reader
    * @return A Map with the parameters of the Instruction Block.
    * Null if the Map could not be built.
    */
  private static Map<Property, String> getParameters(LineReader reader) {
     Map<Property, String> parameters = new EnumMap<Property, String>(Property.class);

     for(int i=0; i<Property.values().length; i++) {
         String line = reader.nextLine();
         String[] keyValue = line.split(PARAMETER_SEPARATOR);
         // Get Key
         try {
            Property field = Property.valueOf(keyValue[0]);
            parameters.put(field, keyValue[1]);
         } catch (IllegalArgumentException ex) {
            Logger.getLogger(BlockIO.class.getName()).
                    warning("Could not decode block parameter:'"+keyValue[0]+"'.");
            return null;
         }

      }

     return parameters;
   }

   private static List<GenericInstruction> getInstructions(LineReader reader) {
      List<GenericInstruction> instructions = new ArrayList<GenericInstruction>();

      String line = reader.nextLine();

      while(line != null) {
         GenericInstruction newInstruction = GenericInstruction.fromLine(line);
         instructions.add(newInstruction);
         line = reader.nextLine();
      }

      return instructions;
   }

   private static Map<Property, String> buildPropertiesTable(InstructionBlock instructionBlock) {
      Map<Property, String> newTable = new EnumMap<Property, String>(Property.class);

      newTable.put(Property.id, String.valueOf(instructionBlock.getId()));
      newTable.put(Property.repetitions, String.valueOf(instructionBlock.getRepetitions()));
      newTable.put(Property.totalinstructions, String.valueOf(instructionBlock.getTotalInstructions()));

      return newTable;
   }

   /**
    * INNER CLASS
    */
   static public enum Property {
      id,
      repetitions,
      totalinstructions;
   }

   /**
    * DEFINITIONS
    */
   public final static String PARAMETER_SEPARATOR = "=";
   public final static String NEWLINE = "\n";
}

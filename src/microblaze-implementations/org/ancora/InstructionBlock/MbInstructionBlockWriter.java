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
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import org.ancora.MicroBlaze.InstructionName;
import org.ancora.SharedLibrary.IoUtils;
import org.ancora.SharedLibrary.ParseUtils;
import org.ancora.SharedLibrary.LineReader;

/**
 *
 * @author Joao Bispo
 */
public class MbInstructionBlockWriter implements InstructionBlockListener {




   //public MbInstructionBlockWriter(String traceFilename) {
   public MbInstructionBlockWriter(String baseFilename) {
      counter = 0;
      //int lastIndexOfSeparator = traceFilename.lastIndexOf(EXTENSION_SEPARATOR);
      //baseFilename = traceFilename.substring(0, lastIndexOfSeparator);
      this.baseFilename = baseFilename;
      ids = new HashSet<Integer>();
   }



   public void accept(InstructionBlock instructionBlock) {
      // Check if block was already written
      if(ids.contains(instructionBlock.getId())) {
         return;
      }
      
      ids.add(instructionBlock.getId());
      
      // Create filename
      File file = createFile();

      saveInstructionBlock(instructionBlock, file);
   }

   public void flush() {
      // Do Nothing
   }

  private File createFile() {
     String foldername = FOLDER_START + BLOCK_FOLDER + FOLDER_SEPARATOR + baseFilename + FOLDER_SEPARATOR;
     File folder = IoUtils.safeFolder(foldername);

     String filename = baseFilename+"-"+counter+IoUtils.DEFAULT_EXTENSION_SEPARATOR+BLOCK_EXTENSION;
     File file = new File(folder, filename);

     
     counter++;
     return file;
   }

   public static boolean saveInstructionBlock(InstructionBlock block, File destination) {
      // Generate file contents
      String fileContents = buildFile(block);

      // Save file
      return IoUtils.write(destination, fileContents);
   }

   private static String buildFile(InstructionBlock instructionBlock) {
      StringBuilder builder = new StringBuilder();

      // Add parameters to file
      Map<Field, String> parameters = buildPropertiesTable(instructionBlock);
      for(Field field : Field.values()) {
         builder.append(field.name());
         builder.append(PARAMETER_SEPARATOR);
         builder.append(parameters.get(field));
         builder.append(NEWLINE);
      }

      // Add instructions
      for(GenericInstruction instruction : instructionBlock.getInstructions()) {
         builder.append(instruction.getAddress());
         builder.append(INSTRUCTION_SEPARATOR);
         builder.append(instruction.getInstruction());
         builder.append(NEWLINE);
      }

      return builder.toString();
   }

   private static Map<Field, String> buildPropertiesTable(InstructionBlock instructionBlock) {
      Map<Field, String> newTable = new Hashtable<Field, String>();

      newTable.put(Field.id, String.valueOf(instructionBlock.getId()));
      newTable.put(Field.repetitions, String.valueOf(instructionBlock.getRepetitions()));

      return newTable;
   }

   public static InstructionBlock loadInstructionBlock(File source) {
      LineReader reader = LineReader.createLineReader(source);

      // 1. Get parameters
      Map<Field, String> parameters = getParameters(reader);
      if(parameters == null) {
         Logger.getLogger(MbInstructionBlockWriter.class.getName()).
                    warning("Could not load file '"+source+"'.");
         return null;
      }
      
      int id = ParseUtils.parseInt(parameters.get(Field.id));
      int repetitions = ParseUtils.parseInt(parameters.get(Field.repetitions));


      // 2. Get Instructions
      List<GenericInstruction> instructions = getInstructions(reader);

      // Build Instruction Block
      return new InstructionBlock(instructions, repetitions, id);
   }

   /**
    *
    * @param reader
    * @return A Map with the parameters of the Instruction Block.
    * Null if the Map could not be built.
    */
  private static Map<Field, String> getParameters(LineReader reader) {
     Map<Field, String> parameters = new Hashtable<Field, String>();

     for(int i=0; i<Field.values().length; i++) {
         String line = reader.nextLine();
         String[] keyValue = line.split(PARAMETER_SEPARATOR);
         // Get Key
         try {
            Field field = Field.valueOf(keyValue[0]);
            parameters.put(field, keyValue[1]);
         } catch (IllegalArgumentException ex) {
            Logger.getLogger(MbInstructionBlockWriter.class.getName()).
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
         //System.out.println("Line:"+line);
         //MbInstruction newInstruction = MbTraceReader.createMicroBlazeInstruction(line);
         MbInstruction newInstruction = createMicroBlazeInstruction(line);
         instructions.add(newInstruction);
         //System.out.println("Parsed Address:"+newInstruction.getAddress());
         line = reader.nextLine();
      }

      return instructions;
   }

   /**
    * Block format is diferent from MicroBlaze trace format. The address of the
    * instructions in the former is a decimal value, while in the later is a
    * hexadecimal value prefixed by "0x".
    * @param blockLine
    * @return
    */
   public static MbInstruction createMicroBlazeInstruction(String blockLine) {
      /// Split the trace instruction in parts
      int whiteSpaceIndex = ParseUtils.indexOfFirstWhiteSpace(blockLine);

      /// Get Address
      String addressString = blockLine.substring(0, whiteSpaceIndex);

      // Parse to integer
      int instructionAddress = Integer.valueOf(addressString);

      /// Get Instruction
      String instruction = blockLine.substring(whiteSpaceIndex).trim();

      /// Get InstructionName
      whiteSpaceIndex = ParseUtils.indexOfFirstWhiteSpace(instruction);
      String instNameString = instruction.substring(0, whiteSpaceIndex);
      InstructionName instName = InstructionName.getEnum(instNameString);

      return new MbInstruction(instructionAddress, instruction, instName);
   }

   private int counter;
   private String baseFilename;
   private Set<Integer> ids;

   public final static String BLOCK_FOLDER = "blocks";
   public final static String FOLDER_SEPARATOR = "/";
   public final static String FOLDER_START = "./";
   public final static String BLOCK_EXTENSION = "block";
   public final static String PARAMETER_SEPARATOR = "=";
   public final static String INSTRUCTION_SEPARATOR = " ";
   public final static String NEWLINE = "\n";





   public enum Field {
      id,
      repetitions;
   }

}

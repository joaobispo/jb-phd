/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.ancora.InstructionBlock;

import java.io.File;
import java.util.logging.Logger;
import org.ancora.DTool.TraceLine;
import org.ancora.DTool.TraceReader;
import org.ancora.MicroBlaze.InstructionName;
import org.ancora.SharedLibrary.ParseUtils;

/**
 * Reads MicroBlaze traces as if they where executing in an instruction bus.
 *
 * @author Joao Bispo
 */
public class MbTraceReader implements InstructionBusReader {

   /**
    * Private constructor for static creator method.
    *
    * @param reader
    */
    private MbTraceReader(TraceReader reader) {
       this.reader = reader;
    }

   /**
    * Builds a TraceReader from the given file. If the object could
    * not be created, returns null.
    *
    * <p>Creating a TraceReader involves File operations which can lead
    * to failure in creation of the object. That is why we use a public
    * static method instead of a constructor.
    *
    * @param traceFile a file representing a MicroBlaze Trace, as in the format
    * of the tool of the Ancora Group (not avaliable yet).
    * @return a TraceReader If the object could not be created, returns null.
    */
   public static MbTraceReader createTraceReader(File traceFile) {

      TraceReader reader = TraceReader.createTraceReader(traceFile);
      if(reader == null) {
         Logger.getLogger(MbTraceReader.class.getName()).
                    warning("Could not create MbTraceReader.");
         return null;
      }

      // Extract information about number of instructions and cycles
      return new MbTraceReader(reader);
   }


    /**
     * @return the next line in the file which qualifies as an instruction, or
     * null if the end of the stream has been reached.
     * A line is considered as a trace instruction if it starts with "0x".
     */
   public GenericInstruction nextInstruction() {

      // While there are lines and a trace instruction was not found, loop.
      TraceLine line = null;
      
      while (true) {
         line = reader.nextLine();

         if(line == null) {
            return null;
         }

         // Extract instruction name from instruction
         InstructionName name = getInstructionName(line.getInstruction());

         // Transform TraceLine into MbInstruction
         return new MbInstruction(line.getAddress(), line.getInstruction(), name);
      }
   }

   public long getCycles() {
      return reader.getCycles();
   }

   public long getInstructions() {
      return reader.getNumberInstructions();
   }

   private InstructionName getInstructionName(String instruction) {
      int whiteSpaceIndex = ParseUtils.indexOfFirstWhiteSpace(instruction);
      String instNameString = instruction.substring(0, whiteSpaceIndex);
      return InstructionName.getEnum(instNameString);
   }

   /**
    * INSTANCE VARIABLES
    */
    private final TraceReader reader;


}

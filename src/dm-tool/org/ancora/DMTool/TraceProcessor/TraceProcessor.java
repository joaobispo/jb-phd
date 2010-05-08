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

package org.ancora.DMTool.TraceProcessor;

import java.io.File;
import java.util.List;
import org.ancora.DMTool.Shell.System.GeneralPreferences;
import org.ancora.DMTool.Configuration.PartitionerType;
import org.ancora.DMTool.TransformStudy.NamedBlock;
import org.ancora.DMTool.Utils.EnumUtils;
import org.ancora.InstructionBlock.InstructionBlock;
import org.ancora.InstructionBlock.InstructionBusReader;
import org.ancora.SharedLibrary.ParseUtils;
import org.ancora.SharedLibrary.Preferences.EnumPreferences;

/**
 * TODO: Delete this class
 * @author Joao Bispo
 */
public class TraceProcessor {



   public TraceProcessor() {
      //worker = new TraceProcessorWorker();
   }

   /*
   public void setGatherer(boolean value) {
      worker.useGatherer = value;
   }

   public void setSelector(boolean value) {
      worker.useSelector = value;
   }

   public void setWriter(boolean value) {
      worker.useWriter = value;
   }

   public void setSelectorRepetitionThreshold(int value) {
      worked
   }
   */
/*
   public static void processReader(InstructionBusReader busReader, String baseFilename, List<NamedBlock> blocks) {
      // Setup TraceProcessorWorker according to Preferences
      TraceProcessorWorker worker = new TraceProcessorWorker();

      EnumPreferences prefs = GeneralPreferences.getPreferences();

      worker.useGatherer = Boolean.parseBoolean(prefs.getPreference(GeneralPreferences.useGatherer));
      worker.useSelector = Boolean.parseBoolean(prefs.getPreference(GeneralPreferences.useSelector));
      worker.useWriter = Boolean.parseBoolean(prefs.getPreference(GeneralPreferences.useBlockWriter));

      worker.selectorRepThreshold = ParseUtils.parseInt(prefs.getPreference(GeneralPreferences.selectorThreshold));
      worker.maxSuperBlockPatternSize = ParseUtils.parseInt(prefs.getPreference(GeneralPreferences.megablockMaxPatternSize));
      
      String partitioner = prefs.getPreference(GeneralPreferences.partitioner);
      selectPartitioner(partitioner, worker);
      
      // Add blocks
      blocks.addAll(worker.processTrace(baseFilename, busReader));
   }
*/

   /*
   private static void selectPartitioner(String partitioner, TraceProcessorWorker worker) {
      PartitionerType part = EnumUtils.valueOf(PartitionerType.class, partitioner);

      if(part == null) {
         System.out.println("TraceProcessor:Could not select partitioner '"+partitioner+"'");
      }

      switch(part) {
         case BasicBlock:
            worker.partitionerType = TraceProcessorWorker.PartitionerType.BasicBlock;
            break;
         case SuperBlock:
            worker.partitionerType = TraceProcessorWorker.PartitionerType.SuperBlock;
            break;
         case MegaBlock:
            worker.partitionerType = TraceProcessorWorker.PartitionerType.MegaBlock;
            break;
         default:
            System.out.println("TraceProcessor: type not defined '"+part+"'");
      }
   }
*/
   /*
   public List<InstructionBlock> processTrace(File trace) {
      System.out.println("Processing " + trace.getName() + "...");
      return worker.processTrace(trace);
   }
    */
/*
   public void selectPartBasicBlock() {
      worker.partitionerType = TraceProcessorWorker.PartitionerType.BasicBlock;
   }

   public void selectPartSuperBlock() {
      worker.partitionerType = TraceProcessorWorker.PartitionerType.SuperBlock;
   }

   public void selectPartMegaBlock(int maxSuperBlockPatternSize) {
      worker.partitionerType = TraceProcessorWorker.PartitionerType.MegaBlock;
      worker.maxSuperBlockPatternSize = maxSuperBlockPatternSize;
   }

   public void useGatherer() {
      worker.useGatherer = true;
   }

   public void useSelector(int repetitionsThreshold) {
      worker.useSelector = true;
      worker.selectorRepThreshold = repetitionsThreshold;
   }

   public void useFileWriter(){
      worker.useWriter = false;
   }
 */

   /**
    * INSTANCE VARIABLES
    */
   TraceProcessorWorker worker;
}

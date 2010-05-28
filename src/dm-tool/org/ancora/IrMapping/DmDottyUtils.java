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

package org.ancora.IrMapping;

import java.io.File;
import java.util.List;
import org.ancora.DMTool.Settings.Options;
import org.ancora.DMTool.Settings.Options.OptionName;
import org.ancora.IntermediateRepresentation.Operation;
import org.ancora.IrMapping.Tools.Dotty;
import org.ancora.SharedLibrary.IoUtils;

/**
 * Methods related to writing dot files by the DM program.
 *
 * @author Joao Bispo
 */
public class DmDottyUtils {

   public static File getDottyFile(String baseName, String specificName) {
      String extension = IoUtils.DEFAULT_EXTENSION_SEPARATOR
              + Options.optionsTable.get(OptionName.extension_dot);
      String outputFoldername = Options.optionsTable.get(OptionName.general_outputfolder)
              + "/dot/" + baseName;
      String outputFilename = specificName + extension;

      File folder = IoUtils.safeFolder(outputFoldername);
      File dotFile = new File(folder, outputFilename);

      return dotFile;
   }

   public static boolean writeBlockDot(List<Operation> operations, File dotFile) {
      // Processing on list ended. Removed nops before printing
      List<Operation> ops = Dotty.removeNops(operations);

      // Connect
      ops = Dotty.connectOperations(operations);

      return IoUtils.write(dotFile, Dotty.generateDot(ops));
   }
}

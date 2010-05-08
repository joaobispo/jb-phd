/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.ancora.DMTool.Utils;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Accepts files with a certain extension. The default extension separator is '.'
 *
 * @author Joao Bispo
 */
public class ExtensionFilter implements FilenameFilter {

   public ExtensionFilter(String extension) {
      this.extension = extension;
      this.separator = DEFAULT_EXTENSION_SEPARATOR;
   }

   public boolean accept(File dir, String name) {
      String suffix = separator+extension;
      return name.endsWith(suffix);
   }

   private String extension;
   private String separator;

   public static final String DEFAULT_EXTENSION_SEPARATOR = ".";
}

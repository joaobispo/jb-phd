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

package org.ancora.DMTool.Utils;

/**
 *
 * @author Joao Bispo
 */
public class EnumUtils {

   /**
    *
    * @param <T>
    * @param enumType
    * @param name
    * @return the enum with the same name, or null if not found.
    */
   public static <T extends Enum<T>> T valueOf(Class<T> enumType, String name) {
      try{
         return Enum.valueOf(enumType, name);
      } catch (IllegalArgumentException ex) {
         return null;
      } catch (NullPointerException ex) {
         return null;
      }
   }

   public static <T extends Enum<T>> boolean containsEnum(Class<T> enumType, String name) {
      Enum enumeration = valueOf(enumType, name);
      if (enumeration == null) {
         return false;
      } else {
         return true;
      }
   }

   /*
   public static <T extends Enum<T>> boolean containsEnum(Class<T> enumType, Enum name) {
      Enum enumeration = valueOf(enumType, name);
      if (enumeration == null) {
         return false;
      } else {
         return true;
      }
   }
    */


   /*
   public static Enum valueOf(Class<T> enumType, String name) {
      return Enum.valueOf(enumType, name);
   }
    */
}

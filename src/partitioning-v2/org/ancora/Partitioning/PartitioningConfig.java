/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.ancora.Partitioning;

/**
 *
 * @author Joao Bispo
 */
public class PartitioningConfig {

   public PartitioningConfig(boolean useGatherer, boolean useSelector, boolean useUniqueFilter, int selectorRepThreshold) {
      this.useGatherer = useGatherer;
      this.useSelector = useSelector;
      this.useUniqueFilter = useUniqueFilter;
      this.selectorRepThreshold = selectorRepThreshold;
   }



   public int getSelectorRepThreshold() {
      return selectorRepThreshold;
   }

   public boolean useGatherer() {
      return useGatherer;
   }

   public boolean useSelector() {
      return useSelector;
   }

   public boolean useUniqueFilter() {
      return useUniqueFilter;
   }

   
/*
    public void setUseGatherer(boolean useGatherer) {
      this.useGatherer = useGatherer;
   }

   public void setUseUniqueFilter(boolean useUniqueFilter) {
      this.useUniqueFilter = useUniqueFilter;
   }

   public void setUseSelector(boolean useSelector) {
      this.useSelector = useSelector;
   }

   public void setSelectorRepThreshold(int selectorRepThreshold) {
      this.selectorRepThreshold = selectorRepThreshold;
   }
*/
   private boolean useGatherer;
   private boolean useSelector;
   private boolean useUniqueFilter;
   private int selectorRepThreshold;
}

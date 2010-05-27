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
package org.ancora.Partitioning.deprecated.Iterative;


import java.util.ArrayList;
import java.util.List;
import org.ancora.InstructionBlock.GenericInstruction;
import org.ancora.InstructionBlock.InstructionBlock;
import org.ancora.InstructionBlock.InstructionBlockListener;
import org.ancora.Partitioning.Tools.PatternFinder;
import org.ancora.Partitioning.Tools.PatternFinderInfo;
import org.ancora.SharedLibrary.BitUtils;

/**
 *
 * @author Joao Bispo
 */
public class MegaBlock extends Partitioner {

   public MegaBlock(SuperBlock superBlock, int maxPatternSize) {
      this.sbPartitioner = superBlock;
      //this.mbBuilder = new MegaBlockBuilder(maxPatternSize);
         initCurrentMegaBlock();
         initPreviousMegaBlock();

         // Init object state
         lastPatternSize = 0;
         state = BuilderState.LOOKING_FOR_PATTERN;
         patternFinder = new PatternFinder(maxPatternSize);
//      this.sbPartitioner.addListener(mbBuilder);
   }

   @Override
   public String getName() {
      return NAME;
   }

   @Override
   public InstructionBlock acceptInstruction(GenericInstruction instruction) {
      // Give it to SuperBlock
      InstructionBlock instructionBlock = sbPartitioner.acceptInstruction(instruction);
      if (instructionBlock == null) {
         return null;
      }

      // Give instructions to pattern finder
      patternFinder.accept(instructionBlock);
      PatternFinderInfo patternInfo = patternFinder.getInfo();


 //   return parseInstructionBlock(instructionBlock, patternInfo);
      //TODO: THIS IS WRONG
      return null;
   }

   @Override
   public InstructionBlock lastInstruction() {
      return flushCurrentMegaBlock();
   }
   /**
    * INSTANCE VARIABLES
    */
   private SuperBlock sbPartitioner;
   //private MegaBlockBuilder mbBuilder;
   public static final String NAME = "MegaBlock";



   //class MegaBlockBuilder implements InstructionBlockListener {
/*
      public MegaBlockBuilder(int maxPatternSize) {
         initCurrentMegaBlock();
         initPreviousMegaBlock();

         // Init object state
         lastPatternSize = 0;
         state = BuilderState.LOOKING_FOR_PATTERN;
         patternFinder = new PatternFinder(maxPatternSize);
      }
*/

      private void initCurrentMegaBlock() {
         currentBlock = new ArrayList<GenericInstruction>();
         currentLoopSize = 0;
         currentIds = new ArrayList<Integer>();
      }

      private void initPreviousMegaBlock() {
         //previousBlock = null;
         //loopIterations = 0;
         previousIds = null;
      }

      /*
      public void accept(InstructionBlock instructionBlock) {
         // Give instructions to pattern finder
         patternFinder.accept(instructionBlock);
         PatternFinderInfo patternInfo = patternFinder.getInfo();


         parseInstructionBlock(instructionBlock, patternInfo);
      }
       *
       */
/*
      public void flush() {
         flushCurrentMegaBlock();
         flushListeners();
      }
*/
      /*
      private InstructionBlock parseInstructionBlock(InstructionBlock instructionBlock, PatternFinderInfo patternInfo) {

         switch (state) {
            case LOOKING_FOR_PATTERN:
               return stateLookingForPattern(instructionBlock, patternInfo);
               //break;

            case BUILDING_PATTERN:
               return stateBuildingPattern(instructionBlock, patternInfo);
               //break;

            case CHECKING_PATTERN:
               return stateCheckingPattern(instructionBlock, patternInfo);

         }
      }

       */
/*
      private InstructionBlock stateLookingForPattern(InstructionBlock instructionBlock, PatternFinderInfo patternInfo) {
         // Check if there is a pattern
         if (patternInfo.getPaternSize() > 0) {
            // Flush current superblocks
            flushCurrentMegaBlock();

            // Prepare Data
            changeState(BuilderState.BUILDING_PATTERN);

            lastPatternSize = patternInfo.getPaternSize();

            // Start processing pattern
            parseInstructionBlock(instructionBlock, patternInfo);
            return;
         }

         addInstructionsToCurrentBlock(instructionBlock);
      }
*/
 /*
      private void stateBuildingPattern(InstructionBlock instructionBlock, PatternFinderInfo patternInfo) {
         // Check pattern size. If it has changed, we can interrupt the building
         // of the pattern.
         if (lastPatternSize != patternInfo.getPaternSize()) {
            flushCurrentMegaBlock();
            changeState(BuilderState.LOOKING_FOR_PATTERN);
            lastPatternSize = 0;

            parseInstructionBlock(instructionBlock, patternInfo);
            return;
         }

         // Just add SuperBlocks to current until it has size equal to the pattern.
         addInstructionsToCurrentBlock(instructionBlock);
         //currentSuperBlockLoop.addSuperBlock(superBlock);

         if (currentLoopSize == lastPatternSize) {

            //buildPatternLoop();
            storeCurrentMegaBlock();
            flushCurrentMegaBlock();
            //initCurrentLoop();
            // Send just found loop
            changeState(BuilderState.CHECKING_PATTERN);
         }
      }
  * 
  */
/*
      private void stateCheckingPattern(InstructionBlock instructionBlock, PatternFinderInfo patternInfo) {
         // First, check if incoming superblock is part of the found pattern
         int index = currentLoopSize;
         //int patternHash = patternSuperBlockLoop.getSuperBlocks().get(index).getHash();
         int patternHash = previousIds.get(index);

         // If there is a mismatch, reset pattern and go looking for new patterns.
         if (patternHash != instructionBlock.getId()) {
            initPreviousMegaBlock();
            //flushPatternSuperBlockLoop();

            changeState(BuilderState.LOOKING_FOR_PATTERN);
            lastPatternSize = 0;

            parseInstructionBlock(instructionBlock, patternInfo);
            return;
         }

         // There is no mismatch, add instructions to current.
         addInstructionsToCurrentBlock(instructionBlock);

         // If currentLoop is the same size as pattern, flush current MegaBlock.
         if (currentLoopSize == lastPatternSize) {
            flushCurrentMegaBlock();
            //loopIterations++;
            //initCurrentLoop();
            }
      }
*/
      /**
       * Sends the current MegaBlock to registred consumers and resets it.
       */
      private InstructionBlock flushCurrentMegaBlock() {
         if (currentLoopSize == 0) {
            return null;
         }

         InstructionBlock newBlock = sendBlock(currentBlock, currentIds);
         /*
         int blockId = calcId(currentIds);

         // Build InstructionBlock
         InstructionBlock newBlock = new InstructionBlock(currentBlock, 1, blockId);
         // Send it to listeners
         noticeListeners(newBlock);
          */

         initCurrentMegaBlock();

         return newBlock;
      }

      private InstructionBlock sendBlock(List<GenericInstruction> instructions, List<Integer> currentIds) {
         int blockId = calcId(currentIds);

         // Build InstructionBlock
         //InstructionBlock newBlock = new InstructionBlock(instructions, 1, blockId);
         return new InstructionBlock(instructions, 1, blockId, instructions.size());
         // Send it to listeners
         //noticeListeners(newBlock);
      }

      private void addInstructionsToCurrentBlock(InstructionBlock instructionBlock) {
         currentBlock.addAll(instructionBlock.getInstructions());
         currentLoopSize++;
         currentIds.add(instructionBlock.getId());
      }

      private void storeCurrentMegaBlock() {
         //previousBlock = currentBlock;
         //loopIterations = 1;
         previousIds = currentIds;
      }

      // }
      private int calcId(List<Integer> currentIds) {
         int hash = HASH_INITIAL_VALUE;

         for (Integer superBlockHash : currentIds) {
            hash = BitUtils.superFastHash(superBlockHash, hash);
         }

         return hash;
      }

      private void changeState(BuilderState builderState) {
         state = builderState;
      }
      /**
       * INSTANCE VARIABLES
       */
      // State for the MegaBlock being built
      private List<GenericInstruction> currentBlock;
      private int currentLoopSize;
      private List<Integer> currentIds;

      // State for the previous found MegaBlock

      //private List<GenericInstruction> previousBlock;
      // Stores the hashes of each SuperBlock in sequence
      private List<Integer> previousIds;
      

      // Object state for finding patterns
      private BuilderState state;
      private int lastPatternSize;
      private PatternFinder patternFinder;
      
      private static final int HASH_INITIAL_VALUE = 4;
   }

   enum BuilderState {

      LOOKING_FOR_PATTERN,
      BUILDING_PATTERN,
      CHECKING_PATTERN;
   }
//}

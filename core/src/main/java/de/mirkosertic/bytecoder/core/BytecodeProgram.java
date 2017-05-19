/*
 * Copyright 2017 Mirko Sertic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.mirkosertic.bytecoder.core;

import java.util.ArrayList;
import java.util.List;

public class BytecodeProgram {

    private final List<BytecodeInstruction> instructions;

    public BytecodeProgram() {
        instructions = new ArrayList<>();
    }

    public void addInstruction(BytecodeInstruction aInstruction) {
        instructions.add(aInstruction);
    }

    public List<BytecodeInstruction> getInstructions() {
        return instructions;
    }

    public BytecodeExceptionTableEntry[] getActiveExceptionHandlers(BytecodeOpcodeAddress aAddress, BytecodeExceptionTableEntry[] aExceptionHandlerEntries) {
        List<BytecodeExceptionTableEntry> theResult = new ArrayList<>();
        for (BytecodeExceptionTableEntry aEntry : aExceptionHandlerEntries) {
            if (!aEntry.isFinally()) {
                if (aEntry.getStartPC().isBefore(aAddress) && aEntry.getEndPc().isAfter(aAddress)) {
                    theResult.add(aEntry);
                }
            }
        }
        return theResult.toArray(new BytecodeExceptionTableEntry[theResult.size()]);
    }

    public int getNextInstructionAddress(BytecodeInstruction aInstruction) {
        int p = instructions.indexOf(aInstruction);
        if (p== instructions.size() -1) {
            return -1;
        }
        return instructions.get(p + 1).getOpcodeAddress().getAddress();
    }
}
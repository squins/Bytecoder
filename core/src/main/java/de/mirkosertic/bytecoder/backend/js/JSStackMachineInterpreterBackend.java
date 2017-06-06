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
package de.mirkosertic.bytecoder.backend.js;

import de.mirkosertic.bytecoder.annotations.EmulatedByRuntime;
import de.mirkosertic.bytecoder.annotations.Import;
import de.mirkosertic.bytecoder.classlib.ExceptionRethrower;
import de.mirkosertic.bytecoder.classlib.java.lang.TArray;
import de.mirkosertic.bytecoder.classlib.java.lang.TClass;
import de.mirkosertic.bytecoder.classlib.java.lang.TString;
import de.mirkosertic.bytecoder.core.BytecodeAnnotation;
import de.mirkosertic.bytecoder.core.BytecodeArrayTypeRef;
import de.mirkosertic.bytecoder.core.BytecodeBasicBlock;
import de.mirkosertic.bytecoder.core.BytecodeClassinfoConstant;
import de.mirkosertic.bytecoder.core.BytecodeCodeAttributeInfo;
import de.mirkosertic.bytecoder.core.BytecodeConstant;
import de.mirkosertic.bytecoder.core.BytecodeControlFlowGraph;
import de.mirkosertic.bytecoder.core.BytecodeDoubleConstant;
import de.mirkosertic.bytecoder.core.BytecodeExceptionTableEntry;
import de.mirkosertic.bytecoder.core.BytecodeFieldRefConstant;
import de.mirkosertic.bytecoder.core.BytecodeFloatConstant;
import de.mirkosertic.bytecoder.core.BytecodeInstruction;
import de.mirkosertic.bytecoder.core.BytecodeInstructionACONSTNULL;
import de.mirkosertic.bytecoder.core.BytecodeInstructionALOAD;
import de.mirkosertic.bytecoder.core.BytecodeInstructionANEWARRAY;
import de.mirkosertic.bytecoder.core.BytecodeInstructionObjectRETURN;
import de.mirkosertic.bytecoder.core.BytecodeInstructionARRAYLENGTH;
import de.mirkosertic.bytecoder.core.BytecodeInstructionASTORE;
import de.mirkosertic.bytecoder.core.BytecodeInstructionATHROW;
import de.mirkosertic.bytecoder.core.BytecodeInstructionBIPUSH;
import de.mirkosertic.bytecoder.core.BytecodeInstructionCHECKCAST;
import de.mirkosertic.bytecoder.core.BytecodeInstructionD2Generic;
import de.mirkosertic.bytecoder.core.BytecodeInstructionDCONST;
import de.mirkosertic.bytecoder.core.BytecodeInstructionDUP;
import de.mirkosertic.bytecoder.core.BytecodeInstructionDUPX1;
import de.mirkosertic.bytecoder.core.BytecodeInstructionF2Generic;
import de.mirkosertic.bytecoder.core.BytecodeInstructionFCONST;
import de.mirkosertic.bytecoder.core.BytecodeInstructionGETFIELD;
import de.mirkosertic.bytecoder.core.BytecodeInstructionGETSTATIC;
import de.mirkosertic.bytecoder.core.BytecodeInstructionGOTO;
import de.mirkosertic.bytecoder.core.BytecodeInstructionGenericADD;
import de.mirkosertic.bytecoder.core.BytecodeInstructionGenericAND;
import de.mirkosertic.bytecoder.core.BytecodeInstructionGenericArrayLOAD;
import de.mirkosertic.bytecoder.core.BytecodeInstructionGenericArraySTORE;
import de.mirkosertic.bytecoder.core.BytecodeInstructionGenericCMP;
import de.mirkosertic.bytecoder.core.BytecodeInstructionGenericDIV;
import de.mirkosertic.bytecoder.core.BytecodeInstructionGenericLDC;
import de.mirkosertic.bytecoder.core.BytecodeInstructionGenericLOAD;
import de.mirkosertic.bytecoder.core.BytecodeInstructionGenericMUL;
import de.mirkosertic.bytecoder.core.BytecodeInstructionGenericNEG;
import de.mirkosertic.bytecoder.core.BytecodeInstructionGenericOR;
import de.mirkosertic.bytecoder.core.BytecodeInstructionGenericREM;
import de.mirkosertic.bytecoder.core.BytecodeInstructionGenericRETURN;
import de.mirkosertic.bytecoder.core.BytecodeInstructionGenericSHL;
import de.mirkosertic.bytecoder.core.BytecodeInstructionGenericSHR;
import de.mirkosertic.bytecoder.core.BytecodeInstructionGenericSTORE;
import de.mirkosertic.bytecoder.core.BytecodeInstructionGenericSUB;
import de.mirkosertic.bytecoder.core.BytecodeInstructionGenericUSHR;
import de.mirkosertic.bytecoder.core.BytecodeInstructionGenericXOR;
import de.mirkosertic.bytecoder.core.BytecodeInstructionI2Generic;
import de.mirkosertic.bytecoder.core.BytecodeInstructionICONST;
import de.mirkosertic.bytecoder.core.BytecodeInstructionIFACMP;
import de.mirkosertic.bytecoder.core.BytecodeInstructionIFCOND;
import de.mirkosertic.bytecoder.core.BytecodeInstructionIFICMP;
import de.mirkosertic.bytecoder.core.BytecodeInstructionIFNONNULL;
import de.mirkosertic.bytecoder.core.BytecodeInstructionIFNULL;
import de.mirkosertic.bytecoder.core.BytecodeInstructionIINC;
import de.mirkosertic.bytecoder.core.BytecodeInstructionINSTANCEOF;
import de.mirkosertic.bytecoder.core.BytecodeInstructionINVOKEINTERFACE;
import de.mirkosertic.bytecoder.core.BytecodeInstructionINVOKESPECIAL;
import de.mirkosertic.bytecoder.core.BytecodeInstructionINVOKESTATIC;
import de.mirkosertic.bytecoder.core.BytecodeInstructionINVOKEVIRTUAL;
import de.mirkosertic.bytecoder.core.BytecodeInstructionL2Generic;
import de.mirkosertic.bytecoder.core.BytecodeInstructionLCMP;
import de.mirkosertic.bytecoder.core.BytecodeInstructionLCONST;
import de.mirkosertic.bytecoder.core.BytecodeInstructionLOOKUPSWITCH;
import de.mirkosertic.bytecoder.core.BytecodeInstructionNEW;
import de.mirkosertic.bytecoder.core.BytecodeInstructionNEWARRAY;
import de.mirkosertic.bytecoder.core.BytecodeInstructionNEWMULTIARRAY;
import de.mirkosertic.bytecoder.core.BytecodeInstructionNOP;
import de.mirkosertic.bytecoder.core.BytecodeInstructionObjectArrayLOAD;
import de.mirkosertic.bytecoder.core.BytecodeInstructionObjectArraySTORE;
import de.mirkosertic.bytecoder.core.BytecodeInstructionPOP;
import de.mirkosertic.bytecoder.core.BytecodeInstructionPUTFIELD;
import de.mirkosertic.bytecoder.core.BytecodeInstructionPUTSTATIC;
import de.mirkosertic.bytecoder.core.BytecodeInstructionRETURN;
import de.mirkosertic.bytecoder.core.BytecodeInstructionSIPUSH;
import de.mirkosertic.bytecoder.core.BytecodeInstructionTABLESWITCH;
import de.mirkosertic.bytecoder.core.BytecodeIntegerConstant;
import de.mirkosertic.bytecoder.core.BytecodeInterfaceRefConstant;
import de.mirkosertic.bytecoder.core.BytecodeLinkedClass;
import de.mirkosertic.bytecoder.core.BytecodeLinkerContext;
import de.mirkosertic.bytecoder.core.BytecodeLongConstant;
import de.mirkosertic.bytecoder.core.BytecodeMethodRefConstant;
import de.mirkosertic.bytecoder.core.BytecodeMethodSignature;
import de.mirkosertic.bytecoder.core.BytecodeNameAndTypeConstant;
import de.mirkosertic.bytecoder.core.BytecodeObjectTypeRef;
import de.mirkosertic.bytecoder.core.BytecodeOpcodeAddress;
import de.mirkosertic.bytecoder.core.BytecodePrimitiveTypeRef;
import de.mirkosertic.bytecoder.core.BytecodeProgram;
import de.mirkosertic.bytecoder.core.BytecodeStringConstant;
import de.mirkosertic.bytecoder.core.BytecodeTypeRef;
import de.mirkosertic.bytecoder.core.BytecodeUtf8Constant;
import de.mirkosertic.bytecoder.core.BytecodeVirtualMethodIdentifier;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicLong;

public class JSStackMachineInterpreterBackend extends AbstractJSBackend {

    public JSStackMachineInterpreterBackend() {
    }

    public String generateJumpCodeFor(BytecodeOpcodeAddress aTarget) {
        return "currentLabel = " + aTarget.getAddress()+";continue controlflowloop;";
    }

    @Override
    public String generateCodeFor(BytecodeLinkerContext aLinkerContext) {

        final AtomicLong theNumberOfVirtualCalls = new AtomicLong(0);
        final AtomicLong theNumberOfRealVirtualCalls = new AtomicLong(0);

        BytecodeLinkedClass theClassLinkedCass = aLinkerContext.linkClass(BytecodeObjectTypeRef.fromRuntimeClass(TClass.class));

        BytecodeLinkedClass theExceptionRethrower = aLinkerContext.linkClass(BytecodeObjectTypeRef.fromRuntimeClass(
                ExceptionRethrower.class));
        theExceptionRethrower.linkStaticMethod("registerExceptionOutcome", theRegisterExceptionOutcomeSignature);
        theExceptionRethrower.linkStaticMethod("getLastOutcomeOrNullAndReset", theGetLastExceptionOutcomeSignature);

        StringWriter theStrWriter = new StringWriter();
        final PrintWriter theWriter = new PrintWriter(theStrWriter);
        theWriter.println("'use strict';");

        theWriter.println("var bytecoder = {");

        theWriter.println();
        theWriter.println("     logDebug : function(aValue) { ");
        theWriter.println("         console.log(aValue);");
        theWriter.println("     }, ");

        theWriter.println();
        theWriter.println("     logByteArrayAsString : function(aArray) { ");
        theWriter.println("         var theResult = '';");
        theWriter.println("         for (var i=0;i<aArray.data.length;i++) {");
        theWriter.println("             theResult += String.fromCharCode(aArray.data[i]);");
        theWriter.println("         }");
        theWriter.println("         console.log(theResult);");
        theWriter.println("     }, ");
        theWriter.println();

        theWriter.println("     newString : function(aByteArray) { ");

        BytecodeObjectTypeRef theStringTypeRef = BytecodeObjectTypeRef.fromRuntimeClass(TString.class);
        BytecodeObjectTypeRef theArrayTypeRef = BytecodeObjectTypeRef.fromRuntimeClass(TArray.class);

        BytecodeMethodSignature theStringConstructorSignature = new BytecodeMethodSignature(BytecodePrimitiveTypeRef.VOID,
                new BytecodeTypeRef[]{new BytecodeArrayTypeRef(BytecodePrimitiveTypeRef.BYTE, 1)});

        // Construct a String
        theWriter.println("          var theNewString = " + JSWriterUtils.toClassName(theStringTypeRef) + ".emptyInstance();");
        theWriter.println("          var theBytes = " + JSWriterUtils.toClassName(theArrayTypeRef) + ".emptyInstance();");
        theWriter.println("          theBytes.data = aByteArray;");
        theWriter.println("          " + JSWriterUtils.toClassName(theStringTypeRef) + "." + JSWriterUtils.toMethodName("init", theStringConstructorSignature) + "(theNewString, theBytes);");
        theWriter.println("          return theNewString;");
        theWriter.println("     },");
        theWriter.println();
        theWriter.println("     newArray : function(aLength, aDefault) {");

        BytecodeObjectTypeRef theArrayType = BytecodeObjectTypeRef.fromRuntimeClass(TArray.class);
        theWriter.println("          var theInstance = " + JSWriterUtils.toClassName(theArrayType)+ ".emptyInstance();");
        theWriter.println("          theInstance.data = [];");
        theWriter.println("          theInstance.data.length = aLength;");
        theWriter.println("          for (var i=0;i<aLength;i++) {");
        theWriter.println("             theInstance.data[i] = aDefault;");
        theWriter.println("          }");
        theWriter.println("          return theInstance;");
        theWriter.println("     }");
        theWriter.println("}");
        theWriter.println();
        aLinkerContext.forEachClass(aEntry -> {

            if (aEntry.getValue().getBytecodeClass().getAccessFlags().isInterface()) {
                return;
            }

            final String theOverriddenParentClassName = getOverriddenParentClassFor(aEntry.getValue().getBytecodeClass());

            String theJSClassName = JSWriterUtils.toClassName(aEntry.getKey());
            theWriter.println("var " + theJSClassName + " = {");

            if (!aEntry.getValue().getBytecodeClass().getAccessFlags().isInterface()) {

                theWriter.println("    staticFields : {");

                theWriter.println("        name : '" + aEntry.getValue().getClassName().name() + "',");
                if (aEntry.getValue().hasClassInitializer()) {
                    theWriter.println("        classInitialized : false,");
                }
                aEntry.getValue().forEachStaticField(
                        aFieldEntry -> theWriter.println("        " + aFieldEntry.getKey() + " : null, // declared in " + aFieldEntry.getValue().getDeclaringType().name() ));
                theWriter.println("    },");
                theWriter.println();

                theWriter.println("    runtimeClass : {");
                theWriter.println("        jsType: function() {return " + theJSClassName + ";},");
                theWriter.println("        clazz: {");
                theWriter.println("            resolveVirtualMethod: function(aIdentifier) {");
                theWriter.println("                switch(aIdentifier) {");

                theClassLinkedCass.forEachVirtualMethod(
                        aClassMethod -> {
                            theWriter.println("                    case " + aClassMethod.getKey().getIdentifier() + ": // " + aClassMethod.getValue().getTargetMethod().getName().stringValue());
                            if ("getClass".equals(aClassMethod.getValue().getTargetMethod().getName().stringValue())) {
                                theWriter.println(
                                        "                        return " + JSWriterUtils.toClassName(theClassLinkedCass.getClassName()));
                            } else if ("toString".equals(aClassMethod.getValue().getTargetMethod().getName().stringValue())) {
                                    theWriter.println("                        throw 'Not implemented';");
                            } else if ("equals".equals(aClassMethod.getValue().getTargetMethod().getName().stringValue())) {
                                theWriter.println("                        throw 'Not implemented';");
                            } else if ("hashCode".equals(aClassMethod.getValue().getTargetMethod().getName().stringValue())) {
                                theWriter.println("                        throw 'Not implemented';");
                            } else if ("desiredAssertionStatus".equals(aClassMethod.getValue().getTargetMethod().getName().stringValue())) {
                                theWriter.println("                        return function(callsite) {return false};");
                            } else if ("getEnumConstants".equals(aClassMethod.getValue().getTargetMethod().getName().stringValue())) {
                                theWriter.println("                        return function(callsite) {return callsite.jsType().staticFields.$VALUES;};");
                            } else {
                                theWriter.println("                        throw {type: 'not implemented virtual name'} // " + aClassMethod.getValue().getTargetMethod().getName().stringValue());
                            }
                        });

                theWriter.println("                    default:");
                theWriter.println("                        throw {type: 'unknown virtual name'}");
                theWriter.println("                }");
                theWriter.println("            }");
                theWriter.println("        }");
                theWriter.println("    },");
                theWriter.println();

                theWriter.println("    resolveVirtualMethod : function(aIdentifier) {");
                theWriter.println("        switch(aIdentifier) {");
                aEntry.getValue().forEachVirtualMethod(aVirtualMethod -> {
                    BytecodeLinkedClass.LinkedMethod theLinkTarget = aVirtualMethod.getValue();
                    theWriter.println("            case " + aVirtualMethod.getKey().getIdentifier() + ":");
                    if (theLinkTarget.getTargetMethod() != BytecodeLinkedClass.GET_CLASS_PLACEHOLDER) {
                        theWriter.println(
                                "                return " + JSWriterUtils.toClassName(theLinkTarget.getDeclaringType()) + "." + JSWriterUtils.toMethodName(
                                        theLinkTarget.getTargetMethod().getName().stringValue(),
                                        theLinkTarget.getTargetMethod().getSignature()) + ";");
                    } else {
                        theWriter.println(
                                "                return function(callsite) {return " + theJSClassName + ".runtimeClass;};");
                    }
                });
                theWriter.println("            default:");
                theWriter.println("                throw {type: 'unknown virtual name'}");
                theWriter.println("        }");
                theWriter.println("    },");
                theWriter.println();

                theWriter.println("    classInitCheck : function() {");
                if (aEntry.getValue().hasClassInitializer()) {
                    theWriter.println("        if (" + theJSClassName + ".staticFields.classInitialized == false) {");
                    theWriter.println("            " + theJSClassName + ".staticFields.classInitialized = true;");
                    theWriter.println("            " + theJSClassName + ".VOIDclinit();");
                    theWriter.println("        }");
                }
                theWriter.println("    },");
                theWriter.println();


                theWriter.println("    emptyInstance : function() {");
                if (aEntry.getValue().hasClassInitializer()) {
                    theWriter.println("        " + theJSClassName + ".classInitCheck();");
                }
                theWriter.println("        return {data: {");
                aEntry.getValue().forEachMemberField(aField -> theWriter.println("            " + aField.getKey() + " : null, // declared in " + aField.getValue().getDeclaringType().name()));
                theWriter.println("        }, clazz: " + JSWriterUtils.toClassName(aEntry.getKey())+ "};");
                theWriter.println("    },");
                theWriter.println();

                theWriter.println("    thisIdentifier : function() {");
                theWriter.println("        return " + aEntry.getValue().getUniqueId());
                theWriter.println("    },");
                theWriter.println();

                theWriter.println("    instanceOfType : function(aType) {");
                theWriter.println("        switch(aType) {");

                for (BytecodeLinkedClass theType : aEntry.getValue().getImplementingTypes()) {
                    theWriter.println("            case " + theType.getUniqueId() +":");
                    theWriter.println("                return 1;");
                }

                theWriter.println("            default:");
                theWriter.println("                return 0;");
                theWriter.println("        }");
                theWriter.println("    },");
                theWriter.println();
            }

            aEntry.getValue().forEachMethod(aMethod -> {

                // Do not generate code for abstract methods
                if (aMethod.getAccessFlags().isAbstract()) {
                    return;
                }

                BytecodeMethodSignature theCurrentMethodSignature = aMethod.getSignature();
                BytecodeTypeRef[] theMethodArguments = theCurrentMethodSignature.getArguments();
                StringBuffer theArguments = new StringBuffer();
                if (!aMethod.getAccessFlags().isStatic()) {
                    theArguments.append("thisRef");
                }
                for (int i=1;i<=theMethodArguments.length;i++) {
                    if (theArguments.length() > 0) {
                        theArguments.append(",");
                    }
                    theArguments.append("p" + i);
                }

                if (aMethod.getAccessFlags().isNative()) {
                    if (aEntry.getValue().getBytecodeClass().getAnnotations().getAnnotationByType(EmulatedByRuntime.class.getName()) != null) {
                        return;
                    }
                    BytecodeAnnotation theImportAnnotation = aMethod.getAnnotations().getAnnotationByType(Import.class.getName());
                    if (theImportAnnotation == null) {
                        throw new IllegalStateException("No @Import annotation found. Potential linker error!");
                    }

                    JSModule theModule = modules.resolveModule(theImportAnnotation.getElementValueByName("module").stringValue());
                    JSFunction theFunction = theModule.resolveFunction(theImportAnnotation.getElementValueByName("name").stringValue());
                    theWriter.println();
                    theWriter.println("    " + JSWriterUtils.toMethodName(aMethod.getName().stringValue(), theCurrentMethodSignature) + " : function(" + theArguments.toString() + ") {");
                    theWriter.println("         " + theFunction.generateCode(theCurrentMethodSignature));
                    theWriter.println("    },");
                    return;
                }

                BytecodeCodeAttributeInfo theCode = aMethod.getCode(aEntry.getValue().getBytecodeClass());

                theWriter.println();
                theWriter.println("    " + JSWriterUtils.toMethodName(aMethod.getName().stringValue(), theCurrentMethodSignature) + " : function(" + theArguments.toString() + ") {");

                //theWriter.println("        console.log('" + theJSClassName + "." + aMethod.getName().stringValue() + "');");

                theWriter.println("        var frame = {");
                theWriter.println("            stack : [], // " + theCode.getMaxStack() + " max stack depth");

                Map<String, String> theLocalVariables = new TreeMap<>();
                int p = 1;
                if (!aMethod.getAccessFlags().isStatic()) {
                    theLocalVariables.put("local1", "thisRef");
                    p++;
                }

                for (int i=0;i<theMethodArguments.length;i++) {
                    BytecodeTypeRef theRef = theMethodArguments[i];
                    if (theRef == BytecodePrimitiveTypeRef.LONG || theRef == BytecodePrimitiveTypeRef.DOUBLE) {
                        theLocalVariables.put("local" + p, "p" + (i+1));
                        p++;
                        theLocalVariables.put("local" + p, "null");
                        p++;
                    } else {
                        theLocalVariables.put("local" + p, "p" + (i+1));
                        p++;
                    }
                }

                while(p<=theCode.getMaxLocals()) {
                    theLocalVariables.put("local" + p, "null");
                    p++;
                }

                for (Map.Entry<String, String> theEntry : theLocalVariables.entrySet()) {
                    theWriter.println("            " + theEntry.getKey() + " : "  + theEntry.getValue() + ",");
                }

                theWriter.println("        };");

                BytecodeProgram theProgram = theCode.getProgramm();
                BytecodeControlFlowGraph theFlowGraph = new BytecodeControlFlowGraph(theProgram);

                String theInset = "            ";
                theWriter.println("        // Begin name code");
                theWriter.println("        // # basic blocks in flow graph : " + theFlowGraph.getBlocks().size());
                theWriter.println("        var currentLabel = " + theFlowGraph.getBlocks().get(0).getStartAddress().getAddress() + ";");
                theWriter.println("        controlflowloop: while(true) switch(currentLabel) {");

                for (BytecodeBasicBlock theBlock : theFlowGraph.getBlocks()) {

                    theWriter.println("         case " + theBlock.getStartAddress().getAddress() + ": {");

                    for (BytecodeInstruction theInstruction : theBlock.getInstructions()) {
                        if (theInstruction instanceof BytecodeInstructionNOP) {
                            theWriter.println(theInset + "// noop");
                        } else if (theInstruction instanceof BytecodeInstructionRETURN) {
                            theWriter.println(theInset + "return;");
                        } else if (theInstruction instanceof BytecodeInstructionDUP) {
                            BytecodeInstructionDUP theDup = (BytecodeInstructionDUP) theInstruction;
                            theWriter.println(theInset + "frame.stack.push(frame.stack[frame.stack.length - 1]);");
                        } else if (theInstruction instanceof BytecodeInstructionIINC) {
                            BytecodeInstructionIINC theInc = (BytecodeInstructionIINC) theInstruction;
                            theWriter.println(theInset + "frame.local" + (theInc.getIndex() + 1) + " += " + theInc.getConstant() + ";");
                        } else if (theInstruction instanceof BytecodeInstructionDUPX1) {
                            BytecodeInstructionDUPX1 theDup = (BytecodeInstructionDUPX1) theInstruction;

                            theWriter.println(theInset + "var theValue1 = frame.stack.pop();");
                            theWriter.println(theInset + "var theValue2 = frame.stack.pop();");
                            theWriter.println(theInset + "frame.stack.push(theValue1);");
                            theWriter.println(theInset + "frame.stack.push(theValue2);");
                            theWriter.println(theInset + "frame.stack.push(theValue1);");
                        } else if (theInstruction instanceof BytecodeInstructionNEW) {
                            BytecodeInstructionNEW theNew = (BytecodeInstructionNEW) theInstruction;

                            BytecodeClassinfoConstant theConstant = theNew.getClassInfoForObjectToCreate();
                            theWriter.println(theInset + "frame.stack.push(" + JSWriterUtils.toClassName(theConstant)+ ".emptyInstance());");

                        } else if (theInstruction instanceof BytecodeInstructionNEWARRAY) {
                            BytecodeInstructionNEWARRAY theNew = (BytecodeInstructionNEWARRAY) theInstruction;
                            Object theDefaultValue = theNew.getPrimitiveType().defaultValue();
                            if (theDefaultValue == null) {
                                theWriter.println(theInset + "frame.stack.push(bytecoder.newArray(frame.stack.pop(), null));");
                            } else {
                                theWriter.println(theInset + "frame.stack.push(bytecoder.newArray(frame.stack.pop(), " + theDefaultValue + "));");
                            }
                        } else if (theInstruction instanceof BytecodeInstructionNEWMULTIARRAY) {
                            BytecodeInstructionNEWMULTIARRAY theNew = (BytecodeInstructionNEWMULTIARRAY) theInstruction;

                            BytecodeTypeRef theTypeRef = aLinkerContext.getSignatureParser().toFieldType(theNew.getTypeConstant().getConstant());
                            Object theDefaultValue = theTypeRef.defaultValue();

                            if (theNew.getDimensions() > 2) {
                                throw new IllegalStateException("Not supported multi array size : " + theNew.getDimensions());
                            }

                            BytecodeObjectTypeRef theConstant = theNew.getObjectType();

                            theWriter.println(theInset + "var theDimensions = [];");
                            for (int i=0;i<theNew.getDimensions();i++) {
                                theWriter.println(theInset + "theDimensions.push(frame.stack.pop());");
                            }
                            theWriter.println(theInset + "var theInstance = " + JSWriterUtils.toClassName(theConstant)+ ".emptyInstance();");
                            theWriter.println(theInset + "theInstance.data = new Array(theDimensions[0]);");

                            theWriter.println(theInset + "for (var filler=0;filler<theDimensions[0];filler++) {");
                            if (theDefaultValue == null) {
                                theWriter.println(theInset + "  theInstance.data[filler] = null;");
                            } else {
                                theWriter.println(theInset + "  theInstance.data[filler] = " + theDefaultValue.toString() + ";");
                            }
                            theWriter.println(theInset + "}");

                            theWriter.println(theInset + "for (var theLevel=1;theLevel<theDimensions.length;theLevel++) {");
                            theWriter.println(theInset + "  var theParent = theInstance;");
                            theWriter.println(theInset + "  var theNumberOfInstances = theDimensions[theLevel - 1];");
                            theWriter.println(theInset + "  var theRequestedDimension = theDimensions[i];");
                            theWriter.println(theInset + "  for (var j=0;j<theNumberOfInstances;j++) {");
                            theWriter.println(theInset + "      var theSubInstance = " + JSWriterUtils.toClassName(theConstant)+ ".emptyInstance();");
                            theWriter.println(theInset + "      theSubInstance.data = new Array(theRequestedDimension);");

                            theWriter.println(theInset + "      for (var filler=0;filler<theRequestedDimension;filler++) {");
                            if (theDefaultValue == null) {
                                theWriter.println(theInset + "          theSubInstance.data[filler] = null;");
                            } else {
                                theWriter.println(theInset + "          theSubInstance.data[filler] = " + theDefaultValue.toString() + ";");
                            }
                            theWriter.println(theInset + "      }");

                            theWriter.println(theInset + "      theParent.data[j] = theSubInstance;");
                            theWriter.println(theInset + "  }");
                            theWriter.println(theInset + "}");

                            theWriter.println(theInset + "frame.stack.push(theInstance);");

                        } else if (theInstruction instanceof BytecodeInstructionANEWARRAY) {
                            BytecodeInstructionANEWARRAY theNew = (BytecodeInstructionANEWARRAY) theInstruction;
                            Object theDefaultValue = theArrayType.defaultValue();

                            theWriter.println(theInset + "var theLength = frame.stack.pop()");
                            theWriter.println(theInset + "var theInstance = " + JSWriterUtils.toClassName(theArrayType)+ ".emptyInstance();");
                            theWriter.println(theInset + "theInstance.data = new Array(theLength);");
                            theWriter.println(theInset + "for (var i=0;i<theLength;i++) {");
                            if (theDefaultValue == null) {
                                theWriter.println(theInset + "  theInstance.data[i] = null;");
                            } else {
                                theWriter.println(theInset + "  theInstance.data[i] = " + theDefaultValue.toString() + ";");
                            }
                            theWriter.println(theInset + "}");
                            theWriter.println(theInset + "frame.stack.push(theInstance);");

                        } else if (theInstruction instanceof BytecodeInstructionINVOKESPECIAL) {
                            BytecodeInstructionINVOKESPECIAL theInvokeSpecial = (BytecodeInstructionINVOKESPECIAL) theInstruction;

                            BytecodeMethodRefConstant theConstant = theInvokeSpecial.getMethodReference();

                            BytecodeNameAndTypeConstant theMethodRef = theConstant.getNameAndTypeIndex().getNameAndType();
                            BytecodeMethodSignature theSig = theMethodRef.getDescriptorIndex().methodSignature();
                            BytecodeUtf8Constant theName = theMethodRef.getNameIndex().getName();
                            BytecodeTypeRef[] theInvokeArguments = theSig.getArguments();
                            for (int i=theInvokeArguments.length;i>0;i--) {
                                theWriter.println(theInset + "var arg"+i+" = frame.stack.pop();");
                            }
                            theWriter.println(theInset + "var callsite = frame.stack.pop();");
                            if (theSig.getReturnType().isVoid()) {
                                theWriter.print(theInset);
                            } else {
                                theWriter.print(theInset + "frame.stack.push(");
                            }

                            if (theOverriddenParentClassName != null) {
                                theWriter.print(theOverriddenParentClassName.replace(".","_") + "." + JSWriterUtils.toMethodName(theName.stringValue(), theSig) + "(callsite");
                            } else {
                                theWriter.print(JSWriterUtils.toClassName(theConstant.getClassIndex().getClassConstant()) + "." + JSWriterUtils.toMethodName(theName.stringValue(), theSig) + "(callsite");
                            }
                            for (int i=1;i<=theInvokeArguments.length;i++) {
                                theWriter.print(",");
                                theWriter.print("arg" + i);
                            }

                            if (theSig.getReturnType().isVoid()) {
                                theWriter.println(");");
                            } else {
                                theWriter.println("));");
                            }

                            theWriter.println(theInset + "var theLastException = " + JSWriterUtils.toClassName(theExceptionRethrower.getClassName()) + "." + JSWriterUtils.toMethodName("getLastOutcomeOrNullAndReset", theGetLastExceptionOutcomeSignature) + "();");
                            theWriter.println(theInset + "if (theLastException) {");

                            writeExceptionHandlerCode(aLinkerContext, theExceptionRethrower, theWriter, theProgram,
                                    theInset + "    ", theInvokeSpecial, "theLastException");

                            theWriter.println(theInset + "}");

                        } else if (theInstruction instanceof BytecodeInstructionINVOKEVIRTUAL) {
                            BytecodeInstructionINVOKEVIRTUAL theVirtualInvoke = (BytecodeInstructionINVOKEVIRTUAL) theInstruction;

                            BytecodeMethodRefConstant theMethodRefConstant = theVirtualInvoke.getMethodReference();

                            BytecodeClassinfoConstant theClassConstant = theMethodRefConstant.getClassIndex().getClassConstant();
                            BytecodeNameAndTypeConstant theMethodRef = theMethodRefConstant.getNameAndTypeIndex().getNameAndType();
                            BytecodeMethodSignature theSig = theMethodRef.getDescriptorIndex().methodSignature();
                            BytecodeUtf8Constant theName = theMethodRef.getNameIndex().getName();

                            BytecodeVirtualMethodIdentifier theIdentifier = aLinkerContext.getMethodCollection().toIdentifier(theName.stringValue(), theSig);
                            if (theIdentifier == null) {
                                throw new IllegalStateException("Keine gelinkte Methode gefunden für " + theName.stringValue() + " Sig = " + theSig + " in " + aEntry.getValue().getClassName().name());
                            }

                            BytecodeTypeRef[] theInvokeArguments = theSig.getArguments();
                            for (int i=theInvokeArguments.length;i>0;i--) {
                                theWriter.println(theInset + "var arg"+i+" = frame.stack.pop();");
                            }
                            theWriter.println(theInset + "var callsite = frame.stack.pop();");
                            if (theSig.getReturnType().isVoid()) {
                                theWriter.print(theInset);
                            } else {
                                theWriter.print(theInset + "frame.stack.push(");
                            }

                            theNumberOfVirtualCalls.incrementAndGet();
                            List<BytecodeLinkedClass> theLinkedClasses = aLinkerContext.getClassesImplementingVirtualMethod(theIdentifier);
                            if (theLinkedClasses.size() > 1 || BytecodeObjectTypeRef.fromUtf8Constant(theClassConstant.getConstant()).name().equals(TClass.class.getName())) {
                                theNumberOfRealVirtualCalls.incrementAndGet();
                                // More than one class implementing the method, we use a virtual call
                                theWriter.print("callsite.clazz.resolveVirtualMethod(" + theIdentifier.getIdentifier() + ")(callsite");
                            } else {
                                // Only one class, we use a direct call
                                theWriter.print(JSWriterUtils.toClassName(theLinkedClasses.get(0).getClassName()) + "." + JSWriterUtils.toMethodName(theName.stringValue(), theSig) +  "(callsite");
                            }

                            for (int i=1;i<=theInvokeArguments.length;i++) {
                                theWriter.print(",");
                                theWriter.print("arg" + i);
                            }
                            if (theSig.getReturnType().isVoid()) {
                                theWriter.println(");");
                            } else {
                                theWriter.println("));");
                            }

                            theWriter.println(theInset + "var theLastException = " + JSWriterUtils.toClassName(theExceptionRethrower.getClassName()) + "." + JSWriterUtils.toMethodName("getLastOutcomeOrNullAndReset", theGetLastExceptionOutcomeSignature) + "();");
                            theWriter.println(theInset + "if (theLastException) {");

                            writeExceptionHandlerCode(aLinkerContext, theExceptionRethrower, theWriter, theProgram,
                                    theInset + "    ", theVirtualInvoke, "theLastException");

                            theWriter.println(theInset + "}");

                        } else if (theInstruction instanceof BytecodeInstructionINVOKEINTERFACE) {
                            BytecodeInstructionINVOKEINTERFACE theInterfaceInvoke = (BytecodeInstructionINVOKEINTERFACE) theInstruction;

                            BytecodeInterfaceRefConstant theMethodRefConstant = theInterfaceInvoke.getMethodDescriptor();

                            BytecodeNameAndTypeConstant theMethodRef = theMethodRefConstant.getNameAndTypeIndex().getNameAndType();
                            BytecodeMethodSignature theSig = theMethodRef.getDescriptorIndex().methodSignature();
                            BytecodeUtf8Constant theName = theMethodRef.getNameIndex().getName();

                            BytecodeVirtualMethodIdentifier theIdentifier = aLinkerContext.getMethodCollection().toIdentifier(theName.stringValue(), theSig);
                            if (theIdentifier == null) {
                                throw new IllegalStateException("Keine gelinkte Methode gefunden für " + theName.stringValue() + " Sig = " + theSig + " in " + aEntry.getValue().getClassName().name());
                            }

                            BytecodeTypeRef[] theInvokeArguments = theSig.getArguments();
                            for (int i=theInvokeArguments.length;i>0;i--) {
                                theWriter.println(theInset + "var arg"+i+" = frame.stack.pop();");
                            }
                            theWriter.println(theInset + "var callsite = frame.stack.pop();");
                            if (theSig.getReturnType().isVoid()) {
                                theWriter.print(theInset);
                            } else {
                                theWriter.print(theInset + "frame.stack.push(");
                            }

                            theNumberOfVirtualCalls.incrementAndGet();
                            List<BytecodeLinkedClass> theLinkedClasses = aLinkerContext.getClassesImplementingVirtualMethod(theIdentifier);
                            if (theLinkedClasses.size() > 1 || BytecodeObjectTypeRef.fromUtf8Constant(theInterfaceInvoke.getMethodDescriptor().getClassIndex().getClassConstant().getConstant()).name().equals(TClass.class.getName())) {
                                theNumberOfRealVirtualCalls.incrementAndGet();
                                // More than one class implementing the method, we use a virtual call
                                theWriter.print("callsite.clazz.resolveVirtualMethod(" + theIdentifier.getIdentifier() + ")(callsite");
                            } else {
                                // Only one class, we use a direct call
                                theWriter.print(JSWriterUtils.toClassName(theLinkedClasses.get(0).getClassName()) + "." + JSWriterUtils.toMethodName(theName.stringValue(), theSig) +  "(callsite");
                            }

                            for (int i=1;i<=theInvokeArguments.length;i++) {
                                theWriter.print(",");
                                theWriter.print("arg" + i);
                            }
                            if (theSig.getReturnType().isVoid()) {
                                theWriter.println(");");
                            } else {
                                theWriter.println("));");
                            }

                            theWriter.println(theInset + "var theLastException = " + JSWriterUtils.toClassName(theExceptionRethrower.getClassName()) + "." + JSWriterUtils.toMethodName("getLastOutcomeOrNullAndReset", theGetLastExceptionOutcomeSignature) + "();");
                            theWriter.println(theInset + "if (theLastException) {");

                            writeExceptionHandlerCode(aLinkerContext, theExceptionRethrower, theWriter, theProgram,
                                    theInset + "    ", theInterfaceInvoke, "theLastException");

                            theWriter.println(theInset + "}");

                        }else if (theInstruction instanceof BytecodeInstructionINVOKESTATIC) {
                            BytecodeInstructionINVOKESTATIC theStaticInvoke = (BytecodeInstructionINVOKESTATIC) theInstruction;

                            BytecodeMethodRefConstant theMethodRefConstant = theStaticInvoke.getMethodReference();
                            BytecodeClassinfoConstant theClassConstant = theMethodRefConstant.getClassIndex().getClassConstant();
                            BytecodeNameAndTypeConstant theMethodRef = theMethodRefConstant.getNameAndTypeIndex().getNameAndType();
                            BytecodeMethodSignature theSig = theMethodRef.getDescriptorIndex().methodSignature();
                            BytecodeUtf8Constant theName = theMethodRef.getNameIndex().getName();
                            BytecodeTypeRef[] theInvokeArguments = theSig.getArguments();
                            for (int i=theInvokeArguments.length;i>0;i--) {
                                theWriter.println(theInset + "var arg"+i+" = frame.stack.pop();");
                            }
                            if (theSig.getReturnType().isVoid()) {
                                theWriter.print(theInset);
                            } else {
                                theWriter.print(theInset + "frame.stack.push(");
                            }
                            theWriter.print(JSWriterUtils.toClassName((theClassConstant)) + "." + JSWriterUtils.toMethodName(theName.stringValue(), theSig) +"(");
                            for (int i=1;i<=theInvokeArguments.length;i++) {
                                if (i>1) {
                                    theWriter.print(",");
                                }
                                theWriter.print("arg" + i);
                            }

                            if (theSig.getReturnType().isVoid()) {
                                theWriter.println(");");
                            } else {
                                theWriter.println("));");
                            }

                            theWriter.println(theInset + "var theLastException = " + JSWriterUtils.toClassName(theExceptionRethrower.getClassName()) + "." + JSWriterUtils.toMethodName("getLastOutcomeOrNullAndReset", theGetLastExceptionOutcomeSignature) + "();");
                            theWriter.println(theInset + "if (theLastException) {");

                            writeExceptionHandlerCode(aLinkerContext, theExceptionRethrower, theWriter, theProgram,
                                    theInset + "    ", theStaticInvoke, "theLastException");

                            theWriter.println(theInset + "}");

                        } else if (theInstruction instanceof BytecodeInstructionINSTANCEOF) {
                            BytecodeInstructionINSTANCEOF theInstanceOf = (BytecodeInstructionINSTANCEOF) theInstruction;

                            BytecodeLinkedClass theLinkedClass = aLinkerContext.isLinkedOrNull(theInstanceOf.getTypeRef().getConstant());

                            theWriter.println(theInset + "var temp = frame.stack.pop();");
                            theWriter.println(theInset + "if (temp == null) {");
                            theWriter.println(theInset + "  frame.stack.push(0);");
                            theWriter.println(theInset + "} else {");
                            theWriter.println(theInset + "  frame.stack.push(temp.clazz.instanceOfType(" + theLinkedClass.getUniqueId() + "));");
                            theWriter.println(theInset + "}");
                        } else if (theInstruction instanceof BytecodeInstructionPOP) {
                            BytecodeInstructionPOP thePop = (BytecodeInstructionPOP) theInstruction;
                            theWriter.println(theInset + "frame.stack.pop();");
                        } else if (theInstruction instanceof BytecodeInstructionBIPUSH) {
                            BytecodeInstructionBIPUSH thePush = (BytecodeInstructionBIPUSH) theInstruction;
                            theWriter.println(theInset + "frame.stack.push(" + thePush.getByteValue() + ");");
                        } else if (theInstruction instanceof BytecodeInstructionSIPUSH) {
                            BytecodeInstructionSIPUSH thePush = (BytecodeInstructionSIPUSH) theInstruction;
                            theWriter.println(theInset + "frame.stack.push(" + thePush.getShortValue() + ");");
                        } else if (theInstruction instanceof BytecodeInstructionGenericLOAD) {
                            BytecodeInstructionGenericLOAD theLoad = (BytecodeInstructionGenericLOAD) theInstruction;
                            theWriter.println(theInset + "frame.stack.push(frame.local" + (theLoad.getVariableIndex() + 1) + ");");
                        } else if (theInstruction instanceof BytecodeInstructionGenericSTORE) {
                            BytecodeInstructionGenericSTORE theStore = (BytecodeInstructionGenericSTORE) theInstruction;
                            theWriter.println(theInset + "frame.local" + (theStore.getVariableIndex() + 1)+" = frame.stack.pop();");
                        } else if (theInstruction instanceof BytecodeInstructionASTORE) {
                            BytecodeInstructionASTORE theStore = (BytecodeInstructionASTORE) theInstruction;
                            theWriter.println(theInset + "frame.local" + (theStore.getVariableIndex() + 1)+" = frame.stack.pop();");
                        } else if (theInstruction instanceof BytecodeInstructionCHECKCAST) {
                            BytecodeInstructionCHECKCAST theCheckCast = (BytecodeInstructionCHECKCAST) theInstruction;
                            BytecodeClassinfoConstant theConstant = theCheckCast.getTypeCheck();
                            theWriter.println(theInset + "// Checkcast ignored at this place for type " + theConstant.getConstant().stringValue());
                        } else if (theInstruction instanceof BytecodeInstructionACONSTNULL) {
                            BytecodeInstructionACONSTNULL theNullConst = (BytecodeInstructionACONSTNULL) theInstruction;
                            theWriter.println(theInset + "frame.stack.push(null);");
                        } else if (theInstruction instanceof BytecodeInstructionALOAD) {
                            BytecodeInstructionALOAD theStore = (BytecodeInstructionALOAD) theInstruction;
                            theWriter.println(theInset + "frame.stack.push(frame.local" + (theStore.getVariableIndex() + 1) + ");");
                        } else if (theInstruction instanceof BytecodeInstructionObjectArrayLOAD) {
                            BytecodeInstructionObjectArrayLOAD theLoad = (BytecodeInstructionObjectArrayLOAD) theInstruction;
                            theWriter.println(theInset + "var theIndex = frame.stack.pop();");
                            theWriter.println(theInset + "var theArrayRef = frame.stack.pop();");
                            theWriter.println(theInset + "frame.stack.push(theArrayRef.data[theIndex]);");
                        } else if (theInstruction instanceof BytecodeInstructionPUTSTATIC) {
                            BytecodeInstructionPUTSTATIC thePut = (BytecodeInstructionPUTSTATIC) theInstruction;
                            BytecodeFieldRefConstant theConstant = thePut.getConstant();
                            BytecodeClassinfoConstant theClassName = theConstant.getClassIndex().getClassConstant();
                            BytecodeUtf8Constant theFieldName = theConstant.getNameAndTypeIndex().getNameAndType().getNameIndex().getName();
                            theWriter.println(
                                    theInset + JSWriterUtils.toClassName(theClassName) + ".classInitCheck();");
                            theWriter.println(theInset + JSWriterUtils.toClassName(theClassName) + ".staticFields." + theFieldName.stringValue() + " = frame.stack.pop();");
                        } else if (theInstruction instanceof BytecodeInstructionGETSTATIC) {
                            BytecodeInstructionGETSTATIC theGet = (BytecodeInstructionGETSTATIC) theInstruction;
                            BytecodeFieldRefConstant theConstant = theGet.getConstant();
                            BytecodeClassinfoConstant theClassName = theConstant.getClassIndex().getClassConstant();
                            BytecodeUtf8Constant theFieldName = theConstant.getNameAndTypeIndex().getNameAndType().getNameIndex()
                                    .getName();
                            theWriter.println(
                                    theInset + JSWriterUtils.toClassName(theClassName) + ".classInitCheck();");
                            theWriter.println(
                                    theInset + "frame.stack.push(" + JSWriterUtils.toClassName(theClassName) + ".staticFields." + theFieldName
                                            .stringValue() + ");");
                        } else if (theInstruction instanceof BytecodeInstructionPUTFIELD) {
                            BytecodeInstructionPUTFIELD theGet = (BytecodeInstructionPUTFIELD) theInstruction;
                            BytecodeFieldRefConstant theConstant = theGet.getFieldRefConstant();
                            BytecodeUtf8Constant theFieldName = theConstant.getNameAndTypeIndex().getNameAndType().getNameIndex()
                                    .getName();
                            theWriter.println(theInset + "var theValue = frame.stack.pop();");
                            theWriter.println(theInset + "var theObjectRef = frame.stack.pop();");
                            theWriter.println(
                                    theInset + "theObjectRef.data." + theFieldName.stringValue() + " = theValue;");
                        } else if (theInstruction instanceof BytecodeInstructionGETFIELD) {
                            BytecodeInstructionGETFIELD theGet = (BytecodeInstructionGETFIELD) theInstruction;
                            BytecodeFieldRefConstant theConstant = theGet.getFieldRefConstant();
                            BytecodeUtf8Constant theFieldName = theConstant.getNameAndTypeIndex().getNameAndType().getNameIndex()
                                    .getName();
                            theWriter.println(theInset + "var theObjectRef = frame.stack.pop();");
                            theWriter.println(
                                    theInset + "frame.stack.push(theObjectRef.data." + theFieldName.stringValue() + ");");
                        } else if (theInstruction instanceof BytecodeInstructionLCMP) {
                            BytecodeInstructionLCMP theCmp = (BytecodeInstructionLCMP) theInstruction;
                            theWriter.println(theInset + "var temp2 = frame.stack.pop();");
                            theWriter.println(theInset + "var temp1 = frame.stack.pop();");
                            theWriter.println(theInset + "if (temp1 > temp2) {");
                            theWriter.println(theInset + "    frame.stack.push(1);");
                            theWriter.println(theInset + "} else if (temp1 < temp2) {");
                            theWriter.println(theInset + "    frame.stack.push(-1);");
                            theWriter.println(theInset + "} else {");
                            theWriter.println(theInset + "    frame.stack.push(0);");
                            theWriter.println(theInset + "}");
                        } else if (theInstruction instanceof BytecodeInstructionGenericSHL) {
                            BytecodeInstructionGenericSHL theShift = (BytecodeInstructionGenericSHL) theInstruction;
                            theWriter.println(theInset + "var theValue2 = frame.stack.pop();");
                            theWriter.println(theInset + "var theValue1 = frame.stack.pop();");
                            theWriter.println(theInset + "frame.stack.push(theValue1 << theValue2);");
                        } else if (theInstruction instanceof BytecodeInstructionGenericSHR) {
                            BytecodeInstructionGenericSHR theShift = (BytecodeInstructionGenericSHR) theInstruction;
                            theWriter.println(theInset + "var theValue2 = frame.stack.pop();");
                            theWriter.println(theInset + "var theValue1 = frame.stack.pop();");
                            theWriter.println(theInset + "frame.stack.push(theValue1 >> theValue2);");
                        } else if (theInstruction instanceof BytecodeInstructionGenericAND) {
                            BytecodeInstructionGenericAND theAnd = (BytecodeInstructionGenericAND) theInstruction;
                            theWriter.println(theInset + "var theValue2 = frame.stack.pop();");
                            theWriter.println(theInset + "var theValue1 = frame.stack.pop();");
                            theWriter.println(theInset + "frame.stack.push(theValue1 & theValue2);");
                        } else if (theInstruction instanceof BytecodeInstructionGenericOR) {
                            BytecodeInstructionGenericOR theOr = (BytecodeInstructionGenericOR) theInstruction;
                            theWriter.println(theInset + "var theValue2 = frame.stack.pop();");
                            theWriter.println(theInset + "var theValue1 = frame.stack.pop();");
                            theWriter.println(theInset + "frame.stack.push(theValue1 | theValue2);");
                        } else if (theInstruction instanceof BytecodeInstructionGenericXOR) {
                            BytecodeInstructionGenericXOR theXOr = (BytecodeInstructionGenericXOR) theInstruction;
                            theWriter.println(theInset + "var theValue2 = frame.stack.pop();");
                            theWriter.println(theInset + "var theValue1 = frame.stack.pop();");
                            theWriter.println(theInset + "frame.stack.push(theValue1 ^ theValue2);");
                        } else if (theInstruction instanceof BytecodeInstructionIFNULL) {
                            BytecodeInstructionIFNULL theIf = (BytecodeInstructionIFNULL) theInstruction;
                            theWriter.println(theInset + "var currentStack = frame.stack.pop();");
                            theWriter.println(theInset + "if (!(currentStack)) {");
                            theWriter.println(theInset + "    " + generateJumpCodeFor(theIf.getJumpTarget()));
                            theWriter.println(theInset + "}");
                        } else if (theInstruction instanceof BytecodeInstructionIFNONNULL) {
                            BytecodeInstructionIFNONNULL theIf = (BytecodeInstructionIFNONNULL) theInstruction;
                            theWriter.println(theInset + "var currentStack = frame.stack.pop();");
                            theWriter.println(theInset + "if (currentStack) {");
                            theWriter.println(theInset + "    " + generateJumpCodeFor(theIf.getJumpTarget()));
                            theWriter.println(theInset + "}");
                        } else if (theInstruction instanceof BytecodeInstructionGenericCMP) {
                            BytecodeInstructionGenericCMP theCmp = (BytecodeInstructionGenericCMP) theInstruction;
                            theWriter.println(theInset + "var temp2 = frame.stack.pop();");
                            theWriter.println(theInset + "var temp1 = frame.stack.pop();");
                            theWriter.println(theInset + "if (temp1 > temp2) {");
                            theWriter.println(theInset + "    frame.stack.push(1);");
                            theWriter.println(theInset + "} else if (temp1 < temp2) {");
                            theWriter.println(theInset + "    frame.stack.push(-1);");
                            theWriter.println(theInset + "} else {");
                            theWriter.println(theInset + "    frame.stack.push(0);");
                            theWriter.println(theInset + "}");
                        } else if (theInstruction instanceof BytecodeInstructionGenericADD) {
                            BytecodeInstructionGenericADD theAdd = (BytecodeInstructionGenericADD) theInstruction;
                            theWriter.println(theInset + "frame.stack.push(frame.stack.pop() + frame.stack.pop());");
                        } else if (theInstruction instanceof BytecodeInstructionGenericDIV) {
                            BytecodeInstructionGenericDIV theDiv = (BytecodeInstructionGenericDIV) theInstruction;
                            theWriter.println(theInset + "var temp1 = frame.stack.pop();");
                            theWriter.println(theInset + "var temp2 = frame.stack.pop();");
                            switch (theDiv.getType()) {
                                case INT:
                                case BYTE:
                                case CHAR:
                                case LONG:
                                case SHORT:
                                    theWriter.println(theInset + "frame.stack.push(Math.floor(temp2 / temp1));");
                                    break;
                                default:
                                    theWriter.println(theInset + "frame.stack.push(temp2 / temp1);");
                                    break;
                            }
                        } else if (theInstruction instanceof BytecodeInstructionGenericUSHR) {
                            BytecodeInstructionGenericUSHR theShift = (BytecodeInstructionGenericUSHR) theInstruction;
                            theWriter.println(theInset + "var theValue2 = frame.stack.pop();");
                            theWriter.println(theInset + "var theValue1 = frame.stack.pop();");
                            theWriter.println(theInset + "frame.stack.push(theValue1 >>> theValue2);");
                        } else if (theInstruction instanceof BytecodeInstructionGenericREM) {
                            BytecodeInstructionGenericREM theRem = (BytecodeInstructionGenericREM) theInstruction;
                            theWriter.println(theInset + "var temp1 = frame.stack.pop();");
                            theWriter.println(theInset + "var temp2 = frame.stack.pop();");
                            theWriter.println(theInset + "frame.stack.push(Math.floor(temp2 % temp1));");
                        } else if (theInstruction instanceof BytecodeInstructionGenericMUL) {
                            BytecodeInstructionGenericMUL theMul = (BytecodeInstructionGenericMUL) theInstruction;
                            theWriter.println(theInset + "var temp1 = frame.stack.pop();");
                            theWriter.println(theInset + "var temp2 = frame.stack.pop();");
                            theWriter.println(theInset + "frame.stack.push(temp1 * temp2);");
                        } else if (theInstruction instanceof BytecodeInstructionGenericSUB) {
                            BytecodeInstructionGenericSUB theSub = (BytecodeInstructionGenericSUB) theInstruction;
                            theWriter.println(theInset + "var temp1 = frame.stack.pop();");
                            theWriter.println(theInset + "var temp2 = frame.stack.pop();");
                            theWriter.println(theInset + "frame.stack.push(temp2 - temp1);");
                        } else if (theInstruction instanceof BytecodeInstructionGenericNEG) {
                            BytecodeInstructionGenericNEG theNeg = (BytecodeInstructionGenericNEG) theInstruction;
                            theWriter.println(theInset + "var temp = - frame.stack.pop();");
                            theWriter.println(theInset + "frame.stack.push(temp);");
                        } else if (theInstruction instanceof BytecodeInstructionGenericArraySTORE) {
                            BytecodeInstructionGenericArraySTORE theStore = (BytecodeInstructionGenericArraySTORE) theInstruction;
                            theWriter.println(theInset + "var theValue = frame.stack.pop();");
                            theWriter.println(theInset + "var theIndex = frame.stack.pop();");
                            theWriter.println(theInset + "var theArrayRef = frame.stack.pop();");
                            theWriter.println(theInset + "theArrayRef.data[theIndex] = theValue;");
                        } else if (theInstruction instanceof BytecodeInstructionObjectArraySTORE) {
                            BytecodeInstructionObjectArraySTORE theStore = (BytecodeInstructionObjectArraySTORE) theInstruction;
                            theWriter.println(theInset + "var theValue = frame.stack.pop();");
                            theWriter.println(theInset + "var theIndex = frame.stack.pop();");
                            theWriter.println(theInset + "var theArrayRef = frame.stack.pop();");
                            theWriter.println(theInset + "theArrayRef.data[theIndex] = theValue;");
                        } else if (theInstruction instanceof BytecodeInstructionGenericArrayLOAD) {
                            BytecodeInstructionGenericArrayLOAD theLoad = (BytecodeInstructionGenericArrayLOAD) theInstruction;
                            theWriter.println(theInset + "var theIndex = frame.stack.pop();");
                            theWriter.println(theInset + "var theArrayRef = frame.stack.pop();");
                            theWriter.println(theInset + "frame.stack.push(theArrayRef.data[theIndex]);");
                        } else if (theInstruction instanceof BytecodeInstructionARRAYLENGTH) {
                            BytecodeInstructionARRAYLENGTH theLength = (BytecodeInstructionARRAYLENGTH) theInstruction;
                            theWriter.println(theInset + "var theArrayRef = frame.stack.pop();");
                            theWriter.println(theInset + "frame.stack.push(theArrayRef.data.length);");
                        } else if (theInstruction instanceof BytecodeInstructionGenericRETURN) {
                            BytecodeInstructionGenericRETURN theReturn = (BytecodeInstructionGenericRETURN) theInstruction;
                            theWriter.println(theInset + "return frame.stack.pop();");
                        } else if (theInstruction instanceof BytecodeInstructionObjectRETURN) {
                            BytecodeInstructionObjectRETURN theReturn = (BytecodeInstructionObjectRETURN) theInstruction;
                            theWriter.println(theInset + "return frame.stack.pop();");
                        } else if (theInstruction instanceof BytecodeInstructionATHROW) {
                            BytecodeInstructionATHROW theThrow = (BytecodeInstructionATHROW) theInstruction;

                            theWriter.println(theInset + "var theException = frame.stack[frame.stack.length - 1];");
                            writeExceptionHandlerCode(aLinkerContext, theExceptionRethrower, theWriter, theProgram,
                                    theInset, theThrow, "theException");

                        } else if (theInstruction instanceof BytecodeInstructionTABLESWITCH) {
                            BytecodeInstructionTABLESWITCH theSwitch = (BytecodeInstructionTABLESWITCH) theInstruction;
                            theWriter.println("// tableswitch");
                            theWriter.println(theInset + "var theCurrentValue = frame.stack.pop();");
                            theWriter.println(theInset + "if (theCurrentValue < " + theSwitch.getLowValue() + " || theCurrentValue > " + theSwitch.getHighValue() + ") {");
                            theWriter.println(theInset + "  currentLabel = " + theSwitch.getDefaultJumpTarget().getAddress() + ";");
                            theWriter.println(theInset + "  continue controlflowloop;");
                            theWriter.println(theInset + "}");
                            theWriter.println(theInset + "var theOffset = theCurrentValue - " + theSwitch.getLowValue() + ";");
                            theWriter.println(theInset + "switch(theOffset) {");
                            long[] theOffsets = theSwitch.getOffsets();
                            for (int i=0;i<theOffsets.length;i++) {
                                theWriter.println(theInset + "  case " + i + ":");
                                theWriter.println(theInset + "    currentLabel = " + (theSwitch.getOpcodeAddress().getAddress() + theOffsets[i]) + ";");
                                theWriter.println(theInset + "    continue controlflowloop;");
                            }
                            theWriter.println(theInset + "}");
                            theWriter.println(theInset + "throw \"Illegal jump target\";");

                        } else if (theInstruction instanceof BytecodeInstructionLOOKUPSWITCH) {
                            BytecodeInstructionLOOKUPSWITCH theSwitch = (BytecodeInstructionLOOKUPSWITCH) theInstruction;
                            theWriter.println("// lookupwitch");
                            theWriter.println(theInset + "var theCurrentValue = frame.stack.pop();");
                            theWriter.println(theInset + "switch(theCurrentValue) {");
                            for (BytecodeInstructionLOOKUPSWITCH.Pair thePair : theSwitch.getPairs()) {
                                theWriter.println(theInset + "  case " + thePair.getMatch() + ":");
                                theWriter.println(theInset + "    currentLabel = " + (theSwitch.getOpcodeAddress().getAddress() + thePair.getOffset()) + ";");
                                theWriter.println(theInset + "    continue controlflowloop;");
                            }
                            theWriter.println(theInset + "}");
                            theWriter.println(theInset + "return " + theSwitch.getDefaultJumpTarget().getAddress() + ";");
                        } else if (theInstruction instanceof BytecodeInstructionFCONST) {
                            BytecodeInstructionFCONST theConst = (BytecodeInstructionFCONST) theInstruction;
                            theWriter.println(theInset + "frame.stack.push(" + theConst.getFloatValue() + ");");
                        } else if (theInstruction instanceof BytecodeInstructionICONST) {
                            BytecodeInstructionICONST theConst = (BytecodeInstructionICONST) theInstruction;
                            theWriter.println(theInset + "frame.stack.push(" + theConst.getIntConst() + ");");
                        } else if (theInstruction instanceof BytecodeInstructionLCONST) {
                            BytecodeInstructionLCONST theConst = (BytecodeInstructionLCONST) theInstruction;
                            theWriter.println(theInset + "frame.stack.push(" + theConst.getLongConst() + ");");
                        } else if (theInstruction instanceof BytecodeInstructionDCONST) {
                            BytecodeInstructionDCONST theConst = (BytecodeInstructionDCONST) theInstruction;
                            theWriter.println(theInset + "frame.stack.push(" + theConst.getDoubleConst() + ");");
                        } else if (theInstruction instanceof BytecodeInstructionI2Generic) {
                            theWriter.println(theInset + "var theInt = frame.stack.pop();");
                            theWriter.println(theInset + "frame.stack.push(theInt);");
                        } else if (theInstruction instanceof BytecodeInstructionL2Generic) {
                            theWriter.println(theInset + "var theLong = frame.stack.pop();");
                            theWriter.println(theInset + "frame.stack.push(theLong);");
                        } else if (theInstruction instanceof BytecodeInstructionD2Generic) {
                            theWriter.println(theInset + "var theDouble = frame.stack.pop();");
                            BytecodeInstructionD2Generic theConv = (BytecodeInstructionD2Generic) theInstruction;
                            switch (theConv.getTargetType()) {
                                case BYTE:
                                case SHORT:
                                case CHAR:
                                case INT:
                                case LONG:
                                    theWriter.println(theInset + "frame.stack.push(Math.round(theDouble));");
                                    break;
                                case FLOAT:
                                    theWriter.println(theInset + "frame.stack.push(theDouble);");
                                    break;
                                default:
                                    theWriter.println(theInset + "frame.stack.push(theDouble);");
                                    break;
                            }
                        } else if (theInstruction instanceof BytecodeInstructionF2Generic) {
                            theWriter.println(theInset + "var theFloat = frame.stack.pop();");
                            BytecodeInstructionF2Generic theConv = (BytecodeInstructionF2Generic) theInstruction;
                            switch (theConv.getTargetType()) {
                                case BYTE:
                                case SHORT:
                                case CHAR:
                                case INT:
                                case LONG:
                                    theWriter.println(theInset + "frame.stack.push(Math.round(theFloat));");
                                    break;
                                default:
                                    theWriter.println(theInset + "frame.stack.push(theFloat);");
                                    break;
                            }
                        } else if (theInstruction instanceof BytecodeInstructionGenericLDC) {
                            BytecodeInstructionGenericLDC theLoad = (BytecodeInstructionGenericLDC) theInstruction;
                            BytecodeConstant theConstant = theLoad.constant();
                            if (theConstant instanceof BytecodeFloatConstant) {
                                BytecodeFloatConstant theFloat = (BytecodeFloatConstant) theConstant;
                                theWriter.println(theInset + "frame.stack.push(" + theFloat.getFloatValue() + ");");
                            } else if (theConstant instanceof BytecodeDoubleConstant) {
                                BytecodeDoubleConstant theDouble = (BytecodeDoubleConstant) theConstant;
                                theWriter.println(theInset + "frame.stack.push(" + theDouble.getDoubleValue() + ");");
                            } else if (theConstant instanceof BytecodeLongConstant) {
                                BytecodeLongConstant theLong = (BytecodeLongConstant) theConstant;
                                theWriter.println(theInset + "frame.stack.push(" + theLong.getLongValue() + ");");
                            } else if (theConstant instanceof BytecodeClassinfoConstant) {
                                BytecodeClassinfoConstant theClassInfo = (BytecodeClassinfoConstant) theConstant;
                                theWriter.println(theInset + JSWriterUtils.toClassName(theClassInfo) + ".classInitCheck();");
                                theWriter.println(theInset + "frame.stack.push(" + JSWriterUtils.toClassName(theClassInfo) + ".runtimeClass);");
                            } else if (theConstant instanceof BytecodeIntegerConstant) {
                                BytecodeIntegerConstant theInteger = (BytecodeIntegerConstant) theConstant;
                                theWriter.println(theInset + "frame.stack.push(" + theInteger.getIntegerValue() + ");");
                            } else if (theConstant instanceof BytecodeStringConstant) {
                                try {
                                    BytecodeStringConstant theStr = (BytecodeStringConstant) theConstant;
                                    String theValue = theStr.getValue().stringValue();
                                    byte[] theBytes = theStr.getValue().toUTF8Bytes();

                                    // Construct a String
                                    theWriter.println(theInset + "// new String from constant pool : " + theValue);
                                    theWriter.println(theInset + "frame.stack.push(bytecoder.newString(" + JSWriterUtils.toArray(theBytes) + "));");
                                } catch (UnsupportedEncodingException e) {
                                    throw new RuntimeException(e);
                                }
                            } else {
                                throw new IllegalStateException("Unsupported constant : " + theConstant);
                            }
                        } else if (theInstruction instanceof BytecodeInstructionGOTO) {
                            BytecodeInstructionGOTO theGoto = (BytecodeInstructionGOTO) theInstruction;
                            theWriter.println(theInset + generateJumpCodeFor(theGoto.getJumpAddress()));
                        } else if (theInstruction instanceof BytecodeInstructionIFACMP) {
                            BytecodeInstructionIFACMP theCond = (BytecodeInstructionIFACMP) theInstruction;
                            BytecodeOpcodeAddress theTarget = theCond.getJumpTarget();
                            theWriter.println(theInset + "var theValue2 = frame.stack.pop();");
                            theWriter.println(theInset + "var theValue1 = frame.stack.pop();");
                            switch (theCond.getType()) {
                                case eq:
                                    theWriter.println(theInset + "if (theValue1 == theValue2) {");
                                    theWriter.println(theInset + "    " + generateJumpCodeFor(theTarget));
                                    theWriter.println(theInset + "}");
                                    break;
                                case ne:
                                    theWriter.println(theInset + "if (theValue1 != theValue2) {");
                                    theWriter.println(theInset + "    " + generateJumpCodeFor(theTarget));
                                    theWriter.println(theInset + "}");
                                    break;
                            }
                        } else if (theInstruction instanceof BytecodeInstructionIFICMP) {
                            BytecodeInstructionIFICMP theCond = (BytecodeInstructionIFICMP) theInstruction;

                            BytecodeOpcodeAddress theTarget = theCond.getJumpTarget();
                            theWriter.println(theInset + "var theValue2 = frame.stack.pop();");
                            theWriter.println(theInset + "var theValue1 = frame.stack.pop();");
                            switch (theCond.getType()) {
                                case eq:
                                    theWriter.println(theInset + "if (theValue1 == theValue2) {");
                                    theWriter.println(theInset + "    " + generateJumpCodeFor(theTarget));
                                    theWriter.println(theInset + "}");
                                    break;
                                case gt:
                                    theWriter.println(theInset + "if (theValue1 > theValue2) {");
                                    theWriter.println(theInset + "    " + generateJumpCodeFor(theTarget));
                                    theWriter.println(theInset + "}");
                                    break;
                                case le:
                                    theWriter.println(theInset + "if (theValue1 <= theValue2) {");
                                    theWriter.println(theInset + "    " + generateJumpCodeFor(theTarget));
                                    theWriter.println(theInset + "}");
                                    break;
                                case ge:
                                    theWriter.println(theInset + "if (theValue1 >= theValue2) {");
                                    theWriter.println(theInset + "    " + generateJumpCodeFor(theTarget));
                                    theWriter.println(theInset + "}");
                                    break;
                                case lt:
                                    theWriter.println(theInset + "if (theValue1 < theValue2) {");
                                    theWriter.println(theInset + "    " + generateJumpCodeFor(theTarget));
                                    theWriter.println(theInset + "}");
                                    break;
                                case ne:
                                    theWriter.println(theInset + "if (theValue1 != theValue2) {");
                                    theWriter.println(theInset + "    " + generateJumpCodeFor(theTarget));
                                    theWriter.println(theInset + "}");
                                    break;
                            }

                        } else if (theInstruction instanceof BytecodeInstructionIFCOND) {
                            BytecodeInstructionIFCOND theCond = (BytecodeInstructionIFCOND) theInstruction;
                            BytecodeOpcodeAddress theTarget = theCond.getJumpTarget();
                            theWriter.println(theInset + "var theValue = frame.stack.pop();");
                            switch (theCond.getType()) {
                                case eq:
                                    theWriter.println(theInset + "if (theValue == 0) {");
                                    theWriter.println(theInset + "    " + generateJumpCodeFor(theTarget));
                                    theWriter.println(theInset + "}");
                                    break;
                                case gt:
                                    theWriter.println(theInset + "if (theValue > 0) {");
                                    theWriter.println(theInset + "    " + generateJumpCodeFor(theTarget));
                                    theWriter.println(theInset + "}");
                                    break;
                                case le:
                                    theWriter.println(theInset + "if (theValue <= 0) {");
                                    theWriter.println(theInset + "    " + generateJumpCodeFor(theTarget));
                                    theWriter.println(theInset + "}");
                                    break;
                                case ge:
                                    theWriter.println(theInset + "if (theValue >= 0) {");
                                    theWriter.println(theInset + "    " + generateJumpCodeFor(theTarget));
                                    theWriter.println(theInset + "}");
                                    break;
                                case lt:
                                    theWriter.println(theInset + "if (theValue < 0) {");
                                    theWriter.println(theInset + "    " + generateJumpCodeFor(theTarget));
                                    theWriter.println(theInset + "}");
                                    break;
                                case ne:
                                    theWriter.println(theInset + "if (theValue != 0) {");
                                    theWriter.println(theInset + "    " + generateJumpCodeFor(theTarget));
                                    theWriter.println(theInset + "}");
                                    break;
                            }
                        } else {
                            throw new IllegalStateException("Cannot compile " + theInstruction);
                        }
                    }
                    if (!theBlock.endsWithJump() && !theBlock.endsWithReturn()) {
                        theWriter.println(
                                theInset + "currentLabel = " + theProgram.getNextInstructionAddress(theBlock.getLastInstruction())
                                        + ";");
                        theWriter.println(
                                theInset + "continue controlflowloop;");
                    }
                    theWriter.println("         }");
                }
                theWriter.println("        }");
                theWriter.println("    },");
            });

            theWriter.println("}");
            theWriter.println();
        });

        theWriter.flush();

        System.out.println("Total number of virtual method calls : " + theNumberOfVirtualCalls.get());
        System.out.println("Remaining virtual method calls " + theNumberOfRealVirtualCalls.get());

        return theStrWriter.toString();
    }

    private void writeExceptionHandlerCode(BytecodeLinkerContext aLinkerContext, BytecodeLinkedClass aExceptionRethrower,
            PrintWriter aWriter, BytecodeProgram aProgram,
            String aInset, BytecodeInstruction aInstruction, String aExceptionVariableName) {
        BytecodeExceptionTableEntry[] theActiveHandlers = aProgram.getActiveExceptionHandlers(aInstruction.getOpcodeAddress(), aProgram.getExceptionHandlers());
        if (theActiveHandlers.length == 0) {
            // Missing catch block
            aWriter.println(aInset + JSWriterUtils.toClassName(aExceptionRethrower.getClassName()) + "." + JSWriterUtils.toMethodName("registerExceptionOutcome", theRegisterExceptionOutcomeSignature) + "(" + aExceptionVariableName + ");");
            aWriter.println(aInset + "return;");
        } else {
            for (BytecodeExceptionTableEntry theEntry : theActiveHandlers) {
                if (!theEntry.isFinally()) {
                    BytecodeClassinfoConstant theConstant = theEntry.getCatchType();
                    BytecodeLinkedClass theLinkedClass = aLinkerContext.isLinkedOrNull(theConstant.getConstant());
                    if (theLinkedClass != null) {
                        aWriter.println(
                                aInset + "if (" + aExceptionVariableName + ".clazz.instanceOfType(" + theLinkedClass.getUniqueId()
                                        + ")) {");
                        aWriter.println(aInset + "    currentLabel = " + theEntry.getHandlerPc().getAddress() + ";");
                        aWriter.println(aInset + "    continue controlflowloop;");
                        aWriter.println(aInset + "}");
                    }
                }
            }
            aWriter.println(aInset + JSWriterUtils.toClassName(aExceptionRethrower.getClassName()) + "." + JSWriterUtils.toMethodName("registerExceptionOutcome", theRegisterExceptionOutcomeSignature) + "(" + aExceptionVariableName + ");");
            aWriter.println(aInset + "return;");
        }
    }
}
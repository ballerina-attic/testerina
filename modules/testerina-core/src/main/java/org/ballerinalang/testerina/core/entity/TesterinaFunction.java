/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.ballerinalang.testerina.core.entity;

import org.ballerinalang.bre.Context;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.util.codegen.FunctionInfo;
import org.ballerinalang.util.codegen.ProgramFile;
import org.ballerinalang.util.exceptions.BallerinaException;
import org.ballerinalang.util.program.BLangFunctions;

/**
 * TesterinaFunction entity class
 */
public class TesterinaFunction {

    private String name;
    private Type type;
    private FunctionInfo bFunction;
    private ProgramFile programFile;

    public static final String PREFIX_TEST = "TEST";
    public static final String PREFIX_BEFORETEST = "BEFORETEST";
    public static final String PREFIX_AFTERTEST = "AFTERTEST";

    /**
     * Prefixes for the test function names
     */
    public enum Type {
        TEST(PREFIX_TEST), BEFORE_TEST(PREFIX_BEFORETEST), AFTER_TEST(PREFIX_AFTERTEST);

        String prefix;

        Type(String prefix) {
            this.prefix = prefix;
        }

        @Override
        public String toString() {
            return prefix;
        }
    }

    TesterinaFunction(ProgramFile programFile, FunctionInfo bFunction, Type type) {
        this.name = bFunction.getName();
        this.type = type;
        this.bFunction = bFunction;
        this.programFile = programFile;
    }

    public BValue[] invoke() throws BallerinaException {
        return invoke(new BValue[] {});
    }

    private BValue[] invoke(BValue[] args) {
        return invoke(args, new Context());
    }

    /**
     * Invokes a Ballerina function defined in the given language model.
     *
     * @param bContext ballerina context
     * @param args     function arguments
     * @return return values from the function
     */
    public BValue[] invoke(BValue[] args, Context bContext) {
        return BLangFunctions.invokeNew(programFile, bFunction.getPackageInfo().getPkgPath(), bFunction.getName(),
                                        args);
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public FunctionInfo getbFunction() {
        return this.bFunction;
    }

    public void setbFunctionInfo(FunctionInfo bFunction) {
        this.bFunction = bFunction;
    }

}

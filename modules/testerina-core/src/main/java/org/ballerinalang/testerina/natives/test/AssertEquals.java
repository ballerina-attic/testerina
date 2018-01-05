/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.ballerinalang.testerina.natives.test;

import org.ballerinalang.bre.Context;
import org.ballerinalang.model.types.TypeKind;
import org.ballerinalang.model.values.BJSON;
import org.ballerinalang.model.values.BString;
import org.ballerinalang.model.values.BStruct;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.model.values.BXML;
import org.ballerinalang.natives.AbstractNativeFunction;
import org.ballerinalang.natives.annotations.Argument;
import org.ballerinalang.natives.annotations.BallerinaFunction;
import org.ballerinalang.util.exceptions.BLangRuntimeException;
import org.ballerinalang.util.program.BLangFunctions;

/**
 * Implementation for assert equals function. This does not support JSON and XML variables.
 */
@BallerinaFunction(packageName = "ballerina.test", functionName = "assertEquals",
                   args = {
                           @Argument(name = "expected", type = TypeKind.ANY),
                           @Argument(name = "actual", type = TypeKind.ANY),
                           @Argument(name = "message", type = TypeKind.STRING)},
                   isPublic = true)
public class AssertEquals extends AbstractNativeFunction {
    private static final String ASSERT_FAILURE_ERROR_CATEGORY = "assert-failure";
    
    /**
     * {@inheritDoc}
     */
    @Override
    public BValue[] execute(Context context) {
        // Using ref to get expected and unexpected as 'any' variables are refs.
        BValue expectedArgument = getRefArgument(context, 0);
        BValue actualArgument = getRefArgument(context, 1);
        
        // Getting the assert error message.
        String assertMessage = getStringArgument(context, 0);
    
        // Checking whether the types of the expected and actual are the same.
        if (expectedArgument.getType().getPackagePath() != null &&
            actualArgument.getType().getPackagePath() != null &&
            !expectedArgument.getType().getPackagePath().equals(actualArgument.getType().getPackagePath())) {
            this.throwAssertError(context, expectedArgument.stringValue(), actualArgument.stringValue(),
                                                                "Types are from different packages. " + assertMessage);
        }
        
        if (!expectedArgument.getType().equals(actualArgument.getType())) {
            this.throwAssertError(context, expectedArgument.stringValue(), actualArgument.stringValue(),
                                                                            "Types are different. " + assertMessage);
        }
    
        if (expectedArgument instanceof BJSON) {
            throw new BLangRuntimeException("assertEquals is not supported for json.");
        }
    
        if (expectedArgument instanceof BXML) {
            throw new BLangRuntimeException("assertEquals is not supported for xml.");
        }
    
        if (!expectedArgument.stringValue().equals(actualArgument.stringValue())) {
            this.throwAssertError(context, expectedArgument.stringValue(), actualArgument.stringValue(), assertMessage);
        }
        return VOID_RETURN;
    }
    
    /**
     * Throwing an error using createBallerinaError() non-native function.
     * @param context The program context.
     * @param expectedValue The expected value as a String.
     * @param actualValue The actual value as a String.
     * @param assertMessage The assert error message;
     */
    public void throwAssertError(Context context, String expectedValue, String actualValue, String assertMessage) {
        if ("".equals(assertMessage)) {
            assertMessage = "assert failed.";
        }
        
        BString errorMessage = new BString(assertMessage +
                                                ". expected [" + expectedValue + "] but found [" + actualValue + "].");
        BString category = new BString(ASSERT_FAILURE_ERROR_CATEGORY);
        BValue[] args = new BValue[]{errorMessage, category};
        BValue[] error = BLangFunctions.invokeNew(context.programFile, "ballerina.test",
                                                                                "createBallerinaError", args, context);
        BStruct errorStruct = (BStruct) error[0];
        throw new BLangRuntimeException(errorStruct.getStringField(0));
    }
}

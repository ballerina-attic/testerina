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

package org.ballerinalang.testerina.test;

import org.ballerinalang.testerina.test.utils.BTestUtils;
import org.ballerinalang.testerina.test.utils.CompileResult;
import org.ballerinalang.util.codegen.FunctionInfo;
import org.ballerinalang.util.codegen.PackageInfo;
import org.ballerinalang.util.exceptions.BLangRuntimeException;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Optional;

/**
 * Test cases for assertEqual() function.
 */
public class AssertEqualsTest {
    CompileResult compileResult;
    
    @BeforeClass
    public void setup() {
        compileResult = BTestUtils.compile("assert-test/assert-equals-valid.bal");
    }
    
    /**
     * Data Provider which provides all FunctionInfo items in the bal file.
     * @return FunctionInfo of the bal file..
     * @throws FileNotFoundException Unable to find test bal file.
     */
    @DataProvider(name = "AssertEqualsValidFunctionInfos")
    public Object[][] functionNames() throws FileNotFoundException {
        Optional<PackageInfo> assertFilePackage = Arrays.stream(compileResult.getProgFile().getPackageInfoEntries())
                .filter((packageInfo -> packageInfo.getPkgPath().equals(".")))
                .findFirst();
        
        if (assertFilePackage.isPresent()) {
            PackageInfo packageInfo = assertFilePackage.get();
            return Arrays.stream(packageInfo.getFunctionInfoEntries())
                    .filter((func) -> !func.getName().equals("..<init>"))
                    .map(functionInfo -> new Object[]{functionInfo})
                    .toArray(Object[][]::new);
            
        } else {
            throw new FileNotFoundException("Unable to find test file.");
        }
    }
    
    /**
     * Validating assertEquals functions.
     * @param assertFunction The FunctionInfo object.
     */
    @Test(dataProvider = "AssertEqualsValidFunctionInfos")
    public void testAssertEquals(FunctionInfo assertFunction) {
        if (assertFunction.getName().contains("False")) {
            try {
                BTestUtils.invoke(compileResult, assertFunction.getName());
                Assert.assertTrue(false);
            } catch (BLangRuntimeException e) {
                boolean assertCondition = e.getMessage().contains("expected") ||
                                                                            e.getMessage().contains("not supported");
                Assert.assertTrue(assertCondition, "Invalid error message received.");
            }
        } else {
            BTestUtils.invoke(compileResult, assertFunction.getName());
        }
    }
}

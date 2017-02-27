/*
*  Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing,
*  software distributed under the License is distributed on an
*  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*  KIND, either express or implied.  See the License for the
*  specific language governing permissions and limitations
*  under the License.
*/
package org.ballerinalang.testerina.test;

import org.ballerinalang.testerina.core.BTestRunner;
import org.testng.annotations.Test;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TesterinaTestGen {
    private static Path programDirPath = Paths.get("src/test/resources/balFiles/balFolder/a/b");
    Path[] paths = {programDirPath};

    private String resources = "";
    BTestRunner bTestRunner = new BTestRunner();

    @Test(description = "Test copy resources functionality")
    public void testResources() throws IOException{
        bTestRunner.runTest(paths);
    }
}

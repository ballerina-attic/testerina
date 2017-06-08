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
package org.ballerinalang.testerina.natives.mock;

import org.ballerinalang.bre.Context;
import org.ballerinalang.model.BLangPackage;
import org.ballerinalang.model.BLangProgram;
import org.ballerinalang.model.BallerinaConnectorDef;
import org.ballerinalang.model.Connector;
import org.ballerinalang.model.ParameterDef;
import org.ballerinalang.model.Service;
import org.ballerinalang.model.expressions.BasicLiteral;
import org.ballerinalang.model.expressions.ConnectorInitExpr;
import org.ballerinalang.model.expressions.Expression;
import org.ballerinalang.model.statements.VariableDefStmt;
import org.ballerinalang.model.types.BType;
import org.ballerinalang.model.types.TypeEnum;
import org.ballerinalang.model.values.BConnector;
import org.ballerinalang.model.values.BRefType;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.model.values.StructureType;
import org.ballerinalang.natives.AbstractNativeFunction;
import org.ballerinalang.natives.annotations.Argument;
import org.ballerinalang.natives.annotations.Attribute;
import org.ballerinalang.natives.annotations.BallerinaAnnotation;
import org.ballerinalang.natives.annotations.BallerinaFunction;
import org.ballerinalang.testerina.core.TesterinaRegistry;
import org.ballerinalang.testerina.core.TesterinaUtils;
import org.ballerinalang.util.codegen.AttributeInfo;
import org.ballerinalang.util.codegen.ProgramFile;
import org.ballerinalang.util.codegen.ServiceInfo;
import org.ballerinalang.util.exceptions.BallerinaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.security.pkcs11.wrapper.Constants;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Native function ballerina.lang.mock:setValue.
 * This can be used to modify a global connector instance's arguments.
 * Behavior is reflection-like.
 *
 * @since 0.8.0
 */
@BallerinaFunction(packageName = "ballerina.mock", functionName = "setValue", args = {
        @Argument(name = "mockableConnectorPathExpr", type = TypeEnum.STRING),
        @Argument(name = "value", type = TypeEnum.STRING) }, isPublic = true)
@BallerinaAnnotation(annotationName = "Description",
                     attributes = { @Attribute(name = "value",
                                               value = "Modifies global connector instance's arguments for mocking "
                                                       + "purposes") })
@BallerinaAnnotation(annotationName = "Param",
                     attributes = { @Attribute(name = "mockableConnectorPathExpr",
                                               value = "A path like syntax to identify and navigate the "
                                                       + "connector instances of a ballerina service") })
@BallerinaAnnotation(annotationName = "Param",
                     attributes = { @Attribute(name = "value",
                                               value = "Mock value to set (e.g.: endpoint URL)") })
public class SetValue extends AbstractNativeFunction {

    private static final String COULD_NOT_FIND_MATCHING_CONNECTOR = "Could not find a matching connector for the name ";

    private static final String MSG_PREFIX = "mock:setValue: ";
    private static final String MOCK_PATH_SYNTAX = "<ServiceName>[.]<ConnectorVariableName(s)>[.]parameterX";
    private static final String MOCK_PATH_SYNTAX_EXAMPLE = "helloWorld.httpCon.parameter1";
    private static final int INTEGER = 0;
    private static final int FLOAT = 1;
    private static final int BOOLEAN = 2;
    private static final int STRING = 3;

    private static final Logger logger = LoggerFactory.getLogger(SetValue.class);

    //TODO: Improve this to support modification of local variables as well
    @Override
    public BValue[] execute(Context ctx) {

        //Set the global connector instance as given by names in the path array.
        //keep traversing the path array until the last connector (element - 1).
        //once found, get the primitive that has the name of last element in the path array
        //change that primitive type's value to the `value` user entered.
        //then return

        if (!TesterinaUtils.isMockEnabled()) {
            throw new BallerinaException(
                    MSG_PREFIX + "'--mock' parameter or the 'ballerina.mock' system property is not found. ");
        }

        //1) split the mockConnectorPath string by dots
        //first element is the service name, in-betweens are connectors and the last element is a primitive
        MockConnectorPath mockCnctrPath = parseMockConnectorPath(ctx);

        //Locate the relevant Application, Package, and the Service
        ServiceInfo service = getMatchingService(mockCnctrPath.serviceName);

        if (mockCnctrPath.connectorNames.size() < 1) {
            throw new BallerinaException(
                    "Connectors entered for the service " + mockCnctrPath.serviceName + " is empty." + ". Syntax: "
                            + MOCK_PATH_SYNTAX + ". Example: " + MOCK_PATH_SYNTAX_EXAMPLE);
        }
        String firstConnectorName = mockCnctrPath.connectorNames.pop();

        StructureType globalMemBlock = ctx.getProgramFile().getGlobalMemoryBlock();
        int nGlobalRefVars = ctx.getProgramFile().getGlobalVarIndexes()[5];
        BConnector connector = null;

        for (int i = 0; i < nGlobalRefVars; i++) {
            if (globalMemBlock.getRefField(i) instanceof BConnector) {
                connector = (BConnector) globalMemBlock.getRefField(i);
                break;
            }
        }

        if (connector == null) {
            throw new BallerinaException(COULD_NOT_FIND_MATCHING_CONNECTOR);
        }

        //TODO: Find a better way to do this 
        BType[] fieldTypes = connector.getFieldTypes();
        int[] fieldTypeCount = new int[5];

        for (int i = 0; i <= mockCnctrPath.indexOfMockField; i++) {
            switch (fieldTypes[i].getName()) {
                case "int":
                    fieldTypeCount[INTEGER]++;
                    break;
                case "float":
                    fieldTypeCount[FLOAT]++;
                    break;
                case "boolean":
                    fieldTypeCount[BOOLEAN]++;
                    break;
                case "string":
                    fieldTypeCount[STRING]++;
                    break;
                default:
                    fieldTypeCount[4]++;
                    break; // TODO: Improve this
            }
        }

        switch (fieldTypes[mockCnctrPath.indexOfMockField].getName()) {
            case "int":
                connector.setIntField(fieldTypeCount[INTEGER] - 1, Integer.valueOf(mockCnctrPath.mockValue));
                break;
            case "float":
                connector.setFloatField(fieldTypeCount[FLOAT] - 1, Double.valueOf(mockCnctrPath.mockValue));
                break;
            case "boolean":
                connector.setBooleanField(fieldTypeCount[BOOLEAN] - 1, Integer.valueOf(mockCnctrPath.mockValue));
                break;
            case "string":
                connector.setStringField(fieldTypeCount[STRING] - 1, mockCnctrPath.mockValue);
                break;
            default:
                throw new BallerinaException(fieldTypes[mockCnctrPath.indexOfMockField].getName() +
                                                     " type variables cannot be modified.");
        }

        return VOID_RETURN;
    }

    private ServiceInfo getMatchingService(String serviceName) {
        Optional<ServiceInfo> matchingService = Optional.empty();
        for (ProgramFile bLangProgram : TesterinaRegistry.getInstance().getProgramFiles()) {
            // 1) First, we get the Service for the given serviceName from the original BLangProgram
            matchingService = Arrays.stream(bLangProgram.getServicePackageNameList())
                    .map(sName -> bLangProgram.getPackageInfo(sName).getServiceInfoList())
                    .flatMap(Arrays::stream)
                    .filter(serviceInfo -> serviceInfo.getName().equals(serviceName))
                    .findAny();
        }

        // fail further processing if we can't find the application/service
        if (!matchingService.isPresent()) {
            // Added for user convenience. Since we are stopping further progression of the program,
            // perf overhead is ignored.
            Set<String> servicesSet = TesterinaRegistry.getInstance().getProgramFiles().stream()
                    .map(ProgramFile::getServicePackageNameList).flatMap(Arrays::stream)
                    .collect(Collectors.toSet());

            throw new BallerinaException(MSG_PREFIX + "No matching service for the name '" + serviceName + "' found. "
                    + "Did you mean to include one of these services? " + servicesSet);
        }

        return matchingService.get();
    }

    private MockConnectorPath parseMockConnectorPath(Context ctx) {
        String mockCntrPathString = getArgument(ctx, 0).stringValue();
        String mockValue = getArgument(ctx, 1).stringValue();
        String[] mockCntrPathArr = mockCntrPathString.split("\\.");
        if (mockCntrPathArr.length < 2) {
            throw new BallerinaException(
                    "Error in parsing " + mockCntrPathString + ". Syntax: " + MOCK_PATH_SYNTAX + ". Example: "
                            + MOCK_PATH_SYNTAX_EXAMPLE);
        }

        String paramName = mockCntrPathArr[mockCntrPathArr.length - 1];

        //in case of multi-argument connector, we need a way to find the exact argument to update
        //for ballerina connectors, it can be derived via ParameterDefStmts of the connector
        //but for native connectors, it is not possible. Hence, user need to specify the index.
        //ex. helloService.myConnector.parameter2 ==> update the 2nd parameter of the connector
        int indexOfMockField = 0;
        Pattern pattern = Pattern.compile("^([0-9]*).+");
        Matcher matcher = pattern.matcher(new StringBuilder(paramName).reverse());
        if (matcher.matches()) {
            String group = new StringBuilder(matcher.group(1)).reverse().toString();
            if (!group.isEmpty()) {
                indexOfMockField = Integer.parseInt(group);
                indexOfMockField--; //user inputs for the parameter indexes begin from 1, but for us it's 0
            }
        }

        LinkedList<String> connectorNamesList = new LinkedList<>(
                Arrays.asList(Arrays.copyOfRange(mockCntrPathArr, 1, mockCntrPathArr.length - 1)));
        return new MockConnectorPath(mockCntrPathString, mockCntrPathArr[0], connectorNamesList, paramName, mockValue,
                indexOfMockField);
    }

    /**
     * This is the parsed model of the user's mockConnectorPath argument.
     */
    protected static class MockConnectorPath {
        String originalString;
        String serviceName;
        LinkedList<String> connectorNames;
        String terminalVarName;
        String mockValue;
        int indexOfMockField = 0;

        MockConnectorPath(String mockCntrPathString, String serviceName, LinkedList<String> connectorNames,
                String terminalVarName, String mockValue, int indexOfMockField) {
            this.originalString = mockCntrPathString;
            this.serviceName = serviceName;
            this.connectorNames = connectorNames;
            this.terminalVarName = terminalVarName;
            this.mockValue = mockValue;
            this.indexOfMockField = indexOfMockField;
        }

        @Override
        public String toString() {
            return originalString;
        }
    }

}

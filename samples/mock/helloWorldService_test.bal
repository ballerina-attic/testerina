package mock;

import ballerina.lang.messages;
import ballerina.mock;
import ballerina.test;
import ballerina.net.http;
import ballerina.lang.system;

function testMain () {
    message response = {};
    message request = {};
    string responseString;

    string myURL = test:startService("helloWorld");
    string mockURL = test:startService("mockService");
    mock:setValue("helloWorld.terminalCon.param1", mockURL);

    http:ClientConnector varEP = create http:ClientConnector(myURL);
    messages:setStringPayload(request, mockURL);
    response = http:ClientConnector.get(varEP, "/", request);
    responseString = messages:getStringPayload(response);
    system:println("hello response: " + responseString);
    test:assertEquals(responseString, "You invoked mockService!");
}
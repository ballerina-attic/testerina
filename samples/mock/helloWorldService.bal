package mock;

import ballerina.lang.messages;
import ballerina.net.http;
import ballerina.lang.system;

@http:BasePath  {value: "/hello"}
service helloWorld {
    http:ClientConnector terminalCon = create http:ClientConnector("http://localhost:8080/original");

    @http:GET{}
    @http:Path {value: "/"}
    resource sayHello(message m) {
        string action1;
        message req = {};
        message response = http:ClientConnector.get(terminalCon, "/", req);
        system:println("response " + messages:getStringPayload(response));
        action1 =  messages:getStringPayload(response);
        messages:setStringPayload(response, action1);
        reply response;
    }
}

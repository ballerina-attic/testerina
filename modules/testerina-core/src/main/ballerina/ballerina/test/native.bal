package ballerina.test;

@Description { value:"Starts the service specified in the 'serviceName' argument" }
@Param {value:"serviceName: Name of the service to start"}
public native function startService (string serviceName) (string);

@Description{ value: "Asserts whether two variables are same in value. JSON and XML are not supported." }
@Param {value:"expected: Expected value."}
@Param {value:"actual: Actual value."}
@Param {value:"message: Assert error message."}
public native function assertEquals(any expected, any actual, string message);
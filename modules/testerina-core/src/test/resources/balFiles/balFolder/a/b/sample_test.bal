package a.b;

import ballerina.test;

function testAddTwoNumbers() {
    test:assertEquals(addTwoNumbers(1,2), 3, "Positive number addition failed");
}
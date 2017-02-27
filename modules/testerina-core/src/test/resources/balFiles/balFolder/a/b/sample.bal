package a.b;

import ballerina.lang.system;

function main (string[] args) {
int i = addTwoNumbers(1, 2);
        system:println("Result: " + i);
                      }

function addTwoNumbers(int a, int b) (int) {
return a + b;
}
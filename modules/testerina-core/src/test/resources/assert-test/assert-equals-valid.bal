import ballerina.test;

function testAssertEqualTrueString() {
    string s1 = "a";
    string s2 = "a";
    string message = "Strings does not match";
    test:assertEquals(s1, s2, message);
}

function testAssertEqualFalseString() {
    string s1 = "a";
    string s2 = "b";
    string message = "Strings does not match";
    test:assertEquals(s1, s2, message);
}

function testAssertEqualTrueInt() {
    int i1 = 5;
    int i2 = 5;
    string message = "Integers does not match";
    test:assertEquals(i1, i2, message);
}

function testAssertEqualFalseInt() {
    int i1 = 5;
    int i2 = 10;
    string message = "Integers does not match";
    test:assertEquals(i1, i2, message);
}

function testAssertEqualTrueFloat() {
    float f1 = 2.0;
    float f2 = 2.0;
    string message = "Floats does not match";
    test:assertEquals(f1, f2, message);
}

function testAssertEqualFalseFloat() {
    float f1 = 2.0;
    float f2 = 5.0;
    string message = "Floats does not match";
    test:assertEquals(f1, f2, message);
}

function testAssertEqualTrueBoolean() {
    boolean b1 = true;
    boolean b2 = true;
    string message = "Booleans does not match";
    test:assertEquals(b1, b2, message);
}

function testAssertEqualFalseBoolean() {
    boolean b1 = true;
    boolean b2 = false;
    string message = "Booleans does not match";
    test:assertEquals(b1, b2, message);
}

function testAssertEqualTrueStringArray() {
    string[] s1 = ["a", "b"];
    string[] s2 = ["a", "b"];
    string message = "Strings arrays does not match";
    test:assertEquals(s1, s2, message);
}

function testAssertEqualFalseStringArray() {
    string[] s1 = ["a", "b"];
    string[] s2 = ["a", "c"];
    string message = "Strings arrays does not match";
    test:assertEquals(s1, s2, message);
}

function testAssertEqualTrueIntArray() {
    int[] i1 = [5, 10];
    int[] i2 = [5, 10];
    string message = "Integers arrays does not match";
    test:assertEquals(i1, i2, message);
}

function testAssertEqualFalseIntArray() {
    int[] i1 = [5, 10];
    int[] i2 = [10, 10];
    string message = "Integers arrays does not match";
    test:assertEquals(i1, i2, message);
}

function testAssertEqualTrueFloatArray() {
    float[] f1 = [2.0, 20.5];
    float[] f2 = [2.0, 20.5];
    string message = "Floats arrays does not match";
    test:assertEquals(f1, f2, message);
}

function testAssertEqualFalseFloatArray() {
    float[] f1 = [2.0, 20.5];
    float[] f2 = [7.5, 20.5];
    string message = "Floats arrays does not match";
    test:assertEquals(f1, f2, message);
}

function testAssertEqualTrueBooleanArray() {
    boolean[] b1 = [true, true];
    boolean[] b2 = [true, true];
    string message = "Booleans arrays does not match";
    test:assertEquals(b1, b2, message);
}

function testAssertEqualFalseBooleanArray() {
    boolean[] b1 = [true, true];
    boolean[] b2 = [true, false];
    string message = "Booleans arrays does not match";
    test:assertEquals(b1, b2, message);
}

// JSONs are same. But assertEquals for Json are not supported, hence this should fail.
function testAssertEqualFalseJSON() {
    json j1 = { name: "apple", color: "red", price: 50.00 };
    json j2 = { name: "apple", color: "red", price: 50.00 };
    string message = "JSONs does not match";
    test:assertEquals(j1, j2, message);
}

// XMLs are same. But assertEquals for XML are not supported, hence this should fail.
function testAssertEqualFalseXML() {
    xml x1 = xml `<book>The Lost World</book>`;
    xml x2 = xml `<book>The Lost World</book>`;
    string message = "XMLs does not match";
    test:assertEquals(x1, x2, message);
}

function testAssertEqualTrueMap() {
    map m1 = {line1:"No. 20", line2:"Palm Grove",
                      city:"Colombo 03", country:"Sri Lanka"};
    map m2 = {line1:"No. 20", line2:"Palm Grove",
                 city:"Colombo 03", country:"Sri Lanka"};
    string message = "Maps does not match";
    test:assertEquals(m1, m2, message);
}

function testAssertEqualFalseMap() {
    map m1 = {line1:"No. 20", line2:"Palm Grove",
                 city:"Colombo 03", country:"Sri Lanka"};
    map m2 = {line1:"No. 25", line2:"Palm Grove",
                 city:"Colombo 03", country:"Sri Lanka"};
    string message = "Maps does not match";
    test:assertEquals(m1, m2, message);
}

struct Person {
    string name;
    int age = -1;
    Person parent;
    string status;
}

function testAssertEqualTrueStruct() {
    Person p1 = {name:"Jack", age:20, status: "single"};
    Person p2 = {name:"Jack", age:20, status: "single"};
    string message = "Structs does not match";
    test:assertEquals(p1, p2, message);
}

function testAssertEqualFalseStruct() {
    Person p1 = {name:"Jack", age:20, status: "single"};
    Person p2 = {name:"Jill", age:20, status: "single"};
    string message = "Structs does not match";
    test:assertEquals(p1, p2, message);
}

function testAssertEqualTrueStructNested() {
    Person guardian1 = {name:"Jack1", age:40, status: "married"};
    Person guardian2 = {name:"Jack1", age:40, status: "married"};
    Person p1 = {name:"Jackson", age:20, status: "single", parent: guardian1};
    Person p2 = {name:"Jackson", age:20, status: "single", parent: guardian2};
    string message = "Nested structs does not match";
    test:assertEquals(p1, p2, message);
}

function testAssertEqualFalseStructNested() {
    Person guardian1 = {name:"Jack1", age:40, status: "married"};
    Person guardian2 = {name:"Jack2", age:40, status: "married"};
    Person p1 = {name:"Jackson", age:20, status: "single", parent: guardian1};
    Person p2 = {name:"Jackson", age:20, status: "single", parent: guardian2};
    string message = "Nested structs does not match";
    test:assertEquals(p1, p2, message);
}


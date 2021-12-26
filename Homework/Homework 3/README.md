# Homework 3


$ mkdir contracts
$ cd contracts
$ npm init

Use the defaults, except for "main" (specify contracts.js), "test" (specify mocha),
and "author" (specify your name). If you make a mistake, you can edit package.json
to correct.

Also, use npm to install different modules that you will need:

$ npm install mocha


A contract is a JavaScript function that takes in one argument
and returns true if the argument meets certain conditions;
otherwise it returns false. (The functions may have an optional
"expected" property for more informative error messages).

isNumber: Returns true if the argument is a number.
isBoolean
isDefined: Returns true if the argument is neither null or undefined.
isString
isNegative
isPositive

any: Accepts any argument.
not: Takes a single contract; returns a contract that returns true
only if the original contract returns false.
or: Takes a variable number of contracts; returns true if any of
the original contracts return true.


To test your code, run `npm test`. Before you move on to the next part, all tests should pass.
When working correctly, make this library available on your system. 
On the command line, change to the 'contracts' directory and type `npm link`.

--------------------------------------------------------------------------------------------

Programming contracts are focused on ensuring that the inputs to a function are correct,
and that the return value is correct. Most importantly, when something does go wrong,
the contract library should be able to identify who is at fault -- the library writer,
or the user of the library.

Contracts.js. It takes in a list of pre-conditions,
a post-condition, and the function itself. If any of the pre-conditions are not met,
throw an exception blaming the caller. If the post-condition is not met, throw an
exception blaming the library. (The pre-conditions and post-condition are specified
using the contracts that you defined in part 1.)


$ cd ..
$ mkdir example
$ npm init
$ npm link contracts

run `node example1.js` to test. The output of this program should exactly match example1.output.

--------------------------------------------------------------------------------------------

The example2.js file contains some contracts involving objects. 
Your output should exactly match example2.output.

In example3.js, we see an alternate approach using objects.
There is a custom contract that uses the 'this' keyword to refer to
the object containing the method. Your output should exactly match example3.output.
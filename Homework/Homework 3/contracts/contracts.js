// NOTE: This library uses non-standard JS features (although widely supported).
// Specifically, it uses Function.name.

function any(v) {
    return true;
  }
  
  function isNumber(v) {
    return !Number.isNaN(v) && typeof v === 'number';
  }
  isNumber.expected = "number";
  
  //true if boolean
  //anything else false
  function isBoolean(bool){
    if (bool === true || bool === false){
        return true;
    }
    else{
        return false;
    }
        
  }
  isBoolean.expected = "boolean";


  //true if defined
  //false if null or undefined
  function isDefined(def){
    if (def === undefined || def === null){
        return false;
    }
    else{
        return true;
    }
  }
  isDefined.expected = "defined";


  //true if is of type string
  //false if not string
  function isString(str){
    if (typeof str === 'string' || str instanceof String){
        return true;
    }
    else{
        return false;
    }
  }
  isString.expected = "string";

  //true if is negative
  //false if is positive or not a number
  function isNegative(neg){
    if (isNaN(neg)){
      return false;
    }
    else {
        if (neg >= 0 || typeof neg === 'boolean')
            return false;
        else
            return true;
    }
  }
  isNegative.expected = "negative number";

  //true if is positive
  //false if not a number or negative
  function isPositive(pos){
    if (isNaN(pos)){
      return false;
    }
    else {
        if (pos <= 0 || typeof pos === 'boolean')
            return false;
        else
            return true;
    }
  }
  isPositive.expected = "positive number";
  
  // Combinators:
  
  function and() {
    let args = Array.prototype.slice.call(arguments);
    let cont = function(v) {
      for (let i in args) {
        if (!args[i].call(this, v)) {
          return false;
        }
      }
      return true;
    }
    cont.expected = expect(args[0]);
    for (let i=1; i<args.length; i++) {
      cont.expected += " and " + expect(args[i]);
    }
    return cont;
  }
  
  //return true if contracts are true
  function or() {
    let args = Array.prototype.slice.call(arguments);
      let cont = function(v) {
          for (let i in args) {
              if (args[i].call(this, v)) {
                  return true;
              }
          }
          return false;
      }
      cont.expected = expect(args[0]);
    for (let i=1; i<args.length; i++) {
      cont.expected += " or " + expect(args[i]);
    }
    return cont;
  };

  //return true if contract return false
  function not(args){
    let cont = function(v) {
        let i = args.call(this, v);
        return !i;
    }
    cont.expected = "not " + args.expected;
    return cont;
  };

  
  
  // Utility function that returns what a given contract expects.
  function expect(f) {
    // For any contract function f, return the "expected" property
    // if it is specified.  (This allows developers to specify what
    // the expected property should be in a more readable form.)
    if (f.expected) {
      return f.expected;
    }
    // If the function name is available, use that.
    if (f.name) {
      return f.name;
    }
    // In case an anonymous contract is specified.
    return "ANONYMOUS CONTRACT";
  }
  
  
  function contract (preList, post, f) {
    let contract = function() {
      //check contracts
      for (let i = 0; i < preList.length; i++) {
        let check = preList[i].call(this, arguments[i]);
        if (!check)
          throw new Error("Contract violation in position " + i + ". Expected " + preList[i].expected + " but received " + arguments[i] + ".  Blame -> Top-level code");
      }
      
      //check expected
      let expResult = f.apply(this, arguments);
      if (!post(expResult)){
        throw new Error("Contract violation. Expected " + post.expected + " but returned " + expResult + ".  Blame -> " + f.name);
      }
    
      return expResult;
    }
    return contract;
  }
  
  
  module.exports = {
    contract: contract,
    any: any,
    isBoolean: isBoolean,
    isDefined: isDefined,
    isNumber: isNumber,
    isPositive: isPositive,
    isNegative: isNegative,
    isInteger: Number.isInteger,
    isString: isString,
    and: and,
    or: or,
    not: not,
  };
  
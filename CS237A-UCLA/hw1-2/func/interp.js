'use strict';


var env = {};
var temp_env = {};

//number.prototype.eval = function(){
//	return this;
//};
Let.prototype.eval = function() {
	var v1 = this.e1.eval();
	env[this.x] = v1;
	
	var v2 = this.e2.eval();
	delete env[this.x];
	return v2;
};

Var.prototype.eval = function() {
	if(isString(this.x)){
		var v =  temp_env[this.x];
		if(v === undefined){
			v = env[this.x];
			if(v === undefined)
				throw new Error("Value " + this.x +" is not defined at this point");
			else
				return v;
		}else
			return v;
	}
	throw new Error("Value" + this.x +" is not defined at this point");
};
Val.prototype.eval = function() {
	return this.primValue;
};

If.prototype.eval = function() {
	var v1 = this.e1.eval();
	var v2 = this.e2.eval();
	var v3 = this.e3.eval();
	//Type Checks
	if(!isBoolean(v1)){
		throw new Error("Expected a boolean value. Non Boolean value provided");
	}
	if(v1)
		return v2;
	else
		return v3;
};

Fun.prototype.eval = function(args) {
	if(args === undefined) {
		var t_env = JSON.parse(JSON.stringify(env));
		return new Closure(this.xs, this.e, t_env);
	}
	if(args.length !== this.xs.length){
		throw new Error("Number of arguments does not match");
	}
	for(var i =0 ; i<args.length ; i++){
		env[this.xs[i]] = args[i].eval();
	}
	var v = this.e.eval();
	for(var i =0 ; i<args.length ; i++){
		delete env[this.xs[i]];
	}
	return v;
};

Closure.prototype.eval = function(args) {
	if(args === undefined) {
		var t_env = JSON.parse(JSON.stringify(env));
		return new Closure(this.xs, this.e, t_env);
	}
	if(args.length !== this.xs.length){
		throw new Error("Number of arguments does not match");
	}
	temp_env = this.env;
	for(var i =0 ; i<args.length ; i++){
		temp_env[this.xs[i]] = args[i].eval();
		env[this.xs[i]] = temp_env[this.xs[i]]
	}

	var v = this.e.eval();
	for(var i =0 ; i<args.length ; i++){
		delete env[this.xs[i]];
	}
	temp_env = {};
	return v;
};

Call.prototype.eval = function(args) {
	if(args !== undefined){

	}
	if(this.ef instanceof Var){
		var f = this.ef.eval();
		return f.eval(this.es);
	}
	return this.ef.eval().eval(this.es);

};

BinOp.prototype.eval = function() {
	var v1 = this.e1.eval();
	var v2 = this.e2.eval();
	switch (this.op) {
	    case "+":
	    	if(isNumber(v1) && isNumber(v2)){
	    		return v1 + v2;
	    	}else{
				throw new Error("+ Operator not available for boolean values");
	    	}
	        break;
	    case "-":
	    	if(isNumber(v1) && isNumber(v2)){
	    		return v1 - v2;
	    	}else{
				throw new Error("- Operator not available for boolean values");
	    	}
	    	break;
	    case "*":
	    	if(isNumber(v1) && isNumber(v2)){
	    		return v1 * v2;
	    	}else{
				throw new Error("* Operator not available for boolean values");
	    	}
	    	break;
	    case "/":
	    	if(isNumber(v1) && isNumber(v2)){
	    		if(v2 === 0){
					throw new Error(" Division by zero ");
	    		}
	    		return v1 / v2;
	    	}else{
				throw new Error("/ Operator not available for boolean values");
	    	}
	    	break;
	    case "%":
	    	if(isNumber(v1) && isNumber(v2)){
	    		if(v2 === 0){
					throw new Error(" Division by zero ");
	    		}
	    		return v1 % v2;
	    	}else{
				throw new Error("/ Operator not available for boolean values");
	    	}
	        break;
	    case "=":
	    	return v1 === v2;
	    	break;
	    case "!=":
	    	return v1 !== v2;
	   	    break;
	    case ">":
	    	if(isNumber(v1) && isNumber(v2)){
	    		return v1 > v2;
	    	}else{
				throw new Error("> Operator not available for boolean values");
	    	}
	        break;
	    case "<":
	        if(isNumber(v1) && isNumber(v2)){
	    		return v1 < v2;
	    	}else{
				throw new Error("< Operator not available for boolean values");
	    	}
	        break;
	    case "&&":
	     	if(isBoolean(v1) && isBoolean(v2)){
	    		return v1 && v2;
	    	}else{
				throw new Error("&& Operator not available for number values");
	    	}
	        break;
	    case "||":
	     	if(isBoolean(v1) && isBoolean(v2)){
	    		return v1 || v2;
	    	}else{
				throw new Error("|| Operator not available for number values");
	    	}
	        break;
	    default:
			throw new Error("UnKnown Binary operator type!");
	}

};

function isNumber(num){
	return "number" === typeof num;
};

function isBoolean(bool){
	return "boolean" === typeof bool;
};

function isString(str){
	return "string" === typeof str;
};

function interp(ast) {
	env = {};
	temp_env = {};
	return ast.eval();
}


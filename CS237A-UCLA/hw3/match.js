'use strict';
var env =[]
var instof = function(){
	var pat_arg  = arguments;
	return function instof(x){
		if( x instanceof pat_arg[0]){
			var args  =  x.deconstruct();
			for(var i =0 ; i<args.length ; i++){
				if(!args[i].match(pat_arg[i+1]) ) {
					return false;
				}
			}
			return true;
		}
	};
};
var pred = function (){
	var pat_arg  = arguments[0];
	return function pred(){
		if (pat_arg.apply(this, arguments)){
			for(var a=0;a<arguments.length;a++) env[env.length-1].push(arguments[a]);
			return true;
		}else{
			return false;
		}
	}
}

String.prototype.match = function(other){
	if(other instanceof WildCard){
		env[env.length-1].push(this.valueOf());
		return true;
	}else if(typeof other !== "string"){
		return false;
	}else if(this.valueOf() === other){
		return true;
	}else{
		return false;
	}
}
Number.prototype.match = function(other){
	if(other instanceof WildCard){
		env[env.length-1].push(this.valueOf());
		return true;
	}else if(typeof other !== "number"){
		return false;
	}
	else if(this.valueOf() === other){
		return true;
	}else{
		return false;
	}
}
Array.prototype.match = function(other) {
	if(! other instanceof Array){
		return false;
	}
	if(this.length !== other.length ){
	  throw new Error('Arguments length Does Not Match');		
	}
	for(var i =0 ; i<this.length ; i++){
				if(!matchValue(this[i],other[i])){
					return false;
				}
	}	
	return true;
};
function matchValue(vl , pat){
	if(vl instanceof Array){
		return vl.match(pat);
	}
	else if(pat.name  === "pred"){
		return pat(vl);
	}
		else if(pat.name === "instof" ){
		return pat(vl)
	}
	else if(typeof vl == "string"){
		return vl.match(pat)
	}
	else if(typeof vl == "number"){
		return vl.match(pat)
	}

	
}

class WildCard {
  constructor() {
  	this.x = "_";
  }
}

var _ = new WildCard();

function match(value /* pat1, fun1, pat2, fun2, ... */) {
	for (var i = 1 ; i< arguments.length; i+=2) {
		env.push([]);
		if(matchValue(value, arguments[i])) {
			var args_list = env[env.length-1]
			if(args_list.length === arguments[i+1].length)
				return arguments[i+1].apply(this, args_list);
			else
				throw new Error('Arguments length Does Not Match');			
		}
		env.pop();
	};

	throw new Error('No Pattern Matched');
}

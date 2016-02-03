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
var pred = function () {
	var pat_arg = arguments[0];
	return function pred() {
		if (pat_arg.apply(this, arguments)) {
			for (var a = 0; a < arguments.length; a++) env[env.length - 1].push(arguments[a]);
			return true;
		} else {
			return false;
		}
	}
}

var many = function (){
    var many_arg = arguments[0];
    return function many(){
        var rounds  = 0 ;
        var init_len = env.length-1;
        var i=0;
        for( i = 0 ; i < arguments[0].length ; i++){
           env.push([]);
            if(!matchValue(arguments[0][i] , many_arg)){
                env.pop();
                break;
            }
            rounds++;
        }
        for(var j=0 ; j<(many_arg.length==0 || many_arg.length === undefined ?1: many_arg.length); j++ ){
            var temp_list = []
         for(var k = init_len+1 ; k<env.length ;  k++ ){
             temp_list.push(env[k][j]);
         }
            env[init_len].push(temp_list);
        }
        env = env.splice(0, init_len+1);
        return i;
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

function containsMany(o){
    for(var i =0 ; i< o.length ; i++){
        if(o[i].name === "many")
        return true
    }
    return false;

}
Array.prototype.match = function(other) {
	if(! other instanceof Array){
		return false;
	}
	if(this.length !== other.length && !containsMany(other)){
	  throw new Error('Arguments length Does Not Match');		
	}
    if(this.length === 0 ){
        if(other.length!==0)return (matchValue(this,other[0])>-1?true:false) ; else return false;
    }
    var j=0;
	for(var i =0 ; i<this.length ; i++){
                if(other[j].name === "many"){
                    var temp = other[j](this.slice(i,this.length))
                    if(temp <0){
                        return false;
                    }else{
                        i=temp+i -1;
                        j++;
                        continue;
                    }
                }
				if(!matchValue(this[i],other[j])){
					return false;
				}
        j++;
	}
    if(j< other.length) return false; else return true;
};
function matchValue(vl , pat){
    if(pat.name === "many"){
      return   pat(vl);
    } else if(vl instanceof Array){
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

var GLOBAL_WILD_MANY = 1;
class WildCard {
  constructor() {
  	this.x = GLOBAL_WILD_MANY ;
      GLOBAL_WILD_MANY++;
  }
}

var _ = new WildCard();

function match(value /* pat1, fun1, pat2, fun2, ... */) {
    env = []
	for (var i = 1 ; i< arguments.length; i+=2) {
		env.push([]);
		if(matchValue(value, arguments[i])) {
			var args_list = env[env.length-1]
			if(args_list.length === arguments[i+1].length)
				return arguments[i+1].apply(this, args_list);
			else {
                throw new Error('Arguments length Does Not Match');
            }
        }
		env.pop();
	};

	throw new Error('No Pattern Matched');
}

'use strict';


/**
 * default classes
 * */
function fixMethodNames(m) {
    switch (m.trim()) {
        case "+" :
            return "plus__";
            break;
        case "-" :
            return "minus__";
            break;
        case "/" :
            return "div__";
            break;
        case "*" :
            return "mul__";
            break;
        case "%" :
            return "mod__";
            break;
        case "==" :
            return "eq__";
            break;
        case "!=" :
            return "noteq__";
            break;
        case ">" :
            return "greaterthan__";
            break;
        case "<" :
            return "lessthan__";
            break;
        default:
            return m + "_";
    }
};

function Obj() {
};
Obj.prototype.init_ = function () {
    return this;
};
Obj.prototype.plus__ = function (x) {
    return this.new( this.x + x.x);
};
Obj.prototype.minus__ = function (x) {
    return this.new( this.x - x.x);
};
Obj.prototype.mul__ = function (x) {
    return this.new( this.x * x.x);
};
Obj.prototype.div__ = function (x) {
    return this.new(  this.x / x.x);
};
Obj.prototype.mod__ = function (x) {
    return this.new( this.x % x.x);
    
};
Obj.prototype.eq__ = function (x) {
    if(this.x === x.x){
        return new True().init_(true)
    }else{
        return new False().init_(false)
    }
};
Obj.prototype.noteq__ = function (x) {
    if(this.x !== x.x){
        return new True().init_(true)
    }else{
        return new False().init_(false)
    }
};
Obj.prototype.greaterthan__ = function (x) {
    if(this.x > x.x){
        return new True().init_(true)
    }else{
        return new False().init_(false)
    }
};
Obj.prototype.lessthan__ = function (x) {
    if(this.x < x.x){
        return new True().init_(true)
    }else{
        return new False().init_(false)
    }};
function Num() {
};
Num.prototype = Object.create(Obj.prototype);
Num.prototype.init_ = function (x) {
    this.x = x;
    return this;
};
Num.prototype.new = function(x){
    return new Num().init_(x)
};


function Str() {
};
Str.prototype = Object.create(Obj.prototype);
Str.prototype.init_ = function (x) {
    this.x = x;
    return this;
};
Str.prototype.new = function(x){
    return new Str().init_(x)
};

function Null() {
};
Null.prototype = Object.create(Obj.prototype);
Null.prototype.init_ = function (x) {
    this.x = x;
    return this;
};
Null.prototype.new = function(x){
    return new Null().init_(x)
};

function Bool() {
};
Bool.prototype = Object.create(Obj.prototype);
Bool.prototype.init_ = function (x) {
    this.x = x;
    return this;
};

function True() {
};
True.prototype = Object.create(Bool.prototype);
True.prototype.init_ = function (x) {
    this.x = x;
    return this;
};
True.prototype.new = function(x){
    return (x) ? new True().init_(x) : new False().init_(x);
};

function False() {
};
False.prototype = Object.create(Bool.prototype);
False.prototype.init_ = function (x) {
    this.x = x;
    return this;
};

False.prototype.new = function(x){
    return (x) ? new True().init_(x) : new False().init_(x);
};

function Block() {
};
Block.prototype = Object.create(Obj.prototype);
Block.prototype.init_ = function (x) {
    this._x = x;
    return this;
};
Block.prototype.setContext_ = function(c ){
    this.c = c;
    return this;
}
Block.prototype.call_ = function () {
    return this._x.apply(this.c, arguments)
};
function Ran(x,y){
    this.x=x;this.y=y;return this;
}

/**
 * Implementation
 */

var classes = ["Obj"];
var methods = ["Obj.init"];
var classfields = {};
var inherit = {};
function findClassDec(name) {
    return classes.indexOf(name) === -1 ? false : true;
};


This.prototype.eval = function () {
    return "this";
};
function findVar(c, x) {
    while (c !== undefined) {
        var clz = classfields[c]
        if (clz.indexOf(x) !== -1) {
            return true;
        }
        c = inherit[c]
    }
    return false;
};
var ar = 0;

BlockLit.prototype.eval = function (set) {
    var fun = "function(" + this.xs.join(",") + "){";
    var foundreturn = false;
    for (var s = 0; s < this.ss.length; s++) {
        if (s === this.ss.length - 1) {
            if (( this.ss[s] instanceof ExpStmt || this.ss[s] instanceof Exp) && !(this.ss[s] instanceof Return)) {
                fun = fun + "throw " + this.ss[s].eval(true) + ";"
                foundreturn = true;
            } else {
                    fun = fun + this.ss[s].eval(true) + ";"
            }
        } else {
            fun = fun + this.ss[s].eval(true) + ";"
        }
        if (this.ss[s] instanceof Return) {
            foundreturn = true;
        }
    }

    if (!foundreturn) {
        fun = fun + "return new Null().init_(null);"
    }
    fun = "new Block().init_(" + fun + "}" + ").setContext_(this)";
    //if(set === undefined){
      //  var localname  =  "tmp_" + name++;
       // fun = "var " +localname +" = "+ fun+ ";";
       // fun  = fun +localname;
    //}
        //  return "new Block().init_(" + fun + ")";
    return fun;
};


InstVar.prototype.eval = function () {
    if (currentClass.length !== 0) {
        if (findVar(currentClass[currentClass.length - 1], this.x)) {
            return "this." + this.x;
        } else {
            throw new Error('Class does not have field' + this.x);
        }
    }
    return "this." + this.x;
};

SuperSend.prototype.eval = function () {
    var parent = inherit[currentClass[currentClass.length - 1]];
    if (parent === undefined) return ""
    else {
        var args = this.es.map(function (x) {
            return x.eval()
        });

        return parent + ".prototype." + fixMethodNames(this.m) + ".call(this" + ((args.length > 0) ? "," + args.toString() : "") + ")"
    }
};

ClassDecl.prototype.eval = function () {
    var clzz = "function " + this.C + "(" + this.xs.toString() + "){};";
    clzz = clzz + this.C + ".prototype=Object.create(" + this.S + ".prototype);";
    classes.push(this.C)
    classfields[this.C] = this.xs;
    inherit[this.C] = this.S;
    return clzz
};

New.prototype.eval = function () {
    var args = "";
    for (var j = 0; j < this.es.length; j++) {
        args = args + this.es[j].eval() + ","
    }
    if (args.endsWith(",")) {
        args = args.substring(0, args.length - 1)
    }
    return "new " + this.C + "().init_(" + args + ")";
};
Send.prototype.eval = function (set) {
    /// Static check if method exists---pending
    var send = this.erecv.eval(set);
    if(this.erecv instanceof BlockLit && set ===undefined){
          var localname  =  "tmp_" + name++;
         send = "var " +localname +" = "+ send+ ";";
         send  = send + localname;
    }
  /*  if(this.m === "call"){
        send = send + "." + "apply(this";
        if(this.es.length>0){
            send = send+",";
            var args = "[";
            for (var j = 0; j < this.es.length; j++) {
                args = args + this.es[j].eval(set) + ","
            }
            if (args.endsWith(",")) {
                args = args.substring(0, args.length - 1)
            }
            send = send + args + "])";
        }else{
            send = send + ")";
        }

    }else {
    */    send = send + "." + fixMethodNames(this.m) + "(";
        var args = "";
        for (var j = 0; j < this.es.length; j++) {
            args = args + this.es[j].eval(set) + ","
        }
        if (args.endsWith(",")) {
            args = args.substring(0, args.length - 1)
        }
        send = send + args + ")";

    return send;
};

Return.prototype.eval = function (set, vale) {
    if(set !== true)return "return " + this.e.eval();
    else return "throw " +  "new Ran(" + this.e.eval() + ", ar__)";
};

InstVarAssign.prototype.eval = function () {
    return "this." + this.x + " = " + this.e.eval()
};


MethodDecl.prototype.eval = function () {
    var method = "";
    if (findClassDec(this.C)) {
        currentClass.push(this.C)
        //if(this.m === "init") {
        //  method  = "function " + this.C + "("+this.xs.toString()+"){";

//        }else{
        method = this.C + ".prototype." + fixMethodNames(this.m) + "=function(" + this.xs.toString() +
            "){ var ar__ = ar++ ; try{";
        //      }
        var foundreturn = false;
        for (var s = 0; s < this.ss.length; s++) {
            if (this.ss[s] instanceof Return) {
                foundreturn = true
            }
            method = method + this.ss[s].eval(this.C) + ";"
        }
        if (!foundreturn) {
            method = method + "return this;"
        }
        method = method + " } catch(err){ " +
            "if(err instanceof Ran){ " +
            "   if(err.y === ar__ ){" +
            "   return err.x}else{" +
            "                   throw err}" +
            "}" +
            "throw err;}" +
            "};";
        methods.push(this.C + "." + fixMethodNames(this.m))
        currentClass.pop()

        return method
    } else {
        throw new Error('Class def not found' + name);
    }
};

Var.prototype.eval = function () {
    return this.x;
};

VarDecl.prototype.eval = function () {
    return "var " + this.x + " = " + this.e.eval();
};

Lit.prototype.eval = function () {
    if (typeof this.primValue === "string") {
        return "new Str().init_(" + "\"" + this.primValue + "\"" + ")";
    }
    else if (typeof this.primValue === "number") {
        return "new Num().init_(" + this.primValue + ")";
    }
    else if (typeof this.primValue === "boolean") {
        if (this.primValue) {
            return "new True().init_(" + this.primValue + ")";

        } else {
            return "new False().init_(" + this.primValue + ")";
        }
        return "new Bool().init_(" + this.primValue + ")";
    } else if (this.primValue == undefined) {
        return "new Null().init_(" + "null" + ")";
    }
    return this.primValue
};

BinOp.prototype.eval = function () {
//    return "(" + this.e1.eval() + this.op + this.e2.eval() + ")";
    var c = new Send(this.e1, this.op, [this.e2]);
    return c.eval()
};
var name = 0
ExpStmt.prototype.eval = function (set) {
    if (set === undefined) {
        var localname = "tmp_" + name++;
        var str = "";
        str = "var " + localname + " = " + this.e.eval(true) + ";";
        str = str + "if(" + localname + " === undefined){" + localname + "=" + "null" + "}";
        str = str + "if(" + localname + " instanceof Num ||" + localname + " instanceof Str ||" + localname + " instanceof True ||" + localname + " instanceof False || " + localname + " instanceof Null){" + localname + "=" + localname + ".x;};"
        return str + localname
    } else {
        return this.e.eval();
    }
};


Program.prototype.eval = function () {
    var prg = "";

    for (var s = 0; s < this.ss.length; s++) {
        prg = prg + this.ss[s].eval() + ";"
    }
    if (!(this.ss[this.ss.length - 1] instanceof ExpStmt)) {
        prg = prg + "null;"
    }
    return prg
};
var currentClass = []

function trans(ast) {
    currentClass = []
    classfields = {};
    inherit = {};
    classes = ["Obj", "Num", "Str", "Null", "Bool", "False", "True"];
    methods = ["Obj.init_"];
    var str = "try{" + ast.eval() + "} catch(err){"+
     "if(err === undefined){null}" +
        "if(err instanceof Num || err instanceof Str || err instanceof True || err instanceof False || err instanceof Null){err.x;}};"

    ;
    //  console.log(str);
    var res = str;
    if (res === undefined) {
        return "null"
    }
    else if (typeof res === "string") {
        return res
    } else {
        return res.toString()
    }
    throw new TODO('implement translation for ' + ast.constructor.name);
};

try {
    True.prototype.thenElse_ = function(tb, fb) {
        var ar__ = ar++;
        try {
            return tb.call_();
        } catch (err) {
            if (err instanceof Ran) {
                if (err.y === ar__) {
                    return err.x
                } else {
                    throw err
                }
            }
            throw err;
        }
    };;
    False.prototype.thenElse_ = function(tb, fb) {
        var ar__ = ar++;
        try {
            return fb.call_();
        } catch (err) {
            if (err instanceof Ran) {
                if (err.y === ar__) {
                    return err.x
                } else {
                    throw err
                }
            }
            throw err;
        }
    };;
    Num.prototype.fact_ = function() {
        var ar__ = ar++;
        try {
            this.eq__(new Num().init_(0)).thenElse_(new Block().init_(function() {
                throw new Ran(new Num().init_(1), ar__);
            }).setContext_(this), new Block().init_(function() {
                throw new Ran(this.mul__(this.minus__(new Num().init_(1)).fact_()), ar__);
            }).setContext_(this));
            return this;
        } catch (err) {
            if (err instanceof Ran) {
                if (err.y === ar__) {
                    return err.x
                } else {
                    throw err
                }
            }
            throw err;
        }
    };;
    var tmp_22 = new Num().init_(5).fact_();
    if (tmp_22 === undefined) {
        tmp_22 = null
    }
    if (tmp_22 instanceof Num || tmp_22 instanceof Str || tmp_22 instanceof True || tmp_22 instanceof False || tmp_22 instanceof Null) {
        tmp_22 = tmp_22.x;
    };
    tmp_22;
} catch (err) {
    if (err === undefined) {
        null
    }
    if (err instanceof Num || err instanceof Str || err instanceof True || err instanceof False || err instanceof Null) {
        err.x;
    }
};

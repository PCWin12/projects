'use strict';


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

Send.prototype.eval = function () {
    /// Static check if method exists---pending
    var send = this.erecv.eval();
    send = send + "." + fixMethodNames(this.m) + "(";
    var args = "";
    for (var j = 0; j < this.es.length; j++) {
        args = args + this.es[j].eval() + ","
    }
    if (args.endsWith(",")) {
        args = args.substring(0, args.length - 1)
    }
    send = send + args + ")";
    return send;
};


Return.prototype.eval = function () {
    return "return " + this.e.eval();
};

InstVarAssign.prototype.eval = function () {
    return "this." + this.x + " = " + this.e.eval()
};

function fixMethodNames(m) {
    return m + "_";
};

MethodDecl.prototype.eval = function () {
    var method = "";
    if (findClassDec(this.C)) {
        currentClass.push(this.C)
        //if(this.m === "init") {
        //  method  = "function " + this.C + "("+this.xs.toString()+"){";

//        }else{
        method = this.C + ".prototype." + fixMethodNames(this.m) + "=function(" + this.xs.toString() +
            "){";
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
        method = method + "};";
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
        return "\"" + this.primValue + "\"";
    }
    return this.primValue
};

BinOp.prototype.eval = function () {
    return "(" + this.e1.eval() + this.op + this.e2.eval() + ")";
};

ExpStmt.prototype.eval = function () {
    return this.e.eval()
};

Program.prototype.eval = function () {
    var prg = "function Obj(){};Obj.prototype.init_ = function(){return this;};"
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
    classes = ["Obj"];
    methods = ["Obj.init"];
    var str = ast.eval();
    console.log(str);
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


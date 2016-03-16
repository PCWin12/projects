// -----------------------------------------------------------------------------
// Part I: Rule.prototype.makeCopyWithFreshVarNames() and
//         {Clause, Var}.prototype.rewrite(subst)
// -----------------------------------------------------------------------------


Var.prototype.eval = function (sb) {
    var t = sb.lookup(this.name);
    if (t == undefined) {
        throw  Error("Variable not defined");
    } else {
        if (!(t instanceof Num))
            throw  Error("Non Number type");
        else return t.eval();
    }
};
Num.prototype.eval = function () {
    return this;
};
var current = 0;
Var.prototype.freshnames = function () {
    return new Var(this.name + "_" + current)
};
Num.prototype.freshnames = function () {
    return this;
};
Str.prototype.freshnames = function () {
    return this;
};
Clause.prototype.freshnames = function () {
    var temp_args = [];
    for (var i = 0; i < this.args.length; i++) {
        temp_args.push(this.args[i].freshnames());
    }
    return new Clause(this.name, temp_args)
};

Comp.prototype.freshnames = function () {
    return new Comp(this.op, this.e1.freshnames(), this.e2.freshnames());
};

Rule.prototype.makeCopyWithFreshVarNames = function () {
    current++;
    var rule = new Rule();
    rule.head = this.head.freshnames();
    var temp_body = [];
    for (var i = 0; i < this.body.length; i++) {
        temp_body.push(this.body[i].freshnames());
    }
    rule.body = temp_body;
    return rule;
};
Num.prototype.rewrite = function (subst, name) {
    return this;
};
Clause.prototype.rewrite = function (subst, name) {
    var temp = [];
    for (var i = 0; i < this.args.length; i++) {
        //this.args[i]
        temp[i] = this.args[i].rewrite(subst, name);
    }
    return new Clause(this.name, temp);
};

Var.prototype.rewrite = function (subst, name) {
    var result = subst.lookup(this.name);
    if (result instanceof Clause) {
        if (inList(name, result.args)) {
            throw Error("occurs check")
        }
    }
    if (result === undefined) {
        return new Var(this.name);
    } else {
        return result;
    }
};

// -----------------------------------------------------------------------------
// Part II: Subst.prototype.unify(term1, term2)
// -----------------------------------------------------------------------------

function inList(a, list) {
    if (a === undefined)return false;
    for (var i = 0; i < list.length; i++) {
        if (a === list[i].name && list[i] instanceof Var) {
            return true;
        }
    }
    return false;
}

Subst.prototype.unify = function (term1, term2) {
    if (term1 instanceof Add) term1 = term1.eval(this);
    if (term2 instanceof Add) term2 = term2.eval(this);

    if (term1 instanceof Var && term2 instanceof Clause) {
        if (this.lookup(term1.name) != undefined) {
            var temp = this.lookup(term1.name)
            this.unify(temp, term2)
        } else {
            if (inList(term1.name, term2.args)) {
                throw Error("Occurs Check")
            }
            this.bind(term1.name, term2)
            // return this;
        }
    } else if (term2 instanceof Var && term1 instanceof Clause) {
        if (this.lookup(term2.name) != undefined) {
            var temp = this.lookup(term2.name)
            this.unify(temp, term1)
        } else {
            if (inList(term2.name, term1.args)) {
                throw Error("Occurs Check")
            }
            this.bind(term2.name, term1)
            // return this;
        }
    }
    else if (term2 instanceof Var && term1 instanceof Var) {
        var var1 = this.lookup(term1.name);
        var var2 = this.lookup(term2.name);
        if (var1 === undefined && var2 !== undefined) {
            this.bind(term1.name, var2)
        } else if (var2 === undefined && var1 !== undefined) {
            this.bind(term2.name, var1)
        } else if (var2 !== undefined && var1 !== undefined) {
            if (term1.name === term2.name) {
                //return this;
            } else {
                this.unify(var1, var2)
                //       this.bind(term1.name , term2)
            }
        } else {
            this.bind(term1.name, term2)
        }
    } else if (term2 instanceof Clause && term1 instanceof Clause) {
        if (term1.name === term2.name && term1.args.length === term2.args.length) {
            for (var i = 0; i < term1.args.length; i++) {
                this.unify(term1.args[i], term2.args[i])
            }
        }
        else {
            throw Error("unification error")
        }
    } else if (term1 instanceof Num) {
        if (term2 instanceof Num) {
            if (term1.x !== term2.x)  throw Error("unification error")
        }
        else if (term2 instanceof Var) {
            var temp = this.lookup(term2.name);
            if (temp === undefined) {
                this.bind(term2.name, term1)
            } else {
                if (temp !== term1.x) {
                    this.unify(temp, term1)
                }
            }
        } else if (term2 instanceof Clause) {
            throw Error("unification error")
        }

    } else if (term2 instanceof Num) {
        if (term1 instanceof Num) {
            if (term2.x !== term1.x)  throw Error("unification error")
        }
        else if (term1 instanceof Var) {
            var temp = this.lookup(term1.name);
            if (temp === undefined) {
                this.bind(term1.name, term2)
            } else {
                this.unify(temp, term2)
            }
        } else if (term1 instanceof Clause) {
            throw Error("unification error")
        }

    }

    for (var varName in this.bindings) {
        var temp = this.lookup(varName).rewrite(this, varName)
        if (temp instanceof Clause) {
            if (inList(varName, temp.args)) {
                throw Error("occurs check")
            }
        }
        // var t = this.lookup(varName).rewrite(this);
        if (temp.name === varName) {
            delete this.bindings[temp.name];
        } else {
            this.bind(varName, temp);
        }
    }
    return this;
    // throw new TODO('Subst.prototype.unify not implemented');
};

Comp.prototype.unify = function (sb) {
    var temp = this.e1.eval(sb);
    if (!operation[this.op](temp.x, this.e2.eval(sb).x)) {
        throw Error("unification error");
    }
};


function unify(cl, sb) {
    var temp = sb.lookup(cl.args[0].name)
    if (temp === undefined) {
        sb.bind(cl.args[0].name, cl.args[1].eval(sb))
    } else {
        if (cl.args[0].eval(sb).x !== temp.x) {
            throw Error("unification error")
        }
    }
    for (var varName in sb.bindings) {
        var temp = sb.lookup(varName).rewrite(sb, varName)
        if (temp.name === varName) {
            delete sb.bindings[temp.name];
        } else {
            sb.bind(varName, temp);
        }
    }
}

// -----------------------------------------------------------------------------
// Part III: Program.prototype.solve()
// -----------------------------------------------------------------------------


function Iterator(rules, queries) {
    this.rules = rules;
    this.queries = queries;
    this.subst = [];
    this.stack = [];
    this.goal = this.queries.slice().reverse()
    this.output_String = "";

}
Iterator.prototype.toOutput = function(goal, sbst){
    var temp = "";
    for(var i =0 ; i < goal.args.length ; i++){
        if(goal.args[i] instanceof Var){
            var a = goal.args[i].name;
            temp = temp + sbst.lookup(goal.args[i].name)+ "  "
        }else if(goal.args[i] instanceof Clause){
            temp = temp + goal.args[i].name +"  ";
        }else if(goal.args[i] instanceof Str){
         temp = temp + goal.args[i].name +"  ";
        }
    }
    this.output_String = this.output_String + temp+"\n";
    wind_output.setValue(this.output_String);
};
Iterator.prototype.next = function (sbst) {
    var current_goal, rule_look = 0, current_subst = new Subst();
    if (sbst !== undefined) {
        current_subst = sbst
    }

    // get the solving goal

    if (this.goal.length == 0) {
        if (this.stack.length == 0) {
            if (sbst !== undefined)
                return current_subst;
            else {
                return false
            }
        }
        current_goal = this.stack.pop();
        rule_look = current_goal[2];
        current_subst = current_goal[1];
        this.goal = current_goal[0].slice(1);
        current_goal = current_goal[0][0];
    } else {
        current_goal = this.goal.pop();
    }

    if(wind_output!== undefined && current_goal.name === "write") {
        this.toOutput(current_goal,current_subst.clone())
        if(this.goal.length === 0){
            return current_subst
        }
        return this.next(current_subst);
    }

    if (current_goal.name === "is") {
        try {
            unify(current_goal, current_subst);
            if (this.goal.length === 0) {
                return current_subst;
            }
            return this.next(current_subst)
        } catch (e) {
            if (this.stack.length !== 0) {
                this.goal = [];
                return this.next();
            }
            return false;
        }
    } else if (current_goal.name === "!") {
        this.stack = [];
        return this.next(current_subst);
    } else if (current_goal.name === "~") {
        //this.goal = this.goal.concat(current_goal.args[0]);
        var iter = new Iterator(this.rules , [current_goal.args[0]]);
        var sol = iter.next(current_subst.clone());
        if (sol) {
            if (this.stack.length !== 0) {
                this.goal = [];
                return this.next();
            }
            return false;
        } else {
            if (this.goal.length === 0) {
                return current_subst;
            }
            return this.next(current_subst);
        }
    }
    if (current_goal instanceof Comp) {
        try {
            current_goal.unify(current_subst);
            if (this.goal.length === 0) {
                return current_subst;
            }
            return this.next(current_subst)
        } catch (e) {
            if (this.stack.length !== 0) {
                this.goal = [];
                return this.next();
            }
            return false;
        }
    }
    for (var i = rule_look; i < this.rules.length; i++) {
        if (current_goal.name === this.rules[i].head.name && current_goal.args.length === this.rules[i].head.args.length) {
            this.stack.push([[current_goal].concat(this.goal), current_subst.clone(), i + 1]);
            this.rules[i] = this.rules[i].makeCopyWithFreshVarNames();
            var temp_sbst = current_subst.clone();
            try {
                current_subst.unify(current_goal, this.rules[i].head);
                if (this.rules[i].body.length === 0 && this.goal.length === 0) {
                    return current_subst;
                }
                this.goal = this.goal.concat(this.rules[i].body.slice().reverse());
                return this.next(current_subst);
            } catch (e) {
                current_subst = temp_sbst;
                this.stack.pop();
            }
        }
    }

    if (this.stack.length !== 0) {
        this.goal = [];
        return this.next();
    }
    if (this.goal.length !== 0) {
        return false
    }
    return false;
    // check if backtracking or not
};
var wind_output =
Program.prototype.solve = function (out) {
    wind_output = out;
    if(out !==undefined)wind_output.setValue('');
//  throw new TODO('Program.prototype.solve not implemented');
    return new Iterator(this.rules, this.query)

};


///Plumbing

var operation = {};
operation['>'] = function (a, b) {
    return a > b;
};
operation['<'] = function (a, b) {
    return a < b;
};
operation['=='] = function (a, b) {
    return a === b;
};
operation['!='] = function (a, b) {
    return a !== b;
};

Add.prototype.freshnames = function () {
    return new Add(this.e1.freshnames(), this.e2.freshnames())
};

Mul.prototype.freshnames = function () {
    return new Mul(this.e1.freshnames(), this.e2.freshnames())
};

Sub.prototype.freshnames = function () {
    return new Sub(this.e1.freshnames(), this.e2.freshnames())
};

Div.prototype.freshnames = function () {
    return new Div(this.e1.freshnames(), this.e2.freshnames())
};
Mod.prototype.freshnames = function () {
    return new Mod(this.e1.freshnames(), this.e2.freshnames())
};

Pow.prototype.freshnames = function () {
    return new Pow(this.e1.freshnames(), this.e2.freshnames())
};


Mod.prototype.eval = function (sb) {
    return new Num(this.e1.eval(sb).x % this.e2.eval(sb).x);
};
Pow.prototype.eval = function (sb) {
    return new Num(Math.pow(this.e1.eval(sb).x, this.e2.eval(sb).x));
};

Add.prototype.eval = function (sb) {
    return new Num(this.e1.eval(sb).x + this.e2.eval(sb).x);
};

Sub.prototype.eval = function (sb) {
    return new Num(this.e1.eval(sb).x - this.e2.eval(sb).x);
};

Mul.prototype.eval = function (sb) {
    return new Num(this.e1.eval(sb).x * this.e2.eval(sb).x);
};

Div.prototype.eval = function (sb) {
    return new Num(this.e1.eval(sb).x / this.e2.eval(sb).x);
};
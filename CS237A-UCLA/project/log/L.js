// The parser

var g = ohm.grammarFromScriptElement();
var L = new Language(g, g.semantics().addOperation('toAST', {

  Program: function(rules, query) {
    return new Program(rules.toAST(), query.toAST());
  },

  Rule_body: function(head, _, body, _) {
    return new Rule(head.toAST(), body.toAST());
  },

  Rule_noBody: function(head, _) {
    return new Rule(head.toAST(), []);
  },

  Query: function(c, _) {
    return c.toAST();
  },
  Clause_binop: function(as,sym,as1) {
    return new Comp(sym.toAST(), as.toAST(), as1.toAST() );
  },
  Clause_infix: function(sym, _, as, _ , as1, _) {
    return new Clause(sym.toAST(), [as.toAST(), as1.toAST()]);
  },
  Clause_infixis : function(as, _, as1) {
    return new Clause("is",[ as.toAST(), as1.toAST()]);
  },
  Clause_arth: function(sym, _, as, _ , as1, _) {
    var sym = sym.toAST();
    if(['+','-', '/','*' , '%' , '^'].indexOf(sym) !== -1){
      switch(sym){
        case '+':
          return new Add(as.toAST() , as1.toAST());
        case '-':
          return new Sub(as.toAST() , as1.toAST());
        case '*':
          return new Mul(as.toAST() , as1.toAST());
        case '/':
          return new Div(as.toAST() , as1.toAST());
        case '%':
          return new Mod(as.toAST() , as1.toAST());
        case '^':
          return new Pow(as.toAST() , as1.toAST());
      }
    }
    return new Clause(sym.toAST(), [as.toAST(), as1.toAST()]);
  },
  Clause_args: function(sym, _, as, _) {
    return new Clause(sym.toAST(), as.toAST());
  },
  Clause_neg: function(sym, _, as, _) {
    return new Clause(sym.toAST(), [as.toAST()]);
  },
  Clause_cut: function(sym) {
    return new Clause(sym.toAST(), []);
  },
  Clause_noArgs: function(sym) {
    return new Clause(sym.toAST(), []);
  },

  List: function(_, xs, _) {
    return xs.toAST()[0] || new Clause('_nil', []);
  },

  Contents_cons1: function(x, _, xs) {
    return new Clause('_cons', [x.toAST(), xs.toAST()]);
  },

  Contents_cons2:  function(x, _, xs) {
    return new Clause('_cons', [x.toAST(), xs.toAST()]);
  },

  Contents_single: function(x) {
    return new Clause('_cons', [x.toAST(), new Clause('_nil', [])]);
  },

  variable: function(_, _) {
    return new Var(this.interval.contents);
  },
  AddExpr_mod: function(x, _, y) {
    return new Mod(x.toAST(), y.toAST());
  },
  AddExpr_plus: function(x, _, y) {
    return new Add(x.toAST(), y.toAST());
  },

  AddExpr_minus: function(x, _, y) {
    return new Sub(x.toAST(), y.toAST());
  },

  MulExpr_times: function(x, _, y) {
    return new Mul(x.toAST(), y.toAST());
  },

  MulExpr_divide: function(x, _, y) {
    return new Div(x.toAST(), y.toAST());
  },

  ExpExpr_exp: function(x, _, y) {
    return new Pow(x.toAST(), y.toAST());
  },

  PriExpr_paren: function(_op, e, _cp) {
    return e.toAST();
  },
  number: function(_) {
    return new Num(parseFloat(this.interval.contents));
  },
  symbol: function(_, _) {
    return this.interval.contents;
  },

  NonemptyListOf: function(x, _, xs) {
    return [x.toAST()].concat(xs.toAST());
  },

  EmptyListOf: function() {
    return [];
  }

}));

// L.evalAST is declared in classes.js
// L.prettyPrintAST and L.prettyPrintValue are declared in prettyPrint.js


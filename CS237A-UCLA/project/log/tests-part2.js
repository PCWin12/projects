tests(
  JS,
  {
    name: 'unify(Var, Clause)',
    code: 'new Subst().unify(new Var("X"),\n' +
          '                  new Clause("foo", []));',
    expected: new Subst().bind("X", new Clause("foo", []))
  },
  {
    name: 'unify(Clause, Var)',
    code: 'new Subst().unify(new Clause("foo", []),\n' +
          '                  new Var("X"));',
    expected: new Subst().bind("X", new Clause("foo", []))
  },
  {
    name: 'unify(Var, Var)',
    code: 'var s = new Subst().unify(new Var("X"),\n' +
          '                          new Var("Y"));\n' +
          'var ans1 = new Subst().bind("X", new Var("Y"));\n' +
          'var ans2 = new Subst().bind("Y", new Var("X"));\n' +
          '__equals__(s, ans1) || __equals__(s, ans2);',
    expected: true
  },
  {
    name: 'unify(Clause, Clause) (1/5)',
    code: 'new Subst().unify(new Clause("foo", []),\n' +
          '                  new Clause("foo", []));',
    expected: new Subst()
  },
  {
    name: 'unify(Clause, Clause) (2/5)',
    code: 'new Subst().unify(new Clause("foo", []),\n' +
          '                  new Clause("bar", []));',
    shouldThrow: true
  },
  {
    name: 'unify(Clause, Clause) (3/5)',
    code: 'new Subst().unify(new Clause("foo", [new Var("X")]),\n' +
          '                  new Clause("foo", [new Clause("bar", [new Clause("baz", [])])]));',
    expected: new Subst().bind("X", new Clause("bar", [new Clause("baz", [])]))
  },
  {
    name: 'unify(Clause, Clause) (4/5)',
    code: 'new Subst().unify(new Clause("foo", [new Var("X"), new Clause("baz", [])]),\n' +
          '                  new Clause("foo", [new Clause("bar", []), new Var("Y")]));',
    expected: new Subst().bind("X", new Clause("bar", []))
                         .bind("Y", new Clause("baz", []))
  },
  {
    name: 'unify(Clause, Clause) (5/5)',
    code: 'new Subst().unify(new Clause("f", [new Var("X"), new Var("Y")]),\n' +
          '                  new Clause("f", [new Clause("a", []), new Var("X")]));',
    expected: new Subst().bind("X", new Clause("a", []))
                         .bind("Y", new Clause("a", []))
  },
    {
        name: 'unify Occurs Check',
        code: 'new Subst().unify(new Clause("data", [new Var("Z"), new Var("X"), new Clause("s", [new Var("X"), new Var("Z")])]) , new Clause("data", [new Var("T"), new Var("Y"), new Var("Y")]));\n',
        shouldThrow: true
    }
    ,
    {
        name: 'Resolve',
        code: 'new Subst().unify( new Clause("foo", [new Clause("bar", []), new Var("Y"), new Var("Y")]) , new Clause("foo", [new Var("X"), new Clause("baz", [new Var("X")]), new Var("Z")]));\n',
        expected: new Subst().bind("X", new Clause("bar", []))
            .bind("Y", new Clause("baz", [new Clause("bar", [])])).bind("Z", new Clause("baz", [new Clause("bar", [])]))
    },
    {
        name: 'Resolve 2',
        code: 'new Subst().unify( new Clause("foo", [new Var("X"),new Var("Y"),new Var("Z")]) , new Clause("foo", [new Var("Y"),new Var("Z"),new Var("X")] ));\n',
        expected: new Subst().bind("X", new Var("Z")).bind("Y", new Var("Z"))
    },
    {
        name: 'conflict 1',
        code: 'new Subst().unify(new Clause("foo", [new Var("X"),          new Clause("baz", [])]),new Clause("foo", [new Clause("bar", []), new Var("X")]));',
        shouldThrow: true
    },
    {
        name: 'unify Occurs Check 2',
        code: 'new Subst().unify(new Clause("data", [new Clause("foo", [new Var("X")]) , new Clause("foo", [new Var("Y")])]), \n'+
        '                 new Clause("data",[new Var("Y") , new Var("X") ]) );\n',
        shouldThrow: true
    },
    {
        name: 'unify check EQ',
        code: 'new Subst().unify(new Clause("foo", [new Clause("z", []) ,\n'+
        'new Var("X") , new Var("X")]),\n'+
        ' new Clause("foo",[new Var("Y") ,\n'+
        'new Clause("s", [new Clause("s", [new Clause("z", [])])])]))\n',
        shouldThrow: true
    }
);

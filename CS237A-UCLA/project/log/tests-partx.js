/**
 * Created by malig on 3/13/16.
 */
tests(
    L,
    {
        name: 'duh (1/3)',
        code: 'p.\n' +
        'p?',
        expected: makeIterator({})
    },
    {
        name: 'duh (2/3)',
        code: 'p.\n' +
        'q?',
        expected: makeIterator()
    },
    {
        name: 'duh (3/3)',
        code: 'p.\n' +
        'q.\n' +
        'p, q?',
        expected: makeIterator({})
    },
    {
        name: 'alice and bob are people',
        code: 'person(1).\n' +
        'person(2).\n' +
        'person(X)?',
        expected: makeIterator(
            { X: new Num(1) },
            { X: new Num(2) }
        )
    },
    {
        name: 'sick and tired',
        code: 'sick(3).\n' +
        'sick(4).\n' +
        'sick(5).\n' +
        'tired(2).\n' +
        'tired(3).\n' +
        'sick(X), tired(X)?',
        expected: makeIterator(
            { X: new Num(3) }
        )
    },
    {
        name: "prereqs",
        code: 'prereq(131, 1371).\n' +
        'prereq(1371, 137).\n' +
        'prereqTrans(X, Y) :- prereq(X, Y).\n' +
        'prereqTrans(X, Y) :- prereq(X, Z), prereqTrans(Z, Y).\n' +
        'prereqTrans(P, 137)?',
        expected: makeIterator(
            { P: new Num(1371) },
            { P: new Num(131) }
        )
    },
    {
        name: 'nats',
        code: 'nat(1).\n' +
        'nat(s(X)) :- nat(X).\n' +
        'nat(X)?',
        expected: makeIterator(
            { X: new Num(1) },
            { X: new Clause("s", [new Num(1)]) },
            { X: new Clause("s", [new Clause("s", [new Num(1)])]) },
            { X: new Clause("s", [new Clause("s", [new Clause("s", [new Num(1)])])]) },
            { X: new Clause("s", [new Clause("s", [new Clause("s", [new Clause("s", [new Num(1)])])])]) }
        )
    },
    {
        name: 'evens',
        code: 'nat(23).\n' +
        'nat(s(X)) :- nat(X).\n' +
        'even(23).\n' +
        'even(s(s(X))) :- even(X).\n' +
        'even(X)?',
        expected: makeIterator(
            { X: new Num(23)},
            { X: new Clause("s", [new Clause("s", [new Num(23)])]) },
            { X: new Clause("s", [new Clause("s", [new Clause("s", [new Clause("s", [new Num(23)])])])]) },
            { X: new Clause("s", [new Clause("s", [new Clause("s", [new Clause("s", [new Clause("s", [new Clause("s", [new Num(23)])])])])])]) },
            { X: new Clause("s", [new Clause("s", [new Clause("s", [new Clause("s", [new Clause("s", [new Clause("s", [new Clause("s", [new Clause("s", [new Num(23)])])])])])])])]) }
        )
    },
    {
        name: 'plus (1/3)',
        code: 'plus(2, X, X).\n' +
        'plus(s(X), Y, s(Z)) :- plus(X, Y, Z).\n' +
        'plus(s(2), s(s(2)), X)?\n',
        expected: makeIterator(
            { X: new Clause("s", [new Clause("s", [new Clause("s", [new Num(2)])])]) }
        )
    },
    {
        name: 'plus (2/3)',
        code: 'plus(z, X, X).\n' +
        'plus(s(X), Y, s(Z)) :- plus(X, Y, Z).\n' +
        'plus(X, s(s(z)), s(s(s(z))))?\n',
        expected: makeIterator(
            { X: new Clause("s", [new Clause("z", [])]) }
        )
    },
    {
        name: 'plus (3/3)',
        code: 'plus(z, X, X).\n' +
        'plus(s(X), Y, s(Z)) :- plus(X, Y, Z).\n' +
        'plus(s(z), X, s(s(s(z))))?\n',
        expected: makeIterator(
            { X: new Clause("s", [new Clause("s", [new Clause("z", [])])]) }
        )
    },
    {
        name: 'cons and car',
        code: 'car([X|Y], X).\n' +
        'car([11,22,33], X)?',
        expected: makeIterator(
            { X: new Num(11) }
        )
    },
    {
        name: 'length',
        code: 'length([], z).\n' +
        'length([H|T], s(LT)) :- length(T, LT).\n' +
        'length([a,b,c], X)?',
        expected: makeIterator(
            { X: new Clause("s", [new Clause("s", [new Clause("s", [new Clause("z", [])])])]) }
        )
    },
    {
        name: 'append (1/3)',
        code: 'append([], L, L).\n' +
        'append([H|T], L2, [H|L3]) :- append(T, L2, L3).\n' +
        'append([1, 2], [3, 4], X)?',
        expected: makeIterator(
            { X: new Clause("_cons", [new Num(1),
                new Clause("_cons", [new Num(2),
                    new Clause("_cons", [new Num(3),
                        new Clause("_cons", [new Num(4), new Clause("_nil", [])])])])]) })
    },
    {
        name: 'append (2/3)',
        code: 'append([], L, L).\n' +
        'append([H|T], L2, [H|L3]) :- append(T, L2, L3).\n' +
        'append(X, [4, 5], [1, 2, 4, 5])?',
        expected: makeIterator(
            { X: new Clause("_cons", [new Num(1),
                new Clause("_cons", [new Num(2), new Clause("_nil", [])])]) })
    },
    {
        name: 'append (3/3)',
        code: 'append([], L, L).\n' +
        'append([H|T], L2, [H|L3]) :- append(T, L2, L3).\n' +
        'append([1, 2], X, [1, 2, 3, 4])?',
        expected: makeIterator(
            { X: new Clause("_cons", [new Num(3),
                new Clause("_cons", [new Num(4), new Clause("_nil", [])])]) })
    },
    {
        name: "homer's children",
        code: 'father(homer, bart).\n' +
        'father(homer, lisa).\n' +
        'father(homer, maggie).\n' +
        'parent(X, Y) :- father(X, Y).\n' +
        'parent(homer, Y)?',
        expected: makeIterator(
            { Y: new Clause("bart", []) },
            { Y: new Clause("lisa", []) },
            { Y: new Clause("maggie", []) }
        )
    },
    {
        name: "lisa's father",
        code: 'father(homer, bart).\n' +
        'father(homer, lisa).\n' +
        'father(homer, maggie).\n' +
        'parent(X, Y) :- father(X, Y).\n' +
        'parent(X, lisa)?',
        expected: makeIterator(
            { X: new Clause("homer", []) }
        )
    },
    {
        name: "parent",
        code: 'father(abe, homer).\n' +
        'father(homer, bart).\n' +
        'father(homer, lisa).\n' +
        'father(homer, maggie).\n' +
        'parent(X, Y) :- father(X, Y).\n' +
        'parent(X, Y)?',
        expected: makeIterator(
            { X: new Clause("abe", []), Y: new Clause("homer", []) },
            { X: new Clause("homer", []), Y: new Clause("bart", []) },
            { X: new Clause("homer", []), Y: new Clause("lisa", []) },
            { X: new Clause("homer", []), Y: new Clause("maggie", []) }
        )
    },
    {
        name: "grandfather",
        code: 'father(orville, abe).\n' +
        'father(abe, homer).\n' +
        'father(homer, bart).\n' +
        'father(homer, lisa).\n' +
        'father(homer, maggie).\n' +
        'parent(X, Y) :- father(X, Y).\n' +
        'grandfather(X, Y) :- father(X, Z), parent(Z, Y).\n' +
        'grandfather(X, Y)?',
        expected: makeIterator(
            { X: new Clause("orville", []), Y: new Clause("homer", []) },
            { X: new Clause("abe", []), Y: new Clause("bart", []) },
            { X: new Clause("abe", []), Y: new Clause("lisa", []) },
            { X: new Clause("abe", []), Y: new Clause("maggie", []) }
        )
    }
);


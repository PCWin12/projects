/**
 * Created by malig on 3/13/16.
 */
tests(
    L,
    {
        name: 'sick and tired with integers',
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
        name: "prereqs with integers",
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
        name: 'cons and car',
        code: 'car([X|Y], X).\n' +
        'car([11,22,33], X)?',
        expected: makeIterator(
            { X: new Num(11) }
        )
    },
    {
        name: 'fib using is , > and numbers',
        code: 'fib(1,1).\nfib(2,1).\nfib(N,R):- N > 2,NN is N-1,NNN is N-2,fib(NN,RR),fib(NNN,RRR),R is RR+RRR.\nfib(7,R)?\n',
        expected: makeIterator(
            { R: new Num(13) }
        )
    },
    {
        name: 'Factorial',
        code: 'fact(0,1).\nfact(N,F) :-N>0,N1 is N-1,fact(N1,F1), F is N * F1.\nfact(6,F)?',
        expected: makeIterator(
            { F: new Num(720) }
        )
    },
    {
        name: 'Dot Product',
        code: 'prodv([X],[Y],[R]):- R is X*Y.\nprodv([H|T],[HH|TT],[R|RR]):- prodv(T,TT,RR),R is H*HH.\n prodv([2,4],[2,3],R)?',
        expected: makeIterator(
            { R: new Clause("_cons", [new Num(4),
                new Clause("_cons", [new Num(12), new Clause("_nil", [])])]) }
        )
    },
    {
        name: 'Max Cut',
        code: 'max(X,Y,Y) :- X < Y,!.\nmax(X,Y,X).\nmax(101,100,X)?',
        expected: makeIterator(
            { X: new Num(101) }
        )
    },
    {
        name: 'Power Cut',
        code: 'power(N,0,1):- !.\npower(N,K,R):- K1 is K-1,power(N,K1,R1),R is R1*N.\npower(4,4,R)?',
        expected: makeIterator(
            { R: new Num(256) }
        )
    },
    {
        name: 'Deletes all the occurences of an element in a list',
        code: 'delete(X,[],[]).\ndelete(X,[X|T],R):- delete(X,T,R),!.\ndelete(X,[Y|T],[Y|R]):- delete(X,T,R).\ndelete(1,[1,2,[1,3],1,5],R)?',
        expected: makeIterator(
            { R: new Clause("_cons", [new Num("2"), new Clause("_cons", [new Clause("_cons", [new Num("1"), new Clause("_cons", [new Num("3"), new Clause("_nil", [])])]), new Clause("_cons", [new Num("5"), new Clause("_nil", [])])])])}
        )
    },
    {
    name: 'Insertion Sort of a list',
        code: 'insertsort(L,S):-isort(L,[],S).\nisort([],A,A).\nisort([H|T],A,S):-insert(H,A,NA),isort(T,NA,S).\ninsert(X,[Y|T],[Y|NT]):- X>Y,insert(X,T,NT).\ninsert(X,[Y|T],[X,Y|T]):- X -1 <Y.\ninsert(X,[],[X]).\ninsertsort([12,43,123,656,7],S)?' ,
        expected: makeIterator(
        { S: new Clause("_cons", [new Num("7"), new Clause("_cons", [new Num("12"), new Clause("_cons", [new Num("43"), new Clause("_cons", [new Num("123"), new Clause("_cons", [new Num("656"), new Clause("_nil", [])])])])])])}
    )},
    {
        name: 'Split Negative and Positive Using Cut',
        code: 'split([],[],[]).\nsplit([X|L],[X|L1],L2):- X> 0,!,split(L,L1,L2).\nsplit([X|L],L1,[X|L2]):-split(L,L1,L2).\nsplit([1,-3,-5,2],X,Y)?',
        expected: makeIterator(
            { X:new Clause("_cons", [new Num("1"), new Clause("_cons", [new Num("2"), new Clause("_nil", [])])]) ,
            Y :new Clause("_cons", [new Num("-3"), new Clause("_cons", [new Num("-5"), new Clause("_nil", [])])]) }
        )
    },
    {
        name: 'Sum list -- Arithmetic Check ',
        code: 'sum([],0).\nsum([X|List],Sum) :-sum(List,Sum1), Sum is X + Sum1.\nsum([1,2,3,4],S)?\n',
        expected: makeIterator(
            { S:new Num(10)}
        )
    },
    {
        name: 'QuickSort with Cuts and floats',
        code: 'append([], L, L).\nappend([H|T], L2, [H|L3]) :- append(T, L2, L3).\nfilter( [], X, [], []).\n' +
        'filter( [X|Y], P, [X|L], H ):- X-1 < P, filter(Y, P, L, H).\nfilter( [X|Y], P, L, [X|H] ):- X > P, filter(Y, P, L, H).\n' +
        'qsort([], []):-!.\nqsort([X], [X]):-!.\nqsort([X,Y], [X,Y]):-X-1<Y,!.\nqsort([X,Y], [Y,X]):-X>Y,!.\nqsort([X|Y], SXY):- filter(Y, X, L, H), qsort(L,SL), qsort(H,SH), append(SL, [X], SLX), append(SLX, SH, SXY).\n' +
        'qsort([13,12.2,7.6,5.4],X)?',
        expected: makeIterator(
            {X:new Clause("_cons", [new Num("5.4"), new Clause("_cons", [new Num("7.6"), new Clause("_cons", [new Num("12.2"), new Clause("_cons", [new Num("13"), new Clause("_nil", [])])])])])}
        )
    },

    {
        name: 'Cut- fail negation',
        code: 'enjoys(vincent,X) :- bigkahunaburger(X),!,fail.\nenjoys(vincent,X) :- burger(X).\nburger(X) :- bigmac(X).' +
        '\nburger(X) :- bigkahunaburger(X).\nburger(X) :- whopper(X).\nbigmac(a).\nbigkahunaburger(b).\nbigmac(c).' +
        '\nwhopper(d).\nenjoys(vincent,b)?',
        expected: makeIterator()
    },
    {
        name: 'Cut 1',
        code: 'p(X):- a(X).\np(X):- b(X),c(X),!,d(X),e(X).\np(X):- f(X).\na(1).\nb(1). b(2).\nc(1). c(2).\nd(2).\ne(2).\nf(3)\n.p(X)?',
        expected: makeIterator({X: new Num(1)})
    },
    {
        name: 'cut 2',
        code: 'b(1).\nb(2).\nb(3).\n\n' +
        'c(1).\nc(2).\nc(3).\n\n' +
        'a(X, Y) :- b(X), !, c(Y).\n' +
        'a(X,Y)?',
        expected: makeIterator(
            { X: new Num(1), Y: new Num(1) },
            { X: new Num(1), Y: new Num(2) },
            { X: new Num(1), Y: new Num(3) }
        )
    },
    {
        name: 'cut 3',
        code: 'b(1).\nb(2).\nb(3).\n\n' +
        'c(1).\nc(2).\nc(3).\n\n' +
        'd(4).\nd(6).\n\n' +
        'a(X, Y) :- b(X), fail, !, c(Y).\n' +
        'a(X, Y) :- d(X), d(Y).\n' +
        'a(X,Y)?',
        expected: makeIterator(
            { X: new Num(4), Y: new Num(4) },
            { X: new Num(4), Y: new Num(6) },
            { X: new Num(6), Y: new Num(4) },
            { X: new Num(6), Y: new Num(6) }
        )
    },
    {
        name: 'cut 4',
        code: 'b(1).\nb(2).\nb(3).\n\n' +
        'c(1).\nc(2).\nc(3).\n\n' +
        'd(4).\nd(6).\n\n' +
        'a(X, Y) :- b(X), !, c(Y), fail.\n' +
        'a(X, Y) :- d(X), d(Y).\n' +
        'a(X,Y)?',
        expected: makeIterator()
    },

    {
        name: 'not 1',
        code: 'p.\n~(2 == 4)?',
        expected: makeIterator({})
    },
    {
        name: 'not 2',
        code: 'unmarriedstudent(X):- ~(married(X)), student(X).\nstudent(joe).\nmarried(john).\nunmarriedstudent(john)?',
        expected: makeIterator()
    },
    {
        name: 'not 3',
        code: 'man(jim).\nman(fred).\nwoman(X):- ~( man(X) ).\nwoman(merry)?',
        expected: makeIterator({})
    },
    {
        name: 'Primal Check using not, arithmetic and relational operators ',
        code: 'isprime(2).\nisprime(3).\nisprime(P) :- P > 3, P % 2 != 0, ~(hasfactor(P,3)).\nhasfactor(N,L) :- N % L == 0.\nhasfactor(N,L) :- L * L < N, L2 is L + 2, hasfactor(N,L2).\nisprime(4111)?',
        expected: makeIterator({})
    },
    {
        name: ' References in Rules with prefox notations',
        code: 'p(0).\np(2).\np(5).\np(10).\np(17).\nq(1).\nq(5).\nq(28).\nq(11).\nq(19).\nq(38).\noddeven(X,Y,Z) :- X%2==1, Y%2 == 0, is(Z, +(X,Y)).\np(X), q(Y), oddeven(X,Y,Z), Z%2 == 1?',
        expected: makeIterator(   { X: new Num(5), Y: new Num(28), Z: new Num(33) },
            { X: new Num(5), Y: new Num(38), Z: new Num(43) },
            { X: new Num(17), Y: new Num(28), Z: new Num(45) },
            { X: new Num(17), Y: new Num(38), Z: new Num(55) })
    },
    {
        name: 'Greatest Common Divisor',
        code: 'gcd(X,0,X) :- X > 0.\ngcd(X,Y,G) :- Y > 0, Z is X % Y, gcd(Y,Z,G).\ngcd(3134544,1908,G)?',
        expected: makeIterator(
            { G: new Num(12)})
    }
,{
    name: 'Eulers totient function',
    code: 'gcd(X,0,X) :- X > 0.\ngcd(X,Y,G) :- Y > 0, Z is X % Y, gcd(Y,Z,G).\ncoprime(X,Y) :- gcd(X,Y,1).\ntotientphi(1,1) :- !.\ntotientphi(M,Phi) :- tphi(M,Phi,1,0).\ntphi(M,Phi,M,Phi) :- !.\n' +
    'tphi(M,Phi,K,C) :-K < M, coprime(K,M), !,C1 is C + 1, K1 is K + 1,tphi(M,Phi,K1,C1).\ntphi(M,Phi,K,C) :-K < M, K1 is K + 1,tphi(M,Phi,K1,C).\n totientphi(12, X)?',
    expected: makeIterator(
        { X: new Num(4)})
},
    {
        name: ' Goldbachs conjecture. ',
        code: 'isprime(2).isprime(3).\nisprime(P) :- P > 3, P % 2 != 0, ~(hasfactor(P,3)).\nhasfactor(N,L) :- N % L == 0.\nhasfactor(N,L) :- L * L < N, L2 is L + 2, hasfactor(N,L2).\n' +
        'goldbach(4,[2,2]) :- !.goldbach(N,L) :- N % 2 == 0, N > 4, goldbach(N,L,3).' +
        '\ngoldbach(N,[P,Q],P) :- Q is N - P, isprime(Q), !.\ngoldbach(N,L,P) :- P < N, nextprime(P,P1), goldbach(N,L,P1).' +
        '\nnextprime(P,P1) :- P1 is P + 2, isprime(P1), !.\nnextprime(P,P1) :- P2 is P + 2, nextprime(P2,P1).\ngoldbach(28,L)?',
        expected: makeIterator(
            { L :  new Clause("_cons", [new Num(5),
                new Clause("_cons", [new Num(23), new Clause("_nil", [])])])}
        )
    }

);


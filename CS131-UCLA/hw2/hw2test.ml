let accept_all derivation string = Some (derivation, string)
let accept_empty_suffix derivation = function
   | [] -> Some (derivation, [])
   | _ -> None

type awksub_nonterminals =
  | Expr | Term | Lvalue | Incrop | Binop | Num

let awkish_grammar =
  (Expr,
   function
     | Expr ->
         [[N Term; N Binop; N Expr];
          [N Term]]
     | Term ->
	 [[N Num];
	  [N Lvalue];
	  [N Incrop; N Lvalue];
	  [N Lvalue; N Incrop];
	  [T"("; N Expr; T")"]]
     | Lvalue ->
	 [[T"$"; N Expr]]
     | Incrop ->
	 [[T"++"];
	  [T"--"]]
     | Binop ->
	 [[T"+"];
	  [T"-"]]
     | Num ->
	 [[T"0"]; [T"1"]; [T"2"]; [T"3"]; [T"4"];
	  [T"5"]; [T"6"]; [T"7"]; [T"8"]; [T"9"]])

let test_1 =
  ((parse_prefix awkish_grammar accept_all ["9"; "-" ; "1"])
   = Some
       ([(Expr, [N Term; N Binop; N Expr]); (Term, [N Num]); (Num, [T "9"]);
	 (Binop, [T "-"]); (Expr, [N Term]); (Term, [N Num]);(Num, [T "1"])] , []))



type my_nonterminals =
  | A | B | S | C

let my_grammar =
  S,
  [S, [N A ;N C; N B];
   C, [T"o"];
   C, [N S];
   A, [T"("]; 
   B, [T")"]]

let hw2_grammar = convert_grammar my_grammar

let test_2 =
  ((parse_prefix hw2_grammar accept_all ["("; "o"; ")"])
   = Some
       ([(S, [N A; N C; N B]); (A, [T "("]); (C, [T"o"]);
   (B, [T")"])],[]))
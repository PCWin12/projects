type ('terminal, 'nonterminal) symbol = 
	| T of 'terminal 
	| N of 'nonterminal;;


(* This is a simple helper function I made to accumulate all the rules which are usually 
separated in the HW1 grammar
*)
let rec get_rules lhs r = match r with
	| [] -> []
	| (ls, rs)::t -> if(ls=lhs) then rs::(get_rules lhs t)
		else get_rules lhs t
	
	
(* The function we are asked to create to generate the hw2 grammar, As told in the discussion session
I returned a tuple of the start symbol same as hw1 grammar and a production function that takes in 
the left side of the rule and returns a "list of list" of derivvations *)
let convert_grammar gram1 = ((fst(gram1)), fun lhs -> (get_rules lhs (snd(gram1))))



(* This function Consumes one terminal/nonterminal at a time and see if it matches the frag. It 
the next element in the frag is same as terminal in rule it will consume it and call this function
again on the rest of frag and rule. if a non-terminal is encountered then it will call traverse_productions 
to consume the productions for that non-terminal and the rest of the production after that non-terminal is further consumed
by the accepter i sent to that function. Once all the evaluations of that non-terminal is done, the use_production will call
the accept derv frag to perform the consumption of the rest of the frag. *)

let rec use_production rules rhs accept derv frag   = match rhs with
	| [] -> accept derv frag
	| _ -> 	match frag with
		| []	-> None
		| h::t	-> match rhs with
			| (T term) :: tt -> if (h = term) 
				then (use_production rules tt accept derv t )
				else	None
			| (N nterm) :: tt -> traverse_productions (use_production rules tt accept) derv frag rules nterm (rules nterm)

			

(* This function traverse all the possible producitons of a given non-terminal if any of the 
production fails , it will then try the rest of the productions in the list. It returns None 
if no production were able to drive the given frag*)
and traverse_productions accept derv frag rules nt rhs  = match rhs with
	| []  -> None 
	| _ ->  match rhs with
		| h::t	-> match (use_production rules h accept (derv@[(nt, h)]) frag) with
			| Some(a, b)	-> Some(a, b)
			| None 	-> (traverse_productions accept derv frag rules nt t )



(*The only need to this function is to apply the production function on the given non-terminal 
and supply the resulted expression to eh traverses productions function*)
and init accept derv frag rules nt  = traverse_productions accept derv frag rules nt (rules nt)


(* This is the original function we were asked to implement this function in turns call the do_match function
that traverse over each set of rule and further calls use_production function to use the productions one by 
one if matches and since each rule contains list of terminals and non terminal the use_production function has 
to call recursively itself and other function like init to get the production of the non-terminals faced in
the derivation of a particular rule with respect to the given frag. Initially it was really really difficult to 
design a procedure to evaluate the Non-terminals since every time it goes into infinite loop and did not return
but then in the end i figured out to use the accepter to perform the recursive non-terminal evaluation
  *)
let parse_prefix gram accept frag = init  accept [] frag (snd(gram)) (fst(gram))






(define (backtrack frag)
	(call/cc (lambda (k1) (cons frag (lambda () (k1 #f))))))

(define (make-matcher pat)
    (lambda (frag) ((old-matcher-maker pat) frag backtrack)))

(define match-junk
  (lambda (k frag backtrack)
    (or (backtrack frag)
	(and (< 0 k)
	     (pair? frag)
	     (match-junk (- k 1) (cdr frag) backtrack)))))

(define match-*
  (lambda (matcher frag backtrack)
    (or (backtrack frag)
	(matcher frag
		 (lambda (frag1)
		   (and (not (eq? frag frag1))
			(match-* matcher frag1 backtrack)))))))

(define old-matcher-maker
  (lambda (pat)
    (cond

     ((symbol? pat)
      (lambda (frag backtrack)
	(and (pair? frag)
	     (eq? pat (car frag))
	     (backtrack (cdr frag)))))

     ((eq? 'or (car pat))
      (let make-or-matcher ((pats (cdr pat)))
	(if (null? pats)
	    (lambda (frag backtrack) #f)
	    (let ((head-matcher (old-matcher-maker (car pats)))
		  (tail-matcher (make-or-matcher (cdr pats))))
	      (lambda (frag backtrack)
		(or (head-matcher frag backtrack)
		    (tail-matcher frag backtrack)))))))

     ((eq? 'list (car pat))
      (let make-list-matcher ((pats (cdr pat)))
	(if (null? pats)
	    (lambda (frag backtrack) (backtrack frag))
	    (let ((head-matcher (old-matcher-maker (car pats)))
		  (tail-matcher (make-list-matcher (cdr pats))))
	      (lambda (frag backtrack)
		(head-matcher frag
			      (lambda (frag1)
				(tail-matcher frag1 backtrack))))))))

     ((eq? 'junk (car pat))
      (let ((k (cadr pat)))
	(lambda (frag backtrack)
	  (match-junk k frag backtrack))))

     ((eq? '* (car pat))
      (let ((matcher (old-matcher-maker (cadr pat))))
	(lambda (frag backtrack)
	  (match-* matcher frag backtrack)))))))
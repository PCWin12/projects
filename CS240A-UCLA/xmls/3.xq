xquery version "1.0";

import module namespace query =  "modules" at "query.xq";

declare variable $start := '1994-05-01';

declare variable $end:= '1996-05-06' ;

<departments>
{
for $dept 
	in doc("v-depts.xml")//department[not( @tstart > $end or $start >= @tend)]
	order by $dept/deptno
	return element
		{node-name($dept)}
		{attribute tend   {query:checkmin($end,$dept/@tend)},
	attribute tstart {query:checkmax($start,$dept/@tstart)},
	$dept/@*[name(.)!="tend" and name(.)!="tstart"], 
		query:resetDates($dept/*[not( @tstart > $end or $start >= @tend)], $start, $end)
		}
}
</departments>
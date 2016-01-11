xquery version "3.0";

import module namespace query =  "modules" at "query.xq";
  
<employees>
{
for $employee 
	in doc("v-emps.xml")//employee[@tstart <= '1995-01-01' and '1995-01-01' <= @tend]
	let $salary := $employee/salary[@tstart <= '1995-01-01' and '1995-01-01' <= @tend],
	    $deptno := $employee/deptno[@tstart <= '1995-01-01' and '1995-01-01' <= @tend]
	where($salary > 80000 )
	return
		<employee>
			{query:removeAttr( ($employee/firstname,
							 $employee/lastname,
							 $deptno,
							 $salary) )}
		</employee>
}
</employees>


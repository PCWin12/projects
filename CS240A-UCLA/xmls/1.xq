xquery version "3.0";

import module namespace query = 
  "modules" at "file:///Users/malig/Documents/Grad School/CS240A/projectcs240/query.xq";

declare variable $start := '0001-01-01';

declare variable $end:= xs:string( fn:adjust-date-to-timezone( current-date( ), () ) );

<employees>
{
for 
	$employee 
		in doc("v-emps.xml")//employee[firstname="Anneke" and lastname="Preusig"]
	return element
	{node-name($employee)}
	{ attribute tend   {query:checkmin($end,$employee/@tend)},
	attribute tstart {$employee/@tstart},
	$employee/@*[name(.)!="tend" and name(.)!="tstart"],
			query:resetDates($employee/firstname,$start, $end), 
			query:resetDates($employee/lastname,$start, $end),
		query:resetDates(query:getAll( (
			$employee/salary) ) ,$start, $end)
	}
}
</employees>

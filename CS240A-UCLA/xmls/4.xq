xquery version "1.0";
import module namespace query =  "modules" at "query.xq";


declare variable $current:= xs:string( fn:adjust-date-to-timezone( current-date( ), () ) );

declare variable $start := '0001-01-01';

<employees>
{
for $employee 
	in doc("v-emps.xml")//employee
	let $period := (
		for $salary in $employee/salary
			return
				 xs:date(query:checkmin($current, $salary/@tend)) - xs:date($salary/@tstart)
	)
	return element
		{node-name($employee)}
		{
			attribute tend   {query:checkmin($current,$employee/@tend)},
	attribute tstart {query:checkmax($start,$employee/@tstart)},
	$employee/@*[name(.)!="tend" and name(.)!="tstart"],
			query:resetDates(($employee/firstname,$employee/lastname) ,$start, $current),
			for 
				$salary
					in $employee/salary[xs:date(query:checkmin($current, @tend))-xs:date(@tstart)=max($period)]
				order by $salary/@tstart, $salary/@tend
				return query:resetDates($salary, $start, $current)
				
		}
}
</employees>


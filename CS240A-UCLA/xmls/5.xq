xquery version "3.0";
(: First get the list of all the manager that the a employee have worked with then find th longest duration period for employee on the list of managers

also reser the dates of the managers so that it comply with the employeee start and end date witht that department and also the start and end date of maanger itself
:)
import module namespace query = 
  "modules" at "query.xq";

declare variable $start := '0001-01-01';

declare variable $current:= xs:string( fn:adjust-date-to-timezone( current-date( ), () ) );

declare variable $temp := element employees{
for $employee
	in doc("v-emps.xml")//employee
	return element
		{node-name($employee)}	
		{
			attribute tend   {query:checkmin($current,$employee/@tend)},
	attribute tstart {query:checkmax($start,$employee/@tstart)},
	$employee/@*[name(.)!="tend" and name(.)!="tstart"],
			query:resetDates( (
				$employee/empno,
				$employee/firstname,
				$employee/lastname), $start, $current ),
			query:resetDates( (
				$employee/title ),  $start, $current  ),
		element managers
			{   for
					$deptno
						in $employee/deptno,
					$manager
						in doc("v-depts.xml")//
								department[deptno=$deptno]/mgrno[@tstart<=$deptno/@tend and $deptno/@tstart<=@tend]
					let $dept_duration := (attribute tend   {query:checkmin(string(query:checkmin($current, $deptno/@tend)),$manager/@tend)},
	                                         attribute tstart {query:checkmax( $deptno/@tstart,$manager/@tstart)},
	                                         $deptno/@*[name(.)!="tend" and name(.)!="tstart"])
					return query:adddeptName( $deptno, query:resetDates($manager, string($dept_duration[2]), string($dept_duration[1]) ) )
			}			
		}
};
element employees{
for $employee 
	in $temp//employee 
	let $period := (
		for $manager in $employee/managers/mgrno
			return
				 xs:date(query:checkmin($current, $manager/@tend)) - xs:date($manager/@tstart)
	)
	return element
		{node-name($employee)}
		{
			attribute tend   {query:checkmin($current,$employee/@tend)},
	attribute tstart {query:checkmax($start,$employee/@tstart)},
	$employee/@*[name(.)!="tend" and name(.)!="tstart"],
			$employee/empno,$employee/firstname,$employee/lastname,$employee/deptno,
			for 
				$manager
					in $employee/managers/mgrno[xs:date(query:checkmin($current, @tend))-xs:date(@tstart)=max($period)]
				order by $manager/@tstart, $manager/@tend
				return $manager		
		}
}


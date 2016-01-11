xquery version "1.0";



declare variable $emps := doc( "v-emps.xml" );

declare variable $deptno := $emps//salary;

declare variable $tstart_dates :=  
	for 
		$i in distinct-values( $deptno/@tstart )
		order by $i 
		return xs:date( $i );

declare variable $tend_dates   := 
	for $i in distinct-values( $deptno/@tend )   
		order by $i 
		return xs:date( $i );


declare variable $dates := 
	for $i in distinct-values( ($tstart_dates, $tend_dates) )
		order by $i
		return $i;

declare variable $counts :=
	for $tstart at $i in $dates
		let $y := $deptno[@tstart <= $tstart and $tstart < @tend]
		let $count := avg($y)
		order by $tstart
		return <average tstart="{$tstart}" tend="{$dates[$i+1]}" >{$count}</average>;

<company>
{$counts}
</company>


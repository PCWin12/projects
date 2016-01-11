xquery version "1.0";
(: I frist start by collect the distinct date in which salaries can differ and then I query all the available titles. Then for each title and for each date I run auery to find the salaries employees 
who are holding that particular title at that date. THe return salary is also for the same period. Once I have the view of all the salaries for a particular date , average is computed and shown tp suer fpr each 
time period and eventually for each title:)
import module namespace query = 
  "modules" at  "query.xq";

declare variable $emps := doc( "v-emps.xml" );

declare variable $deptno := $emps//salary;

declare variable $tstart_dates :=  
	for 
		$i in distinct-values( $deptno/@tstart )
		order by $i 
		return xs:date( $i );
		
		declare variable $titles :=  
	for 
		$i in distinct-values( $emps//title )
		order by $i 
		return  $i;

declare variable $tend_dates   := 
	for $i in distinct-values( $deptno/@tend )   
		order by $i 
		return xs:date( $i );


declare variable $dates := 
	for $i in distinct-values( ($tstart_dates, $tend_dates) )
		order by $i
		return $i;

declare variable $counts :=
for $title in $titles
return <title name="{$title}" >{
	for $tstart at $j in $dates
	    let $sal := query:gettitle($title, $tstart)
	    let $sal2 := $sal/salary[@tstart <= $tstart and $tstart < @tend]
		let $count := avg($sal2)
		where ($count)
		order by $tstart
		return <average tstart="{$tstart}" tend="{$dates[$j+1]}" >{$count}</average>
} </title>;

<company>
{
 $counts
}
</company>


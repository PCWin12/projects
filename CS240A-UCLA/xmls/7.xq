xquery version "1.0";
(: I frist start by collect the distinct date in which salaries can differ and then I query with a condition that the department is d005 for that time and corresponsing salary for each employee.
All salaries for that department are then gathered and max is computed for each time period. :)

import module namespace query =
"modules" at "query.xq";

declare variable $dept := 'd005';
declare variable $emps := doc("v-emps.xml");
declare variable $tstart_dates :=
for
$i in distinct-values($emps//salary/@tstart)
order by $i
return
    xs:date($i);

declare variable $tend_dates :=
for $i in distinct-values($emps//salary/@tend)
order by $i
return
    xs:date($i);


declare variable $dates :=
for $i in distinct-values(($tstart_dates, $tend_dates))
order by $i
return
    $i;


declare variable $counts1 :=
for $tstart at $i in $dates
let $sal1 := query:getd005($dept, $tstart)
let $sal2 := $sal1/salary[@tstart <= $tstart and $tstart < @tend]
let $av := max($sal2)
where ($av)
return
    <max
        tstart="{$tstart}"
        tend="{$dates[$i + 1]}">{$av}</max>;



declare variable $counts :=
<deptno
    name="{$dept}">{$counts1}
</deptno>;


<company>
    {
        $counts
        (:<count tstart="{$tstart/@date}" tend="{$tend/@date}">{string($unique_counts[$pos])}</count>
:)
    }
</company>

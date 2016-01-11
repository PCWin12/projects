
module namespace query = "modules";

declare function query:getAll($elements as element()*) as element()*
{
    for $element in $elements
    order by $element/@tstart,
        $element/@tend
    return
        $element
};

declare function query:removeAttr($elements as element()*) as element()*
{
    for $element in $elements
    return
        element
        {node-name($element)}
        {
            $element/@*[name(.) != "tend" and name(.) != "tstart"],
            data($element)
        }
};


declare function query:checkmin($d1 as xs:string, $d2 as xs:string) as xs:date
{
    if (xs:date($d1) > xs:date($d2))
    then
        xs:date($d2)
    else
        xs:date($d1)
};

declare function query:checkmax($d1 as xs:string, $d2 as xs:string) as xs:date
{
    if (xs:date($d1) > xs:date($d2))
    then
        xs:date($d1)
    else
        xs:date($d2)
};
declare function query:resetDates($elements as element()*, $start as xs:string, $stop as xs:string) as element()*
{
    for $element in $elements
    return
        element {node-name($element)} {
            attribute tend {query:checkmin($stop, $element/@tend)},
            attribute tstart {query:checkmax($start, $element/@tstart)},
            $element/@*[name(.) != "tend" and name(.) != "tstart"],
            string($element)
        }
};

declare function query:slice_all($elements as element()*,
$start as xs:string, $stop as xs:string) as element()*
{
    for $element in $elements
    return
        element {node-name($element)}
        {
            $element/@tstart, $element/@tend,
            string($element)
        }
};


declare function query:getdeptName($deptnos as element()*) as element()*
{
    for $deptno in $deptnos
    return
        element
        {node-name($deptno)}
        {
            $deptno/@*,
            attribute deptname {string(doc("v-depts.xml")//department[deptno = $deptno]/deptname)},
            string($deptno)
        }
};

declare function query:adddeptName($deptno as element()*, $mgrs) as element()*
{
    for $mgr in $mgrs
    return
        element
        {node-name($mgr)}
        {
            $mgr/@*,
            attribute deptname {string(doc("v-depts.xml")//department[deptno = $deptno]/deptname)},
            attribute deptno {$deptno},
            string($mgr)
        }
};


declare function query:getd005($dept as xs:string, $tstart as xs:date) as element()*
{
    for $i in doc("v-emps.xml")//employee
    where ($i/deptno = $dept and $i/deptno/@tstart <= $tstart and $i/deptno/@tend > $tstart and $i/salary/@tstart <= $tstart and $i/salary/@tend > $tstart
    and  (for $dep in $i/deptno
            where $dep = $dept and $dep/@tstart <= $tstart and $dep/@tend > $tstart
            return
                $dep)
    )
    return
        $i
};
declare function query:gettitle($title as xs:string, $tstart as xs:date) as element()*
{
    for $i in doc("v-emps.xml")//employee
    where ($i/title = $title and $i/title/@tstart <= $tstart and $i/title/@tend > $tstart and $i/salary/@tstart <= $tstart and $i/salary/@tend > $tstart and (for $titl in $i/title
            where $titl = $title and $titl/@tstart <= $tstart and $titl/@tend > $tstart
            return
                $titl))
    return
        element employee {
            for $titl in $i/title
            where $titl = $title and $titl/@tstart <= $tstart and $titl/@tend > $tstart
            return
                $titl ,  $i/salary[@tstart <= $tstart and @tend > $tstart]
        
        }
};

<%@page import="code.StudentDB"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.List"%>

<%
    StudentDB db = new StudentDB();

    String query = request.getParameter("q");

    List<String> countries = db.getData(query, (String) session.getValue("uname"));

    Iterator<String> iterator = countries.iterator();
    while (iterator.hasNext()) {
        String country = (String) iterator.next();
        out.println(country);
    }
%>
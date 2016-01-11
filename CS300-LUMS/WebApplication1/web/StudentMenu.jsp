<%-- 
    Document   : StudentMenu
    Created on : May 7, 2013, 4:31:58 PM
    Author     : Muhammad Ali Gulzar
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Menu</title>
    </head>
    <body  style="text-align: center;background-image:url('back.jpg');">
        <h1>Welcome:  <%; 
       String str = (String) session.getValue("t");
       out.print( session.getValue("uname"));
        if(str.equalsIgnoreCase("2"))
                       {
        out.println("(Student)");
        }else{
            out.println("(Faculty)");
        }
        %></h1>
        <form action="http://localhost:8080/WebApplication1/View" method="POST">
            <select name ="ch">
                <option value="1">Issued Resource</option>
                <option value="2">Pending Requests</option>
                <option value="3">Fines</option>
            </select>
            <input type="submit" value="View">
        </form>

        <form action="http://localhost:8080/WebApplication1/issueResource" method="POST">

            <input type="submit" value="Issue Resource">
        </form>
        <form action="http://localhost:8080/WebApplication1/returnResource">
            <input type="submit" value="Return Resource">
        </form>

        <form action="http://localhost:8080/WebApplication1/deleteRequest">
            <input type="submit" value="Delete Request">
        </form>
        <form action="http://localhost:8080/WebApplication1/renew.jsp">
            <input type="submit" value="Renew Resource">
        </form>
        <form action="http://localhost:8080/WebApplication1/index.jsp">
            <input type="submit" value="Logout">
        </form>


    </body>
</html>

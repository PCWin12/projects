<%-- 
    Document   : index
    Created on : May 5, 2013, 10:35:38 AM
    Author     : Muhammad Ali Gulzar
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>LMS</title>
    </head>
    <%
        session.invalidate();
    %>
    <body  style="text-align: center;background-image:url('back.jpg');">
        <h1>Library Management System</h1>

        <form action="http://localhost:8080/WebApplication1/LoginValid" method="POST">

            <input type="radio" name="type" value="1">Admin<br>
            <input type="radio" name="type" value="2">Student<br>
            <input type="radio" name="type" value="3">Faculty<br>
            Username : <input type="text" name="username" value=""><br>
            Password   : <input type="text" name="password" value=""><br>
            <input type="submit" value="Submit">
        </form>
        <form action="http://localhost:8080/WebApplication1/libraryinfo">
            <input type="submit" value="Library Stats">
        </form>
    </body>
</html>

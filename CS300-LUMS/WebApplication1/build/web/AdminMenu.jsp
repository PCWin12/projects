<%-- 
    Document   : AdminMenue
    Created on : May 5, 2013, 1:20:50 PM
    Author     : Muhammad Ali Gulzar
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Admin</title>
    </head>
    <body  style="text-align: center;background-image:url('back.jpg');">
        <h1>Welcome:  <%= session.getValue("uname")%></h1>
        <form action="http://localhost:8080/WebApplication1/AddUser.jsp" method="POST">
          
            <input type="submit" value="Create Account">
        </form>

            <form action="http://localhost:8080/WebApplication1/AddResource.jsp" method="POST">
         
            <input type="submit" value="Add Resource">
        </form>
        <form action="http://localhost:8080/WebApplication1/removeUsers">
            <input type="submit" value="Remove User">
        </form>

        <form action="http://localhost:8080/WebApplication1/removeResource">
            <input type="submit" value="Remove Resource">
        </form>
        <form action="http://localhost:8080/WebApplication1/index.jsp">
            <input type="submit" value="Logout">
        </form>


    </body>
</html>

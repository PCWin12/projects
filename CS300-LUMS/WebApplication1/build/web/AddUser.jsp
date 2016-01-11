<%-- 
    Document   : AddUser
    Created on : May 5, 2013, 3:32:55 PM
    Author     : Muhammad Ali Gulzar
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Add User</title>
    </head>
  <body  style="text-align: center;background-image:url('back.jpg');">      <form action="http://localhost:8080/WebApplication1/addUser" method="POST">
             <select name ="type">
                <option value="1">Admin</option>
                <option value="2">Student</option>
                <option value="3">Faculty</option>
           </select><br>
            User Name: <input type="text" name="username" value=""><br>
            Password : <input type="text" name="password" value=""><br>
            <input type="submit" value="Add">
        </form>
    </body>
</html>

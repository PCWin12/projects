<%-- 
    Document   : AddResource
    Created on : May 5, 2013, 4:49:38 PM
    Author     : Muhammad Ali Gulzar
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Add Resource</title>
    </head>
  <body  style="text-align: center;background-image:url('back.jpg');">     <form action="http://localhost:8080/WebApplication1/addResource" method="POST">
             <select name ="type">
                <option value="1">Book</option>
                <option value="2">CoursePack</option>
                <option value="3">Magazine</option>
           </select><br>
            Name: <input type="text" name="name" value=""><br>
             <input type="submit" value="Add">
        </form>
    </body>
</html>

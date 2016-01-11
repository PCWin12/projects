<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="java.sql.Statement"%>
<%@page import="controller.StudentDB"%>
<%@page import="java.sql.Connection"%>
<html lang="en">
    <head>
        <meta charset="utf-8" />
        <title>Issue Resource</title>
        <link rel="stylesheet" href="http://code.jquery.com/ui/1.10.3/themes/smoothness/jquery-ui.css" />
        <script src="http://code.jquery.com/jquery-1.9.1.js"></script>
        <script src="http://code.jquery.com/ui/1.10.3/jquery-ui.js"></script>
        <link rel="stylesheet" href="/resources/demos/style.css" />
        <%
            Connection connect = null;
            Statement statement = null;
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;

            connect = new StudentDB().getConnection();
            statement = connect.createStatement();
            // Result set get the result of the SQL query
            resultSet = statement.executeQuery("SELECT * FROM library_resource WHERE  type IN(1,2)");
        %> 
        
        <script>
            $(function() {
                var availableTags = [ 
            <% while (resultSet.next()) {%>
            "<%=resultSet.getString("name")%>", 
            <% }%>
                ];
                $( "#tags" ).autocomplete({
                    source: availableTags
                });
            });
        </script>
    </head>
 <body  style="text-align: center;background-image:url('back.jpg');">

        <div class="ui-widget">
            <form action="http://localhost:8080/WebApplication1/isResource" method="POST">
           <label for="tags">Resource Name: </label>
           
           <input type="text" id="tags" name="rname" >
            <input type="submit" value="Issue" />         
        </form>
           
        </div>


    </body>
</html>
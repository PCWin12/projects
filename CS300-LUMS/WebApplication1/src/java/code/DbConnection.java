package code;

import java.io.PrintWriter;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Muhammad Ali Gulzar
 */
public class DbConnection {

    private Connection connect = null;
    private Statement statement = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;

    public Connection getConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            // Setup the connection with the DB
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/ass6", "root", "");
        } catch (SQLException ex) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;

    }

    public void readDataBase(PrintWriter out) throws Exception {

        connect = getConnection();

        // Statements allow to issue SQL queries to the database
        statement = connect.createStatement();
        // Result set get the result of the SQL query
        resultSet = statement.executeQuery("SELECT * FROM library_user");
        while (resultSet.next()) {
            String user = resultSet.getString("uid");
            out.println(user);

            //String website = resultSet.getString("webpage");
            // String summery = resultSet.getString("summery");
            //  Date date = resultSet.getDate("datum");
            //String comment = resultSet.getString("comments");
        }
        resultSet.close();
        connect.close();

    }

    public int addUser(String username, String pass, String type) throws SQLException {
        Connection con = new DbConnection().getConnection();
        // Result set get the result of the SQL query




        PreparedStatement preparedStatement = con
                .prepareStatement("INSERT INTO ass6.library_user (uid ,username,password,type "
                + ")VALUES (NULL ,?,?,?)");
        // "myuser, webpage, datum, summery, COMMENTS from FEEDBACK.COMMENTS");
        // Parameters start with 1
        preparedStatement.setString(1, username);
        preparedStatement.setString(2, pass);
        preparedStatement.setInt(3, Integer.parseInt(type));
        int k = preparedStatement.executeUpdate();

        con.close();
        return k;



    }

    public int addResource(String name, String type) throws SQLException {
        Connection con = new DbConnection().getConnection();
        // Result set get the result of the SQL query


        PreparedStatement preparedStatement = con
                .prepareStatement("INSERT INTO ass6.library_resource ("
                + "rid ,available ,type ,name)VALUES (NULL , '1', ?, ?)");
        // "myuser, webpage, datum, summery, COMMENTS from FEEDBACK.COMMENTS");
        // Parameters start with 1
        preparedStatement.setString(2, name);
        preparedStatement.setInt(1, Integer.parseInt(type));
        int k = preparedStatement.executeUpdate();


        con.close();
        return k;



    }

    public void displayData(PrintWriter out) throws Exception {

        connect = getConnection();

        // Statements allow to issue SQL queries to the database
        statement = connect.createStatement();
        // Result set get the result of the SQL query
        resultSet = statement.executeQuery("SELECT * FROM library_user");
        out.println("<h1>Registered Users</h1><table>");

        out.println("<th>UserName</th><th>User ID</th>");


        while (resultSet.next()) {
            String user = resultSet.getString("uid");

            String name = resultSet.getString("username");
            out.println("<tr><td>" + name + "</td>");
            out.println("<td><form action=\"http://localhost:8080/WebApplication1/remUser\"  method=\"POST\" >"
                    + "<input type=\"submit\" name = \"uid\" value=\"" + user + "\">" + "</form></td></tr>");

            //String website = resultSet.getString("webpage");
            // String summery = resultSet.getString("summery");
            //  Date date = resultSet.getDate("datum");
            //String comment = resultSet.getString("comments");
        }
        out.println("</table>");
        resultSet.close();
        connect.close();

    }

    public void displayResource(PrintWriter out) throws Exception {

        connect = getConnection();

        // Statements allow to issue SQL queries to the database
        statement = connect.createStatement();
        // Result set get the result of the SQL query
        resultSet = statement.executeQuery("SELECT * FROM library_resource");
        out.println("<h1>Registered Resource</h1><table>");

        out.println("<th>Name</th><th>Resource ID</th>");


        while (resultSet.next()) {
            String user = resultSet.getString("rid");

            String name = resultSet.getString("name");
            out.println("<tr><td>" + name + "</td>");
            out.println("<td><form action=\"http://localhost:8080/WebApplication1/remResource\" method=\"POST\"    >"
                    + "<input type=\"submit\" name = \"rid\" value=\"" + user + "\">" + "</form></td></tr>");

            //String website = resultSet.getString("webpage");
            // String summery = resultSet.getString("summery");
            //  Date date = resultSet.getDate("datum");
            //String comment = resultSet.getString("comments");
        }
        out.println("</table>");
        resultSet.close();
        connect.close();

    }

    public int deleteUser(PrintWriter out, String id) throws Exception {

        Connection con = getConnection();
        // Result set get the result of the SQL query


        PreparedStatement preparedStatement = con
                .prepareStatement("DELETE  FROM library_user WHERE uid = ?");
        // "myuser, webpage, datum, summery, COMMENTS from FEEDBACK.COMMENTS");
        // Parameters start with 1
        preparedStatement.setInt(1, Integer.parseInt(id));
        int k = preparedStatement.executeUpdate();



        con.close();
        return k;

    }

    public int deleteResource(PrintWriter out, String id) throws Exception {

        Connection con = getConnection();
        // Result set get the result of the SQL query


        PreparedStatement preparedStatement = con
                .prepareStatement("DELETE  FROM library_resource WHERE rid = ?");
        // "myuser, webpage, datum, summery, COMMENTS from FEEDBACK.COMMENTS");
        // Parameters start with 1
        preparedStatement.setInt(1, Integer.parseInt(id));
        int k = preparedStatement.executeUpdate();



        con.close();
        return k;

    }

    public void displayInfo(PrintWriter out) throws Exception {
        out.println("<h3>**** Library System Stats****</h3>");

        connect = getConnection();

        // Statements allow to issue SQL queries to the database
        statement = connect.createStatement();
        // Result set get the result of the SQL query
        resultSet = statement.executeQuery("SELECT count( * ) as c ,type FROM library_user GROUP BY TYPE");
        int admin = 0, fac = 0, user = 0;
        while (resultSet.next()) {
            int type = resultSet.getInt("type");
            if (type == 1) {

                admin = resultSet.getInt("c");
            } else if (type == 2) {
                user = resultSet.getInt("c");
            } else {

                fac = resultSet.getInt("c");
            }
        }
        out.println("<br /><br /> Total Users: " + (admin + fac + user));
        out.println("<br /> Administrators: " + admin);
        out.println("<br /> Students: " + user);
        out.println("<br /> Faculty: " + fac);

        int cp = 0, books = 0, mag = 0, over = 0, req = 0, issue = 0;
        resultSet.close();
        resultSet = statement.executeQuery("SELECT count( * ) as c ,type FROM library_resource GROUP BY TYPE");

        while (resultSet.next()) {
            int type = resultSet.getInt("type");
            if (type == 1) {

                books = resultSet.getInt("c");
            } else if (type == 2) {
                cp = resultSet.getInt("c");
            } else {

                mag = resultSet.getInt("c");
            }
        }
        out.println("<br /><br /><br /> Total Resources: " + (books + cp + mag));
        out.println("<br /> Books: " + books);
        out.println("<br /> Course Packs: " + cp);
        out.println("<br /> Magazine: " + mag);
        resultSet.close();
          statement = connect.createStatement();
        resultSet = statement.executeQuery("SELECT count(*) as c FROM library_resource WHERE dueDate < CURRENT_DATE");

        while (resultSet.next()) {
            over = resultSet.getInt("c");
        }
        resultSet.close();
        resultSet = statement.executeQuery("SELECT count(*) as c FROM library_resource WHERE available=0");

        while (resultSet.next()) {
            issue = resultSet.getInt("c");
        }
        resultSet.close();
        resultSet = statement.executeQuery("SELECT count(*) as c FROM requests WHERE 1");

        while (resultSet.next()) {
            req = resultSet.getInt("c");
        }
        out.println("<br /><br />Total Issued: " + issue);
        out.println("<br /> Total Overdue: " + over);
        out.println("<br /> Total Requests:" + req);

        resultSet.close();
        connect.close();

    }
}

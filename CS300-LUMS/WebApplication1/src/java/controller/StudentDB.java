/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Muhammad Ali Gulzar
 */
public class StudentDB {

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
        Connection connect = getConnection();
        // Statements allow to issue SQL queries to the database
        Statement statement = connect.createStatement();
        // Result set get the result of the SQL query
        ResultSet resultSet = statement.executeQuery("SELECT * FROM library_user");
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

    public String getUID(String name) throws SQLException {
        connect = new DbConnection().getConnection();
        // Result set get the result of the SQL query

        statement = connect.createStatement();
        // Result set get the result of the SQL query
        resultSet = statement.executeQuery("SELECT * FROM library_user where username = '" + name + "'");
        resultSet.next();
        String userid = resultSet.getString("uid");
        resultSet.close();

        connect.close();

        return userid;
    }

    public String getUIDResource(String name) throws SQLException {
        connect = new DbConnection().getConnection();
        // Result set get the result of the SQL query

        statement = connect.createStatement();
        // Result set get the result of the SQL query
        resultSet = statement.executeQuery("SELECT * FROM library_resource where name = '" + name + "'");
        resultSet.next();
        String userid = resultSet.getString("rid");
        resultSet.close();

        connect.close();

        return userid;
    }

    public String getTypeResource(String rid) throws SQLException {
        connect = new DbConnection().getConnection();
        // Result set get the result of the SQL query

        statement = connect.createStatement();
        // Result set get the result of the SQL query
        resultSet = statement.executeQuery("SELECT * FROM library_resource where rid = '" + rid + "'");
        resultSet.next();
        String userid = resultSet.getString("type");
        resultSet.close();

        connect.close();

        return userid;
    }

    public String getType(String uid) throws SQLException {
        connect = new DbConnection().getConnection();
        // Result set get the result of the SQL query

        statement = connect.createStatement();
        // Result set get the result of the SQL query
        resultSet = statement.executeQuery("SELECT * FROM library_user where uid = '" + uid + "'");
        resultSet.next();
        String userid = resultSet.getString("type");
        resultSet.close();

        connect.close();

        return userid;
    }

    public int issueRes(String userid, String rid) throws SQLException {
        String type = getType(userid);
        String due = "";
        if (type.equals("2")) {
            due = "(SELECT ADDDATE(CURRENT_DATE, 15))";
        } else {
            due = "(SELECT ADDDATE(CURRENT_DATE, 30))";
        }
        if (getTypeResource(rid).equalsIgnoreCase("2")) {
            due = "(SELECT ADDDATE(CURRENT_DATE, 1))";
        }
        connect = new DbConnection().getConnection();
        // Result set get the result of the SQL

        PreparedStatement preparedStatement = connect
                .prepareStatement("UPDATE library_resource SET issueDate = CURRENT_DATE, dueDate =" + due + ","
                + "issueTo =?,available = 0 WHERE rid =? AND available = 1 AND type!=3");
        // "myuser, webpage, datum, summery, COMMENTS from FEEDBACK.COMMENTS");
        // Parameters start with 1

        preparedStatement.setInt(1, Integer.parseInt(userid));
        preparedStatement.setInt(2, Integer.parseInt(rid));
        int k = preparedStatement.executeUpdate();

        connect.close();
        return k;



    }

    public void view(PrintWriter out, int ch, String uid) throws SQLException {

        connect = getConnection();

        // Statements allow to issue SQL queries to the database
        statement = connect.createStatement();
        // Result set get the result of the SQL query
        if (ch == 1) {
            resultSet = statement.executeQuery("SELECT * FROM library_resource WHERE issueTo='" + uid + "'");
            out.println("<h1>Issued Resources</h1><table>");

            out.println("<th>Name</th><th>Due Date</th>");

        } else if (ch == 2) {
            resultSet = statement.executeQuery("SELECT * FROM library_resource r,requests req WHERE r.rid=req.rid AND req.uid='" + uid + "'");
            out.println("<h1>Pending Requests</h1><table>");

            out.println("<th>Name</th><th>Return Date</th>");
        } else {
            String fines = viewFines(uid);
            out.println("<h3> Total Fines : " + fines + "Rs.</h3>");

            return;

        }

        while (resultSet.next()) {
            String user = resultSet.getString("dueDate");

            String name = resultSet.getString("name");
            out.println("<tr><td>" + name + "</td>");
            out.println("<td>" + user + "</td></tr>");

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

        out.println("<th>Name</th><th>Issue</th>");


        while (resultSet.next()) {
            String user = resultSet.getString("rid");

            String name = resultSet.getString("name");
            out.println("<tr><td>" + name + "</td>");
            out.println("<td><form action=\"http://localhost:8080/WebApplication1/isResource\" method=\"POST\"    >"
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

    public void deleteReq(PrintWriter out, String uid) throws Exception {

        connect = getConnection();

        // Statements allow to issue SQL queries to the database
        statement = connect.createStatement();
        // Result set get the result of the SQL query
        resultSet = statement.executeQuery("SELECT * FROM library_resource r,requests req WHERE r.rid=req.rid AND req.uid='" + uid + "'");
        out.println("<h1>Pending Requests</h1><table>");

        out.println("<th>Name</th><th>Delete</th>");


        while (resultSet.next()) {
            String user = resultSet.getString("rid");

            String name = resultSet.getString("name");
            out.println("<tr><td>" + name + "</td>");
            out.println("<td><form action=\"http://localhost:8080/WebApplication1/delReq\" method=\"POST\"    >"
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

    public int delete(PrintWriter out, String rid, String uid) throws Exception {

        Connection con = getConnection();
        // Result set get the result of the SQL query


        PreparedStatement preparedStatement = con
                .prepareStatement("DELETE  FROM requests WHERE rid = ? AND uid = ?");
        // "myuser, webpage, datum, summery, COMMENTS from FEEDBACK.COMMENTS");
        // Parameters start with 1
        preparedStatement.setInt(2, Integer.parseInt(uid));
        preparedStatement.setInt(1, Integer.parseInt(rid));

        int k = preparedStatement.executeUpdate();



        con.close();
        return k;

    }

    public void retResource(PrintWriter out, String uid) throws SQLException {

        connect = getConnection();

        // Statements allow to issue SQL queries to the database
        statement = connect.createStatement();
        // Result set get the result of the SQL query

        resultSet = statement.executeQuery("SELECT * FROM library_resource WHERE issueTo='" + uid + "'");
        out.println("<h1>Issued Resources</h1><table>");

        out.println("<th>Type</th><th>Name</th><th>Due Date</th><th>Return</th>");



        while (resultSet.next()) {
            String due = resultSet.getString("dueDate");
            String id = resultSet.getString("rid");
            String type = resultSet.getString("type");

            String name = resultSet.getString("name");
            out.println("<tr><td>" + type + "</td>");
            out.println("<td>" + name + "</td>");
            out.println("<td>" + due + "</td>");
            out.println("<td><form action=\"http://localhost:8080/WebApplication1/retRes\" method=\"POST\"    >"
                    + "<input type=\"submit\" name = \"rid\" value=\"" + id + "\">" + "</form></td></tr>");

            //String website = resultSet.getString("webpage");
            // String summery = resultSet.getString("summery");
            //  Date date = resultSet.getDate("datum");
            //String comment = resultSet.getString("comments");
        }
        out.println("</table>");
        resultSet.close();
        connect.close();


    }

    public int returnRes(String uid, String rid) throws SQLException {


        connect = new DbConnection().getConnection();
        // Result set get the result of the SQL query

        statement = connect.createStatement();

        PreparedStatement preparedStatement = connect
                .prepareStatement("UPDATE library_resource SET issueTo =0,available = 1 WHERE rid =? AND issueTo=?");
        // "myuser, webpage, datum, summery, COMMENTS from FEEDBACK.COMMENTS");
        // Parameters start with 1

        preparedStatement.setInt(1, Integer.parseInt(rid));
        preparedStatement.setInt(2, Integer.parseInt(uid));
        int k = preparedStatement.executeUpdate();

        statement = connect.createStatement();
        // Result set get the result of the SQL query
        resultSet = statement.executeQuery("SELECT *  FROM requests where rid = '" + rid + "'");
        if (resultSet.next()) {

            String id = resultSet.getString("id");


            String userid = resultSet.getString("uid");
            connect.close();

            resultSet.close();
            issueRes(userid, rid);
            connect = new DbConnection().getConnection();

            preparedStatement = connect
                    .prepareStatement("DELETE FROM requests WHERE uid=? AND rid=?");
            // "myuser, webpage, datum, summery, COMMENTS from FEEDBACK.COMMENTS");
            // Parameters start with 1

            preparedStatement.setInt(1, Integer.parseInt(userid));
            preparedStatement.setInt(2, Integer.parseInt(rid));
            preparedStatement.execute();
            connect.close();

            resultSet.close();
            return k;

        }

        return k;



    }

    public String renew(String uid, String name) throws SQLException {

        String type = getType(uid);
        String rid = getUIDResource(name);

        String due = "";
        if (type.equals("2")) {
            due = "(SELECT ADDDATE(CURRENT_DATE, 15))";
        } else {
            due = "(SELECT ADDDATE(CURRENT_DATE, 30))";
        }
        if (getTypeResource(rid).equalsIgnoreCase("2")) {
            due = "(SELECT ADDDATE(CURRENT_DATE, 1))";
        }
        connect = new DbConnection().getConnection();
        // Result set get the result of the SQL query

        statement = connect.createStatement();
        // Result set get the result of the SQL query

        resultSet = statement.executeQuery("SELECT count(*) as c FROM requests where rid = '" + rid + "'");
        resultSet.next();
        int count = Integer.parseInt(resultSet.getString("c"));
        if (count < 1) {
            PreparedStatement preparedStatement = connect
                    .prepareStatement("UPDATE library_resource SET  dueDate = " + due + " WHERE rid =? AND issueTo=?");
            preparedStatement.setInt(2, Integer.parseInt(uid));
            preparedStatement.setInt(1, Integer.parseInt(rid));
            int k = preparedStatement.executeUpdate();

            connect.close();
            return "" + k;
        } else {
            return "Request for the Resource already exist.";
        }

    }

    public List<String> getData(String query, String name) throws SQLException {
        ArrayList<String> list = new ArrayList<String>();
        String uid = getUID(name);
        statement = connect.createStatement();
        // Result set get the result of the SQL query
        resultSet = statement.executeQuery("SELECT * FROM library_resource WHERE  issueTo='" + uid + "'");

        while (resultSet.next()) {
            list.add(resultSet.getString("name"));

        }
        resultSet.close();
        connect.close();



        String country = null;
        query = query.toLowerCase();
        List<String> matched = new ArrayList<String>();
        for (int i = 0; i < list.size(); i++) {
            country = list.get(i).toLowerCase();
            if (country.startsWith(query)) {
                matched.add(list.get(i));
            }
        }
        return matched;
    }

    public int addRequest(String uid, String rid) throws SQLException {
        Connection con = new DbConnection().getConnection();
        // Result set get the result of the SQL query




        PreparedStatement preparedStatement = con
                .prepareStatement("INSERT INTO ass6.requests (id ,uid,rid)VALUES (NULL ,?,?)");
        // "myuser, webpage, datum, summery, COMMENTS from FEEDBACK.COMMENTS");
        // Parameters start with 1
        preparedStatement.setString(1, uid);
        preparedStatement.setString(2, rid);
        int k = preparedStatement.executeUpdate();

        con.close();
        return k;



    }

    public boolean borrow(String uid) throws Exception {
        String type = getType(uid);
        int max;
        if (type.equalsIgnoreCase("2")) {
            max = 3;
        } else {
            max = 6;
        }
        connect = getConnection();

        // Statements allow to issue SQL queries to the database
        statement = connect.createStatement();
        // Result set get the result of the SQL query
        resultSet = statement.executeQuery("SELECT count(*) as c FROM library_resource WHERE issueTo='" + uid + "'");

        int count = 0;
        if (resultSet.next()) {
            count = Integer.parseInt(resultSet.getString("c"));

        }

        connect.close();
        resultSet.close();

        if (count >= max) {
            return false;
        } else {
            return true;
        }

    }

    public String viewFines(String uid) throws SQLException {

        connect = getConnection();
        // Statements allow to issue SQL queries to the database
        statement = connect.createStatement();
        // Result set get the result of the SQL query
        resultSet = statement.executeQuery("SELECT DATEDIFF( CURDATE( ) , dueDate ) as  diff , type FROM library_resource "
                + "WHERE issueTo='" + uid + "'");
        int fine = 0;
        while (resultSet.next()) {
            int diff = resultSet.getInt("diff");
            int type = resultSet.getInt("type");
            if (diff > 0) {
                if (type == 2) {
                    fine = fine + diff * 500;
                } else {
                    fine = fine + diff * 100;
                }
            }
        }

        return "" + fine;
    }
}

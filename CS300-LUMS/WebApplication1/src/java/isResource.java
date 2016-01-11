/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import controller.DbConnection;
import controller.StudentDB;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Muhammad Ali Gulzar
 */
@WebServlet(name = "isResource", urlPatterns = {"/isResource"})
public class isResource extends HttpServlet {

    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            /* TODO output your page here. You may use following sample code. */
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet isResource</title>");
            out.println("</head>");
            out.println("<body  style=\"text-align: center;background-image:url('back.jpg');\">");

            StudentDB st = new StudentDB();
            String rid = st.getUIDResource((String)request.getParameter("rname"));
            
            int k = st.issueRes(st.getUID((String) request.getSession().getValue("uname")),rid );
            if (k > 0) {
                out.println("<h1>Resource Issued</h1>");
            } else {
                st.addRequest(st.getUID((String) request.getSession().getValue("uname")),rid);
                out.println("<h1>Resource Not Issued. Request Added</h1>");

            }
            out.println("<form action=\"http://localhost:8080/WebApplication1/StudentMenu.jsp\">"+
                    "<input type=\"submit\" value=\"Back\"></form>");
            out.println("</body>");
            out.println("</html>");
        } catch (SQLException ex) {
            ex.printStackTrace(out);
        } finally {
            out.close();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}

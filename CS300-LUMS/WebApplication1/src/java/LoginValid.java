/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import Model.Driver;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Muhammad Ali Gulzar
 */
@WebServlet(name = "LoginValid", urlPatterns = {"/LoginValid"})
public class LoginValid extends HttpServlet {

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
        Driver driver = new Driver("LUMS");

        try {
            /* TODO output your page here. You may use following sample code. */
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet LoginValid</title>");
            out.println("</head>");
            out.println("<body >");
            out.println("<h1>Servlet LoginValid at " + request.getContextPath() + "</h1>");
         if( driver.loginIO(request.getParameter("username"), request.getParameter("password"), Integer.parseInt(request.getParameter("type")), out))
         { // driver.loginIO("admin" , "admin" , 1 ,out);
            out.println("</body>");
            out.println("</html>");
            request.getSession().putValue("uname", request.getParameter("username"));
             request.getSession().putValue("t", request.getParameter("type"));
             int type = Integer.parseInt(request.getParameter("type"));
             if(type==1){
               request.getRequestDispatcher("/AdminMenu.jsp").forward(request, response);
             }else{
                   request.getRequestDispatcher("/StudentMenu.jsp").forward(request, response);
             }
             
             
             }else{
                      request.getRequestDispatcher("/loginfailed.jsp").forward(request, response);
      
         }    
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

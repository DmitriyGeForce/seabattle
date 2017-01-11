/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.web;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import ua.dao.AccountingDao;
import ua.dao.AccountingDaoImpl;

/**
 *
 * @author User
 */
@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    AccountingDao dao = new AccountingDaoImpl();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String message = null;
        if (AccountingUtility.paramExist(request.getParameter("created login")) && AccountingUtility.paramExist(request.getParameter("created password"))) {
            if (dao.register(request.getParameter("created login"), request.getParameter("created password"))) {
                response.sendRedirect("entrance");
                return;
            } else {
                message = "Username is already taken. Create another one";
            }
        }
        request.setAttribute("message", message);
        request.getRequestDispatcher("WEB-INF/pages/register.jsp").forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("WEB-INF/pages/register.jsp").forward(request, response);
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.web;

import java.io.IOException;
import java.util.Arrays;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import ua.dao.AccountingDao;
import ua.dao.AccountingDaoImpl;

/**
 *
 * @author User
 */
@WebServlet("/entrance")
public class EnterServlet extends HttpServlet {

    AccountingDao dao = new AccountingDaoImpl();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String message = null;
        if (AccountingUtility.paramExist(request.getParameter("login")) && AccountingUtility.paramExist(request.getParameter("password"))) {
            if (dao.enter(request.getParameter("login"), request.getParameter("password"))) {
                if (request.getParameter("login").equals("admin")) {
                    HttpSession session = request.getSession();
                    session.setAttribute("username", "admin");
                    response.sendRedirect("admin");
                } else {
                    HttpSession session = request.getSession();
                    session.setAttribute("username", request.getParameter("login"));
                    response.sendRedirect("user");
                }
                return;
            } else {
                message = "Incorrect login or password";
            }
        }
        request.setAttribute("message", message);
        request.getRequestDispatcher("WEB-INF/pages/entrance.jsp").forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("WEB-INF/pages/entrance.jsp").forward(request, response);
    }
}

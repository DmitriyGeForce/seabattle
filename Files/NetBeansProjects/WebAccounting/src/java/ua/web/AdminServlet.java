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
import ua.dao.model.AccountingTable;
import ua.dao.model.Users;

/**
 *
 * @author User
 */
@WebServlet("/admin")
public class AdminServlet extends HttpServlet {

    AccountingDao dao = new AccountingDaoImpl();
    AccountingUtility utility = new AccountingUtility();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("searchInput");
        String selectedFilter = request.getParameter("filter");
        if (!"".equals(username) && username != null) {
            Users user = dao.getUserByUsername(username);
            if (user != null) {
                request.setAttribute("Accounts", user.getAccountingTableCollection());
                request.setAttribute("username", user.getUsername());
                utility.checkFilter(selectedFilter, request, username);
            } else {
                request.setAttribute("error", "Can't find any results");
            }
        } else utility.checkFilter(selectedFilter, request, "admin");
        request.setAttribute("selectedFilter", selectedFilter);
        request.getRequestDispatcher("WEB-INF/pages/admin.jsp").forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("Accounts", dao.getAllUsersTable());
        request.getRequestDispatcher("WEB-INF/pages/admin.jsp").forward(request, response);
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import ua.dao.AccountingDao;
import ua.dao.AccountingDaoImpl;
import ua.dao.model.AccountingTable;
import static ua.web.AccountingUtility.*;

/**
 *
 * @author User
 */
@WebServlet("/user")
public class UserServlet extends HttpServlet {

    AccountingDao dao = new AccountingDaoImpl();
    AccountingUtility utility = new AccountingUtility();
    DateTimeFormatter format = DateTimeFormat.forPattern("HH:mm dd.MM.yyyy");

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        String username = session.getAttribute("username").toString();
        String error = "";
        String amount = request.getParameter("amount");
        if (!paramExist(amount)) {
            error = "Input amount of money";
            request.setAttribute("error", error);
        } else if (Integer.parseInt(amount) < 0) {
            error = "You can't input negative number";
            request.setAttribute("error", error);
        } else {
            String typeOfAction = request.getParameter("typeOfAction");
            if ("Income".equals(typeOfAction) || "Expense".equals(typeOfAction)) {
                if (paramExist(request.getParameter("edit"))) {
                    dao.update(new AccountingTable(Integer.parseInt(amount), Integer.parseInt(request.getParameter("editSampleId")), typeOfAction), username);
                } else {
                    dao.add(new AccountingTable(Integer.parseInt(amount), new DateTime().toString(format), typeOfAction), username);
                }
            }
            response.sendRedirect("user");
            return;
        }
        String selectedFilter = request.getParameter("filter");
        utility.checkFilter(selectedFilter, request, username);
        request.setAttribute("selectedFilter", selectedFilter);
        request.setAttribute("totalAmount", dao.getLastTotalAmount(username));
        request.getRequestDispatcher("WEB-INF/pages/user-page.jsp").forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        String username = session.getAttribute("username").toString();
        String removeParam = request.getParameter("remove");
        String editParam = request.getParameter("update");
        if (paramExist(removeParam)) {
            dao.remove(Integer.parseInt(removeParam), username);      // username for recount total amount only for one usertable
            response.sendRedirect("user");
            return;
        } else if (paramExist(editParam)) {
            AccountingTable account = dao.getAction(Integer.parseInt(editParam));
            request.setAttribute("editSample", account);
        }
        request.setAttribute("totalAmount", dao.getLastTotalAmount(username));
        request.setAttribute("Accounts", dao.getUserTable(username));
        request.getRequestDispatcher("WEB-INF/pages/user-page.jsp").forward(request, response);
    }

}

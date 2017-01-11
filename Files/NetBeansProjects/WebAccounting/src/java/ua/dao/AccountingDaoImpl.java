/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.dao;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import ua.dao.model.AccountingTable;
import ua.dao.model.Users;

/**
 *
 * @author User
 */
public class AccountingDaoImpl implements AccountingDao {

    List<String> alphaNum = new ArrayList<>();
    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("WebAccounting2.0PU");

    private EntityManager getEntityManger() {
        return emf.createEntityManager();
    }

    public AccountingDaoImpl() {
        for (char c = 'A'; c <= 'z'; c++) {
            String s = new String();
            s += c;
            alphaNum.add(s);
            if (c == 'Z') {
                c = 'a' - 1;
            }
        }

        for (int c = 0; c < 10; c++) {
            String s = new String();
            s += c;
            alphaNum.add(s);
        }
    }

    @Override
    public boolean enter(String login, String password) {
        EntityManager em = getEntityManger();
        TypedQuery<Users> namedQuery = em.createNamedQuery("Users.findByUsername", Users.class).setParameter("username", login);
        if (namedQuery.getResultList().isEmpty()) {
            return false;
        }
        Users user = namedQuery.getSingleResult();
        String inputPassword = getEncryptedPassword(password, user.getSalt());
        String DBpassword = user.getPassword();
        return inputPassword.equals(DBpassword);
    }

    @Override
    public boolean register(String login, String password) {
        EntityManager em = getEntityManger();
        TypedQuery<Users> namedQuery = em.createNamedQuery("Users.findByUsername", Users.class).setParameter("username", login);
        if (namedQuery.getResultList().isEmpty()) {
            Users user = getEncryptedUser(login, password);
            em.getTransaction().begin();
            em.persist(user);
            em.getTransaction().commit();
            em.close();
            return true;
        }
        return false;
    }

    @Override
    public List<AccountingTable> getAllUsersTable() {
       EntityManager em = getEntityManger();
       List <AccountingTable> table = em.createNamedQuery("AccountingTable.findAll", AccountingTable.class).getResultList();
       em.close();
       return table;
    }

    @Override
    public List<AccountingTable> getUserTable(String login) {
        EntityManager em = getEntityManger();
        List<AccountingTable> list = em.createNamedQuery("AccountingTable.findByUserId", AccountingTable.class).setParameter("userId", em.createNamedQuery("Users.findByUsername", Users.class).setParameter("username", login).getSingleResult()).getResultList();
        em.close();
        return list;
    }

    @Override
    public boolean add(AccountingTable action, String login) {
        EntityManager em = getEntityManger();
        int currentAmount = action.getAmount();
        int totalAmount = getLastTotalAmount(login);
        if (action.getTypeOfAction().equals("Income")) {
            action.setTotalAmount(totalAmount + currentAmount);
        } else {
            action.setTotalAmount(totalAmount - currentAmount);
        }
        action.setUserId(em.createNamedQuery("Users.findByUsername", Users.class).setParameter("username", login).getSingleResult());
        em.getTransaction().begin();
        em.persist(action);
        em.getTransaction().commit();
        em.close();
        return true;
    }

    @Override
    public boolean remove(int accountingTableId, String login) {
        EntityManager em = getEntityManger();
        em.getTransaction().begin();
        em.remove(em.find(AccountingTable.class, accountingTableId));
        em.getTransaction().commit();
        em.close();
        reCount(login);
        return true;
    }

    @Override
    public AccountingTable getAction(int accountingTableId) {
        EntityManager em = getEntityManger();
        Query action = em.createNativeQuery("SELECT * FROM accounting.accounting_table WHERE accounting_table_id =?", AccountingTable.class).setParameter(1, accountingTableId);
        if (action.getResultList().isEmpty()) {
            return null;
        } else {
            return (AccountingTable) action.getSingleResult();
        }
    }                               

    @Override
    public void update(AccountingTable editAccount, String login) {
        EntityManager em = getEntityManger();
        System.out.println(editAccount.getAccountingTableId());
        AccountingTable row = em.createNamedQuery("AccountingTable.findByAccountingTableId", AccountingTable.class).setParameter("accountingTableId", editAccount.getAccountingTableId()).getSingleResult();
        em.getTransaction().begin();
        row.setAmount(editAccount.getAmount());
        row.setTotalAmount(editAccount.getTotalAmount());
        row.setTypeOfAction(editAccount.getTypeOfAction());
        em.getTransaction().commit();
        em.close();
        reCount(login);
    }

    @Override
    public int getLastTotalAmount(String login) {
        EntityManager em = getEntityManger();
        int userId = em.createNamedQuery("Users.findByUsername", Users.class).setParameter("username", login).getSingleResult().getUserId();
        Query lastUserAction = em.createNativeQuery("SELECT * FROM accounting.accounting_table WHERE user_id = ? ORDER BY accounting_table_id DESC LIMIT 1", AccountingTable.class).setParameter(1, userId);
        if (lastUserAction.getResultList().isEmpty()) {
            em.close();
            return 0;
        } else {
            AccountingTable lastRow = (AccountingTable) lastUserAction.getSingleResult();
            em.close();
            return lastRow.getTotalAmount();
        }
    }

    private Users getEncryptedUser(String login, String password) {
        String salt = getRandomSalt();
        String saltedPassword = salt + password;
        MessageDigest md = getMessageDigest();
        md.update(saltedPassword.getBytes());
        byte byteData[] = md.digest();
        StringBuilder encryptedPassword = new StringBuilder();
        for (int i = 0; i < byteData.length; i++) {
            encryptedPassword.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }
        Users user = new Users(login, salt, encryptedPassword.toString());
        return user;
    }

    private String getRandomSalt() {
        StringBuilder salt = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            salt.append(alphaNum.get((int) (Math.random() * alphaNum.size())));
        }
        return salt.toString();
    }

    private MessageDigest getMessageDigest() {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException ex1) {
            Logger.getLogger(AccountingDaoImpl.class.getName()).log(Level.SEVERE, null, ex1);
        }
        return md;
    }

    private String getEncryptedPassword(String password, String salt) {
        String saltedPassword = salt + password;
        MessageDigest md = getMessageDigest();
        md.update(saltedPassword.getBytes());
        byte byteData[] = md.digest();
        StringBuilder encryptedPassword = new StringBuilder();
        for (int i = 0; i < byteData.length; i++) {
            encryptedPassword.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }
        return encryptedPassword.toString();
    }

    @Override
    public Users getUserByUsername(String login) {
        EntityManager em = getEntityManger();
        Users user = null;
        TypedQuery<Users> query = em.createNamedQuery("Users.findByUsername", Users.class).setParameter("username", login);
        if (!query.getResultList().isEmpty()) {
            user = query.getSingleResult();
        }
        em.close();
        return user;
    }
    
    public Users getUserByUserId(int userId) {
        EntityManager em = getEntityManger();
        Users user = em.createNamedQuery("Users.findByUserId", Users.class).setParameter("userId", userId).getSingleResult();
        em.close();
        return user;
    }

    @Override
    public void reCount(String login) {
        EntityManager em = getEntityManger();
        int totalAmount=0;
        List<AccountingTable> accounts = getUserTable(login);
        for (AccountingTable account : accounts) {
            AccountingTable tempAccount = em.createNamedQuery("AccountingTable.findByAccountingTableId", AccountingTable.class).setParameter("accountingTableId", account.getAccountingTableId()).getSingleResult();
            em.getTransaction().begin();
            int currentAmount = tempAccount.getAmount();
            if (tempAccount.getTypeOfAction().equals("Income")) {
                totalAmount = totalAmount + currentAmount;
                tempAccount.setTotalAmount(totalAmount);
            } else {
                totalAmount = totalAmount - currentAmount;
                tempAccount.setTotalAmount(totalAmount);
            }
            em.getTransaction().commit();
        }
        em.close();
    }
}

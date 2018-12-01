package com.paypal.desk;



import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DbHelper {

    private static final Connection connection = getConnection();

    private static Connection getConnection() {
        try {
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://127.0.0.1:3306/paypal",
                    "root",
                    ""
            );

            System.out.println("Connection successful");
            return connection;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    static int createUser(String firstName, String lastName) {
        String sql = "insert into users " +
                "(first_name, last_name)" +
                " values (" +
                "'" + firstName + "'" +
                ", " +
                "'" + lastName + "'" +
                ")";

        try {
            Statement statement = connection.createStatement();
            statement.execute(sql);

            String idSql = "select max(id) from users";
            Statement idStatement = connection.createStatement();
            ResultSet resultSet = idStatement.executeQuery(idSql);

            resultSet.next();

            return resultSet.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Updates the user balance in database
     * Sets balance = balance + amount
     *
     * @param userId id of the user in users table
     * @param amount double value of the amount to insert
     */
    static boolean cashFlow(int userId, double amount) {

        if (!(isValidUserID(userId))) return false;

        String sql = "update users " +
                "set balance = balance + "+
                amount+
                "where id = "+
                userId;

        try {
            Statement statement = connection.createStatement();
            statement.execute(sql);
        }catch (SQLException e){
            e.printStackTrace();
        }
        return true;
    }

    /**
     * Emulates a transaction between 2 users
     * Takes money from one account and adds to another account
     *
     * @param userFrom source user id
     * @param userTo   target user id
     * @param amount   transaction amount
     */
    static void transaction(int userFrom, int userTo, double amount) {
    int iii;
            if (!(isValidUserID(userFrom) & isValidUserID(userTo))) {
                System.out.println("Bad user ID!!!");
                return;
            }
            if (!isValidAmount(amount, userFrom)){
                System.out.println("Bad amount!!!");
                return;
            }

            String transact = "insert into transactions " +
                "(user_from, user_to, transaction_amount)" +
                " values ("+
                userFrom +","+
                userTo  +","+
                amount+")";

            String userFromText = "update users " +
                "set balance = balance - "+
                amount+
                " where id = "+
                userFrom;

            String userToText = "update users " +
                "set balance = balance + "+
                amount+
                " where id = "+
                userTo;


        try {
            Statement statementTransactions = connection.createStatement();

            Statement statementUserFrom = connection.createStatement();

            Statement statementUsersTo = connection.createStatement();

            statementTransactions.execute(transact);

            statementUserFrom.execute(userFromText);

            statementUsersTo.execute(userToText);

        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    private static boolean isValidAmount(double amount, int user) {

        if (amount<0) return false;

        String amountText = "select balance from users"+
                " where id = "+ user;
        try {
            Statement userFromStatement = connection.createStatement();

            ResultSet resultSet = userFromStatement.executeQuery(amountText);

            resultSet.next();

            double balance = resultSet.getDouble(1);

            if (balance<amount) return false;

        } catch (SQLException e) {

            return false;
        }

        return true;
    }

    private static boolean isValidUserID(int user) {

        String userText = "select * from users"+
                " where id = "+ user;
        try {
            Statement userFromStatement = connection.createStatement();
            ResultSet resultSet = userFromStatement.executeQuery(userText);
            if (!resultSet.next())
                return false;

        } catch (SQLException e) {
            return false;
        }

        return true;
    }

    static List<User> listUsers() {
        String sql = "select * from users";

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            List<User> userList = new ArrayList<>();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                double balance = resultSet.getDouble("balance");

                userList.add(new User(
                        id, firstName, lastName, balance
                ));
            }
            return userList;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}

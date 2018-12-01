package com.paypal.desk;

import java.text.MessageFormat;
import java.util.List;
import java.util.Scanner;

public class PaypalDesk {

    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        System.out.println("Welcome to paypal");
        System.out.println("Enter command");

        while (true) {
            System.out.println("(C) -> Create user");
            System.out.println("(L) -> List users");
            System.out.println("(+) -> Cash in");
            System.out.println("(-) -> Cash out");
            System.out.println("(T) -> Transaction");
            System.out.println("(Q) -> Quit");

            String command = scanner.nextLine();

            switch (command) {
                case "c":
                case "C":
                    createUser();
                    break;
                case "l":
                case "L":
                    listUsers();
                    break;
                case "+":
                    cashIn();
                    break;
                case "-":
                    cashOut();
                    break;
                case "t":
                case "T":
                    transaction();
                    break;
                case "q":
                case "Q":
                    return;
                default:
                    System.out.println("Wrong command!!! Try again.");
                    break;
            }
        }
    }

    private static void createUser() {
        System.out.print("First name: ");
        String firstName = scanner.nextLine();

        System.out.print("Last name: ");
        String lastName = scanner.nextLine();

        int userId = DbHelper.createUser(
                firstName, lastName
        );

        if (userId != -1) {
            System.out.println(
                    MessageFormat.format(
                            "User {0} created successfully",
                            userId
                    )
            );
        } else {
            System.out.println(
                    "Error while creating the user"
            );
        }
    }

    private static void listUsers() {
        List<User> users = DbHelper.listUsers();
        if (users == null) return;

        for (User user : users) {
            System.out.println(user);
        }
    }

    private static void cashIn() {

        int userId;
        double amount;

        try {
                userId= getUserIdFromConsole("User id: ");

                amount = getAmountFromConsole();

        }catch (NumberFormatException e){

            System.out.println("Error while parsing numbers from console!!!");
            System.out.println("Try again");
            return;
        }

      if (DbHelper.cashFlow(userId, amount)) {
          System.out.println("Cash in successful");
      }
      else System.out.println("Bad User ID!!!");
    }

    private static void cashOut() {

        int userId;
        double amount;

        try {
            userId = getUserIdFromConsole("User id: ");

            amount = getAmountFromConsole();
        }catch (NumberFormatException e){
            System.out.println("Error while parsing numbers from console!!!");
            System.out.println("Try again");
            return;

        }

        if (DbHelper.cashFlow(userId, -amount)){
            System.out.println("Cash out successful");
        }else
            System.out.println("Bad User ID!!!");
    }

    private static void transaction() {

        int userFrom;
        int userTo;
        double amount;

        try {

            userFrom = getUserIdFromConsole(
                    "User id: "
            );
            userTo = getUserIdFromConsole(
                    "Target user id: "
            );

            amount = getAmountFromConsole();
        }catch (NumberFormatException e){
            System.out.println("Error while parsing numbers from console!!!");
            System.out.println("Try again");
            return;
        }

        DbHelper.transaction(
                userFrom, userTo, amount
        );

        System.out.println("Transaction successful");
    }


    private static int getUserIdFromConsole(String message) {
        System.out.print(message);

        int i;

        try{
            i = Integer.parseInt(scanner.nextLine());
        }catch (NumberFormatException e){
            throw new NumberFormatException();
        }

        return i;
    }

    private static double getAmountFromConsole() {
        System.out.println("Amount: ");
        double d;
        try {
            d = Double.parseDouble(scanner.nextLine());
        }catch (NumberFormatException e){
            throw new NumberFormatException();
        }
         return d;

    }
}

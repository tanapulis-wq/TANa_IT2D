

package com.eventregistration.ui;

import com.eventregistration.dao.UserDao;
import com.eventregistration.model.User;

import java.util.Scanner;

public class Menu {

    private final Scanner scanner;
    private final UserDao userDao;

    public Menu(Scanner scanner, UserDao userDao) {
        this.scanner = scanner;
        this.userDao = userDao;
    }

    public void start() {
        boolean running = true;

        while (running) {
            System.out.println("\n================================");
            System.out.println("    EVENT REGISTRATION SYSTEM");
            System.out.println("================================");
            System.out.println("1. Register Account");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> register();
                case "2" -> login();
                case "3" -> {
                    running = false;
                    System.out.println(
                            "Thank you for using the system!");
                }
                default -> System.out.println(
                        "Invalid choice. Try again.");
            }
        }
    }

    private void register() {
        System.out.println("\n--- REGISTER ACCOUNT ---");

        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        if (username.isBlank() || password.isBlank()) {
            System.out.println(
                    "Username and password are required.");
            return;
        }

        System.out.println("Select role:");
        System.out.println("1. TEACHER");
        System.out.println("2. STUDENT");
        System.out.print("Choose role: ");

        String choice = scanner.nextLine().trim();
        String role;

        if (choice.equals("1")) {
            role = "TEACHER";
        } else if (choice.equals("2")) {
            role = "STUDENT";
        } else {
            System.out.println("Invalid role.");
            return;
        }

        boolean success = userDao.registerUser(
                username, password, role);

        if (success) {
            System.out.println(
                    "Registration successful!");
            System.out.println(
                    "Your account status is PENDING.");
        } else {
            System.out.println(
                    "Registration failed. Please try again.");
        }
    }

    private void login() {
        System.out.println("\n--- LOGIN ---");

        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        User user = userDao.login(username, password);

        if (user == null) {
            System.out.println(
                    "Invalid username or password.");
            return;
        }

        System.out.println("\nLogin successful!");
        System.out.println("Welcome, " + user.username());
        System.out.println("Role: " + user.roles());
        System.out.println("Account Status: " + user.status());

        if ("PENDING".equals(user.status())) {
            System.out.println(
                    "Your account is waiting for approval.");
        }

        userSession(user);
    }

    private void userSession(User user) {
        boolean loggedIn = true;

        while (loggedIn) {
            System.out.println("\n--- USER MENU ---");
            System.out.println("Logged in as: " + user.username());
            System.out.println("1. View Account Information");
            System.out.println("2. Logout");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> {
                    System.out.println("User ID: " + user.id());
                    System.out.println(
                            "Username: " + user.username());
                    System.out.println("Role: " + user.roles());
                    System.out.println(
                            "Status: " + user.status());
                }
                case "2" -> {
                    loggedIn = false;
                    System.out.println("You have logged out.");
                }
                default -> System.out.println(
                        "Invalid choice. Try again.");
            }
        }
    }
}

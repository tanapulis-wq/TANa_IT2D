package com.eventregistration.ui;

import com.eventregistration.dao.UserDao;
import com.eventregistration.model.User;

import java.util.Scanner;

/**
 * Single entry for registration and login. After a successful login the user
 * is routed to the menu that matches their role (ADMIN, ORGANIZER, PARTICIPANT).
 */
public class Authentication {
    private final Scanner scanner;
    private final UserDao userDao;

    public Authentication(Scanner scanner, UserDao userDao) {
        this.scanner = scanner;
        this.userDao = userDao;
    }

    public void start() {
        while (true) {
            System.out.println("\n=== EVENT REGISTRATION SYSTEM ===");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Choice: ");
            switch (scanner.nextLine().trim()) {
                case "1" -> register();
                case "2" -> login();
                case "3" -> {
                    System.out.println("Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private void register() {
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        if (username.isBlank() || password.isBlank()) {
            System.out.println("Username and password required.");
            return;
        }

        System.out.println("1. ORGANIZER");
        System.out.println("2. PARTICIPANT");
        System.out.print("Role: ");
        String role = switch (scanner.nextLine().trim()) {
            case "1" -> "ORGANIZER";
            case "2" -> "PARTICIPANT";
            default -> "";
        };
        if (role.isEmpty()) {
            System.out.println("Invalid role.");
            return;
        }

        if (userDao.registerUser(username, password, role))
            System.out.println("Registration successful. Status: PENDING.");
        else
            System.out.println("Registration failed.");
    }

    private void login() {
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        // Demo administrator account for the coursework console application.
        if ("admin".equals(username) && "admin123".equals(password)) {
            new AdminMenu(scanner, userDao).start(new User(0, "admin", "", "ADMIN", "APPROVED"));
            return;
        }

        User user = userDao.login(username, password);
        if (user == null) {
            System.out.println("Invalid credentials.");
            return;
        }
        if (!"APPROVED".equalsIgnoreCase(user.status()) && !"ACTIVE".equalsIgnoreCase(user.status())) {
            System.out.println("Account status: " + user.status() + ". Approval required before access.");
            return;
        }

        // Route to the menu for the user's role.
        switch (user.roles()) {
            case "ADMIN" -> new AdminMenu(scanner, userDao).start(user);
            case "ORGANIZER" -> new OrganizerMenu(scanner, userDao).start(user);
            case "PARTICIPANT" -> new ParticipantMenu(scanner, userDao).start(user);
            default -> System.out.println("Unknown assigned role: " + user.roles());
        }
    }
}
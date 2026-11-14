package com.scc.ui;

import com.scc.dao.ActivityLogDao;
import com.scc.dao.SecurityUtil;
import com.scc.dao.UserDao;
import com.scc.model.ActivityLog;
import com.scc.model.User;

import java.time.LocalDateTime;
import java.util.Scanner;

public class Authentication {

    public static void start() {
        Scanner scanner = new Scanner(System.in);
        UserDao userDao = new UserDao();
        ActivityLogDao logDao = new ActivityLogDao();

        while (true) {
            System.out.println("\n=== Event Registration System - Authentication ===");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Choose option: ");

            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Enter a number between 1 and 3.");
                continue;
            }

            switch (choice) {
                case 1:
                    User user = login(scanner, userDao, logDao);
                    if (user != null) {
                        navigateUserMenu(user, scanner);
                    }
                    break;
                case 2:
                    register(scanner, userDao);
                    break;
                case 3:
                    System.out.println("Exiting application. Goodbye!");
                    scanner.close();
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static User login(Scanner scanner, UserDao userDao, ActivityLogDao logDao) {
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        User user = userDao.getUserByEmail(email);
        if (user != null && user.getPassword().equals(SecurityUtil.hashPassword(password))) {
            logDao.logAction(new ActivityLog(user.getUserId(), "LOGIN", LocalDateTime.now().toString(), "User logged in"));
            System.out.println("Login successful! Welcome, " + user.getName() + " (" + user.getRole() + ")");
            return user;
        }
        System.out.println("Invalid email or password.");
        return null;
    }

    private static void register(Scanner scanner, UserDao userDao) {
        System.out.print("Enter full name: ");
        String name = scanner.nextLine();
        System.out.print("Enter email: ");
        String email = scanner.nextLine();

        if (userDao.getUserByEmail(email) != null) {
            System.out.println("Email already registered.");
            return;
        }

        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        System.out.print("Enter role (ADMIN / ORGANIZER / PARTICIPANT): ");
        String role = scanner.nextLine().toUpperCase();

        if (!role.equals("ADMIN") && !role.equals("ORGANIZER") && !role.equals("PARTICIPANT")) {
            System.out.println("Invalid role specified.");
            return;
        }

        User newUser = new User(name, email, SecurityUtil.hashPassword(password), role, "ACTIVE");
        if (userDao.registerUser(newUser)) {
            System.out.println("Registration successful! You can now log in.");
        } else {
            System.out.println("Registration failed.");
        }
    }

    private static void navigateUserMenu(User user, Scanner scanner) {
        switch (user.getRole()) {
            case "ADMIN":
                new AdminMenu().showMenu(user, scanner);
                break;
            case "ORGANIZER":
                new OrganizerMenu().showMenu(user, scanner);
                break;
            case "PARTICIPANT":
                new ParticipantMenu().showMenu(user, scanner);
                break;
            default:
                System.out.println("Unknown role permission.");
        }
    }
}
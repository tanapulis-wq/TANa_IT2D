package com.eventregistration.ui;

import com.eventregistration.dao.UserDao;
import com.eventregistration.model.User;
import java.util.List;
import java.util.Scanner;

public class AdminMenu {
    private final Scanner scanner;
    private final UserDao userDao;

    public AdminMenu(Scanner scanner, UserDao userDao) {
        this.scanner = scanner;
        this.userDao = userDao;
    }

    public void start(User admin) {
        while (true) {
            System.out.println("\n========== ADMIN MENU ==========");
            System.out.println("Logged in as: " + admin.username());
            System.out.println("1. View All System Users");
            System.out.println("2. View Pending Registrations");
            System.out.println("3. Approve User Account");
            System.out.println("4. Update User Role/Status");
            System.out.println("5. Logout");
            System.out.print("Choose an option: ");
            switch (scanner.nextLine().trim()) {
                case "1" -> printUsers(userDao.getAllUsers());
                case "2" -> printUsers(userDao.getPendingUsers());
                case "3" -> approve();
                case "4" -> updateUser(admin);
                case "5" -> { System.out.println("Admin logged out."); return; }
                default -> System.out.println("Invalid choice. Please choose 1-5.");
            }
        }
    }

    private void approve() {
        List<User> pending = userDao.getPendingUsers();
        if (pending.isEmpty()) {
            System.out.println("There are no pending registrations.");
            return;
        }
        System.out.println("\n--- PENDING REGISTRATIONS ---");
        printUsers(pending);
        int userId = readIntInput("Enter User ID to approve (0 to cancel): ");
        if (userId == 0) {
            System.out.println("Approval cancelled.");
            return;
        }
        boolean exists = pending.stream().anyMatch(user -> user.id() == userId);
        if (!exists) {
            System.out.println("That ID is not in the pending list. No changes made.");
            return;
        }
        if (userDao.approveUser(userId))
            System.out.println("SUCCESS: User ID " + userId + " has been approved.");
        else
            System.out.println("Approval failed. The account may already have been updated.");
    }

    private void updateUser(User admin) {
        System.out.println("\n--- UPDATE USER ROLE / STATUS ---");
        List<User> users = userDao.getAllUsers();
        printUsers(users);

        int targetUserId = readIntInput("Enter Target User ID (0 to cancel): ");
        if (targetUserId == 0) {
            System.out.println("Update cancelled.");
            return;
        }
        if (targetUserId == admin.id()) {
            System.out.println("WARNING: Modifying the currently active admin account is restricted.");
            return;
        }
        if (users.stream().noneMatch(u -> u.id() == targetUserId)) {
            System.out.println("No user found with ID " + targetUserId + ". No changes made.");
            return;
        }
        String newRole = readValidRole();
        if (newRole == null) { System.out.println("Update cancelled."); return; }
        String newStatus = readValidStatus();
        if (newStatus == null) { System.out.println("Update cancelled."); return; }

        if (userDao.updateUserRoleAndStatus(targetUserId, newRole, newStatus))
            System.out.println("SUCCESS: User ID " + targetUserId + " updated to role "
                    + newRole + " and status " + newStatus + ".");
        else
            System.out.println("Update failed. No changes were made.");
    }

    /** Keeps asking until the user enters a whole number; letters/symbols never crash the program. */
    private int readIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number (digits only).");
            }
        }
    }

    /** Returns STUDENT, TEACHER or ADMIN, or null if the admin enters 0 to cancel. */
    private String readValidRole() {
        while (true) {
            System.out.print("Enter new role (STUDENT / TEACHER / ADMIN, 0 to cancel): ");
            String input = scanner.nextLine().trim().toUpperCase();
            if (input.equals("0")) return null;
            if (input.equals("STUDENT") || input.equals("TEACHER") || input.equals("ADMIN")) return input;
            System.out.println("Invalid role. Please enter STUDENT, TEACHER or ADMIN.");
        }
    }

    /** Returns APPROVED or PENDING, or null if the admin enters 0 to cancel. */
    private String readValidStatus() {
        while (true) {
            System.out.print("Enter new status (APPROVED / PENDING, 0 to cancel): ");
            String input = scanner.nextLine().trim().toUpperCase();
            if (input.equals("0")) return null;
            if (input.equals("APPROVED") || input.equals("PENDING")) return input;
            System.out.println("Invalid status. Please enter APPROVED or PENDING.");
        }
    }

    private void printUsers(List<User> users) {
        String border = "+-------+-----------------+--------------+------------+";
        System.out.println(border);
        System.out.printf("| %-5s | %-15s | %-12s | %-10s |%n", "ID", "USERNAME", "ROLE", "STATUS");
        System.out.println(border);
        if (users.isEmpty()) {
            System.out.printf("| %-5s | %-15s | %-12s | %-10s |%n", "-", "No users found", "-", "-");
        } else {
            for (User user : users) {
                System.out.printf("| %-5d | %-15.15s | %-12.12s | %-10.10s |%n",
                        user.id(), user.username(), user.roles(), user.status());
            }
        }
        System.out.println(border);
    }
}

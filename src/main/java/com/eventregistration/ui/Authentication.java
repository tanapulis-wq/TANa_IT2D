package com.eventregistration.ui;
import com.eventregistration.dao.UserDao;
import com.eventregistration.model.User;
import java.util.Scanner;
public class Authentication {
    private final Scanner scanner;
    private final UserDao userDao;
    public Authentication(Scanner scanner, UserDao userDao) { this.scanner=scanner; this.userDao=userDao; }
    public void start() {
        while (true) {
            System.out.println("\n=== EVENT REGISTRATION SYSTEM ===");
            System.out.println("1. Register\n2. Login\n3. Exit");
            System.out.print("Choice: ");
            switch (scanner.nextLine().trim()) {
                case "1" -> register(); case "2" -> login();
                case "3" -> { System.out.println("Goodbye!"); return; }
                default -> System.out.println("Invalid choice.");
            }
        }
    }
    private void register() {
        System.out.print("Username: "); String username=scanner.nextLine().trim();
        System.out.print("Password: "); String password=scanner.nextLine();
        if (username.isBlank() || password.isBlank()) { System.out.println("Username and password required."); return; }
        System.out.println("1. TEACHER\n2. STUDENT"); System.out.print("Role: ");
        String role = switch (scanner.nextLine().trim()) { case "1" -> "TEACHER"; case "2" -> "STUDENT"; default -> ""; };
        if (role.isEmpty()) { System.out.println("Invalid role."); return; }
        if (userDao.registerUser(username,password,role)) System.out.println("Registration successful. Status: PENDING.");
        else System.out.println("Registration failed.");
    }
    private void login() {
        System.out.print("Username: "); String username=scanner.nextLine().trim();
        System.out.print("Password: "); String password=scanner.nextLine();
        User user=userDao.login(username,password);
        if (user==null) { System.out.println("Invalid credentials."); return; }
        if (!"APPROVED".equalsIgnoreCase(user.status()) && !"ACTIVE".equalsIgnoreCase(user.status())) {
            System.out.println("Account status: " + user.status() + ". Approval required before access."); return;
        }
        switch (user.roles()) {
            case "TEACHER" -> new TeacherMenu(scanner,userDao).start(user);
            case "STUDENT" -> new StudentMenu(scanner,userDao).start(user);
            default -> System.out.println("Unknown assigned role.");
        }
    }
}

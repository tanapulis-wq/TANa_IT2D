package com.eventregistration.ui;
import com.eventregistration.dao.UserDao;
import com.eventregistration.model.User;
import java.util.Scanner;
public class ParticipantMenu {
    private final Scanner scanner;
    private final UserDao userDao;
    public ParticipantMenu(Scanner scanner, UserDao userDao) { this.scanner=scanner; this.userDao=userDao; }
    public void start(User user) {
        while (true) {
            System.out.println("\n--- PARTICIPANT MENU ---");
            System.out.println("1. View account\n2. Logout");
            System.out.print("Choice: ");
            switch (scanner.nextLine().trim()) {
                case "1" -> System.out.println("ID: " + user.id() + " | Username: " + user.username() + " | Role: " + user.roles() + " | Status: " + user.status());
                case "2" -> { return; }
                default -> System.out.println("Invalid choice.");
            }
        }
    }
}

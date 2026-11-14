package com.scc.ui;

import com.scc.dao.ActivityLogDao;
import com.scc.dao.EventDao;
import com.scc.model.ActivityLog;
import com.scc.model.Event;
import com.scc.model.User;

import java.util.List;
import java.util.Scanner;

public class AdminMenu {
    private EventDao eventDao = new EventDao();
    private ActivityLogDao logDao = new ActivityLogDao();

    public void showMenu(User user, Scanner scanner) {
        while (true) {
            System.out.println("\n--- Administrator Menu ---");
            System.out.println("1. Manage Users & System Records");
            System.out.println("2. Add, Update, or Delete Events");
            System.out.println("3. View Reports & Activity Logs");
            System.out.println("4. Logout");
            System.out.print("Choose option: ");

            int choice = Integer.parseInt(scanner.nextLine());
            if (choice == 1) {
                System.out.println("Feature: Manage system users and record statuses.");
            } else if (choice == 2) {
                manageEvents(scanner, user.getUserId());
            } else if (choice == 3) {
                viewReportsAndLogs();
            } else if (choice == 4) {
                break;
            }
        }
    }

    private void manageEvents(Scanner scanner, int userId) {
        System.out.println("1. Add Event\n2. Update Event\n3. Delete/Cancel Event");
        int ch = Integer.parseInt(scanner.nextLine());
        if (ch == 1) {
            System.out.print("Event Name: "); String name = scanner.nextLine();
            System.out.print("Date (YYYY-MM-DD): "); String date = scanner.nextLine();
            System.out.print("Time: "); String time = scanner.nextLine();
            System.out.print("Venue: "); String venue = scanner.nextLine();
            System.out.print("Description: "); String desc = scanner.nextLine();
            System.out.print("Capacity: "); int cap = Integer.parseInt(scanner.nextLine());

            Event ev = new Event(name, date, time, venue, desc, cap, "ACTIVE");
            eventDao.addEvent(ev);
            logDao.logAction(new ActivityLog(userId, "EVENT_CREATE", java.time.LocalDateTime.now().toString(), "Created event: " + name));
            System.out.println("Event added successfully!");
        } else if (ch == 3) {
            System.out.print("Enter Event ID to cancel: ");
            int id = Integer.parseInt(scanner.nextLine());
            eventDao.deleteEvent(id);
            System.out.println("Event canceled.");
        }
    }

    private void viewReportsAndLogs() {
        System.out.println("\n=== Event Registration & Activity Logs Report ===");
        List<ActivityLog> logs = logDao.getAllLogs();
        for (ActivityLog l : logs) {
            System.out.println("[" + l.getDateTime() + "] User ID " + l.getUserId() + " - " + l.getAction() + ": " + l.getDescription());
        }
    }
}
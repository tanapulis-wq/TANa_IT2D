package com.scc.ui;

import com.scc.dao.EventDao;
import com.scc.dao.RegistrationDao;
import com.scc.model.Event;
import com.scc.model.Registration;
import com.scc.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class ParticipantMenu {
    private EventDao eventDao = new EventDao();
    private RegistrationDao registrationDao = new RegistrationDao();

    public void showMenu(User user, Scanner scanner) {
        while (true) {
            System.out.println("\n--- Participant Menu ---");
            System.out.println("1. View Available Events & Register");
            System.out.println("2. View Personal Registration Information / Cancel");
            System.out.println("3. Logout");
            System.out.print("Choose option: ");

            int choice = Integer.parseInt(scanner.nextLine());
            if (choice == 1) {
                List<Event> events = eventDao.getAllEvents();
                System.out.println("\n--- Available Events ---");
                for (Event e : events) {
                    System.out.println("ID: " + e.getEventId() + " | " + e.getEventName() + " on " + e.getEventDate() + " at " + e.getVenue());
                }
                System.out.print("Enter Event ID to register (0 to skip): ");
                int eventId = Integer.parseInt(scanner.nextLine());
                if (eventId > 0) {
                    Registration reg = new Registration(user.getUserId(), eventId, LocalDate.now().toString(), "REGISTERED");
                    registrationDao.registerParticipant(reg);
                    System.out.println("Successfully registered for event!");
                }
            } else if (choice == 2) {
                List<Registration> regs = registrationDao.getRegistrationsByUser(user.getUserId());
                System.out.println("\n--- My Registrations ---");
                for (Registration r : regs) {
                    System.out.println("Reg ID: " + r.getRegistrationId() + " | Event ID: " + r.getEventId() + " | Status: " + r.getRegistrationStatus());
                }
                System.out.print("Enter Registration ID to cancel (0 to skip): ");
                int regId = Integer.parseInt(scanner.nextLine());
                if (regId > 0) {
                    registrationDao.cancelRegistration(regId);
                    System.out.println("Registration canceled.");
                }
            } else if (choice == 3) {
                break;
            }
        }
    }
}
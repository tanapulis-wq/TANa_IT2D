package com.scc.ui;

import com.scc.dao.ActivityLogDao;
import com.scc.dao.EventDao;
import com.scc.dao.RegistrationDao;
import com.scc.model.ActivityLog;
import com.scc.model.Event;
import com.scc.model.Registration;
import com.scc.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class OrganizerMenu {
    private EventDao eventDao = new EventDao();
    private RegistrationDao registrationDao = new RegistrationDao();
    private ActivityLogDao logDao = new ActivityLogDao();

    public void showMenu(User user, Scanner scanner) {
        while (true) {
            System.out.println("\n--- Event Organizer Menu ---");
            System.out.println("1. Create a New Event");
            System.out.println("2. Update Existing Event Details");
            System.out.println("3. View Registered Participants per Event");
            System.out.println("4. Record Participant Attendance");
            System.out.println("5. Logout");
            System.out.print("Choose option: ");

            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number between 1 and 5.");
                continue;
            }

            switch (choice) {
                case 1:
                    createEvent(user, scanner);
                    break;
                case 2:
                    updateEvent(user, scanner);
                    break;
                case 3:
                    viewRegisteredParticipants(scanner);
                    break;
                case 4:
                    recordAttendance(user, scanner);
                    break;
                case 5:
                    System.out.println("Logging out from Organizer account.");
                    return;
                default:
                    System.out.println("Invalid option. Please choose a valid menu number.");
            }
        }
    }

    private void createEvent(User user, Scanner scanner) {
        System.out.println("\n--- Create New Event ---");
        System.out.print("Enter Event Name: ");
        String eventName = scanner.nextLine();
        System.out.print("Enter Event Date (YYYY-MM-DD): ");
        String eventDate = scanner.nextLine();
        System.out.print("Enter Event Time (e.g., 10:00 AM): ");
        String eventTime = scanner.nextLine();
        System.out.print("Enter Venue: ");
        String venue = scanner.nextLine();
        System.out.print("Enter Description: ");
        String description = scanner.nextLine();
        System.out.print("Enter Capacity: ");

        int capacity;
        try {
            capacity = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid capacity number. Event creation failed.");
            return;
        }

        Event newEvent = new Event(eventName, eventDate, eventTime, venue, description, capacity, "ACTIVE");
        boolean success = eventDao.addEvent(newEvent);

        if (success) {
            logDao.logAction(new ActivityLog(user.getUserId(), "EVENT_CREATE", LocalDateTime.now().toString(), "Organizer created event: " + eventName));
            System.out.println("Event created successfully!");
        } else {
            System.out.println("Failed to create event.");
        }
    }

    private void updateEvent(User user, Scanner scanner) {
        System.out.println("\n--- Update Event Details ---");
        List<Event> events = eventDao.getAllEvents();
        if (events.isEmpty()) {
            System.out.println("No active events found.");
            return;
        }

        System.out.println("Available Events:");
        for (Event e : events) {
            System.out.println("ID: " + e.getEventId() + " | " + e.getEventName() + " (" + e.getEventDate() + ")");
        }

        System.out.print("Enter Event ID to update: ");
        int eventId;
        try {
            eventId = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid Event ID.");
            return;
        }

        Event targetEvent = null;
        for (Event e : events) {
            if (e.getEventId() == eventId) {
                targetEvent = e;
                break;
            }
        }

        if (targetEvent == null) {
            System.out.println("Event ID not found.");
            return;
        }

        System.out.print("Enter new Event Name [" + targetEvent.getEventName() + "]: ");
        String name = scanner.nextLine();
        if (!name.isEmpty()) targetEvent.setEventName(name);

        System.out.print("Enter new Date [" + targetEvent.getEventDate() + "]: ");
        String date = scanner.nextLine();
        if (!date.isEmpty()) targetEvent.setEventDate(date);

        System.out.print("Enter new Time [" + targetEvent.getEventTime() + "]: ");
        String time = scanner.nextLine();
        if (!time.isEmpty()) targetEvent.setEventTime(time);

        System.out.print("Enter new Venue [" + targetEvent.getVenue() + "]: ");
        String venue = scanner.nextLine();
        if (!venue.isEmpty()) targetEvent.setVenue(venue);

        System.out.print("Enter new Description [" + targetEvent.getDescription() + "]: ");
        String desc = scanner.nextLine();
        if (!desc.isEmpty()) targetEvent.setDescription(desc);

        System.out.print("Enter new Capacity [" + targetEvent.getCapacity() + "]: ");
        String capStr = scanner.nextLine();
        if (!capStr.isEmpty()) {
            try {
                targetEvent.setCapacity(Integer.parseInt(capStr));
            } catch (NumberFormatException ignored) {}
        }

        boolean success = eventDao.updateEvent(targetEvent);
        if (success) {
            logDao.logAction(new ActivityLog(user.getUserId(), "EVENT_UPDATE", LocalDateTime.now().toString(), "Updated event ID: " + eventId));
            System.out.println("Event updated successfully!");
        } else {
            System.out.println("Failed to update event.");
        }
    }

    private void viewRegisteredParticipants(Scanner scanner) {
        System.out.println("\n--- View Registered Participants ---");
        List<Event> events = eventDao.getAllEvents();
        if (events.isEmpty()) {
            System.out.println("No events found.");
            return;
        }

        for (Event e : events) {
            System.out.println("ID: " + e.getEventId() + " | " + e.getEventName());
        }

        System.out.print("Enter Event ID to view participants: ");
        int eventId;
        try {
            eventId = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid Event ID.");
            return;
        }

        List<Registration> registrations = registrationDao.getRegistrationsByEvent(eventId);
        if (registrations.isEmpty()) {
            System.out.println("No participants registered for this event yet.");
            return;
        }

        System.out.println("\nRegistered Participants List:");
        for (Registration reg : registrations) {
            System.out.println("Registration ID: " + reg.getRegistrationId() + " | User ID: " + reg.getUserId() + " | Status: " + reg.getRegistrationStatus());
        }
    }

    private void recordAttendance(User user, Scanner scanner) {
        System.out.println("\n--- Record Participant Attendance ---");
        System.out.print("Enter Registration ID to mark attendance: ");
        int regId;
        try {
            regId = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid Registration ID.");
            return;
        }

        boolean success = registrationDao.updateRegistrationStatus(regId, "ATTENDED");
        if (success) {
            logDao.logAction(new ActivityLog(user.getUserId(), "RECORD_ATTENDANCE", LocalDateTime.now().toString(), "Recorded attendance for Registration ID: " + regId));
            System.out.println("Participant attendance recorded successfully as 'ATTENDED'.");
        } else {
            System.out.println("Failed to record attendance. Check Registration ID.");
        }
    }
}
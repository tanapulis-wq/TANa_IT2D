package com.scc.model;

public class Registration {
    private int registrationId;
    private int userId;
    private int eventId;
    private String registrationDate;
    private String registrationStatus;

    public Registration() {}

    public Registration(int userId, int eventId, String registrationDate, String registrationStatus) {
        this.userId = userId;
        this.eventId = eventId;
        this.registrationDate = registrationDate;
        this.registrationStatus = registrationStatus;
    }

    // Getters and Setters
    public int getRegistrationId() { return registrationId; }
    public void setRegistrationId(int registrationId) { this.registrationId = registrationId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getEventId() { return eventId; }
    public void setEventId(int eventId) { this.eventId = eventId; }

    public String getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(String registrationDate) { this.registrationDate = registrationDate; }

    public String getRegistrationStatus() { return registrationStatus; }
    public void setRegistrationStatus(String registrationStatus) { this.registrationStatus = registrationStatus; }
}
package com.scc.model;

public class ActivityLog {
    private int logId;
    private int userId;
    private String action;
    private String dateTime;
    private String description;

    public ActivityLog() {}

    public ActivityLog(int userId, String action, String dateTime, String description) {
        this.userId = userId;
        this.action = action;
        this.dateTime = dateTime;
        this.description = description;
    }

    // Getters and Setters
    public int getLogId() { return logId; }
    public void setLogId(int logId) { this.logId = logId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getDateTime() { return dateTime; }
    public void setDateTime(String dateTime) { this.dateTime = dateTime; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
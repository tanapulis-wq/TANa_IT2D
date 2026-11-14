package com.scc.ui;

import com.scc.dao.ActivityLogDao;
import com.scc.model.ActivityLog;
import java.util.List;

public class ActivityLogViewer {
    private ActivityLogDao logDao = new ActivityLogDao();

    public void displayLogs() {
        List<ActivityLog> logs = logDao.getAllLogs();
        System.out.println("\n--- System Activity / Audit Logs ---");

        if (logs.isEmpty()) {
            System.out.println("No activity logs recorded yet.");
            return;
        }

        for (ActivityLog log : logs) {
            System.out.println("[" + log.getDateTime() + "] User ID: " + log.getUserId() +
                    " | Action: " + log.getAction() +
                    " | Description: " + log.getDescription());
        }
    }
}
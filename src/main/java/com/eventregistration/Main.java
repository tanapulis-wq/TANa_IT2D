package com.eventregistration;
import com.eventregistration.dao.DatabaseInitializer;
import com.eventregistration.dao.UserDao;
import com.eventregistration.ui.Authentication;
import java.util.Scanner;
public class Main {
    public static void main(String[] args) {
        try {
            DatabaseInitializer.initialize();
            try (Scanner scanner = new Scanner(System.in)) {
                UserDao userDao = new UserDao();
                new Authentication(scanner, userDao).start();
            }
        } catch (IllegalStateException e) {
            System.err.println(e.getMessage());
            if (e.getCause() != null) System.err.println(e.getCause().getMessage());
        }
    }
}

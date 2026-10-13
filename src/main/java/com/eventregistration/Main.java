

import com.eventregistration.dao.DatabaseConnection;
import com.eventregistration.dao.UserDao;
import com.eventregistration.ui.Menu;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        DatabaseConnection.initializeDatabase();

        try (Scanner scanner = new Scanner(System.in)) {
            UserDao userDao = new UserDao();
            Menu menu = new Menu(scanner, userDao);

            menu.start();
        }

        System.out.println("Application closed safely.");
    }
}


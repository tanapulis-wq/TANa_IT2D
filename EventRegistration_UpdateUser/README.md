# Event Registration System

Java 17 console application using Maven and SQLite.

## Requirements
- JDK 17 or newer
- Maven (or open the project in IntelliJ IDEA and let Maven import `pom.xml`)

## Run
1. Open this folder as a Maven project in IntelliJ IDEA.
2. Wait for Maven dependencies to load.
3. Run `src/main/java/com/eventregistration/Main.java`.

The application initializes `event_registration.db` on startup and enables SQLite foreign-key enforcement on every connection.

## Demo administrator
- Username: `admin`
- Password: `admin123`

The demo administrator is a coursework-only login handled by `Authentication`; it is not a row in the `users` table. Registered Teacher and Student accounts default to `PENDING` and cannot enter their role menus until approved.

## Admin approval workflow
1. Log in with the demo administrator credentials.
2. Choose **2. View Pending Registrations** to list all pending accounts.
3. Choose **3. Approve User Account** and enter the user ID. Enter `0` to cancel.
4. Verify the user's status is `APPROVED` in the `users` table using DB Browser for SQLite or SQLiteStudio.

## Main project packages
- `dao`: SQLite connection, initialization, and user queries
- `model`: `User` record
- `ui`: authentication and role-specific menus
- `Main.java`: single application entry point

## Admin: update user role/status
Admin Menu option 4 lists all users, asks for a target User ID (0 cancels), blocks editing the logged-in admin's own
account, then asks for a valid role (STUDENT/TEACHER/ADMIN) and status (APPROVED/PENDING). Invalid input re-prompts
instead of crashing. The update runs `UPDATE users SET roles = ?, status = ? WHERE id = ?;` as a PreparedStatement.

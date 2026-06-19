package hostel;

import java.util.ArrayList;
import java.util.List;

/**
 * AuthManager – user account registry and session management.
 *
 * Accounts are seeded / loaded by FileManager.
 * After a successful login the logged-in account is held here
 * so any part of the UI can check the current role.
 */
public class AuthManager {

    private static final List<UserAccount> accounts = new ArrayList<>();

    /** The account that is currently logged in, or null if no one is logged in. */
    private static UserAccount currentUser = null;

    // ── Account list access ───────────────────────────────────────────────────
    public static List<UserAccount> getAccounts() { return accounts; }

    // ── Session ───────────────────────────────────────────────────────────────
    public static UserAccount getCurrentUser()          { return currentUser; }
    public static void         setCurrentUser(UserAccount u) { currentUser = u; }
    public static void         logout()                      { currentUser = null; }

    public static boolean isAdminLoggedIn() {
        return currentUser != null && currentUser.isAdmin();
    }

    public static boolean isStudentLoggedIn() {
        return currentUser != null && currentUser.isStudent();
    }

    // ── Authentication ────────────────────────────────────────────────────────

    /**
     * Attempts to log in with the given credentials.
     * Returns the matching UserAccount on success, or null on failure.
     */
    public static UserAccount login(String username, String password) {
        return accounts.stream()
                .filter(a -> a.getUsername().equals(username)
                          && a.getPassword().equals(password))
                .findFirst()
                .orElse(null);
    }

    // ── Account management ────────────────────────────────────────────────────

    public static boolean usernameExists(String username) {
        return accounts.stream()
                .anyMatch(a -> a.getUsername().equalsIgnoreCase(username));
    }

    /**
     * Registers a new STUDENT account linked to the given studentId.
     * Saves immediately to users.txt.
     * Returns false if username already taken.
     */
    public static boolean registerStudent(String username, String password,
                                          String studentId) {
        if (usernameExists(username)) return false;
        UserAccount ua = new UserAccount(username, password,
                UserAccount.Role.STUDENT, studentId);
        accounts.add(ua);
        FileManager.saveUsers(accounts);
        return true;
    }

    /**
     * Called by DataStore.init() to ensure at least one admin account exists.
     * Default admin credentials:  username=admin  password=admin123
     */
    public static void ensureDefaultAdmin() {
        boolean hasAdmin = accounts.stream().anyMatch(UserAccount::isAdmin);
        if (!hasAdmin) {
            accounts.add(new UserAccount("admin", "admin123",
                    UserAccount.Role.ADMIN, "NULL"));
            FileManager.saveUsers(accounts);
        }
    }
}



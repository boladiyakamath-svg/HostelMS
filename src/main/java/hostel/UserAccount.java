package hostel;

/**
 * Represents a user account in the system.
 *
 * Roles:
 *   ADMIN   – full access to all tabs
 *   STUDENT – can only view their own details and the mess menu
 *
 * File format (users.txt, pipe-delimited):
 *   username|passwordHash|role|linkedStudentId
 *   e.g.  STU001|abc123|STUDENT|STU001
 *         admin|secret|ADMIN|NULL
 *
 * passwordHash is stored as plain text for simplicity
 * (acceptable for a college project; real apps would use BCrypt).
 */
public class UserAccount {

    public enum Role { ADMIN, STUDENT }

    private String username;
    private String password;        // stored as plain text
    private Role   role;
    private String linkedStudentId; // NULL for admin accounts

    public UserAccount(String username, String password,
                       Role role, String linkedStudentId) {
        this.username         = username;
        this.password         = password;
        this.role             = role;
        this.linkedStudentId  = linkedStudentId;
    }

    // ── Getters ───────────────────────────────────────────────────────────────
    public String getUsername()        { return username; }
    public String getPassword()        { return password; }
    public Role   getRole()            { return role; }
    public String getLinkedStudentId() { return linkedStudentId; }

    // ── Setters ───────────────────────────────────────────────────────────────
    public void setPassword(String p)        { password = p; }
    public void setLinkedStudentId(String s) { linkedStudentId = s; }

    public boolean isAdmin()   { return role == Role.ADMIN; }
    public boolean isStudent() { return role == Role.STUDENT; }
}



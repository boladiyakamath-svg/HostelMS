package hostel;

import java.time.LocalDate;

/**
 * Model class representing a Hostel Student / Resident.
 */
public class Student {

    private String    studentId;
    private String    name;
    private String    contact;
    private String    email;
    private String    course;
    private String    roomNumber;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;   // null  →  still residing
    private boolean   messSubscribed;

    public Student(String studentId, String name, String contact,
                   String email, String course,
                   String roomNumber, boolean messSubscribed) {
        this.studentId      = studentId;
        this.name           = name;
        this.contact        = contact;
        this.email          = email;
        this.course         = course;
        this.roomNumber     = roomNumber;
        this.messSubscribed = messSubscribed;
        this.checkInDate    = LocalDate.now();
    }

    // ── Getters ──────────────────────────────────────────────────────────────
    public String    getStudentId()     { return studentId; }
    public String    getName()          { return name; }
    public String    getContact()       { return contact; }
    public String    getEmail()         { return email; }
    public String    getCourse()        { return course; }
    public String    getRoomNumber()    { return roomNumber; }
    public LocalDate getCheckInDate()   { return checkInDate; }
    public LocalDate getCheckOutDate()  { return checkOutDate; }
    public boolean   isMessSubscribed() { return messSubscribed; }

    // ── Setters ──────────────────────────────────────────────────────────────
    public void setCheckInDate(LocalDate d)   { checkInDate = d; }
    public void setCheckOutDate(LocalDate d)  { checkOutDate = d; }
    public void setMessSubscribed(boolean b)  { messSubscribed = b; }
    public void setRoomNumber(String r)       { roomNumber = r; }
}



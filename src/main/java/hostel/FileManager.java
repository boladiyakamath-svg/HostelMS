package hostel;

import java.io.*;
import java.time.LocalDate;
import java.util.List;

/**
 * FileManager – Text-file persistence layer.
 *
 * Files are stored in  hostel_data/  (auto-created on first run).
 *
 * ── File formats (pipe-delimited, one record per line) ──────────────────────
 *
 *  rooms.txt
 *    roomNumber|roomType|pricePerMonth|floor|available
 *
 *  students.txt
 *    studentId|name|contact|email|course|roomNumber|checkInDate|checkOutDate|messSubscribed
 *    (checkOutDate = literal "NULL" when still residing)
 *
 *  mess_menu.txt
 *    day|breakfast|lunch|eveningSnack|dinner
 *
 *  users.txt
 *    username|password|role|linkedStudentId
 *    (role = ADMIN or STUDENT; linkedStudentId = NULL for admin)
 */
public class FileManager {

    private static final String DATA_DIR      = "hostel_data";
    private static final String ROOMS_FILE    = DATA_DIR + File.separator + "rooms.txt";
    private static final String STUDENTS_FILE = DATA_DIR + File.separator + "students.txt";
    private static final String MESS_FILE     = DATA_DIR + File.separator + "mess_menu.txt";
    private static final String USERS_FILE    = DATA_DIR + File.separator + "users.txt";

    private static final String SEP        = "|";
    private static final String NULL_TOKEN = "NULL";

    // ── Directory init ────────────────────────────────────────────────────────
    public static void initDirectory() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) dir.mkdirs();
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  SAVE
    // ══════════════════════════════════════════════════════════════════════════

    public static void saveRooms(List<Room> rooms) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ROOMS_FILE))) {
            for (Room r : rooms) {
                bw.write(join(r.getRoomNumber(), r.getRoomType(),
                        String.valueOf(r.getPricePerMonth()),
                        String.valueOf(r.getFloor()),
                        String.valueOf(r.isAvailable())));
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("[FileManager] ERROR saving rooms: " + e.getMessage());
        }
    }

    public static void saveStudents(List<Student> students) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(STUDENTS_FILE))) {
            for (Student s : students) {
                String co = s.getCheckOutDate() == null
                        ? NULL_TOKEN : s.getCheckOutDate().toString();
                bw.write(join(s.getStudentId(), s.getName(), s.getContact(),
                        s.getEmail(), s.getCourse(), s.getRoomNumber(),
                        s.getCheckInDate().toString(), co,
                        String.valueOf(s.isMessSubscribed())));
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("[FileManager] ERROR saving students: " + e.getMessage());
        }
    }

    public static void saveMessMenus(List<MessMenu> menus) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(MESS_FILE))) {
            for (MessMenu m : menus) {
                bw.write(join(m.getDay(), m.getBreakfast(), m.getLunch(),
                        m.getEveningSnack(), m.getDinner()));
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("[FileManager] ERROR saving mess menus: " + e.getMessage());
        }
    }

    public static void saveUsers(List<UserAccount> users) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(USERS_FILE))) {
            for (UserAccount u : users) {
                bw.write(join(u.getUsername(), u.getPassword(),
                        u.getRole().name(),
                        u.getLinkedStudentId() == null ? NULL_TOKEN
                                                       : u.getLinkedStudentId()));
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("[FileManager] ERROR saving users: " + e.getMessage());
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  LOAD  (return true if file existed)
    // ══════════════════════════════════════════════════════════════════════════

    public static boolean loadRooms(List<Room> rooms) {
        File f = new File(ROOMS_FILE);
        if (!f.exists()) return false;
        rooms.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] p = line.split("\\|", -1);
                if (p.length < 5) continue;
                Room r = new Room(p[0], p[1],
                        Double.parseDouble(p[2]), Integer.parseInt(p[3]));
                r.setAvailable(Boolean.parseBoolean(p[4]));
                rooms.add(r);
            }
        } catch (IOException e) {
            System.err.println("[FileManager] ERROR loading rooms: " + e.getMessage());
        }
        return true;
    }

    public static boolean loadStudents(List<Student> students) {
        File f = new File(STUDENTS_FILE);
        if (!f.exists()) return false;
        students.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] p = line.split("\\|", -1);
                if (p.length < 9) continue;
                Student s = new Student(p[0], p[1], p[2], p[3], p[4], p[5],
                        Boolean.parseBoolean(p[8]));
                s.setCheckInDate(LocalDate.parse(p[6]));
                s.setCheckOutDate(NULL_TOKEN.equals(p[7]) ? null : LocalDate.parse(p[7]));
                students.add(s);
            }
        } catch (IOException e) {
            System.err.println("[FileManager] ERROR loading students: " + e.getMessage());
        }
        return true;
    }

    public static boolean loadMessMenus(List<MessMenu> menus) {
        File f = new File(MESS_FILE);
        if (!f.exists()) return false;
        menus.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] p = line.split("\\|", -1);
                if (p.length < 5) continue;
                menus.add(new MessMenu(p[0], p[1], p[2], p[3], p[4]));
            }
        } catch (IOException e) {
            System.err.println("[FileManager] ERROR loading mess menus: " + e.getMessage());
        }
        return true;
    }

    public static boolean loadUsers(List<UserAccount> users) {
        File f = new File(USERS_FILE);
        if (!f.exists()) return false;
        users.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] p = line.split("\\|", -1);
                if (p.length < 4) continue;
                UserAccount.Role role = UserAccount.Role.valueOf(p[2]);
                String linkedId = NULL_TOKEN.equals(p[3]) ? null : p[3];
                users.add(new UserAccount(p[0], p[1], role, linkedId));
            }
        } catch (IOException e) {
            System.err.println("[FileManager] ERROR loading users: " + e.getMessage());
        }
        return true;
    }

    // ── Helper ────────────────────────────────────────────────────────────────
    private static String join(String... parts) {
        return String.join(SEP, parts);
    }
}



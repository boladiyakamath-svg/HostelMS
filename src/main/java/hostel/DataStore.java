package hostel;

import java.util.ArrayList;
import java.util.List;

/**
 * Central data store.
 * Loads from hostel_data/*.txt on startup; seeds files on very first run.
 */
public class DataStore {

    private static final List<Room>     rooms    = new ArrayList<>();
    private static final List<Student>  students = new ArrayList<>();
    private static final List<MessMenu> menus    = new ArrayList<>();

    public static void init() {

        FileManager.initDirectory();

        // ── Rooms ─────────────────────────────────────────────────────────────
        if (!FileManager.loadRooms(rooms)) {
            rooms.add(new Room("101", "Single",        3500, 1));
            rooms.add(new Room("102", "Single",        3500, 1));
            rooms.add(new Room("103", "Double",        5500, 1));
            rooms.add(new Room("104", "Double",        5500, 1));
            rooms.add(new Room("105", "Triple",        7000, 1));
            rooms.add(new Room("201", "Single",        3800, 2));
            rooms.add(new Room("202", "Single",        3800, 2));
            rooms.add(new Room("203", "Double",        5800, 2));
            rooms.add(new Room("204", "Triple",        7200, 2));
            rooms.add(new Room("301", "Deluxe Single", 5500, 3));
            rooms.add(new Room("302", "Deluxe Double", 7800, 3));
            FileManager.saveRooms(rooms);
        }

        // ── Students ──────────────────────────────────────────────────────────
        if (!FileManager.loadStudents(students)) {
            FileManager.saveStudents(students);
        }

        // ── Mess menus ────────────────────────────────────────────────────────
        if (!FileManager.loadMessMenus(menus)) {
            menus.add(new MessMenu("Monday",
                    "Idli, Sambar, Coconut Chutney",
                    "Rice, Dal Tadka, Aloo Sabzi, Roti, Salad",
                    "Tea, Marie Biscuits",
                    "Chapati, Paneer Butter Masala, Dal, Rice, Curd"));
            menus.add(new MessMenu("Tuesday",
                    "Poha, Banana, Milk",
                    "Rice, Rajma Curry, Mixed Veg, Roti, Pickle",
                    "Coffee, Samosa",
                    "Rice, Dal Fry, Bhindi Masala, Roti, Papad"));
            menus.add(new MessMenu("Wednesday",
                    "Dosa, Sambar, Coconut Chutney",
                    "Rice, Chole, Jeera Aloo, Roti, Raita",
                    "Tea, Bread Pakora",
                    "Chapati, Egg Curry / Paneer Bhurji, Dal, Rice, Salad"));
            menus.add(new MessMenu("Thursday",
                    "Upma, Coconut Chutney, Juice",
                    "Rice, Dal Makhani, Gobhi Sabzi, Roti",
                    "Tea, Vada",
                    "Chapati, Aloo Matar, Dal, Rice, Sweet (Halwa)"));
            menus.add(new MessMenu("Friday",
                    "Puri, Aloo Sabzi, Curd",
                    "Rice, Sambar, Rasam, Papad, Pickle, Payasam",
                    "Coffee, Pakora",
                    "Biryani (Veg / Egg), Raita, Salad, Appam"));
            menus.add(new MessMenu("Saturday",
                    "Paratha, Curd, Pickle, Lassi",
                    "Fried Rice, Gobi Manchurian, Roti, Soup",
                    "Tea, Banana",
                    "Chapati, Dal Makhani, Jeera Rice, Kheer"));
            menus.add(new MessMenu("Sunday",
                    "Bread, Butter, Jam, Omelette / Sprouts, Milk",
                    "Special: Veg / Chicken Biryani, Raita, Gulab Jamun",
                    "Cold Drink / Juice, Samosa",
                    "Chapati, Paneer / Egg Curry, Rice, Ice Cream"));
            FileManager.saveMessMenus(menus);
        }

        // ── User accounts ─────────────────────────────────────────────────────
        if (!FileManager.loadUsers(AuthManager.getAccounts())) {
            FileManager.saveUsers(AuthManager.getAccounts());
        }
        // Always ensure at least one admin account exists
        AuthManager.ensureDefaultAdmin();
    }

    // ── Accessors ─────────────────────────────────────────────────────────────
    public static List<Room>     getRooms()    { return rooms; }
    public static List<Student>  getStudents() { return students; }
    public static List<MessMenu> getMenus()    { return menus; }

    // ── Finders ───────────────────────────────────────────────────────────────
    public static Room findRoom(String number) {
        return rooms.stream()
                .filter(r -> r.getRoomNumber().equalsIgnoreCase(number))
                .findFirst().orElse(null);
    }

    public static Student findStudent(String id) {
        return students.stream()
                .filter(s -> s.getStudentId().equalsIgnoreCase(id))
                .findFirst().orElse(null);
    }

    public static Student findActiveStudentByRoom(String roomNumber) {
        return students.stream()
                .filter(s -> s.getRoomNumber().equalsIgnoreCase(roomNumber)
                          && s.getCheckOutDate() == null)
                .findFirst().orElse(null);
    }

    public static MessMenu findMenuByDay(String day) {
        return menus.stream()
                .filter(m -> m.getDay().equalsIgnoreCase(day))
                .findFirst().orElse(null);
    }

    public static boolean roomExists(String number) {
        return rooms.stream()
                .anyMatch(r -> r.getRoomNumber().equalsIgnoreCase(number));
    }

    public static boolean studentExists(String id) {
        return students.stream()
                .anyMatch(s -> s.getStudentId().equalsIgnoreCase(id));
    }
}



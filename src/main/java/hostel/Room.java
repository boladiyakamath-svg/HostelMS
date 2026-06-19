package hostel;

/**
 * Model class representing a Hostel Room.
 */
public class Room {

    private String roomNumber;
    private String roomType;      // Single, Double, Triple, Deluxe
    private double pricePerMonth;
    private int    floor;
    private boolean available;

    public Room(String roomNumber, String roomType, double pricePerMonth, int floor) {
        this.roomNumber    = roomNumber;
        this.roomType      = roomType;
        this.pricePerMonth = pricePerMonth;
        this.floor         = floor;
        this.available     = true;
    }

    // ── Getters ──────────────────────────────────────────────────────────────
    public String  getRoomNumber()    { return roomNumber; }
    public String  getRoomType()      { return roomType; }
    public double  getPricePerMonth() { return pricePerMonth; }
    public int     getFloor()         { return floor; }
    public boolean isAvailable()      { return available; }

    // Convenience for TableView column binding
    public String  getStatus()        { return available ? "Available" : "Occupied"; }

    // ── Setters ──────────────────────────────────────────────────────────────
    public void setAvailable(boolean available) { this.available = available; }

    @Override
    public String toString() { return roomNumber; }
}



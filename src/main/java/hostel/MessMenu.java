package hostel;

/**
 * Model class representing the Mess Menu for one day of the week.
 */
public class MessMenu {

    private String day;
    private String breakfast;
    private String lunch;
    private String eveningSnack;
    private String dinner;

    public MessMenu(String day, String breakfast, String lunch,
                    String eveningSnack, String dinner) {
        this.day          = day;
        this.breakfast    = breakfast;
        this.lunch        = lunch;
        this.eveningSnack = eveningSnack;
        this.dinner       = dinner;
    }

    // ── Getters ──────────────────────────────────────────────────────────────
    public String getDay()          { return day; }
    public String getBreakfast()    { return breakfast; }
    public String getLunch()        { return lunch; }
    public String getEveningSnack() { return eveningSnack; }
    public String getDinner()       { return dinner; }

    // ── Setters ──────────────────────────────────────────────────────────────
    public void setBreakfast(String s)    { breakfast    = s; }
    public void setLunch(String s)        { lunch        = s; }
    public void setEveningSnack(String s) { eveningSnack = s; }
    public void setDinner(String s)       { dinner       = s; }
}



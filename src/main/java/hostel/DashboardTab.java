package hostel;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

public class DashboardTab {

    private static final String BG_TAB        = "#162030";
    private static final String BG_CARD       = "#1c2b3a";
    private static final String BG_BANNER     = "#172436";
    private static final String ACCENT_GOLD   = "#c9a84c";
    private static final String TEXT_PRIMARY  = "#f0e6d3";
    private static final String TEXT_MUTED    = "#9baab8";
    private static final String BORDER_COLOR  = "#2e4155";
    private static final String SUCCESS_COLOR = "#4caf7d";
    private static final String ERROR_COLOR   = "#e05c5c";

    public Tab createTab() {
        Tab tab = new Tab("  Dashboard");
        tab.setClosable(false);

        Label title = new Label("Hostel Management System");
        title.setFont(Font.font("Georgia", FontWeight.BOLD, 26));
        title.setTextFill(Color.web(ACCENT_GOLD));

        Label sub = new Label("Live overview  •  " + LocalDate.now().toString());
        sub.setFont(Font.font("Segoe UI", FontPosture.ITALIC, 13));
        sub.setTextFill(Color.web(TEXT_MUTED));

        HBox cardRow = buildCardRow();

        String todayName = LocalDate.now()
                .getDayOfWeek()
                .getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        MessMenu todayMenu = DataStore.findMenuByDay(todayName);

        VBox menuBanner = new VBox(8);
        menuBanner.setPadding(new Insets(16));
        menuBanner.setStyle(
            "-fx-background-color:" + BG_BANNER + ";" +
            "-fx-border-color:" + ACCENT_GOLD + ";" +
            "-fx-border-radius:8;" +
            "-fx-background-radius:8;" +
            "-fx-border-width:1;"
        );

        Label menuTitle = new Label("Today's Mess Menu  (" + todayName + ")");
        menuTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        menuTitle.setTextFill(Color.web(ACCENT_GOLD));
        menuBanner.getChildren().add(menuTitle);

        if (todayMenu != null) {
            menuBanner.getChildren().addAll(
                    mealRow("Breakfast",     todayMenu.getBreakfast()),
                    mealRow("Lunch",         todayMenu.getLunch()),
                    mealRow("Evening Snack", todayMenu.getEveningSnack()),
                    mealRow("Dinner",        todayMenu.getDinner()));
        } else {
            Label noMenu = new Label("Menu not configured for today.");
            noMenu.setTextFill(Color.web(TEXT_MUTED));
            menuBanner.getChildren().add(noMenu);
        }

        Label recTitle = new Label("Recently Admitted Students");
        recTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        recTitle.setTextFill(Color.web(TEXT_PRIMARY));

        TextArea recentArea = new TextArea();
        recentArea.setEditable(false);
        recentArea.setPrefHeight(150);
        recentArea.setStyle(
            "-fx-font-family:'Courier New';" +
            "-fx-font-size:12px;" +
            "-fx-background-color:#111e2b;" +
            "-fx-text-fill:" + TEXT_PRIMARY + ";" +
            "-fx-border-color:" + BORDER_COLOR + ";" +
            "-fx-border-radius:6;" +
            "-fx-background-radius:6;"
        );
        refreshRecent(recentArea);

        Button btnRefresh = btn("Refresh Dashboard", ACCENT_GOLD, "#0f1923");
        btnRefresh.setOnAction(e -> {
            cardRow.getChildren().setAll(buildCardRow().getChildren());
            refreshRecent(recentArea);
        });

        Separator sep1 = styledSep();
        Separator sep2 = styledSep();
        Separator sep3 = styledSep();

        VBox body = new VBox(18,
                title, sub,
                sep1,
                cardRow,
                sep2,
                menuBanner,
                sep3,
                recTitle, recentArea,
                new HBox(btnRefresh));
        body.setPadding(new Insets(26));
        body.setStyle("-fx-background-color:" + BG_TAB + ";");

        ScrollPane sp = new ScrollPane(body);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color:transparent;-fx-background:" + BG_TAB + ";");

        tab.setContent(sp);
        return tab;
    }

    private HBox buildCardRow() {
        long total    = DataStore.getRooms().size();
        long avail    = DataStore.getRooms().stream().filter(Room::isAvailable).count();
        long occupied = total - avail;
        long active   = DataStore.getStudents().stream()
                .filter(s -> s.getCheckOutDate() == null).count();
        long messSubs = DataStore.getStudents().stream()
                .filter(s -> s.isMessSubscribed() && s.getCheckOutDate() == null).count();

        HBox row = new HBox(14,
                card("Total Rooms",      String.valueOf(total),    "#1e3a5c", "#7ab8e8"),
                card("Available",        String.valueOf(avail),    "#1a3d2e", "#4caf7d"),
                card("Occupied",         String.valueOf(occupied), "#3d1e1e", "#e05c5c"),
                card("Active Students",  String.valueOf(active),   "#2e1e4a", "#9b7de0"),
                card("Mess Subscribers", String.valueOf(messSubs), "#3d2e0e", "#c9a84c"));
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private VBox card(String label, String value, String bgHex, String accentHex) {
        Label valLbl  = new Label(value);
        Label nameLbl = new Label(label);
        valLbl.setFont(Font.font("Georgia", FontWeight.BOLD, 34));
        valLbl.setTextFill(Color.web(accentHex));
        nameLbl.setFont(Font.font("Segoe UI", 12));
        nameLbl.setTextFill(Color.web(accentHex));
        nameLbl.setOpacity(0.85);

        Region topBar = new Region();
        topBar.setPrefHeight(3);
        topBar.setStyle("-fx-background-color:" + accentHex + ";-fx-background-radius:2;");

        VBox box = new VBox(6, topBar, valLbl, nameLbl);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(14, 20, 14, 20));
        box.setMinWidth(145);
        box.setStyle(
            "-fx-background-color:" + bgHex + ";" +
            "-fx-background-radius:10;" +
            "-fx-border-color:derive(" + bgHex + ",30%);" +
            "-fx-border-radius:10;" +
            "-fx-border-width:1;" +
            "-fx-effect:dropshadow(gaussian,rgba(0,0,0,0.3),8,0,0,3);"
        );
        return box;
    }

    private HBox mealRow(String meal, String items) {
        Label k = new Label(meal + ":  ");
        k.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        k.setTextFill(Color.web("#c9a84c"));
        k.setMinWidth(140);
        Label v = new Label(items);
        v.setFont(Font.font("Segoe UI", 13));
        v.setTextFill(Color.web("#f0e6d3"));
        v.setWrapText(true);
        HBox row = new HBox(k, v);
        row.setAlignment(Pos.TOP_LEFT);
        return row;
    }

    private void refreshRecent(TextArea ta) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-12s %-22s %-8s %-12s%n",
                "ID", "Name", "Room", "Check-In"));
        sb.append("-".repeat(56)).append("\n");
        DataStore.getStudents().stream()
                .filter(s -> s.getCheckOutDate() == null)
                .sorted((a, b) -> b.getCheckInDate().compareTo(a.getCheckInDate()))
                .limit(8)
                .forEach(s -> sb.append(String.format("%-12s %-22s %-8s %-12s%n",
                        s.getStudentId(), s.getName(),
                        s.getRoomNumber(), s.getCheckInDate())));
        ta.setText(sb.toString());
    }

    private Separator styledSep() {
        Separator s = new Separator();
        s.setStyle("-fx-background-color:#2e4155;");
        return s;
    }

    private Button btn(String text, String bgHex, String textHex) {
        Button b = new Button(text);
        b.setStyle(
            "-fx-background-color:" + bgHex + ";" +
            "-fx-text-fill:" + textHex + ";" +
            "-fx-font-size:13px;" +
            "-fx-padding:7 16;" +
            "-fx-background-radius:6;" +
            "-fx-cursor:hand;" +
            "-fx-font-weight:bold;"
        );
        b.setOnMouseEntered(e -> b.setOpacity(0.85));
        b.setOnMouseExited(e  -> b.setOpacity(1.0));
        return b;
    }
}



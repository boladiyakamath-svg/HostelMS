package hostel;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import java.time.LocalDate;

public class BookingTab {

    private static final String BG_TAB        = "#162030";
    private static final String BG_FORM       = "#1c2b3a";
    private static final String ACCENT_GOLD   = "#c9a84c";
    private static final String ACCENT_BLUE   = "#5b9bd5";
    private static final String ACCENT_RED    = "#c0392b";
    private static final String ACCENT_GREY   = "#6b7d8e";
    private static final String TEXT_PRIMARY  = "#f0e6d3";
    private static final String TEXT_MUTED    = "#9baab8";
    private static final String TEXT_DARK     = "#0f1923";
    private static final String BORDER_COLOR  = "#2e4155";
    private static final String INPUT_BG      = "#111e2b";
    private static final String SUCCESS_COLOR = "#4caf7d";
    private static final String ERROR_COLOR   = "#e05c5c";

    public Tab createTab() {
        Tab tab = new Tab("  Booking & Checkout");
        tab.setClosable(false);

        Label title = new Label("Booking & Checkout");
        title.setFont(Font.font("Georgia", FontWeight.BOLD, 22));
        title.setTextFill(Color.web(ACCENT_GOLD));

        Label lblId = new Label("Student ID:");
        lblId.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 13));
        lblId.setTextFill(Color.web(TEXT_MUTED));

        TextField txtId = new TextField();
        txtId.setPromptText("Enter student ID");
        txtId.setPrefWidth(210);
        txtId.setStyle(
            "-fx-font-size:13px;" +
            "-fx-background-color:" + INPUT_BG + ";" +
            "-fx-text-fill:" + TEXT_PRIMARY + ";" +
            "-fx-prompt-text-fill:#5a6e80;" +
            "-fx-border-color:" + BORDER_COLOR + ";" +
            "-fx-border-radius:5;" +
            "-fx-background-radius:5;" +
            "-fx-padding:6 10;"
        );

        Button btnFetch    = btn("Fetch Details",    ACCENT_BLUE, TEXT_DARK);
        Button btnCheckout = btn("Checkout Student", "#8b2c2c",   "#ffe0e0");

        HBox lookupRow = new HBox(10, lblId, txtId, btnFetch, btnCheckout);
        lookupRow.setAlignment(Pos.CENTER_LEFT);

        GridPane detailGrid = new GridPane();
        detailGrid.setHgap(16);
        detailGrid.setVgap(10);
        detailGrid.setPadding(new Insets(16));
        detailGrid.setStyle(
            "-fx-background-color:" + BG_FORM + ";" +
            "-fx-border-color:" + BORDER_COLOR + ";" +
            "-fx-border-radius:8;" +
            "-fx-background-radius:8;"
        );

        String[] keys = {"Name", "Student ID", "Room No", "Course",
                          "Check-In Date", "Mess Subscription", "Price / Month"};
        Label[] valLabels = new Label[keys.length];

        for (int i = 0; i < keys.length; i++) {
            Label k = new Label(keys[i] + ":");
            k.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
            k.setTextFill(Color.web(TEXT_MUTED));
            k.setMinWidth(160);
            valLabels[i] = new Label("-");
            valLabels[i].setFont(Font.font("Segoe UI", 13));
            valLabels[i].setTextFill(Color.web(TEXT_PRIMARY));
            detailGrid.add(k,            0, i);
            detailGrid.add(valLabels[i], 1, i);
        }

        Label lblStatus = statusLabel();

        Label overviewTitle = new Label("Room Status Overview");
        overviewTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        overviewTitle.setTextFill(Color.web(TEXT_PRIMARY));

        TextArea roomStatus = new TextArea();
        roomStatus.setEditable(false);
        roomStatus.setPrefHeight(220);
        roomStatus.setStyle(
            "-fx-font-family:'Courier New';" +
            "-fx-font-size:12px;" +
            "-fx-background-color:#111e2b;" +
            "-fx-text-fill:" + TEXT_PRIMARY + ";" +
            "-fx-border-color:" + BORDER_COLOR + ";" +
            "-fx-border-radius:6;" +
            "-fx-background-radius:6;"
        );
        refreshRoomStatus(roomStatus);

        Button btnRefresh = btn("Refresh Overview", ACCENT_GREY, TEXT_PRIMARY);
        btnRefresh.setOnAction(e -> refreshRoomStatus(roomStatus));

        btnFetch.setOnAction(e -> {
            String id = txtId.getText().trim();
            if (id.isEmpty()) {
                status(lblStatus, "Please enter a Student ID.", false); return;
            }
            Student s = DataStore.findStudent(id);
            if (s == null || s.getCheckOutDate() != null) {
                status(lblStatus, "No active student found with ID: " + id, false);
                clearDetails(valLabels); return;
            }
            Room r = DataStore.findRoom(s.getRoomNumber());
            valLabels[0].setText(s.getName());
            valLabels[1].setText(s.getStudentId());
            valLabels[2].setText(s.getRoomNumber());
            valLabels[3].setText(s.getCourse());
            valLabels[4].setText(s.getCheckInDate().toString());
            valLabels[5].setText(s.isMessSubscribed() ? "Yes" : "No");
            valLabels[6].setText(r != null ? "Rs " + (int) r.getPricePerMonth() : "N/A");
            status(lblStatus, "Student details loaded.", true);
        });

        btnCheckout.setOnAction(e -> {
            String id = txtId.getText().trim();
            if (id.isEmpty()) {
                status(lblStatus, "Please enter a Student ID.", false); return;
            }
            Student s = DataStore.findStudent(id);
            if (s == null || s.getCheckOutDate() != null) {
                status(lblStatus, "No active student found with ID: " + id, false); return;
            }

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirm Checkout");
            confirm.setHeaderText(null);
            confirm.setContentText("Check out " + s.getName()
                    + " from Room " + s.getRoomNumber() + "?");
            confirm.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);

            confirm.showAndWait().ifPresent(bt -> {
                if (bt == ButtonType.YES) {
                    s.setCheckOutDate(LocalDate.now());
                    Room r = DataStore.findRoom(s.getRoomNumber());
                    if (r != null) r.setAvailable(true);
                    FileManager.saveStudents(DataStore.getStudents());
                    FileManager.saveRooms(DataStore.getRooms());

                    status(lblStatus,
                            s.getName() + " checked out. Room "
                                    + s.getRoomNumber() + " is now available.", true);
                    clearDetails(valLabels);
                    txtId.clear();
                    refreshRoomStatus(roomStatus);
                }
            });
        });

        VBox root = new VBox(16,
                title, styledSep(),
                sectionLabel("Student Lookup & Checkout"),
                lookupRow, detailGrid, lblStatus,
                styledSep(),
                overviewTitle, roomStatus,
                new HBox(btnRefresh));
        root.setPadding(new Insets(22));
        root.setStyle("-fx-background-color:" + BG_TAB + ";");

        ScrollPane sp = new ScrollPane(root);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color:transparent;-fx-background:" + BG_TAB + ";");

        tab.setContent(sp);
        return tab;
    }

    private void refreshRoomStatus(TextArea ta) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-10s %-20s %-15s %-12s%n",
                "Room No", "Type", "Price/Month", "Status"));
        sb.append("-".repeat(60)).append("\n");
        for (Room r : DataStore.getRooms()) {
            sb.append(String.format("%-10s %-20s Rs %-12.0f %-12s%n",
                    r.getRoomNumber(), r.getRoomType(),
                    r.getPricePerMonth(), r.getStatus()));
        }
        ta.setText(sb.toString());
    }

    private void clearDetails(Label[] vals) {
        for (Label v : vals) v.setText("-");
    }

    private Button btn(String text, String bgHex, String textHex) {
        Button b = new Button(text);
        b.setStyle(
            "-fx-background-color:" + bgHex + ";" +
            "-fx-text-fill:" + textHex + ";" +
            "-fx-font-size:13px;" +
            "-fx-padding:7 14;" +
            "-fx-background-radius:6;" +
            "-fx-cursor:hand;" +
            "-fx-font-weight:bold;"
        );
        b.setOnMouseEntered(e -> b.setOpacity(0.82));
        b.setOnMouseExited(e  -> b.setOpacity(1.0));
        return b;
    }

    private Label sectionLabel(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 14));
        l.setTextFill(Color.web(TEXT_PRIMARY));
        return l;
    }

    private Separator styledSep() {
        Separator s = new Separator();
        s.setStyle("-fx-background-color:#2e4155;");
        return s;
    }

    private Label statusLabel() {
        Label l = new Label();
        l.setFont(Font.font("Segoe UI", 13));
        return l;
    }

    private void status(Label lbl, String msg, boolean ok) {
        lbl.setText(msg);
        lbl.setTextFill(ok ? Color.web(SUCCESS_COLOR) : Color.web(ERROR_COLOR));
    }
}



package hostel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;

public class RoomTab {

    private static final String BG_TAB        = "#162030";
    private static final String BG_FORM       = "#1c2b3a";
    private static final String ACCENT_GOLD   = "#c9a84c";
    private static final String ACCENT_GREEN  = "#4caf7d";
    private static final String ACCENT_BLUE   = "#5b9bd5";
    private static final String ACCENT_PURPLE = "#9b7de0";
    private static final String ACCENT_GREY   = "#6b7d8e";
    private static final String TEXT_PRIMARY  = "#f0e6d3";
    private static final String TEXT_MUTED    = "#9baab8";
    private static final String TEXT_DARK     = "#0f1923";
    private static final String BORDER_COLOR  = "#2e4155";
    private static final String INPUT_BG      = "#111e2b";
    private static final String SUCCESS_COLOR = "#4caf7d";
    private static final String ERROR_COLOR   = "#e05c5c";

    private final ObservableList<Room> tableData = FXCollections.observableArrayList();

    public Tab createTab() {
        Tab tab = new Tab("  Rooms");
        tab.setClosable(false);

        Label title = new Label("Room Management");
        title.setFont(Font.font("Georgia", FontWeight.BOLD, 22));
        title.setTextFill(Color.web(ACCENT_GOLD));

        TextField txtNum   = field("e.g. 201");
        TextField txtPrice = field("e.g. 4500");
        TextField txtFloor = field("e.g. 2");

        ComboBox<String> cmbType = new ComboBox<>();
        cmbType.getItems().addAll("Single", "Double", "Triple",
                "Deluxe Single", "Deluxe Double");
        cmbType.setValue("Single");
        styleCombo(cmbType);

        GridPane form = new GridPane();
        form.setHgap(14);
        form.setVgap(12);
        form.setPadding(new Insets(16));
        form.setStyle(
            "-fx-background-color:" + BG_FORM + ";" +
            "-fx-border-color:" + BORDER_COLOR + ";" +
            "-fx-border-radius:8;" +
            "-fx-background-radius:8;"
        );

        addRow(form, 0, "Room Number:",        txtNum);
        addRow(form, 1, "Room Type:",           cmbType);
        addRow(form, 2, "Price / Month (Rs):",  txtPrice);
        addRow(form, 3, "Floor:",               txtFloor);

        Button btnAdd   = btn("Add Room",        ACCENT_GREEN,  TEXT_DARK);
        Button btnAll   = btn("View All Rooms",  ACCENT_BLUE,   TEXT_DARK);
        Button btnAvail = btn("Available Only",  ACCENT_PURPLE, "#f5f0ff");
        Button btnClear = btn("Clear Fields",    ACCENT_GREY,   TEXT_PRIMARY);

        HBox buttons = new HBox(10, btnAdd, btnAll, btnAvail, btnClear);
        buttons.setAlignment(Pos.CENTER_LEFT);

        Label lblStatus = statusLabel();

        TableView<Room> table = new TableView<>(tableData);
        styleTable(table);
        table.getColumns().addAll(
                col("Room No",     "roomNumber",    100),
                col("Type",        "roomType",      140),
                col("Price/Month", "pricePerMonth", 140),
                col("Floor",       "floor",          80),
                col("Status",      "status",        110));
        tableData.setAll(DataStore.getRooms());

        btnAdd.setOnAction(e -> {
            String num   = txtNum.getText().trim();
            String price = txtPrice.getText().trim();
            String floor = txtFloor.getText().trim();

            if (num.isEmpty() || price.isEmpty() || floor.isEmpty()) {
                status(lblStatus, "Please fill in all fields.", false); return;
            }
            if (DataStore.roomExists(num)) {
                status(lblStatus, "Room " + num + " already exists.", false); return;
            }
            try {
                double p = Double.parseDouble(price);
                int    f = Integer.parseInt(floor);
                Room room = new Room(num, cmbType.getValue(), p, f);
                DataStore.getRooms().add(room);
                FileManager.saveRooms(DataStore.getRooms());
                tableData.setAll(DataStore.getRooms());
                status(lblStatus, "Room " + num + " added successfully.", true);
                txtNum.clear(); txtPrice.clear(); txtFloor.clear();
                cmbType.setValue("Single");
            } catch (NumberFormatException ex) {
                status(lblStatus, "Price and Floor must be numeric values.", false);
            }
        });

        btnAll.setOnAction(e -> {
            tableData.setAll(DataStore.getRooms());
            status(lblStatus, "Showing all " + tableData.size() + " rooms.", true);
        });

        btnAvail.setOnAction(e -> {
            tableData.setAll(DataStore.getRooms().stream()
                    .filter(Room::isAvailable).toList());
            status(lblStatus, "Showing " + tableData.size() + " available rooms.", true);
        });

        btnClear.setOnAction(e -> {
            txtNum.clear(); txtPrice.clear(); txtFloor.clear();
            cmbType.setValue("Single"); lblStatus.setText("");
        });

        VBox root = new VBox(16,
                title, styledSep(),
                sectionLabel("Add New Room"), form,
                buttons, lblStatus,
                styledSep(), table);
        root.setPadding(new Insets(22));
        root.setStyle("-fx-background-color:" + BG_TAB + ";");
        VBox.setVgrow(table, Priority.ALWAYS);

        tab.setContent(root);
        return tab;
    }

    @SuppressWarnings("unchecked")
    private <T> TableColumn<Room, T> col(String header, String prop, double w) {
        TableColumn<Room, T> c = new TableColumn<>(header);
        c.setCellValueFactory(new PropertyValueFactory<>(prop));
        c.setPrefWidth(w);
        return c;
    }

    private void addRow(GridPane g, int row, String lbl, javafx.scene.Node field) {
        Label l = new Label(lbl);
        l.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 13));
        l.setTextFill(Color.web("#9baab8"));
        l.setMinWidth(160);
        g.add(l, 0, row);
        g.add(field, 1, row);
    }

    private TextField field(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setPrefWidth(210);
        tf.setStyle(
            "-fx-font-size:13px;" +
            "-fx-background-color:" + INPUT_BG + ";" +
            "-fx-text-fill:" + TEXT_PRIMARY + ";" +
            "-fx-prompt-text-fill:#5a6e80;" +
            "-fx-border-color:" + BORDER_COLOR + ";" +
            "-fx-border-radius:5;" +
            "-fx-background-radius:5;" +
            "-fx-padding:6 10;"
        );
        return tf;
    }

    private void styleCombo(ComboBox<String> c) {
        c.setStyle(
            "-fx-font-size:13px;" +
            "-fx-background-color:" + INPUT_BG + ";" +
            "-fx-text-fill:" + TEXT_PRIMARY + ";" +
            "-fx-border-color:" + BORDER_COLOR + ";" +
            "-fx-border-radius:5;" +
            "-fx-background-radius:5;"
        );
    }

    private void styleTable(TableView<?> t) {
        t.setStyle(
            "-fx-background-color:#111e2b;" +
            "-fx-text-fill:" + TEXT_PRIMARY + ";" +
            "-fx-border-color:" + BORDER_COLOR + ";" +
            "-fx-border-radius:6;" +
            "-fx-background-radius:6;" +
            "-fx-table-header-border-color:" + BORDER_COLOR + ";"
        );
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



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

public class StudentTab {

    private static final String BG_TAB        = "#162030";
    private static final String BG_FORM       = "#1c2b3a";
    private static final String ACCENT_GOLD   = "#c9a84c";
    private static final String ACCENT_GREEN  = "#4caf7d";
    private static final String ACCENT_BLUE   = "#5b9bd5";
    private static final String ACCENT_ORANGE = "#d4845a";
    private static final String ACCENT_GREY   = "#6b7d8e";
    private static final String TEXT_PRIMARY  = "#f0e6d3";
    private static final String TEXT_MUTED    = "#9baab8";
    private static final String TEXT_DARK     = "#0f1923";
    private static final String BORDER_COLOR  = "#2e4155";
    private static final String INPUT_BG      = "#111e2b";
    private static final String SUCCESS_COLOR = "#4caf7d";
    private static final String ERROR_COLOR   = "#e05c5c";

    private final ObservableList<Student> tableData = FXCollections.observableArrayList();

    public Tab createTab() {
        Tab tab = new Tab("  Students");
        tab.setClosable(false);

        Label title = new Label("Student Management");
        title.setFont(Font.font("Georgia", FontWeight.BOLD, 22));
        title.setTextFill(Color.web(ACCENT_GOLD));

        TextField txtId     = field("e.g. STU001");
        TextField txtName   = field("Full name");
        TextField txtPhone  = field("10-digit number");
        TextField txtEmail  = field("Optional");
        TextField txtCourse = field("e.g. B.Tech CSE");

        ComboBox<String> cmbRoom = new ComboBox<>();
        styleCombo(cmbRoom);
        cmbRoom.setPrefWidth(210);
        populateRoomCombo(cmbRoom);

        CheckBox chkMess = new CheckBox("Subscribe to Mess");
        chkMess.setFont(Font.font("Segoe UI", 13));
        chkMess.setTextFill(Color.web(TEXT_PRIMARY));
        chkMess.setStyle("-fx-text-fill:" + TEXT_PRIMARY + ";");

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

        addRow(form, 0, "Student ID:",  txtId,     "Name:",    txtName);
        addRow(form, 1, "Contact:",     txtPhone,  "Email:",   txtEmail);
        addRow(form, 2, "Course:",      txtCourse, "Room No:", cmbRoom);
        form.add(chkMess, 1, 3);

        Button btnAdmit   = btn("Admit Student",     ACCENT_GREEN,  TEXT_DARK);
        Button btnViewAll = btn("View All Students", ACCENT_BLUE,   TEXT_DARK);
        Button btnRefRoom = btn("Refresh Rooms",     ACCENT_ORANGE, "#fff5f0");
        Button btnClear   = btn("Clear Fields",      ACCENT_GREY,   TEXT_PRIMARY);

        HBox buttons = new HBox(10, btnAdmit, btnViewAll, btnRefRoom, btnClear);
        buttons.setAlignment(Pos.CENTER_LEFT);

        Label lblStatus = statusLabel();

        TableView<Student> table = new TableView<>(tableData);
        styleTable(table);
        table.getColumns().addAll(
                col("ID",         "studentId",      90),
                col("Name",       "name",          150),
                col("Contact",    "contact",       120),
                col("Course",     "course",        130),
                col("Room",       "roomNumber",     70),
                col("Check-In",   "checkInDate",   110),
                col("Mess",       "messSubscribed", 60));
        tableData.setAll(DataStore.getStudents());

        btnAdmit.setOnAction(e -> {
            String id     = txtId.getText().trim();
            String name   = txtName.getText().trim();
            String phone  = txtPhone.getText().trim();
            String email  = txtEmail.getText().trim();
            String course = txtCourse.getText().trim();
            String room   = cmbRoom.getValue();

            if (id.isEmpty() || name.isEmpty() || phone.isEmpty()
                    || course.isEmpty() || room == null) {
                status(lblStatus, "Please fill all required fields and select a room.", false);
                return;
            }
            if (DataStore.studentExists(id)) {
                status(lblStatus, "Student ID " + id + " already exists.", false);
                return;
            }
            Room r = DataStore.findRoom(room);
            if (r == null || !r.isAvailable()) {
                status(lblStatus, "Selected room is not available.", false);
                return;
            }

            Student s = new Student(id, name, phone, email, course, room, chkMess.isSelected());
            DataStore.getStudents().add(s);
            r.setAvailable(false);
            FileManager.saveStudents(DataStore.getStudents());
            FileManager.saveRooms(DataStore.getRooms());

            tableData.setAll(DataStore.getStudents());
            populateRoomCombo(cmbRoom);
            status(lblStatus, "Student " + name + " admitted to Room " + room + ".", true);
            clearForm(txtId, txtName, txtPhone, txtEmail, txtCourse, cmbRoom, chkMess);
        });

        btnViewAll.setOnAction(e -> {
            tableData.setAll(DataStore.getStudents());
            status(lblStatus, "Total students: " + tableData.size(), true);
        });

        btnRefRoom.setOnAction(e -> {
            populateRoomCombo(cmbRoom);
            status(lblStatus, "Room list refreshed.", true);
        });

        btnClear.setOnAction(e -> {
            clearForm(txtId, txtName, txtPhone, txtEmail, txtCourse, cmbRoom, chkMess);
            lblStatus.setText("");
        });

        VBox root = new VBox(16,
                title, styledSep(),
                sectionLabel("Admit New Student"), form,
                buttons, lblStatus,
                styledSep(), table);
        root.setPadding(new Insets(22));
        root.setStyle("-fx-background-color:" + BG_TAB + ";");
        VBox.setVgrow(table, Priority.ALWAYS);

        tab.setContent(root);
        return tab;
    }

    private void populateRoomCombo(ComboBox<String> cmb) {
        cmb.getItems().clear();
        DataStore.getRooms().stream()
                .filter(Room::isAvailable)
                .forEach(r -> cmb.getItems().add(r.getRoomNumber()));
    }

    @SuppressWarnings("unchecked")
    private <T> TableColumn<Student, T> col(String header, String prop, double w) {
        TableColumn<Student, T> c = new TableColumn<>(header);
        c.setCellValueFactory(new PropertyValueFactory<>(prop));
        c.setPrefWidth(w);
        return c;
    }

    private void addRow(GridPane g, int row,
                        String l1, javafx.scene.Node f1,
                        String l2, javafx.scene.Node f2) {
        Label lab1 = lbl(l1); Label lab2 = lbl(l2);
        g.add(lab1, 0, row); g.add(f1, 1, row);
        g.add(lab2, 2, row); g.add(f2, 3, row);
    }

    private Label lbl(String t) {
        Label l = new Label(t);
        l.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 13));
        l.setTextFill(Color.web(TEXT_MUTED));
        l.setMinWidth(90);
        return l;
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
            "-fx-background-radius:6;"
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

    private void clearForm(TextField id, TextField name, TextField phone,
                           TextField email, TextField course,
                           ComboBox<String> cmb, CheckBox chk) {
        id.clear(); name.clear(); phone.clear();
        email.clear(); course.clear();
        cmb.setValue(null);
        chk.setSelected(false);
    }
}



package hostel;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

public class StudentPortal {

    private static final String BG_DARK       = "#0f1923";
    private static final String BG_HEADER     = "#111e2b";
    private static final String BG_TAB        = "#162030";
    private static final String BG_FORM       = "#1c2b3a";
    private static final String BG_BANNER     = "#172436";
    private static final String ACCENT_TEAL   = "#3d8b6e";
    private static final String ACCENT_TEAL_LT= "#5cb88a";
    private static final String ACCENT_GOLD   = "#c9a84c";
    private static final String TEXT_PRIMARY  = "#f0e6d3";
    private static final String TEXT_MUTED    = "#9baab8";
    private static final String BORDER_COLOR  = "#2e4155";
    private static final String INPUT_BG      = "#111e2b";

    private final Stage   stage;
    private final Student student;

    public StudentPortal(Stage stage, Student student) {
        this.stage   = stage;
        this.student = student;
    }

    public Scene buildScene() {

        // ── Header ────────────────────────────────────────────────────────────
        Label appName = new Label("Hostel Management System");
        appName.setFont(Font.font("Georgia", FontWeight.BOLD, 20));
        appName.setTextFill(Color.web(ACCENT_TEAL_LT));

        Label tagline = new Label("Student Portal  •  " + student.getName());
        tagline.setFont(Font.font("Segoe UI", FontPosture.ITALIC, 12));
        tagline.setTextFill(Color.web(TEXT_MUTED));

        VBox titleBox = new VBox(3, appName, tagline);

        Label lblWho = new Label("ID: " + student.getStudentId()
                + "  |  Room: " + student.getRoomNumber());
        lblWho.setFont(Font.font("Segoe UI", 12));
        lblWho.setTextFill(Color.web(TEXT_MUTED));

        Button btnLogout = new Button("Logout");
        btnLogout.setStyle(
            "-fx-background-color:#8b2c2c;" +
            "-fx-text-fill:#ffe0e0;" +
            "-fx-font-size:12px;" +
            "-fx-padding:5 14;" +
            "-fx-background-radius:6;" +
            "-fx-cursor:hand;"
        );
        btnLogout.setOnMouseEntered(e -> btnLogout.setOpacity(0.85));
        btnLogout.setOnMouseExited(e  -> btnLogout.setOpacity(1.0));
        btnLogout.setOnAction(e -> logout());

        VBox rightBox = new VBox(5, lblWho, btnLogout);
        rightBox.setAlignment(Pos.CENTER_RIGHT);

        HBox header = new HBox(titleBox);
        HBox.setHgrow(titleBox, Priority.ALWAYS);
        header.getChildren().add(rightBox);
        header.setPadding(new Insets(14, 22, 14, 22));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle(
            "-fx-background-color: linear-gradient(to right, #0a1a12, #111e2b, #162030);" +
            "-fx-border-color:" + BORDER_COLOR + ";" +
            "-fx-border-width: 0 0 1 0;"
        );

        // ── Tab pane ──────────────────────────────────────────────────────────
        TabPane tabPane = new TabPane();
        tabPane.setTabMinHeight(38);
        tabPane.setTabMinWidth(160);
        tabPane.setStyle(
            "-fx-font-size:13px;" +
            "-fx-font-family:'Segoe UI';" +
            "-fx-background-color:" + BG_TAB + ";"
        );
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        tabPane.getTabs().addAll(
                buildMyDetailsTab(),
                buildMessMenuTab());

        // ── Footer ────────────────────────────────────────────────────────────
        Label footer = new Label(
                "Student Portal  |  Read-Only View  |  Contact admin for changes");
        footer.setFont(Font.font("Segoe UI", 11));
        footer.setTextFill(Color.web(TEXT_MUTED));

        HBox footerBar = new HBox(footer);
        footerBar.setPadding(new Insets(7, 18, 7, 18));
        footerBar.setStyle(
            "-fx-background-color:" + BG_HEADER + ";" +
            "-fx-border-color:" + BORDER_COLOR + ";" +
            "-fx-border-width:1 0 0 0;"
        );

        BorderPane root = new BorderPane();
        root.setTop(header);
        root.setCenter(tabPane);
        root.setBottom(footerBar);
        root.setStyle("-fx-background-color:" + BG_DARK + ";");

        Scene scene = new Scene(root, 900, 640);
        stage.setMinWidth(700);
        stage.setMinHeight(520);
        return scene;
    }

    private Tab buildMyDetailsTab() {
        Tab tab = new Tab("  My Details");

        Label title = new Label("My Hostel Details");
        title.setFont(Font.font("Georgia", FontWeight.BOLD, 22));
        title.setTextFill(Color.web(ACCENT_TEAL_LT));

        Label note = new Label("This is a read-only view. Contact the admin to update your information.");
        note.setFont(Font.font("Segoe UI", FontPosture.ITALIC, 12));
        note.setTextFill(Color.web(TEXT_MUTED));
        note.setWrapText(true);

        Room room = DataStore.findRoom(student.getRoomNumber());

        GridPane card = new GridPane();
        card.setHgap(22);
        card.setVgap(14);
        card.setPadding(new Insets(20));
        card.setStyle(
            "-fx-background-color:" + BG_FORM + ";" +
            "-fx-border-color:" + BORDER_COLOR + ";" +
            "-fx-border-radius:10;" +
            "-fx-background-radius:10;"
        );

        String[][] rows = {
            { "Student ID",        student.getStudentId() },
            { "Full Name",         student.getName() },
            { "Contact Number",    student.getContact() },
            { "Email",             student.getEmail().isEmpty() ? "—" : student.getEmail() },
            { "Course",            student.getCourse() },
            { "Room Number",       student.getRoomNumber() },
            { "Room Type",         room != null ? room.getRoomType() : "—" },
            { "Floor",             room != null ? String.valueOf(room.getFloor()) : "—" },
            { "Rent / Month",      room != null ? "Rs " + (int)room.getPricePerMonth() : "—" },
            { "Check-In Date",     student.getCheckInDate().toString() },
            { "Mess Subscription", student.isMessSubscribed() ? "Yes (Subscribed)" : "No" },
            { "Status",            student.getCheckOutDate() == null
                                       ? "Currently Residing"
                                       : "Checked Out on " + student.getCheckOutDate() }
        };

        for (int i = 0; i < rows.length; i++) {
            Label key = new Label(rows[i][0] + ":");
            key.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
            key.setTextFill(Color.web(TEXT_MUTED));
            key.setMinWidth(170);

            Label val = new Label(rows[i][1]);
            val.setFont(Font.font("Segoe UI", 13));
            val.setTextFill(Color.web(TEXT_PRIMARY));

            card.add(key, 0, i);
            card.add(val, 1, i);
        }

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color:#2e4155;");

        VBox content = new VBox(16, title, note, sep, card);
        content.setPadding(new Insets(24));
        content.setStyle("-fx-background-color:" + BG_TAB + ";");

        ScrollPane sp = new ScrollPane(content);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color:transparent;-fx-background:" + BG_TAB + ";");

        tab.setContent(sp);
        return tab;
    }

    private Tab buildMessMenuTab() {
        Tab tab = new Tab("  Mess Menu");

        Label title = new Label("Mess Menu");
        title.setFont(Font.font("Georgia", FontWeight.BOLD, 22));
        title.setTextFill(Color.web(ACCENT_TEAL_LT));

        String todayName = LocalDate.now()
                .getDayOfWeek()
                .getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        MessMenu today = DataStore.findMenuByDay(todayName);

        VBox todayBox = new VBox(8);
        todayBox.setPadding(new Insets(16));
        todayBox.setStyle(
            "-fx-background-color:" + BG_BANNER + ";" +
            "-fx-border-color:" + ACCENT_GOLD + ";" +
            "-fx-border-radius:8;" +
            "-fx-background-radius:8;" +
            "-fx-border-width:1;"
        );

        Label todayHdr = new Label("Today's Menu  (" + todayName + ")");
        todayHdr.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        todayHdr.setTextFill(Color.web(ACCENT_GOLD));
        todayBox.getChildren().add(todayHdr);

        if (today != null) {
            todayBox.getChildren().addAll(
                    mealRow("Breakfast",     today.getBreakfast()),
                    mealRow("Lunch",         today.getLunch()),
                    mealRow("Evening Snack", today.getEveningSnack()),
                    mealRow("Dinner",        today.getDinner()));
        } else {
            Label noMenu = new Label("Menu not available for today.");
            noMenu.setTextFill(Color.web(TEXT_MUTED));
            todayBox.getChildren().add(noMenu);
        }

        Label weekLabel = new Label("Full Weekly Menu");
        weekLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        weekLabel.setTextFill(Color.web(TEXT_PRIMARY));

        TableView<MessMenu> table = new TableView<>(
                FXCollections.observableArrayList(DataStore.getMenus()));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setEditable(false);
        table.setPrefHeight(260);
        table.setStyle(
            "-fx-background-color:#111e2b;" +
            "-fx-text-fill:" + TEXT_PRIMARY + ";" +
            "-fx-border-color:" + BORDER_COLOR + ";" +
            "-fx-border-radius:6;" +
            "-fx-background-radius:6;"
        );

        table.getColumns().addAll(
                menuCol("Day",          "day",          90),
                menuCol("Breakfast",    "breakfast",    180),
                menuCol("Lunch",        "lunch",        210),
                menuCol("Eve Snack",    "eveningSnack", 150),
                menuCol("Dinner",       "dinner",       200));

        table.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(MessMenu item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null && item.getDay().equalsIgnoreCase(todayName)) {
                    setStyle("-fx-background-color:#1e3a2a;");
                } else if (!empty) {
                    setStyle("-fx-background-color:#111e2b;");
                } else {
                    setStyle("");
                }
            }
        });

        // Mess subscription badge
        String subText = student.isMessSubscribed()
                ? "You are subscribed to the mess."
                : "You are NOT subscribed to the mess. Contact admin to subscribe.";
        String badgeBg    = student.isMessSubscribed() ? "#1a3a2a" : "#3a2a0a";
        String badgeBorder= student.isMessSubscribed() ? "#4caf7d" : "#c9a84c";
        String badgeText  = student.isMessSubscribed() ? "#4caf7d" : "#c9a84c";

        Label lblSub = new Label(subText);
        lblSub.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        lblSub.setTextFill(Color.web(badgeText));
        lblSub.setWrapText(true);
        lblSub.setPadding(new Insets(10, 14, 10, 14));
        lblSub.setStyle(
            "-fx-background-color:" + badgeBg + ";" +
            "-fx-border-radius:6;" +
            "-fx-background-radius:6;" +
            "-fx-border-color:" + badgeBorder + ";" +
            "-fx-border-width:1;"
        );

        Separator sep1 = new Separator();
        sep1.setStyle("-fx-background-color:#2e4155;");
        Separator sep2 = new Separator();
        sep2.setStyle("-fx-background-color:#2e4155;");

        VBox body = new VBox(16,
                title, sep1,
                lblSub,
                todayBox,
                sep2,
                weekLabel, table);
        body.setPadding(new Insets(24));
        body.setStyle("-fx-background-color:" + BG_TAB + ";");

        ScrollPane sp = new ScrollPane(body);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color:transparent;-fx-background:" + BG_TAB + ";");

        tab.setContent(sp);
        return tab;
    }

    private HBox mealRow(String meal, String items) {
        Label k = new Label(meal + ":  ");
        k.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        k.setTextFill(Color.web(ACCENT_GOLD));
        k.setMinWidth(150);
        Label v = new Label(items);
        v.setFont(Font.font("Segoe UI", 13));
        v.setTextFill(Color.web(TEXT_PRIMARY));
        v.setWrapText(true);
        HBox row = new HBox(k, v);
        row.setAlignment(Pos.TOP_LEFT);
        return row;
    }

    @SuppressWarnings("unchecked")
    private TableColumn<MessMenu, String> menuCol(String header, String prop, double w) {
        TableColumn<MessMenu, String> c = new TableColumn<>(header);
        c.setCellValueFactory(new PropertyValueFactory<>(prop));
        c.setPrefWidth(w);
        c.setCellFactory(col -> {
            TableCell<MessMenu, String> cell = new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) setText(null);
                    else {
                        setText(item);
                        setWrapText(true);
                        setTextFill(Color.web(TEXT_PRIMARY));
                    }
                }
            };
            return cell;
        });
        return c;
    }

    private void logout() {
        AuthManager.logout();
        LoginPage login = new LoginPage(stage);
        stage.setScene(login.buildScene());
        stage.setTitle("Hostel Management System");
    }
}



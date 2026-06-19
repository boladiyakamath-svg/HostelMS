package hostel;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;

public class AdminDashboard {

    private static final String BG_DARK       = "#0f1923";
    private static final String BG_HEADER     = "#111e2b";
    private static final String ACCENT_GOLD   = "#c9a84c";
    private static final String TEXT_PRIMARY  = "#f0e6d3";
    private static final String TEXT_MUTED    = "#9baab8";
    private static final String BORDER_COLOR  = "#2e4155";
    private static final String TAB_BG        = "#162030";
    private static final String FOOTER_BG     = "#111e2b";

    private final Stage stage;

    public AdminDashboard(Stage stage) {
        this.stage = stage;
    }

    public Scene buildScene() {

        // ── Header ────────────────────────────────────────────────────────────
        Label appName = new Label("Hostel Management System");
        appName.setFont(Font.font("Georgia", FontWeight.BOLD, 20));
        appName.setTextFill(Color.web(ACCENT_GOLD));

        Label tagline = new Label("Admin Panel  •  Full Access");
        tagline.setFont(Font.font("Segoe UI", FontPosture.ITALIC, 12));
        tagline.setTextFill(Color.web(TEXT_MUTED));

        VBox titleBox = new VBox(3, appName, tagline);

        String adminName = AuthManager.getCurrentUser() != null
                ? AuthManager.getCurrentUser().getUsername() : "admin";
        Label lblWho = new Label("Logged in as: " + adminName);
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
            "-fx-background-color: linear-gradient(to right, #0a1219, " + BG_HEADER + ", #162030);" +
            "-fx-border-color:" + BORDER_COLOR + ";" +
            "-fx-border-width: 0 0 1 0;"
        );

        // ── Tab pane ──────────────────────────────────────────────────────────
        TabPane tabPane = new TabPane();
        tabPane.setTabMinHeight(38);
        tabPane.setTabMinWidth(130);
        tabPane.setStyle(
            "-fx-font-size:13px;" +
            "-fx-font-family:'Segoe UI';" +
            "-fx-background-color:" + TAB_BG + ";" +
            "-fx-tab-header-area-background-color:" + BG_DARK + ";"
        );
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        tabPane.getTabs().addAll(
                new DashboardTab().createTab(),
                new RoomTab().createTab(),
                new StudentTab().createTab(),
                new BookingTab().createTab(),
                new MessTab().createTab());

        // ── Footer ────────────────────────────────────────────────────────────
        Label footer = new Label(
                "Hostel Management System  |  JavaFX 17  |  JDK 17  |  Data saved to hostel_data/");
        footer.setFont(Font.font("Segoe UI", 11));
        footer.setTextFill(Color.web(TEXT_MUTED));

        HBox footerBar = new HBox(footer);
        footerBar.setPadding(new Insets(7, 18, 7, 18));
        footerBar.setStyle(
            "-fx-background-color:" + FOOTER_BG + ";" +
            "-fx-border-color:" + BORDER_COLOR + ";" +
            "-fx-border-width:1 0 0 0;"
        );

        // ── Root ──────────────────────────────────────────────────────────────
        BorderPane root = new BorderPane();
        root.setTop(header);
        root.setCenter(tabPane);
        root.setBottom(footerBar);
        root.setStyle("-fx-background-color:" + BG_DARK + ";");

        Scene scene = new Scene(root, 1150, 720);
        stage.setMinWidth(900);
        stage.setMinHeight(620);
        return scene;
    }

    private void logout() {
        AuthManager.logout();
        LoginPage login = new LoginPage(stage);
        stage.setScene(login.buildScene());
        stage.setTitle("Hostel Management System");
    }
}



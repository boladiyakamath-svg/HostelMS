package hostel;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;

public class LoginPage {

    private static final String BG_DARK        = "#0f1923";
    private static final String BG_CARD        = "#1c2b3a";
    private static final String ACCENT_GOLD    = "#c9a84c";
    private static final String ACCENT_GOLD_LT = "#e8c97a";
    private static final String TEXT_PRIMARY   = "#f0e6d3";
    private static final String TEXT_SECONDARY = "#9baab8";
    private static final String BORDER_SUBTLE  = "#2e4155";
    private static final String INPUT_BG       = "#162030";
    private static final String BTN_ADMIN      = "#c9a84c";
    private static final String BTN_STUDENT    = "#3d8b6e";
    private static final String BTN_REGISTER   = "#7c5cbf";
    private static final String SUCCESS_COLOR  = "#4caf7d";
    private static final String ERROR_COLOR    = "#e05c5c";

    private final Stage stage;

    public LoginPage(Stage stage) {
        this.stage = stage;
    }

    public Scene buildScene() {
        Label appTitle = new Label("Hostel Management System");
        appTitle.setFont(Font.font("Georgia", FontWeight.BOLD, 28));
        appTitle.setTextFill(Color.web(ACCENT_GOLD));

        Label appSub = new Label("Welcome — please log in to continue");
        appSub.setFont(Font.font("Segoe UI", FontPosture.ITALIC, 13));
        appSub.setTextFill(Color.web(TEXT_SECONDARY));

        Region divLine = new Region();
        divLine.setPrefHeight(2);
        divLine.setMaxWidth(340);
        divLine.setStyle("-fx-background-color: linear-gradient(to right, transparent, " + ACCENT_GOLD + ", transparent);");

        VBox brandBox = new VBox(8, appTitle, divLine, appSub);
        brandBox.setAlignment(Pos.CENTER);
        brandBox.setPadding(new Insets(36, 20, 32, 20));
        brandBox.setStyle("-fx-background-color: linear-gradient(to bottom, #0a1219, " + BG_DARK + ");");

        TabPane tabs = new TabPane(buildAdminTab(), buildStudentTab());
        tabs.setTabMinHeight(40);
        tabs.setTabMinWidth(180);
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabs.setStyle(
            "-fx-font-size:13px;" +
            "-fx-font-family:'Segoe UI';" +
            "-fx-background-color:" + BG_CARD + ";"
        );

        VBox card = new VBox(tabs);
        card.setMaxWidth(520);
        card.setStyle(
            "-fx-background-color:" + BG_CARD + ";" +
            "-fx-border-radius:12;" +
            "-fx-background-radius:12;" +
            "-fx-border-color:" + BORDER_SUBTLE + ";" +
            "-fx-border-width:1;" +
            "-fx-effect:dropshadow(gaussian,rgba(0,0,0,0.55),24,0,0,8);"
        );

        StackPane centre = new StackPane(card);
        centre.setPadding(new Insets(44));
        centre.setStyle("-fx-background-color:" + BG_DARK + ";");

        BorderPane root = new BorderPane();
        root.setTop(brandBox);
        root.setCenter(centre);
        root.setStyle("-fx-background-color:" + BG_DARK + ";");

        return new Scene(root, 900, 640);
    }

    private Tab buildAdminTab() {
        Tab tab = new Tab("  Admin Login  ");

        Label heading = new Label("Admin Login");
        heading.setFont(Font.font("Georgia", FontWeight.BOLD, 20));
        heading.setTextFill(Color.web(ACCENT_GOLD));

        Label lblUser = new Label("Username:");
        Label lblPass = new Label("Password:");
        styleLabel(lblUser); styleLabel(lblPass);

        TextField txtUser = inputField("Enter admin username");
        PasswordField txtPass = passField("Enter password");

        Button btnLogin = bigBtn("Login as Admin", BTN_ADMIN, "#0f1923");
        Label  lblStatus = statusLbl();

        Label hint = new Label("Default: admin / admin123");
        hint.setFont(Font.font("Segoe UI", FontPosture.ITALIC, 11));
        hint.setTextFill(Color.web(TEXT_SECONDARY));

        btnLogin.setOnAction(e -> {
            String u = txtUser.getText().trim();
            String p = txtPass.getText();
            if (u.isEmpty() || p.isEmpty()) {
                status(lblStatus, "Please enter username and password.", false); return;
            }
            UserAccount account = AuthManager.login(u, p);
            if (account == null || !account.isAdmin()) {
                status(lblStatus, "Invalid admin credentials.", false); return;
            }
            AuthManager.setCurrentUser(account);
            openAdminDashboard();
        });

        txtPass.setOnAction(e -> btnLogin.fire());

        GridPane form = new GridPane();
        form.setHgap(14); form.setVgap(14);
        form.add(lblUser, 0, 0); form.add(txtUser, 1, 0);
        form.add(lblPass, 0, 1); form.add(txtPass, 1, 1);

        VBox box = new VBox(20, heading, form, btnLogin, lblStatus, hint);
        box.setPadding(new Insets(32, 32, 32, 32));
        box.setAlignment(Pos.TOP_LEFT);
        box.setStyle("-fx-background-color:" + BG_CARD + ";");

        tab.setContent(box);
        return tab;
    }

    private Tab buildStudentTab() {
        Tab tab = new Tab("  Student Login  ");

        Label loginHeading = new Label("Student Login");
        loginHeading.setFont(Font.font("Georgia", FontWeight.BOLD, 20));
        loginHeading.setTextFill(Color.web(ACCENT_GOLD_LT));

        Label lblLUser = new Label("Username:");
        Label lblLPass = new Label("Password:");
        styleLabel(lblLUser); styleLabel(lblLPass);

        TextField     txtLUser = inputField("Your registered username");
        PasswordField txtLPass = passField("Your password");

        Button btnLogin  = bigBtn("Login as Student", BTN_STUDENT, "#f0fdf4");
        Label  lblLStatus = statusLbl();

        btnLogin.setOnAction(e -> {
            String u = txtLUser.getText().trim();
            String p = txtLPass.getText();
            if (u.isEmpty() || p.isEmpty()) {
                status(lblLStatus, "Please enter username and password.", false); return;
            }
            UserAccount account = AuthManager.login(u, p);
            if (account == null || !account.isStudent()) {
                status(lblLStatus, "Invalid student credentials.", false); return;
            }
            Student s = DataStore.findStudent(account.getLinkedStudentId());
            if (s == null || s.getCheckOutDate() != null) {
                status(lblLStatus, "Your hostel record is inactive. Contact admin.", false);
                return;
            }
            AuthManager.setCurrentUser(account);
            openStudentPortal(s);
        });

        txtLPass.setOnAction(e -> btnLogin.fire());

        GridPane loginForm = new GridPane();
        loginForm.setHgap(14); loginForm.setVgap(14);
        loginForm.add(lblLUser, 0, 0); loginForm.add(txtLUser, 1, 0);
        loginForm.add(lblLPass, 0, 1); loginForm.add(txtLPass, 1, 1);

        Region sepLine = new Region();
        sepLine.setPrefHeight(1);
        sepLine.setStyle("-fx-background-color:" + BORDER_SUBTLE + ";");

        Label regHeading = new Label("New Student? Create an Account");
        regHeading.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        regHeading.setTextFill(Color.web(TEXT_PRIMARY));

        Label regNote = new Label(
                "You must already be admitted by the admin before registering.");
        regNote.setFont(Font.font("Segoe UI", FontPosture.ITALIC, 11));
        regNote.setTextFill(Color.web(TEXT_SECONDARY));
        regNote.setWrapText(true);

        Label lblRId   = new Label("Student ID:");
        Label lblRUser = new Label("Username:");
        Label lblRPass = new Label("Password:");
        Label lblRConf = new Label("Confirm Password:");
        for (Label l : new Label[]{lblRId, lblRUser, lblRPass, lblRConf}) styleLabel(l);

        TextField     txtRId   = inputField("Your student ID (e.g. STU001)");
        TextField     txtRUser = inputField("Choose a username");
        PasswordField txtRPass = passField("Choose a password");
        PasswordField txtRConf = passField("Re-enter password");

        Button btnRegister = bigBtn("Register", BTN_REGISTER, "#f5f0ff");
        Label  lblRStatus  = statusLbl();

        btnRegister.setOnAction(e -> {
            String id   = txtRId.getText().trim();
            String user = txtRUser.getText().trim();
            String pass = txtRPass.getText();
            String conf = txtRConf.getText();

            if (id.isEmpty() || user.isEmpty() || pass.isEmpty() || conf.isEmpty()) {
                status(lblRStatus, "Please fill all registration fields.", false); return;
            }
            if (!pass.equals(conf)) {
                status(lblRStatus, "Passwords do not match.", false); return;
            }
            if (pass.length() < 4) {
                status(lblRStatus, "Password must be at least 4 characters.", false); return;
            }
            Student s = DataStore.findStudent(id);
            if (s == null || s.getCheckOutDate() != null) {
                status(lblRStatus, "Student ID not found or already checked out.", false); return;
            }
            boolean alreadyHasAccount = AuthManager.getAccounts().stream()
                    .anyMatch(a -> a.isStudent() && id.equals(a.getLinkedStudentId()));
            if (alreadyHasAccount) {
                status(lblRStatus, "An account already exists for this student ID.", false); return;
            }
            if (AuthManager.usernameExists(user)) {
                status(lblRStatus, "Username '" + user + "' is already taken.", false); return;
            }

            boolean ok = AuthManager.registerStudent(user, pass, id);
            if (ok) {
                status(lblRStatus, "Account created! You can now log in.", true);
                txtRId.clear(); txtRUser.clear(); txtRPass.clear(); txtRConf.clear();
            } else {
                status(lblRStatus, "Registration failed. Try a different username.", false);
            }
        });

        GridPane regForm = new GridPane();
        regForm.setHgap(14); regForm.setVgap(10);
        regForm.add(lblRId,   0, 0); regForm.add(txtRId,   1, 0);
        regForm.add(lblRUser, 0, 1); regForm.add(txtRUser, 1, 1);
        regForm.add(lblRPass, 0, 2); regForm.add(txtRPass, 1, 2);
        regForm.add(lblRConf, 0, 3); regForm.add(txtRConf, 1, 3);

        VBox box = new VBox(14,
                loginHeading, loginForm, btnLogin, lblLStatus,
                sepLine,
                regHeading, regNote, regForm, btnRegister, lblRStatus);
        box.setPadding(new Insets(26, 32, 26, 32));
        box.setStyle("-fx-background-color:" + BG_CARD + ";");

        ScrollPane sp = new ScrollPane(box);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color:transparent;-fx-background:" + BG_CARD + ";");

        tab.setContent(sp);
        return tab;
    }

    private void openAdminDashboard() {
        AdminDashboard dash = new AdminDashboard(stage);
        stage.setScene(dash.buildScene());
        stage.setTitle("Hostel Management System  —  Admin");
    }

    private void openStudentPortal(Student student) {
        StudentPortal portal = new StudentPortal(stage, student);
        stage.setScene(portal.buildScene());
        stage.setTitle("Hostel Management System  —  Student Portal");
    }

    private void styleLabel(Label l) {
        l.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 13));
        l.setTextFill(Color.web(TEXT_SECONDARY));
        l.setMinWidth(140);
    }

    private TextField inputField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setPrefWidth(230);
        tf.setStyle(
            "-fx-font-size:13px;" +
            "-fx-background-color:" + INPUT_BG + ";" +
            "-fx-text-fill:" + TEXT_PRIMARY + ";" +
            "-fx-prompt-text-fill:" + TEXT_SECONDARY + ";" +
            "-fx-border-color:" + BORDER_SUBTLE + ";" +
            "-fx-border-radius:6;" +
            "-fx-background-radius:6;" +
            "-fx-padding:7 10;"
        );
        return tf;
    }

    private PasswordField passField(String prompt) {
        PasswordField pf = new PasswordField();
        pf.setPromptText(prompt);
        pf.setPrefWidth(230);
        pf.setStyle(
            "-fx-font-size:13px;" +
            "-fx-background-color:" + INPUT_BG + ";" +
            "-fx-text-fill:" + TEXT_PRIMARY + ";" +
            "-fx-prompt-text-fill:" + TEXT_SECONDARY + ";" +
            "-fx-border-color:" + BORDER_SUBTLE + ";" +
            "-fx-border-radius:6;" +
            "-fx-background-radius:6;" +
            "-fx-padding:7 10;"
        );
        return pf;
    }

    private Button bigBtn(String text, String bgHex, String textHex) {
        Button b = new Button(text);
        b.setPrefWidth(Double.MAX_VALUE);
        String baseStyle =
            "-fx-background-color:" + bgHex + ";" +
            "-fx-text-fill:" + textHex + ";" +
            "-fx-font-size:14px;" +
            "-fx-padding:10 0;" +
            "-fx-background-radius:7;" +
            "-fx-font-family:'Segoe UI';" +
            "-fx-font-weight:bold;" +
            "-fx-cursor:hand;";
        b.setStyle(baseStyle);
        b.setOnMouseEntered(e -> b.setOpacity(0.85));
        b.setOnMouseExited(e  -> b.setOpacity(1.0));
        return b;
    }

    private Label statusLbl() {
        Label l = new Label();
        l.setFont(Font.font("Segoe UI", 13));
        l.setWrapText(true);
        return l;
    }

    private void status(Label l, String msg, boolean ok) {
        l.setText(msg);
        l.setTextFill(ok ? Color.web(SUCCESS_COLOR) : Color.web(ERROR_COLOR));
    }
}



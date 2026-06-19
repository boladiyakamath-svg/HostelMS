package hostel;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

public class MessTab {

    private static final String BG_TAB        = "#162030";
    private static final String BG_FORM       = "#1c2b3a";
    private static final String BG_BANNER     = "#172436";
    private static final String ACCENT_GOLD   = "#c9a84c";
    private static final String ACCENT_GREEN  = "#4caf7d";
    private static final String ACCENT_GREY   = "#6b7d8e";
    private static final String TEXT_PRIMARY  = "#f0e6d3";
    private static final String TEXT_MUTED    = "#9baab8";
    private static final String TEXT_DARK     = "#0f1923";
    private static final String BORDER_COLOR  = "#2e4155";
    private static final String INPUT_BG      = "#111e2b";
    private static final String SUCCESS_COLOR = "#4caf7d";
    private static final String ERROR_COLOR   = "#e05c5c";

    private static final String[] DAYS = {
        "Monday", "Tuesday", "Wednesday", "Thursday",
        "Friday", "Saturday", "Sunday"
    };

    public Tab createTab() {
        Tab tab = new Tab("  Mess");
        tab.setClosable(false);

        Label title = new Label("Mess Management");
        title.setFont(Font.font("Georgia", FontWeight.BOLD, 22));
        title.setTextFill(Color.web(ACCENT_GOLD));

        String todayName = LocalDate.now()
                .getDayOfWeek()
                .getDisplayName(TextStyle.FULL, Locale.ENGLISH);

        VBox todayBox = buildTodayBanner(todayName);

        Label weekLabel = new Label("Weekly Mess Menu");
        weekLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        weekLabel.setTextFill(Color.web(TEXT_PRIMARY));

        TableView<MessMenu> weekTable = new TableView<>(
                FXCollections.observableArrayList(DataStore.getMenus()));
        weekTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        weekTable.setPrefHeight(210);
        weekTable.setStyle(
            "-fx-background-color:#111e2b;" +
            "-fx-text-fill:" + TEXT_PRIMARY + ";" +
            "-fx-border-color:" + BORDER_COLOR + ";" +
            "-fx-border-radius:6;" +
            "-fx-background-radius:6;"
        );
        weekTable.getColumns().addAll(
                menuCol("Day",          "day",          90),
                menuCol("Breakfast",    "breakfast",    190),
                menuCol("Lunch",        "lunch",        210),
                menuCol("Eve Snack",    "eveningSnack", 160),
                menuCol("Dinner",       "dinner",       210));

        // highlight today's row
        weekTable.setRowFactory(tv -> new TableRow<>() {
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

        Label editLabel = new Label("Edit Menu for a Day");
        editLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        editLabel.setTextFill(Color.web(TEXT_PRIMARY));

        ComboBox<String> cmbDay = new ComboBox<>();
        cmbDay.getItems().addAll(DAYS);
        cmbDay.setValue("Monday");
        cmbDay.setStyle(
            "-fx-font-size:13px;" +
            "-fx-background-color:" + INPUT_BG + ";" +
            "-fx-text-fill:" + TEXT_PRIMARY + ";" +
            "-fx-border-color:" + BORDER_COLOR + ";" +
            "-fx-border-radius:5;" +
            "-fx-background-radius:5;"
        );

        TextField txtBreakfast = editField();
        TextField txtLunch     = editField();
        TextField txtEvSnack   = editField();
        TextField txtDinner    = editField();

        cmbDay.setOnAction(e -> loadDay(cmbDay.getValue(),
                txtBreakfast, txtLunch, txtEvSnack, txtDinner));
        loadDay("Monday", txtBreakfast, txtLunch, txtEvSnack, txtDinner);

        GridPane editForm = new GridPane();
        editForm.setHgap(14);
        editForm.setVgap(12);
        editForm.setPadding(new Insets(16));
        editForm.setStyle(
            "-fx-background-color:" + BG_FORM + ";" +
            "-fx-border-color:" + BORDER_COLOR + ";" +
            "-fx-border-radius:8;" +
            "-fx-background-radius:8;"
        );

        editRow(editForm, 0, "Day:",           cmbDay);
        editRow(editForm, 1, "Breakfast:",     txtBreakfast);
        editRow(editForm, 2, "Lunch:",         txtLunch);
        editRow(editForm, 3, "Evening Snack:", txtEvSnack);
        editRow(editForm, 4, "Dinner:",        txtDinner);

        Button btnSave = btn("Save Menu", ACCENT_GREEN, TEXT_DARK);
        Label  lblSave = statusLabel();

        btnSave.setOnAction(e -> {
            MessMenu m = DataStore.findMenuByDay(cmbDay.getValue());
            if (m == null) {
                status(lblSave, "Day not found.", false); return;
            }
            m.setBreakfast(txtBreakfast.getText().trim());
            m.setLunch(txtLunch.getText().trim());
            m.setEveningSnack(txtEvSnack.getText().trim());
            m.setDinner(txtDinner.getText().trim());
            weekTable.refresh();
            FileManager.saveMessMenus(DataStore.getMenus());
            status(lblSave, "Menu for " + cmbDay.getValue() + " updated.", true);

            if (cmbDay.getValue().equals(todayName)) {
                todayBox.getChildren().setAll(buildTodayBanner(todayName).getChildren());
            }
        });

        Label subLabel = new Label("Active Mess Subscribers");
        subLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        subLabel.setTextFill(Color.web(TEXT_PRIMARY));

        TextArea subArea = new TextArea();
        subArea.setEditable(false);
        subArea.setPrefHeight(140);
        subArea.setStyle(
            "-fx-font-family:'Courier New';" +
            "-fx-font-size:12px;" +
            "-fx-background-color:#111e2b;" +
            "-fx-text-fill:" + TEXT_PRIMARY + ";" +
            "-fx-border-color:" + BORDER_COLOR + ";" +
            "-fx-border-radius:6;" +
            "-fx-background-radius:6;"
        );
        refreshSubs(subArea);

        Button btnRefSubs = btn("Refresh Subscribers", ACCENT_GREY, TEXT_PRIMARY);
        btnRefSubs.setOnAction(e -> refreshSubs(subArea));

        VBox body = new VBox(16,
                title, styledSep(),
                todayBox,
                styledSep(),
                weekLabel, weekTable,
                styledSep(),
                editLabel, editForm,
                new HBox(10, btnSave), lblSave,
                styledSep(),
                subLabel, subArea,
                new HBox(btnRefSubs));
        body.setPadding(new Insets(22));
        body.setStyle("-fx-background-color:" + BG_TAB + ";");

        ScrollPane sp = new ScrollPane(body);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color:transparent;-fx-background:" + BG_TAB + ";");

        tab.setContent(sp);
        return tab;
    }

    private VBox buildTodayBanner(String todayName) {
        VBox box = new VBox(8);
        box.setPadding(new Insets(16));
        box.setStyle(
            "-fx-background-color:" + BG_BANNER + ";" +
            "-fx-border-color:" + ACCENT_GOLD + ";" +
            "-fx-border-radius:8;" +
            "-fx-background-radius:8;" +
            "-fx-border-width:1;"
        );

        Label hdr = new Label("Today's Mess Menu  (" + todayName + ")");
        hdr.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        hdr.setTextFill(Color.web(ACCENT_GOLD));
        box.getChildren().add(hdr);

        MessMenu m = DataStore.findMenuByDay(todayName);
        if (m != null) {
            box.getChildren().addAll(
                    mealRow("Breakfast",     m.getBreakfast()),
                    mealRow("Lunch",         m.getLunch()),
                    mealRow("Evening Snack", m.getEveningSnack()),
                    mealRow("Dinner",        m.getDinner()));
        } else {
            Label no = new Label("Menu not set for today.");
            no.setTextFill(Color.web(TEXT_MUTED));
            box.getChildren().add(no);
        }
        return box;
    }

    private HBox mealRow(String meal, String items) {
        Label k = new Label(meal + ":  ");
        k.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        k.setTextFill(Color.web(ACCENT_GOLD));
        k.setMinWidth(140);
        Label v = new Label(items);
        v.setFont(Font.font("Segoe UI", 13));
        v.setTextFill(Color.web("#f0e6d3"));
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
                    if (empty || item == null) { setText(null); }
                    else {
                        setText(item);
                        setWrapText(true);
                        setTextFill(Color.web("#f0e6d3"));
                    }
                }
            };
            return cell;
        });
        return c;
    }

    private void loadDay(String day,
                         TextField b, TextField l, TextField ev, TextField d) {
        MessMenu m = DataStore.findMenuByDay(day);
        if (m != null) {
            b.setText(m.getBreakfast());
            l.setText(m.getLunch());
            ev.setText(m.getEveningSnack());
            d.setText(m.getDinner());
        }
    }

    private void editRow(GridPane g, int row, String labelText, javafx.scene.Node field) {
        Label lbl = new Label(labelText);
        lbl.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 13));
        lbl.setTextFill(Color.web(TEXT_MUTED));
        lbl.setMinWidth(140);
        g.add(lbl, 0, row);
        g.add(field, 1, row);
    }

    private TextField editField() {
        TextField tf = new TextField();
        tf.setPrefWidth(480);
        tf.setStyle(
            "-fx-font-size:13px;" +
            "-fx-background-color:" + INPUT_BG + ";" +
            "-fx-text-fill:#f0e6d3;" +
            "-fx-prompt-text-fill:#5a6e80;" +
            "-fx-border-color:" + BORDER_COLOR + ";" +
            "-fx-border-radius:5;" +
            "-fx-background-radius:5;" +
            "-fx-padding:6 10;"
        );
        return tf;
    }

    private void refreshSubs(TextArea ta) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-12s %-24s %-10s%n", "ID", "Name", "Room"));
        sb.append("-".repeat(48)).append("\n");
        DataStore.getStudents().stream()
                .filter(s -> s.isMessSubscribed() && s.getCheckOutDate() == null)
                .forEach(s -> sb.append(String.format("%-12s %-24s %-10s%n",
                        s.getStudentId(), s.getName(), s.getRoomNumber())));
        if (sb.toString().lines().count() <= 2) {
            sb.append("  (no active mess subscribers)");
        }
        ta.setText(sb.toString());
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



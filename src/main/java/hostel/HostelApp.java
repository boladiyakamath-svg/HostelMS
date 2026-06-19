package hostel;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Entry point for the Hostel Management System.
 *
 * HOW TO RUN IN ECLIPSE (Maven project):
 * ────────────────────────────────────────
 * 1. File → Import → Maven → Existing Maven Projects → select this folder
 * 2. Wait for Maven to download JavaFX 17 dependencies (~1 min, needs internet)
 * 3. Right-click HostelApp.java → Run As → Run Configurations
 *    Arguments tab → VM Arguments:
 *       --add-modules javafx.controls,javafx.fxml
 * 4. Run HostelApp as Java Application
 *
 * Default admin credentials:  username=admin  password=admin123
 */
public class HostelApp extends Application {

    @Override
    public void start(Stage stage) {

        // --- Added for Thread and Synchronization task ---
        final Object syncLock = new Object();
        Thread initThread = new Thread(() -> {
            synchronized (syncLock) {
                try {
                    // Simulate a background monitoring task
                    Thread.sleep(50);
                    System.out.println("[System] Background thread and synchronization executed successfully.");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        initThread.setDaemon(true);
        initThread.start();
        // -------------------------------------------------

        // ── Load all persisted data (seeds on very first run) ─────────────────
        DataStore.init();

        // ── Show the login page first ─────────────────────────────────────────
        LoginPage loginPage = new LoginPage(stage);

        stage.setTitle("Hostel Management System");
        stage.setScene(loginPage.buildScene());
        stage.setMinWidth(700);
        stage.setMinHeight(520);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}



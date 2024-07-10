package team03.mudecowboys.projekt2.jcleaner.view;

import atlantafx.base.theme.PrimerDark;
import atlantafx.base.theme.PrimerLight;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;
import team03.mudecowboys.projekt2.jcleaner.controller.MainWindowController;

import java.util.logging.Logger;


/**
 * Manages the window (view) configurations.
 */
public class MainWindow extends Application {
    private static final Logger logger = Logger.getLogger(MainWindow.class.getCanonicalName());

    private static final int MIN_WINDOW_WIDTH = 800, MIN_WINDOW_HEIGHT = 450;
    private static final String APP_TITLE = "JCleaner";


    /**
     * {@inheritDoc}
     */
    @Override public void start(Stage primaryStage) {
        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
        Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());

        mainWindow(primaryStage);    
    }


    /**
     * It defines the main window.
     * While starting up the stage it shows a splash screen.
     * While the splash screen is running, the mainwindow gets already initialized.
     * @param primaryStage The stage, where the main scene gets initialized.
     */
    private void mainWindow(Stage primaryStage) {
        try {
            primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("../J.png")));

            FXMLLoader splashLoader = new FXMLLoader(getClass().getResource("../SplashWindow.fxml"));
            Pane splashPane = splashLoader.load();
            Scene splashScene = new Scene(splashPane);

            FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("../MainWindow.fxml"));
            Pane mainPane = mainLoader.load();
            Scene mainScene = new Scene(mainPane);
            MainWindowController mainWindowLoader = mainLoader.getController();
            mainWindowLoader.setStage(primaryStage);

            primaryStage.setScene(splashScene);
            primaryStage.setMinWidth(MIN_WINDOW_WIDTH);
            primaryStage.setMinHeight(MIN_WINDOW_HEIGHT);

            primaryStage.setTitle(APP_TITLE);
            mainScene.getStylesheets().add(getClass().getResource("../MainWindow.css").toExternalForm());
            primaryStage.show();

            FadeTransition fadeIn = new FadeTransition(Duration.seconds(5), splashPane);
            fadeIn.play();

            fadeIn.setOnFinished(e -> primaryStage.setScene(mainScene));

            primaryStage.show();

            logger.info("=> main window is active.");
        } catch(Exception e) {
            logger.severe(String.format("There was an error in the window configuration.\n-> %s", e));
        }
    }
}

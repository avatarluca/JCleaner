package team03.mudecowboys.projekt2.jcleaner;

import team03.mudecowboys.projekt2.jcleaner.view.MainWindow;
import java.util.logging.Logger;
import javafx.application.Application;
import java.util.logging.Level;


/**
 * Main app class.
 * => Starts the application by calling the view {@link team03.mudecowboys.projekt2.jcleaner.view.MainWindow}.
 */
public class App {
    private static final Logger logger = Logger.getLogger(App.class.getCanonicalName());

    public static void main(String[] args) {   
        LogConfiguration.setLogLevel(App.class, Level.INFO); // load static block with jvm classloader.
        logger.info("Hello! Starting application...");

        Application.launch(MainWindow.class, args);

        logger.info("... terminating application. Bye!");
    }
}
package team03.mudecowboys.projekt2.jcleaner.controller;

import javafx.fxml.FXML;
import javafx.scene.text.Text;
import team03.mudecowboys.projekt2.jcleaner.model.boot.Boot;

import java.util.logging.Logger;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.net.URISyntaxException;


public class SplashWindowController {
    private Media media = null;
    private MediaPlayer player = null;

    @FXML private Text splashAscii;
    @FXML private Text splashWelcome;
    private static final Logger logger = Logger.getLogger(SplashWindowController.class.getCanonicalName());

    @FXML public void initialize() {
        logger.info("Starting to initialize Splash window.");
        
        try {
            media = new Media(getClass().getResource("/music/The_good_the_bad_and_the_ugly_short.mp3").toURI().toString());
            
            player = new MediaPlayer(media); 
            player.play();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } 

        Boot boot = new Boot();
        splashAscii.textProperty().bind(boot.getFrameProperty());
        splashWelcome.textProperty().bind(boot.getTitleProperty());
        boot.start();

        logger.info("initialized SplashWindow.");
    }
}

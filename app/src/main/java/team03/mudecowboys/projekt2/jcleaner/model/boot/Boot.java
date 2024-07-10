package team03.mudecowboys.projekt2.jcleaner.model.boot;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import team03.mudecowboys.projekt2.jcleaner.model.db.SettingsPropertiesHandler;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Original Müde Cowboys boot class to animate a boot animation in the console.
 * A distinction is made between start and end animation.
 * <br /><br />
 * An asciiart animation is output, which simulates a console cleanup by the character combination "\033[H\033[2J" (= so many enters are output until the previous text is no longer visible).
 * Between frames, "Thread.sleep()" pauses the current thread to simulate a flipbook.
 */
public class Boot extends Thread {
    private final StringProperty currentFrame;
    private static final Logger logger = Logger.getLogger(SettingsPropertiesHandler.class.getCanonicalName());
    private final StringProperty title;
    private final String startFrames[] = {
                """
                            Time to wake up...
            
                                                         zz                     
                                _____               zzzzz 
                            ___/_____\\__/      zzzz 
                               | - - |          
                               \\__o__/      
                              __#| |#___
                         _ _ /  #####   \\
                        / 7 \\|   ###_ |\\ \\
                        || \\_      |_P\\__/
                        ||   |        |
                             \\####O###/
                """,
                """
                            Time to wake up...
            
                                                         zzZZZZZ                
                                _____               zz
                            ___/_____\\__/      z
                               | - - |          
                               \\__o__/      
                              __#| |#___
                         _ _ /  #####   \\
                        / 7 \\|   ###_ |\\ \\
                        || \\_      |_P\\__/
                        ||   |        |
                             \\####O###/
                """,
                """
                            Time to wake up...
            
                                                         
                                _____               zzZ                         
                            ___/_____\\__/      ZZZzz
                               | - - |          
                               \\__o__/      
                              __#| |#___
                         _ _ /  #####   \\
                        / 7 \\|   ###_ |\\ \\
                        || \\_      |_P\\__/
                        ||   |        |
                             \\####O###/
                """,
                """
                            Time to wake up...   
            
                                                         zzZZZZZZ               
                                _____               zzZZZ
                            ___/_____\\__/      zZZZZ
                               | - - |          
                               \\__-__/      
                              __#| |#___
                         _ _ /  #####   \\
                        / 7 \\|   ###_ |\\ \\
                        || \\_      |_P\\__/
                        ||   |        |
                             \\####O###/
                """,
                """
                            Time to wake up...
            
                                                         zzZzzZ                 
                                _____               zzZZZ
                            ___/_____\\__/      zzz
                               | - - |          
                               \\__-__/      
                              __#| |#___
                         _ _ /  #####   \\
                        / 7 \\|   ###_ |\\ \\
                        || \\_      |_P\\__/
                        ||   |        |
                             \\####O###/
                """,
                """
                            Time to wake up...
            
                                                         zzZZzzZ                
                                _____               zzzzz 
                            ___/_____\\__/      zzZZ
                               | - - |          
                               \\__-__/      
                              __#| |#___
                         _ _ /  #####   \\
                        / 7 \\|   ###_ |\\ \\
                        || \\_      |_P\\__/
                        ||   |        |
                             \\####O###/
                """,
                """
                            Time to wake up...
            
                                                         zzZZ                   
                                _____               zzz
                            ___/_____\\__/      z
                               | - - |          
                               \\__o__/      
                              __#| |#___
                         _ _ /  #####   \\
                        / 7 \\|   ###_ |\\ \\
                        || \\_      |_P\\__/
                        ||   |        |
                             \\####O###/
                """,
                """
                            Time to wake up...
            
                                                         zzZZ                   
                                _____               zzz
                            ___/_____\\__/      
                               | - - |          
                               \\__o__/      
                              __#| |#___
                         _ _ /  #####   \\
                        / 7 \\|   ###_ |\\ \\
                        || \\_      |_P\\__/
                        ||   |        |
                             \\####O###/
                """,
                """
                            Time to wake up..
            
                                                         zzZZ                   
                                _____               zz
                            ___/_____\\__/      
                               | - - |          
                               \\__o__/      
                              __#| |#___
                         _ _ /  #####   \\
                        / 7 \\|   ###_ |\\ \\
                        || \\_      |_P\\__/
                        ||   |        |
                             \\####O###/
                """,
                """
                            Time to wake up..
            
                                                                                
                                _____               
                            ___/_____\\__/      
                               | o o |          
                               \\__-__/      
                              __#| |#___
                         _ _ /  #####   \\
                        / 7 \\|   ###_ |\\ \\
                        || \\_      |_P\\__/
                        ||   |        |
                             \\####O###/
                """,
                """
                            Time to wake up..
            
                                                         
                                _____                                           
                            ___/_____\\__/      
                               | o o |          
                               \\__-__/      
                              __#| |#___
                         _ _ /  #####   \\
                        / 7 \\|   ###_ |\\ \\
                        || \\_      |_P\\__/
                        ||   |        |
                             \\####O###/
                """,
                """
                            Time to wake up..
            
                                                                                
                                _____               
                            ___/_____\\__/      
                               | o - |          
                               \\__-__/      
                              __#| |#___
                         _ _ /  #####   \\
                        / 7 \\|   ###_ |\\ \\
                        || \\_      |_P\\__/
                        ||   |        |
                             \\####O###/
                """,
                """
                            Time to wake up..
            
                                                                                
                                _____               
                            ___/_____\\__/      
                               | o - |          
                               \\__-__/      
                              __#| |#___
                         _ _ /  #####   \\
                        / 7 \\|   ###_ |\\ \\
                        || \\_      |_P\\__/
                        ||   |        |
                             \\####O###/
                """,
                """
                            Time to wake up..
            
                                                                                
                                _____               
                            ___/_____\\__/      
                               | o o |          
                               \\__-__/      
                              __#| |#___
                         _ _ /  #####   \\
                        / 7 \\|   ###_ |\\ \\
                        || \\_      |_P\\__/
                        ||   |        |
                             \\####O###/
                """,
                """
                            Time to wake up..
            
                                                                                
                                _____               
                            ___/_____\\__/      
                               | o o |          
                               \\__-__/      
                              __#| |#___
                         _ _ /  #####   \\
                        / 7 \\|   ###_ |\\ \\
                        || \\_      |_P\\__/
                        ||   |        |
                             \\####O###/
                """
    };

    private final String logo = """
        __  __           _         _____              _                    
       |   /  |         | |       / ____|            | |                    
       |    / |_   _  __| | ___  | |     _____      _| |__   ___  _   _ ___  
       | | /| | | | |/ _` |/ _   | |    / _     /  / / '_   / _  | | | / __| 
       | |  | | |_| | (_| |  __/ | |___| (_)   V  V /| |_) | (_) | |_|  __  
       |_|  |_| __,_| __,_| ___|   _____ ___/  _/ _/ |_.__/  ___/  __, |___/
                                                                   __/ |    
                                                                  |___/ 
       Faster than your shadow.""";


    public Boot() {
        this.currentFrame = new SimpleStringProperty();
        this.title = new SimpleStringProperty();
    }


    public StringProperty getFrameProperty() {
        return this.currentFrame;
    }

    public StringProperty getTitleProperty() {
        return this.title;
    }


    /**
     * Main method that defines the course of the boot process.
     */
    @Override public void run() {
        printStartAnimation();

        title.set(logo);

        pause(2);
    }

    /**
     * Pauses the current thread.
     * @param seconds Time span of the pause in seconds.
     */
    private void pause(double seconds) {
        try {
            Thread.sleep((int)(seconds * 1000));
        } catch(Exception e) {
            logger.log(Level.WARNING, "Thread could not be paused!", e);
        }
    }

    /**
     * Manages the startup animation.
     */
    private void printStartAnimation() {
        final double timePerFrame = 0.25;

        for(String frame : startFrames) {
            currentFrame.set(frame);

            pause(timePerFrame);
        }

        currentFrame.set("");
    }
}
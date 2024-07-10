package team03.mudecowboys.projekt2.jcleaner;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;


/**
 * Configuration class for Java Logging
 * Reads configuration from property file, specified in the following order:
 * - System property "java.util.logging.config.file"
 * - file "log.properties" in working directory
 * - file "log.properties" in project resources
 */
public class LogConfiguration {
    private static final Logger logger = Logger.getLogger(LogConfiguration.class.getCanonicalName());


    static {
        Locale.setDefault(Locale.ROOT); 

        String logConfigFile = System.getProperty("java.util.logging.config.file", "log.properties");
        Path logConfigPath = Path.of(logConfigFile);
        try {
            InputStream configFileStream;
            if (Files.isReadable(logConfigPath)) {
                configFileStream = Files.newInputStream(logConfigPath);
            } else {
                logConfigFile="resources:/log.properties";
                configFileStream = ClassLoader.getSystemClassLoader().getResourceAsStream("log.properties");
            }
            if (configFileStream != null) {
                LogManager.getLogManager().readConfiguration(configFileStream);
                logger.log(Level.FINE, "Log configuration read from {0}", logConfigFile);
            } else {
                logger.warning("No log configuration found. Using system default settings.");
            }
        } catch (IOException e) {
            logger.log(Level.WARNING, "Error loading log configuration", e);
        }
    }

    
    public static String getProperty(String name) {
        return LogManager.getLogManager().getProperty(name);
     }
 
     public static void setLogLevel(Class<?> clazz, Level level) {
         Logger.getLogger(clazz.getCanonicalName()).setLevel(level);
     }
 
     public static Level getLogLevel(Class<?> clazz) {
         return Logger.getLogger(clazz.getCanonicalName()).getLevel();
     }
}

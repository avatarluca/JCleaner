package team03.mudecowboys.projekt2.jcleaner.model.db;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import team03.mudecowboys.projekt2.jcleaner.model.db.util.SettingsDataSpecification;


/**
 * The manager of the settings properties.
 * It updates and reads the property file.
 */
public class SettingsPropertiesHandler implements SettingsDataSpecification {
    private static final Logger logger = Logger.getLogger(SettingsPropertiesHandler.class.getCanonicalName());
    private static final String SETTINGS_DB = "db/settings.properties",
            SETTINGS_DEFAULT_DB = "db/settingsDefault.properties",
            TEMP_FILE_NAME = "settingsTemp",
            TEMP_FILE_SUFFIX = ".properties";

    private final File settingsFile, settingsDefaultFile;
    private File settingsTempFile;


    public SettingsPropertiesHandler() throws IllegalStateException{
        this.settingsFile = new File(SETTINGS_DB);
        this.settingsDefaultFile = new File(SETTINGS_DEFAULT_DB);

        this.settingsTempFile = createTempFile();

        if(!settingsDefaultFile.exists()) {
            logger.log(Level.SEVERE, "Default settingsfile isn't available.");
            throw new IllegalStateException("Default settingsfile isn't available.");
        }

        if(!settingsFile.exists()) {
            logger.fine("No saved settingsfile available. Creating new one.");
            transfer(settingsDefaultFile, settingsFile);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override public void update(String property, String data){
        logger.info(String.format("Updating setting property %s with %s.", property, data));

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(settingsTempFile, false))) {
            Properties properties = readProperties(settingsFile);

            properties.setProperty(property, data);

            properties.store(writer, "JCleaner Settings");
            transfer(settingsTempFile, settingsFile);
            settingsTempFile.delete();
        } catch (IOException e) {
            logger.severe(String.format("Couldn't write and manipulate settingsfiles: %s.", e));
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override public String getValueByKey(String id) {
        logger.finer(String.format("Get value by key: %s.", id));

        String value = "";

        Properties properties = readProperties(settingsFile);

        value = properties.getProperty(id);

        logger.finer(String.format("Got Value: %s for key: %s",value,id));
        return value;
    }



    /**
     * Reads all properties from a property file.
     * @param file The file, from which the properties gets read.
     * @return A list of properties (empty when there aren't properties in the file or it couldn't read).
     */
    final private Properties readProperties(File file) {
        Properties properties = new Properties();

        try(BufferedReader reader = new BufferedReader(new FileReader(file))) {
            properties.load(reader);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Couldn't fetch setting properties.", e);
        }

        return properties;
    }

    /**
     * Transfers (copies) a file to another file.
     * @param src The src file.
     * @param dest The dest file.
     */
    private void transfer(File src, File dest) {
        try {
            if(dest.exists()) {
                dest.delete();
            }
            Files.copy(src.toPath(), dest.toPath());
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Couldn't transfer file.", e);
        }
    }

    /**
     * Creates a (empty) temp file.
     * @return A file object referencing to the newly created file.
     */
    final private File createTempFile() throws IllegalStateException{
        File tempFile;
        try {
            logger.fine("Creating tempfile for settings");
            tempFile = Files.createTempFile(TEMP_FILE_NAME, TEMP_FILE_SUFFIX).toFile();
            tempFile.createNewFile();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Couldn't create temp file.", e);
            throw new IllegalStateException("Could not create temp settings file");
        }

        return tempFile;
    }
}

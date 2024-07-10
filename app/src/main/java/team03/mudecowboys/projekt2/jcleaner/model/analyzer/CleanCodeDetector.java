package team03.mudecowboys.projekt2.jcleaner.model.analyzer;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import team03.mudecowboys.projekt2.jcleaner.model.util.CodePositionWarning;


/**
 * {@inheritDoc}
 *
 * Used to inspect the user code for clean code.
 * It checks if the method, field an class names are defined as the given user regex.
 */
public class CleanCodeDetector extends Detector {

    private static final Logger logger = Logger.getLogger(CleanCodeDetector.class.getCanonicalName());
    private final ListProperty<CodePositionWarning> regexMethodWarnings, regexClassWarnings, regexFieldWarnings;
    private final StringProperty methodRegex, classRegex, fieldRegex;
    private final BooleanProperty namingCheck, isClassNamingCheckEnabled, isMethodNamingCheckEnabled, isFieldNamingCheckEnabled;


    public CleanCodeDetector() {
        this.regexMethodWarnings = new SimpleListProperty<>(FXCollections.observableArrayList(new ArrayList<>()));
        this.regexClassWarnings = new SimpleListProperty<>(FXCollections.observableArrayList(new ArrayList<>()));
        this.regexFieldWarnings = new SimpleListProperty<>(FXCollections.observableArrayList(new ArrayList<>()));

        this.methodRegex = new SimpleStringProperty("^[a-z][A-Za-z0-9_]{0,20}$");
        this.classRegex = new SimpleStringProperty("^[A-Z][A-Za-z0-9_]{0,20}$");
        this.fieldRegex = new SimpleStringProperty("^[a-z_][A-Za-z0-9_]{0,10}$");

        this.namingCheck = new SimpleBooleanProperty();
        this.isClassNamingCheckEnabled = new SimpleBooleanProperty();
        this.isMethodNamingCheckEnabled = new SimpleBooleanProperty();
        this.isFieldNamingCheckEnabled = new SimpleBooleanProperty();
    }


    public BooleanProperty getNamingCheckProperty() {
        return namingCheck;
    }

    public BooleanProperty getNamingClassCheckProperty() {
        return isClassNamingCheckEnabled;
    }

    public BooleanProperty getNamingMethodCheckProperty() {
        return isMethodNamingCheckEnabled;
    }

    public BooleanProperty getNamingFieldCheckProperty() {
        return isFieldNamingCheckEnabled;
    }

    public StringProperty getClassRegex() {
        return classRegex;
    }

    public StringProperty getMethodRegex() {
        return methodRegex;
    }

    public StringProperty getFieldProperty() {
        return fieldRegex;
    }


    /**
     * {@inheritDoc}
     */
    @Override public void run() throws Exception {
        logger.info("> Starting clean code detection...");

        clear();

        if(namingCheck.get()) analyzeMethodRegex();

        logger.info("Finished clean code detection.");
    }


    /**
     * {@inheritDoc}
     */
    @Override public HashMap<String, HashMap<String, ListProperty>> getResults(){
        logger.info("Preparing results.");

        HashMap<String, HashMap<String, ListProperty>> results = new HashMap<>();
        HashMap<String, ListProperty> propertyHashMap = new HashMap<>();
        propertyHashMap.put("regexMethodWarnings", regexMethodWarnings);
        propertyHashMap.put("regexClassWarnings", regexClassWarnings);
        propertyHashMap.put("regexFieldWarnings", regexFieldWarnings);
        results.put("CleanCode", propertyHashMap);

        logger.info("Results have been prepared.");

        return results;
    }


    /**
     * Checks the method, field and class names with the specified regex.
     */
    private void analyzeMethodRegex() {
        logger.info("Starting to analyze the method's.");

        for(Class<?> clazz : classes ) {
            logger.fine("Current checked class: " + clazz.getSimpleName());

            Method[] methods = clazz.getDeclaredMethods();
            Field[] fields = clazz.getDeclaredFields();

            if(isMethodNamingCheckEnabled.get()) {
                logger.finer("Method naming check is enabled.");

                for(Method method : methods) {
                    logger.finer("Current checked method: " + method.getName());

                    if(!(method.getName().matches(methodRegex.get()))) regexMethodWarnings.add(new CodePositionWarning(method.getDeclaringClass(), method));
                }
            }

            if(isFieldNamingCheckEnabled.get()) {
                logger.finest("Field naming Check is enabled.");

                for(Field field : fields) {
                    logger.finest("Current checked field: " + field.getName());

                    if(!(field.getName().matches(fieldRegex.get()))) regexFieldWarnings.add(new CodePositionWarning(field.getDeclaringClass(), field));
                }
            }

            if(isClassNamingCheckEnabled.get() &&
               !(clazz.getSimpleName().matches(classRegex.get()))) regexClassWarnings.add(new CodePositionWarning(clazz, null));
        }
    }

    /**
     * Cleans everything before starting a new test, so that it can update it on the new test state.
     */
    private void clear() {
        regexMethodWarnings.clear();
        regexClassWarnings.clear();
        regexFieldWarnings.clear();
    }
}


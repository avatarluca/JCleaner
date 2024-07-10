package team03.mudecowboys.projekt2.jcleaner.model;

import javafx.beans.property.ListProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import team03.mudecowboys.projekt2.jcleaner.model.analyzer.CleanCodeDetector;
import team03.mudecowboys.projekt2.jcleaner.model.analyzer.CohesionDetector;
import team03.mudecowboys.projekt2.jcleaner.model.analyzer.CouplingDetector;
import team03.mudecowboys.projekt2.jcleaner.model.db.SettingsPropertiesHandler;
import team03.mudecowboys.projekt2.jcleaner.model.io.ProjectLoader;
import team03.mudecowboys.projekt2.jcleaner.model.util.ExecutableLengthCheckTypes;
import team03.mudecowboys.projekt2.jcleaner.model.util.ParameterCheckFeedbackTypes;
import team03.mudecowboys.projekt2.jcleaner.model.util.CodePositionWarning;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import javafx.beans.property.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DetectorTest {

    private String testClassesDir = Paths.get(
            "src", "test", "java", "team03", "mudecowboys", "projekt2", "jcleaner", "model", "classes"
    ).toFile().getAbsolutePath();

    private CouplingDetector couplingDetector = new CouplingDetector();
    private CohesionDetector cohesionDetector = new CohesionDetector();
    private CleanCodeDetector cleanCodeDetector = new CleanCodeDetector();
    private SettingsPropertiesHandler settingsHandler;


    private HashMap<String, ListProperty> couplingDetectorResult;
    private HashMap<String, ListProperty> cohesionDetectorResult;
    private HashMap<String, ListProperty> cleanCodeDetectorResult;

    private Comparator<CodePositionWarning> warningComparator = Comparator.comparing(w -> w.classObj().toString());

    @Test
    public void helloWorldTest() throws IOException {
        ProjectLoader projectLoader = new ProjectLoader(Paths.get(testClassesDir, "hello_world"));
        String projectAsString = "package team03.mudecowboys.projekt2.jcleaner.model;\n\npublic class HelloWorld {\n    public static void main(String[] args) {\n        System.out.println(\"Hello, World!\");\n    }\n}";
        assertEquals(projectLoader.getProjectAsString(), projectAsString);
        // How to test getProjectAsClassSet
    }

    @Test
    public void checkFieldDependenceWarnings() {
        List<CodePositionWarning> results = couplingDetectorResult.get("fieldDependenceWarnings");
        results.sort(warningComparator);

        assertEquals(results.size(), 2);
        assertEquals(results.get(0).classObj().toString(), "class TestJava2$TestReal");
        assertEquals(results.get(1).classObj().toString(), "class TestJava3");
    }

    @Test
    public void checkAccessModificatorWarnings() {
        List<CodePositionWarning> results = couplingDetectorResult.get("AccessModificatorWarnings");
        results.sort(warningComparator);

        assertEquals(results.size(), 2);
        assertEquals(results.get(0).classObj().toString(), "class TestJava1");
        assertEquals(results.get(1).classObj().toString(), "class TestJava2$TestReal");
    }

    @Test
    public void checkFieldBidirectionalWarnings() {
        List<CodePositionWarning> results = couplingDetectorResult.get("FieldBidirectionalWarnings");
        results.sort(warningComparator);

        assertEquals(results.size(), 2);
        assertEquals(results.get(0).classObj().toString(), "class TestJava2");
        assertEquals(results.get(1).classObj().toString(), "class TestJava3");
    }

    @Test
    public void checkCodeInstandVariableWarnings() {
        List<CodePositionWarning> results = cohesionDetectorResult.get("codeInstandVariableWarnings");

        assertEquals(results.size(), 0);
    }

    @Test
    public void checkCodeNestedKeywordComplexityWarnings() {
        List<CodePositionWarning> results = cohesionDetectorResult.get("codeNestedKeywordComplexityWarnings");
        results.sort(warningComparator);

        assertEquals(results.size(), 1);
        assertEquals(results.get(0).classObj().toString(), "class TestJava1");
    }

    @Test
    public void checkCodeExcecutableWarnings() {
        List<CodePositionWarning> results = cohesionDetectorResult.get("codeExcecutableWarnings");

        assertEquals(results.size(), 0);
    }

    @Test
    public void checkCodeParameterLengthWarnings() {
        List<CodePositionWarning> results = cohesionDetectorResult.get("codeParameterLengthWarnings");

        assertEquals(results.size(), 1);
        assertEquals(results.get(0).classObj().toString(), "class TestJava2");
    }



    @Test
    public void checkRegexClassWarnings() {
        List<CodePositionWarning> results = cleanCodeDetectorResult.get("regexClassWarnings");

        assertEquals(results.size(), 0);
    }

    @Test
    public void checkRegexFieldWarnings() {
        List<CodePositionWarning> results = cleanCodeDetectorResult.get("regexFieldWarnings");

        assertEquals(results.size(), 1);
        assertEquals(results.get(0).classObj().toString(), "class TestJava2$TestReal");
    }

    @Test
    public void checkRegexMethodWarnings() {
        List<CodePositionWarning> results = cleanCodeDetectorResult.get("regexMethodWarnings");

        assertEquals(results.size(), 1);
        assertEquals(results.get(0).classObj().toString(), "class TestJava2$TestReal");
    }

    /**
     * Runs all the detectors over the test project "user_code"
     * @throws Exception on errors when loading the project
     */
    @BeforeEach
    public void user_codeRunDetectors() throws Exception {
        ProjectLoader projectLoader = new ProjectLoader(Paths.get(testClassesDir, "user_code"));
        Set<Class<?>> projectAsClass = projectLoader.getProjectAsClassSet();
        String projectAsString = projectLoader.getProjectAsString();

        initSettings();

        couplingDetector.setUpInputData(projectAsClass, projectAsString);
        cohesionDetector.setUpInputData(projectAsClass, projectAsString);
        cleanCodeDetector.setUpInputData(projectAsClass, projectAsString);

        couplingDetector.run();
        cohesionDetector.run();
        cleanCodeDetector.run();

        couplingDetectorResult = couplingDetector.getResults().get("Coupling");
        cohesionDetectorResult = cohesionDetector.getResults().get("Cohesion");
        cleanCodeDetectorResult = cleanCodeDetector.getResults().get("CleanCode");

    }

    /**
     * Resets the detector settings to the defaults defined in settingsDefault.properties
     * @throws IOException
     */
    private void resetSettings() throws IOException {
        Path settings = new File("db/settings.properties").toPath();
        Path defaultSettings = new File("db/settingsDefault.properties").toPath();
        Files.copy(defaultSettings, settings, REPLACE_EXISTING);
    }

    /**
     * Loads the settings using SettingsHandler
     * @throws IOException
     */
    private void initSettings() throws IOException {
        resetSettings();
        settingsHandler = new SettingsPropertiesHandler();

        couplingDetector.getBidirProperty().set(Boolean.parseBoolean(settingsHandler.getValueByKey("couplingInstantiationBiDirectional")));
        couplingDetector.getOnePlaceProperty().set(Boolean.parseBoolean(settingsHandler.getValueByKey("couplingInstantiationOnePlace")));
        couplingDetector.getMethodAccessProperty().set(Boolean.parseBoolean(settingsHandler.getValueByKey("couplingAccessMethod")));
        couplingDetector.getFieldAccessProperty().set(Boolean.parseBoolean(settingsHandler.getValueByKey("couplingAccessField")));
        couplingDetector.getClassAccessProperty().set(Boolean.parseBoolean(settingsHandler.getValueByKey("couplingAccessInnerClass")));

        cohesionDetector.getParameterCheckProperty().set(Boolean.parseBoolean(settingsHandler.getValueByKey("enableParameterCheck")));
        cohesionDetector.getFieldTypesCheckProperty().set(Boolean.parseBoolean(settingsHandler.getValueByKey("enableFieldTypeCheck")));
        cohesionDetector.getComplexityCheckProperty().set(Boolean.parseBoolean(settingsHandler.getValueByKey("enableComplexityCheck")));
        cohesionDetector.getExecutableLengthProperty().set(Boolean.parseBoolean(settingsHandler.getValueByKey("enableMethodLengthCheck")));
        cohesionDetector.getPrimitiveFieldCheck().set(Boolean.parseBoolean(settingsHandler.getValueByKey("cohesionFieldPrimitiveCheck")));

        cleanCodeDetector.getNamingCheckProperty().set(Boolean.parseBoolean(settingsHandler.getValueByKey("enableNamingCheck")));
        cleanCodeDetector.getNamingClassCheckProperty().set(Boolean.parseBoolean(settingsHandler.getValueByKey("cleanCodeNamingEnableClass")));
        cleanCodeDetector.getNamingMethodCheckProperty().set(Boolean.parseBoolean(settingsHandler.getValueByKey("cleanCodeNamingEnableMethod")));
        cleanCodeDetector.getNamingFieldCheckProperty().set(Boolean.parseBoolean(settingsHandler.getValueByKey("cleanCodeNamingEnableField")));
        cohesionDetector.getMaximalParameterProperty().set(settingsHandler.getValueByKey("cohesionParameterMaxAmount"));
        cohesionDetector.getMaxFieldTypesProperty().set(settingsHandler.getValueByKey("cohesionFieldMaxTypes"));
        cohesionDetector.getMaximalAmountKeywordsProperty().set(settingsHandler.getValueByKey("cohesionComplexityMaxAmount"));
        cohesionDetector.getCheckedKeywordsProperty().set(settingsHandler.getValueByKey("cohesionNestedKeywords"));
        cohesionDetector.getMaxMethodLength().set(settingsHandler.getValueByKey("cohesionMethodLengthMethodMax"));
        cohesionDetector.getMaxConstructorLength().set(settingsHandler.getValueByKey("cohesionMethodLengthConstructorMax"));
        cleanCodeDetector.getClassRegex().set(settingsHandler.getValueByKey("cleanCodeNamingClassRegex"));
        cleanCodeDetector.getMethodRegex().set(settingsHandler.getValueByKey("cleanCodeNamingMethodRegex"));
        cleanCodeDetector.getFieldProperty().set(settingsHandler.getValueByKey("cleanCodeNamingFieldRegex"));
        cohesionDetector.getParameterLengthFeedbackTypesProperty().set(ParameterCheckFeedbackTypes.valueOf(settingsHandler.getValueByKey("cohesionParameterFeedbackType")));
        cohesionDetector.getMethodLengthCheckTypeProperty().set(ExecutableLengthCheckTypes.valueOf(settingsHandler.getValueByKey("cohesionMethodLengthCheckType")));

    }


}

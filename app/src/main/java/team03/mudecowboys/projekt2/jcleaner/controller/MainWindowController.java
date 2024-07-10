package team03.mudecowboys.projekt2.jcleaner.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import team03.mudecowboys.projekt2.jcleaner.model.analyzer.CleanCodeDetector;
import team03.mudecowboys.projekt2.jcleaner.model.analyzer.CohesionDetector;
import team03.mudecowboys.projekt2.jcleaner.model.analyzer.CouplingDetector;
import team03.mudecowboys.projekt2.jcleaner.model.db.SettingsPropertiesHandler;
import team03.mudecowboys.projekt2.jcleaner.model.io.ProjectLoader;
import team03.mudecowboys.projekt2.jcleaner.model.util.CodePositionWarning;
import team03.mudecowboys.projekt2.jcleaner.model.util.ExecutableLengthCheckTypes;
import team03.mudecowboys.projekt2.jcleaner.model.util.ParameterCheckFeedbackTypes;


public class MainWindowController {
    private static final Logger logger = Logger.getLogger(MainWindowController.class.getCanonicalName());

    private SettingsPropertiesHandler settingsHandler;

    private Stage stage;

    private String projectAsString;
    private Set<Class<?>> projectAsClass;

    private CouplingDetector couplingDetector;
    private CohesionDetector cohesionDetector;
    private CleanCodeDetector cleanCodeDetector;

    @FXML
    private Label errorMessage;

    // Coupling values
    @FXML
    private CheckBox couplingInstantiationBiDirectional;
    @FXML
    private CheckBox couplingInstantiationOnePlace;
    @FXML
    private CheckBox couplingAccessMethod;
    @FXML
    private CheckBox couplingAccessField;
    @FXML
    private CheckBox couplingAccessInnerClass;

    // Cohesion values
    @FXML
    private CheckBox enableParameterCheck;
    @FXML
    private TextField cohesionParameterMaxAmount;
    @FXML
    private ChoiceBox cohesionParameterFeedbackType;
    @FXML
    private CheckBox enableFieldTypeCheck;
    @FXML
    private TextField cohesionFieldMaxTypes;
    @FXML
    private CheckBox cohesionFieldPrimitiveCheck;
    @FXML
    private CheckBox enableComplexityCheck;
    @FXML
    private TextField cohesionComplexityMaxAmount;
    @FXML
    private TextArea cohesionNestedKeywords;
    @FXML
    private CheckBox enableMethodLengthCheck;
    @FXML
    private ChoiceBox cohesionMethodLengthCheckType;
    @FXML
    private TextField cohesionMethodLengthMethodMax;
    @FXML
    private TextField cohesionMethodLengthConstructorMax;

    // Cleancode values
    @FXML
    private CheckBox enableNamingCheck;
    @FXML
    private CheckBox cleanCodeNamingEnableClass;
    @FXML
    private TextField cleanCodeNamingClassRegex;
    @FXML
    private CheckBox cleanCodeNamingEnableMethod;
    @FXML
    private TextField cleanCodeNamingMethodRegex;
    @FXML
    private CheckBox cleanCodeNamingEnableField;
    @FXML
    private TextField cleanCodeNamingFieldRegex;


    /**
     * Manages the initialization of the detectors and linking model to view components.
     */
    @FXML
    public void initialize() {
        settingsHandler = new SettingsPropertiesHandler();

        initChoiceBoxes();
        initDetectors();
        initSettings();
        linkSettingComponentsToModel();
    }

    /**
     * Manages the open folder process and changes {@link #projectAsClass} and {@link #projectAsString} according to the given path.
     * It also calls {@link #detect()} to start the analysis.
     */
    @FXML
    public void openFolder() {
        logger.info("Starting to open folder");
        resetMessageToUI();
        resetInputData();

        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("JCleaner Directory Picker");

        File selectedDirectory = chooser.showDialog(stage);

        if (selectedDirectory != null) {
            try {
                ProjectLoader loader = new ProjectLoader(selectedDirectory.toPath());

                projectAsString = loader.getProjectAsString();
                projectAsClass = loader.getProjectAsClassSet();

                detect();
            } catch (IllegalArgumentException | IOException | UnsupportedClassVersionError e) {
                String errorMessage = "There was an error loading the class(es).";

                logger.severe(String.format("%s\n-> %s", errorMessage, e.getMessage()));

                printMessageToUI(errorMessage);
            }
        }
    }


    public void setStage(Stage stage) {
        this.stage = stage;
    }


    /**
     * Main method to manage the detection (logic) process.
     */
    private void detect() {
        Platform.runLater(() -> {
            logger.info("Starting with analysis.");

            if (projectAsString == null ||
                    projectAsClass == null ||
                    projectAsString.isBlank() ||
                    projectAsClass.isEmpty()) {
                String errorMessage = "Couldn't find any java classes from the given path.";

                logger.warning(errorMessage);

                printMessageToUI(errorMessage);
            } else {
                couplingDetector.setUpInputData(projectAsClass, projectAsString);
                cohesionDetector.setUpInputData(projectAsClass, projectAsString);
                cleanCodeDetector.setUpInputData(projectAsClass, projectAsString);

                try {
                    couplingDetector.run();
                    cohesionDetector.run();
                    cleanCodeDetector.run();

                    openResults();
                } catch (Exception e) {
                    String errorMessage = "Something went wrong when analyzing the classes.";

                    logger.severe(String.format("%s\n-> %s", errorMessage, e.getMessage()));

                    printMessageToUI(errorMessage);
                }
            }

            logger.info("Ending with analysis.");
        });
    }

    /**
     * Opens the result window and configure it with the calculated / detected values from the model.
     *
     * @throws IOException When the resultwindow couldn't open.
     */
    private void openResults() throws IOException {
        try {
            logger.info("Try to open Results");

            Stage stageResults = new Stage();
            stageResults.setTitle("JCleaner Results");
            VBox results = new VBox();
            Pane rootPane = new HBox();
            ScrollPane scrollPane = new ScrollPane(new AnchorPane(results));
            rootPane.getChildren().add(scrollPane);

            stageResults.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("../J.png"))));
            HashMap<String, HashMap<String, ListProperty>> resultsMap = new HashMap<>();
            resultsMap.putAll(couplingDetector.getResults());
            resultsMap.putAll(cohesionDetector.getResults());
            resultsMap.putAll(cleanCodeDetector.getResults());

            for (Map.Entry<String, HashMap<String, ListProperty>> entry : resultsMap.entrySet()) {
                String titel = entry.getKey();
                HashMap<String, ListProperty> subMap = entry.getValue();
                VBox anchorPane = new VBox();
                for (Map.Entry<String, ListProperty> resultEntry : subMap.entrySet()) {
                    String subTitel = resultEntry.getKey();
                    ListProperty<CodePositionWarning> resultProperty = resultEntry.getValue();
                    Text subTitelText = new Text(subTitel);
                    subTitelText.setStyle("-fx-font-size: 20px;");
                    anchorPane.getChildren().add(subTitelText);
                    if (resultProperty.isEmpty()) {
                        HBox hBox = new HBox();
                        hBox.getChildren().add(new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("../checkmark.png")))));
                        hBox.getChildren().add(new Text("No problems found."));
                        hBox.alignmentProperty().setValue(Pos.CENTER_LEFT);
                        anchorPane.getChildren().add(hBox);
                    }
                    for (CodePositionWarning result : resultProperty) {
                        StringBuilder resultOutput = new StringBuilder();
                        resultOutput.append("Class: ");
                        resultOutput.append(result.classObj().getSimpleName());
                        if (result.memberObj() != null) {
                            resultOutput.append(", Position: ");
                            resultOutput.append(result.memberObj());
                        }
                        if (result.informations() != null && result.informations().length > 0) {
                            resultOutput.append(", Information: ");
                            resultOutput.append(Arrays.toString(result.informations()));
                        }
                        anchorPane.getChildren().add(new Text(resultOutput.toString()));
                    }
                    anchorPane.getChildren().add(new Text("\n"));
                }
                TitledPane titledPane = new TitledPane(titel, anchorPane);
                titledPane.setPadding(new Insets(30, 30, 0, 30));
                results.getChildren().add(titledPane);

            }


            Scene scene = new Scene(rootPane);
            results.prefWidthProperty().bind(scene.widthProperty());
            results.prefHeightProperty().bind(scene.heightProperty());
            stageResults.setScene(scene);
            stageResults.setHeight(800);
            stageResults.show();

            logger.info("successfully pictured results.");
        } catch (Exception e) {
            logger.severe(String.format("Couldn't open the results window.\n-> %s", e.getMessage()));
            throw e;
        }
    }

    /**
     * Add values to the choiceboxes in the settings.
     * It also sets a default value.
     */
    private void initChoiceBoxes() {
        logger.fine("Starting to initialize the choiceboxes.");

        cohesionParameterFeedbackType.setValue(ParameterCheckFeedbackTypes.BOTH);
        cohesionMethodLengthCheckType.setValue(ExecutableLengthCheckTypes.LINEBREAK_COUNTER);

        logger.fine("Initialized all the choiceboxes.");
    }

    /**
     * Create new detector object and put them into a field variable.
     */
    private void initDetectors() {
        logger.fine("Starting to initialize Detectors");

        couplingDetector = new CouplingDetector();
        cohesionDetector = new CohesionDetector();
        cleanCodeDetector = new CleanCodeDetector();

        logger.fine("initialized Detectors.");
    }

    /**
     * Init the setting with {@link team03.mudecowboys.projekt2.jcleaner.model.db.SettingsPropertiesHandler} and its properties file (db).
     * Updates the setting view elements and the respective model values.
     */
    private void initSettings() {
        logger.fine("Starting to initialize settings");

        couplingInstantiationBiDirectional.setSelected(Boolean.parseBoolean(settingsHandler.getValueByKey(couplingInstantiationBiDirectional.getId())));
        couplingDetector.getBidirProperty().set(Boolean.parseBoolean(settingsHandler.getValueByKey(couplingInstantiationBiDirectional.getId())));

        couplingInstantiationOnePlace.setSelected(Boolean.parseBoolean(settingsHandler.getValueByKey(couplingInstantiationOnePlace.getId())));
        couplingDetector.getOnePlaceProperty().set(Boolean.parseBoolean(settingsHandler.getValueByKey(couplingInstantiationOnePlace.getId())));

        couplingAccessMethod.setSelected(Boolean.parseBoolean(settingsHandler.getValueByKey(couplingAccessMethod.getId())));
        couplingDetector.getMethodAccessProperty().set(Boolean.parseBoolean(settingsHandler.getValueByKey(couplingAccessMethod.getId())));

        couplingAccessField.setSelected(Boolean.parseBoolean(settingsHandler.getValueByKey(couplingAccessField.getId())));
        couplingDetector.getFieldAccessProperty().set(Boolean.parseBoolean(settingsHandler.getValueByKey(couplingAccessField.getId())));

        couplingAccessInnerClass.setSelected(Boolean.parseBoolean(settingsHandler.getValueByKey(couplingAccessInnerClass.getId())));
        couplingDetector.getClassAccessProperty().set(Boolean.parseBoolean(settingsHandler.getValueByKey(couplingAccessInnerClass.getId())));


        enableParameterCheck.setSelected(Boolean.parseBoolean(settingsHandler.getValueByKey(enableParameterCheck.getId())));
        cohesionDetector.getParameterCheckProperty().set(Boolean.parseBoolean(settingsHandler.getValueByKey(enableParameterCheck.getId())));

        enableFieldTypeCheck.setSelected(Boolean.parseBoolean(settingsHandler.getValueByKey(enableFieldTypeCheck.getId())));
        cohesionDetector.getFieldTypesCheckProperty().set(Boolean.parseBoolean(settingsHandler.getValueByKey(enableFieldTypeCheck.getId())));

        enableComplexityCheck.setSelected(Boolean.parseBoolean(settingsHandler.getValueByKey(enableComplexityCheck.getId())));
        cohesionDetector.getComplexityCheckProperty().set(Boolean.parseBoolean(settingsHandler.getValueByKey(enableComplexityCheck.getId())));

        enableMethodLengthCheck.setSelected(Boolean.parseBoolean(settingsHandler.getValueByKey(enableMethodLengthCheck.getId())));
        cohesionDetector.getExecutableLengthProperty().set(Boolean.parseBoolean(settingsHandler.getValueByKey(enableMethodLengthCheck.getId())));

        cohesionFieldPrimitiveCheck.setSelected(Boolean.parseBoolean(settingsHandler.getValueByKey(cohesionFieldPrimitiveCheck.getId())));
        cohesionDetector.getPrimitiveFieldCheck().set(Boolean.parseBoolean(settingsHandler.getValueByKey(cohesionFieldPrimitiveCheck.getId())));

        enableNamingCheck.setSelected(Boolean.parseBoolean(settingsHandler.getValueByKey(enableNamingCheck.getId())));
        cleanCodeDetector.getNamingCheckProperty().set(Boolean.parseBoolean(settingsHandler.getValueByKey(enableNamingCheck.getId())));

        cleanCodeNamingEnableClass.setSelected(Boolean.parseBoolean(settingsHandler.getValueByKey(cleanCodeNamingEnableClass.getId())));
        cleanCodeDetector.getNamingClassCheckProperty().set(Boolean.parseBoolean(settingsHandler.getValueByKey(cleanCodeNamingEnableClass.getId())));

        cleanCodeNamingEnableMethod.setSelected(Boolean.parseBoolean(settingsHandler.getValueByKey(cleanCodeNamingEnableMethod.getId())));
        cleanCodeDetector.getNamingMethodCheckProperty().set(Boolean.parseBoolean(settingsHandler.getValueByKey(cleanCodeNamingEnableMethod.getId())));

        cleanCodeNamingEnableField.setSelected(Boolean.parseBoolean(settingsHandler.getValueByKey(cleanCodeNamingEnableField.getId())));
        cleanCodeDetector.getNamingFieldCheckProperty().set(Boolean.parseBoolean(settingsHandler.getValueByKey(cleanCodeNamingEnableField.getId())));

        cohesionParameterMaxAmount.setText(settingsHandler.getValueByKey(cohesionParameterMaxAmount.getId()));
        cohesionDetector.getMaximalParameterProperty().set(settingsHandler.getValueByKey(cohesionParameterMaxAmount.getId()));

        cohesionFieldMaxTypes.setText(settingsHandler.getValueByKey(cohesionFieldMaxTypes.getId()));
        cohesionDetector.getMaxFieldTypesProperty().set(settingsHandler.getValueByKey(cohesionFieldMaxTypes.getId()));

        cohesionComplexityMaxAmount.setText(settingsHandler.getValueByKey(cohesionComplexityMaxAmount.getId()));
        cohesionDetector.getMaximalAmountKeywordsProperty().set(settingsHandler.getValueByKey(cohesionComplexityMaxAmount.getId()));

        cohesionNestedKeywords.setText(settingsHandler.getValueByKey(cohesionNestedKeywords.getId()));
        cohesionDetector.getCheckedKeywordsProperty().set(settingsHandler.getValueByKey(cohesionNestedKeywords.getId()));

        cohesionMethodLengthMethodMax.setText(settingsHandler.getValueByKey(cohesionMethodLengthMethodMax.getId()));
        cohesionDetector.getMaxMethodLength().set(settingsHandler.getValueByKey(cohesionMethodLengthMethodMax.getId()));

        cohesionMethodLengthConstructorMax.setText(settingsHandler.getValueByKey(cohesionMethodLengthConstructorMax.getId()));
        cohesionDetector.getMaxConstructorLength().set(settingsHandler.getValueByKey(cohesionMethodLengthConstructorMax.getId()));

        cleanCodeNamingClassRegex.setText(settingsHandler.getValueByKey(cleanCodeNamingClassRegex.getId()));
        cleanCodeDetector.getClassRegex().set(settingsHandler.getValueByKey(cleanCodeNamingClassRegex.getId()));

        cleanCodeNamingMethodRegex.setText(settingsHandler.getValueByKey(cleanCodeNamingMethodRegex.getId()));
        cleanCodeDetector.getMethodRegex().set(settingsHandler.getValueByKey(cleanCodeNamingMethodRegex.getId()));

        cleanCodeNamingFieldRegex.setText(settingsHandler.getValueByKey(cleanCodeNamingFieldRegex.getId()));
        cleanCodeDetector.getFieldProperty().set(settingsHandler.getValueByKey(cleanCodeNamingFieldRegex.getId()));

        try {
            cohesionParameterFeedbackType.setValue(ParameterCheckFeedbackTypes.valueOf(settingsHandler.getValueByKey(cohesionParameterFeedbackType.getId())));
            cohesionDetector.getParameterLengthFeedbackTypesProperty().set(ParameterCheckFeedbackTypes.valueOf(settingsHandler.getValueByKey(cohesionParameterFeedbackType.getId())));

            cohesionMethodLengthCheckType.setValue(ExecutableLengthCheckTypes.valueOf(settingsHandler.getValueByKey(cohesionMethodLengthCheckType.getId())));
            cohesionDetector.getMethodLengthCheckTypeProperty().set(ExecutableLengthCheckTypes.valueOf(settingsHandler.getValueByKey(cohesionMethodLengthCheckType.getId())));
        } catch (IllegalArgumentException e) {
            String errorMessage = "Invalid choicebox formats for settings (using default settings now).";

            logger.warning(errorMessage);

            printMessageToUI(errorMessage);
        }

        logger.fine("initialized settings.");
    }

    /**
     * Binds all components from the settings to the respective detector value.
     * When the view element gets updated (changed), the respective model value gets changed and
     * the new state gets written to the properties file.
     */
    private void linkSettingComponentsToModel() {
        logger.finer("Starting to link settingsdata with modeldata");

        couplingInstantiationBiDirectional.selectedProperty().addListener((observable, oldValue, newValue) -> {
            couplingDetector.getBidirProperty().set(newValue);
            settingsHandler.update(couplingInstantiationBiDirectional.getId(), String.valueOf(newValue));
        });
        couplingInstantiationOnePlace.selectedProperty().addListener((observable, oldValue, newValue) -> {
            couplingDetector.getOnePlaceProperty().set(newValue);
            settingsHandler.update(couplingInstantiationOnePlace.getId(), String.valueOf(newValue));
        });
        couplingAccessMethod.selectedProperty().addListener((observable, oldValue, newValue) -> {
            couplingDetector.getMethodAccessProperty().set(newValue);
            settingsHandler.update(couplingAccessMethod.getId(), String.valueOf(newValue));
        });
        couplingAccessField.selectedProperty().addListener((observable, oldValue, newValue) -> {
            couplingDetector.getFieldAccessProperty().set(newValue);
            settingsHandler.update(couplingAccessField.getId(), String.valueOf(newValue));
        });
        couplingAccessInnerClass.selectedProperty().addListener((observable, oldValue, newValue) -> {
            couplingDetector.getClassAccessProperty().set(newValue);
            settingsHandler.update(couplingAccessInnerClass.getId(), String.valueOf(newValue));
        });

        enableParameterCheck.selectedProperty().addListener((observable, oldValue, newValue) -> {
            cohesionDetector.getParameterCheckProperty().set(newValue);
            settingsHandler.update(enableParameterCheck.getId(), String.valueOf(newValue));
        });
        enableFieldTypeCheck.selectedProperty().addListener((observable, oldValue, newValue) -> {
            cohesionDetector.getFieldTypesCheckProperty().set(newValue);
            settingsHandler.update(enableFieldTypeCheck.getId(), String.valueOf(newValue));
        });
        enableComplexityCheck.selectedProperty().addListener((observable, oldValue, newValue) -> {
            cohesionDetector.getComplexityCheckProperty().set(newValue);
            settingsHandler.update(enableComplexityCheck.getId(), String.valueOf(newValue));
        });
        enableMethodLengthCheck.selectedProperty().addListener((observable, oldValue, newValue) -> {
            cohesionDetector.getExecutableLengthProperty().set(newValue);
            settingsHandler.update(enableMethodLengthCheck.getId(), String.valueOf(newValue));
        });
        cohesionParameterMaxAmount.textProperty().addListener((observable, oldValue, newValue) -> {
            cohesionDetector.getMaximalParameterProperty().set(newValue);
            settingsHandler.update(cohesionParameterMaxAmount.getId(), newValue);
        });
        cohesionParameterFeedbackType.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            cohesionDetector.getParameterLengthFeedbackTypesProperty().set((ParameterCheckFeedbackTypes) newValue);
            settingsHandler.update(cohesionParameterFeedbackType.getId(), String.valueOf(newValue));
        });
        cohesionFieldMaxTypes.textProperty().addListener((observable, oldValue, newValue) -> {
            cohesionDetector.getMaxFieldTypesProperty().set(newValue);
            settingsHandler.update(cohesionFieldMaxTypes.getId(), newValue);
        });
        cohesionFieldPrimitiveCheck.selectedProperty().addListener((observable, oldValue, newValue) -> {
            cohesionDetector.getPrimitiveFieldCheck().set(newValue);
            settingsHandler.update(cohesionFieldPrimitiveCheck.getId(), String.valueOf(newValue));
        });
        cohesionComplexityMaxAmount.textProperty().addListener((observable, oldValue, newValue) -> {
            cohesionDetector.getMaximalAmountKeywordsProperty().set(newValue);
            settingsHandler.update(cohesionComplexityMaxAmount.getId(), newValue);
        });
        cohesionNestedKeywords.textProperty().addListener((observable, oldValue, newValue) -> {
            cohesionDetector.getCheckedKeywordsProperty().set(newValue);
            settingsHandler.update(cohesionNestedKeywords.getId(), newValue);
        });
        cohesionMethodLengthCheckType.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            cohesionDetector.getMethodLengthCheckTypeProperty().set((ExecutableLengthCheckTypes) newValue);
            settingsHandler.update(cohesionMethodLengthCheckType.getId(), String.valueOf(newValue));
        });
        cohesionMethodLengthMethodMax.textProperty().addListener((observable, oldValue, newValue) -> {
            cohesionDetector.getMaxMethodLength().set(newValue);
            settingsHandler.update(cohesionMethodLengthMethodMax.getId(), newValue);
        });
        cohesionMethodLengthConstructorMax.textProperty().addListener((observable, oldValue, newValue) -> {
            cohesionDetector.getMaxConstructorLength().set(newValue);
            settingsHandler.update(cohesionMethodLengthConstructorMax.getId(), newValue);
        });

        enableNamingCheck.selectedProperty().addListener((observable, oldValue, newValue) -> {
            cleanCodeDetector.getNamingCheckProperty().set(newValue);
            settingsHandler.update(enableNamingCheck.getId(), String.valueOf(newValue));
        });
        cleanCodeNamingEnableClass.selectedProperty().addListener((observable, oldValue, newValue) -> {
            cleanCodeDetector.getNamingClassCheckProperty().set(newValue);
            settingsHandler.update(cleanCodeNamingEnableClass.getId(), String.valueOf(newValue));
        });
        cleanCodeNamingClassRegex.textProperty().addListener((observable, oldValue, newValue) -> {
            cleanCodeDetector.getClassRegex().set(newValue);
            settingsHandler.update(cleanCodeNamingClassRegex.getId(), newValue);
        });
        cleanCodeNamingEnableMethod.selectedProperty().addListener((observable, oldValue, newValue) -> {
            cleanCodeDetector.getNamingMethodCheckProperty().set(newValue);
            settingsHandler.update(cleanCodeNamingEnableMethod.getId(), String.valueOf(newValue));
        });
        cleanCodeNamingMethodRegex.textProperty().addListener((observable, oldValue, newValue) -> {
            cleanCodeDetector.getMethodRegex().set(newValue);
            settingsHandler.update(cleanCodeNamingMethodRegex.getId(), newValue);
        });
        cleanCodeNamingEnableField.selectedProperty().addListener((observable, oldValue, newValue) -> {
            cleanCodeDetector.getNamingFieldCheckProperty().set(newValue);
            settingsHandler.update(cleanCodeNamingEnableField.getId(), String.valueOf(newValue));
        });
        cleanCodeNamingFieldRegex.textProperty().addListener((observable, oldValue, newValue) -> {
            cleanCodeDetector.getFieldProperty().set(newValue);
            settingsHandler.update(cleanCodeNamingFieldRegex.getId(), newValue);
        });

        logger.finer("Linked settings.");
    }

    /**
     * Refreshes {@link #projectAsClass} and {@link #projectAsString}.
     */
    private void resetInputData() {
        projectAsString = null;
        projectAsClass = null;
    }

    /**
     * Prints the message in the message section {@link #errorMessage}.
     */
    private void printMessageToUI(String message) {
        Platform.runLater(() -> {
            errorMessage.setText(message);
        });
    }

    /**
     * Resets the message in the message section {@link #errorMessage}.
     */
    private void resetMessageToUI() {
        Platform.runLater(() -> {
            errorMessage.setText("");
        });
    }
}

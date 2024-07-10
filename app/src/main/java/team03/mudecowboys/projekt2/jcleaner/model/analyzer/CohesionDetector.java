package team03.mudecowboys.projekt2.jcleaner.model.analyzer;

import java.lang.reflect.*;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import team03.mudecowboys.projekt2.jcleaner.model.exception.FalseCheckedKeywordFormat;
import team03.mudecowboys.projekt2.jcleaner.model.util.CodePositionWarning;
import team03.mudecowboys.projekt2.jcleaner.model.util.ExecutableLengthCheckTypes;
import team03.mudecowboys.projekt2.jcleaner.model.util.ParameterCheckFeedbackTypes;


/**
 * {@inheritDoc}
 *
 * Used to inspect the user code for cohesion (responsibility of code modules).
 * It checks mainly the codecomplexities,
 * if there aren't too much parameters,
 * the method and constructor lengths and
 * if a class hasn't not too much different field types.
 */
public class CohesionDetector extends Detector {
    private static final Logger logger = Logger.getLogger(CohesionDetector.class.getCanonicalName());
    private final ListProperty<CodePositionWarning> codeNestedKeywordComplexityWarnings, codeParameterLengthWarnings, codeExcecutableWarnings, codeInstandVariableWarnings;
    private final BooleanProperty primitiveFieldCheck;

    private final StringProperty maximalNestedKeywords, maximalParameterLength;
    private final StringProperty maxFieldTypes;
    private final StringProperty maxMethodLength, maxConstructorLength;

    private final StringProperty nestedKeywords;

    private final BooleanProperty isParameterCheckEnabled,
                                  isFieldTypesCheckEnables,
                                  isComplexityCheckEnabled,
                                  isExecutableLengthCheckEnabled;

    private final ObjectProperty<ParameterCheckFeedbackTypes> parameterLengthFeedbackType;
    private final ObjectProperty<ExecutableLengthCheckTypes> methodLengthCheckType;


    public CohesionDetector() {
        this.codeNestedKeywordComplexityWarnings = new SimpleListProperty<>(FXCollections.observableArrayList(new ArrayList<>()));
        this.codeParameterLengthWarnings = new SimpleListProperty<>(FXCollections.observableArrayList(new ArrayList<>()));
        this.codeExcecutableWarnings = new SimpleListProperty<>(FXCollections.observableArrayList(new ArrayList<>()));
        this.codeInstandVariableWarnings = new SimpleListProperty<>(FXCollections.observableArrayList(new ArrayList<>()));
        this.maximalNestedKeywords = new SimpleStringProperty();
        this.maximalParameterLength = new SimpleStringProperty();
        this.primitiveFieldCheck = new SimpleBooleanProperty();
        this.isParameterCheckEnabled = new SimpleBooleanProperty();
        this.isFieldTypesCheckEnables = new SimpleBooleanProperty();
        this.isComplexityCheckEnabled = new SimpleBooleanProperty();
        this.isExecutableLengthCheckEnabled = new SimpleBooleanProperty();
        this.parameterLengthFeedbackType = new SimpleObjectProperty<>();
        this.maxFieldTypes = new SimpleStringProperty();
        this.nestedKeywords = new SimpleStringProperty();
        this.methodLengthCheckType = new SimpleObjectProperty<>();
        this.maxMethodLength = new SimpleStringProperty();
        this.maxConstructorLength = new SimpleStringProperty();
    }


    public BooleanProperty getParameterCheckProperty() {
        return isParameterCheckEnabled;
    }

    public BooleanProperty getFieldTypesCheckProperty() {
        return isFieldTypesCheckEnables;
    }

    public BooleanProperty getComplexityCheckProperty() {
        return isComplexityCheckEnabled;
    }

    public BooleanProperty getExecutableLengthProperty() {
        return isExecutableLengthCheckEnabled;
    }

    public StringProperty getMaximalParameterProperty() {
        return maximalParameterLength;
    }

    public ObjectProperty<ParameterCheckFeedbackTypes> getParameterLengthFeedbackTypesProperty() {
        return parameterLengthFeedbackType;
    }

    public StringProperty getMaxFieldTypesProperty() {
        return maxFieldTypes;
    }

    public BooleanProperty getPrimitiveFieldCheck() {
        return primitiveFieldCheck;
    }

    public StringProperty getMaximalAmountKeywordsProperty() {
        return maximalNestedKeywords;
    }

    public StringProperty getCheckedKeywordsProperty() {
        return nestedKeywords;
    }

    public ObjectProperty<ExecutableLengthCheckTypes> getMethodLengthCheckTypeProperty() {
        return methodLengthCheckType;
    }

    public StringProperty getMaxMethodLength() {
        return maxMethodLength;
    }

    public StringProperty getMaxConstructorLength() {
        return maxConstructorLength;
    }


    /**
     * {@inheritDoc}
     */
    @Override public void run() throws Exception {
        logger.info("- Starting cohesion detection...");

        clear();

        if(isComplexityCheckEnabled.get()) analyzeCodeComplexity();
        if(isParameterCheckEnabled.get()) analyzeParameterLength();
        if(isExecutableLengthCheckEnabled.get()) analyzeMethodLength();
        if(isExecutableLengthCheckEnabled.get()) analyzeConstructorLength();
        if(isFieldTypesCheckEnables.get()) analyzeInstantvariableAmount();

        logger.info("=> finished cohesion detection");
    }


    /**
     * {@inheritDoc}
     */
    @Override public HashMap<String, HashMap<String, ListProperty>> getResults(){
        HashMap<String, HashMap<String, ListProperty>> results = new HashMap<>();
        HashMap<String, ListProperty> propertyHashMap = new HashMap<>();
        propertyHashMap.put("codeNestedKeywordComplexityWarnings", codeNestedKeywordComplexityWarnings);
        propertyHashMap.put("codeParameterLengthWarnings", codeParameterLengthWarnings);
        propertyHashMap.put("codeExcecutableWarnings", codeExcecutableWarnings);
        propertyHashMap.put("codeInstandVariableWarnings", codeInstandVariableWarnings);
        results.put("Cohesion", propertyHashMap);
        return results;
    }


    /**
     * Checks the codecomplexities.
     * Because nested and branched functions and sequence structure blocks can be an indication of low cohesion.
     * @throws FalseCheckedKeywordFormat if the nested keyword format given in the settings is invalid.
     */
    private void analyzeCodeComplexity() throws FalseCheckedKeywordFormat {
        logger.info("- Starting code complexity check.");
        for(Class<?> clazz : classes) {
            logger.fine("current checked class: " + clazz.getSimpleName());

            Method[] methods = clazz.getDeclaredMethods();
            Constructor[] constructors = clazz.getDeclaredConstructors();

            for(Method method : methods) {
                logger.finer("Start to analyze method: " + method.getName());

                analyzeNestedKeywordsComplexity(method);
            }

            for(Constructor constructor : constructors) {
                logger.finest("Start to analyze constructor: " + constructor.getName());
                analyzeNestedKeywordsComplexity(constructor);
            }
        }
        logger.info("=> Completed code complexity analysis.");
    }

    /**
     * Checks the parameter length.
     * If there are too much parameters, it also gives a solution to make it better.
     * @throws NumberFormatException if it couldn't parse from string ({@link #maximalParameterLength}) to int.
     */
    public void analyzeParameterLength() throws NumberFormatException {
        logger.info("- Starting to analyze Parameter length.");
        for(Class<?> clazz : classes) {
            logger.fine("Current checked class: " + clazz.getSimpleName());

            Method[] methods = clazz.getDeclaredMethods();
            Constructor[] constructors = clazz.getDeclaredConstructors();

            for(Method method : methods) {
                logger.finer("Current checked method: " + method.getName());

                Parameter[] parameters = method.getParameters();

                if(parameters.length > Integer.parseInt(maximalParameterLength.get())) {
                   logger.info("Found a method with too many parameters.");
                    String argObject = switch(parameterLengthFeedbackType.get()) {
                        case BOTH -> prepareArgObject(parameters, method.getName());
                        case ARGUMENT_OBJECT -> prepareArgObject(parameters, method.getName());
                        default -> "";
                    };
                    String builderObject = switch(parameterLengthFeedbackType.get()) {
                        case BOTH -> prepareBuilderObject(parameters, method.getName());
                        case BUILDER_OBJECT -> prepareBuilderObject(parameters, method.getName());
                        default -> "";
                    };


                    codeParameterLengthWarnings.add(new CodePositionWarning(clazz, method, argObject, builderObject));
                }
            }

            for(Constructor constructor : constructors) {
                logger.finest("Current checked constructor: " + constructor.getName());

                Parameter[] parameters = constructor.getParameters();

                if(parameters.length > Integer.parseInt(maximalParameterLength.get())) {
                  logger.info("Found a constructor with too many parameters.");
                    String argObject = switch(parameterLengthFeedbackType.get()) {
                        case BOTH -> prepareArgObject(parameters, constructor.getName());
                        case ARGUMENT_OBJECT -> prepareArgObject(parameters, constructor.getName());
                        default -> "";
                    };
                    String builderObject = switch(parameterLengthFeedbackType.get()) {
                        case BOTH -> prepareBuilderObject(parameters, constructor.getName());
                        case BUILDER_OBJECT -> prepareBuilderObject(parameters, constructor.getName());
                        default -> "";
                    };

                    codeParameterLengthWarnings.add(new CodePositionWarning(clazz, constructor, argObject, builderObject));
                }
            }
        }
        logger.info("=> Completed Paramether length analysis.");
    }

    /**
     * Checks all methods for the length.
     * If the methods is longer than the set max, codeParameterLengthWarning is on.
     * @throws NumberFormatException if it couldn't parse from string ({@link #maxMethodLength}) to int.
     */
    public void analyzeMethodLength() throws NumberFormatException {
        logger.info("- Starting to analyze method length.");
        for(Class<?> clazz : classes ) {
            logger.fine("Current checked class" + clazz.getSimpleName());

            Method[] methods = clazz.getDeclaredMethods();

            for(Method method : methods) {
                logger.finer("Current checked method" + method.getName());

                int newLineAmount = 0;
                String methodAsString = getExecutableAsString(method);
                char[] chars = methodAsString.toCharArray();
                for(char newLine : chars) {
                    if(newLine == methodLengthCheckType.get().getSymbol()) {
                        newLineAmount += 1;
                    }
                }
                if(newLineAmount > Integer.parseInt(maxMethodLength.get())) {
                  logger.finest("Found a method which is too long");
                    codeExcecutableWarnings.add(new CodePositionWarning(clazz, method));
                }
            }
        }
        logger.info("=> Completed method length analysis.");
    }


    /**
     * Checks all constructors for the length.
     * if the constructor is longer than the set max, codeParameterLengthWarning is on
     * @throws NumberFormatException if it couldn't parse from string ({@link #maxConstructorLength}) to int.
     */
    public void analyzeConstructorLength() throws NumberFormatException {
        logger.info("- Starting to analyze constructor length.");
        for(Class<?> clazz : classes) {
            logger.fine("Current checked class: " + clazz.getSimpleName());

            Constructor[] constructors = clazz.getDeclaredConstructors();

            for(Constructor constructor : constructors) {
                logger.finer("Current checked constructor: " + constructor.getName());

                int newLineAmount = 0;
                String constructorAsString = getExecutableAsString(constructor);
                char[] chars = constructorAsString.toCharArray();
                for(char newLine : chars) {
                    if(newLine == methodLengthCheckType.get().getSymbol()) {
                        newLineAmount += 1;
                    }
                }
                if(newLineAmount > Integer.parseInt(maxConstructorLength.get())) {
                  logger.finest("Found a constructor which is too long");
                    codeExcecutableWarnings.add(new CodePositionWarning(clazz, constructor));
                }
            }
        }
        logger.info("=> Completed constructor length analysis.");
    }

    /**
     * Checks the amount of fields a class has.
     * It differs between Wrapper/primitiv & normal classes.
     * @throws NumberFormatException if it couldn't parse from string ({@link #maxFieldTypes}) to int.
     */
    public void analyzeInstantvariableAmount() throws NumberFormatException {
        logger.info("- Starting to analyze the amount of instant variables.");
        for(Class<?> clazz : classes) {
            logger.fine("Current checked class: " + clazz.getSimpleName());

            int amountOfFields = 0;
            Field[] fields = clazz.getDeclaredFields();

            for(Field field : fields) {
                logger.finer("Current checked field: " + field.getName());

                Class fieldClass =  field.getClass();
                boolean isWrapperPrimitive = isWrapperOrPrimitiveType(fieldClass);

                if((primitiveFieldCheck.get() && isWrapperPrimitive) || !isWrapperPrimitive) {
                    amountOfFields += 1;
                }
            }

            if(amountOfFields > Integer.parseInt(maxFieldTypes.get())) {
                logger.finest("Found a class with too many fields.");
                codeInstandVariableWarnings.add(new CodePositionWarning(clazz, null));

            }
        }
        logger.info("=> Completed amount of instand variables analysis.");
    }


    /**
     * Helpermethod for {@link #analyzeCodeComplexity()} to check if there aren't too much keywords in one statement body.
     * A statement body is checked just in the layer of the method / constructor (static block aren't checked at all - feature for future releases).
     * @param executable Method or constructor.
     * @throws NumberFormatException if it couldn't parse from string ({@link #maximalNestedKeywords}) to int.
     * @throws FalseCheckedKeywordFormat if the nested keyword format given in the settings is invalid.
     */
    private void analyzeNestedKeywordsComplexity(Executable executable) throws NumberFormatException, FalseCheckedKeywordFormat {
        String[] formatedNestedKeywords = prepareNestedKeywords();
            logger.info("- Starting to analyze nested keywords complexity.");
        List<Integer[]> statementPosition = new ArrayList<>();
        String methodBody = getExecutableAsString(executable);

        String keywords = "";
        for(int i = 0; i < formatedNestedKeywords.length - 1; i++) keywords += "(;|\\}|\\{|\\()\\s*"+ formatedNestedKeywords[i] + "\\b|";

        keywords += "(;|\\}|\\{|\\()\\s*" + formatedNestedKeywords[formatedNestedKeywords.length - 1] + "\\b";

        Pattern statementFilter = Pattern.compile(keywords);
        Matcher matcher = statementFilter.matcher(methodBody);

        while(matcher.find()) {
            if(!checkIfOneLineStatement(matcher.start(), methodBody)) {
                logger.fine("Found a statement with more than one Line.");

                String statementBody = extractBody(matcher.start() + 1, methodBody);
                Integer[] position = {matcher.start(), matcher.start() + statementBody.length() - 1};

                if(!checkIfOverlap(position, statementPosition)) statementPosition.add(position);
            }
        }

        for(Integer[] position : statementPosition) {
            logger.finer("Checking positions");

            int counter = 0;
            String statement = methodBody.substring(position[0], position[1]);

            for(String keyword : formatedNestedKeywords) {
                logger.finest("Current checked keyword: " + keyword);
                Pattern pattern = Pattern.compile("(\s|\\(|\\{|\\}|\\;|\\))" + keyword + "(\s|\\(|\\{|\\}|\\;|\\))");
                Matcher keywordMatch = pattern.matcher(statement);

                counter += keywordMatch.results().count();
            }

            if(counter > Integer.parseInt(maximalNestedKeywords.get())) codeNestedKeywordComplexityWarnings.add(new CodePositionWarning(executable.getDeclaringClass(), executable)); // TODO: ADD INFO WHERE EXACTLY AND MAYBE STATEMENT CODE SNIPPET?
        }
        logger.info("=> Completed nested keywords complexity analysis.");
    }

    /**
     * Puts {@link #nestedKeywords} into a string array.
     * @return A string array with the keywords.
     * @throws FalseCheckedKeywordFormat if the nested keyword format given in the settings is invalid.
     */
    private String[] prepareNestedKeywords() throws FalseCheckedKeywordFormat {
        logger.info("- put nested keyword into a string.");
        final String DELIMITER = ",";

        Pattern keywordFilter = Pattern.compile("(^\s*')([a-zA-Z0-9_ ]*)(')");

        String[] splitedKeywords = nestedKeywords.get().split(DELIMITER);
        String[] keywords = new String[splitedKeywords.length];

        for(int i = 0; i < splitedKeywords.length; i++) {
            Matcher matcher = keywordFilter.matcher(splitedKeywords[i]);

            if(matcher.find()) {
                keywords[i] = matcher.group(2);
            } else {
                throw new FalseCheckedKeywordFormat();
            }
        }
        logger.info("=> nested keywords are now a string.");
        return keywords;
    }

    /**
     * Helpermethod for {@link #analyzeNestedKeywordsComplexity(Executable)} to check if the statement is a one line statement or not.
     * In a single statement, a semicolon comes before a curly bracket (except a statement with normal brackets).
     * To count the amount of brackets open / close, we implemented a pushdown automaton emulator (similar to {@link #extractBody(int, String)}).
     * @param index The start index of the statement.
     * @param methodBody Method body, which gets checked.
     * @return True if end of string or single line statement.
     */
    private boolean checkIfOneLineStatement(int index, String methodBody) {
        logger.info("- Starting one line statement check.");

        final char BRACKETS_OPEN = '(', BRACKETS_CLOSE = ')', INIT_SYMBOL = '$'; // stackalphabet
        final List<Character> stack = new ArrayList<>();

        boolean firstFound = false, lastFound = false;

        stack.add(INIT_SYMBOL);

        for(int i = index + 1; i < methodBody.length() - 1; i++) {
            logger.fine("Checking method body:");

            final char currentChar = methodBody.charAt(i);

            if(currentChar == BRACKETS_OPEN && !lastFound) {
                stack.add(BRACKETS_OPEN); // push
                firstFound = true;
            }
            else if(currentChar == BRACKETS_CLOSE && !lastFound) stack.remove(stack.size() - 1); // pop

            if(stack.get(stack.size() - 1) == INIT_SYMBOL && firstFound) {
                lastFound = true;

                if(currentChar == '{') return false;
                else if(currentChar == ';') return true;
            }
        }
        logger.info("=> Completed one line statement check.");

        return true; // EOF
    }

    /**
     * Check if the new positions overlapping with already captured statements.
     * This prevents the capturing for nested keywords.
     * @param position The position, which gets checked.
     * @param list The list, with already captured positions.
     * @return True if the positions overlap.
     */
    private boolean checkIfOverlap(Integer[] position, List<Integer[]> list) {
        logger.info("- Starting overlap check.");

        int startPosition = position[0];
        int endPosition = position[1];

        for(Integer[] listPosition : list) {
            int startListPosition = listPosition[0];
            int endListPosition = listPosition[1];

            if((startListPosition < startPosition && endListPosition > startPosition) ||
               (startListPosition < endPosition && endListPosition > endPosition)) return true;
        }
        logger.info("=> Completed overlap check. position does not Overlap");

        return false;
    }

    /**
     * Cleans everything before starting a new test, so that it can update it on the new test state.
     */
    private void clear() {
        codeNestedKeywordComplexityWarnings.clear();
        codeParameterLengthWarnings.clear();
        codeExcecutableWarnings.clear();
        codeInstandVariableWarnings.clear();
    }

    /**
     * Helpermethod for {@link #analyzeParameterLength()} to prepare a warning solution (as string) for the user.
     * This solution contains a new object as an argument object.
     * @param parameters An array of the parameter of the executable.
     * @param executableName The name of the executable.
     * @return A string of the warning solution.
     */
    private static String prepareArgObject(Parameter[] parameters, String executableName) {
        logger.info("- Starting to prepare Arg Object");

        String argObject = String.format("public class %sArgumentObject {\n", (executableName.substring(0, 1).toUpperCase() + executableName.substring(1)));
        String argObjectField = "\n", argObjectGetterSetter = "\n";

        for(Parameter parameter : parameters) {
            logger.fine("Parameter to prepare: " + parameter.getName());

            String parameterType = parameter.getType().getSimpleName();
            String parameterNameMethod = parameter.getName().substring(0, 1).toUpperCase() + parameter.getName().substring(1);
            String parameterNameVariable = parameter.getName();

            argObjectField += String.format("\tprivate %s %s;\n", parameterType, parameterNameVariable);
            argObjectGetterSetter += String.format("\tpublic %s get%s() {\n\t\treturn this.%s;\n\t}\n\tpublic void set%s(%s %s) {\n\t\tthis.%s = %s;\n\t}\n\n", parameterType, parameterNameMethod, parameterNameVariable, parameterNameMethod, parameterType, parameterNameVariable, parameterNameVariable, parameterNameVariable);
        }
        logger.info("=> Completed to prepare arg object");

        return argObject + argObjectField + argObjectGetterSetter + "\n}";
    }

    /**
     * Helpermethod for {@link #analyzeParameterLength()} to prepare a warning solution (as string) for the user.
     * This solution contains a new object with a builder.
     * @param parameters An array of the parameter of the executable.
     * @param executableName The name of the executable.
     * @return A string of the warning solution.
     */
    private String prepareBuilderObject(Parameter[] parameters, String executableName) {
        logger.info("-Starting to prepare builder object.");

        String argObject = String.format("public class %sArgumentBuilder {\n", (executableName.substring(0, 1).toUpperCase() + executableName.substring(1)));
        String argObjectBuilder = "\tpublic static class Builder {\n";
        String argObjectField = "\n", argObjectGetter = "\n", argObjectBuilderField = "\n", argObjectBuilderSetter = "\n";
        String builderBuild = String.format("\n\t\tpublic %sArgumentBuilder build() {\n \t\t\treturn new %sArgumentBuilder(", (executableName.substring(0, 1).toUpperCase() + executableName.substring(1)), (executableName.substring(0, 1).toUpperCase() + executableName.substring(1)));
        String argObjectConstructor = String.format("\n\tprivate %sArgumentBuilder(", (executableName.substring(0, 1).toUpperCase() + executableName.substring(1))), argObjectConstructorField = "\n";


        for(int i = 0; i < parameters.length; i++) {
            String parameterType = parameters[i].getType().getSimpleName();
            String parameterNameMethod = parameters[i].getName().substring(0, 1).toUpperCase() + parameters[i].getName().substring(1);
            String parameterNameVariable = parameters[i].getName();

            argObjectField += String.format("\tprivate final %s %s;\n", parameterType, parameterNameVariable);
            argObjectGetter += String.format("\tpublic %s get%s() {\n\t\treturn this.%s;\n\t}\n", parameterType, parameterNameMethod, parameterNameVariable);
            argObjectBuilderField += String.format("\t\tprivate %s %s;\n", parameterType, parameterNameVariable);
            argObjectBuilderSetter += String.format("\t\tpublic Builder set%s(%s %s) {\n\t\t\tthis.%s = %s;\n\t\t\treturn this;\n\t\t}\n", parameterNameMethod, parameterType, parameterNameVariable, parameterNameVariable, parameterNameVariable);
            argObjectConstructorField += String.format("\t\tthis.%s = %s;\n", parameterNameVariable, parameterNameVariable);

            if(i < parameters.length - 1) {
                builderBuild += String.format("this.%s,", parameterNameVariable);
                argObjectConstructor += String.format("%s %s,", parameterType, parameterNameVariable);
            }
        }

        builderBuild += String.format("this.%s);\n\t\t}\n\t}\n}", parameters[parameters.length - 1]);
        argObjectConstructor += String.format("%s %s) {\n", parameters[parameters.length - 1].getType().getSimpleName(), parameters[parameters.length - 1].getName());
        argObjectConstructorField += "\t}\n";

        logger.info("=> Completed preparation of Builder object.");

        return argObject + argObjectField + argObjectConstructor + argObjectConstructorField + argObjectGetter + argObjectBuilder + argObjectBuilderField + argObjectBuilderSetter + builderBuild;
    }
}

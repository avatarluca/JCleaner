package team03.mudecowboys.projekt2.jcleaner.model.analyzer;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import team03.mudecowboys.projekt2.jcleaner.model.util.CodePositionWarning;


/**
 * {@inheritDoc}
 *
 * Used to inspect the user code for coupling (dependence of code modules).
 * It checks mainly if each class may only be instantiated in one place,
 * if there isn't a bidirectional dependency between two classes and
 * if a method is public and if it is not called anywhere else.
 */
public class CouplingDetector extends Detector {
    private static final Logger logger = Logger.getLogger(CouplingDetector.class.getCanonicalName());

    private final ListProperty<CodePositionWarning> fieldDependenceWarnings, fieldBidirectionalWarnings, accessModificatorWarnings;
    private final BooleanProperty isBidirEnabled, isOnePlaceEnabled;
    private final BooleanProperty isMethodAccessEnabled, isFieldAccessEnabled, isClassAccessEnabled;


    public CouplingDetector() {
        this.fieldDependenceWarnings = new SimpleListProperty<>(FXCollections.observableArrayList(new ArrayList<>()));
        this.fieldBidirectionalWarnings = new SimpleListProperty<>(FXCollections.observableArrayList(new ArrayList<>()));
        this.accessModificatorWarnings = new SimpleListProperty<>(FXCollections.observableArrayList(new ArrayList<>()));
        this.isBidirEnabled = new SimpleBooleanProperty();
        this.isOnePlaceEnabled = new SimpleBooleanProperty();
        this.isMethodAccessEnabled = new SimpleBooleanProperty();
        this.isFieldAccessEnabled = new SimpleBooleanProperty();
        this.isClassAccessEnabled = new SimpleBooleanProperty();
    }


    public ListProperty<CodePositionWarning> getFieldDependenceWarningsProperty() {
        return fieldDependenceWarnings;
    }

    public ListProperty<CodePositionWarning> getFieldBidirectionalWarningsProperty() {
        return fieldBidirectionalWarnings;
    }

    public ListProperty<CodePositionWarning> getAccessModificatorWarningsProperty() {
        return accessModificatorWarnings;
    }

    public BooleanProperty getBidirProperty() {
        return isBidirEnabled;
    }

    public BooleanProperty getOnePlaceProperty() {
        return isOnePlaceEnabled;
    }

    public BooleanProperty getMethodAccessProperty() {
        return isMethodAccessEnabled;
    }

    public BooleanProperty getFieldAccessProperty() {
        return isFieldAccessEnabled;
    }

    public BooleanProperty getClassAccessProperty() {
        return isClassAccessEnabled;
    }


    /**
     * {@inheritDoc}
     */
    @Override public void run() throws Exception {
        logger.info("> Starting coupling detection...");

        clear();

        analyzeFieldDependence();
        analyzeAccessModificator();

        logger.info("... finish coupling detection.");
    }



    /**
     * {@inheritDoc}
     */
    @Override public HashMap<String, HashMap<String, ListProperty>> getResults(){
        HashMap<String, HashMap<String, ListProperty>> results = new HashMap<>();
        HashMap<String, ListProperty> propertyHashMap = new HashMap<>();
        propertyHashMap.put("fieldDependenceWarnings", fieldDependenceWarnings);
        propertyHashMap.put("FieldBidirectionalWarnings", fieldBidirectionalWarnings);
        propertyHashMap.put("AccessModificatorWarnings", accessModificatorWarnings);
        results.put("Coupling", propertyHashMap);
        return results;
    }


    /**
     * Checks if each class may only be instantiated in one place (data area of a class) for {@link #fieldDependenceWarnings}
     * and if there isn't a bidirectional dependency between two classes for {@link #fieldBidirectionalWarnings}.
     * In addition, the field shouldn't be a primitive type. This is checked with {@link #isWrapperOrPrimitiveType(Class)}.
     */
    public void analyzeFieldDependence() {
        logger.info("- Starting field dependence check.");

        for(Class currentClass : classes) {
            logger.fine("Current checked class: " + currentClass.getSimpleName());

            final Field[] fields = currentClass.getDeclaredFields();

            for(Field currentField : fields) {
                logger.finer("Checking now field: " + currentField.getName());

                if(!isWrapperOrPrimitiveType(currentField.getType()) &&
                   !checkAmountOfInstantiations(currentField, classes) &&
                   !isAlreadyAWarning(currentClass, currentField, fieldDependenceWarnings) &&
                    isOnePlaceEnabled.get()) {
                    logger.info("Founded a field dependence warning in class: " + currentClass.getSimpleName() + " in field: " + currentField.getName());
                    fieldDependenceWarnings.add(new CodePositionWarning(currentClass, currentField));
                }

                if(!isWrapperOrPrimitiveType(currentField.getType()) &&
                    checkBidirectionalDependence(currentField, classes) &&
                    isBidirEnabled.get()) {
                    logger.info("Founded a bidirectional field dependence warning in class: " + currentClass.getSimpleName() + " in field: " + currentField.getName());
                    fieldBidirectionalWarnings.add(new CodePositionWarning(currentClass, currentField));
                }
            }
        }

        logger.info("=> Completed field dependence check.");
    }

    /**
     * Checks if a method is public or protected and if it is not called anywhere else it should give a warning.
     * There are basically two ways how the methods can be called outside the class:
     * <ul>
     *  <li>Classcall (static): CLASSNAME.METHODNAME(), CLASSNAME::METHODNAME</li>
     *  <li>Objectcall: OBJECTNAME.METHODNAME(), OBJECTNAME::METHODNAME</li>
     * </ul>
     *
     * For the first case (if the method is declared statically) each class (except its own) is checked
     * if the syntax is as defined above (this includes static blocks).
     * For the second case, each method is checked (this excludes static blocks -
     * if we want to have this feature, the code must be extended to extract also "static{...}" out of the source code).
     *
     * If a code segment is found with the respective call, the following flowchart is followed:
     * <pre>
     *                              Is OBJECTNAME defined locally?
     *                                           |
     *                    _________ YES _________|_________ NO _________
     *                   |                                              |
     *                   |                                              |
     *   Does this have the searched type?         Is OBJECTNAME defined in the parameter list?
     *   => Return                                                      |
     *                                           _________ YES _________|_________ NO _________
     *                                          |                                              |
     *                                          |                                              |
     *                          Does this have the searched type?          Is OBJECTNAME defined in the field list?
     *                          => Return                                                      |
     *                                                                  _________ YES _________|_________ NO _________
     *                                                                 |                                              |
     *                                                                 |                                              |
     *                                                  Does this have the searched type?              Go up to super or member class field
     *                                                  => Return                                      => Return
     * </pre>
     *
     * It also checks if a datafield is public. If yes then it gives a warning.
     * In addition it checks if a inner class is public. If yes then it gives a warning.
     */
    private void analyzeAccessModificator() {
        logger.info("- Starting access modificator check.");

        for(Class currentClass : classes) {
            logger.fine(String.format("Current checked class: %s.", currentClass.getSimpleName()));

            final Method[] methods = currentClass.getDeclaredMethods();
            final Field[] fields = currentClass.getFields();

            for(Method currentMethod : methods) {
                logger.finer(String.format("Checking now method: %s.", currentMethod.getName()));

                if(((Modifier.isPublic(currentMethod.getModifiers())) ||
                   (Modifier.isProtected(currentMethod.getModifiers()))) &&
                   (!checkMethodAccess(currentMethod, classes)) &&
                   isMethodAccessEnabled.get()) {
                    logger.info(String.format("Founded a access modificator warning in class: %s in method: %s.", currentClass.getSimpleName(), currentMethod.getName()));
                    accessModificatorWarnings.add(new CodePositionWarning(currentClass, currentMethod));
                }
            }

            for(Field field : fields) {
                logger.finer("Checking now field: " + field.getName());

                if((Modifier.isPublic(field.getModifiers()) ||
                    Modifier.isProtected(field.getModifiers())) &&
                    isFieldAccessEnabled.get()) {
                    logger.info(String.format("Founded a access modificator warning in class: %s in field: %s." + currentClass.getSimpleName(), field.getName()));
                    accessModificatorWarnings.add(new CodePositionWarning(currentClass, field));
                }
            }

            if(currentClass.isMemberClass() &&
               Modifier.isPublic(currentClass.getModifiers()) &&
               isClassAccessEnabled.get()) {
                logger.info(String.format("Founded a access modificator warning in inner/member class: %s.", currentClass.getSimpleName()));
                accessModificatorWarnings.add(new CodePositionWarning(currentClass, null));
            }
        }

        logger.info("=> Completed access modificator check.");
    }

    /**
     * Helpermethod for {@link #analyzeFieldDependence(Set)} to check if each class may only be instantiated
     * in one place.
     * @param field The field, which gets watched.
     * @param classes The classes, which gets inspected.
     * @return True if the field occurs only once.
     */
    private boolean checkAmountOfInstantiations(Field field, Set<Class<?>> classes) {
        int amountOfInstantiations = 0;

        for(Class currentClass : classes) {
            final Field[] fields = currentClass.getDeclaredFields();

            for(Field currentField : fields) {
                if(currentField.getType().getName().equals(field.getType().getName())) amountOfInstantiations++;
            }
        }

        logger.finest(String.format("Amount of instantiations (of class: %s) is %d times (should be 1)." , field.getType().getName(), amountOfInstantiations));

        return amountOfInstantiations == 1;
    }

    /**
     * Helpermethod for {@link #analyzeFieldDependence(Set)} to check if there isn't a bidirectional dependency
     * between two classes.
     * @param field The field, which gets watched.
     * @param classes The classes, which gets inspected.
     * @return True if the field contains a bidirectional connection.
     */
    private boolean checkBidirectionalDependence(Field field, Set<Class<?>> classes) {
        logger.info("- Starting to check if there is a bidirectional dependence.");

        final Class<?> typeOfCurrentField = field.getType();

        for(Field fieldOfOtherField : typeOfCurrentField.getDeclaredFields()) {
            final Class<?> typeOfOtherField = fieldOfOtherField.getType();

            if(typeOfOtherField.getName().equals(field.getDeclaringClass().getName())) return true;
        }

        logger.info("=> checked the bidirectional dependence.");
        return false;
    }

    /**
     * Helpermethod for {@link #analyzeAccessModificator(Set)} to check if a method is called somewhere outside
     * the declared class.
     * @param method The method, which gets watched.
     * @param classes The classes, which gets inspected.
     * @return True if the method gets called at least once outside of the declared class.
     */
    private boolean checkMethodAccess(Method method, Set<Class<?>> classes) {
        logger.info("- Starting to check where a method is called.");

        for(Class currentClass : classes) {
            logger.fine("Current checked class: " + currentClass.getSimpleName());

            if(currentClass.getSimpleName().equals(method.getDeclaringClass().getSimpleName())) continue;

            final Method[] methods = currentClass.getDeclaredMethods();
            final Constructor[] constructors = currentClass.getDeclaredConstructors();

            for(Method currentMethod : methods) {
                logger.finest("Current checked method: " + currentMethod.getName());

                if((Modifier.isStatic(method.getModifiers()) &&
                   (checkStaticMethodCall(method, currentMethod, currentClass)) ||
                   checkNonStaticMethodCall(method, currentMethod, currentClass))) return true;
            }

            for(Constructor currentConstructor : constructors) {
                logger.finest("Current checked Constructor: " + currentConstructor.getName());

                if((Modifier.isStatic(method.getModifiers()) &&
                   (checkStaticMethodCall(method, currentConstructor, currentClass)) ||
                   checkNonStaticMethodCall(method, currentConstructor, currentClass))) return true;
            }
        }

        logger.info("=> Checked where a method is called.");
        return false;
    }

    /**
     * Helpermethod for {@link #checkMethodAccess(Method, Set)} to check,
     * if modifier of the given method is static and the other methods contain CLASSNAME.METHODNAME() then return true.
     * @param method The method, which gets watched.
     * @param executable A constructor or a method.
     * @param currentClass The class, which gets inspected.
     * @return True if CLASSNAME.METHODNAME() is defined in the class.
     */
    private boolean checkStaticMethodCall(Method method, Executable executable, Class<?> currentClass) {
        logger.info("- Starting to check if a static method is called.");
        logger.fine("Current checked class: " + currentClass.getSimpleName());

        final Pattern pattern1 = Pattern.compile(method.getDeclaringClass().getSimpleName() + "\s*.\s*" + method.getName() + "\s*\\(\\)"); // => "CLASSNAME.METHODNAME()"
        final Pattern pattern2 = Pattern.compile(method.getDeclaringClass().getSimpleName() + "\s*::\s*" + method.getName()); // => "CLASSNAME::METHODNAME"

        final String executableAsString = getExecutableAsString(executable);

        if((pattern1.matcher(executableAsString).find() || pattern2.matcher(executableAsString).find())) return true;

        logger.info("=> Checked for static method calls.");

        return false;
    }

    /**
     * Helpermethod for {@link #checkMethodAccess(Method, Set)} to check,
     * if the method gets called in an other method with OBJECTNAME.METHODNAME() then return true
     * (by searching OBJECTNAME definition in local variable field and data field of the other class
     * - see {@link #analyzeAccessModificator(Set)}).
     * @param method The method, which gets watched.
     * @param executable A constructor or a method.
     * @param currentClass The class, which gets inspected.
     * @return True if OBJECTNAME.METHODNAME() is defined in the class.
     */
    private boolean checkNonStaticMethodCall(Method method, Executable executable, Class<?> currentClass) {
        logger.info("- Starting to check for non static method call.");
        logger.fine("Current checked class: " + currentClass.getSimpleName());
        logger.finer("Current checked method: " + method.getName());

        final Pattern pattern1 = Pattern.compile("[a-zA-Z0-9_]+\s*\\.\s*" + method.getName() + "\s*\\("); // => "OBJECTNAME.METHODNAME(" // TODO ()
        final Pattern pattern2 = Pattern.compile("[a-zA-Z0-9_]+\s*::\s*" + method.getName()); // => "OBJECTNAME::METHODNAME"

        final String methodAsString = getExecutableAsString(executable);

        final Matcher matcher1 = pattern1.matcher(methodAsString);
        final Matcher matcher2 = pattern2.matcher(methodAsString);

        logger.info("=> Completed check for non static method call.");
    
        return matchMethodCallViaObject(matcher1, methodAsString, method, executable, currentClass) ||
               matchMethodCallViaObject(matcher2, methodAsString, method,  executable, currentClass);
    }

    /**
     * Helpermethod for {@link #checkNonStaticMethodCall(Method, Executable, Class)} to manage
     * the methodcall matching.
     * @param matcher A matcher to find the object call.
     * @param methodAsString The method, which gets watched as string.
     * @param method The method, which gets watched.
     * @param executable A constructor or a method.
     * @param currentClass The class, which gets inspected.
     * @return True if OBJECTNAME.METHODNAME() is defined in the class.
     */
    private boolean matchMethodCallViaObject(Matcher matcher, String methodAsString, Method method, Executable executable, Class<?> currentClass) {
        logger.info("- Starting to search a match for a method call.");
        logger.fine("Current checked class: " + currentClass.getSimpleName());
        logger.finer("Current checked method: " + method.getName());
        
        String objectName = "";
        while(matcher.find()) {
            final String match = matcher.group();
            objectName = getWord(match);

            Pattern pattern = Pattern.compile("\\([a-zA-Z0-9_,\s\\<\\?\\>\\[\\].]*\\)");
            Matcher parameterlistMatcher = pattern.matcher(methodAsString);
            final String parameterlist = parameterlistMatcher.find() ? parameterlistMatcher.group() : "";

            if(findLocalFieldList(methodAsString, objectName, method.getDeclaringClass()) ||
               findParameterList(parameterlist, executable, objectName, method.getDeclaringClass()) ||
               findFieldList(currentClass.getDeclaredFields(), objectName, method.getDeclaringClass())) return true;
        }
        logger.info("=> finished searching for a method call match.");

        return searchForSuperClass(currentClass, objectName, method.getDeclaringClass());
    }

    /**
     * Helpermethod for {@link #matchMethodCallViaObject(Matcher, String, Method, Executable, Class)} to find
     * a local field with the given object name and type.
     * @param methodAsString The method, which gets watched as string.
     * @param objectName The word before the ".", ":" or " " (Java Methodcall Syntax).
     * @param declaringClass The class of the method, which gets watched.
     * @return True if there is a local field variable defined with the given objectname and type.
     */
    private boolean findLocalFieldList(String methodAsString, String objectName, Class<?> declaringClass) {
        logger.info("- Starting to search for a local field with the given object name and type.");
        logger.fine("Current checked class: " + declaringClass.getSimpleName());
        logger.finer("Current checked method: " + methodAsString);
        System.out.println(objectName);
        final Pattern pattern = Pattern.compile("[a-zA-Z0-9_]+\s+" + objectName); // => "TYPE OBJECTNAME"
        final Matcher matcher = pattern.matcher(methodAsString);

        while(matcher.find()) {
            String match = matcher.group();

            String objectType = getWord(match);

            if(checkIfSameType(objectType, declaringClass)) return true;
        }

        logger.info("=> finished searching for a local field with the given object name and type. No field found");

        return false;
    }

    /**
     * Helpermethod for {@link #matchMethodCallViaObject(Matcher, String, Method, Executable, Class)} to find
     * a parameter field with the given object name and type. Solved with using a parameterlist string.
     * In java reflection, there would also be a "Parameter" class to get parameters from an Executable.
     * But mostly the compiler doesn't save parameter name inside a class file,
     * so ".isNamePresent()" gives false all the time.
     * @param parameterlist A parameterlist string of the method signature.
     * @param executable A constructor or a method.
     * @param objectName The word before the ".", ":" or " " (Java Methodcall Syntax).
     * @param declaringClass The class of the method, which gets watched.
     * @return True if there is a parameter field variable defined with the given objectname and type.
     */
    private boolean findParameterList(String parameterlist, Executable executable, String objectName, Class<?> declaringClass) {
        logger.info("- Starting to search for a parameter field with the given object name and type.");
        logger.fine("Current checked class: " + declaringClass.getSimpleName());

        Pattern pattern = Pattern.compile("([a-zA-Z0-9_]+)\s+" + objectName);
        Matcher matcher = pattern.matcher(parameterlist);

        if(matcher.find()) {
            String typeName = matcher.group(1);

            if(checkIfSameType(typeName, declaringClass)) return true;
        }

        logger.info("=> finished searching for a parameter: no Parameter found");

        return false;
    }

    /**
     * Helpermethod for {@link #matchMethodCallViaObject(Matcher, String, Method, Executable, Class)} to find
     * a field variable with the given object name and type.
     * @param fields The fields of the current searching class.
     * @param objectName The word before the ".", ":" or " " (Java Methodcall Syntax).
     * @param declaringClass The class of the method, which gets watched.
     * @return True if there is a field variable defined with the given objectname and type.
     */
    private boolean findFieldList(Field[] fields, String objectName, Class<?> declaringClass) {
        logger.info("- Starting to search for a field variable with the given object name and type.");

        for(Field field : fields) {
            if(field != null && field.getName().equals(objectName) && checkIfSameType(field.getType().getSimpleName(), declaringClass)) return true;
        }
        logger.info("=> finished searching for a field.");
        return false;
    }

    /**
     * Helpermethod for {@link #matchMethodCallViaObject(Matcher, String, Method, Executable, Class)} to check superclasses.
     * If a methodcall couldn't find any field variables it could be possible that the current class is a subclass
     * or a memberclass. This must be checked.
     *
     * Note:
     * Anonymous an local class shouldn't be checked. This is a feature for future releases.
     * With reflection we can use "getEnclosingMethod()" or constructor to analyse
     * if there is a datafield definition locally. Currently if there is a local or anonymous class inside a method
     * it gets parsed as a statementblock in this method and handled as a method block.
     * @param currentClass The class, which gets checked.
     * @return True if there is a field inside the class hirarchy.
     */
    private boolean searchForSuperClass(Class<?> currentClass, String objectName, Class<?> declaringClass) {
        logger.info("- Starting to search for superclasses.");
        logger.fine("Current checked class: " + currentClass.getSimpleName());

        Class<?> memberClass = currentClass.getDeclaringClass();
        Class<?> superClass = currentClass.getSuperclass();
        Class<?>[] superInterfaces = currentClass.getInterfaces();

        while(memberClass != null) { // check memberclasses
            if(findFieldList(memberClass.getDeclaredFields(), objectName, declaringClass)) return true;

            memberClass = memberClass.getDeclaringClass();
        }

        while(superClass != null) { // check superclasses
            Field[] fields = superClass.getDeclaredFields();
            Field[] filteredFields = new Field[fields.length];

            for(int i = 0; i < fields.length; i++) {
                if(!Modifier.isPrivate(fields[i].getModifiers())) filteredFields[i] = fields[i];
            }

            if(findFieldList(filteredFields, objectName, declaringClass)) return true;

            superClass = superClass.getSuperclass();
        }

        for(Class<?> superInterface : superInterfaces) { // check implemented interfaces
            logger.fine("Current checked interface: " + superInterface.getSimpleName());

            do {
                Field[] fields = superInterface.getDeclaredFields();
                Field[] filteredFields = new Field[fields.length];

                for(int i = 0; i < fields.length; i++) {
                    if(!Modifier.isPrivate(fields[i].getModifiers())) filteredFields[i] = fields[i];
                }

                if(findFieldList(filteredFields, objectName, declaringClass)) return true;
            } while(superInterface.getInterfaces().length != 0);
        }
        logger.info("=> finished searching for superclasses.");
        return false;
    }

    /**
     * Helpermethod for {@link #findLocalFieldList(String, String, Class)} and {@link #findParameterList(Executable, String, Class)} to check if the founded objectype has the same type as the starting class (and its super types).
     * This method could be extended to also analyse implemented interfaces.
     * @param objectType The founded objecttype as string in the method body.
     * @param declaringClass The class of the method, which gets watched.
     * @return True if the objecttype are the same.
     */
    private boolean checkIfSameType(String objectType, Class<?> declaringClass) {
        logger.info("- Starting to check for same type.");

        while(declaringClass != null) {
            if(declaringClass.getSimpleName().equals(objectType)) return true;

            declaringClass = declaringClass.getSuperclass();
        }
        logger.info("=> Completed check for same type.");

        return false;
    }

    /**
     * Helpermethod for {@link #checkMethodAccess(Method, Set)} to get the objectname before the point (call of the method).
     * For example: "test.t()" -> "test" gets extracted and returned.
     * @param statement The statement, which gets inspected.
     * @return The word before the ".", ":" or " " (Java Methodcall Syntax).
     */
    private String getWord(String statement) {
        logger.info("- Starting to get objective name.");

        String word = "";

        for(int i = 0; i < statement.length(); i++) {
           final char currentChar = statement.charAt(i);

           if(currentChar == ' ' || currentChar == '.' || currentChar == ':') break;

           word += String.valueOf(currentChar);
        }
        logger.info("=> Completed objective name.");

        return word;
    }

    /**
     * Checks if a composition of a warning is already in the specified / given warning list.
     * @param currentClass The class object, which gets checked.
     * @param currentField The member object, which get checked.
     * @return True if its already in the list.
     */
    private boolean isAlreadyAWarning(Class<?> currentClass, Member currentMember, ListProperty<CodePositionWarning> warnings) {
        logger.info("- Checking for existing warning.");
        for(CodePositionWarning warning : warnings) {
            if(currentClass.equals(warning.classObj()) &&
            currentMember.equals(warning.memberObj())) return true;
        }

        return false;
    }

    /**
     * Cleans everything before starting a new test, so that it can update it on the new test state.
     */
    private void clear() {
        fieldDependenceWarnings.clear();
        fieldBidirectionalWarnings.clear();
        accessModificatorWarnings.clear();
    }
}

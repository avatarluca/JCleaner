package team03.mudecowboys.projekt2.jcleaner.model.analyzer;

import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.beans.property.ListProperty;


/**
 * Abstract class which manages each detector class.
 */
public abstract class Detector {
  private static final Logger logger = Logger.getLogger(CouplingDetector.class.getCanonicalName());
  private Map<String, String> parsedClasses;
  
  protected Set<Class<?>> classes;


  public void setUpInputData(Set<Class<?>> classes, String classesAsString) {
    this.parsedClasses = new HashMap<>();
    this.classes = classes;

    parseClasses(classesAsString);
  }


  /**
   * Abstract method to start a detector.
   */
  public abstract void run() throws Exception;


  /**
   * Parses the results of the detector.
   */
  public abstract HashMap<String, HashMap<String, ListProperty>> getResults();
  

  /**
   * Checks if a given class is a primitive type class wrapped in a wrapper class or just a primitive class. 
   * This is used mainly to check if a field variable isn't a datatype variable.
   * @param type The class which gets checked.
   * @return True if the given type is a primitive type.
   */
  public boolean isWrapperOrPrimitiveType(Class<?> type) {
    final Set<Class<?>> primitiveClassList = new HashSet<Class<?>>();
    primitiveClassList.add(Boolean.class);
    primitiveClassList.add(Character.class);
    primitiveClassList.add(Byte.class);
    primitiveClassList.add(Short.class);
    primitiveClassList.add(Integer.class);
    primitiveClassList.add(Long.class);
    primitiveClassList.add(Float.class);
    primitiveClassList.add(Double.class);
    primitiveClassList.add(Void.class);
    primitiveClassList.add(boolean.class);
    primitiveClassList.add(char.class);
    primitiveClassList.add(byte.class);
    primitiveClassList.add(short.class);
    primitiveClassList.add(int.class);
    primitiveClassList.add(long.class);
    primitiveClassList.add(float.class);
    primitiveClassList.add(double.class);
    primitiveClassList.add(void.class);

    logger.finest(String.format("Class %s is a %s.",  type.getSimpleName(), (primitiveClassList.contains(type) ? "primitive type" : "wrapper type")));

    return primitiveClassList.contains(type);
  }

  /**
   * Gets the suitable txt file for the class and extracts the given constructor or method.  
   * @param executable A constructor or method.
   * @return The extracted constructor or method.
   */
  public String getExecutableAsString(Executable executable) {
    String exeName = "";
    if(executable instanceof Method) exeName = executable.getName();
    else exeName = executable.getDeclaringClass().getSimpleName();

    final Pattern pattern = Pattern.compile("(([a-zA-Z0-9_\\<\\>]+\s+)*|(\s*))" + exeName + "\s*\\([a-zA-Z0-9_,\s\\<\\?\\>\\[\\].]*\\)\s*(|throws\s*[a-zA-Z0-9_\\<\\>,]+\s*)\\{");

    final String declaringClassAsString = executable.getDeclaringClass().getSimpleName();
    final String classAsTxt = parsedClasses.get(declaringClassAsString);

    final Matcher matcher = pattern.matcher(classAsTxt);

    final Parameter[] parameters = executable.getParameters();

    String executableAsString = "";

    while(matcher.find()) {
      final String methodSignature = matcher.group();
      boolean containsAllParameters = true;

      for(Parameter parameter : parameters) {
        if(!methodSignature.contains(parameter.getType().getSimpleName())) {
          containsAllParameters = false;
          break;
        }
      }

      if(containsAllParameters) {
        executableAsString += extractBody(matcher.start(), classAsTxt);
        break;
      }
    }

    logger.finest(String.format("Extracted executable (%s): %s.", executable.getName(), executableAsString));
    
    return executableAsString;
  }


  /**
   * Extracting the methodbody or classbody using a simple pushdown automaton emulator to read the brackets.
   * @param index The start index of the extraction.
   * @param classAsTxt The class as text / string.
   * @return The extracted body.
   */
  public String extractBody(int index, String classAsTxt) {
    final char BRACKETS_OPEN = '{', BRACKETS_CLOSE = '}', INIT_SYMBOL = '$'; // stackalphabet

    final List<Character> stack = new ArrayList<>();
    stack.add(INIT_SYMBOL);
   
    String body = "";
    boolean firstFound = false;
    for(int i = index; i < classAsTxt.length(); i++) {
      final char currentSymbol = classAsTxt.charAt(i);
      
      if(currentSymbol == BRACKETS_OPEN) {
        stack.add(BRACKETS_OPEN); // push
        firstFound = true;
      }
      else if(currentSymbol == BRACKETS_CLOSE) stack.remove(stack.size() - 1); // pop

      body += String.valueOf(currentSymbol);

      if(stack.get(stack.size() - 1) == INIT_SYMBOL && firstFound) return body; // read
    }

    logger.finest(String.format("Extracted body (starting at index %d): %s of class (as txt): %s.", index, body, classAsTxt));

    return body;
  }

  /**
   * Minimal parsing the given classes to {@link #parsedClasses}.
   * It works currently just for classes in the same package. 
   * In the future we could add the full packagename with classname as a hashmap key.
   * For this we also have to change each check to handle full packagenames.
   * @param classes A string of all classes, which the user has entered.
   */
  private void parseClasses(String classesAsString) {
    for(Class<?> currentClass : classes) {
      final Pattern pattern = Pattern.compile("(([a-zA-Z0-9_\\<\\>]+\s+)*|(\s*))(class|enum|interface|record)\s*" + currentClass.getSimpleName() + "\s([a-zA-Z0-9_,\s\\<\\>\\?\\[\\].]*|)\s*\\{");
      final Matcher matcher = pattern.matcher(classesAsString);

      String classBody = "";
      if(matcher.find()) {
        classBody = extractBody(matcher.start(), classesAsString);
      }

      Class<?>[] memberClasses = currentClass.getDeclaredClasses();

      classBody = removeMemberClasses(classBody, memberClasses, currentClass);

      parsedClasses.put(currentClass.getSimpleName(), classBody);
    }
  }

  /**
   * Removes memberclasses from a given string.
   * Anonymous and local classes don't get extracted. For this we have to define currentClass.isAnonymous() 
   * and currentClass.isLocalClass() (from java.lang.Class).
   * @param classBody The class body as a string.
   * @param memberClasses All member classes of a given class.
   * @param currentClass The given super class.
   * @return A new string, without memberclasses.
   */
  private String removeMemberClasses(String classBody, Class<?>[] memberClasses, Class<?> currentClass) {
    logger.finest(String.format("Class body before removing member class: %s.", classBody));

    for(Class<?> member : memberClasses) {
      if(member.isMemberClass()) {
        final Pattern pattern = Pattern.compile("(([a-zA-Z0-9_\\<\\>]+\s+)*|(\s*))(class|enum|interface|record)\s*" + member.getSimpleName() + "\s([a-zA-Z0-9_,\s\\<\\>\\?\\[\\].]*|)\s*\\{");
        Matcher matcher = pattern.matcher(classBody);

        if(matcher.find()) {
          String body = extractBody(matcher.start(), classBody);

          classBody = classBody.substring(0, matcher.start()) + classBody.substring(matcher.start() + body.length(), classBody.length());
        }
      }
    }
    
    logger.finest(String.format("Class body after removing member class: %s.", classBody));

    return classBody;
  }
}
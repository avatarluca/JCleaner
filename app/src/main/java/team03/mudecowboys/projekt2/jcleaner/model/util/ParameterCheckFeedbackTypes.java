package team03.mudecowboys.projekt2.jcleaner.model.util;

import java.util.Arrays;


/**
 * Used to make a choice between the cohesion feedback types of the amount of parameters a method can have.
 */
public enum ParameterCheckFeedbackTypes {
    BUILDER_OBJECT("Builder object"),
    ARGUMENT_OBJECT("Argument object"),
    BOTH("Both"), 
    NONE("None");

    private final String name;


    private ParameterCheckFeedbackTypes(final String name) {
        this.name = name;
    }


    public String getName() {
        return this.name;
    }
    

    /**
     * Collects all names from the enum values.
     * @return An array with the names.
     */
    public static String[] getAllNames() {
        ParameterCheckFeedbackTypes[] parameterCheckFeedbackTypes = ParameterCheckFeedbackTypes.values();

        return Arrays.stream(parameterCheckFeedbackTypes)
                     .map(ParameterCheckFeedbackTypes::getName)
                     .toArray(String[]::new);
    }
}
package team03.mudecowboys.projekt2.jcleaner.model.util;

import java.util.Arrays;


/**
 * Used to make a choice between the cohesion check types to get the method length.
 */
public enum ExecutableLengthCheckTypes {
    SEMICOLON_COUNTER("Semicolon counter", ';'),
    LINEBREAK_COUNTER("Linebreak counter", '\n');

    private final String name;
    private final char symbol;


    private ExecutableLengthCheckTypes(final String name, final char symbol) {
        this.name = name;
        this.symbol = symbol;
    }

    
    public String getName() {
        return this.name;
    }

    public char getSymbol() {
        return this.symbol;
    }


    /**
     * Collects all names from the enum values.
     * @return An array with the names.
     */
    public static String[] getAllNames() {
        ExecutableLengthCheckTypes[] executableLengthCheckTypes = ExecutableLengthCheckTypes.values();

        return Arrays.stream(executableLengthCheckTypes)
                     .map(ExecutableLengthCheckTypes::getName)
                     .toArray(String[]::new);
    }
}
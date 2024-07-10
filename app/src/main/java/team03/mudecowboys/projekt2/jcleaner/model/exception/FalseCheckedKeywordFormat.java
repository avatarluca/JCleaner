package team03.mudecowboys.projekt2.jcleaner.model.exception;


/**
 * Custom exception, if the nested keyword format given in the settings is invalid.
 */
public class FalseCheckedKeywordFormat extends Exception {
    public FalseCheckedKeywordFormat(String message) {
        super(message);
    }
    public FalseCheckedKeywordFormat() {
        super();
    }
}

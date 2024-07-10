package team03.mudecowboys.projekt2.jcleaner.model.exception;


/**
 * Custom exception, if there are more then one defined package in a directory.
 * For future releases we can check more the one package by referencing each class with the full packagename and classname.
 * In addition we should then add a recursive classloader which loads classes in other packages (= directories) 
 * (see Detector#parseClasses and https://github.zhaw.ch/PM2-IT22bWIN-ruiz-kars/team03-MudeCowboys-projekt2-JCleaner/issues/17).
 */
public class TooManyPackagesException extends Exception {
    public TooManyPackagesException(String message) {
        super(message);
    }
    public TooManyPackagesException() {
        super();
    }
}

package team03.mudecowboys.projekt2.jcleaner.model.db.util;


/**
 * Specification interface to manage all db handlers, which saves the state of the settings.
 * So in future, we could just add a sql db handler (to save the setting states on our server),
 * which implements this interface (so we don't have to change the callers).
 */
public interface SettingsDataSpecification {
    /**
     * Updates the setting property.
     * If the record (property) wasn't found, it doesn't update the property (because it isn't available).
     * @param property The key of the property.
     * @param data The value of the property. 
     */
    public abstract void update(String property, String data);

    /**
     * Gets the value of a setting with the respective key (id).
     * @param id The key of the property.
     * @return A string with the value of the property.
     */
    public abstract String getValueByKey(String id);
}
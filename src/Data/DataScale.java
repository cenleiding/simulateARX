package Data;

import java.io.Serializable;

/**
 * This class represents different scales of measure. Note that the order of entries in this enum is important.
 * 
 * @author Fabian Prasser
 * @author Florian Kohlmayer
 */
public enum DataScale implements Serializable {
    
    NOMINAL("Nominal scale"),
    
    ORDINAL("Ordinal scale"),
    
    INTERVAL("Interval scale"),
    
    RATIO("Ratio scale");
    
    /** Label */
    private final String label;
    
    /**
     * Constructor
     * @param label
     */
    private DataScale(String label) {
        this.label = label;
    }
    
    /**
     * Returns whether this scale provides at least the properties of the given scale.
     * @param other
     * @return
     */
    public boolean provides(DataScale other) {
        return this.compareTo(other) >= 0;
    }
    
    @Override
    public String toString() {
        return label;
    }
}
/**
 * Field - Represents a database table field/column
 * 
 * This class encapsulates the properties of a database column including
 * name, data type, constraints, and provides methods for SQL generation.
 * 
 * @author John Hernandez
 * @version 2.0
 */
public class Field {
    
    private String name;
    private String dataType;
    private boolean isPrimaryKey;
    private boolean isNotNull;
    private Integer maxLength; // for VARCHAR types
    
    /**
     * Constructor for basic field
     * 
     * @param name Field name
     * @param dataType SQL data type (INTEGER, VARCHAR, TEXT, etc.)
     */
    public Field(String name, String dataType) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Field name cannot be null or empty");
        }
        if (dataType == null || dataType.trim().isEmpty()) {
            throw new IllegalArgumentException("Data type cannot be null or empty");
        }
        
        this.name = name.trim();
        this.dataType = dataType.trim().toUpperCase();
        this.isPrimaryKey = false;
        this.isNotNull = false;
    }
    
    /**
     * Constructor with all basic options
     * 
     * @param name Field name
     * @param dataType SQL data type
     * @param isPrimaryKey Whether this field is a primary key
     * @param isNotNull Whether this field allows NULL values
     * @param isUnique Whether this field must have unique values (unused in current version)
     * @param defaultValue Default value for the field (unused in current version)
     * @param maxLength Maximum length for VARCHAR/CHAR types
     */
    public Field(String name, String dataType, boolean isPrimaryKey, 
                 boolean isNotNull, boolean isUnique, String defaultValue, Integer maxLength) {
        this(name, dataType);
        this.isPrimaryKey = isPrimaryKey;
        this.isNotNull = isNotNull || isPrimaryKey; // Primary keys are automatically NOT NULL
        this.maxLength = maxLength;
    }
    
    /**
     * Sets this field as a primary key
     * Primary keys are automatically set to NOT NULL
     * 
     * @return this Field object for method chaining
     */
    public Field primaryKey() {
        this.isPrimaryKey = true;
        this.isNotNull = true; // Primary keys are automatically NOT NULL
        return this;
    }
    
    /**
     * Sets this field to NOT NULL
     * 
     * @return this Field object for method chaining
     */
    public Field notNull() {
        this.isNotNull = true;
        return this;
    }
    
    /**
     * Sets the maximum length for VARCHAR/CHAR fields
     * 
     * @param length Maximum length
     * @return this Field object for method chaining
     */
    public Field maxLength(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Max length must be positive");
        }
        this.maxLength = length;
        return this;
    }
    
    /**
     * Generates SQL fragment for this field
     * 
     * @return SQL string representation of this field
     */
    public String toSQL() {
        StringBuilder sql = new StringBuilder();
        sql.append(name).append(" ");
        
        // Handle data type with length
        if (maxLength != null && (dataType.equalsIgnoreCase("VARCHAR") || 
                                  dataType.equalsIgnoreCase("CHAR"))) {
            sql.append(dataType).append("(").append(maxLength).append(")");
        } else {
            sql.append(dataType);
        }
        
        // Add constraints
        if (isPrimaryKey) {
            sql.append(" PRIMARY KEY");
        } else if (isNotNull) {
            sql.append(" NOT NULL");
        }
        
        return sql.toString();
    }
    
    /**
     * Returns a string representation of this field
     * 
     * @return String description of the field
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Field{name='").append(name).append("'");
        sb.append(", dataType='").append(dataType);
        if (maxLength != null) {
            sb.append("(").append(maxLength).append(")");
        }
        sb.append("'");
        if (isPrimaryKey) sb.append(", PRIMARY KEY");
        if (isNotNull && !isPrimaryKey) sb.append(", NOT NULL");
        sb.append("}");
        return sb.toString();
    }
    
    // Getters
    public String getName() { 
        return name; 
    }
    
    public String getDataType() { 
        return dataType; 
    }
    
    public boolean isPrimaryKey() { 
        return isPrimaryKey; 
    }
    
    public boolean isNotNull() { 
        return isNotNull; 
    }
    
    public Integer getMaxLength() { 
        return maxLength; 
    }
    
    /**
     * Checks if this field is compatible with VARCHAR operations
     * 
     * @return true if field is VARCHAR or CHAR type
     */
    public boolean isStringType() {
        return dataType.equalsIgnoreCase("VARCHAR") || 
               dataType.equalsIgnoreCase("CHAR") || 
               dataType.equalsIgnoreCase("TEXT");
    }
    
    /**
     * Checks if this field is a numeric type
     * 
     * @return true if field is numeric type
     */
    public boolean isNumericType() {
        return dataType.equalsIgnoreCase("INTEGER") || 
               dataType.equalsIgnoreCase("INT") ||
               dataType.equalsIgnoreCase("BIGINT") ||
               dataType.equalsIgnoreCase("DECIMAL") ||
               dataType.equalsIgnoreCase("NUMERIC") ||
               dataType.equalsIgnoreCase("REAL") ||
               dataType.equalsIgnoreCase("DOUBLE");
    }
}

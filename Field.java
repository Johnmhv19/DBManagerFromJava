
public class Field {
    private String name;
    private String dataType;
    private boolean isPrimaryKey;
    private boolean isNotNull;
    /*private boolean isUnique;
    private String defaultValue;*/
    private Integer maxLength; // for VARCHAR types
    
    // Constructor for basic field
    public Field(String name, String dataType) {
        this.name = name;
        this.dataType = dataType;
        this.isPrimaryKey = false;
        this.isNotNull = false;
        //this.isUnique = false;
    }
    
    // Constructor with all options
   /* public Field(String name, String dataType, boolean isPrimaryKey, 
                 boolean isNotNull, boolean isUnique, String defaultValue, Integer maxLength) {*/
    public Field(String name, String dataType, boolean isPrimaryKey, 
                boolean isNotNull, boolean isUnique, String defaultValue, Integer maxLength) {
        this.name = name;
        this.dataType = dataType;
        this.isPrimaryKey = isPrimaryKey;
        this.isNotNull = isNotNull;
        /*this.isUnique = isUnique;
        this.defaultValue = defaultValue;*/
        this.maxLength = maxLength;
    }
    
    // Builder pattern methods for fluent API
    public Field primaryKey() {
        this.isPrimaryKey = true;
        this.isNotNull = true; // Primary keys are automatically NOT NULL
        return this;
    }
    
    public Field notNull() {
        this.isNotNull = true;
        return this;
    }
    
   /* public Field unique() {
        this.isUnique = true;
        return this;
    }
    
    public Field defaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }*/
    
    public Field maxLength(int length) {
        this.maxLength = length;
        return this;
    }
    
    // Generate SQL fragment for this field
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
        }
        if (isNotNull && !isPrimaryKey) {
            sql.append(" NOT NULL");
        }
     /*   if (isUnique && !isPrimaryKey) {
            sql.append(" UNIQUE");
        }
        if (defaultValue != null) {
            sql.append(" DEFAULT ").append(defaultValue);
        }*/
        
        return sql.toString();
    }
    
    // Getters and setters...
    public String getName() { return name; }
    public String getDataType() { return dataType; }
    public boolean isPrimaryKey() { return isPrimaryKey; }
    
}
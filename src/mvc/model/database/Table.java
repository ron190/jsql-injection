package mvc.model.database;

/**
 * Define a Table, e.g is sent to the view by the model after injection
 * Allow to traverse upward to its corresponding database  
 */
public class Table extends ElementDatabase {
	// The database that contains the current column
	public Database parentDatabase;
	
	// The number of rows in the table
    public String rowCount;

	// Define the table label, number of rows and parent database
    public Table(String newTableName, String newRowCount, Database newParentDatabase) {
    	this.elementValue = newTableName;
    	this.rowCount = newRowCount;
    	this.parentDatabase = newParentDatabase;
    }

    // Return the parent database
	@Override
	public ElementDatabase getParent() {
		return this.parentDatabase;
	}

	// Return the number of rows in the table
	@Override
	public int getCount() {
		return Integer.parseInt(rowCount);
	}

	/**
	 * A readable label for the table, with number of rows, displayed by the view
	 * If parent database is the system information_schema, number of rows is unknown
	 * e.g my_table (7 rows)
	 */
	@Override
	public String toFormattedString() {
		return this.elementValue + " ("+
        		((parentDatabase+"").equals("information_schema")?"?":rowCount)
        	+" rows)";
	}
}

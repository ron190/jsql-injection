package mvc.model.database;

public class Column extends ElementDatabase {
	public Table parentTable;

    public Column(String newColumnName, Table newTableName) {
    	this.elementValue = newColumnName;
    	this.parentTable = newTableName;
    }

	@Override
	public ElementDatabase getParent() {
		return parentTable;
	}

	@Override
	public int getCount() {
		return 0;
	}

	@Override
	public String toFormattedString() {
		return toString();
	}
}


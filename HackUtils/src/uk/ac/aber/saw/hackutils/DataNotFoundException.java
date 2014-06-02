package uk.ac.aber.saw.hackutils;
/**
* This exception is used when the SAWDatabaseHelper and associated classes
* cannot find any data matching the criteria requested.
*
* It is really just a place holder, and doesn't have any "real" functionality
* other than to be an identifier when thrown, that the error was actually
* related to a lack of data rather than something else.
*
* @author Michael Clarke <mfc1@aber.ac.uk>
* 
* @version 0.1
*/
public class DataNotFoundException extends Exception {
	
	private static final long serialVersionUID = 1L;

	String tableName;
	String [] whereArgs;
	String columnName;
	String whereKey;

	public DataNotFoundException() {
		this.tableName = "";
		this.columnName = "";
		this.whereKey = "";
		this.whereArgs = new String [] { "" };
	}

	public DataNotFoundException(String tableName, String columnName,
										String whereKey, String[] whereArgs) {

		this.tableName = tableName;
		this.whereArgs = whereArgs;
		this.columnName = columnName;
		this.whereKey = whereKey;

	}

	public String toString() {

		String str = "SELECT " + columnName + " FROM " + tableName +
												" WHERE " + whereKey + "(";

		for (int i = 0; i < whereArgs.length; i++) {
			str += whereArgs[i] + ", ";
		}

		str += ")";

		return str;

	}

}
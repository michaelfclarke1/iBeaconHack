package uk.ac.aber.saw.hackutils;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * A class containing useful Database functions for the SAW
 * (Software Alliance Wales) iBeacon hack.
 * 
 * Example usage, assuming you have two databases in the /assets folder
 * of your project:
 * 
 *  /assets/mydatabase.sqlite
 *  /assets/myotherdatabase.sqlite
 *  
 *  <code>
 *  SAWDatabaseHelper dbOne = SAWDatabaseHelper.getInstance(
 *  								getApplicationContext(), "mydatabase");
 *  
 *  SAWDatabaseHelper dbTwo = SAWDatabaseHelper.getInstance(
 *  							getApplicationContext(), "myotherdatabase");
 *  <code>
 * 
 * After getting hold of the databases, queries map as follows:
 *
 * "SELECT column FROM table WHERE value=a"
 * 
 * <code>
 * try {
 * 
 * 	String result = dbOne.getStringWithQuery("table", "column", 
 * 										"value=?", new String[] { "a" } );
 * 
 * } catch (DataNotFoundException dnfe) {
 * 
 * 	// Do something about the error.
 * 
 * }
 * </code>
 * 
 * A more complex query, where there are multiple results, and multiple WHERE
 * clauses, and the data should be ordered by a column:
 * 
 * "SELECT column FROM table WHERE value1=a AND value2=b ORDER BY value3"
 * 
 * <code>
 * try{
 * 
 * 	ArrayList<String> results = 
 * 			dbTwo.getOrderedStringArrayWithQuery("table", "column",
 * 				"value1=? AND value2=?", new String[] { "a", "b" }, "value3");
 * 
 * } catch (DataNotFoundException dnfe) {
 * 
 *	// Do something about the error.
 *
 * }
 * </code>
 * 
 * @author Michael Clarke <mfc1@aber.ac.uk>
 * @version 0.1
 *
 */
public class SAWDatabaseHelper extends SQLiteOpenHelper {
	
	private static String TAG = "SAWDatabaseHelper";
	private static int DATABASE_VERS = 1;
	private static String DATABASE_PATH = null;
	private static HashMap<String, SAWDatabaseHelper> instances;
	
	private String dbName;
	private SQLiteDatabase db = null;
	private Context context;
	

	/**
	 * Used to load a new database instance. 
	 *
	 * Example usage, assuming you have two databases in the /assets folder
	 * of your project:
	 * 
	 *  /assets/mydatabase.sqlite
	 *  /assets/myotherdatabase.sqlite
	 *  
	 * <code>
	 *  SAWDatabaseHelper dbOne = SAWDatabaseHelper.getInstance(
	 *  								getApplicationContext(), "mydatabase");
	 *  SAWDatabaseHelper dbTwo = SAWDatabaseHelper.getInstance(
	 *  							getApplicationContext(), "myotherdatabase");
	 * </code>
	 * 
	 * @param dbName The file name of the .sqlite file in the "assets" folder.
	 * 
	 * @param context The application or activity/fragment context.
	 * 
	 * @return The existing database instance if one already exists for the
	 *         given database, otherwise a new instance for the given .sqlite
	 *         database.
	 */
	public static SAWDatabaseHelper
						getInstance(Context context, String dbName) {
		
		if (instances == null)
			instances = new HashMap<String, SAWDatabaseHelper>();
	
		if (DATABASE_PATH == null) {
			
			if(android.os.Build.VERSION.SDK_INT >= 17)
				DATABASE_PATH = 
					context.getApplicationInfo().dataDir + "/databases/";
			else
				DATABASE_PATH = 
					"/data/data/" + context.getPackageName() + "/databases/";
			
		}
		
		SAWDatabaseHelper instance = instances.get(dbName);
		if (instance == null) {
			instance = new SAWDatabaseHelper(context, dbName + ".sqlite");
			instances.put(dbName, instance);
		}
		
		return instance;
		
	}
	
	/**
	 * This method is used to get a string from the database.
	 * 
	 * @param tableName The table in the database that holds the
	 *                  string we're after.
	 * 
	 * @param columnName The column name we're after.
	 * 
	 * @param where Any WHERE clauses, i.e. "id=? AND name=?, etc."
	 * 
	 * @param whereArgs The arguments that match the WHERE clause,
	 * 				    in a String array.
	 * 
	 * @return The found string, if it is in the database.
	 * 
	 * @throws DataNotFoundException if the data if not in the database,
	 *         or if an error occurs.
	 */
	public String getStringWithQuery(String tableName, String columnName,
				String where, String whereArgs[]) throws DataNotFoundException {

		String result = null;
		Cursor cursor = this.db.query(tableName, new String[] { columnName },
											where, whereArgs, null, null, null);

		if (cursor != null) {
			if (cursor.moveToNext()) {
				int index = cursor.getColumnIndexOrThrow(columnName);
				if (index != -1) {
					result = cursor.getString(index);
				}
			}
			cursor.close();
		}
		
		if (result == null) 
			throw new DataNotFoundException(
					tableName, columnName, where, whereArgs);

		return result;

	}
	
	/**
	 * This method is used to get a single character from the SQLite database.
	 * It can also be used to get just the first character of a string entry in
	 * the database.
	 *
	 * @param tableName The table in the database that holds the character
	 *                  we're after.
	 *                 
	 * @param columnName The column name we're after.
	 * 
	 * @param where Any WHERE clauses, i.e. "id=? AND name=?, etc."
	 * 
	 * @param whereArgs The arguments that match the WHERE clause,
	 *                  in a String array.
	 *
	 * @return The found character, if it is in the database.
	 * 
	 * @throws DataNotFoundException if the data if not in the database,
	 *         or if an error occurs.
	 */
	public char getCharWithQuery(String tableName, String columnName,
				String where, String whereArgs[]) throws DataNotFoundException {

		return this.getStringWithQuery(tableName, columnName,
										where, whereArgs).toCharArray()[0];

	}
	
	/**
	 * This method is used to get an integer from the SQLite database.
	 *
	 * @param tableName The table in the database that holds the integer
	 *                  value we're after.
	 * 
	 * @param columnName The column name we're after.
	 * 
	 * @param where Any WHERE clauses, i.e. "id=? AND name=?, etc."
	 * 
	 * @param whereArgs The arguments that match the WHERE clause, in a
	 *                  String array.
	 *
	 * @return The found integer, if it is in the database.
	 *
	 * @throws DataNotFoundException if the data if not in the database,
	 * 		   or if an error occurs.
	 */
	public int getIntegerWithQuery(String tableName, String columnName,
			String where, String whereArgs[]) throws DataNotFoundException {

		Integer result = null;

		Cursor cursor = this.db.query(tableName, new String[] {columnName},
											where, whereArgs, null, null, null);
		if (cursor != null) {
			if (cursor.moveToNext()) {
				int index = cursor.getColumnIndexOrThrow(columnName);
				if (index != -1) {
					result = Integer.valueOf(cursor.getInt(index));
				}
			}
		}

		if (result == null) 
			throw new DataNotFoundException(
						tableName, columnName, where, whereArgs);

		return (int)result;

	}
	
	/**
	 * This method is used to the a count of the number of items matching the
	 * requested query string.
	 *
	 * @param tableName The table name in the database to query.
	 * 
	 * @param columnName The column name in the table that holds the data
	 * 					 we want.
	 * 
	 * @param where Any specific constraints, i.e. "id=?", otherwise null.
	 * 
	 * @param whereArgs The argument strings that match the "where" param,
	 *                  otherwise null.
	 *
	 * @return An integer with the number of items that matched the search
	 *         criteria.
	 *
	 * @throws DataNotFoundException If no data is found, we throw this.
	 */
	public int getCountWithQuery(String tableName, String columnName,
										String where, String whereArgs[])
												throws DataNotFoundException {

		Cursor cursor = this.db.query(tableName, new String[] {columnName},
										where, whereArgs, null, null, null);


		if (cursor == null) throw new DataNotFoundException(
				tableName, columnName, where, whereArgs);

		return cursor.getCount();

	}
	
	/**
	 * This method is used to get a whole array of string values from the
	 * database.
	 *
	 * @param tableName The table name in the database to query.
	 * 
	 * @param columnName The column name in the table that holds the data
	 *                   we want.
	 *                   
	 * @param where Any specific constraints, i.e. "id=?", otherwise null.
	 * 
	 * @param whereArgs The argument strings that match the "where" param,
	 *                  otherwise null.
	 *
	 * @return An array list of strings that match the requested criteria.
	 *
	 * @throws DataNotFoundException If no data is found, we throw this.
	 */
	public ArrayList<String> getStringArrayWithQuery(String tableName,
						String columnName, String where, String whereArgs[])
												throws DataNotFoundException {

		ArrayList<String> result = null;

		Cursor cursor = this.db.query(tableName, new String[] {columnName},
										where, whereArgs, null, null, null);

		if (cursor != null) {

			result = new ArrayList<String>();

			while (cursor.moveToNext()) {
				int index = cursor.getColumnIndexOrThrow(columnName);
				if (index != -1) {
					result.add(cursor.getString(index));
				}
			}

		}

		if (result == null) 
			throw new DataNotFoundException(
								tableName, columnName, where, whereArgs);

		return result;

	}

	/**
	 * This method is used to get a whole array of integer values from the
	 * database.
	 *
	 * @param tableName The table name in the database to query.
	 * 
	 * @param columnName The column name in the table that holds the data
	 *                   we want.
	 *                   
	 * @param where Any specific constraints, i.e. "id=?", otherwise null.
	 * 
	 * @param whereArgs The argument strings that match the "where" param,
	 *                  otherwise null.
	 *
	 * @return An array list of integers that match the requested criteria.
	 *
	 * @throws DataNotFoundException If no data is found, we throw this.
	 */
	public ArrayList<Integer> getIntegerArrayWithQuery(String tableName,
						String columnName, String where, String whereArgs[])
												throws DataNotFoundException {

		ArrayList<Integer> result = null;

		Cursor cursor = this.db.query(tableName, new String[] {columnName},
										where, whereArgs, null, null, null);

		if (cursor != null) {

			result = new ArrayList<Integer>();

			while (cursor.moveToNext()) {
				int index = cursor.getColumnIndexOrThrow(columnName);
				if (index != -1) {
					result.add(Integer.valueOf(cursor.getInt(index)));
				}
			}

		}

		if (result == null) 
			throw new DataNotFoundException(
								tableName, columnName, where, whereArgs);

		return result;

	}

	/**
	 * This method is used to get a whole array of string values from the
	 * database. The array will be ordered by the orderBy value.
	 *
	 * @param tableName The table name in the database to query.
	 * 
	 * @param columnName The column name in the table that holds the data
	 *                   we want.
	 *                   
	 * @param where Any specific constraints, i.e. "id=?", otherwise null.
	 * 
	 * @param whereArgs The argument strings that match the "where" param,
	 *                  otherwise null.
	 *                  
	 * @param orderBy The column to order values by.
	 *
	 * @return An array list of strings that match the requested criteria.
	 *
	 * @throws DataNotFoundException If no data is found, we throw this.
	 */
	public ArrayList<String> getOrderedStringArrayWithQuery(String tableName,
						String columnName, String where, String whereArgs[],
								String orderBy) throws DataNotFoundException {

		ArrayList<String> result = null;

		Cursor cursor = this.db.query(tableName, new String[] {columnName},
								where, whereArgs, null, null, "" +orderBy+"");

		if (cursor != null) {

			result = new ArrayList<String>();
			while (cursor.moveToNext()) {
				int index = cursor.getColumnIndexOrThrow(columnName);
				if (index != -1) {
					result.add(cursor.getString(index));
				}
			}
		}

		return result;

	}	

	/**
	 * This method is used to get a whole array of integer values from the
	 * database. The array will be ordered by the orderBy value.
	 *
	 * @param tableName The table name in the database to query.
	 * 
	 * @param columnName The column name in the table that holds the data
	 *                   we want.
	 *                   
	 * @param where Any specific constraints, i.e. "id=?", otherwise null.
	 * 
	 * @param whereArgs The argument strings that match the "where" param,
	 *                  otherwise null.
	 *                  
	 * @param orderBy The column to order values by.
	 *
	 * @return An array list of integers that match the requested criteria.
	 *
	 * @throws DataNotFoundException If no data is found, we throw this.
	 */
	public ArrayList<Integer> getOrderedIntegerArrayWithQuery(String tableName,
						String columnName, String where, String whereArgs[],
								String orderBy) throws DataNotFoundException {

		ArrayList<Integer> result = null;

		Cursor cursor = this.db.query(tableName, new String[] {columnName},
								where, whereArgs, null, null, "" +orderBy+"");

		if (cursor != null) {

			result = new ArrayList<Integer>();
			while (cursor.moveToNext()) {
				int index = cursor.getColumnIndexOrThrow(columnName);
				if (index != -1) {
					result.add(Integer.valueOf(cursor.getInt(index)));
				}
			}
		}

		return result;

	}
	
	private SAWDatabaseHelper(Context context, String dbName) {
		
		super(context, dbName, null, DATABASE_VERS);
		
		this.dbName = dbName;
		this.context = context;
		
		this.getReadableDatabase();
		this.close();
		
		try {	
			this.copyDatabase();
			Log.i(TAG, "Created database (" + dbName +") from asset folder.");
		} catch (Exception e) {
			Log.e(TAG, "Error creating database (" + dbName + ") from " + 
											"asset folder. " + e.toString());
		}

		try {
			this.db = SQLiteDatabase.openDatabase(DATABASE_PATH + dbName,
										null, SQLiteDatabase.OPEN_READONLY);
			Log.i(TAG, "Database (" + dbName + ") opened.");
		} catch (Exception e) {
			Log.e(TAG, "Error opening database " +
										"(" + dbName + "). " + e.toString());
		}
		
	}
	
	private void copyDatabase() throws IOException {

		InputStream is = context.getAssets().open(this.dbName);
		OutputStream os = new FileOutputStream(DATABASE_PATH + this.dbName);

		byte[] buffer = new byte[1024];
		int length;

		while ((length = is.read(buffer))> 0 )
			os.write(buffer, 0, length);


		os.flush();
		os.close();
		is.close();

	}
	
	@Override
	public void onCreate(SQLiteDatabase arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}
	
}

package data.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import processing.core.PApplet;

public class DataManager {

	private static final String DATABASE_NAME = "edu.uic.ketai.db";
	private static final int DATABASE_VERSION = 1;
	private static final String DATA_ROOT_DIRECTORY = "ketai_data";

	private Context context;
	private SQLiteDatabase db;

	private SQLiteStatement sqlStatement;

	public DataManager(Context context) {
		this.context = context;
		OpenHelper openHelper = new OpenHelper(this.context);
		this.db = openHelper.getWritableDatabase();
	}

	public SQLiteDatabase getDb() {
		return this.db;
	}

	public String[] getTables() {
		String s = "SELECT name FROM sqlite_master WHERE type='table' ORDER BY name;";
		ArrayList<String> tables = new ArrayList<String>();
		try {
			Cursor cursor = this.db.rawQuery(s, null);
			if (cursor.moveToFirst()) {
				do {
					if (cursor.getString(0) != "android_metadata")
						tables.add(cursor.getString(0));
				} while (cursor.moveToNext());
			}
		} catch (SQLiteException x) {
			x.printStackTrace();
		}
		String[] strArray = new String[tables.size()];
		tables.toArray(strArray);

		return strArray;
	}

	public Cursor executeSQL(String q) {
		try {
			Cursor cursor = this.db.rawQuery(q, null);
			return cursor;
		} catch (SQLiteException x) {
			x.printStackTrace();
		}
		return null;

	}

	public String[] getFields(String table) {
		String s = "PRAGMA table_info(" + table + ");";
		ArrayList<String> fields = new ArrayList<String>();
		try {
			Cursor cursor = this.db.rawQuery(s, null);
			if (cursor.moveToFirst()) {
				do {
					fields.add(cursor.getString(1));
				} while (cursor.moveToNext());
			}
		} catch (SQLiteException x) {
			x.printStackTrace();
		}
		String[] strArray = new String[fields.size()];
		fields.toArray(strArray);

		return strArray;
	}

	public String getFieldMin(String table, String field) {
		String q = "SELECT MIN(" + field + ") FROM " + table;
		this.sqlStatement = this.db.compileStatement(q);
		String c = this.sqlStatement.simpleQueryForString();
		if(c == null)
			return "0";
		return c;
	}

	public String getFieldMax(String table, String field) {

		String q = "SELECT MAX(" + field + ") FROM " + table;
		this.sqlStatement = this.db.compileStatement(q);
		String c = this.sqlStatement.simpleQueryForString();
		if(c == null)
			return "0";
		return c;
	}

	public long getRecordCountForTable(String table) {
		this.sqlStatement = this.db.compileStatement("SELECT COUNT(*) FROM "
				+ table);
		long c = this.sqlStatement.simpleQueryForLong();
		return c;
	}

	public long getDataCount() {
		long count = 0;
		String tablename;
		try {
			Cursor cursor = this.db.rawQuery("select name from SQLite_Master",
					null);
			if (cursor.moveToFirst()) {
				do {
					tablename = cursor.getString(0);

					// skip the android-specific table in our count
					if (tablename.equals("android_metadata"))
						continue;
					this.sqlStatement = this.db
							.compileStatement("SELECT COUNT(*) FROM "
									+ tablename);
					long c = this.sqlStatement.simpleQueryForLong();
					count += c;

				} while (cursor.moveToNext());
			}

			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		} catch (SQLiteException x) {
			x.printStackTrace();
		}
		return count;
	}

	public boolean tableExists(String _table) {
		Cursor cursor = this.db
				.rawQuery("select name from SQLite_Master", null);
		if (cursor.moveToFirst()) {
			do {
				PApplet.println("DataManager found this table: "
						+ cursor.getString(0));
				if (cursor.getString(0).equalsIgnoreCase(_table))
					return true;
			} while (cursor.moveToNext());
		}

		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return false;
	}

	public void exportData(String _targetDirectory) throws IOException {

		String directory = String.valueOf(System.currentTimeMillis());

		// First make sure the target directory exists....
		File dir = new File(Environment.getExternalStorageDirectory(),
				DATA_ROOT_DIRECTORY + "/" + directory);
		if (!dir.exists()) {
			if (dir.mkdirs())
				PApplet.println("success making directory: "
						+ dir.getAbsolutePath());
			else {
				PApplet.println("Failed making directory. Check your sketch permissions or that your device is not connected in disk mode.");
				return;
			}
		}
		String tablename;
		int rowCount = 0;

		try {
			Cursor cursor = this.db.rawQuery("select name from SQLite_Master",
					null);
			if (cursor.moveToFirst() && cursor.getCount() > 0) {
				String row = "";
				do {
					tablename = cursor.getString(0);

					// skip the android-specific table in our count
					if (tablename.equals("android_metadata"))
						continue;
					Cursor c = this.db.rawQuery("SELECT * FROM " + tablename,
							null);

					if (c.moveToFirst()) {
						do {
							int i = c.getColumnCount();
							for (int j = 0; j < i; j++)
								row += c.getString(j) + "\t";
							row += "\n";
							rowCount++;
							if (rowCount > 100) {
								if (row.length() > 0)
									this.writeToFile(row,
											dir.getAbsolutePath(), tablename);
								row = "";
								rowCount = 0;
							}
						} while (c.moveToNext());
						writeToFile(row, dir.getAbsolutePath(), tablename);
						row = "";
						rowCount = 0;
					}
				} while (cursor.moveToNext());
			}

			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
			deleteAllData();
		} catch (SQLiteException x) {
			x.printStackTrace();
		}
	}

	public void deleteAllData() {
		String tablename;
		try {
			Cursor cursor = this.db.rawQuery("select name from SQLite_Master",
					null);
			if (cursor.moveToFirst()) {
				do {
					tablename = cursor.getString(0);

					// skip the android-specific table in our count
					if (tablename.equals("android_metadata"))
						continue;
					this.db.delete(tablename, null, null);
				} while (cursor.moveToNext());
			}

			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		} catch (SQLiteException x) {
			x.printStackTrace();
		}
	}

	private void writeToFile(String data, String _dir, String exportFileName) {
		try {
			PApplet.print(".");
			String fileToWrite = _dir + "/" + exportFileName + ".csv";
			FileWriter fw = new FileWriter(fileToWrite, true);
			BufferedWriter out = new BufferedWriter(fw);
			out.write(data);
			out.close();
			fw.close();
		} catch (Exception x) {
			PApplet.println("Error exporting data. ("
					+ x.getMessage()
					+ ") Check the sketch permissions or that the device is not connected in disk mode.");
		}
		//
		// File file = new File(dir, exportFileName + ".csv");
		//
		// file.createNewFile();
		//
		// ByteBuffer buff = ByteBuffer.wrap(data.getBytes());
		// FileChannel channel = new FileOutputStream(file).getChannel();
		// try {
		// channel.write(buff);
		// } finally {
		// if (channel != null)
		// channel.close();
		// }
	}

	private static class OpenHelper extends SQLiteOpenHelper {

		OpenHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		public void onCreate(SQLiteDatabase db) {
		}

		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		}
	}

}
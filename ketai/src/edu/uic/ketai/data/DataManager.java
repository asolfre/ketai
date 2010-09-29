package edu.uic.ketai.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

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

	public String getStats() {
		return "";
	}

	public void deleteAllTables() {

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

	private static class OpenHelper extends SQLiteOpenHelper {

		OpenHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
		}

		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		}
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
			PApplet.println("Creating directory: " + dir.getAbsolutePath());
			if(dir.mkdirs())
				PApplet.println("success making dir");
			else
			{
				PApplet.println("Failed making dir");
				return;
			}
		}
		String tablename;
		try {
			Cursor cursor = this.db.rawQuery("select name from SQLite_Master",
					null);
			if (cursor.moveToFirst()) {
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
						} while (c.moveToNext());
					}
					if (row.length() > 0)
						this.writeToFile(row, dir.getPath(), tablename);
					row = "";
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

	private void writeToFile(String data, String dir, String exportFileName)
			throws IOException {

		File file = new File(dir, exportFileName + ".csv");

		file.createNewFile();

		ByteBuffer buff = ByteBuffer.wrap(data.getBytes());
		FileChannel channel = new FileOutputStream(file).getChannel();
		try {
			channel.write(buff);
		} finally {
			if (channel != null)
				channel.close();
		}
	}
}
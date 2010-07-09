package edu.uic.ketai;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class SensorDataCSVExporter {

	   private static final String DATASUBDIRECTORY = "ketai_data";

	   private SQLiteDatabase db;
	   private String filenameLabel;

	   public SensorDataCSVExporter(SQLiteDatabase db) {
	      this.db = db;
//	      filenameLabel = label;
	   }

	   public void export(String dbName, String exportFileNamePrefix) throws IOException {
	      Log.i(MyApplication.APP_NAME, "exporting database - " + dbName + " exportFileNamePrefix=" + exportFileNamePrefix);
	      String output="";
	      
	      //First lets get the total number of parent records we will need to write out so we can pace ourselves.
	      // get the tables
	      String sql = "select count(*) from sensor_data";
	      Cursor c = this.db.rawQuery(sql, new String[0]);
//	      int numberOfRows = c.getInt(c.getColumnIndex(""));
	      // get the tables
	      sql = "select * from sensor_data";
	      c = this.db.rawQuery(sql, new String[0]);
	      
	      if (c.moveToFirst()) {
	         do {
	        	
	            String id = c.getString(c.getColumnIndex("id"));
	            String timestamp = c.getString(c.getColumnIndex("timestamp"));
	            String type = c.getString(c.getColumnIndex("sensor_type"));
	            String innerSQL = "select * from sensor_raw_data where parent='" + id + "' order by sensorIndex desc";
	            Cursor cc = this.db.rawQuery(innerSQL, new String[0]);
	            if(cc.moveToFirst())
	            {
	            	do{
	            		output += timestamp + "\t" + type + "\t" + cc.getString(cc.getColumnIndex("sensorIndex")) + "\t" + cc.getString(cc.getColumnIndex("value")) + "\n";
	            	}while(cc.moveToNext());
	            }
	         } while (c.moveToNext());
	      }
	      this.writeToFile(output, exportFileNamePrefix + ".csv");
	      Log.i(MyApplication.APP_NAME, "exporting database complete");
	   }


	   private void writeToFile(String xmlString, String exportFileName) throws IOException {
	      File dir = new File(Environment.getExternalStorageDirectory(), DATASUBDIRECTORY);
	      if (!dir.exists()) {
	         dir.mkdirs();
	      }
	      File file = new File(dir, exportFileName);
	      file.createNewFile();

	      ByteBuffer buff = ByteBuffer.wrap(xmlString.getBytes());
	      FileChannel channel = new FileOutputStream(file).getChannel();
	      try {
	         channel.write(buff);
	      } finally {
	         if (channel != null)
	            channel.close();
	      }
	   }
	}

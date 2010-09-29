package edu.uic.ketai.analyzer;

import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteStatement;
import android.hardware.SensorEvent;

import edu.uic.ketai.data.DataManager;

public class SensorAnalyzer extends AbstractKetaiAnalyzer  {

	final static String NAME = "AllSensorData";
	final static String DESCRIPTION = "Stores all sensor data.";
	final static String[] servicesubscription = {"edu.uic.ketai.inputService.KetaiSensorManager"};
	private static String TABLE_NAME = "sensor_events";
	private static String CREATE_TABLE_SQL = "CREATE TABLE sensor_events (id INTEGER PRIMARY KEY, timestamp BIGINT, sensor_type INTEGER, value0 FLOAT, value1 FLOAT, value2 FLOAT)";
	private static String INSERT_SQL = "insert into " + TABLE_NAME + " (timestamp, sensor_type, value0, value1, value2) values (?, ?, ?, ?, ?)";

	public SensorAnalyzer(DataManager _datamanager)
	{
		super(_datamanager);
		verifyDatabase();
	}

	public String getAnalysisDescription() {
		return null;
	}

	public void analyzeData(Object _data) {

		if(!(_data instanceof SensorEvent))
			return;
		
		SQLiteStatement insertStatement;
		SensorEvent data = (SensorEvent)_data;
		
		insertStatement = datamanager.getDb().compileStatement(INSERT_SQL);
		insertStatement.bindLong(1, data.timestamp);
		insertStatement.bindLong(2, data.sensor.getType());
		 for (int i = 0; i < data.values.length && i < 3; i++)
			 insertStatement.bindDouble(3 + i, data.values[i]);
		
		 insertStatement.executeInsert();		
	}

	public String getAnalyzerName() {
		return NAME;
	}

	public String getTableCreationString() {
		return CREATE_TABLE_SQL;
	}
	
	public Class<?> getServiceProviderClass()  {
		try {
			return Class.forName("edu.uic.ketai.inputService.KetaiSensorManager");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private void verifyDatabase()
	{
		if(datamanager.tableExists(TABLE_NAME))
			return;
		
		//Let's create our table if necessary
		try{
			datamanager.getDb().execSQL(CREATE_TABLE_SQL);
		}catch (SQLiteException sqlx){ sqlx.printStackTrace(); }
	}
}

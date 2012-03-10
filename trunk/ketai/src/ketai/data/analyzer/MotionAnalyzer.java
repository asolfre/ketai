package ketai.data.analyzer;

import data.database.DataManager;
import data.inputService.SensorService;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteStatement;
import android.hardware.SensorEvent;

public class MotionAnalyzer extends AbstractKetaiAnalyzer {
	final static String NAME = "MotionAnalyzer";
	final static String DESCRIPTION = "Saves motion events triggered by onset detection";
	final static String[] servicesubscription = { SensorService.class.getName()};
	private static String TABLE_NAME = "motionanalyzer";

	private static String CREATE_TABLE_SQL = "CREATE TABLE "
			+ TABLE_NAME
			+ "  (id INTEGER PRIMARY KEY, starttime BIGINT,  endtime BIGINT, startx REAL, starty REAL, startz REAL,"
			+ "endx REAL, endy REAL, endz REAL, sumofchange REAL, movementstring TEXT )";

	private static String INSERT_SQL = "insert into "
			+ TABLE_NAME
			+ " (starttime, endtime, startx, starty, startz, endx, endy, endz, sumofchange, movementstring) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

	public MotionAnalyzer(DataManager _datamanager) {
		super(_datamanager);
		verifyDatabase();
	}

	public String getAnalysisDescription() {
		return DESCRIPTION;
	}

	/*
	 * Where all the magic happens.....
	 */
	public void analyzeData(Object _data) {

		if (!(_data instanceof SensorEvent))
			return;

		SensorEvent data = (SensorEvent) _data;

		if (data.values[0] > 10)
			saveData(data.timestamp, data.timestamp, data.values[0],
					data.values[1], data.values[2], data.values[0],
					data.values[1], data.values[2], 0, "test");

	}

	private void saveData(long starttime, long endtime, float startx,
			float starty, float startz, float endx, float endy, float endz,
			long someofchange, String movementstring) {
		SQLiteStatement insertStatement;
		insertStatement = datamanager.getDb().compileStatement(INSERT_SQL);
		insertStatement.bindLong(1, starttime);
		insertStatement.bindLong(2, endtime);
		insertStatement.bindDouble(3, startx);
		insertStatement.bindDouble(4, starty);
		insertStatement.bindDouble(5, startz);
		insertStatement.bindDouble(6, endx);
		insertStatement.bindDouble(7, endy);
		insertStatement.bindDouble(8, endz);
		insertStatement.bindLong(9, someofchange);
		insertStatement.bindString(10, movementstring);

		insertStatement.executeInsert();

	}

	public String getAnalyzerName() {
		return NAME;
	}

	public String getTableCreationString() {
		return CREATE_TABLE_SQL;
	}

	public Class<?> getServiceProviderClass() {
		try {
			return Class
					.forName(SensorService.class.getName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void verifyDatabase() {
		if (datamanager.tableExists(TABLE_NAME))
			return;

		// Let's create our table if necessary
		try {
			datamanager.getDb().execSQL(CREATE_TABLE_SQL);
		} catch (SQLiteException sqlx) {
			sqlx.printStackTrace();
		}
	}

}

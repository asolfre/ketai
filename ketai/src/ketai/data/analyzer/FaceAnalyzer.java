package ketai.data.analyzer;

import data.database.DataManager;
import data.inputService.CameraService;
import ketai.camera.FaceFinder;
import ketai.camera.kFace;
import processing.core.PApplet;
import processing.core.PImage;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteStatement;

public class FaceAnalyzer extends AbstractKetaiAnalyzer {

	final static String NAME = "AllSensorData";
	final static String DESCRIPTION = "Wraps the android face detector function.   Will find a face in the camera preview and save a photo.";
	final static String[] servicesubscription = { CameraService.class.getName() };
	private static String TABLE_NAME = "facedetected";
	private static String CREATE_TABLE_SQL = "CREATE TABLE facedetected (id INTEGER PRIMARY KEY, timestamp BIGINT, number_of_faces INT)";
	private static String INSERT_SQL = "insert into " + TABLE_NAME
			+ " (timestamp, number_of_faces) values (?, ?)";
	
	public FaceAnalyzer(DataManager _datamanager) {
		super(_datamanager);
		verifyDatabase();
	}

	public String getAnalysisDescription() {
		return DESCRIPTION;
	}

	public void analyzeData(Object _data) {
		long timestamp = System.currentTimeMillis();

		if (!(_data instanceof PImage))
			return;

		SQLiteStatement insertStatement;

		kFace[] faces = FaceFinder.findFaces((PImage)_data, 5);

		int numberOfFaces = faces.length;

		if (numberOfFaces < 1) {
			broadcastKetaiEvent("noface", null);
			return;
		}
		insertStatement = datamanager.getDb().compileStatement(INSERT_SQL);
		insertStatement.bindLong(1, timestamp);
		insertStatement.bindLong(2, numberOfFaces);

		insertStatement.executeInsert();
		for (int i = 0; i < numberOfFaces; i++) {
			broadcastKetaiEvent("face", faces[i]);
		}
	}

	public String getAnalyzerName() {
		return NAME;
	}

	public String getTableCreationString() {
		return CREATE_TABLE_SQL;
	}

	public Class<?> getServiceProviderClass() {
		try {
			return Class.forName(CameraService.class.getName());
		} catch (ClassNotFoundException e) {
			PApplet.println("tried:"  + CameraService.class.getName());
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

package edu.uic.ketai;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

public class Main extends Activity implements SensorEventListener{

   private static final String NAME = "NAME";

   private static final int MENU_MANAGE = 0;
   
   private EditText input;
   private Button saveButton;
   private Button deleteButton;
   private Button toggleSensorCollection;
   private Button exportSensorData;
   private TextView output;
   private TextView sensorDataCount, accel_data, orientation_data;
   private CheckBox isOrientationSensorEnabled;
   private CheckBox isAccelerometerSensorEnabled;
   private MyApplication application;
   private SensorManager  sm;

   @Override
   public void onCreate(final Bundle savedInstanceState) {
      Log.d(MyApplication.APP_NAME, "onCreate");
      super.onCreate(savedInstanceState);
      sm = (SensorManager)getSystemService(SENSOR_SERVICE);
      this.setContentView(R.layout.main);

      // get "Application" object for shared state or creating of expensive resources - like DataHelper
      // (this is not recreated as often as each Activity)
      this.application = (MyApplication) this.getApplication();

      // inflate views
      this.input = (EditText)	 this.findViewById(R.id.in_text);
      this.saveButton = (Button) this.findViewById(R.id.save_button);
      this.deleteButton = (Button) this.findViewById(R.id.del_button);
      this.output = (TextView) this.findViewById(R.id.out_text);
      this.sensorDataCount = (TextView)this.findViewById(R.id.sensor_data_count);
      this.toggleSensorCollection = (Button) this.findViewById(R.id.sensor_button);
      this.exportSensorData = (Button)this.findViewById(R.id.export_sensor_data_button);
      this.isAccelerometerSensorEnabled = (CheckBox)this.findViewById(R.id.accelerometer_checkbox);
      this.isOrientationSensorEnabled = (CheckBox)this.findViewById(R.id.orientation_sensor_checkbox);
      this.accel_data = (TextView)this.findViewById(R.id.accelerometer_data);
      this.orientation_data = (TextView)this.findViewById(R.id.orientation_data);
      
      // initially populate "output" view from database
      new SelectDataTask().execute();

      sensorDataCount.setText("Total Raw Data Points: " + Long.toString(Main.this.application.getDataManager().getRawSensorDataCount()));
      
      // save new data to database (when save button is clicked)
      this.saveButton.setOnClickListener(new OnClickListener() {
         public void onClick(final View v) {
            new InsertDataTask().execute(Main.this.input.getText().toString());
            Main.this.input.setText("");
         }
      });
      
      this.toggleSensorCollection.setOnClickListener(new OnClickListener(){
    	  public void onClick(final View v) {
    		Main.this.sensorDataCount.setText("Total Raw Data Points: " + Long.toString(Main.this.application.getDataManager().getRawSensorDataCount()));

    		int s=0;
    	    sm.unregisterListener(Main.this);
    		
    		if(Main.this.isAccelerometerSensorEnabled.isChecked())
    			s |= SensorManager.SENSOR_ACCELEROMETER;
    		else
    			s &= ~SensorManager.SENSOR_ACCELEROMETER;
    		
    		if(Main.this.isOrientationSensorEnabled.isChecked())
    			s |= SensorManager.SENSOR_ORIENTATION;
    		else
    			s &= ~SensorManager.SENSOR_ORIENTATION;
    		
    		Main.this.application.getSensorProcessor().setSensorsToListenTo(s);
    		
    		Main.this.application.getSensorProcessor().toggleCollect();
    		if(Main.this.application.getSensorProcessor().getCollectionState())
    		{
    			Main.this.toggleSensorCollection.setText("Disable Sensor Data Collection");
    		}
    		else
    		{
    		      Sensor oSensor = sm.getDefaultSensor(Sensor.TYPE_ORIENTATION);       
    		      Sensor aSensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    		      
    		      sm.registerListener(Main.this, oSensor, SensorManager.SENSOR_DELAY_UI);
    		      sm.registerListener(Main.this, aSensor, SensorManager.SENSOR_DELAY_UI);
    			Main.this.toggleSensorCollection.setText("Enable Sensor Data Collection");
    		}
    	  }
      });
      
      this.exportSensorData.setOnClickListener(new OnClickListener(){
    	  public void onClick(final View v){
    		  new ExportSensorDataTask().execute();
    	   	  Main.this.sensorDataCount.setText("Total Raw Data Points: " + Long.toString(Main.this.application.getDataManager().getRawSensorDataCount()));
    	  }
      });
      
      // delete all data from database (when delete button is clicked)
      this.deleteButton.setOnClickListener(new OnClickListener() {
         public void onClick(final View v) {
            new DeleteDataTask().execute();
         }
      });
     
      Sensor oSensor = sm.getDefaultSensor(Sensor.TYPE_ORIENTATION);       
      Sensor aSensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
      
      sm.registerListener(this, oSensor, SensorManager.SENSOR_DELAY_UI);
      sm.registerListener(this, aSensor, SensorManager.SENSOR_DELAY_UI);
      
    //setup state on sensor enabling button  
	if(Main.this.application.getSensorProcessor().getCollectionState())
		Main.this.toggleSensorCollection.setText("Disable Sensor Data Collection");
	else
		Main.this.toggleSensorCollection.setText("Enable Sensor Data Collection");     

	//setup the sensor checkboxes
	int s = Main.this.application.getSensorProcessor().getSensorsToListenTo();
	if((s | SensorManager.SENSOR_ACCELEROMETER) != 0)
		this.isAccelerometerSensorEnabled.setChecked(true);
	else
		this.isAccelerometerSensorEnabled.setChecked(false);

	if((s | SensorManager.SENSOR_ORIENTATION) != 0)
		this.isOrientationSensorEnabled.setChecked(true);
	else
		this.isOrientationSensorEnabled.setChecked(false);
	
   }

   @Override
   public void onSaveInstanceState(final Bundle b) {
      Log.d(MyApplication.APP_NAME, "onSaveInstanceState");
      if ((this.input.getText().toString() != null) && (this.input.getText().toString().length() > 0)) {
         b.putString(Main.NAME, this.input.getText().toString());
      }
      super.onSaveInstanceState(b);
   }

   @Override
   public void onRestoreInstanceState(final Bundle b) {
      super.onRestoreInstanceState(b);
      Log.d(MyApplication.APP_NAME, "onRestoreInstanceState");
      String name = b.getString(Main.NAME);
      if (name != null) {
         // use onSaveInstanceState/onRestoreInstance state to manage state when orientation is changed (and whenever restarted)
         // put some text in input box, then rotate screen, text should remain
         // COMMENT this out, and try again, text won't be there - you need to maintain this state - esp for orientation changes
         // (you can rotate the screen in the emulator by pressing 9 on numeric keypad)
         this.input.setText(name);
      }
   }
   
   @Override
   public boolean onCreateOptionsMenu(final Menu menu) {
      menu.add(0, Main.MENU_MANAGE, 1, "Manage Database").setIcon(android.R.drawable.ic_menu_manage);
      return super.onCreateOptionsMenu(menu);
   }

   @Override
   public boolean onOptionsItemSelected(final MenuItem item) {
      switch (item.getItemId()) {      
      case MENU_MANAGE:
          this.startActivity(new Intent(Main.this, ManageData.class));          
          return true;
      default:
         return super.onOptionsItemSelected(item);
      }
   }

   private class InsertDataTask extends AsyncTask<String, Void, Void> {
      private final ProgressDialog dialog = new ProgressDialog(Main.this);

      // can use UI thread here
      protected void onPreExecute() {
         this.dialog.setMessage("Inserting data...");
         this.dialog.show();
      }

      // automatically done on worker thread (separate from UI thread)
      protected Void doInBackground(final String... args) {
         Main.this.application.getDataManager().insertQRCode(args[0], System.currentTimeMillis());
         return null;
      }

      // can use UI thread here
      protected void onPostExecute(final Void unused) {
         if (this.dialog.isShowing()) {
            this.dialog.dismiss();
         }
         // reset the output view by retrieving the new data
         // (note, this is a naive example, in the real world it might make sense
         // to have a cache of the data and just append to what is already there, or such
         // in order to cut down on expensive database operations)
         new SelectDataTask().execute();
      }
   }
   
   private class SelectDataTask extends AsyncTask<String, Void, String> {
      private final ProgressDialog dialog = new ProgressDialog(Main.this);

      // can use UI thread here
      protected void onPreExecute() {
         this.dialog.setMessage("Selecting data...");
         this.dialog.show();
      }

      // automatically done on worker thread (separate from UI thread)
      protected String doInBackground(final String... args) {
         List<String> names = Main.this.application.getDataManager().selectAll();
         StringBuilder sb = new StringBuilder();
         for (String name : names) {
            sb.append(name + "\n");
         }
         return sb.toString();
      }

      // can use UI thread here
      protected void onPostExecute(final String result) {
         if (this.dialog.isShowing()) {
            this.dialog.dismiss();
         }
         Main.this.output.setText(result);
      }
   }

   private class DeleteDataTask extends AsyncTask<String, Void, Void> {
      private final ProgressDialog dialog = new ProgressDialog(Main.this);

      // can use UI thread here
      protected void onPreExecute() {
         this.dialog.setMessage("Deleting data...");
         this.dialog.show();
      }

      // automatically done on worker thread (separate from UI thread)
      protected Void doInBackground(final String... args) {
         Main.this.application.getDataManager().deleteAll();
         return null;
      }

      // can use UI thread here
      protected void onPostExecute(final Void unused) {
         if (this.dialog.isShowing()) {
            this.dialog.dismiss();
         }
         // reset the output view by retrieving the new data
         // (note, this is a naive example, in the real world it might make sense
         // to have a cache of the data and just append to what is already there, or such
         // in order to cut down on expensive database operations)
         new SelectDataTask().execute();
    	 Main.this.sensorDataCount.setText("Total Raw Data Points: " + Long.toString(Main.this.application.getDataManager().getRawSensorDataCount()));
      }
   }

   private class ExportSensorDataTask extends AsyncTask<String, Void, Void> {
	      private final ProgressDialog dialog = new ProgressDialog(Main.this);

	      // can use UI thread here
	      protected void onPreExecute() {
	         this.dialog.setMessage("Exporting sensor data...");
	         this.dialog.show();
	      }

	      // automatically done on worker thread (separate from UI thread)
	      protected Void doInBackground(final String... args) {
    		  try{
    			  Main.this.application.getDataManager().exportData(Main.this.input.getText().toString());    	  
    		  }catch (IOException iox){ Main.this.sensorDataCount.setText("Error Writing DB file: "+iox.getMessage()); }
	         return null;
	      }

	      // can use UI thread here
	      protected void onPostExecute(final Void unused) {
	         if (this.dialog.isShowing()) {
	            this.dialog.dismiss();
	         }
	         // reset the output view by retrieving the new data
	         // (note, this is a naive example, in the real world it might make sense
	         // to have a cache of the data and just append to what is already there, or such
	         // in order to cut down on expensive database operations)
	            new DeleteDataTask().execute();
	      }
	   }

public void onAccuracyChanged(Sensor sensor, int accuracy) {
	// TODO Auto-generated method stub
	
}

public void onSensorChanged(SensorEvent event) {
	// TODO Auto-generated method stub
	String s = "";
	DecimalFormat df = new DecimalFormat("#.00");
	for(int i = 0; i < event.values.length; i++)
		s+= df.format(event.values[i]) + "/";
	if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
		this.accel_data.setText("Accelerometer data:"+s);
	else if(event.sensor.getType() == Sensor.TYPE_ORIENTATION)
		this.orientation_data.setText("Orientation Data: " + s);
}

}
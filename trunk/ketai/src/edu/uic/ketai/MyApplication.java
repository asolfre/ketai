package edu.uic.ketai;

import android.app.Application;
import android.hardware.SensorManager;
import android.util.Log;

public class MyApplication extends Application {

   public static final String APP_NAME = "Ketai In Motion";  
   
   private DataManager dataManager;
   private SensorProcessor sensorProcessor;
   private CameraManager cameraManager;
   
   @Override
   public void onCreate() {
      super.onCreate();
      Log.d(APP_NAME, "APPLICATION onCreate");
      this.dataManager = new DataManager(this);
      this.sensorProcessor = new SensorProcessor(dataManager, (SensorManager)this.getSystemService(SENSOR_SERVICE)); 
      this.cameraManager = new CameraManager();
   }
   
   @Override
   public void onTerminate() {
      Log.d(APP_NAME, "APPLICATION onTerminate");      
      super.onTerminate();      
   }

   public DataManager getDataManager() {
      return this.dataManager;
   }
   
   public SensorProcessor getSensorProcessor(){
	   return this.sensorProcessor;
   }
   
   public CameraManager getCameraManager(){
	   return this.cameraManager;
   }

   public void setDataHelper(DataManager dataHelper) {
      this.dataManager = dataHelper;
   }
}

long startTime;
String[] sensorName = new String[129];
void loadSensorNames (){
 sensorName[1] = "SENSOR_ORIENTATION";
sensorName[2] = "SENSOR_ACCELEROMETER";
sensorName[4] = "SENSOR_TEMPERATURE";
sensorName[8] = "SENSOR_MAGNETIC_FIELD";
sensorName[16] = "SENSOR_LIGHT";
sensorName[32] = "SENSOR_PROXIMITY";
sensorName[64] = "SENSOR_TRICORDER";
sensorName[128] = "SENSOR_ORIENTATION_RAW"; 
}
color guiColor1 = color(204, 102, 0);
color guiColor2 = color(0, 102, 153);
int border = 50;

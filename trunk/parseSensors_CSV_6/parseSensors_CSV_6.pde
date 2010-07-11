Table sensorTable;
int rowCount;
ArrayList sensorTypes;  // stores index of sensor types available
ArrayList sensors;      // stores data for every sensor

void setup() {
  size(1440, 900);
  sensorTable = new Table("skate_KETAI_DB.csv");
  rowCount = sensorTable.getRowCount();
  guiSetup(); // make the GUI menu
  sensors = new ArrayList(); // create empty sensor Array of sensor objects
  sensorTypes = new ArrayList(); // create empty sensor Array of sensor objects
  for (int row = 0; row < rowCount; row++) {
    int type = sensorTable.getInt(row, 1);
    if (sensorTypes.contains(type)) {
    }
    else {
      sensorTypes.add(type);
      println("sensor type ["+type+"] added");
      sensors.add(new Sensor(type));
    }
  }

  for (int row = 0; row < rowCount; row++) {
    for (int i = 0; i < sensors.size(); i++) {
      float timeStamp = sensorTable.getFloat(row, 0);
      if (row == 0) startTime = timeStamp;
      int type = sensorTable.getInt(row, 1);
      int index = sensorTable.getInt(row, 2);
      float value = sensorTable.getFloat(row, 3);
      // println("type: "+type+" | index: "+index+" | value: "+value);
      // pointer to current sensor
      Sensor s = (Sensor) sensors.get(i);
      if (s.getType() == type) {
        s.addValue(timeStamp, index, value);
      }
    }
  }
  // initialize sensor object after data has been added
  for (int i = 0; i < sensors.size(); i++) {
    Sensor s = (Sensor) sensors.get(i);
      s.init();
    }
    
}

void draw() {
  background(0);
  for (int i = 0; i < sensors.size(); i++) {
    Sensor s = (Sensor) sensors.get(i);

  }
}


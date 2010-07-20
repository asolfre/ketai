// SENSOR CLASS INTERFACE FOR ONE PARTICULAR SENSOR
class Sensor {
  // CLASS VARIABLES
  ArrayList row = new ArrayList();
  ArrayList indexTypes = new ArrayList();
  ArrayList timeStampTypes = new ArrayList();
  Vector[] value;
  color myColor;
  float sensorMin = MAX_FLOAT;
  float sensorMax = MIN_FLOAT;
  // store minimum values for all indexes of the sensor type
  float myMin[] = {
    MAX_FLOAT, MAX_FLOAT, MAX_FLOAT, MAX_FLOAT, MAX_FLOAT, MAX_FLOAT
  };
  // store maximum values for all indexes of the sensor type
  float myMax[] = {
    MIN_FLOAT, MIN_FLOAT, MIN_FLOAT, MIN_FLOAT, MIN_FLOAT, MIN_FLOAT
  };
  float myDuration = 0;
  int type;
  Textarea myTextarea;
  Textlabel myTextlabelMin, myTextlabelMax, myTextlabelZero;
  boolean plotVisible = true;
  String src = "[type] : milliSeconds : index : value\n";
  // CONSTRUCTOR
  Sensor(int sensorType) {
    type = sensorType;        // int type, represents specific sensor id
    src+= sensorName[type]+"\n\n";
  }
  // DRAW GRAPHIC SENSOR COMPONENTS
  void draw() {
    noStroke();
    fill(myColor);
    //for (int i=0; i<value.length; i++) {
    //ellipse(map(value[i].value.x, sensorMin, sensorMax, 0, width), map(value[i].value.y, 0, sensorMin, sensorMax, height), 5, 5);
    //}
    pushMatrix();
    translate(0,height/2);
    noFill();
    for (int indexID=0; indexID<indexTypes.size(); indexID++) {
      if (plotVisible) plotNormalized(indexID);
    }
    popMatrix();
  }
  // PLOT TIMELINE
  void plot(int index) {
    ;
    for (int i=0; i<value.length; i++) {
      ellipse(map(value[i].timeStamp, 0, myDuration, border, (width-2*border)),value[i].getValue(index), 3, 3);
    }
  }
  void plotNormalized(int index) {
    beginShape();
            stroke(subColor(index));
    for (int i=1; i<value.length; i++) {

      float plotX = map(value[i].timeStamp, 0, myDuration, border, (width-2*border));
      float plotY = map(value[i].getValue(index), myMin[index], myMax[index], -height/2+border, height/2-border*2);
      // check if value rolls over, don't connect the line then
      if (abs(plotY-map(value[i-1].getValue(index), myMin[index], myMax[index], -height/2+border, height/2-border*2)) > height*.75) {
        endShape();
        beginShape();
      }
      ellipse(plotX, plotY, 3, 3);
      vertex(plotX, plotY);
    }
    endShape();
  }
  // UNIQUE COLOR FOR EVERY INDEX WITHIN A SPECIFIC SENSOR COLOR
  color subColor(int index) {
    return lerpColor(myColor, color(myColor, 127), float(index)/indexTypes.size());
  }
  // INIT, ASSEMBLE GUI
  void init() {
    // unboxing unique timeStamps
    int len = timeStampTypes.size();
    long[] timeStamp = new long[len];
    Long[] fa = new Long[len];
    timeStampTypes.toArray(fa);
    value = new Vector[len];
    for (int i = 0; i < len; i++)
    {
      timeStamp[i] = fa[i];
      value[i] = new Vector(timeStamp[i], type);
    }
    // parsing all rows
    for (int i=0; i<row.size(); i++) {
      Row v = (Row) row.get(i);
      text("["+type+"] "+(v.timeStamp-startTime)+" : "+v.index+" : "+v.value+"", type, i*12);
      src += "["+type+"] "+(v.timeStamp-startTime)+" : "+v.index+" : "+v.value+"\n";
      for (int j=0; j<timeStamp.length; j++) {
        if (timeStamp[j] == (v.timeStamp-startTime)) {
          value[j].setValue(v.index, v.value);
          if (sensorMax<v.value) sensorMax = v.value;
          if (sensorMin>v.value) sensorMin = v.value;
          // center align all values
          if (sensorMax>abs(sensorMin)) {
            sensorMin = -abs(sensorMax);
          } 
          else {
            sensorMax = abs(sensorMin);
          }
          if ((v.timeStamp-startTime) > myDuration) myDuration = (v.timeStamp-startTime);
        }
      }
    }
    // sensor-specific gui
    MultiListButton multi;
    // add sensor to global navigation
    multi = mlButton.add("sensors_"+type,100+type);
    multi.setLabel(type+" : "+sensorName[type]);
    myColor = lerpColor(guiColor1, guiColor2, type/sensors.size());
    // textarea for source data
    myTextarea = controlP5.addTextarea("src_"+type, "", (width-4*border)/sensors.size()*(type-1)+type*border,border,(width-4*border)/sensors.size(),height-3*border);
    myTextarea.setText(src);
    myTextarea.setColorForeground(myColor);
    // max
    myTextlabelMin = controlP5.addTextlabel("min"+type,sensorMin+"",int(width-1.5*border),border);
    myTextlabelMin.setColorValue(myColor);
    // min
    myTextlabelMax = controlP5.addTextlabel("max"+type,sensorMax+"",int(width-1.5*border),height-2*border);
    myTextlabelMax.setColorValue(myColor);
    // zero
    myTextlabelZero = controlP5.addTextlabel("zero"+type,"0",int(width-1.5*border),(int)map(0, sensorMin, sensorMax, border, height-2*border));
    myTextlabelZero.setColorValue(myColor);
    line(0, map(0, sensorMin, sensorMax, border, height-2*border), width, map(0, sensorMin, sensorMax, border, height-2*border));
  }
  // ADD ROW FROM .csv FILE
  void addValue(long timeStamp, int index, float value) {
    // add row object - maybe obsolete
    row.add(new Row(timeStamp, index, value));
    // detect indexes within one sensor type
    if (indexTypes.contains(index)) {
    }
    else {
      indexTypes.add(index);
      //ArrayList row+index = new ArrayList();
      println("index ["+index+"] added for sensor type "+type);
    }
    // detect unique timestamps, create timeStamp object
    if (timeStampTypes.contains(timeStamp-startTime)) {
    }
    else {
      timeStampTypes.add((timeStamp-startTime));
      //ArrayList row+index = new ArrayList();
      println("timeStamp ["+(timeStamp-startTime)+"] added for sensor type "+type);
    }
  }
  // SENSOR NAME/DESCRIPITON
  String sensorName(int type) {
    return sensorName[type];
  }
  // NORMALIZING VALUES -> WARNING: NOT CONSISTENT THROUGH ALL ROWS
  void normalizeValues() {
    for (int i=0; i<value.length; i++) {
      value[i].normalizeValues();
    }
  }
  // TOGGLE FOR GUI
  void active(String name, float value) {
    if (int(value-100) == type && !name.equals("myNavigation")) {
      if (myTextarea.isVisible()) {
        myTextarea.hide();
        myTextlabelMin.hide();
        myTextlabelMax.hide();
        myTextlabelZero.hide();
        plotVisible = false;
      } 
      else {
        myTextarea.show();
        myTextlabelMin.show();
        myTextlabelMax.show();
        myTextlabelZero.show();
        plotVisible = true;
      }
    }
  }
}

/* C Code
 ArrayList arrayList = new ArrayList();
 
 for (int i = 0; i < d1 ; i++)
 {
 
 ArrayList l2 = new ArrayList(); arrayList.Add(l2);
 
 }
 
 */

// CLASS HOLDING VALUED FOR EACH DATA ROW (timestamp, index, value) SOURCE: .csv file
class Row {
  long timeStamp;
  int index;
  float value;
  Row (long _timeStamp, int _index, float _value ) {
    timeStamp = _timeStamp;
    index= _index;
    value = _value;
  }
}
// LOWEST LEVEL CLASS, STORES x, y, z in PVector value; rawX, rawY, rawZ in PVector valueRaw
class Vector {
  PVector value;
  PVector valueRaw;
  long timeStamp;
  int type;
  Vector (long _timeStamp, int _type) {
    timeStamp = _timeStamp;
    value = new PVector(0, 0, 0);
    valueRaw = new PVector(0, 0, 0);
    type = _type;
  }
  long getTimeStamp() {
    return timeStamp;
  }
  void setX(float x) {
    value.set(x, value.y, value.z);
  }
  void setY(float y) {
    value.set(value.x, y, value.z);
  }
  void setZ(float z) {
    value.set(value.x, value.y, z);
  }
  float getX() {
    return value.x;
  }
  float getY() {
    return value.y;
  }
  float getZ() {
    return value.z;
  }
  void setValue(int index, float input) {
    Sensor s = (Sensor) sensors.get(type-1);
    if (s.myMin[index] > input) s.myMin[index] = input;
    if (s.myMax[index] < input) s.myMax[index] = input;
    switch(index) {
    case 0: 
      value.set(input, value.y, value.z);
      break;
    case 1: 
      value.set(value.x, input, value.z);
      break;
    case 2: 
      value.set(value.x, value.y, input);
      break;
    case 3: 
      valueRaw.set(input, valueRaw.y, valueRaw.z);
      break;
    case 4: 
      valueRaw.set(valueRaw.x, input, valueRaw.z);
      break;
    case 5: 
      valueRaw.set(valueRaw.x, valueRaw.y, input);
      break;
    default:
      println("invalid index: "+input); // Does not execute
      break;
    }
  }
  float getValue(int index) {
    switch(index) {
    case 0: 
      return value.x;
    case 1: 
      return value.y;
    case 2: 
      return value.z;
    case 3: 
      return valueRaw.x;
    case 4: 
      return valueRaw.y;
    case 5: 
      return valueRaw.z;
    default:
      println("invalid index: "+index); // Does not execute
      return -1;
    }
  }
  void normalizeValues() {
    value.normalize();
    valueRaw.normalize();
  }
}


// VALUE MAPPING USED BY ANDROID OS FOR SENSORS

// Compiled from SensorManager.java (version 1.5 : 49.0, super bit)

// Field descriptor #8 I
public static final int SENSOR_ORIENTATION = 1;

// Field descriptor #8 I
public static final int SENSOR_ACCELEROMETER = 2;

// Field descriptor #8 I
public static final int SENSOR_TEMPERATURE = 4;

// Field descriptor #8 I
public static final int SENSOR_MAGNETIC_FIELD = 8;

// Field descriptor #8 I
public static final int SENSOR_LIGHT = 16;

// Field descriptor #8 I
public static final int SENSOR_PROXIMITY = 32;

// Field descriptor #8 I
public static final int SENSOR_TRICORDER = 64;

// Field descriptor #8 I
public static final int SENSOR_ORIENTATION_RAW = 128;

// Field descriptor #8 I
public static final int SENSOR_ALL = 127;

// Field descriptor #8 I
public static final int SENSOR_MIN = 1;

// Field descriptor #8 I
public static final int SENSOR_MAX = 64;

// Field descriptor #8 I
public static final int DATA_X = 0;

// Field descriptor #8 I
public static final int DATA_Y = 1;

// Field descriptor #8 I
public static final int DATA_Z = 2;

// Field descriptor #8 I
public static final int RAW_DATA_INDEX = 3;

// Field descriptor #8 I
public static final int RAW_DATA_X = 3;

// Field descriptor #8 I
public static final int RAW_DATA_Y = 4;

// Field descriptor #8 I
public static final int RAW_DATA_Z = 5;

// Field descriptor #40 F
public static final float STANDARD_GRAVITY = 9.80665f;

// Field descriptor #40 F
public static final float GRAVITY_SUN = 275.0f;

// Field descriptor #40 F
public static final float GRAVITY_MERCURY = 3.7f;

// Field descriptor #40 F
public static final float GRAVITY_VENUS = 8.87f;

// Field descriptor #40 F
public static final float GRAVITY_EARTH = 9.80665f;

// Field descriptor #40 F
public static final float GRAVITY_MOON = 1.6f;

// Field descriptor #40 F
public static final float GRAVITY_MARS = 3.71f;

// Field descriptor #40 F
public static final float GRAVITY_JUPITER = 23.12f;

// Field descriptor #40 F
public static final float GRAVITY_SATURN = 8.96f;

// Field descriptor #40 F
public static final float GRAVITY_URANUS = 8.69f;

// Field descriptor #40 F
public static final float GRAVITY_NEPTUNE = 11.0f;

// Field descriptor #40 F
public static final float GRAVITY_PLUTO = 0.6f;

// Field descriptor #40 F
public static final float GRAVITY_DEATH_STAR_I = 3.5303614E-7f;

// Field descriptor #40 F
public static final float GRAVITY_THE_ISLAND = 4.815162f;

// Field descriptor #40 F
public static final float MAGNETIC_FIELD_EARTH_MAX = 60.0f;

// Field descriptor #40 F
public static final float MAGNETIC_FIELD_EARTH_MIN = 30.0f;

// Field descriptor #40 F
public static final float LIGHT_SUNLIGHT_MAX = 120000.0f;

// Field descriptor #40 F
public static final float LIGHT_SUNLIGHT = 110000.0f;

// Field descriptor #40 F
public static final float LIGHT_SHADE = 20000.0f;

// Field descriptor #40 F
public static final float LIGHT_OVERCAST = 10000.0f;

// Field descriptor #40 F
public static final float LIGHT_SUNRISE = 400.0f;

// Field descriptor #40 F
public static final float LIGHT_CLOUDY = 100.0f;

// Field descriptor #40 F
public static final float LIGHT_FULLMOON = 0.25f;

// Field descriptor #40 F
public static final float LIGHT_NO_MOON = 0.0010f;

// Field descriptor #8 I
public static final int SENSOR_DELAY_FASTEST = 0;

// Field descriptor #8 I
public static final int SENSOR_DELAY_GAME = 1;

// Field descriptor #8 I
public static final int SENSOR_DELAY_UI = 2;

// Field descriptor #8 I
public static final int SENSOR_DELAY_NORMAL = 3;

// Field descriptor #8 I
public static final int SENSOR_STATUS_UNRELIABLE = 0;

// Field descriptor #8 I
public static final int SENSOR_STATUS_ACCURACY_LOW = 1;

// Field descriptor #8 I
public static final int SENSOR_STATUS_ACCURACY_MEDIUM = 2;

// Field descriptor #8 I
public static final int SENSOR_STATUS_ACCURACY_HIGH = 3;

// Field descriptor #8 I
public static final int AXIS_X = 1;

// Field descriptor #8 I
public static final int AXIS_Y = 2;

// Field descriptor #8 I
public static final int AXIS_Z = 3;

// Field descriptor #8 I
public static final int AXIS_MINUS_X = 129;

// Field descriptor #8 I
public static final int AXIS_MINUS_Y = 130;

// Field descriptor #8 I
public static final int AXIS_MINUS_Z = 131;


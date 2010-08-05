package ketaiosopengl;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PVector;
import processing.opengl.*;
import controlP5.*;


public class KetaiOSOpenGL extends PApplet {

	Table sensorTable;
	int rowCount;
	ArrayList<Integer> sensorTypes;  // stores index of sensor types available
	ArrayList<Sensor> sensors;      // stores data for every sensor
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
	int guiColor1 = color(204, 102, 0);
	int guiColor2 = color(0, 102, 153);
	int border = 50;
	
	public void setup() {
		size(1400, 768, OPENGL);
		hint(DISABLE_OPENGL_2X_SMOOTH);
		//hint(ENABLE_OPENGL_4X_SMOOTH); //after disabling the 2x this works fine
		smooth(); // this works even better than the 4x! super-smooth! ;)
		sensorTable = new Table("KETAI_DB_Ds_1280960966045.csv");
		loadSensorNames();
		rowCount = sensorTable.getRowCount();
		guiSetup(); // make the GUI menu
		sensors = new ArrayList<Sensor>(); // create empty sensor Array of sensor objects
		sensorTypes = new ArrayList<Integer>(); // create empty sensor Array of sensor objects
		for (int row = 0; row < rowCount; row++) {
		    int type = sensorTable.getInt(row, 1);
		    if (sensorTypes.contains(type)) {
		    } else {
		      sensorTypes.add(type);
		      println("sensor type ["+type+"] added");
		      sensors.add(new Sensor(type));
		    }
		 }
		 for (int row = 0; row < rowCount; row++) {
			for (int i = 0; i < sensors.size(); i++) {
				String timeStampString = sensorTable.getString(row, 0);
				long timeStamp = Long.parseLong(timeStampString);
				if (row == 0) startTime = timeStamp;
				int type = sensorTable.getInt(row, 1);
				int index = sensorTable.getInt(row, 2);
				float value = sensorTable.getFloat(row, 3);
				// println("type: "+type+" | index: "+index+" | value: "+value);
			// pointer to current sensor
				Sensor s = (Sensor) sensors.get(i);
				if (s.type == type) {
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

	public void draw() {
		 background(0);
		  fill(255);
		  for (int i = 0; i < sensors.size(); i++) {
		    Sensor s = (Sensor) sensors.get(i);
		    s.draw();
		  }
	}
	
	// GUI

	ControlP5 controlP5;
	MultiList multiList;
	MultiListButton mlButton;
	Range range;
	
	public void guiSetup() {
	  controlP5 = new ControlP5(this);
	  multiList = controlP5.addMultiList("myNavigation",0,10,150,12);
	  mlButton = multiList.add("sensor data", 1);
	  range = controlP5.addRange("timeScale",0,100, 0,100, border,height-border,width/4,12);	}

	public void controlEvent(ControlEvent theEvent) {
	  println(theEvent.controller().name()+" = "+theEvent.value());  
	  for (int i = 0; i < sensors.size(); i++) {
	    Sensor s = (Sensor) sensors.get(i);
	    s.active(theEvent.controller().name(),theEvent.value());
	  }
	  //theEvent.controller().setLabel(theEvent.controller().name()+" klicked");
	}
	
	// SENSOR (one instance for each registered sensor)
	
	public class Sensor {
	  // CLASS VARIABLES
	  ArrayList<Row> row = new ArrayList<Row>();
	  ArrayList<Integer> indexTypes = new ArrayList<Integer>();
	  ArrayList<Long> timeStampTypes = new ArrayList<Long>();
	  Vector[] value;
	  int myColor;
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
	  Textlabel[] label = new Textlabel[6];
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
	    pushMatrix();
	    translate(0,height/2);
	    noFill();
	    for (int indexID=0; indexID<3; indexID++) { //replace 3 with indexTypes.size() to also show raw data
	      if (plotVisible) plotNormalized(indexID);
	    }
	    popMatrix();
	  }
	  // PLOT ABSOLUTE VALUES
	  void plot(int index) {
	    for (int i=0; i<value.length; i++) {
	      ellipse(map(value[i].timeStamp, 0, myDuration, border+(width-2*border)*100/range.lowValue(), (width-2*border)*100/range.highValue()),value[i].getValue(index), 3, 3);
	    }
	  }
	  // PLOT TIMELINE // ROLLOVER
	  void plotNormalized(int index) {
	    beginShape();
	    stroke(subColor(index));
	    noFill();
	    // graph
	    //translate(range.lowValue(), 0, 0);
	    for (int i=1; i<value.length; i++) {
	      float plotX = -range.lowValue()/100*(width-2*border)*100/(range.highValue()-range.lowValue())+map(value[i].timeStamp, 0, myDuration, border, (width-2*border)*100/(range.highValue()-range.lowValue()));
	      float plotY = map(value[i].getValue(index), myMin[index], myMax[index], -height/2+border, height/2-border*2);
		  value[i].setPosition(index, plotX, plotY, 0);
	      // check if value rolls over, don't connect the line then
	      if (abs(plotY-map(value[i-1].getValue(index), myMin[index], myMax[index], -height/2+border, height/2-border*2)) > height*.75) {
	        endShape();
	        beginShape();
	      }
	      vertex(plotX, plotY);
	    }
	    endShape();
	    // rollover graphics
	    for (int i=1; i<value.length; i++) {
		    if (abs(mouseX-value[i].x[index])<2){
		    	  fill(255);
				    ellipse(value[i].x[index], value[i].y[index], 3, 3);
				    // rollover label
				    for (int j=0; j<6; j++){  // j<3 : only show data 0..2, not raw data (index 3..5)
				    	label[j].setPosition((int)value[i].x[j]+2, (int)value[i].y[j]+2+height/2);
				    	label[j].setValue("["+j+"] "+value[i].timeStamp+"ms -> "+value[i].valueList[j]);
				    	label[j].setColorValue(myColor);
				    }
		    } else {
		    	  noFill();
		    }
		    pushMatrix();
		    translate(value[i].x[index], 0, 0);
		    value[i].display();
		    popMatrix();
	    }
	  }
	  // UNIQUE COLOR FOR EVERY INDEX WITHIN A SPECIFIC SENSOR COLOR
	  public int subColor(int index) {
	    return lerpColor(myColor, color(myColor, 127), (float)(index)/indexTypes.size());
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
	    myTextlabelMin = controlP5.addTextlabel("min"+type,sensorMin+"",(int)(width-1.5*border),border);
	    myTextlabelMin.setColorValue(myColor);
	    // min
	    myTextlabelMax = controlP5.addTextlabel("max"+type,sensorMax+"",(int)(width-1.5*border),height-2*border);
	    myTextlabelMax.setColorValue(myColor);
	    // zero
	    myTextlabelZero = controlP5.addTextlabel("zero"+type,"0",(int)(width-1.5*border),(int)map(0, sensorMin, sensorMax, border, height-2*border));
	    myTextlabelZero.setColorValue(myColor);
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
	      println("index ["+index+"] added for sensor type "+type);
	      // add text label for each index available
		  label[index] = controlP5.addTextlabel("label_"+type+"_"+index, "index: "+index, -100, -100);
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
	    if ((int)(value-100) == type && !name.equals("myNavigation")) {
	      if (myTextarea.isVisible()) {
	        myTextarea.hide();
	        myTextlabelMin.hide();
	        myTextlabelMax.hide();
	        myTextlabelZero.hide();
	        plotVisible = false;
	        for (int i=0; i<6; i++){
	        	label[i].hide();
	        }
	      } 
	      else {
	        myTextarea.show();
	        myTextlabelMin.show();
	        myTextlabelMax.show();
	        myTextlabelZero.show();
	        plotVisible = true;
	        for (int i=0; i<6; i++){
	        	label[i].show();
	        }
	      }
	    }
	  }
	}

	// ROW [HOLDING VALUE FOR EACH DATA ROW (timestamp, index, value) SOURCE: .csv file]
	public class Row {
	  long timeStamp;
	  int index;
	  float value;
	  Row (long _timeStamp, int _index, float _value ) {
	    timeStamp = _timeStamp;
	    index= _index;
	    value = _value;
	  }
	}
	// VECTOR [LOWEST LEVEL CLASS, STORES x, y, z in PVector value; rawX, rawY, rawZ in PVector valueRaw]
	public class Vector {
	  PVector value;
	  PVector valueRaw;
	  float[] valueList = new float[6] ;
	  long timeStamp;
	  int type;
	  float x[] = new float[6]; 
	  float y[] = new float[6]; 
	  float z[] = new float[6]; 
	  Vector (long _timeStamp, int _type) {
	    timeStamp = _timeStamp;
	    value = new PVector(0, 0, 0);
	    valueRaw = new PVector(0, 0, 0);
	    type = _type;
	  }
	  void display() {
		  stroke(255);
		  line(0, 0, 0, value.x, value.y, value.z);
	  }
	  void setPosition(int index, float _x, float _y, float _z) {
		  x[index] = _x;
		  y[index] = _y;
		  z[index] = _z;
	  }
	  void updateLabel(){
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
	      valueList[0] = input;
	      break;
	    case 1: 
	      value.set(value.x, input, value.z);
	      valueList[1] = input;
	      break;
	    case 2: 
	      value.set(value.x, value.y, input);
	      valueList[2] = input;
	      break;
	    case 3: 
	      valueRaw.set(input, valueRaw.y, valueRaw.z);
	      valueList[3] = input;
	      break;
	    case 4: 
	      valueRaw.set(valueRaw.x, input, valueRaw.z);
	      valueList[4] = input;
	      break;
	    case 5: 
	      valueRaw.set(valueRaw.x, valueRaw.y, input);
	      valueList[5] = input;
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

	// TABLE
	
	public class Table {
		  int rowCount;
		  String[][] data;
		  
		  
		  Table(String filename) {
		    String[] rows = loadStrings(filename);
		    data = new String[rows.length][];
		    
		    for (int i = 0; i < rows.length; i++) {
		      if (trim(rows[i]).length() == 0) {
		        continue; // skip empty rows
		      }
		      if (rows[i].startsWith("#")) {
		        continue;  // skip comment lines
		      }
		      
		      // split the row on the tabs
		      String[] pieces = split(rows[i], TAB);
		      // copy to the table array
		      data[rowCount] = pieces;
		      rowCount++;
		      
		      // this could be done in one fell swoop via:
		      //data[rowCount++] = split(rows[i], TAB);
		    }
		    // resize the 'data' array as necessary
		    data = (String[][]) subset(data, 0, rowCount);
		  }
		  
		  
		  int getRowCount() {
		    return rowCount;
		  }
		  
		  
		  // find a row by its name, returns -1 if no row found
		  int getRowIndex(String name) {
		    for (int i = 0; i < rowCount; i++) {
		      if (data[i][0].equals(name)) {
		        return i;
		      }
		    }
		    println("No row named '" + name + "' was found");
		    return -1;
		  }
		  
		  
		  String getRowName(int row) {
		    return getString(row, 0);
		  }


		  String getString(int rowIndex, int column) {
		    return data[rowIndex][column];
		  }

		  
		  String getString(String rowName, int column) {
		    return getString(getRowIndex(rowName), column);
		  }

		  
		  int getInt(String rowName, int column) {
		    return parseInt(getString(rowName, column));
		  }

		  
		  int getInt(int rowIndex, int column) {
		    return parseInt(getString(rowIndex, column));
		  }

		  
		  float getFloat(String rowName, int column) {
		    return parseFloat(getString(rowName, column));
		  }

		  
		  float getFloat(int rowIndex, int column) {
		    return parseFloat(getString(rowIndex, column));
		  }
		  
		  
		  void setRowName(int row, String what) {
		    data[row][0] = what;
		  }


		  void setString(int rowIndex, int column, String what) {
		    data[rowIndex][column] = what;
		  }

		  
		  void setString(String rowName, int column, String what) {
		    int rowIndex = getRowIndex(rowName);
		    data[rowIndex][column] = what;
		  }

		  
		  void setInt(int rowIndex, int column, int what) {
		    data[rowIndex][column] = str(what);
		  }

		  
		  void setInt(String rowName, int column, int what) {
		    int rowIndex = getRowIndex(rowName);
		    data[rowIndex][column] = str(what);
		  }

		  
		  void setFloat(int rowIndex, int column, float what) {
		    data[rowIndex][column] = str(what);
		  }


		  void setFloat(String rowName, int column, float what) {
		    int rowIndex = getRowIndex(rowName);
		    data[rowIndex][column] = str(what);
		  }  
		}
	
	// FULLSCREEN APP
	public static void main(String args[])
    {
      PApplet.main(new String[] { "--present", ketaiosopengl.KetaiOSOpenGL.class.getName() });
    }
}
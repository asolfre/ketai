package ketaiosopengl;

import java.util.ArrayList;
import processing.core.PApplet;
import processing.core.PVector; //import processing.opengl.*;
import controlP5.*;

public class KetaiOSOpenGL extends PApplet {

	Table sensorTable;
	int rowCount;
	ArrayList<Integer> sensorTypes; // stores index of sensor types available
	ArrayList<Sensor> sensors; // stores data for every sensor
	long startTime;
	String[] sensorName = new String[129];
	int guiColor1 = color(204, 102, 0);
	int guiColor2 = color(0, 102, 153);
	int border = 50;

	public void setup() {
		size(1400, 768, OPENGL);
		hint(DISABLE_OPENGL_2X_SMOOTH);
		// hint(ENABLE_OPENGL_4X_SMOOTH);
		sensorTable = new Table("KETAI_DB_THREEVALUES_1281638153946.csv");
		rowCount = sensorTable.getRowCount();
		guiSetup(); // make the GUI menu
		sensors = new ArrayList<Sensor>(); // create empty sensor Array
		sensorTypes = new ArrayList<Integer>(); // create empty sensorTypes
		// Array
		// parse data for sensor types
		String dataStructure = "";
		if (sensorTable.data[0].length == 4) {
			dataStructure = "TTIV";
		} else if (sensorTable.data[0].length == 5) {
			dataStructure = "TTXYZ";
		}
		loadSensorNames(dataStructure); // fullText sensor descriptions
		for (int row = 0; row < rowCount; row++) {
			int type = sensorTable.getInt(row, 1);
			if (sensorTypes.contains(type)) {
			} else {
				sensorTypes.add(type);
				println("sensor type [" + type + "] added");
				sensors.add(new Sensor(type, dataStructure));
			}
		}
		// initialize sensor object after data has been added
		for (int i = 0; i < sensors.size(); i++) {
			sensors.get(i).gui(lerpColor(guiColor1, guiColor2, sensors.get(i).type / sensors.size()));
		}
	}

	public void draw() {
		background(0);
		fill(255);
		for (int i = 0; i < sensors.size(); i++) {
			sensors.get(i).display();
		}
		// save("Ketai_Capture.tif");
		// exit();
	}

	// GUI
	ControlP5 controlP5; // global GUI
	MultiList multiList; // selecting sensor types to display - toggle
	MultiListButton mlButton;
	Range range; // range slider to determine timeline scope, in/out point

	public void guiSetup() {
		controlP5 = new ControlP5(this);
		multiList = controlP5.addMultiList("myNavigation", 0, 10, 150, 12);
		mlButton = multiList.add("sensor data", 1);
		range = controlP5.addRange("timeScale", 0, 100, 0, 10, border, height - border, width / 4, 12);
	}

	public void controlEvent(ControlEvent theEvent) {
		println(theEvent.controller().name() + " = " + theEvent.value());
		for (int i = 0; i < sensors.size(); i++) {
			Sensor s = (Sensor) sensors.get(i);
			s.active(theEvent.controller().name(), theEvent.value());
		}
		// theEvent.controller().setLabel(theEvent.controller().name()+" clicked");
	}

	// SENSOR (one instance for each registered sensor)
	public class Sensor {
		// CLASS VARIABLES
		ArrayList<Integer> indexTypes = new ArrayList<Integer>();
		ArrayList<Long> timeStampTypes = new ArrayList<Long>();
		int myColor;
		float sensorMin = MAX_FLOAT;
		float sensorMax = MIN_FLOAT;
		// store minimum values for all indexes of the sensor type
		float myMin[] = { MAX_FLOAT, MAX_FLOAT, MAX_FLOAT, MAX_FLOAT, MAX_FLOAT, MAX_FLOAT };
		// store maximum values for all indexes of the sensor type
		float myMax[] = { MIN_FLOAT, MIN_FLOAT, MIN_FLOAT, MIN_FLOAT, MIN_FLOAT, MIN_FLOAT };
		float myDuration = 0;
		int type; // sensor type
		Textarea myTextarea;
		Textlabel myTextlabelMin, myTextlabelMax, myTextlabelZero;
		Textlabel[] label = new Textlabel[6];
		boolean plotVisible = true;
		String src = "[type] : milliSeconds : index : value\n";
		Vector[] vector;

		// CONSTRUCTOR
		Sensor(int sensorType, String dataStructure) {
			type = sensorType; // int type, represents specific sensor id
			src += sensorName[type] + " | ";
			src += "DATA: "+dataStructure + "\n\n";
			// Parser for .csv data format [timeStamp | type | index | value] -> TTIV
			if (dataStructure.equals("TTIV")) {
				println(dataStructure);
				for (int row = 0; row < rowCount; row++) {
					Long timeStamp = sensorTable.getLong(row, 0);
					timeStamp /= 1000000; // converts nanoseconds into milliseconds
					if (row == 0)
						startTime = timeStamp;
					int index = sensorTable.getInt(row, 2);
					if (indexTypes.contains(index)) {
					} else {
						indexTypes.add(index);
						println("index [" + index + "] added for sensor type " + type);
						// add text label for each index available
						label[index] = controlP5.addTextlabel("label_" + type + "_" + index, "index: " + index, -100, -100);
					}
					// detect unique timestamps, create timeStamp object
					if (timeStampTypes.contains(timeStamp - startTime)) {
					} else {
						timeStampTypes.add((timeStamp - startTime));
						// ArrayList row+index = new ArrayList();
						println("timeStamp [" + (timeStamp - startTime) + "] added for sensor type " + type);
					}
				}
				// unboxing unique timeStamps
				int len = timeStampTypes.size();
				long[] timeStamps = new long[len];
				Long[] fa = new Long[len];
				timeStampTypes.toArray(fa);
				vector = new Vector[len];
				for (int i = 0; i < len; i++) {
					timeStamps[i] = fa[i];
					vector[i] = new Vector(i, timeStamps[i], type);
				}
				// parsing all rows
				for (int row = 0; row < rowCount; row++) {
					long timeStamp = sensorTable.getLong(row, 0);
					timeStamp /= 1000000; // converts nanoseconds into
					// milliseconds
					int typeVal = sensorTable.getInt(row, 1);
					int index = sensorTable.getInt(row, 2);
					float value = sensorTable.getFloat(row, 3);
					if (type == typeVal) {
						src += "[" + type + "] " + (timeStamp - startTime) + "ms : " + index + " : " + value + "\n";
						for (int j = 0; j < vector.length; j++) {
							if (timeStamps[j] == (timeStamp - startTime)) {
								vector[j].setValue(index, value);
								if (myMin[index] > value)
									myMin[index] = value;
								if (myMax[index] < value)
									myMax[index] = value;
								// for gui
								if (sensorMax < value)
									sensorMax = value;
								if (sensorMin > value)
									sensorMin = value;
								// center align all values
								if (sensorMax > abs(sensorMin)) {
									sensorMin = -abs(sensorMax);
								} else {
									sensorMax = abs(sensorMin);
								}
								if ((timeStamp - startTime) > myDuration)
									myDuration = (timeStamp - startTime);
							}
						}
					}
				}
			} else if (dataStructure.equals("TTXYZ")) {
				println(dataStructure);
				// Parser for .csv data format [timeStamp | type | index | value] -> TTIV
				indexTypes.add(0);
				label[0] = controlP5.addTextlabel("label_" + type + "_" + 0, "index: " + 0, -100, -100);
				indexTypes.add(1);
				label[1] = controlP5.addTextlabel("label_" + type + "_" + 1, "index: " + 1, -100, -100);
				indexTypes.add(2);
				label[2] = controlP5.addTextlabel("label_" + type + "_" + 2, "index: " + 2, -100, -100);
				for (int row = 0; row < rowCount; row++) {
					Long timeStamp = sensorTable.getLong(row, 0);
					timeStamp /= 1000000; // converts nanoseconds into milliseconds
					if (row == 0)
						startTime = timeStamp;
					// detect unique timestamps, create timeStamp object
					if (timeStampTypes.contains(timeStamp - startTime)) {
					} else {
						timeStampTypes.add((timeStamp - startTime));
						// ArrayList row+index = new ArrayList();
						println("timeStamp [" + (timeStamp - startTime) + "] added for sensor type " + type);
					}
				}
				// unboxing unique timeStamps
				int len = timeStampTypes.size();
				long[] timeStamps = new long[len];
				Long[] fa = new Long[len];
				timeStampTypes.toArray(fa);
				vector = new Vector[len];
				for (int i = 0; i < len; i++) {
					timeStamps[i] = fa[i];
					vector[i] = new Vector(i, timeStamps[i], type);
				}
				// parsing all rows
				for (int row = 0; row < rowCount; row++) {
					long timeStamp = sensorTable.getLong(row, 0);
					timeStamp /= 1000000; // converts nanoseconds into
					// milliseconds
					int typeVal = sensorTable.getInt(row, 1);
					float x = sensorTable.getInt(row, 2);
					float y = sensorTable.getFloat(row, 3);
					float z = sensorTable.getFloat(row, 4);
					if (type == typeVal) {
						src += "[" + type + "] " + (timeStamp - startTime) + "ms : " + x + " : " + y + " : " + z + "\n";
						for (int j = 0; j < vector.length; j++) {
							if (timeStamps[j] == (timeStamp - startTime)) {
								vector[j].setValue(0, x);
								vector[j].setValue(1, y);
								vector[j].setValue(2, z);
								if (myMin[0] > x)
									myMin[0] = x;
								if (myMax[0] < x)
									myMax[0] = x;
								if (myMin[1] > y)
									myMin[1] = y;
								if (myMax[1] < y)
									myMax[1] = y;
								if (myMin[2] > z)
									myMin[2] = z;
								if (myMax[2] < z)
									myMax[2] = z;
								// for gui
								if (sensorMax < x)
									sensorMax = x;
								if (sensorMin > x)
									sensorMin = x;
								// center align all values
								if (sensorMax > abs(sensorMin)) {
									sensorMin = -abs(sensorMax);
								} else {
									sensorMax = abs(sensorMin);
								}
								if ((timeStamp - startTime) > myDuration)
									myDuration = (timeStamp - startTime);
							}
						}
					}
				}
			}
		}

		// INIT, ASSEMBLE GUI
		void gui(int _myColor) {
			myColor = _myColor;
			// sensor-specific gui
			MultiListButton multi;
			// add sensor to global navigation
			multi = mlButton.add("sensors_" + type, 100 + type);
			multi.setLabel(type + " : " + sensorName[type]);
			// textarea for source data
			myTextarea = controlP5.addTextarea("src_" + type, "", (width - 4 * border) / sensors.size() * (type - 1) + type * border, border,
					(width - 4 * border) / sensors.size(), height - 3 * border);
			myTextarea.setText(src);
			myTextarea.setColorForeground(myColor);
			// max
			myTextlabelMin = controlP5.addTextlabel("min" + type, sensorMin + "", (int) (width - 1.5 * border), border);
			myTextlabelMin.setColorValue(myColor);
			// min
			myTextlabelMax = controlP5.addTextlabel("max" + type, sensorMax + "", (int) (width - 1.5 * border), height - 2 * border);
			myTextlabelMax.setColorValue(myColor);
			// zero
			myTextlabelZero = controlP5.addTextlabel("zero" + type, "0", (int) (width - 1.5 * border), (int) map(0, sensorMin, sensorMax, border,
					height - 2 * border));
			myTextlabelZero.setColorValue(myColor);
		}

		// DRAW GRAPHIC SENSOR COMPONENTS
		void display() {
			noStroke();
			fill(myColor);
			pushMatrix();
			translate(0, height / 2);
			noFill();
			for (int indexID = 0; indexID < 3; indexID++) { // replace 3 with indexTypes.size() to also show raw data
				if (plotVisible)
					plotNormalized(indexID);
			}
			popMatrix();
		}

		// PLOT TIMELINE // ROLLOVER
		void plotNormalized(int index) {
			// graph
			stroke(subColor(index));
			noFill();
			beginShape();
			for (int i = 1; i < vector.length; i++) {
				float plotX = -range.lowValue() * (width - 2 * border) / (range.highValue() - range.lowValue())
						+ map(vector[i].timeStamp, 0, myDuration, border, (width - 2 * border) * 100 / (range.highValue() - range.lowValue()));
				float plotY = map(vector[i].getValue(index), myMin[index], myMax[index], -height / 2 + border, height / 2 - border * 2);
				vector[i].setPosition(index, plotX, plotY, 0);
				// check if value rolls over, don't connect the line then
				if (abs(plotY - map(vector[i - 1].getValue(index), myMin[index], myMax[index], -height / 2 + border, height / 2 - border * 2)) > height * .75) {
					endShape();
					beginShape();
				}
				vertex(plotX, plotY);
			}
			endShape();
			// rollover graphics
			for (int i = 0; i < vector.length; i++) {
				if (abs(mouseX - vector[i].x[index]) < 100 / (range.highValue() - range.lowValue())) {
					// on timeScale
					noStroke();
					if (index == 0 || index == 3) {
						fill(255, 0, 0);
					} else if (index == 1 || index == 4) {
						fill(0, 255, 0);
					} else if (index == 2 || index == 5) {
						fill(0, 0, 255);
					}
					ellipse(vector[i].x[index], vector[i].y[index], 4, 4);
					stroke(255);
					point(vector[i].x[index], vector[i].y[index]);
					// rollover label
					for (int j = 0; j < 3; j++) { // j<3 : only show data 0..2,
						// not raw data (index 3..5)
						label[j].setPosition((int) vector[i].x[j] + 2, (int) vector[i].y[j] + 2 + height / 2);
						label[j].setValue("[" + j + "] " + vector[i].timeStamp + "ms -> " + vector[i].valueList[j]);
						label[j].setColorValue(myColor);
					}
				} else {
					noFill();
				}
				if (mousePressed && mouseY < height - border) {
					if (abs(mouseX - vector[i].x[index]) < 100 / (range.highValue() - range.lowValue())) {
						// plot the vector visuzlization over timeline
						pushMatrix();
						translate(vector[i].x[index], 0, 0);
						vector[i].display(20); // scale factor (10)
						popMatrix();
					}
				} else {
					// plot the vector visuzlization over timeline
					pushMatrix();
					translate(vector[i].x[index], 0, 0);
					vector[i].display(20); // scale factor (10)
					popMatrix();
				}
			}
		}

		// UNIQUE COLOR FOR EVERY INDEX WITHIN A SPECIFIC SENSOR COLOR
		public int subColor(int index) {
			return lerpColor(myColor, color(myColor, 127), (float) (index) / indexTypes.size());
		}

		// SENSOR NAME/DESCRIPITON
		String sensorName(int type) {
			return sensorName[type];
		}

		// NORMALIZING VALUES -> WARNING: NOT CONSISTENT THROUGH ALL ROWS
		void normalizeValues() {
			for (int i = 0; i < vector.length; i++) {
				vector[i].normalizeValues();
			}
		}

		// TOGGLE FOR GUI
		void active(String name, float value) {
			if ((int) (value - 100) == type && !name.equals("myNavigation")) {
				if (myTextarea.isVisible()) {
					myTextarea.hide();
					myTextlabelMin.hide();
					myTextlabelMax.hide();
					myTextlabelZero.hide();
					plotVisible = false;
					for (int i = 0; i < 6; i++) {
						label[i].hide();
					}
				} else {
					myTextarea.show();
					myTextlabelMin.show();
					myTextlabelMax.show();
					myTextlabelZero.show();
					plotVisible = true;
					for (int i = 0; i < 6; i++) {
						label[i].show();
					}
				}
			}
		}

		void setColor(int col_) {
			myColor = col_;
		}
	}

	// VECTOR [LOWEST LEVEL CLASS, STORES x, y, z in PVector value; rawX, rawY,
	// rawZ in PVector valueRaw]
	public class Vector {
		PVector value;
		PVector valueRaw;
		float[] valueList = new float[6];
		long timeStamp;
		int id;
		int type;
		float x[] = new float[6];
		float y[] = new float[6];
		float z[] = new float[6];

		Vector(int _id, long _timeStamp, int _type) {
			id = _id;
			timeStamp = _timeStamp;
			value = new PVector(0, 0, 0);
			valueRaw = new PVector(0, 0, 0);
			type = _type;
		}

		void display(int mag) {
			// adjusting coordinate system to match device coordinate system http://developer.android.com/reference/android/hardware/SensorEvent.html
			rotateX(HALF_PI); // turning y axis into z to match device
			rotateZ(PI);
			scale(1, -1, 1); // flip y-axis

			PVector origin = new PVector(0, 0, 0); // origin, here (0|0|0);
			PVector delta = new PVector(0, 0, 0); // determined by the origin
			// point and value point (for vectors away from origin)

			delta.x = origin.x - value.x;
			delta.y = origin.y - value.y;
			delta.z = origin.z - value.z;

			pushMatrix();
			scale(mag);

			// static/normal matrix
			if (rollOver()) {
				// value.x Vector
				stroke(255, 0, 0, 127);
				line(origin.x, origin.y, origin.z, value.x, 0, 0);
				// value.y Vector
				stroke(0, 255, 0, 127);
				line(origin.x, origin.y, origin.z, 0, value.y, 0);
				// value.z Vector
				stroke(0, 0, 255, 127);
				line(origin.x, origin.y, origin.z, 0, 0, value.z);
				// value Vector
				// line(origin.x,origin.y,origin.z,value.x,value.y,value.z);
			}
			stroke(255, 255, 0, 127);
			// rotated matrix
			float r = sqrt(sq(delta.x) + sq(delta.y) + sq(delta.z));
			float theta = atan2(delta.y, delta.x);
			float phi = acos(delta.z / r);
			translate(origin.x, origin.y, origin.z);
			// if the normal plans should be on the vector tip at the position value translate(value.x,value.y,value.z);
			rotateZ(theta);
			rotateY(phi);
			rotateX(-HALF_PI);
			// ds added "correction rotation rotateY(-theta);
			stroke(255, 255, 255, 127);
			box(2f, .01f, 1f);
			noFill();
			line(0, 0, 0, 0, delta.mag(), 0); // draw y axis in new Marix orientation

			noStroke();
			// display
			if (rollOver()) {
				fill(255);
			} else {
				fill(255, 127);
			}
			beginShape();
			vertex(-.8f, .01f, -.4f);
			vertex(.8f, .01f, -.4f);
			vertex(.8f, .01f, .4f);
			vertex(-.8f, .01f, .4f);
			endShape();
			// top
			fill(127, 100);
			beginShape();
			vertex(-1f, 0, -.5f);
			vertex(1f, 0, -.5f);
			vertex(1f, 0, .5f);
			vertex(-1f, 0, .5f);
			endShape();
			fill(127, 255);
			// bottom
			beginShape();
			vertex(-1f, -.1f, -.5f);
			vertex(1f, -.1f, -.5f);
			vertex(1f, -.1f, .5f);
			vertex(-1f, -.1f, .5f);
			endShape();
			// front
			fill(127, 100);
			beginShape();
			vertex(-1f, 0, -.5f);
			vertex(1f, 0, -.5f);
			vertex(1f, -.1f, -.5f);
			vertex(-1f, -.1f, -.5f);
			endShape();
			// back
			fill(127, 100);
			beginShape();
			vertex(-1f, 0, .5f);
			vertex(1f, 0, .5f);
			vertex(1f, -.1f, .5f);
			vertex(-1f, -.1f, .5f);
			endShape();
			// left
			fill(127, 100);
			beginShape();
			vertex(-1f, 0, -.5f);
			vertex(-1f, 0, .5f);
			vertex(-1f, -.1f, .5f);
			vertex(-1f, -.1f, -.5f);
			endShape();
			// right
			fill(127, 100);
			beginShape();
			vertex(.1f, 0, -.5f);
			vertex(.1f, 0, .5f);
			vertex(.1f, -.1f, .5f);
			vertex(.1f, -.1f, -.5f);
			endShape();
			popMatrix();
		}

		void setPosition(int index, float _x, float _y, float _z) {
			x[index] = _x;
			y[index] = _y;
			z[index] = _z;
		}

		boolean rollOver() {
			if (abs(mouseX - x[0]) < 100 / (range.highValue() - range.lowValue())) {
				return true;
			} else {
				return false;
			}
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

		// .csv formatting [timeStamp, type, index, value] TTIV
		void setValue(int index, float input) {
			switch (index) {
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
				println("invalid index: " + input); // Does not execute
				break;
			}
		}

		float getValue(int index) {
			switch (index) {
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
				println("invalid index: " + index); // Does not execute
				return -1;
			}
		}

		void normalizeValues() {
			value.normalize();
			valueRaw.normalize();
		}
	}

	// SENSOR NAMES (id to text)
	void loadSensorNames(String dataStructure) {
		if (dataStructure.equals("TTIV")) {
			sensorName[1] = "SENSOR_ORIENTATION";
			sensorName[2] = "SENSOR_ACCELEROMETER";
			sensorName[4] = "SENSOR_TEMPERATURE";
			sensorName[8] = "SENSOR_MAGNETIC_FIELD";
			sensorName[16] = "SENSOR_LIGHT";
			sensorName[32] = "SENSOR_PROXIMITY";
			sensorName[64] = "SENSOR_TRICORDER";
			sensorName[128] = "SENSOR_ORIENTATION_RAW";
		} else if (dataStructure.equals("TTXYZ")) {
			sensorName[2] = "SENSOR_MAGNETIC_FIELD";
			sensorName[1] = "SENSOR_ACCELEROMETER";
			sensorName[3] = "SENSOR_ORIENTATION";
		}
	}

	// 

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
					continue; // skip comment lines
				}

				// split the row on the tabs
				String[] pieces = split(rows[i], TAB);
				// copy to the table array
				data[rowCount] = pieces;
				rowCount++;

				// this could be done in one fell swoop via: data[rowCount++] = split(rows[i], TAB);
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

		// added DS
		long getLong(String rowName, int column) {
			return Long.parseLong(getString(rowName, column));
		}

		// added DS
		long getLong(int rowIndex, int column) {
			return Long.parseLong(getString(rowIndex, column));
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
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", ketaiosopengl.KetaiOSOpenGL.class.getName() });
	}
}

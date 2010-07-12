// stores everything for one sensor type
class Sensor {
  ArrayList row = new ArrayList();
  int type;
  Textarea myTextarea;
  String src = "";
  Sensor(int sensorType) {
    type = sensorType;        // int type, represents specific sensor id
    gui();
    myTextarea = controlP5.addTextarea("src", "", type*150,50+type*20,600,height-150);
  }
  // show points
  void init() {
    for (int i=0; i<row.size(); i++) {
      Value v = (Value) row.get(i);
      fill(200);
      text(v.timeStamp+" : "+v.index+" : "+v.value+"", type, i*12);
      src += v.timeStamp+" : "+v.index+" : "+v.value+"\n";
    }
    myTextarea.setText(src);
    myTextarea.setColorForeground(lerpColor(color(204, 102, 0), color(0, 102, 153), type/sensors.size()));
  }
  // add row from .csv file
  void addValue(float timeStamp, int index, float value) {
    row.add(new Value(timeStamp, index, value));
  }
  // full name of sensor
  String sensorName(int type) {
    return sensorNames[type];
  }
  int getType() {
    return type;
  }
  int getSize() {
    return row.size();
  }
  void gui() {
    MultiListButton multi;
    multi = mlButton.add("sensors_"+type,100+type);
    multi.setLabel(type+" : "+sensorNames[type-1]);
  }
  void active(String name, float value) {
    if (int(value-100) == type && !name.equals("myList")) {
      if (myTextarea.isVisible()) {
        myTextarea.hide();
      } 
      else {
        myTextarea.show();
      }
    }
  }
}

// stores one row (timestamp, index, value) from .csv file
class Value {
  float timeStamp;
  int index;
  float value;
  Value (float _timeStamp, int _index, float _value ) {
    timeStamp = _timeStamp;
    index= _index;
    value = _value;
  }
}


import controlP5.*;
ControlP5 controlP5;
MultiList multiList;
MultiListButton mlButton;
PFont font;

void guiSetup() {
  font = createFont("Helvetica", 10);
  textFont(font);
  controlP5 = new ControlP5(this);
  multiList = controlP5.addMultiList("myList",0,10,100,12);
  int cnt = 100;
  mlButton = multiList.add("sensors", 1);
  //cnt++;
  //for(int j=0;j<sensors.size();j++) { // fix lenght
  //cnt++;
  //MultiListButton multi;
  //multi = mlButton.add("sensors_"+j,100+j+1);
  //multi.setLabel(sensorTypes.get(j)+" : "+sensorNames[int(""+sensorTypes.get(j))-1]);
  //multi.setId(cnt);
  //}
}

void controlEvent(ControlEvent theEvent) {
  println(theEvent.controller().name()+" = "+theEvent.value());  
  for (int i = 0; i < sensors.size(); i++) {
    Sensor s = (Sensor) sensors.get(i);
    s.active(theEvent.controller().name(),theEvent.value());
  }
  //theEvent.controller().setLabel(theEvent.controller().name()+" klicked");
}


import android.hardware.SensorEvent;

class CustomAnalyzer extends AbstractKetaiAnalyzer
{

  CustomAnalyzer()
  {
    println("CustomAnalyzer constructor");
  }

  String getAnalyzerName() {
    return "Sample Custom Analyzer";
  }

  String getAnalysisDescription()
  {
    return "Checks to see if the device is laying flat based on accelerometer data";
  }     

  void analyzeData(Object dataSet)
  {
    if(dataSet instanceof SensorEvent)
    {
      SensorEvent e = (SensorEvent)dataSet;
      //parse the Sensor Event object and check
      //  the 'z' axis data
      if(e.values[2] > 9 && e.values[2] < 11)
      {
        println("broadcasting flatness..");
        broadcastKetaiEvent("flat", null);
      }
      else
      {
        println("broadcasting non-flatness..");
        broadcastKetaiEvent("notflat", null);
      }
    }
  }

  //We're not collecting data
  String getTableCreationString()
  {
    return "";
  }

  Class<?> getServiceProviderClass() { 
    try {
      return Class
        .forName("edu.uic.ketai.inputService.KetaiSensor");
    } 
    catch (ClassNotFoundException e) {
      e.printStackTrace();
      return null;
    }
  }
}


import edu.uic.ketai.analyzer.AbstractKetaiAnalyzer;
import android.hardware.SensorEvent;

class CustomAnalyzer extends AbstractKetaiAnalyzer
{

  CustomAnalyzer()
  {
    println("CustomAnalyzer constructor");
  }

  String getAnalyzerName() {
    return "";
  }

  String getAnalysisDescription()
  {
    return "";
  }     

  void analyzeData(Object dataSet)
  {
    if(dataSet instanceof SensorEvent)
    {
      SensorEvent e = (SensorEvent)dataSet;
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

  String getTableCreationString()
  {
    return "";
  }

  Class<?> getServiceProviderClass() { 
    try {
      return Class
        .forName("edu.uic.ketai.inputService.KetaiSensorManager");
    } 
    catch (ClassNotFoundException e) {
      e.printStackTrace();
      return null;
    }
  }
}


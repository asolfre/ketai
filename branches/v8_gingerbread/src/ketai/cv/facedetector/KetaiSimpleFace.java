package ketai.cv.facedetector;

import processing.core.PVector;
import android.graphics.PointF;
import android.media.FaceDetector.Face;

public class KetaiSimpleFace {
    public PVector location;
    public float distance;
    public float confidence;

    public KetaiSimpleFace(Face f) {
            PointF p = new PointF();
            f.getMidPoint(p);
            location = new PVector(p.x, p.y);
            distance = f.eyesDistance();

    }
}

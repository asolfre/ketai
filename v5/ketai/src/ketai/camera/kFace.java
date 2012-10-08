package ketai.camera;

/* 
 * wrapper for android Face class
 */
import processing.core.PVector;
import android.graphics.PointF;
import android.media.FaceDetector.Face;

public class kFace {
	public PVector location;
	public float distance;
	public float confidence;

	public kFace(Face f) {
		PointF p = new PointF();
		f.getMidPoint(p);
		location = new PVector(p.x, p.y);
		distance = f.eyesDistance();

	}
}

package ketai.camera;

import java.util.ArrayList;

import processing.core.PImage;
import android.media.FaceDetector;
import android.graphics.Bitmap;
import android.media.FaceDetector.Face;

public class FaceFinder {

	public static Face[] findFaces(PImage _p, int MAX_FACES) {
		ArrayList<Face> foundFaces = new ArrayList<Face>();
		int numberOfFaces = 0;

		android.graphics.Bitmap _bitmap = Bitmap.createBitmap(_p.pixels,
				_p.width, _p.height, Bitmap.Config.RGB_565);

		if (_bitmap != null) {
			FaceDetector _detector = new FaceDetector(_p.width, _p.height,
					MAX_FACES);
			Face[] faces = new Face[MAX_FACES];

			numberOfFaces = _detector.findFaces(_bitmap, faces);

			for (int i = 0; i < numberOfFaces; i++) {
				foundFaces.add(faces[i]);
			}
		}
		Face[] f = new Face[numberOfFaces];
		for (int i = 0; i < numberOfFaces; i++) {
			f[i] = foundFaces.get(i);
		}
		return f;
	}

	public static Face[] findFaces(PImage _p) {
		int DEFAULT_MAX_FACES = 5;
		return findFaces(_p, DEFAULT_MAX_FACES);
	}
}

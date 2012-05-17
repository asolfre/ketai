package ketai.cv.facedetector;

import java.util.ArrayList;

import processing.core.PImage;
import android.graphics.Bitmap;
import android.media.FaceDetector;
import android.media.FaceDetector.Face;

public class KetaiFaceDetector {
	public static KetaiSimpleFace[] findFaces(PImage _p, int MAX_FACES) {
		ArrayList<KetaiSimpleFace> foundFaces = new ArrayList<KetaiSimpleFace>();
		int numberOfFaces = 0;

		android.graphics.Bitmap _bitmap = Bitmap.createBitmap(_p.pixels,
				_p.width, _p.height, Bitmap.Config.RGB_565);

		if (_bitmap != null) {
			FaceDetector _detector = new FaceDetector(_p.width, _p.height,
					MAX_FACES);
			Face[] faces = new Face[MAX_FACES];

			numberOfFaces = _detector.findFaces(_bitmap, faces);

			for (int i = 0; i < numberOfFaces; i++) {
				foundFaces.add(new KetaiSimpleFace(faces[i]));
			}
		}
		KetaiSimpleFace[] f = new KetaiSimpleFace[numberOfFaces];
		for (int i = 0; i < numberOfFaces; i++) {
			f[i] = foundFaces.get(i);
		}
		return f;
	}

	public static KetaiSimpleFace[] findFaces(PImage _p) {
		int DEFAULT_MAX_FACES = 5;
		return findFaces(_p, DEFAULT_MAX_FACES);
	}
}

package edu.uic.ketai.inputService;

import processing.core.*;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.content.Context;

public class KetaiSensorManager extends AbstractKetaiInputService implements
		SensorEventListener {

	private SensorManager sensorManager = null;
	private boolean isRegistered = false;
	private PApplet parent;
	private Method onSensorEventMethod;
	private Method onAccelerometerSensorEventMethod,
			onMagneticFieldSensorEventMethod, onOrientationSensorEventMethod,
			onLightSensorEventMethod, onProximitySensorEventMethod,
			onGyroscopeSensorEventMethod, onPressureSensorEventMethod,
			onTemperatureSensorEventMethod, onRotationVectorSensorEventMethod,
			onLinearAccelerationSensorEventMethod;
	private boolean accelerometerSensorEnabled, magneticFieldSensorEnabled,
			orientationSensorEnabled, proximitySensorEnabled;
	private boolean lightSensorEnabled, pressureSensorEnabled,
			temperatureSensorEnabled, gyroscopeSensorEnabled,
			rotationVectorSensorEnabled, linearAccelerationSensorEnabled;
	private long delayInterval, timeOfLastUpdate;
	final static String SERVICE_DESCRIPTION = "Android Sensors.";

	public KetaiSensorManager(PApplet pParent) {
		parent = pParent;
		sensorManager = (SensorManager) parent.getApplicationContext()
				.getSystemService(Context.SENSOR_SERVICE);
		PApplet.println("KetaiSensorManager instantiated...");
		findParentIntentions();

		delayInterval = timeOfLastUpdate = 0;
	}

	public void setDelayInterval(long pDelayInterval) {
		delayInterval = pDelayInterval;
	}

	public void enableAccelerometerSensor() {
		accelerometerSensorEnabled = true;
	}

	public void enableRotationVectorSensor() {
		rotationVectorSensorEnabled = true;
	}

	public void enableLinearAccelerationSensor() {
		linearAccelerationSensorEnabled = true;
	}

	public void disableAccelerometerSensor() {
		accelerometerSensorEnabled = true;
	}

	public void enableMagenticFieldSensor() {
		magneticFieldSensorEnabled = true;
	}

	public void disableMagneticFieldSensor() {
		magneticFieldSensorEnabled = true;
	}

	public void enableOrientationSensor() {
		orientationSensorEnabled = true;
	}

	public void disableOrientationSensor() {
		orientationSensorEnabled = false;
	}

	public void enableProximitySensor() {
		proximitySensorEnabled = true;
	}

	public void disableProximitySensor() {
		proximitySensorEnabled = false;
	}

	public void disablelinearAccelerationSensor() {
		linearAccelerationSensorEnabled = false;
	}

	public void disableRotationVectorSensor() {
		rotationVectorSensorEnabled = false;
	}

	public void enableLightSensor() {
		lightSensorEnabled = true;
	}

	public void disableLightSensor() {
		lightSensorEnabled = true;
	}

	public void enablePressureSensor() {
		pressureSensorEnabled = true;
	}

	public void disablePressureSensor() {
		pressureSensorEnabled = true;
	}

	public void enableTemperatureSensor() {
		temperatureSensorEnabled = true;
	}

	public void disableTemperatureSensor() {
		temperatureSensorEnabled = false;
	}

	public void enableGyroscopeSensor() {
		gyroscopeSensorEnabled = true;
	}

	public void disableGyroscopeSensor() {
		gyroscopeSensorEnabled = false;
	}

	public void enableAllSensors() {
		accelerometerSensorEnabled = magneticFieldSensorEnabled = orientationSensorEnabled = proximitySensorEnabled = lightSensorEnabled = pressureSensorEnabled = temperatureSensorEnabled = gyroscopeSensorEnabled = linearAccelerationSensorEnabled = rotationVectorSensorEnabled = true;
	}

	public boolean isAccelerometerSensorAvailable() {
		return isSensorSupported(Sensor.TYPE_ACCELEROMETER);
	}

	public boolean isLinearAccelerationSensorAvailable() {
		return isSensorSupported(Sensor.TYPE_LINEAR_ACCELERATION);
	}

	public boolean isRotationVectorSensorAvailable() {
		return isSensorSupported(Sensor.TYPE_ROTATION_VECTOR);
	}

	public boolean isMagenticFieldSensorAvailable() {
		return isSensorSupported(Sensor.TYPE_MAGNETIC_FIELD);
	}

	public boolean isOrientationSensorAvailable() {
		return isSensorSupported(Sensor.TYPE_ORIENTATION);
	}

	public boolean isProximitySensorAvailable() {
		return isSensorSupported(Sensor.TYPE_PROXIMITY);
	}

	public boolean isLightSensorAvailable() {
		return isSensorSupported(Sensor.TYPE_LIGHT);
	}

	public boolean isPressureSensorAvailable() {
		return isSensorSupported(Sensor.TYPE_PRESSURE);
	}

	public boolean isTemperatureSensorAvailable() {
		return isSensorSupported(Sensor.TYPE_TEMPERATURE);
	}

	public boolean isGyroscopeSensorAvailable() {
		return isSensorSupported(Sensor.TYPE_GYROSCOPE);
	}

	public String[] list() {
		Vector<String> list = new Vector<String>();

		List<Sensor> foo = sensorManager.getSensorList(Sensor.TYPE_ALL);
		for (Sensor s : foo) {
			list.add(s.getName());
			PApplet.println("KetaiSensorManager sensor list: " + s.getName()
					+ ":" + s.getType());
		}
		String returnList[] = new String[list.size()];
		list.copyInto(returnList);
		return returnList;
	}

	public boolean isStarted() {
		return isRegistered;
	}

	public void start() {
		PApplet.println("KetaiSensorManager: start()...");
		findParentIntentions();

		if (accelerometerSensorEnabled) {
			Sensor s = sensorManager
					.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			sensorManager.registerListener(this, s,
					SensorManager.SENSOR_DELAY_FASTEST);
		}
		if (magneticFieldSensorEnabled) {
			Sensor s = sensorManager
					.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
			sensorManager.registerListener(this, s,
					SensorManager.SENSOR_DELAY_FASTEST);
		}
		if (pressureSensorEnabled) {
			Sensor s = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
			sensorManager.registerListener(this, s,
					SensorManager.SENSOR_DELAY_FASTEST);
		}
		if (orientationSensorEnabled) {
			Sensor s = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
			sensorManager.registerListener(this, s,
					SensorManager.SENSOR_DELAY_FASTEST);
		}
		if (proximitySensorEnabled) {
			Sensor s = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
			sensorManager.registerListener(this, s,
					SensorManager.SENSOR_DELAY_FASTEST);
		}
		if (temperatureSensorEnabled) {
			Sensor s = sensorManager.getDefaultSensor(Sensor.TYPE_TEMPERATURE);
			sensorManager.registerListener(this, s,
					SensorManager.SENSOR_DELAY_FASTEST);
		}
		if (gyroscopeSensorEnabled) {
			Sensor s = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
			sensorManager.registerListener(this, s,
					SensorManager.SENSOR_DELAY_FASTEST);
		}
		if (rotationVectorSensorEnabled) {
			Sensor s = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
			sensorManager.registerListener(this, s,
					SensorManager.SENSOR_DELAY_FASTEST);
		}		
		if (linearAccelerationSensorEnabled) {
			Sensor s = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
			sensorManager.registerListener(this, s,
					SensorManager.SENSOR_DELAY_FASTEST);
		}
		if (lightSensorEnabled) {
			Sensor s = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
			sensorManager.registerListener(this, s,
					SensorManager.SENSOR_DELAY_FASTEST);
		}
		isRegistered = true;
	}

	public void stop() {
		PApplet.println("KetaiSensorManager: Stop()....");
		sensorManager.unregisterListener(this);
		isRegistered = false;
	}

	public void onSensorChanged(SensorEvent arg0) {

		Date date = new Date();
		long now = date.getTime();
		// PApplet.println("onSensorChanged: " + arg0.timestamp + ":" +
		// arg0.sensor.getType());

		if (now < timeOfLastUpdate + delayInterval)
			return;

		timeOfLastUpdate = now;

		broadcastSensorEvent(arg0);

		if (onSensorEventMethod != null) {
			try {
				onSensorEventMethod.invoke(parent, new Object[] { arg0 });
				return;
			} catch (Exception e) {
				PApplet.println("Disabling onSensorEvent() because of an error:"
						+ e.getMessage());
				e.printStackTrace();
				onSensorEventMethod = null;
			}
		}

		if (arg0.sensor.getType() == Sensor.TYPE_ACCELEROMETER
				&& accelerometerSensorEnabled
				&& onAccelerometerSensorEventMethod != null) {
			try {
				onAccelerometerSensorEventMethod.invoke(parent, new Object[] {
						arg0.timestamp, arg0.accuracy, arg0.values[0],
						arg0.values[1], arg0.values[2] });
				timeOfLastUpdate = now;

				broadcastData(arg0);
				return;
			} catch (Exception e) {
				PApplet.println("Disabling onAccelerometerSensorEvent() because of an error:"
						+ e.getMessage());
				e.printStackTrace();
				onAccelerometerSensorEventMethod = null;
			}
		}

		if (arg0.sensor.getType() == Sensor.TYPE_ORIENTATION
				&& orientationSensorEnabled
				&& onOrientationSensorEventMethod != null) {
			try {
				onOrientationSensorEventMethod.invoke(parent, new Object[] {
						arg0.timestamp, arg0.accuracy, arg0.values[0],
						arg0.values[1], arg0.values[2] });
				timeOfLastUpdate = now;
				return;
			} catch (Exception e) {
				PApplet.println("Disabling onOrientationSensorEvent() because of an error:"
						+ e.getMessage());
				e.printStackTrace();
				onOrientationSensorEventMethod = null;
			}
		}

		if (arg0.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD
				&& magneticFieldSensorEnabled
				&& onMagneticFieldSensorEventMethod != null) {
			try {
				onMagneticFieldSensorEventMethod.invoke(parent, new Object[] {
						arg0.timestamp, arg0.accuracy, arg0.values[0],
						arg0.values[1], arg0.values[2] });
				timeOfLastUpdate = now;
				return;
			} catch (Exception e) {
				PApplet.println("Disabling onMagneticFieldSensorEvent() because of an error:"
						+ e.getMessage());
				e.printStackTrace();
				onMagneticFieldSensorEventMethod = null;
			}
		}

		if (arg0.sensor.getType() == Sensor.TYPE_GYROSCOPE
				&& gyroscopeSensorEnabled
				&& onGyroscopeSensorEventMethod != null) {
			try {
				onGyroscopeSensorEventMethod.invoke(parent, new Object[] {
						arg0.timestamp, arg0.accuracy, arg0.values[0],
						arg0.values[1], arg0.values[2] });
				timeOfLastUpdate = now;
				return;
			} catch (Exception e) {
				PApplet.println("Disabling onGyroscopeSensorEvent() because of an error:"
						+ e.getMessage());
				e.printStackTrace();
				onGyroscopeSensorEventMethod = null;
			}
		}

		if (arg0.sensor.getType() == Sensor.TYPE_LIGHT && lightSensorEnabled
				&& onLightSensorEventMethod != null) {
			try {
				onLightSensorEventMethod.invoke(parent, new Object[] {
						arg0.timestamp, arg0.accuracy, arg0.values[0] });
				timeOfLastUpdate = now;
				return;
			} catch (Exception e) {
				PApplet.println("Disabling onLightSensorEvent() because of an error:"
						+ e.getMessage());
				e.printStackTrace();
				onLightSensorEventMethod = null;
			}
		}

		if (arg0.sensor.getType() == Sensor.TYPE_PROXIMITY
				&& proximitySensorEnabled
				&& onProximitySensorEventMethod != null) {
			try {
				onProximitySensorEventMethod.invoke(parent, new Object[] {
						arg0.timestamp, arg0.accuracy, arg0.values[0] });
				timeOfLastUpdate = now;
				return;
			} catch (Exception e) {
				PApplet.println("Disabling onProximitySensorEvent() because of an error:"
						+ e.getMessage());
				e.printStackTrace();
				onProximitySensorEventMethod = null;
			}
		}

		if (arg0.sensor.getType() == Sensor.TYPE_PRESSURE
				&& pressureSensorEnabled && onPressureSensorEventMethod != null) {
			try {
				onPressureSensorEventMethod.invoke(parent, new Object[] {
						arg0.timestamp, arg0.accuracy, arg0.values[0] });
				timeOfLastUpdate = now;
				return;
			} catch (Exception e) {
				PApplet.println("Disabling onPressureSensorEvent() because of an error:"
						+ e.getMessage());
				e.printStackTrace();
				onPressureSensorEventMethod = null;
			}
		}

		if (arg0.sensor.getType() == Sensor.TYPE_TEMPERATURE
				&& temperatureSensorEnabled
				&& onTemperatureSensorEventMethod != null) {
			try {
				onTemperatureSensorEventMethod.invoke(parent, new Object[] {
						arg0.timestamp, arg0.accuracy, arg0.values[0] });
				timeOfLastUpdate = now;
				return;
			} catch (Exception e) {
				PApplet.println("Disabling onTemperatureSensorEvent() because of an error:"
						+ e.getMessage());
				e.printStackTrace();
				onTemperatureSensorEventMethod = null;
			}
		}

		if (arg0.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION
				&& linearAccelerationSensorEnabled
				&& onLinearAccelerationSensorEventMethod != null) {
			try {
				onLinearAccelerationSensorEventMethod.invoke(parent, new Object[] {
						arg0.timestamp, arg0.accuracy, arg0.values[0], arg0.values[1], arg0.values[2] });
				timeOfLastUpdate = now;
				return;
			} catch (Exception e) {
				PApplet.println("Disabling onLinearAccelerationSensorEvent() because of an error:"
						+ e.getMessage());
				e.printStackTrace();
				onLinearAccelerationSensorEventMethod = null;
			}
		}

		if (arg0.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR
				&& rotationVectorSensorEnabled
				&& onRotationVectorSensorEventMethod != null) {
			try {
				onRotationVectorSensorEventMethod.invoke(parent, new Object[] {
						arg0.timestamp, arg0.accuracy, arg0.values[0], arg0.values[1], arg0.values[2] });
				timeOfLastUpdate = now;
				return;
			} catch (Exception e) {
				PApplet.println("Disabling onRotationVectorSensorEvent() because of an error:"
						+ e.getMessage());
				e.printStackTrace();
				onRotationVectorSensorEventMethod = null;
			}
		}
		
	}

	private void broadcastSensorEvent(SensorEvent arg0) {
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	private boolean isSensorSupported(int type) {
		List<Sensor> foo = sensorManager.getSensorList(Sensor.TYPE_ALL);
		for (Sensor s : foo) {
			if (type == s.getType())
				return true;
		}
		return false;
	}

	private void findParentIntentions() {
		try {
			onSensorEventMethod = parent.getClass().getMethod("onSensorEvent",
					new Class[] { SensorEvent.class });
		} catch (NoSuchMethodException e) {
		}

		try {
			onAccelerometerSensorEventMethod = parent.getClass().getMethod(
					"onAccelerometerSensorEvent",
					new Class[] { long.class, int.class, float.class,
							float.class, float.class });
			accelerometerSensorEnabled = true;
			PApplet.println("Found onAccelerometerSensorEvenMethod...");

		} catch (NoSuchMethodException e) {
		}

		try {
			onOrientationSensorEventMethod = parent.getClass().getMethod(
					"onOrientationSensorEvent",
					new Class[] { long.class, int.class, float.class,
							float.class, float.class });
			orientationSensorEnabled = true;
		} catch (NoSuchMethodException e) {
		}

		try {
			onMagneticFieldSensorEventMethod = parent.getClass().getMethod(
					"onMagneticFieldSensorEvent",
					new Class[] { long.class, int.class, float.class,
							float.class, float.class });
			magneticFieldSensorEnabled = true;
		} catch (NoSuchMethodException e) {
		}

		try {
			onGyroscopeSensorEventMethod = parent.getClass().getMethod(
					"onGyroscopeSensorEvent",
					new Class[] { long.class, int.class, float.class,
							float.class, float.class });
			gyroscopeSensorEnabled = true;
			PApplet.println("Found onGyroscopeSensorEvenMethod...");
		} catch (NoSuchMethodException e) {
		}

		try {
			onProximitySensorEventMethod = parent.getClass().getMethod(
					"onProximitySensorEvent",
					new Class[] { long.class, int.class, float.class });
			proximitySensorEnabled = true;
		} catch (NoSuchMethodException e) {
		}

		try {
			onLightSensorEventMethod = parent.getClass().getMethod(
					"onLightSensorEvent",
					new Class[] { long.class, int.class, float.class });
			lightSensorEnabled = true;
		} catch (NoSuchMethodException e) {
		}

		try {
			onPressureSensorEventMethod = parent.getClass().getMethod(
					"onPressureSensorEvent",
					new Class[] { long.class, int.class, float.class });
			pressureSensorEnabled = true;
		} catch (NoSuchMethodException e) {
		}

		try {
			onTemperatureSensorEventMethod = parent.getClass().getMethod(
					"onTemperatureSensorEvent",
					new Class[] { long.class, int.class, float.class });
			temperatureSensorEnabled = true;
		} catch (NoSuchMethodException e) {
		}
		
		try {
			onLinearAccelerationSensorEventMethod = parent.getClass().getMethod(
					"onLinearAccelerationSensorEvent",
					new Class[] { long.class, int.class, float.class,
							float.class, float.class });
			linearAccelerationSensorEnabled = true;
			PApplet.println("Found onLinearAccelerationSensorEventMethod...");

		} catch (NoSuchMethodException e) {
		}

		try {
			onRotationVectorSensorEventMethod = parent.getClass().getMethod(
					"onRotationVectorSensorEvent",
					new Class[] { long.class, int.class, float.class,
							float.class, float.class });
			rotationVectorSensorEnabled = true;
			PApplet.println("Found onRotationVectorSensorEvenMethod...");

		} catch (NoSuchMethodException e) {
		}

	}

	public void startService() {
		start();
	}

	public int getStatus() {
		return 0;
	}

	public void stopService() {
		stop();
	}

	public String getServiceDescription() {
		return SERVICE_DESCRIPTION;
	}
}

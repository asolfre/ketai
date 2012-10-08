package ketai.sensors;

import processing.core.*;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.content.Context;

public class KetaiSensor implements SensorEventListener {

	private SensorManager sensorManager = null;

	private boolean isRegistered = false;
	private PApplet parent;

	private Method onSensorEventMethod;
	float[] accelerometerData, magnetometerData;

	// Simple methods are of the form v1,v2,v3,v4 (typically x,y,z values)
	// and the non-simple methods take values of v1,v2,v3, time, accuracy.
	// see:
	// http://developer.android.com/reference/android/hardware/SensorEvent.html#values
	private Method onAccelerometerEventMethod,
			onAccelerometerEventMethodSimple, onOrientationSensorEventMethod,
			onOrientationSensorEventMethodSimple, onGyroscopeSensorEventMethod,
			onGyroscopeSensorEventMethodSimple,

			onMagneticFieldSensorEventMethod,
			onMagneticFieldSensorEventMethodSimple, onLightSensorEventMethod,
			onLightSensorEventMethodSimple, onProximitySensorEventMethod,
			onProximitySensorEventMethodSimple, onPressureSensorEventMethod,
			onPressureSensorEventMethodSimple, onTemperatureSensorEventMethod,
			onTemperatureSensorEventMethodSimple,
			onRotationVectorSensorEventMethod,
			onRotationVectorSensorEventMethodSimple,
			onGravitySensorEventMethod, onGravitySensorEventMethodSimple,
			onLinearAccelerationSensorEventMethod,
			onLinearAccelerationSensorEventMethodSimple;

	private boolean accelerometerSensorEnabled, magneticFieldSensorEnabled,
			orientationSensorEnabled, proximitySensorEnabled, useSimulator,
			lightSensorEnabled, pressureSensorEnabled,
			temperatureSensorEnabled, gyroscopeSensorEnabled,
			rotationVectorSensorEnabled, linearAccelerationSensorEnabled,
			gravitySensorEnabled;
	private long delayInterval, timeOfLastUpdate;
	final static String SERVICE_DESCRIPTION = "Android Sensors.";

	public KetaiSensor(PApplet pParent) {
		parent = pParent;
		findParentIntentions();
		useSimulator = false;
		sensorManager = (SensorManager) parent.getApplicationContext()
				.getSystemService(Context.SENSOR_SERVICE);

		delayInterval = timeOfLastUpdate = 0;
	}

	public void useSimulator(boolean flag) {
		useSimulator = flag;
	}

	public boolean usingSimulator() {
		return useSimulator;
	}

	public void setDelayInterval(long pDelayInterval) {
		delayInterval = pDelayInterval;
	}

	public void enableAccelerometer() {
		accelerometerSensorEnabled = true;
	}

	public void enableRotationVector() {
		rotationVectorSensorEnabled = true;
	}

	public void enableLinearAcceleration() {
		linearAccelerationSensorEnabled = true;
	}

	public void disableAccelerometer() {
		accelerometerSensorEnabled = true;
	}

	public void enableMagenticField() {
		magneticFieldSensorEnabled = true;
	}

	public void disableMagneticField() {
		magneticFieldSensorEnabled = true;
	}

	public void enableOrientation() {
		orientationSensorEnabled = true;
	}

	public void disableOrientation() {
		orientationSensorEnabled = false;
	}

	public void enableProximity() {
		proximitySensorEnabled = true;
	}

	public void disableProximity() {
		proximitySensorEnabled = false;
	}

	public void disablelinearAcceleration() {
		linearAccelerationSensorEnabled = false;
	}

	public void disableRotationVector() {
		rotationVectorSensorEnabled = false;
	}

	public void enableLight() {
		lightSensorEnabled = true;
	}

	public void disableLight() {
		lightSensorEnabled = true;
	}

	public void enablePressure() {
		pressureSensorEnabled = true;
	}

	public void disablePressure() {
		pressureSensorEnabled = true;
	}

	public void enableTemperature() {
		temperatureSensorEnabled = true;
	}

	public void disableTemperature() {
		temperatureSensorEnabled = false;
	}

	public void enableGyroscope() {
		gyroscopeSensorEnabled = true;
	}

	public void disableGyroscope() {
		gyroscopeSensorEnabled = false;
	}

	public void enableAllSensors() {
		accelerometerSensorEnabled = magneticFieldSensorEnabled = orientationSensorEnabled = proximitySensorEnabled = lightSensorEnabled = pressureSensorEnabled = temperatureSensorEnabled = gyroscopeSensorEnabled = linearAccelerationSensorEnabled = rotationVectorSensorEnabled = true;
	}

	public boolean isAccelerometerAvailable() {
		return isSensorSupported(Sensor.TYPE_ACCELEROMETER);
	}

	public boolean isLinearAccelerationAvailable() {
		return isSensorSupported(Sensor.TYPE_LINEAR_ACCELERATION);
	}

	public boolean isRotationVectorAvailable() {
		return isSensorSupported(Sensor.TYPE_ROTATION_VECTOR);
	}

	public boolean isMagenticFieldAvailable() {
		return isSensorSupported(Sensor.TYPE_MAGNETIC_FIELD);
	}

	public boolean isOrientationAvailable() {
		return isSensorSupported(Sensor.TYPE_ORIENTATION);
	}

	public boolean isProximityAvailable() {
		return isSensorSupported(Sensor.TYPE_PROXIMITY);
	}

	public boolean isLightAvailable() {
		return isSensorSupported(Sensor.TYPE_LIGHT);
	}

	public boolean isPressureAvailable() {
		return isSensorSupported(Sensor.TYPE_PRESSURE);
	}

	public boolean isTemperatureAvailable() {
		return isSensorSupported(Sensor.TYPE_TEMPERATURE);
	}

	public boolean isGyroscopeAvailable() {
		return isSensorSupported(Sensor.TYPE_GYROSCOPE);
	}

	public Collection<? extends String> list() {
		Vector<String> list = new Vector<String>();

		List<Sensor> foo = sensorManager.getSensorList(Sensor.TYPE_ALL);
		for (Sensor s : foo) {
			list.add(s.getName());
			PApplet.println("\tKetaiSensor sensor: " + s.getName() + ":"
					+ s.getType());
		}
		return list;
		// String returnList[] = new String[list.size()];
		// list.copyInto(returnList);
		// return returnList;
	}

	public boolean isStarted() {
		return isRegistered;
	}

	public void start() {
		PApplet.println("KetaiSensor: start()...");
		findParentIntentions();

		if (accelerometerSensorEnabled) {
			Sensor s = sensorManager
					.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			if (s != null) sensorManager.registerListener(this, s,
					SensorManager.SENSOR_DELAY_UI);
		}
		if (magneticFieldSensorEnabled) {
			Sensor s = sensorManager
					.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
			if (s != null) sensorManager.registerListener(this, s,
					SensorManager.SENSOR_DELAY_UI);
		}
		if (pressureSensorEnabled) {
			Sensor s = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
			if (s != null) sensorManager.registerListener(this, s,
					SensorManager.SENSOR_DELAY_UI);
		}
		if (orientationSensorEnabled) {
			Sensor s = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
			if (s != null) sensorManager.registerListener(this, s,
					SensorManager.SENSOR_DELAY_UI);
		}
		if (proximitySensorEnabled) {
			Sensor s = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
			if (s != null) sensorManager.registerListener(this, s,
					SensorManager.SENSOR_DELAY_UI);
		}
		if (temperatureSensorEnabled) {
			Sensor s = sensorManager.getDefaultSensor(Sensor.TYPE_TEMPERATURE);
			if (s != null) sensorManager.registerListener(this, s,
					SensorManager.SENSOR_DELAY_UI);
		}
		if (gyroscopeSensorEnabled) {
			Sensor s = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
			if (s != null) sensorManager.registerListener(this, s,
					SensorManager.SENSOR_DELAY_UI);
		}
		if (rotationVectorSensorEnabled) {
			Sensor s = sensorManager
					.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
			if (s != null) sensorManager.registerListener(this, s,
					SensorManager.SENSOR_DELAY_UI);
		}
		if (linearAccelerationSensorEnabled) {
			Sensor s = sensorManager
					.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
			if (s != null) sensorManager.registerListener(this, s,
					SensorManager.SENSOR_DELAY_UI);
		}
		if (lightSensorEnabled) {
			Sensor s = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
			if (s != null) sensorManager.registerListener(this, s,
					SensorManager.SENSOR_DELAY_UI);
		}
		if (gravitySensorEnabled) {
			Sensor s = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
			if (s != null) sensorManager.registerListener(this, s,
					SensorManager.SENSOR_DELAY_UI);
		}
		isRegistered = true;
	}

	public void stop() {
		PApplet.println("KetaiSensor: Stop()....");
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
				&& accelerometerSensorEnabled) {
			if (onAccelerometerEventMethod != null) {
				try {
					accelerometerData = arg0.values.clone();
					onAccelerometerEventMethod.invoke(parent, new Object[] {
							arg0.values[0], arg0.values[1], arg0.values[2],
							arg0.timestamp, arg0.accuracy });
					timeOfLastUpdate = now;
					return;
				} catch (Exception e) {
					PApplet.println("Disabling onAccelerometerEvent() because of an error:"
							+ e.getMessage());
					e.printStackTrace();
					onAccelerometerEventMethod = null;
				}
			}

			if (onAccelerometerEventMethodSimple != null) {
				try {
					accelerometerData = arg0.values.clone();
					onAccelerometerEventMethodSimple.invoke(parent,
							new Object[] { arg0.values[0], arg0.values[1],
									arg0.values[2] });
					timeOfLastUpdate = now;
					return;
				} catch (Exception e) {
					PApplet.println("Disabling onAccelerometerEvent() [simple] because of an error:"
							+ e.getMessage());
					e.printStackTrace();
					onAccelerometerEventMethodSimple = null;
				}
			}
		}

		if (arg0.sensor.getType() == Sensor.TYPE_GRAVITY
				&& gravitySensorEnabled) {
			if (onGravitySensorEventMethod != null) {
				try {
					onGravitySensorEventMethod.invoke(parent, new Object[] {
							arg0.values[0], arg0.values[1], arg0.values[2],
							arg0.timestamp, arg0.accuracy });
					timeOfLastUpdate = now;
					return;
				} catch (Exception e) {
					PApplet.println("Disabling onGravityEvent() because of an error:"
							+ e.getMessage());
					e.printStackTrace();
					onGravitySensorEventMethod = null;
				}
			}

			if (onGravitySensorEventMethodSimple != null) {
				try {
					onGravitySensorEventMethodSimple.invoke(parent,
							new Object[] { arg0.values[0], arg0.values[1],
									arg0.values[2] });
					timeOfLastUpdate = now;
					return;
				} catch (Exception e) {
					PApplet.println("Disabling onGravityEvent()[simple] because of an error:"
							+ e.getMessage());
					e.printStackTrace();
					onGravitySensorEventMethodSimple = null;
				}
			}
		}

		if (arg0.sensor.getType() == Sensor.TYPE_ORIENTATION
				&& orientationSensorEnabled) {
			if (onOrientationSensorEventMethod != null) {
				try {
					onOrientationSensorEventMethod.invoke(parent, new Object[] {
							arg0.values[0], arg0.values[1], arg0.values[2],
							arg0.timestamp, arg0.accuracy });
					timeOfLastUpdate = now;
					return;
				} catch (Exception e) {
					PApplet.println("Disabling onOrientationEvent() because of an error:"
							+ e.getMessage());
					e.printStackTrace();
					onOrientationSensorEventMethod = null;
				}
			}
			if (onOrientationSensorEventMethodSimple != null) {
				try {
					onOrientationSensorEventMethodSimple.invoke(parent,
							new Object[] { arg0.values[0], arg0.values[1],
									arg0.values[2] });
					timeOfLastUpdate = now;
					return;
				} catch (Exception e) {
					PApplet.println("Disabling onOrientationEvent()[simple] because of an error:"
							+ e.getMessage());
					e.printStackTrace();
					onOrientationSensorEventMethodSimple = null;
				}
			}
		}

		if (arg0.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD
				&& magneticFieldSensorEnabled) {
			if (onMagneticFieldSensorEventMethod != null) {
				try {
					magnetometerData = arg0.values.clone();
					onMagneticFieldSensorEventMethod.invoke(parent,
							new Object[] { arg0.values[0], arg0.values[1],
									arg0.values[2], arg0.timestamp,
									arg0.accuracy });
					timeOfLastUpdate = now;
					return;
				} catch (Exception e) {
					PApplet.println("Disabling onMagneticFieldEvent() because of an error:"
							+ e.getMessage());
					e.printStackTrace();
					onMagneticFieldSensorEventMethod = null;
				}
			}
			if (onMagneticFieldSensorEventMethodSimple != null) {
				try {
					magnetometerData = arg0.values.clone();
					onMagneticFieldSensorEventMethodSimple.invoke(parent,
							new Object[] { arg0.values[0], arg0.values[1],
									arg0.values[2] });
					timeOfLastUpdate = now;
					return;
				} catch (Exception e) {
					PApplet.println("Disabling onMagneticFieldEvent()[simple] because of an error:"
							+ e.getMessage());
					e.printStackTrace();
					onMagneticFieldSensorEventMethodSimple = null;
				}
			}
		}

		if (arg0.sensor.getType() == Sensor.TYPE_GYROSCOPE
				&& gyroscopeSensorEnabled) {
			if (onGyroscopeSensorEventMethod != null) {
				try {
					onGyroscopeSensorEventMethod.invoke(parent, new Object[] {
							arg0.values[0], arg0.values[1], arg0.values[2],
							arg0.timestamp, arg0.accuracy });
					timeOfLastUpdate = now;
					return;
				} catch (Exception e) {
					PApplet.println("Disabling onGyroscopeEvent() because of an error:"
							+ e.getMessage());
					e.printStackTrace();
					onGyroscopeSensorEventMethod = null;
				}
			}
			if (onGyroscopeSensorEventMethodSimple != null) {
				try {
					onGyroscopeSensorEventMethodSimple.invoke(parent,
							new Object[] { arg0.values[0], arg0.values[1],
									arg0.values[2] });
					timeOfLastUpdate = now;
					return;
				} catch (Exception e) {
					PApplet.println("Disabling onGyroscopeEvent()[simple] because of an error:"
							+ e.getMessage());
					e.printStackTrace();
					onGyroscopeSensorEventMethodSimple = null;
				}
			}
		}

		if (arg0.sensor.getType() == Sensor.TYPE_LIGHT && lightSensorEnabled) {
			if (onLightSensorEventMethod != null) {
				try {

					onLightSensorEventMethod.invoke(parent, new Object[] {
							arg0.values[0], arg0.timestamp, arg0.accuracy });
					timeOfLastUpdate = now;
					return;
				} catch (Exception e) {
					PApplet.println("Disabling onLightEvent() because of an error:"
							+ e.getMessage());
					e.printStackTrace();
					onLightSensorEventMethod = null;
				}
			}
			if (onLightSensorEventMethodSimple != null) {
				try {

					onLightSensorEventMethodSimple.invoke(parent,
							new Object[] { arg0.values[0] });
					timeOfLastUpdate = now;
					return;
				} catch (Exception e) {
					PApplet.println("Disabling onLightEvent()[simple] because of an error:"
							+ e.getMessage());
					e.printStackTrace();
					onLightSensorEventMethodSimple = null;
				}
			}
		}

		if (arg0.sensor.getType() == Sensor.TYPE_PROXIMITY
				&& proximitySensorEnabled) {
			if (onProximitySensorEventMethod != null) {
				try {
					onProximitySensorEventMethod.invoke(parent, new Object[] {
							arg0.values[0], arg0.timestamp, arg0.accuracy });
					timeOfLastUpdate = now;
					return;
				} catch (Exception e) {
					PApplet.println("Disabling onProximityEvent() because of an error:"
							+ e.getMessage());
					e.printStackTrace();
					onProximitySensorEventMethod = null;
				}
			}
			if (onProximitySensorEventMethodSimple != null) {
				try {
					onProximitySensorEventMethodSimple.invoke(parent,
							new Object[] { arg0.values[0] });
					timeOfLastUpdate = now;
					return;
				} catch (Exception e) {
					PApplet.println("Disabling onProximityEvent()[simple] because of an error:"
							+ e.getMessage());
					e.printStackTrace();
					onProximitySensorEventMethodSimple = null;
				}
			}
		}
		if (arg0.sensor.getType() == Sensor.TYPE_PRESSURE
				&& pressureSensorEnabled) {

			if (onPressureSensorEventMethod != null) {
				try {
					onPressureSensorEventMethod.invoke(parent, new Object[] {
							arg0.values[0], arg0.timestamp, arg0.accuracy });
					timeOfLastUpdate = now;
					return;
				} catch (Exception e) {
					PApplet.println("Disabling onPressureEvent() because of an error:"
							+ e.getMessage());
					e.printStackTrace();
					onPressureSensorEventMethod = null;
				}
			}
			if (onPressureSensorEventMethodSimple != null) {
				try {
					onPressureSensorEventMethodSimple.invoke(parent,
							new Object[] { arg0.values[0] });
					timeOfLastUpdate = now;
					return;
				} catch (Exception e) {
					PApplet.println("Disabling onPressureEvent()[simple] because of an error:"
							+ e.getMessage());
					e.printStackTrace();
					onPressureSensorEventMethodSimple = null;
				}
			}
		}

		if (arg0.sensor.getType() == Sensor.TYPE_TEMPERATURE
				&& temperatureSensorEnabled) {
			if (onTemperatureSensorEventMethod != null) {
				try {
					onTemperatureSensorEventMethod.invoke(parent, new Object[] {
							arg0.values[0], arg0.timestamp, arg0.accuracy });
					timeOfLastUpdate = now;
					return;
				} catch (Exception e) {
					PApplet.println("Disabling onTemperatureEvent() because of an error:"
							+ e.getMessage());
					e.printStackTrace();
					onTemperatureSensorEventMethod = null;
				}
			}
			if (onTemperatureSensorEventMethodSimple != null) {
				try {
					onTemperatureSensorEventMethodSimple.invoke(parent,
							new Object[] { arg0.values[0] });
					timeOfLastUpdate = now;
					return;
				} catch (Exception e) {
					PApplet.println("Disabling onTemperatureEvent()[simple] because of an error:"
							+ e.getMessage());
					e.printStackTrace();
					onTemperatureSensorEventMethodSimple = null;
				}
			}
		}
		if (arg0.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION
				&& linearAccelerationSensorEnabled) {

			if (onLinearAccelerationSensorEventMethod != null) {
				try {
					onLinearAccelerationSensorEventMethod.invoke(parent,
							new Object[] { arg0.values[0], arg0.values[1],
									arg0.values[2], arg0.timestamp,
									arg0.accuracy });
					timeOfLastUpdate = now;
					return;
				} catch (Exception e) {
					PApplet.println("Disabling onLinearAccelerationEvent() because of an error:"
							+ e.getMessage());
					e.printStackTrace();
					onLinearAccelerationSensorEventMethod = null;
				}
			}

			if (onLinearAccelerationSensorEventMethodSimple != null) {
				try {
					onLinearAccelerationSensorEventMethodSimple.invoke(parent,
							new Object[] { arg0.values[0], arg0.values[1],
									arg0.values[2] });
					timeOfLastUpdate = now;
					return;
				} catch (Exception e) {
					PApplet.println("Disabling onLinearAccelerationEvent()[simple] because of an error:"
							+ e.getMessage());
					e.printStackTrace();
					onLinearAccelerationSensorEventMethodSimple = null;
				}
			}
		}

		if (arg0.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR
				&& rotationVectorSensorEnabled) {
			if (onRotationVectorSensorEventMethod != null) {
				try {
					onRotationVectorSensorEventMethod.invoke(parent,
							new Object[] { arg0.values[0], arg0.values[1],
									arg0.values[2], arg0.timestamp,
									arg0.accuracy });
					timeOfLastUpdate = now;
					return;
				} catch (Exception e) {
					PApplet.println("Disabling onRotationVectorEvent() because of an error:"
							+ e.getMessage());
					e.printStackTrace();
					onRotationVectorSensorEventMethod = null;
				}
			}
			if (onRotationVectorSensorEventMethodSimple != null) {
				try {
					onRotationVectorSensorEventMethodSimple.invoke(parent,
							new Object[] { arg0.values[0], arg0.values[1],
									arg0.values[2] });
					timeOfLastUpdate = now;
					return;
				} catch (Exception e) {
					PApplet.println("Disabling onRotationVectorEvent()[simple] because of an error:"
							+ e.getMessage());
					e.printStackTrace();
					onRotationVectorSensorEventMethodSimple = null;
				}
			}
		}
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
			onAccelerometerEventMethod = parent.getClass().getMethod(
					"onAccelerometerEvent",
					new Class[] { float.class, float.class, float.class,
							long.class, int.class, });
			accelerometerSensorEnabled = true;
			PApplet.println("Found onAccelerometerEvent	Method...");

		} catch (NoSuchMethodException e) {
		}

		try {
			onAccelerometerEventMethodSimple = parent.getClass().getMethod(
					"onAccelerometerEvent",
					new Class[] { float.class, float.class, float.class });
			accelerometerSensorEnabled = true;
			PApplet.println("Found onAccelerometerEventMethod(simple)...");

		} catch (NoSuchMethodException e) {
		}

		try {
			onOrientationSensorEventMethod = parent.getClass().getMethod(
					"onOrientationEvent",
					new Class[] { float.class, float.class, float.class,
							long.class, int.class, });
			orientationSensorEnabled = true;
			PApplet.println("Found onOrientationEventMethod...");
		} catch (NoSuchMethodException e) {
		}

		try {
			onOrientationSensorEventMethodSimple = parent.getClass().getMethod(
					"onOrientationEvent",
					new Class[] { float.class, float.class, float.class });
			orientationSensorEnabled = true;
			PApplet.println("Found onOrientationEventMethod(simple)...");
		} catch (NoSuchMethodException e) {
		}

		try {
			onMagneticFieldSensorEventMethod = parent.getClass().getMethod(
					"onMagneticFieldEvent",
					new Class[] { float.class, float.class, float.class,
							long.class, int.class });
			magneticFieldSensorEnabled = true;
			PApplet.println("Found onMagneticFieldEventMethod...");
		} catch (NoSuchMethodException e) {
		}

		try {
			onMagneticFieldSensorEventMethodSimple = parent
					.getClass()
					.getMethod(
							"onMagneticFieldEvent",
							new Class[] { float.class, float.class, float.class });
			magneticFieldSensorEnabled = true;
			PApplet.println("Found onMagneticFieldEventMethod(simple)...");
		} catch (NoSuchMethodException e) {
		}

		try {
			onGyroscopeSensorEventMethod = parent.getClass().getMethod(
					"onGyroscopeEvent",
					new Class[] { float.class, float.class, float.class,
							long.class, int.class, });
			gyroscopeSensorEnabled = true;
			PApplet.println("Found onGyroscopeEventMethod...");
		} catch (NoSuchMethodException e) {
		}

		try {
			onGyroscopeSensorEventMethodSimple = parent.getClass().getMethod(
					"onGyroscopeEvent",
					new Class[] { float.class, float.class, float.class });
			gyroscopeSensorEnabled = true;
			PApplet.println("Found onGyroscopeEventMethod(simple)...");
		} catch (NoSuchMethodException e) {
		}

		try {
			onGravitySensorEventMethod = parent.getClass().getMethod(
					"onGravityEvent",
					new Class[] { float.class, float.class, float.class,
							long.class, int.class, });
			gravitySensorEnabled = true;
			PApplet.println("Found onGravityEvenMethod...");

		} catch (NoSuchMethodException e) {
		}

		try {
			onGravitySensorEventMethodSimple = parent.getClass().getMethod(
					"onGravityEvent",
					new Class[] { float.class, float.class, float.class });
			gravitySensorEnabled = true;
			PApplet.println("Found onGravityEvenMethod(simple)...");

		} catch (NoSuchMethodException e) {
		}

		try {
			onProximitySensorEventMethod = parent.getClass().getMethod(
					"onProximityEvent",
					new Class[] { float.class, long.class, int.class });
			proximitySensorEnabled = true;
			PApplet.println("Found onLightEventMethod...");
		} catch (NoSuchMethodException e) {
		}

		try {
			onProximitySensorEventMethodSimple = parent.getClass().getMethod(
					"onProximityEvent", new Class[] { float.class });
			proximitySensorEnabled = true;
			PApplet.println("Found onProximityEventMethod(simple)...");
		} catch (NoSuchMethodException e) {
		}

		try {
			onLightSensorEventMethod = parent.getClass().getMethod(
					"onLightEvent",
					new Class[] { float.class, long.class, int.class });
			lightSensorEnabled = true;
			PApplet.println("Found onLightEventMethod...");
		} catch (NoSuchMethodException e) {
		}

		try {
			onLightSensorEventMethodSimple = parent.getClass().getMethod(
					"onLightEvent", new Class[] { float.class });
			lightSensorEnabled = true;
			PApplet.println("Found onLightEventMethod(simple)...");
		} catch (NoSuchMethodException e) {
		}

		try {
			onPressureSensorEventMethod = parent.getClass().getMethod(
					"onPressureEvent",
					new Class[] { float.class, long.class, int.class });
			pressureSensorEnabled = true;
			PApplet.println("Found onPressureEventMethod...");
		} catch (NoSuchMethodException e) {
		}

		try {
			onPressureSensorEventMethodSimple = parent.getClass().getMethod(
					"onPressureEvent", new Class[] { float.class });
			pressureSensorEnabled = true;
			PApplet.println("Found onPressureEventMethod(simple)...");
		} catch (NoSuchMethodException e) {
		}

		try {
			onTemperatureSensorEventMethod = parent.getClass().getMethod(
					"onTemperatureEvent",
					new Class[] { float.class, long.class, int.class });
			temperatureSensorEnabled = true;
			PApplet.println("Found onTemperatureEventMethod...");
		} catch (NoSuchMethodException e) {
		}

		try {
			onTemperatureSensorEventMethodSimple = parent.getClass().getMethod(
					"onTemperatureEvent", new Class[] { float.class });
			temperatureSensorEnabled = true;
			PApplet.println("Found onTemperatureEventMethod(simple)...");
		} catch (NoSuchMethodException e) {
		}

		try {
			onLinearAccelerationSensorEventMethod = parent.getClass()
					.getMethod(
							"onLinearAccelerationEvent",
							new Class[] { float.class, float.class,
									float.class, long.class, int.class });
			linearAccelerationSensorEnabled = true;
			PApplet.println("Found onLinearAccelerationEventMethod...");
		} catch (NoSuchMethodException e) {
		}

		try {
			onLinearAccelerationSensorEventMethodSimple = parent
					.getClass()
					.getMethod(
							"onLinearAccelerationEvent",
							new Class[] { float.class, float.class, float.class });
			linearAccelerationSensorEnabled = true;
			PApplet.println("Found onLinearAccelerationEventMethod(simple)...");
		} catch (NoSuchMethodException e) {
		}

		try {
			onRotationVectorSensorEventMethod = parent.getClass().getMethod(
					"onRotationVectorEvent",
					new Class[] { float.class, float.class, float.class,
							long.class, int.class });
			rotationVectorSensorEnabled = true;
			PApplet.println("Found onRotationVectorEvenMethod...");

		} catch (NoSuchMethodException e) {
		}

		try {
			onRotationVectorSensorEventMethodSimple = parent
					.getClass()
					.getMethod(
							"onRotationVectorEvent",
							new Class[] { float.class, float.class, float.class });
			rotationVectorSensorEnabled = true;
			PApplet.println("Found onRotationVectorEventMethod(simple)...");

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

	public void getRotationMatrixFromVector(float[] R, float[] rotationVector) {
		SensorManager.getRotationMatrixFromVector(R, rotationVector);
	}

	public void getQuaternionFromVector(float[] Q, float[] rv) {
		SensorManager.getQuaternionFromVector(Q, rv);
	}

	public float[] getOrientation() {
		float[] values = new float[3];
		float[] R = new float[16];
		float[] I = new float[9];

		if (!isStarted() && !this.accelerometerSensorEnabled
				&& !this.magneticFieldSensorEnabled) {
			PApplet.println("Cannot compute orientation until sensor service is started and accelerometer and magnetometer must also be enabled.");
			values = new float[3];
			return values;
		}

		if (SensorManager.getRotationMatrix(R, I, accelerometerData,
				magnetometerData))
			values = SensorManager.getOrientation(null, values);

		return values;
	}
}

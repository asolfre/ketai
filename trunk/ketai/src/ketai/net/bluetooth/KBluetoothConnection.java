package ketai.net.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import android.bluetooth.BluetoothSocket;

import processing.core.PApplet;

public class KBluetoothConnection extends Thread {

	private final BluetoothSocket mmSocket;
	private final InputStream mmInStream;
	private final OutputStream mmOutStream;
	private boolean isConnected = false;
	private String address = "";
	private KetaiBluetooth btm;

	public KBluetoothConnection(KetaiBluetooth _btm, BluetoothSocket socket) {
		PApplet.println("create Connection thread to "
				+ socket.getRemoteDevice().getName());
		btm = _btm;
		mmSocket = socket;
		InputStream tmpIn = null;
		OutputStream tmpOut = null;
		address = socket.getRemoteDevice().getAddress();

		try {
			// socket.connect();
			tmpIn = socket.getInputStream();
			tmpOut = socket.getOutputStream();
		} catch (IOException e) {
			PApplet.println("temp sockets not created: " + e.getMessage());
		}

		mmInStream = tmpIn;
		mmOutStream = tmpOut;
		isConnected = true;
	}

	public String getAddress() {
		return address;
	}

	public String getDeviceName() {
		if (mmSocket == null)
			return "";
		return mmSocket.getRemoteDevice().getName();
	}

	public void run() {
		PApplet.println("BEGIN mConnectedThread to " + address);
		byte[] buffer = new byte[1024];
		int bytes;

		// Keep listening to the InputStream while connected
		while (true) {
			try {
				// Read from the InputStream
				bytes = mmInStream.read(buffer);
				byte[] data = Arrays.copyOfRange(buffer, 0, bytes);

				// PApplet.println(bytes + " bytes read from "
				// + mmSocket.getRemoteDevice().getName());

				if (btm.onBluetoothDataEventMethod != null) {
					try {
						btm.onBluetoothDataEventMethod.invoke(btm.parent,
								new Object[] { this.address, data });
					} catch (IllegalAccessException e) {
						PApplet.println("Error in reading connection data.:"
								+ e.getMessage());
					} catch (InvocationTargetException e) {
						PApplet.println("Error in reading connection data.:"
								+ e.getMessage());
					}
				}
				// // Send the obtained bytes to the UI Activity
				// mHandler.obtainMessage(BluetoothChat.MESSAGE_READ, bytes, -1,
				// buffer).sendToTarget();
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {

				}
			} catch (IOException e) {
				btm.removeConnection(this);
				PApplet.println(getAddress() + " disconnected" + e.getMessage());
				// notify manager that we've gone belly up
				// connectionLost();
				isConnected = false;
				break;
			}
		}
	}

	public boolean isConnected() {
		return isConnected;
	}

	public void write(byte[] buffer) {
		try {
			// PApplet.println("KBTConnection thread writing " + buffer.length
			// + " bytes to " + address);

			mmOutStream.write(buffer);
		} catch (IOException e) {
			PApplet.println(getAddress() + ": Exception during write"
					+ e.getMessage());
			btm.removeConnection(this);
		}
	}

	public void cancel() {
		try {
			mmSocket.close();
		} catch (IOException e) {
			PApplet.println("close() of connect socket failed" + e.getMessage());
		}
	}

}

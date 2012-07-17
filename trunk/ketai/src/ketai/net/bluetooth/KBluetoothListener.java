package ketai.net.bluetooth;

import java.io.IOException;

import processing.core.PApplet;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

public class KBluetoothListener extends Thread {
	private final BluetoothServerSocket mmServerSocket;
	private String mSocketType;
	private BluetoothAdapter mAdapter;
	private KetaiBluetooth btManager;
	private boolean go = true;

	public KBluetoothListener(KetaiBluetooth btm, boolean secure) {
		BluetoothServerSocket tmp = null;
		mSocketType = secure ? "Secure" : "Insecure";
		btManager = btm;
		mAdapter = btManager.getBluetoothAdapater();

		// Create a new listening server socket
		try {
			if (secure) {
				tmp = mAdapter.listenUsingRfcommWithServiceRecord(
						btManager.NAME_SECURE, btManager.MY_UUID_SECURE);
			} else {
				tmp = mAdapter.listenUsingInsecureRfcommWithServiceRecord(
						btManager.NAME_INSECURE, btManager.MY_UUID_INSECURE);
			}
		} catch (IOException e) {
			PApplet.println("Socket Type: " + mSocketType + "listen() failed"
					+ e);
		}
		mmServerSocket = tmp;
	}

	public void run() {
		PApplet.println("Socket Type: " + mSocketType + "BEGIN mAcceptThread"
				+ this);
		PApplet.println("AcceptThread" + mSocketType);

		BluetoothSocket socket = null;
		if (mmServerSocket == null) {
			PApplet.println("Failed to get socket for server! bye.");
			return;
		}
		while (go) {
			try {
				socket = mmServerSocket.accept();
				if (socket != null) {
					synchronized (this) {
						PApplet.println("Incoming connection from: "
								+ socket.getRemoteDevice().getName());
						btManager.connectDevice(socket);
						mmServerSocket.close();
					}
				}
			} catch (IOException e) {
				PApplet.println("Socket Type: " + mSocketType
						+ "accept() failed" + e.getMessage());
				break;
			}
		}
		PApplet.println("END mAcceptThread, socket Type: " + mSocketType);

	}

	public void cancel() {
		PApplet.println("Socket Type" + mSocketType + "cancel " + this);
		go = false;
		try {
			if (mmServerSocket != null)
				mmServerSocket.close();
		} catch (IOException e) {
			PApplet.println("Socket Type" + mSocketType
					+ "close() of server failed" + e.getMessage());
		}
	}

}

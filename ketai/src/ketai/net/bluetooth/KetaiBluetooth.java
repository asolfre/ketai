package ketai.net.bluetooth;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import processing.core.PApplet;

public class KetaiBluetooth {
	protected PApplet parent;
	protected BluetoothAdapter bluetoothAdapter;
	private HashMap<String, String> pairedDevices;
	private HashMap<String, String> discoveredDevices;
	private HashMap<String, KBluetoothConnection> currentConnections;
	private KBluetoothListener btListener;
	private ConnectThread mConnectThread;
	private boolean isStarted = false;
	private boolean SLIPMode = false;
	protected Method onBluetoothDataEventMethod;

	protected UUID MY_UUID_SECURE = UUID
			.fromString("fa87c0d0-afac-11de-8a39-0800200c5a66");
	protected UUID MY_UUID_INSECURE = UUID
			.fromString("8ce255c0-200a-11e0-ac64-0800200c5a66");

	protected String NAME_SECURE = "BluetoothSecure";
	protected String NAME_INSECURE = "BluetoothInsecure";

	final static int BLUETOOTH_ENABLE_REQUEST = 1;

	public KetaiBluetooth(PApplet _parent) {
		parent = _parent;
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (bluetoothAdapter == null) {
			PApplet.println("No Bluetooth Support.");
			return;
		}

		if (!bluetoothAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			parent.startActivityForResult(enableBtIntent,
					BLUETOOTH_ENABLE_REQUEST);
		}
		pairedDevices = new HashMap<String, String>();
		discoveredDevices = new HashMap<String, String>();
		currentConnections = new HashMap<String, KBluetoothConnection>();
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		parent.registerReceiver(mReceiver, filter);
		findParentIntention();
	}

	public void setSLIPMode(boolean _flag) {
		SLIPMode = _flag;
	}

	public boolean isStarted() {
		return isStarted;
	}

	public BluetoothAdapter getBluetoothAdapater() {
		return bluetoothAdapter;
	}

	public String toString() {
		String info = "KBluetoothManager dump:\n--------------------\nPairedDevices:\n";
		for (String key : pairedDevices.keySet()) {
			info += key + "->" + pairedDevices.get(key) + "\n";
		}

		info += "\n\nDiscovered Devices\n";
		for (String key : discoveredDevices.keySet()) {
			info += key + "->" + discoveredDevices.get(key) + "\n";
		}

		info += "\n\nCurrent Connections\n";
		for (String key : currentConnections.keySet()) {
			info += key + "->" + currentConnections.get(key) + "\n";
		}
		info += "\n-------------------------------\n";

		return info;
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case BLUETOOTH_ENABLE_REQUEST:
			if (resultCode == Activity.RESULT_OK) {
				PApplet.println("BT made available.");
			} else {
				// User did not enable Bluetooth or an error occurred
				PApplet.println("BT was not made available.");
			}
		}
	}

	public boolean isDiscoverable() {
		return (bluetoothAdapter.getScanMode() == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE);
	}

	public boolean start() {
		// start or re-start
		if (btListener != null) {
			stop();
			isStarted = false;
		}

		btListener = new KBluetoothListener(this, true);
		btListener.start();
		isStarted = true;
		return isStarted;
	}

	public ArrayList<String> getDiscoveredDeviceNames() {
		ArrayList<String> devices = new ArrayList<String>();

		for (String key : discoveredDevices.keySet()) {
			devices.add(key);// key + "->" + discoveredDevices.get(key) + "\n";
		}
		return devices;
	}

	public ArrayList<String> getPairedDeviceNames() {
		ArrayList<String> devices = new ArrayList<String>();

		pairedDevices.clear();
		Set<BluetoothDevice> bondedDevices = bluetoothAdapter
				.getBondedDevices();
		if (bondedDevices.size() > 0) {
			for (BluetoothDevice device : bondedDevices) {
				pairedDevices.put(device.getName(), device.getAddress());
				devices.add(device.getName());
			}
		}
		return devices;
	}

	public ArrayList<String> getConnectedDeviceNames() {
		ArrayList<String> devices = new ArrayList<String>();
		Set<String> connectedDevices = currentConnections.keySet();

		if (connectedDevices.size() > 0) {
			for (String device : connectedDevices) {
				KBluetoothConnection c = currentConnections.get(device);
				devices.add(c.getDeviceName() + "(" + device + ")");
			}
		}
		return devices;
	}

	public boolean connectToDeviceByName(String _name) {
		String address = "";
		if (pairedDevices.containsKey(_name)) {
			address = pairedDevices.get(_name);
		} else if (discoveredDevices.containsKey(_name)) {
			address = discoveredDevices.get(_name);
		}
		if (address.length() > 0 && currentConnections.containsKey(address)) {
			return true;
		}

		return connectDevice(address);
	}

	public boolean connectDevice(String _hwAddress) {
		BluetoothDevice device;

		if (!BluetoothAdapter.checkBluetoothAddress(_hwAddress)) {
			PApplet.println("Bad bluetooth hardware address! : " + _hwAddress);
			return false;
		}
		device = bluetoothAdapter.getRemoteDevice(_hwAddress);

		if (mConnectThread == null) {
			mConnectThread = new ConnectThread(device, true);
			mConnectThread.start();
		} else if (mConnectThread.mmDevice.getAddress() != _hwAddress) {
			mConnectThread.cancel();
			mConnectThread = new ConnectThread(device, true);
			mConnectThread.start();
		}
		return false;
	}

	public boolean connectDeviceUsingSLIP(String _hwAddress) {
		return false;
	}

	public boolean connectDevice(BluetoothSocket _socket) {

		KBluetoothConnection tmp = new KBluetoothConnection(this, _socket);

		if (tmp.isConnected())
			tmp.start();
		else {
			PApplet.println("Error trying to connect to "
					+ _socket.getRemoteDevice().getName() + " ("
					+ _socket.getRemoteDevice().getAddress() + ")");
			mConnectThread = null;
			return false;
		}
		if (tmp != null)
			if (!currentConnections.containsKey(_socket.getRemoteDevice()
					.getAddress()))
				currentConnections.put(_socket.getRemoteDevice().getAddress(),
						tmp);
		if (mConnectThread != null) {
			mConnectThread.cancel();
			mConnectThread = null;
		}
		return true;
	}

	public void discoverDevices() {
		discoveredDevices.clear();
		bluetoothAdapter.cancelDiscovery();
		if (bluetoothAdapter.startDiscovery())
			PApplet.println("Starting bt discovery.");
		else
			PApplet.println("BT discovery failed to start.");
	}

	public String lookupAddressByName(String _name) {
		if (pairedDevices.containsKey(_name)) {
			return pairedDevices.get(_name);
		} else if (discoveredDevices.containsKey(_name)) {
			return discoveredDevices.get(_name);
		}
		return "";
	}

	public void writeToDeviceName(String _name, byte[] data) {
		String address = lookupAddressByName(_name);
		if (address.length() > 0)
			write(address, data);
		else
			PApplet.println("Error writing to " + _name
					+ ".  HW Address was not found.");
	}

	public void write(String _deviceAddress, byte[] data) {
		bluetoothAdapter.cancelDiscovery();
		if (!currentConnections.containsKey(_deviceAddress)) {
			if (!connectDevice(_deviceAddress))
				return;
		}

		if (currentConnections.containsKey(_deviceAddress))
			currentConnections.get(_deviceAddress).write(data);

	}

	public void broadcast(byte[] data) {
		for (Map.Entry<String, KBluetoothConnection> device : currentConnections
				.entrySet()) {
			device.getValue().write(data);
		}
	}

	protected void removeConnection(KBluetoothConnection c) {
		PApplet.println("KBTM removing connection for " + c.getAddress());
		if (currentConnections.containsKey(c.getAddress())) {
			c.cancel();
			currentConnections.remove(c.getAddress());
		}
	}

	public void makeDiscoverable() {
		if (bluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
			Intent discoverableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			discoverableIntent.putExtra(
					BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
			parent.startActivity(discoverableIntent);
		}
	}

	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				if (device != null) {
					discoveredDevices
							.put(device.getName(), device.getAddress());
					PApplet.println("New Device Discovered: "
							+ device.getName());
				}
			}
		}
	};

	private void findParentIntention() {
		try {
			onBluetoothDataEventMethod = parent.getClass().getMethod(
					"onBluetoothDataEvent",
					new Class[] { String.class, byte[].class });
			PApplet.println("Found onBluetoothDataEvent method.");
		} catch (NoSuchMethodException e) {
			PApplet.println("Did not find onBluetoothDataEvent callback method.");
		}

	}

	public void stop() {
		if (btListener != null) {
			btListener.cancel();
		}

		if (mConnectThread != null) {
			mConnectThread.cancel();
		}

		for (String key : currentConnections.keySet()) {
			currentConnections.get(key).cancel();
		}
		currentConnections.clear();
		btListener = null;
		mConnectThread = null;
	}

	private class ConnectThread extends Thread {
		private final BluetoothSocket mmSocket;
		protected final BluetoothDevice mmDevice;
		private String mSocketType;

		public ConnectThread(BluetoothDevice device, boolean secure) {
			mmDevice = device;
			BluetoothSocket tmp = null;
			mSocketType = secure ? "Secure" : "Insecure";

			// Get a BluetoothSocket for a connection with the
			// given BluetoothDevice
			try {
				if (secure) {
					tmp = device
							.createRfcommSocketToServiceRecord(MY_UUID_SECURE);
				} else {
					tmp = device
							.createInsecureRfcommSocketToServiceRecord(MY_UUID_INSECURE);
				}
			} catch (IOException e) {
				PApplet.println("Socket Type: " + mSocketType
						+ "create() failed" + e);
			}
			mmSocket = tmp;
		}

		public void run() {
			while(mmSocket == null)
				try {
					Thread.sleep(250);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			
			PApplet.println("BEGIN mConnectThread SocketType:" + mSocketType
					+ ":" + mmSocket.getRemoteDevice().getName());

			// Always cancel discovery because it will slow down a connection
			bluetoothAdapter.cancelDiscovery();

			// Make a connection to the BluetoothSocket
			try {
				// This is a blocking call and will only return on a
				// successful connection or an exception
				if (mmSocket != null)
					mmSocket.connect();
				PApplet.println("KBTConnect thread connected!");
			} catch (IOException e) {
				// Close the socket
				try {
					mmSocket.close();
				} catch (IOException e2) {
					PApplet.println("unable to close() " + mSocketType
							+ " socket during connection failure" + e2);
				}
				mConnectThread = null;
				return;
			}

			// Start the connected thread
			connectDevice(mmSocket);// , mmDevice, mSocketType);
		}

		public void cancel() {
			// try {
			// mmSocket.close();
			// } catch (IOException e) {
			// PApplet.println("close() of connect " + mSocketType
			// + " socket failed" + e);
			// }
		}

	}

}
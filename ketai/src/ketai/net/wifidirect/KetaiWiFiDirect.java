package ketai.net.wifidirect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import processing.core.PApplet;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ChannelListener;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;

public class KetaiWiFiDirect extends BroadcastReceiver implements
		ChannelListener, ConnectionInfoListener, ActionListener,
		PeerListListener {

	PApplet parent;
	private WifiP2pManager manager;
	private boolean isWifiP2pEnabled = false;
	private boolean retryChannel = false;
	private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();

	private final IntentFilter intentFilter = new IntentFilter();
	private Channel channel;
	private String ip = "";

	public KetaiWiFiDirect(PApplet _parent) {
		parent = _parent;
		intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
		intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
		intentFilter
				.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
		intentFilter
				.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

		manager = (WifiP2pManager) parent
				.getSystemService(Context.WIFI_P2P_SERVICE);

		channel = manager.initialize(parent, parent.getMainLooper(), this);
		parent.registerReceiver(this, intentFilter);

	}

	public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
		this.isWifiP2pEnabled = isWifiP2pEnabled;
	}

	public void onResume() {
		parent.registerReceiver(this, intentFilter);
	}

	public void onPause() {
		parent.unregisterReceiver(this);
	}

	public void connect(WifiP2pConfig config) {
		manager.connect(channel, config, new ActionListener() {

			public void onSuccess() {
				// WiFiDirectBroadcastReceiver will notify us. Ignore for now.
			}

			public void onFailure(int reason) {
				PApplet.println("Connect failed. Retry.");
			}
		});
	}

	public void disconnect() {
		manager.removeGroup(channel, new ActionListener() {

			public void onFailure(int reasonCode) {
				PApplet.println("Disconnect failed. Reason :" + reasonCode);

			}

			public void onSuccess() {

			}

		});
	}

	public void onChannelDisconnected() {
		// we will try once more
		if (manager != null && !retryChannel) {
			PApplet.println("Channel lost. Trying again");
			retryChannel = true;
			manager.initialize(parent, parent.getMainLooper(), this);
		} else {
			PApplet.println("Severe! Channel is probably lost premanently. Try Disable/Re-Enable P2P.");
		}
	}

	public void cancelDisconnect() {

		/*
		 * A cancel abort request by user. Disconnect i.e. removeGroup if
		 * already connected. Else, request WifiP2pManager to abort the ongoing
		 * request
		 */
		if (manager != null) {

			manager.cancelConnect(channel, new ActionListener() {

				public void onSuccess() {
					PApplet.println("Aborting connection");
				}

				public void onFailure(int reasonCode) {
					PApplet.println("Connect abort request failed. Reason Code: "
							+ reasonCode);
				}
			});
		}
	}

	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {

			// UI update to indicate wifi p2p status.
			int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
			if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
				// Wifi Direct mode is enabled
				this.setIsWifiP2pEnabled(true);
			} else {
				this.setIsWifiP2pEnabled(false);
			}
			PApplet.println("P2P state changed - " + state);
		} else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {

			// request available peers from the wifi p2p manager. This is an
			// asynchronous call and the calling activity is notified with a
			// callback on PeerListListener.onPeersAvailable()
			if (manager != null) {
				manager.requestPeers(channel, (PeerListListener) this);
			}
			PApplet.println("P2P peers changed");
		} else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION
				.equals(action)) {

			if (manager == null) {
				return;
			}

			NetworkInfo networkInfo = (NetworkInfo) intent
					.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

			if (networkInfo.isConnected()) {

				// we are connected with the other device, request connection
				// info to find group owner IP

				manager.requestConnectionInfo(channel, this);
			} else {
				// It's a disconnect
			}
		} else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION
				.equals(action)) {

			PApplet.println("p2p device changed"
					+ (WifiP2pDevice) intent
							.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE));

		}
	}

	public void getConnectionInfo() {
		manager.requestConnectionInfo(channel, this);
	}

	public void onConnectionInfoAvailable(WifiP2pInfo arg0) {

		WifiP2pInfo info = arg0;
		if (arg0.groupFormed == false) {
			ip = "";
			return;
		}
		ip = info.groupOwnerAddress.getHostAddress();
		PApplet.println("Connection info available for :" + arg0.toString()
				+ "--" + info.groupOwnerAddress.getHostAddress());
	}

	public String getIPAddress() {
		return ip;
	}

	public void discover() {
		if (manager != null) {
			manager.discoverPeers(channel, this);
		}
	}

	public void onFailure(int arg0) {
		PApplet.println("WifiDirect failed " + arg0);

	}

	public void onSuccess() {
		PApplet.println("WifiDirect succeeded ");

	}

	public void onPeersAvailable(WifiP2pDeviceList arg0) {
		Collection<WifiP2pDevice> list = arg0.getDeviceList();
		if (list.size() > 0) {
			peers.clear();
			for (Iterator<WifiP2pDevice> i = list.iterator(); i.hasNext();)
				peers.add(i.next());
			PApplet.println("New KetaiWifiDirect peer list received:");
			for (WifiP2pDevice d : peers) {
				PApplet.println("\t\t" + d.deviceName + ":" + d.deviceAddress);
			}

		}
	}

	public void reset() {
		peers.clear();
		manager.cancelConnect(channel, this);
		manager.removeGroup(channel, this);

	}

	public ArrayList<String> getPeerNameList() {
		ArrayList<String> names = new ArrayList<String>();
		for (WifiP2pDevice d : peers)
			names.add(d.deviceName);

		return names;
	}

	public void connectToDevice(String deviceName) {

		// obtain a peer from the WifiP2pDeviceList
		WifiP2pDevice device = null;

		for (WifiP2pDevice d : peers) {
			if (d.deviceAddress == deviceName || d.deviceName == deviceName)
				device = d;
		}

		if (device == null)
			return;

		WifiP2pConfig config = new WifiP2pConfig();
		config.deviceAddress = device.deviceAddress;
		manager.connect(channel, config, new ActionListener() {
			public void onSuccess() {
				// success logic
			}

			public void onFailure(int reason) {
				PApplet.println("Failed to connect to device (" + reason + ")");
			}
		});
	}
}

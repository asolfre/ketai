package ketai.net.wifidirect;

import processing.core.PApplet;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.util.Log;

public class WifiBroadcastReceiver extends BroadcastReceiver {

	private WifiP2pManager manager;
	private Channel channel;
	private KetaiWiFiDirect parent;

	public WifiBroadcastReceiver(KetaiWiFiDirect activity,
			WifiP2pManager manager, Channel channel) {
		super();
		this.manager = manager;
		this.channel = channel;
		this.parent = activity;
	}

	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
			PApplet.println("WIFI P2P State changed");
			// UI update to indicate wifi p2p status.
			int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
			if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
				parent.setIsWifiP2pEnabled(true);
			} else {
				parent.setIsWifiP2pEnabled(false);
				parent.reset();
			}
			Log.d("WifiBroadcastReceiver", "P2P state changed - " + state);
		} else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
			PApplet.println("WIFI P2P PEERS changed");

			// request available peers from the wifi p2p manager. This is an
			// asynchronous call and the calling activity is notified with a
			// callback on PeerListListener.onPeersAvailable()
			if (manager != null) {
				manager.requestPeers(channel, (PeerListListener) parent);
			}
			Log.d("WifiBroadcastReceiver", "P2P peers changed");
		} else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION
				.equals(action)) {
			PApplet.println("WIFI P2P Connection changed");

			if (manager == null) {
				return;
			}

			NetworkInfo networkInfo = (NetworkInfo) intent
					.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

			if (networkInfo.isConnected()) {

				// we are connected with the other device, request connection
				// info to find group owner IP
				manager.requestConnectionInfo(channel, parent);
			} else {
				// It's a disconnect
				parent.reset();
			}
		} else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION
				.equals(action)) {

			parent.updateDevice((WifiP2pDevice) intent
					.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE));

		}
	}
}

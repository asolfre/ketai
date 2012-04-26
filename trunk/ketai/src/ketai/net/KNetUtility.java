package ketai.net;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import processing.core.PApplet;

public class KNetUtility {
	static public String getLocalIpAddress() {
		String thing = "0.0.0.0";
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress() && thing == "0.0.0.0"
							&& inetAddress.getHostAddress().matches("\\b(?:\\d{1,3}\\.){3}\\d{1,3}\\b")) {
						thing = inetAddress.getHostAddress();
					}
//					PApplet.println("IP address: "
//							+ inetAddress.getHostAddress());
				}
			}
		} catch (SocketException ex) {
			PApplet.println("SocketException:" + ex.toString());
		}
		catch (NullPointerException nx)
		{
			PApplet.println("Failed to get any network interfaces...");
		}
		return thing;
	}

}

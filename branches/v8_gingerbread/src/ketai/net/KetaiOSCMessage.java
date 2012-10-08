package ketai.net;
/*
 * 	This class is a utility class that exposes the parseMessage(byte[] data) 
 * 		protected method from the OscMessage class.  This allows us to parse
 * 		byte arrays into OscMessages.  This is useful over serial links
 * 		where IP is forsaken. It is identical to OscMessage for all usage
 * 		purposes except the constructor and exposing the isValid flag as
 * 		a means to checking if the byte array yielded a valide OSC message.
 */
import oscP5.OscMessage;

public class KetaiOSCMessage extends OscMessage{

	public KetaiOSCMessage(byte[] _data) {
		super("");
		this.parseMessage(_data);
	}
	
	public boolean isValid()
	{
		return isValid;
	}
	
}

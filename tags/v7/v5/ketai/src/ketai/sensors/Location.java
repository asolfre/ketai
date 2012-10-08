/* 
 * Simple wrapper for android Location class.
 * 
 */

package ketai.sensors;



public class Location extends android.location.Location{
	
	public Location(String _loc)
	{
		super(_loc);
	}

	public Location(android.location.Location l){
		super(l);
	}
}

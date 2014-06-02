package uk.ac.aber.astute.mfc1.ibeacon_demo;
import java.text.DecimalFormat;
import java.util.ArrayList;

import com.radiusnetworks.ibeacon.IBeacon;


import uk.ac.aber.astute.mfc1.ibeacon_demo.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Device List Adapter is used to hold a collection of devices
 * as they are found, and to present them using the "device_list_item"
 * layout style.
 * 
 * @author Michael Clarke <mfc1@aber.ac.uk>
 * @version 0.1
 */
public class DeviceListAdapter extends BaseAdapter {
	
	private ArrayList<IBeacon> devices;
	private LayoutInflater inflater;
	
	public DeviceListAdapter(Context context) {
		
		super();
		devices = new ArrayList<IBeacon>();
		inflater = (LayoutInflater)context.getSystemService(
									Context.LAYOUT_INFLATER_SERVICE);
		
	}
	
	/**
	 * Adds a new device to the list of devices to be displayed.
	 * 
	 * @param project The device to add to the list.
	 */
	public void addDevice(IBeacon device) {
	
		/* We might often get the same device found over and over again.
		 * We don't want to keep adding the device to the list, so, if
		 * it's already in the list, we won't add it again.
		 */
		if (!devices.contains(device))
			devices.add(device);
		
	}
	
	/**
	 * Removes a device from the list of devices to be displayed.
	 * 
	 * @param project The device to remove from the list.
	 */
	public void removeDevice(IBeacon device) {
		devices.remove(device);
	}
	
	/**
	 * Gets a count of the number of devices in the list.
	 * 
	 * @return A count of the devices in the visible list.
	 */
	public int getCount() {
		
		return devices.size();
		
	}
	
	/**
	 * Gets a specific device from the list of devices.
	 * 
	 * @return A device from the list of devices.
	 */
	public Object getItem(int position) {
		
		return devices.get(position);
		
	}
	
	/**
	 * Gets the ID of the device at the given position in the list.
	 * 
	 * @param position The position of the device in the list.
	 * 
	 * @return The ID of the device in the list at the given position.
	 */
	public long getItemId(int position) {
		
		return position;
		
	}
	
	/**
	 * Builds the view for the current device. Called per-device to populate
	 * the list of devices.
	 * 
	 * @param position The position of the device in the list.
	 * @param view The view to edit.
	 * @param viewGroup the view Group we're working with.
	 * 
	 * @return A view for the given device to be displayed in the list.
	 */
	public View getView(int position, View view, ViewGroup viewGroup) {
		
		TextView deviceUUID;
		TextView majorValue;
		TextView minorValue;
		TextView distanceValue;
		IBeacon device;
		
		view = inflater.inflate(R.layout.device_list_item, null);
		
		deviceUUID    = (TextView) view.findViewById(R.id.deviceUUID);
		majorValue    = (TextView) view.findViewById(R.id.majorValue);
		minorValue    = (TextView) view.findViewById(R.id.minorValue);
		distanceValue = (TextView) view.findViewById(R.id.distanceValue);
		
		device = devices.get(position);
		
		deviceUUID.setText(device.getProximityUuid());
		majorValue.setText(Integer.toString(device.getMajor()));
		minorValue.setText(Integer.toString(device.getMinor()));
		
		DecimalFormat df = new DecimalFormat("~ #.## m");
		distanceValue.setText(df.format(device.getAccuracy()));

		
		return view;

	}

	/**
	 * Clears the list of all devices.
	 */
	public void clear() {
		devices.clear();
		
	}
	

}

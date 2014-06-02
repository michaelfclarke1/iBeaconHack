package uk.ac.aber.astute.mfc1.ibeacon_demo;

import java.util.Collection;
import java.util.Iterator;

import com.radiusnetworks.ibeacon.IBeacon;
import com.radiusnetworks.ibeacon.IBeaconConsumer;
import com.radiusnetworks.ibeacon.IBeaconManager;
import com.radiusnetworks.ibeacon.RangeNotifier;
import com.radiusnetworks.ibeacon.Region;

import android.os.Bundle;
import android.os.RemoteException;
import android.app.Activity;
import android.widget.ListView;

public class Scanner extends Activity implements IBeaconConsumer {
	
	private IBeaconManager iBeaconManager = 
			IBeaconManager.getInstanceForApplication(this);
	
	private DeviceListAdapter foundDevices;
	
	@Override
	/**
	 * Main entry point for the app. Gets the device list view and displays
	 * it on screen. Also binds the iBeaconManager to this activity.
	 */
	protected void onCreate(Bundle savedInstanceState) {

		ListView listView;
		
		/* Perform normal action activities, such as calling onCreate()
		 * for the super class, getting hold of the "list" view where
		 * we will display found devices, and indeed the main activity screen
		 * layout.
		 */
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scanner);
		foundDevices = new DeviceListAdapter(this);
		listView = (ListView)findViewById(R.id.foundDevices);
		listView.setAdapter(foundDevices);
		iBeaconManager.bind(this);
		
		
	}
	
	/**
	 * When we destroy this activity we must also ask the iBeaconManager
	 * to unbind.
	 */
	protected void onDestroy() {
		super.onDestroy();
		iBeaconManager.unBind(this);
	}

	@Override
	/**
	 * Called on iBeaconServiceConnect. Used to start the iBeaconRanger
	 * service, and deal with callbacks from the service.
	 */
	public void onIBeaconServiceConnect() {

		/* If we find iBeacons, this gets called. */
		iBeaconManager.setRangeNotifier(new RangeNotifier() {
			
			@Override
			/**
			 * This method puts the given iBeacon into the list of iBeacons
			 * found on the screen.
			 */
			public void didRangeBeaconsInRegion(Collection<IBeacon> arg0,
					Region arg1) {

				/* Clear the recent devices displayed, we may have moved 
				 * away from them by now!
				 */
				foundDevices.clear();
				
				/* Check to see if we found a iBeacon. */
				if (arg0.size() > 0) {
					
					/* We've got some, now loop through them and add them
					 * to the found devices list.
					 */
					Iterator<IBeacon> itt = arg0.iterator();
					while(itt.hasNext()) {
						foundDevices.addDevice(itt.next());
					}
					
				}
				
				/*
				 * Notify the list adapter that the data set may have
				 * changed - i.e. ask it to update the displayed list.
				 */
				runOnUiThread(
						new Runnable() {
							public void run() {
								foundDevices.notifyDataSetChanged();
							}
						}
					);

			}
		});
		
		/* Try and start ranging for iBeacons. */
		try {
			 iBeaconManager.startRangingBeaconsInRegion(
					 new Region("myRangingUniqueId", null, null, null));
			 
		 } catch (RemoteException e) {   
			 e.printStackTrace();			 
		 }
		
	}
	
}

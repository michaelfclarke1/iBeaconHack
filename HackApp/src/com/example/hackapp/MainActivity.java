package com.example.hackapp;

import java.util.ArrayList;
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
import android.util.Log;
import android.widget.ListView;

/**
 * This app is a sample app for the iBeacon Hack. It demonstrates looking
 * in a database (using the SAWDatabaseHelper code) to detect local iBeacons
 * and then look in a database for information about those iBeacons.
 * 
 * It displays the information about the closes iBeacon in a list item.
 * 
 * The information in the database held about the iBeacon is "project" 
 * information such as a team name, project name, project description and
 * list of team members.
 * 
 * @author Michael Clarke <mfc1@aber.ac.uk>
 * @version 0.1
 */
public class MainActivity extends Activity implements IBeaconConsumer {

	private static final String TAG = "CONFERENCE_APP";
	
	private IBeaconManager iBeaconManager;
	private ProjectListAdapter nearProjects;
	
	@Override
	/**
	 * Main entry point for the app. Gets the project list view and displays
	 * it on screen. Also binds the iBeaconManager to this activity.
	 */
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		iBeaconManager = IBeaconManager.getInstanceForApplication(this);
		iBeaconManager.bind(this);
		nearProjects = new ProjectListAdapter(getBaseContext());
		
		ListView listView = (ListView)findViewById(R.id.listView1);
		listView.setAdapter(nearProjects);
		
		
	}

	@Override
	/**
	 * When we destroy this activity we must also ask the iBeaconManager
	 * to unbind.
	 */
	public void onDestroy() {
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
			
			/**
			 * This method puts the given iBeacon into the list of iBeacons
			 * found on the screen. It does this, only if the iBeacon is the
			 * closest and matches the required UUID, Major and Minor numbers.
			 */
			public void didRangeBeaconsInRegion(Collection<IBeacon> arg0,
																Region arg1) {
				
				/* Clear the recent projects displayed, we may have moved 
				 * away from them by now!
				 */
				nearProjects.clear();
							
				/* Check to see if we found a iBeacon. */
				if (arg0.size() > 0) {
					
					/* We've got some, now loop through them. If the iBeacon
					 * "IMMEDIATE" or "NEAR" we should add the given project to 
					 * a list of "near" projects.
					 */
					Iterator<IBeacon> itt = arg0.iterator();	
					while (itt.hasNext()) {

						IBeacon beacon = itt.next();
						
						Log.i(TAG, "Beacon UUID: " + beacon.getProximityUuid());
						Log.i(TAG, "Beacon Major: " + beacon.getMajor());
						Log.i(TAG, "Beacon Minor: " + beacon.getMinor());
						
						if (beacon.getProximity() == IBeacon.PROXIMITY_NEAR ||
						beacon.getProximity() == IBeacon.PROXIMITY_IMMEDIATE) {
							
							/* We only deal with iBeacons that match our 
							 * iBeacon hack uuid.
							 */
							if (beacon.getProximityUuid().equalsIgnoreCase(
									"e2c56db5-dffb-48d2-b060-d0f5a71096e0")) {
							
								/* Get the project assoicated with the UUID,
								 * Major and Minor number for the given iBeacon.
								 */
								Project project = 
									Project.getProjectByUUID(getBaseContext(), 
										beacon.getProximityUuid(),
												beacon.getMajor(), 
													beacon.getMinor(),
														beacon.getAccuracy());
								
								/* Check all the other projects already in the
								 * list, and if we're the closest project
								 * display us - otehrwise, leave the list as is.
								 */
								ArrayList<Project> nearP = nearProjects.getAll();
								if (nearP != null && nearP.size() >= 1) {
									if (nearP.get(0).getDistance() > 
												beacon.getAccuracy()) {
										nearProjects.clear();
										nearProjects.addProject(project);
									}
									
								} else {
									nearProjects.addProject(project);	
								}
								
							}
							
						}
						
						/*
						 * Notify the list adapter that the data set may have
						 * changed - i.e. ask it to update the displayed list.
						 */
						runOnUiThread(
							new Runnable() {
								public void run() {
									nearProjects.notifyDataSetChanged();
								}
							}
						);

					}
					
				}
				
			}
			
		});
		
		/* Try and start ranging for iBeacons. */
		try {
			 iBeaconManager.startRangingBeaconsInRegion(
					 new Region("myRangingUniqueId", null, null, null));
			 
		 } catch (RemoteException e) {   
			 
			 Log.e(TAG, "Unable to start ranging service.");
			 
		 }
		
	}

}
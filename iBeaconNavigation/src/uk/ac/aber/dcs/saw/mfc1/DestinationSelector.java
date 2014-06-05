package uk.ac.aber.dcs.saw.mfc1;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.radiusnetworks.ibeacon.IBeacon;
import com.radiusnetworks.ibeacon.IBeaconConsumer;
import com.radiusnetworks.ibeacon.IBeaconManager;
import com.radiusnetworks.ibeacon.RangeNotifier;
import com.radiusnetworks.ibeacon.Region;

import uk.ac.aber.dcs.saw.mfc1.nav.Graph;
import uk.ac.aber.dcs.saw.mfc1.nav.NavigationStep;
import uk.ac.aber.dcs.saw.mfc1.nav.Vertex;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;
import android.widget.TextView;

public class DestinationSelector extends Activity 
					implements IBeaconConsumer, SensorEventListener {

	private DestinationListAdapter adapter;
	private IBeaconManager iBeaconManager;
	private Graph g;
	private Vertex currentLocation = null;
	private TextView currentLocationTextArea;
	private TextView directions;
	
	private int headingShouldBe = 0;

	private SensorManager sensorManager;
	Sensor accelerometer;
	Sensor magnetometer;
	private float orientationValues[] = new float[3];
	private float filter[] = new float[15];
	private int filter_id = 0;
	float[] gravity = null;
	float[] geomagnetic = null;
	float filter_value = 0;
	
	private boolean downstairs = false;
	private boolean upstairs = false;
	
	private Compass c;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_destination_selector);
		
		sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
	    accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
	    magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

		g = new Graph(getBaseContext(), "navDB");
		adapter = new DestinationListAdapter(getBaseContext(), g.locations());
		
		sensorManager.registerListener(this, accelerometer, 
											SensorManager.SENSOR_DELAY_UI);
		
		sensorManager.registerListener(this, magnetometer, 
											SensorManager.SENSOR_DELAY_UI);
		
		c = (Compass) findViewById(R.id.compass);
		
		Spinner goToSpinner = (Spinner) findViewById(R.id.locations);
		currentLocationTextArea = (TextView) findViewById(R.id.currentLocation);
		
		goToSpinner.setAdapter(adapter);
		currentLocationTextArea.setText("Unknown");
		
		directions = (TextView) findViewById(R.id.directions);
		
		goToSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, 
									View arg1, int arg2, long arg3) {
				
				adapter.setSelected(arg2);
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) { }
			
		});
		
		iBeaconManager = IBeaconManager.getInstanceForApplication(this);
		iBeaconManager.bind(this);
		
	}
	
	/**
	 * When we destroy this activity we must also ask the iBeaconManager
	 * to unbind.
	 */
	public void onDestroy() {
		
		super.onDestroy();
		iBeaconManager.unBind(this);
		sensorManager.unregisterListener(this);
		
	}

	protected void onResume() {
		
		super.onResume();
		
		sensorManager.registerListener(this, accelerometer, 
												SensorManager.SENSOR_DELAY_UI);
		
		sensorManager.registerListener(this, magnetometer, 
												SensorManager.SENSOR_DELAY_UI);
		
	}
		 
	protected void onPause() {
		
		super.onPause();
		sensorManager.unregisterListener(this);
		
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
			public void didRangeBeaconsInRegion(Collection<IBeacon> arg0,
					Region arg1) {
		
				currentLocation = null;
				
				/* Check to see if we found a iBeacon. */
				if (arg0.size() > 0) {
		
					/* We've got some, now loop through them. If the iBeacon
					 * "IMMEDIATE" or "NEAR" we should add the given project to 
					 * a list of "near" projects.
					 */
					Iterator<IBeacon> itt = arg0.iterator();	
					while (itt.hasNext()) {
						
						IBeacon temp = itt.next();
						
						if (temp.getProximity() == IBeacon.PROXIMITY_NEAR ||
						temp.getProximity() == IBeacon.PROXIMITY_IMMEDIATE) {
							currentLocation = g.getCurrentLocation(
								temp.getProximityUuid(), 
											temp.getMajor(), temp.getMinor());
						}
						
					}

				}
				
				runOnUiThread(
						
						new Runnable() {
							
							public void run() {
				
								if (currentLocation == null)
									currentLocationTextArea.setText("Unknown");
								else
									currentLocationTextArea.setText(
											currentLocation.getFriendlyName());
								
								ArrayList<NavigationStep> steps = 
										g.navigate(currentLocation, adapter.getCurrentSelected());
								
								if (steps != null) {
									
									if (steps.size() >= 1) {
										
										NavigationStep dire = steps.get(steps.size()-1);
										
										upstairs = false;
										downstairs = false;
										
										     if (dire.getDirection().equalsIgnoreCase("n"))   headingShouldBe = 0;
										else if (dire.getDirection().equalsIgnoreCase("nne")) headingShouldBe = 23;
										else if (dire.getDirection().equalsIgnoreCase("ne"))  headingShouldBe = 45;
										else if (dire.getDirection().equalsIgnoreCase("ene")) headingShouldBe = 68;
										else if (dire.getDirection().equalsIgnoreCase("e"))   headingShouldBe = 90;
										else if (dire.getDirection().equalsIgnoreCase("ese")) headingShouldBe = 113;
										else if (dire.getDirection().equalsIgnoreCase("se"))  headingShouldBe = 135;
										else if (dire.getDirection().equalsIgnoreCase("sse")) headingShouldBe = 158;
										else if (dire.getDirection().equalsIgnoreCase("s"))   headingShouldBe = 180;
										else if (dire.getDirection().equalsIgnoreCase("ssw")) headingShouldBe = 203;
										else if (dire.getDirection().equalsIgnoreCase("sw"))  headingShouldBe = 225;
										else if (dire.getDirection().equalsIgnoreCase("wsw")) headingShouldBe = 248;
										else if (dire.getDirection().equalsIgnoreCase("w"))   headingShouldBe = 270;
										else if (dire.getDirection().equalsIgnoreCase("wnw")) headingShouldBe = 293;
										else if (dire.getDirection().equalsIgnoreCase("nw"))  headingShouldBe = 315;
										else if (dire.getDirection().equalsIgnoreCase("nnw")) headingShouldBe = 338;
										else if (dire.getDirection().equalsIgnoreCase("upstairs")) {
											upstairs = true;
										} else if (dire.getDirection().equalsIgnoreCase("downstairs")) {
											downstairs = true;
										}
												
										directions.setText(steps.get(steps.size()-1).toString());
										
									}	
								}
								
								
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
	
	public int headingCorrection(int current, int desired) {
		
		int diff = current - desired;
		if (current > desired) {
			diff = 360 - diff; 
		}
		diff %= 360;
		return Math.abs(diff);
		
	}
	
	public void onSensorChanged(SensorEvent event) {
	
		float rmx[] = new float[9];
	    float imx[] = new float[9];
		
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
			gravity = event.values;
		
		if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)		
			geomagnetic = event.values;
		
		if (gravity != null && geomagnetic != null) {
		      if(SensorManager.getRotationMatrix(rmx, imx, gravity, geomagnetic)) {
		    	  
		    	if (this.filter_id >= this.filter.length)
		    		this.filter_id = 0;
		    	
		    	this.filter[filter_id] = this.orientationValues[0];
		    	this.filter_id++;
		    	  
		        SensorManager.getOrientation(rmx, this.orientationValues);
		        this.orientationValues[0] *= (180 / Math.PI);
		        this.orientationValues[0] += 180;
		        this.orientationValues[0] %= 360;
		        
		        this.orientationValues[0] = 
		        		(float)headingCorrection((int)this.orientationValues[0], this.headingShouldBe);
		        
		        this.orientationValues[0] /= 180;
		        this.orientationValues[0] *= Math.PI;
		        
		        filter_value = 0;
		        for (int i = 0; i < filter.length; i++)
		        	filter_value += filter[i];

	        	c.setHeading((this.orientationValues[0] + this.filter_value) / (filter.length + 1));
		        c.setUpstairsDownstairs(upstairs, downstairs);
		        
			}
		}
		
	}
		
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) { }
	
}

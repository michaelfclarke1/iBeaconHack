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
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView;
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
	private ImageView compassView;
	
	private int headingShouldBe = 0;

	private SensorManager sensorManager;
	Sensor accelerometer;
	Sensor magnetometer;
	private float orientationValues[] = new float[3];
	float[] gravity = null;
	float[] geomagnetic = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_destination_selector);
		
		sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
	    accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
	    magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
	    
		
	    
		g = new Graph(getBaseContext(), "navDB");
		adapter = new DestinationListAdapter(getBaseContext(), g.locations());
		
		sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
		sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
		
		compassView = (ImageView) findViewById(R.id.imageView1);
		
		Spinner goToSpinner = (Spinner) findViewById(R.id.locations);
		currentLocationTextArea = (TextView) findViewById(R.id.currentLocation);
		
		goToSpinner.setAdapter(adapter);
		currentLocationTextArea.setText("Unknown");
		
		 
		
		directions = (TextView) findViewById(R.id.directions);
		
		goToSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				adapter.setSelected(arg2);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
			
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
		sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
		sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
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
									if (steps.size() >= 2) {
										NavigationStep dire = steps.get(steps.size()-2);
										if (dire.toString().equalsIgnoreCase("n")) headingShouldBe = 0;
										if (dire.toString().equalsIgnoreCase("nne")) headingShouldBe = 23;
										if (dire.toString().equalsIgnoreCase("ne")) headingShouldBe = 45;
										if (dire.toString().equalsIgnoreCase("ene")) headingShouldBe = 68;
										if (dire.toString().equalsIgnoreCase("e")) headingShouldBe = 90;
										if (dire.toString().equalsIgnoreCase("ese")) headingShouldBe = 113;
										if (dire.toString().equalsIgnoreCase("se")) headingShouldBe = 135;
										if (dire.toString().equalsIgnoreCase("sse")) headingShouldBe = 158;
										if (dire.toString().equalsIgnoreCase("s")) headingShouldBe = 180;
										if (dire.toString().equalsIgnoreCase("ssw")) headingShouldBe = 203;
										if (dire.toString().equalsIgnoreCase("sw")) headingShouldBe = 225;
										if (dire.toString().equalsIgnoreCase("wsw")) headingShouldBe = 248;
										if (dire.toString().equalsIgnoreCase("w")) headingShouldBe = 270;
										if (dire.toString().equalsIgnoreCase("wnw")) headingShouldBe = 293;
										if (dire.toString().equalsIgnoreCase("nw")) headingShouldBe = 315;
										if (dire.toString().equalsIgnoreCase("nnw")) headingShouldBe = 338;
												
										String str = "At " + steps.get(steps.size()-1) + " head " + steps.get(steps.size()-2) + "."; 
										directions.setText(str);
										
										
										
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
		        SensorManager.getOrientation(rmx, this.orientationValues);
		        this.orientationValues[0] *= (180 / Math.PI);
		        this.orientationValues[0] += 180;
		        this.orientationValues[0] %= 360;
		        
		        this.orientationValues[0] = (float)headingCorrection((int)this.orientationValues[0], this.headingShouldBe);
		        
		        if (this.orientationValues[0] >= 337 && this.orientationValues[0] < 23)
		        	compassView.setImageResource(R.drawable.n);
		        else if (this.orientationValues[0] >=23 && this.orientationValues[0] < 45)
		        	compassView.setImageResource(R.drawable.nne);
		        else if (this.orientationValues[0] >=45 && this.orientationValues[0] < 68)
		        	compassView.setImageResource(R.drawable.ene);
		        else if (this.orientationValues[0] >= 68 && this.orientationValues[0] < 90)
		        	compassView.setImageResource(R.drawable.e);
		        else if (this.orientationValues[0] >= 90 && this.orientationValues[0] < 113)
		        	compassView.setImageResource(R.drawable.ese);
		        else if (this.orientationValues[0] >= 113 && this.orientationValues[0] < 137)
		        	compassView.setImageResource(R.drawable.se);
		        else if (this.orientationValues[0] >=137 && this.orientationValues[0] < 160)
		        	compassView.setImageResource(R.drawable.sse);
		        else if (this.orientationValues[0] >=160 && this.orientationValues[0] <183)
		        	compassView.setImageResource(R.drawable.s);
		        else if (this.orientationValues[0] >= 183 && this.orientationValues[0] < 206)
		        	compassView.setImageResource(R.drawable.ssw);
		        else if (this.orientationValues[0] >= 206 && this.orientationValues[0] < 229)
		        	compassView.setImageResource(R.drawable.sw);
		        else if (this.orientationValues[0] >= 229 && this.orientationValues[0] < 252)
		        	compassView.setImageResource(R.drawable.wsw);
		        else if (this.orientationValues[0] >= 252 && this.orientationValues[0] < 275)
		        	compassView.setImageResource(R.drawable.w);
		        else if (this.orientationValues[0] >= 275 && this.orientationValues[0] < 298)
		        	compassView.setImageResource(R.drawable.wnw);
		        else if (this.orientationValues[0] >=298 && this.orientationValues[0] < 321)
		        	compassView.setImageResource(R.drawable.nw);
		        else if (this.orientationValues[0] >= 321 && this.orientationValues[0] < 344)
		        	compassView.setImageResource(R.drawable.nnw);
		        }
		}
		
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) { }
	
}

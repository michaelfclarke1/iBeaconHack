package com.example.hackapp;

import java.util.ArrayList;

import uk.ac.aber.saw.hackutils.DataNotFoundException;
import uk.ac.aber.saw.hackutils.SAWDatabaseHelper;

import android.content.Context;
import android.util.Log;

/**
 * Class to represent a single project in the app. Most of this data
 * matches on to the database.
 * 
 * @author Michael Clarke <mfc1@aber.ac.uk>
 * @version 0.1
 */
public class Project {

	private String uuid;
	private int major;
	private int minor;
	private String teamName;
	private String projectName;
	private String projectDescription;
	private ArrayList<String> projectMembers;
	private double distance;
	
	private static String TAG = "PROJECT_CLASS";
		
	/**
	 * Gets a project from the database, where the unique ID for the project
	 * is a combination of the uuid, major and minor numbers.
	 * 
	 * The distance to the project is also stored for ordering of the list on
	 * the screen.
	 * 
	 * @param context The Android application context we're working in.
	 * 
	 * @param uuid The UUID of the iBeacon we've detected.
	 * @param major The Major number of the iBeacon we've detected.
	 * @param minor The Minor number of the iBeacon we've detected.
	 * 
	 * @param distance The distance to the iBeacon we've detected.
	 * 
	 * @return A project from the database that matches the given unique uuid,
	 *         major and minor numbers.
	 */
	public static Project getProjectByUUID(Context context, String uuid, 
									int major, int minor, double distance) {
		
		
		Project project = new Project();
		
		SAWDatabaseHelper sdh = 
				SAWDatabaseHelper.getInstance(context, "projects");
		
		String[] key = new String[] { uuid, ""+major, ""+minor };
		
		
		try {
			
			project.projectName = sdh.getStringWithQuery("projects", 
						"projectName", "uuid=? AND major=? AND minor=?", key);
			
		} catch (DataNotFoundException dnfe) {
			Log.i(TAG, "Unable to find project based on UUID.");
		}
		
		
		try {
			
			project.projectDescription = sdh.getStringWithQuery("projects",
						"projectDesc", "uuid=? AND major=? AND minor=?", key);
			
		} catch (DataNotFoundException dnfe) {
			Log.i(TAG, "Unable to find project description based on UUID.");
		}
		
		
		try {
			
			project.teamName = sdh.getStringWithQuery("projects", 
							"teamName", "uuid=? AND major=? AND minor=?", key);
			
		} catch (DataNotFoundException dnfe) {
			Log.e(TAG, "Unable to find teamName based on UUID.");
		}
		
		
		try {
			project.projectMembers = sdh.getStringArrayWithQuery("members",
								"name", "uuid=? AND major=? AND minor=?", key);
		} catch (DataNotFoundException dnfe) {
			Log.e(TAG, "Unable to find list of team members for UUID.");
		}
		
		
		project.uuid = uuid;
		project.distance = distance;
		project.minor = minor;
		project.major = major;
		
		
		return project;
	
	}

	private Project() { }
	
	/**
	 * Gets the name of the team for the project.
	 * 
	 * @return The project team name.
	 */
	public String getTeamName() {
		return this.teamName;
	}
	
	/**
	 * Gets the name of the project.
	 * 
	 * @return The name of the project.
	 */
	public String getProjectName() {
		return this.projectName;
	}
	
	/**
	 * Gets the description for the project.
	 * 
	 * @return The description for the project.
	 */
	public String getProjectDescription() {
		return this.projectDescription;
	}
	
	/**
	 * Gets a list of the team members working on the project.
	 * 
	 * @return List of members working on the project.
	 */
	public ArrayList<String> getProjectMembers() {
		return this.projectMembers;
	}
	
	/**
	 * Gets the UUID for the project.
	 * 
	 * @return The project UUID.
	 */
	public String getUUID() {
		return this.uuid;
	}
	
	/**
	 * Gets the Major number for the project.
	 * 
	 * @return The project Major number.
	 */
	public int getMajor() {
		return this.major;
	}
	
	/**
	 * Gets the Minor number for the project.
	 * 
	 * @return The project Minor number.
	 */
	public int getMinor() {
		return this.minor;
	}
	
	/**
	 * Gets the last recorded distance to the project.
	 *  
	 * @return The last recorded distance to the project.
	 */
	public double getDistance() {
		return this.distance;
	}
	
}

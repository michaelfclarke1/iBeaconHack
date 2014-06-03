package uk.ac.aber.dcs.saw.mfc1.nav;

import java.util.ArrayList;

public class Vertex {

	private String uuid;
	private int major;
	private int minor;
	private String friendlyName;
	private ArrayList<Edge> edgesOut;
	private int distance;
	private Vertex previous;
	
	public Vertex(String uuid, int major, int minor, String friendlyName) {
		edgesOut = new ArrayList<Edge>();
		this.uuid = uuid;
		this.minor = minor;
		this.major = major;
		this.friendlyName = friendlyName;
	}
	
	public void setDistance(int distance) {
		this.distance = distance;
	}
	
	public void setPrevious(Vertex previous) {
		this.previous = previous;
	}
	
	public int getDistance() {
		return this.distance;
	}
	
	public Vertex getPrevious() {
		return this.previous;
	}
	
	public void addEdge(Edge edge) {
		edgesOut.add(edge);
	}
	
	public String getUUID() {
		return this.uuid;
	}
	
	public int getMajor() {
		return this.major;
	}
	
	public int getMinor() {
		return this.minor;
	}
	
	public String getFriendlyName() {
		return this.friendlyName;
	}
	
	public ArrayList<Edge> getEdgesOut() {
		return this.edgesOut;
	}
	
	/**
	 * Returns a unique string representing this node in the graph. The
	 * unique string is a combination of the iBeacon UUID, Major and
	 * Minor numbers.
	 * 
	 * So, a iBeacon with the following UUID, Major and Minor numbers:
	 * 
	 * UUID: e2c56db5-dffb-48d2-b060-d0f5a71096e0
	 * Major: 10
	 * Minor: 1
	 * 
	 * Would return a unique hash string of:
	 * 
	 * e2c56db5-dffb-48d2-b060-d0f5a71096e0-10-1
	 * 
	 * @return A unique hash string for this node in the graph.
	 */
	public String getHash() {
		return this.uuid+"-"+this.major+"-"+this.minor;
	}
	
}

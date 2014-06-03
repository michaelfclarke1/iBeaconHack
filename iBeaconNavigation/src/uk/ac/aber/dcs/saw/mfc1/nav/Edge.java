package uk.ac.aber.dcs.saw.mfc1.nav;

public class Edge {

	private Vertex from;
	private Vertex to;
	private int weight;
	private FriendlyNav navString;
	
	public Edge(Vertex from, Vertex to, int weight, FriendlyNav navString) {
		
		this.from = from;
		this.to = to;
		this.weight = weight;
		this.navString = navString;
		
	}
	
	public Vertex getFrom() {
		return this.from;
	}
	
	public Vertex getTo() {
		return this.to;
	}

	public int getWeight() {
		return this.weight;
	}
	
	public FriendlyNav getFriendlyNav() {
		return this.navString;
	}
	
}

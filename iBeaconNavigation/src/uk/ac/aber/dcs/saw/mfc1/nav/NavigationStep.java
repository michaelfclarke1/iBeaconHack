package uk.ac.aber.dcs.saw.mfc1.nav;

public class NavigationStep {

	private Vertex vertex;
	private Edge edge;
	
	public NavigationStep(Vertex vertex) {
		this.vertex = vertex;
		this.edge = null;
	}
	
	public NavigationStep(Edge edge) {
		this.edge = edge;
		this.vertex = null;
	}
	
	public String toString() {
		if (this.edge != null) 
			return this.edge.getFriendlyNav().name();
		else
			return this.vertex.getFriendlyName();
		
	}
	
}

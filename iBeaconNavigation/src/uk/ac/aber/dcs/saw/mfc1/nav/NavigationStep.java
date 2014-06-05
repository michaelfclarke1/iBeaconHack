package uk.ac.aber.dcs.saw.mfc1.nav;

public class NavigationStep {

	private Edge edge;
	
	public NavigationStep(Edge edge) {
		this.edge = edge;
	}
	
	public String getDirection() {
		return this.edge.getFriendlyNav().name();
	}
	
	public String toString() {
			return this.edge.getInstruction();
	}
	
}

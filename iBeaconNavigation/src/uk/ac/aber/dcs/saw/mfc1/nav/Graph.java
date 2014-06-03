package uk.ac.aber.dcs.saw.mfc1.nav;

import java.util.ArrayList;
import java.util.HashMap;

import uk.ac.aber.saw.hackutils.DataNotFoundException;
import uk.ac.aber.saw.hackutils.SAWDatabaseHelper;

import android.content.Context;
import android.util.Log;

public class Graph {
	
	HashMap<String, Vertex> graph = new HashMap<String, Vertex>();
	HashMap<String, Vertex> useful = new HashMap<String, Vertex>();
	ArrayList<Vertex> allVertex = new ArrayList<Vertex>();
	
	public Graph(Context context, String databaseName) {
		
		ArrayList<String> vertexIds = null;
		
		/* Get a list of vertexts from the database and add them to the graph
		 * hash map. Right now, however, we're not going to connect them up.
		 */
		SAWDatabaseHelper sdh = SAWDatabaseHelper.getInstance(context, databaseName);
		
		try {
			vertexIds = sdh.getStringArrayWithQuery("vertex", "id", null, null);
		} catch (DataNotFoundException e) {
			e.printStackTrace();
		}
		
		if (vertexIds != null) {
			
			for (int i = 0; i < vertexIds.size(); i++) {

				try {
					
					String[] id = new String[] { vertexIds.get(i)  };

					String uuid = sdh.getStringWithQuery("vertex", "uuid", "id=?", id);
					String major = sdh.getStringWithQuery("vertex", "major", "id=?", id);
					String minor = sdh.getStringWithQuery("vertex", "minor", "id=?", id);
					String name = sdh.getStringWithQuery("vertex", "name", "id=?", id);

					Vertex v = new Vertex(uuid, Integer.valueOf(major), Integer.valueOf(minor), name);
					graph.put(v.getHash(), v);
					allVertex.add(v);
					useful.put(vertexIds.get(i), v);
					
				} catch (DataNotFoundException e) {
					e.printStackTrace();
				}
				
			}
			
		}
		
		/* Now get all the edges for the graph, and join 
		 * the vertexs together. 
		 */
		try {
			vertexIds = sdh.getStringArrayWithQuery("edge", "id", null, null );
		} catch (DataNotFoundException e) {
			e.printStackTrace();
		}
		
		if (vertexIds != null) {
			
			for (int i = 0; i < vertexIds.size(); i++) {
				
				try {
					
					String id[] = new String[] { vertexIds.get(i) };

					String from = sdh.getStringWithQuery("edge", "from_id", "id=?", id);
					String to = sdh.getStringWithQuery("edge", "to_id", "id=?", id);
					Integer weight = sdh.getIntegerWithQuery("edge", "weight", "id=?", id);
					String direction = sdh.getStringWithQuery("edge", "direction", "id=?", id);
					
					Vertex fromV = useful.get(from);
					Vertex toV = useful.get(to);
					
					FriendlyNav dir;
					
					     if (direction.equalsIgnoreCase("downstairs")) dir = FriendlyNav.DOWNSTAIRS;
					else if (direction.equalsIgnoreCase("upstairs"))   dir = FriendlyNav.UPSTAIRS;
					else if (direction.equalsIgnoreCase("NNE"))        dir = FriendlyNav.NNE;
					else if (direction.equalsIgnoreCase("NE"))         dir = FriendlyNav.NE;
					else if (direction.equalsIgnoreCase("ENE"))        dir = FriendlyNav.ENE;
					else if (direction.equalsIgnoreCase("E"))          dir = FriendlyNav.E;
					else if (direction.equalsIgnoreCase("ESE"))        dir = FriendlyNav.ESE;
					else if (direction.equalsIgnoreCase("SE"))         dir = FriendlyNav.SE;
					else if (direction.equalsIgnoreCase("SSE"))        dir = FriendlyNav.SSE;
					else if (direction.equalsIgnoreCase("S"))          dir = FriendlyNav.S;
					else if (direction.equalsIgnoreCase("SSW"))        dir = FriendlyNav.SSW;
					else if (direction.equalsIgnoreCase("SW"))         dir = FriendlyNav.SW;
					else if (direction.equalsIgnoreCase("WSW"))        dir = FriendlyNav.WSW;
					else if (direction.equalsIgnoreCase("W"))          dir = FriendlyNav.W;
					else if (direction.equalsIgnoreCase("WNW"))        dir = FriendlyNav.WNW;
					else if (direction.equalsIgnoreCase("NW"))         dir = FriendlyNav.NW;
					else if (direction.equalsIgnoreCase("NNW"))        dir = FriendlyNav.NNW;
					else    								           dir = FriendlyNav.N;
					
					Edge e = new Edge(fromV, toV, weight, dir);
					fromV.addEdge(e);
					
				} catch (DataNotFoundException e) {
					e.printStackTrace();
				}
				
			}
			
		}
		
		/* No longer useful, was only worth keeping whilst building the graph. */
		useful = null;
		
		
		
		
		
		
		
		
		
		
		
		/* Build a fake graph for now. We should really load the graph from 
		 * the database for the final app.
		 */
		/*
		Vertex reception = new Vertex("e2c56db5-dffb-48d2-b060-d0f5a71096e0", 10, 1, "Reception");
		Vertex coffeeRoom = new Vertex("e2c56db5-dffb-48d2-b060-d0f5a71096e0", 10, 2, "Coffee Room");
		Vertex reyersOffice = new Vertex("e2c56db5-dffb-48d2-b060-d0f5a71096e0", 10, 3, "Reyer's Office");
		Vertex delph = new Vertex("e2c56db5-dffb-48d2-b060-d0f5a71096e0", 10, 4, "Delph Computer Room");
		Vertex tomsOffice = new Vertex("e2c56db5-dffb-48d2-b060-d0f5a71096e0", 10, 5, "Tom's Office");
		
		Edge receptionToCoffeeRoom = new Edge(reception, coffeeRoom, 1, FriendlyNav.SW);
		Edge coffeeRoomToReception = new Edge(coffeeRoom, reception, 1, FriendlyNav.N);
		
		Edge receptionToDelph = new Edge(reception, delph, 1, FriendlyNav.UPSTAIRS);
		Edge delphToReception = new Edge(delph, reception, 1, FriendlyNav.DOWNSTAIRS);
		
		Edge coffeeRoomToReyersOffice = new Edge(coffeeRoom, reyersOffice, 1, FriendlyNav.SW);
		Edge reyersOfficeToCoffeeRoom = new Edge(reyersOffice, coffeeRoom, 1, FriendlyNav.N);
		
		Edge reyersOfficeToTomsOffice = new Edge(reyersOffice, tomsOffice, 1, FriendlyNav.UPSTAIRS);
		Edge tomsOfficeToReyersOffice = new Edge(tomsOffice, reyersOffice, 1, FriendlyNav.DOWNSTAIRS);
		
		reception.addEdge(receptionToDelph);
		reception.addEdge(receptionToCoffeeRoom);
		
		coffeeRoom.addEdge(coffeeRoomToReyersOffice);
		coffeeRoom.addEdge(coffeeRoomToReception);
		
		delph.addEdge(delphToReception);
		
		reyersOffice.addEdge(reyersOfficeToTomsOffice);
		reyersOffice.addEdge(reyersOfficeToCoffeeRoom);
		
		tomsOffice.addEdge(tomsOfficeToReyersOffice);
		
		graph.put(reception.getHash(), reception);
		graph.put(coffeeRoom.getHash(), coffeeRoom);
		graph.put(reyersOffice.getHash(), reyersOffice);
		graph.put(delph.getHash(), delph);
		graph.put(tomsOffice.getHash(), tomsOffice);
		
		allVertex.add(reception);
		allVertex.add(coffeeRoom);
		allVertex.add(reyersOffice);
		allVertex.add(delph);
		allVertex.add(tomsOffice);
		
		ArrayList<NavigationStep> test = navigate(reyersOffice, delph);

		System.out.printf("Steps: %d\n", test.size());
		for (int i = 0; i < test.size(); i++) {
			System.out.printf("Step %d: %s\n", i, test.get(i).toString());
		}

		test = navigate(tomsOffice, delph);

		System.out.printf("Steps: %d\n", test.size());
		for (int i = 0; i < test.size(); i++) {
			System.out.printf("Step %d: %s\n", i, test.get(i).toString());
		}

		test = navigate(coffeeRoom, tomsOffice);

		System.out.printf("Steps: %d\n", test.size());
		for (int i = 0; i < test.size(); i++) {
			System.out.printf("Step %d: %s\n", i, test.get(i).toString());
		}
		*/
		
		
		
		
		
		
		
		
		
	}
	
	public ArrayList<Vertex> locations() {
		return this.allVertex;
	}
	
	public Vertex getCurrentLocation(String uuid, int major, int minor) {
		return this.graph.get(uuid+"-"+major+"-"+minor); 
	}
	
	public ArrayList<NavigationStep> navigate(Vertex from, Vertex to) {
		
		if (from == null || to == null) return null;
		
		ArrayList<NavigationStep> steps = new ArrayList<NavigationStep>();
		ArrayList<Vertex> queue = new ArrayList<Vertex>();

		/* Reset all distances in graph before search. */
		from.setDistance(0);
		from.setPrevious(null);
		for (int i = 0; i < allVertex.size(); i++) {
			if (!allVertex.get(i).equals(from)) {
				allVertex.get(i).setDistance(100000);
				allVertex.get(i).setPrevious(null);
			}
			queue.add(allVertex.get(i));
		}

		Vertex current = from;
		while (!queue.isEmpty()) {

			current = queue.get(0);
			for (int i = 0; i < queue.size(); i++) {
				if (queue.get(i).getDistance() < current.getDistance()) {
					current = queue.get(i);
				}
			}
			queue.remove(current);

			
			if (current == to) break;
		
			for(int i = 0; i < current.getEdgesOut().size(); i++) {
				
				Vertex v = current.getEdgesOut().get(i).getTo();
				
				int alt = current.getDistance() + current.getEdgesOut().get(i).getWeight();
				if (alt < v.getDistance()) {
					v.setDistance(alt);
					v.setPrevious(current);
				}
			}

		}

		/* Reverse build path. */
		while (current.getPrevious() != null) {
			for (int i = 0; i < current.getPrevious().getEdgesOut().size(); i++) {
				if (current.getPrevious().getEdgesOut().get(i).getTo() == current)
					steps.add(new NavigationStep(current.getPrevious().getEdgesOut().get(i)));
			}
			steps.add(new NavigationStep(current.getPrevious()));
			current = current.getPrevious();
		}

		return steps;
		
	}
		
}

package graphwork.graph;

import java.util.Objects;

public class Edge {

	private final Vertex destination;
	private final float weight;

	public Edge(Vertex destination, float weight) {
		this.destination = destination;
		this.weight = weight;
	}

	public Vertex getDestination() {
		return destination;
	}

	public float getWeight() {
		return weight;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Edge other = (Edge) obj;
		if (!Objects.equals(this.destination, other.destination)) {
			return false;
		} else if (!Objects.equals(this.weight, other.weight)) {
			return false;
		}
		
		return true;
	}
	
	@Override
	public int hashCode() {
		return this.destination.getId();
	}
	
	@Override
	public String toString() {
		return "(" + destination + ", " + weight + ")";
	}

}

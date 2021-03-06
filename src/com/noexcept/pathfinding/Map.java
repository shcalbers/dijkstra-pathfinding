package com.noexcept.pathfinding;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.noexcept.math.Vector3D;

import static com.noexcept.math.Vector3D.distance;

public class Map {

	public static class Builder {

		private HashMap<Vector3D, Node> nodes;
		
		public Builder() {
			this.nodes = new HashMap<Vector3D, Node>();
		}
		
		public Builder addVertex(Vector3D vertex) {
			nodes.put(vertex, new Node(vertex));
			return this;
		}
		
		public Builder connect(Vector3D vertexA, Vector3D vertexB) {
			try {
				return tryConnect(vertexA, vertexB);
			} catch (NullPointerException e) {
				throw new IllegalArgumentException("One or both of the supplied vertices were not found!");
			}
		}
		
		private Builder tryConnect(Vector3D vertexA, Vector3D vertexB) {
			Node nodeA = nodes.get(vertexA);
			Node nodeB = nodes.get(vertexB);
			
			nodeA.addNeighbour(nodeB);
			nodeB.addNeighbour(nodeA);
			
			return this;
		}
		
		public Map build() {
			return new Map(this);
		}
		
	}
	
	private static class Node {
		
		private static class Metadata {
			public Node previous;
			public double distance;
		
			public Metadata() {
				this.previous = null;
				this.distance = Double.MAX_VALUE;
			}
			
			public void reset() {
				this.previous = null;
				this.distance = Double.MAX_VALUE;
			}
		}
		
		private Vector3D position;
		private LinkedList<Node> neighbours;
		
		private Metadata metadata; 
		
		private Node(Vector3D position) {
			this.position = position;
			this.neighbours = new LinkedList<Node>();
			
			this.metadata = new Metadata();
		}
		
		public Vector3D getPosition() {
			return this.position;
		}
		
		public List<Node> getNeighbours() {
			return Collections.unmodifiableList(neighbours);
		}
		
		private void addNeighbour(Node node) {
			this.neighbours.add(node);
		}
		
		private Metadata getMetadata() { 
			return this.metadata;
		}
		
	}
	
	private HashMap<Vector3D, Node> nodes;
	
	private Map(Builder builder) {
		this.nodes = builder.nodes;
	}
	
	public Path findShortestPath(Vector3D start_vertex, Vector3D end_vertex) {
		try {
			return tryFindShortestPath(start_vertex, end_vertex);
		} catch (NullPointerException e){
			throw new IllegalArgumentException("One or both of the supplied vertices were not found on the map");
		}
	}
	
	private Path tryFindShortestPath(Vector3D start_vertex, Vector3D end_vertex) {
		Node source_node = nodes.get(start_vertex);
		Node target_node = nodes.get(end_vertex);
		
		executeDijkstraAlgorithm(source_node, target_node);
		
		Path.Builder pathBuilder = new Path.Builder();
		
		if (target_node.getMetadata().previous != null || target_node != source_node) {
			for (Node current_node = target_node; current_node != null; current_node = current_node.getMetadata().previous) {
				pathBuilder.addPoint(current_node.getPosition());
			}
		}

		return pathBuilder.build();
	}
	
	private void executeDijkstraAlgorithm(Node source_node, Node target_node) {
		Set<Node> unvisited_nodes = new HashSet<Node>(nodes.size());
		for (Node node : nodes.values()) {
			node.getMetadata().reset();
			unvisited_nodes.add(node);
		}
		
		source_node.getMetadata().distance = 0.0;
		
		while (!unvisited_nodes.isEmpty()) {
			Node current_node = findNearestNode(unvisited_nodes);
			
			if (current_node == target_node)
				break;
				
			unvisited_nodes.remove(current_node);
			
			for (Node neighbour_node : current_node.getNeighbours()) {
				double distance = current_node.getMetadata().distance + distance(neighbour_node.getPosition(), current_node.getPosition());
				
				Node.Metadata neighbour_metadata = neighbour_node.getMetadata();
				if (distance < neighbour_metadata.distance) {
					neighbour_metadata.distance = distance;
					neighbour_metadata.previous = current_node;
				}
			}
		}
	}
	
	private Node findNearestNode(Set<Node> nodes) {
		Node nearest_node = null;
		
		for (Node node : nodes) {
			if (nearest_node == null || node.getMetadata().distance < nearest_node.getMetadata().distance) {
				nearest_node = node;
			}
		}
		
		return nearest_node;
	}
	
}

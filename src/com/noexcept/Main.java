package com.noexcept;

import com.noexcept.math.Vector3D;
import com.noexcept.pathfinding.Map;
import com.noexcept.pathfinding.Path;

public final class Main {

	public static void main(String[] args) {
        Vector3D[] vertices = {
        		new Vector3D(-1,  0, -1),
        		new Vector3D(-1,  0, +1),
        		new Vector3D(+1,  0, +1),
        		new Vector3D(+1,  0, -1),
        		new Vector3D( 0,  0, +1),
        		new Vector3D( 0,  0,  0)
        };
            
        Map.Builder builder = new Map.Builder();
        for (Vector3D vertex : vertices) {
        	builder.addVertex(vertex);
        }
            
        builder.connect(vertices[0], vertices[1])
        	   .connect(vertices[1], vertices[2])
        	   .connect(vertices[2], vertices[3])
        	   .connect(vertices[3], vertices[0])
        	   .connect(vertices[0], vertices[4])
        	   .connect(vertices[3], vertices[4])
        	   .connect(vertices[4], vertices[5]);
            
       	Map map = builder.build();
            
        Path path = map.findShortestPath(vertices[0], vertices[5]);
        while (path.hasNextPoint()) {
        	System.out.println(path.getNextPoint());
        }
	}

}

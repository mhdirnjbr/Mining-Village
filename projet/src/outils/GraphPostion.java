package outils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraphPostion {
    private Map<Position, ArrayList<Position>> adjVertices = new HashMap<Position, ArrayList<Position>>();

    public void addVertex(Position position) {
        adjVertices.put(position, new ArrayList<>());
    }

    public void removeVertex(Position position) {
        Position p = new Position(position);
        adjVertices.values().stream().forEach(e -> e.remove(p));
        adjVertices.remove(new Position(position));
    }

    public void addEdge(Position p1, Position p2) {
        boolean p1DansP2 = false;
        boolean p2DansP1 = false;
        if(adjVertices.get(p1) != null) for(Position t : adjVertices.get(p1)) if(!p2.different(t)) p2DansP1 = true;
        if(adjVertices.get(p2) != null) for(Position t : adjVertices.get(p2)) if(!p1.different(t)) p1DansP2 = true;

        if(!p2DansP1 && this.getAdjVertices(p1) != null) {
            this.getAdjVertices(p1).add(p2);
        }
        if(!p1DansP2 && this.getAdjVertices(p2) != null) {
            this.getAdjVertices(p2).add(p1);
        }
    }

    public void removeEdge(Position p1, Position p2) {
        List<Position> eV1 = this.getAdjVertices(p1);
        List<Position> eV2 = this.getAdjVertices(p2);
        if (eV1 != null)
            eV1.remove(p2);
        if (eV2 != null)
            eV2.remove(p1);
    }

    public ArrayList<Position> getAdjVertices(Position position) {
        return adjVertices.get(position);
    }

    public Map<Position, ArrayList<Position>> getAdjVertices() {
        return adjVertices;
    }

    @Override
    public String toString() {
        return "GraphPostion{" +
                "adjVertices=" + adjVertices.values() +
                '}';
    }
}

package com.sscfacilitylocation.algorithms.graph;

public class Edge<Node, Float> {

    private final Node toNode;
    private final Float cost;

    public Edge(Node toNode, Float cost) {
        this.toNode = toNode;
        this.cost = cost;
    }

    public Node getToNode() {
        return toNode;
    }

    public Float getCost() {
        return cost;
    }

    @Override
    public int hashCode() { return toNode.hashCode() ^ cost.hashCode(); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Edge)) return false;
        Edge edge = (Edge) o;
        return this.toNode.equals(edge.getToNode()) &&
                this.cost.equals(edge.getCost());
    }
}

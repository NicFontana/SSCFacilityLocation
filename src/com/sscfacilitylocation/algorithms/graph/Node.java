package com.sscfacilitylocation.algorithms.graph;

import com.sscfacilitylocation.entity.Customer;
import com.sscfacilitylocation.entity.Facility;

import java.util.Objects;

public class Node {

    private String name;
    private Facility facility;
    private Customer leavingCustomer;
    private float distanceFromSource;
    private Node predecessor;
    private NodeType type;

    Node(Facility facility, NodeType type) {
        switch (type) {
            case REGULAR: {
                this.facility = facility;
                distanceFromSource = Float.MAX_VALUE;
                leavingCustomer = facility.getWorstCustomer();
                name = String.valueOf(facility.getId());
                break;
            }
            case DUMMY: {
                this.facility = facility;
                distanceFromSource = Float.MAX_VALUE;
                leavingCustomer = facility.getWorstCustomer();
                name = 'd' + String.valueOf(facility.getId());
                break;
            }
            case SOURCE: {
                this.facility = null;
                distanceFromSource = 0.0F;
                leavingCustomer = null;
                name = "s";
                break;
            }
        }
        predecessor = null;
        this.type = type;
    }

    Node() {
        type = NodeType.SOURCE;
        predecessor = null;
        this.facility = null;
        distanceFromSource = 0.0F;
        leavingCustomer = null;
        name = "s";
    }

    String getName() {
        return name;
    }

    Facility getFacility() {
        return facility;
    }

    Customer getLeavingCustomer() {
        return leavingCustomer;
    }

    float getDistanceFromSource() {
        return distanceFromSource;
    }

    Node getPredecessor() {
        return predecessor;
    }

    NodeType getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Node)) return false;
        Node node = (Node) o;
        return name.equals(node.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}

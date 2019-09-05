package com.sscfacilitylocation.algorithms.graph;

import com.sscfacilitylocation.common.Solution;
import com.sscfacilitylocation.entity.Customer;
import com.sscfacilitylocation.entity.Facility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ImprovementGraph {

    private Map<Node, List<Pair<Node, Float>>> nodes;

    private Node sourceNode;
    private List<Node> regularNodes;
    private List<Node> dummyNodes;

    public ImprovementGraph(Solution solution) {
        nodes = new HashMap<>();

        addSourceNode();

        for (Facility f : solution.getOpenedFacilities().values()) {
            addNode(f, NodeType.REGULAR);
            addNode(f, NodeType.DUMMY);
        }
        for (Facility f : solution.getClosedFacilities().values()) {
            addNode(f, NodeType.DUMMY);
        }

        sourceNode = nodes.keySet().stream()
                .filter(node -> node.getType() == NodeType.SOURCE).findFirst().get();
        regularNodes = nodes.keySet().stream()
                .filter(node -> node.getType() == NodeType.REGULAR).collect(Collectors.toList());
        dummyNodes = nodes.keySet().stream()
                .filter(node -> node.getType() == NodeType.DUMMY).collect(Collectors.toList());

        linkSourceToRegularNodes();
        linkDummyNodesToSource();
        linkRegularNodesToDummyNodes();
        linkRegularNodesToRegularNodes();

    }

    private void addSourceNode() {
        nodes.putIfAbsent(new Node(), new ArrayList<>());
    }

    private void addNode(Facility facility, NodeType type) {
        nodes.putIfAbsent(new Node(facility, type), new ArrayList<>());
    }

    private void addEdge(Node n1, Node n2, float value) {
        Pair<Node, Float> edgePair = new Pair<>(n2, value);
        nodes.get(n1).add(edgePair);
    }

    private void linkSourceToRegularNodes() {
        for (Node regularNode : regularNodes) {
            Facility facility = regularNode.getFacility();
            if (facility.getServedCustomers().size() == 1) {
                addEdge(sourceNode, regularNode, -facility.getFixedCost());
            } else {
                addEdge(sourceNode, regularNode, 0.0F);
            }
        }
    }

    private void linkDummyNodesToSource() {
        for (Node dummyNode : dummyNodes) {
            Facility facility = dummyNode.getFacility();
            if (facility.getServedCustomers().isEmpty()) {
                addEdge(dummyNode, sourceNode, facility.getFixedCost());
            } else {
                addEdge(dummyNode, sourceNode, 0.0F);
            }
        }
    }

    private void linkRegularNodesToDummyNodes() {
        for (Node regularNode : regularNodes) {
            for (Node dummyNode : dummyNodes) {
                if (regularNode.getFacility() != dummyNode.getFacility()) {
                    if (isFeasibleRegularToDummyEdge(regularNode, dummyNode)) {
                        Customer leavingCustomer = regularNode.getLeavingCustomer();
                        float leavingCost = regularNode.getFacility().getCustomerCosts().get(leavingCustomer.getId());
                        float enteringCost = dummyNode.getFacility().getCustomerCosts().get(leavingCustomer.getId());
                        addEdge(regularNode, dummyNode, enteringCost - leavingCost);
                    }
                }
            }
        }
    }

    private void linkRegularNodesToRegularNodes() {
        for (Node n1 : regularNodes) {
            for (Node n2 : regularNodes) {
                if (n1.getFacility() != n2.getFacility()) {
                    if (isFeasibleRegularToRegularEdge(n1, n2)) {
                        Customer leavingCustomer = n1.getLeavingCustomer();
                        float leavingCost = n1.getFacility().getCustomerCosts().get(leavingCustomer.getId());
                        float enteringCost = n2.getFacility().getCustomerCosts().get(leavingCustomer.getId());
                        addEdge(n1, n2, enteringCost - leavingCost);
                    }
                }
            }
        }
    }

    private boolean isFeasibleRegularToDummyEdge(Node n1, Node n2) {
        return n1.getLeavingCustomer().getDemand() <= n2.getFacility().getResidualCapacity();
    }
    private boolean isFeasibleRegularToRegularEdge(Node n1, Node n2) {
        return n1.getLeavingCustomer().getDemand() <= n2.getFacility().getResidualCapacity() + n2.getLeavingCustomer().getDemand();
    }

    @Override
    public String toString() {
        StringBuilder sb =  new StringBuilder();
        sb.append("Improvement Graph: \n");

        nodes.forEach((node, pairList) -> {
            pairList.forEach(nodeCostPair -> {
                sb.append(node.getName()).append(" -> ");
                sb.append(nodeCostPair.getLeft().getName()).
                        append(" [label = ").append(nodeCostPair.getRight()).append("]").append(";\n");
            });
            sb.append("\n");
        });

        return sb.toString();
    }
}

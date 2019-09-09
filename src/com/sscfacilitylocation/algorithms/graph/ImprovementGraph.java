package com.sscfacilitylocation.algorithms.graph;

import com.sscfacilitylocation.common.Solution;
import com.sscfacilitylocation.entity.Customer;
import com.sscfacilitylocation.entity.Facility;
import com.sscfacilitylocation.utility.Console;

import java.util.*;
import java.util.stream.Collectors;

public class ImprovementGraph {

    private Map<Node, Map<Node, Float>> nodes;

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

        regularNodes = nodes.keySet().stream()
                .filter(node -> node.getType() == NodeType.REGULAR).collect(Collectors.toList());
        dummyNodes = nodes.keySet().stream()
                .filter(node -> node.getType() == NodeType.DUMMY).collect(Collectors.toList());

        linkSourceToRegularNodes();
        linkDummyNodesToSource();
        linkRegularNodesToDummyNodes();
        linkRegularNodesToRegularNodes();

    }

    public void addSourceNode() {
        sourceNode = new Node();
        nodes.putIfAbsent(sourceNode, new HashMap<>());
    }

    private void addNode(Facility facility, NodeType type) {
        nodes.putIfAbsent(new Node(facility, type), new HashMap<>());
    }

    public void addEdge(Node n1, Node n2, float value) {
        nodes.get(n1).putIfAbsent(n2, value);
    }

    private Set<Map.Entry<Node, Float>> getForwardStar(Node i) {
        Console.println("Forward Star of " + i +": " + nodes.get(i).keySet());
        return nodes.get(i).entrySet();
    }

    private float getEdgeCostBetween(Node n1, Node n2) {
        return nodes.get(n1).get(n2);
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
        // Regular X -> Dummy DY means that a customer goes from X to Y and Y doesn't lose any customer.
        return n1.getLeavingCustomer().getDemand() <= n2.getFacility().getResidualCapacity();
    }

    private boolean isFeasibleRegularToRegularEdge(Node n1, Node n2) {
        // Regular X -> Regular Y means that a customer goes from X to Y and Y lose his customer.
        return n1.getLeavingCustomer().getDemand() <= n2.getFacility().getResidualCapacity() + n2.getLeavingCustomer().getDemand();
    }

    public List<Node> getBestExchangeCycle() {
        // Forward Search Implementation

        List<Node> cycle = null;
        List<Node> bestCycle = null;
        float maxSaving = Float.MIN_VALUE;
        boolean cycleFound = false;

        Queue<Node> Q = new LinkedList<>();
        Q.add(sourceNode);

        while (!Q.isEmpty()) {
            Node i = Q.poll();

            if (!isSubsetDisjointPathUntil(i)) {
                Console.println("NO");
                Console.println("Avoiding the extraction of node: " + i);
                continue;
            }
            Console.println("YES");
            Console.println("\nExtracting node: " + i);
            for (Map.Entry<Node, Float> edge : getForwardStar(i)) {
                Node h = edge.getKey();
                float edgeCost = edge.getValue();

                if (willBeSubsetDisjointPath(i, h)) { // source -> i -> h is a subset disjoint path
                    Console.println("YES");
                    // Check Bellman's condition
                    if (i.getDistanceFromSource() + edgeCost < h.getDistanceFromSource()) {
                        switch (h.getType()) {
                            case REGULAR: { // Better "distance" found for regular node h
                                Console.println("Better distance found for node " + h + " updating it.");
                                h.setPredecessor(i);
                                h.setDistanceFromSource(i.getDistanceFromSource() + edgeCost);
                                Q.add(h);
                                break;
                            }
                            case DUMMY: { // Node h is a dummmy node: path exchange found
                                h.setPredecessor(i);
                                sourceNode.setPredecessor(h);

                                cycleFound = true;
                                cycle = buildCycle(h);
                                Console.println("\t Path exchange found! " + cycle + " saving = " + -getCycleCost(cycle));
                                break;
                            }
                        }
                    }
                } else { // source -> i -> h is not a subset disjoint path
                    Console.println("NO");
                    switch (h.getType()) {
                        case REGULAR: { // Node h is a regular node: cycle exchange found
                            // Check bellman's condition to see if it's an improving cycle
                            if (i.getDistanceFromSource() + edgeCost < h.getDistanceFromSource()) {
                                Console.println("Better distance found for node " + h + " updating it.");
                                h.setPredecessor(i);
                                h.setDistanceFromSource(i.getDistanceFromSource() + edgeCost);
                                Q.add(h);

                                cycleFound = true;
                                cycle = buildCycle(h);
                                Console.println("\t Profitable Cycle exchange found! " + cycle + " saving = " + -getCycleCost(cycle));
                            } else {
                                cycleFound = true;
                                cycle = buildCycle(h);
                                Console.println("\t Not profitable Cycle exchange found! " + cycle + " saving = " + -getCycleCost(cycle));
                            }
                            break;
                        }
                        case DUMMY: {
                            // Skip
                            break;
                        }
                    }
                }

                if (cycleFound) {
                    float saving = -getCycleCost(cycle);
                    if (saving > maxSaving) {
                        maxSaving = saving;
                        bestCycle = cycle;
                    }
                    cycle = null;
                    cycleFound = false;
                    sourceNode.setPredecessor(null);
                }
            }
        }

        Console.println("\nQueue is empty. Best cycle found: " + bestCycle + " with saving of: " + maxSaving);

        return bestCycle;
    }

    private boolean isSubsetDisjointPathUntil(Node lastNode) {
        Set<Facility> visitedFacilities = new HashSet<>();
        visitedFacilities.add(lastNode.getFacility());

        System.out.print("Is a subset disjoint cycle? " + lastNode + " <- ");
        Node next = lastNode.getPredecessor();
        while (next != null) {
            System.out.print(next + " <- ");
            Facility f = next.getFacility();
            if (visitedFacilities.contains(f)) return false;
            else visitedFacilities.add(f);
            next = next.getPredecessor();
        }

        return true;
    }

    private boolean willBeSubsetDisjointPath(Node lastNode, Node newNode) {
        Set<Facility> visitedFacilities = new HashSet<>();
        visitedFacilities.add(lastNode.getFacility());

        System.out.print("Will be a subset disjoint cycle? " + newNode + " <- " + lastNode + " <- ");
        Node next = lastNode.getPredecessor();
        while (next != null) {
            System.out.print(next + " <- ");
            visitedFacilities.add(next.getFacility());
            next = next.getPredecessor();
        }

        return !visitedFacilities.contains(newNode.getFacility());
    }

    private List<Node> buildCycle(Node lastNode) {
        List<Node> path = new ArrayList<>();

        path.add(lastNode);
        Node next = lastNode.getPredecessor();
        while (next != lastNode) {
            path.add(next);
            next = next.getPredecessor();
        }

        return path;
    }

    private float getCycleCost(List<Node> cycle) {
        // Calculate the path cost
        float cost = 0;
        for (Node currentNode : cycle) {
            cost += getEdgeCostBetween(currentNode.getPredecessor(), currentNode);
        }

        return cost;
    }

    @Override
    public String toString() {
        StringBuilder sb =  new StringBuilder();
        sb.append("Improvement Graph: \n");

        nodes.forEach((node, pairMap) -> {
            pairMap.forEach((toNode, cost) -> {
                sb.append(node).append(" -> ");
                sb.append(toNode).
                        append(" [label = ").append(cost).append("]").append(";\n");
            });
            sb.append("\n");
        });

        return sb.toString();
    }
}

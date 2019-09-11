package com.sscfacilitylocation.common;

import com.sscfacilitylocation.algorithms.greedy.GreedyStrategy;
import com.sscfacilitylocation.algorithms.localsearch.LocalSearchStrategy;
import com.sscfacilitylocation.algorithms.localsearch.SolutionImprovement;
import com.sscfacilitylocation.algorithms.metaheuristic.CustomerTabuSearch;
import com.sscfacilitylocation.entity.Customer;
import com.sscfacilitylocation.entity.Facility;
import com.sscfacilitylocation.utility.Console;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Problem {

    private int numOfFacilities;
    private int numOfCustomers;
    private float[] facilityCapacities;
    private float[] facilityFixedCosts;
    private float[] customerDemands;
    private float[][] facilityToCustomerCosts;
    private GreedyStrategy greedyStrategy;
    private LocalSearchStrategy localSearchStrategy;
    private Solution solution;

    public Problem(String instancePath, GreedyStrategy greedyStrategy, LocalSearchStrategy localSearchStrategy) {
        this.greedyStrategy = greedyStrategy;
        this.localSearchStrategy = localSearchStrategy;

        try {
            BufferedReader inFile = new BufferedReader(new FileReader(instancePath));
            String line = inFile.readLine();
            StringTokenizer tokenizer = new StringTokenizer(line);

            // Load the number of possible facilities and the number of customer
            numOfFacilities = Integer.parseInt(tokenizer.nextToken());
            numOfCustomers = Integer.parseInt(tokenizer.nextToken());

            // Load facility capacities and fixed costs
            facilityCapacities = new float[numOfFacilities];
            facilityFixedCosts = new float[numOfFacilities];
            for (int j = 0; j < numOfFacilities; j++) {
                line = inFile.readLine();
                tokenizer = new StringTokenizer(line);
                facilityCapacities[j] = Float.parseFloat(tokenizer.nextToken());
                facilityFixedCosts[j] = Float.parseFloat(tokenizer.nextToken());
            }

            // Load customer demands
            customerDemands = new float[numOfCustomers];
            line = inFile.readLine();
            tokenizer = new StringTokenizer(line);
            for (int i = 0; i < numOfCustomers; i++) {
                customerDemands[i] = Float.parseFloat(tokenizer.nextToken());
            }

            // Load the facility to customer costs
            facilityToCustomerCosts = new float[numOfFacilities][numOfCustomers];
            for (int j = 0; j < numOfFacilities; j++) {
                line = inFile.readLine();
                tokenizer = new StringTokenizer(line);
                for (int i = 0; i < numOfCustomers; i++) {
                    facilityToCustomerCosts[j][i] = Float.parseFloat(tokenizer.nextToken());
                }
            }
            inFile.close();
        } catch (IOException e) {
            Console.println("Cannot read the instance file: " + e.getMessage());
        }
    }

    public int getNumOfFacilities() {
        return numOfFacilities;
    }

    public int getNumOfCustomers() {
        return numOfCustomers;
    }

    public float[] getFacilityCapacities() {
        return facilityCapacities;
    }

    public float[] getFacilityFixedCosts() {
        return facilityFixedCosts;
    }

    public float[][] getFacilityToCustomerCosts() {
        return facilityToCustomerCosts;
    }

    public Solution getSolution() {
        return solution;
    }

    public void solveWithGreedy() {
        solution = new Solution(this);

        HashSet<Customer> toBeAssignedCustomers = new HashSet<>();
        for (int i=0; i < numOfCustomers; i++) {
            toBeAssignedCustomers.add(new Customer(i, customerDemands[i]));
        }
        PriorityQueue<Facility> facilitiesQueue = (PriorityQueue<Facility>) greedyStrategy.getSortedFacilities(solution.getClosedFacilities().values());

        while (!facilitiesQueue.isEmpty() && !toBeAssignedCustomers.isEmpty()) {
            Facility bestFacility = greedyStrategy.bestFacility(facilitiesQueue);
            if (!greedyStrategy.facilityInd(bestFacility, toBeAssignedCustomers)) {
                continue;
            }
            Console.println("Opening facility " + bestFacility.getId() + " with service cost for all customers: " + bestFacility.getWholeServingCost());
            solution.openFacility(bestFacility);

            PriorityQueue<Customer> customersQueue = (PriorityQueue<Customer>) greedyStrategy.getSortedCustomers(bestFacility, toBeAssignedCustomers);
            while (!customersQueue.isEmpty()) {
                Customer bestCustomer = greedyStrategy.bestCustomer(customersQueue);

                if (greedyStrategy.customerInd(bestFacility, bestCustomer)) {
                    solution.addCustomerToFacility(bestCustomer, bestFacility);
                    Console.println("Assigning customer " + bestCustomer.getId() + " to facility " + bestFacility.getId() + " - capacity left: " + bestFacility.getResidualCapacity());
                    toBeAssignedCustomers.remove(bestCustomer);
                }
            }

            Console.println("No more customers assignable to facility " + bestFacility.getId() + "\n");
        }

        if (!toBeAssignedCustomers.isEmpty()) {
            solution = null;
        }
    }

    public void performLocalSearch() throws RuntimeException {

        if (solution == null) {
            throw new RuntimeException("Cannot perform a Local Search without an initial solution");
        }

        int k = 1;
        Console.println("\nITERATION " + k);
        SolutionImprovement solutionImprovement = localSearchStrategy.getSolutionImprovement(solution);

        while (solutionImprovement != null) {
            solution.applyImprovement(solutionImprovement);
            Console.println("");
            Console.println(solution);
            k += 1;
            Console.println("\nITERATION " + k);
            solutionImprovement = localSearchStrategy.getSolutionImprovement(solution);
        }

        Console.println("\nNo more improvement feasible.");

    }

    public void performTabuSearch(CustomerTabuSearch tabuSearch) throws RuntimeException {
        if (solution == null) {
            throw new RuntimeException("Cannot perform a Local Search without an initial solution");
        }

        solution = tabuSearch.run();

        Console.println("Best solution is \n" + solution);
    }

    @Override
    public String toString() {
        StringBuilder sb =  new StringBuilder();
        sb.append("Facilities: ").append(numOfFacilities)
                .append(" - Customers: ").append(numOfCustomers);

        sb.append("\n").append("Facility Capacities: ");
        for (int j = 0; j < numOfFacilities; j++) {
            sb.append(facilityCapacities[j]).append(" ");
        }

        sb.append("\n").append("Facility Fixed Costs: ");
        for (int j = 0; j < numOfFacilities; j++) {
            sb.append(facilityFixedCosts[j]).append(" ");
        }

        sb.append("\n").append("Customer Demands: ");
        for (int i = 0; i < numOfCustomers; i++) {
            sb.append(customerDemands[i]).append(" ");
        }
        sb.append("\n").append("Facility to Customer Costs: ").append("\n");
        for (int j = 0; j < numOfFacilities; j++) {
            sb.append("\n").append("Facility ").append(j).append(":").append("\n");
            for (int i = 0; i < numOfCustomers; i++) {
                sb.append(i).append(") ").append(facilityToCustomerCosts[j][i]).append("\n");
            }
        }
        return sb.toString();
    }

}

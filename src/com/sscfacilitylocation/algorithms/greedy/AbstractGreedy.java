package com.sscfacilitylocation.algorithms.greedy;

import com.sscfacilitylocation.common.Problem;
import com.sscfacilitylocation.common.Solution;
import com.sscfacilitylocation.entity.Customer;
import com.sscfacilitylocation.entity.Facility;
import com.sscfacilitylocation.utility.Console;

import java.util.Collection;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Queue;

public abstract class AbstractGreedy {

    private Problem problem;

    public AbstractGreedy(Problem problem) {
        this.problem = problem;
    }

    abstract Queue<Facility> getSortedFacilities(Collection<Facility> possibleFacilities);
    abstract Queue<Customer> getSortedCustomers(Facility facility, Collection<Customer> possibleCustomers);
    abstract Facility bestFacility(Queue<Facility> possibleFacilities);
    abstract boolean facilityInd(Facility newFacility, Collection<Customer> toBeAssignedCustomers);
    abstract Customer bestCustomer(Queue<Customer> possibleCustomers);
    abstract boolean customerInd(Facility currentFacility, Customer newCustomer);

    public Solution run() {
        Solution solution = new Solution(problem);

        HashSet<Customer> toBeAssignedCustomers = new HashSet<>();
        for (int i=0; i < problem.getNumOfCustomers(); i++) {
            toBeAssignedCustomers.add(new Customer(i, problem.getCustomerDemands()[i]));
        }
        PriorityQueue<Facility> facilitiesQueue = (PriorityQueue<Facility>) getSortedFacilities(solution.getClosedFacilities().values());

        while (!facilitiesQueue.isEmpty() && !toBeAssignedCustomers.isEmpty()) {
            Facility bestFacility = bestFacility(facilitiesQueue);
            if (!facilityInd(bestFacility, toBeAssignedCustomers)) {
                continue;
            }
            Console.println("Opening facility " + bestFacility.getId() + " with service cost for all customers: " + bestFacility.getWholeServingCost());
            solution.openFacility(bestFacility);

            PriorityQueue<Customer> customersQueue = (PriorityQueue<Customer>) getSortedCustomers(bestFacility, toBeAssignedCustomers);
            while (!customersQueue.isEmpty()) {
                Customer bestCustomer = bestCustomer(customersQueue);

                if (customerInd(bestFacility, bestCustomer)) {
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

        return solution;
    }
}
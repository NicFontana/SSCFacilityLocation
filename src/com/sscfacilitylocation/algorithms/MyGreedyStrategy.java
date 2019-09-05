package com.sscfacilitylocation.algorithms;

import com.sscfacilitylocation.entity.Customer;
import com.sscfacilitylocation.entity.Facility;

import java.util.Collection;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Queue;

public class MyGreedyStrategy implements GreedyStrategy {

    @Override
    public Queue<Facility> getSortedFacilities(Collection<Facility> possibleFacilities) {
        PriorityQueue<Facility> facilities = new PriorityQueue<>(
                possibleFacilities.size(),
                (a, b) -> Float.compare(a.getWholeServingCost(), b.getWholeServingCost())
        );

        facilities.addAll(possibleFacilities);

        return facilities;
    }

    @Override
    public Queue<Customer> getSortedCustomers(Facility facility, Collection<Customer> possibleCustomers) {
        HashMap<Integer, Float> customerCosts = facility.getCustomerCosts();

        PriorityQueue<Customer> customers = new PriorityQueue<>(
                possibleCustomers.size(),
                (a, b) -> Float.compare(customerCosts.get(a.getId()), customerCosts.get(b.getId()))
        );

        customers.addAll(possibleCustomers);

        return customers;
    }

    @Override
    public Facility bestFacility(Queue<Facility> possibleFacilities) {
        return possibleFacilities.poll();
    }

    @Override
    public boolean facilityInd(Facility newFacility, Collection<Customer> toBeAssignedCustomers) {
        float minimumDemand = Float.MAX_VALUE;

        for (Customer customer : toBeAssignedCustomers) {
            float demand = customer.getDemand();
            if (demand < minimumDemand) minimumDemand = demand;
        }

        return newFacility.getCapacity() >= minimumDemand;
    }

    @Override
    public Customer bestCustomer(Queue<Customer> possibleCustomers) {
        return possibleCustomers.poll();
    }

    @Override
    public boolean customerInd(Facility currentFacility, Customer newCustomer) {
        return currentFacility.getResidualCapacity() >= newCustomer.getDemand();
    }
}

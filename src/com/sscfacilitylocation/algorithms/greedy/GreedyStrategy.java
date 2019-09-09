package com.sscfacilitylocation.algorithms.greedy;

import com.sscfacilitylocation.entity.Customer;
import com.sscfacilitylocation.entity.Facility;

import java.util.Collection;
import java.util.Queue;

public interface GreedyStrategy {

    Queue<Facility> getSortedFacilities(Collection<Facility> possibleFacilities);
    Queue<Customer> getSortedCustomers(Facility facility, Collection<Customer> possibleCustomers);
    Facility bestFacility(Queue<Facility> possibleFacilities);
    boolean facilityInd(Facility newFacility, Collection<Customer> toBeAssignedCustomers);
    Customer bestCustomer(Queue<Customer> possibleCustomers);
    boolean customerInd(Facility currentFacility, Customer newCustomer);
}

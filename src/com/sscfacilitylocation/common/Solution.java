package com.sscfacilitylocation.common;

import com.sscfacilitylocation.algorithms.localsearch.CustomersTransfer;
import com.sscfacilitylocation.algorithms.localsearch.SolutionImprovement;
import com.sscfacilitylocation.entity.Customer;
import com.sscfacilitylocation.entity.Facility;
import com.sscfacilitylocation.utility.Console;

import java.util.*;

public class Solution {

    private HashMap<Integer, Facility> openedFacilities;
    private HashMap<Integer, Facility> closedFacilities;

    public Solution(Problem problem) {
        openedFacilities = new HashMap<>();
        closedFacilities = new HashMap<>(problem.getNumOfFacilities());

        for (int j = 0; j < problem.getNumOfFacilities(); j++) {
            Facility f = new Facility(j, problem.getFacilityFixedCosts()[j], problem.getFacilityCapacities()[j]);

            HashMap<Integer, Float> customerCosts = new HashMap<>();
            for (int i = 0; i < problem.getNumOfCustomers(); i++) {
                customerCosts.put(i, problem.getFacilityToCustomerCosts()[j][i]);
            }
            f.setCustomerCosts(customerCosts);
            closedFacilities.put(j, f);
        }
    }

    public HashMap<Integer, Facility> getClosedFacilities() {
        return closedFacilities;
    }

    public HashMap<Integer, Facility> getOpenedFacilities() {
        return openedFacilities;
    }

    public void openFacility(Facility facility) {
        closedFacilities.remove(facility.getId());
        openedFacilities.put(facility.getId(), facility);
    }

    public void closeFacility(Facility facility) {
        openedFacilities.remove(facility.getId());
        closedFacilities.put(facility.getId(), facility);
    }

    public void addCustomerToFacility(Customer customer, Facility facility) {
        if (openedFacilities.containsKey(facility.getId())) {
            facility.addCustomer(customer);
        } else {
            openFacility(facility);
            facility.addCustomer(customer);
        }
    }

    public void removeCustomerFromFacility(Customer customer, Facility facility) {
        openedFacilities.get(facility.getId()).removeCustomer(customer);
        if (facility.getServedCustomers().isEmpty()) {
            closeFacility(facility);
        }
    }

    public void addCustomersToFacility(Set<Customer> customers, Facility facility) {
        if (openedFacilities.containsKey(facility.getId())) {
            facility.addCustomers(customers);
        } else {
            openFacility(facility);
            facility.addCustomers(customers);
        }
    }

    public void removeCustomersFromFacility(Set<Customer> customers, Facility facility) {
        openedFacilities.get(facility.getId()).removeCustomers(customers);
        if (facility.getServedCustomers().isEmpty()) {
            closeFacility(facility);
        }
    }

    public float getCost() {
        float cost = 0;

        for (Facility facility : openedFacilities.values()) {
            cost += facility.getFixedCost();

            for (Customer servedCustomer : facility.getServedCustomers()) {
                cost += facility.getCustomerCosts().get(servedCustomer.getId());
            }
        }
        return cost;
    }

    public void applyImprovement(SolutionImprovement solutionImprovement) {
        for (CustomersTransfer transfer : solutionImprovement.getCustomersTransferList()) {
            Console.println("\t- Moving customers " + transfer.getMovingCustomers() + " from facility " + transfer.getFromFacility() + " to facility " + transfer.getToFacility());
            addCustomersToFacility(transfer.getMovingCustomers(), transfer.getToFacility());
            removeCustomersFromFacility(transfer.getMovingCustomers(), transfer.getFromFacility());
        }
    }

    @Override
    public String toString() {
        StringBuilder sb =  new StringBuilder();
        sb.append("Opened Facilities: \n");

        openedFacilities.forEach((id, facility) -> {
            sb.append(id)
                    .append(" (Capacity: ").append(facility.getCapacity())
                    .append(" - Fixed Cost: ").append(facility.getFixedCost())
                    .append(") serving customers: ");
            facility.getServedCustomers().forEach(customer -> {
                sb.append(customer.getId())
                        .append(" (Demand: ").append(customer.getDemand())
                        .append(" - Cost: ").append(facility.getCustomerCosts().get(customer.getId()))
                        .append("), ");
            });
            sb.append("\n");
        });

        sb.append("TOTAL COST = ").append(getCost());
        return sb.toString();
    }
}

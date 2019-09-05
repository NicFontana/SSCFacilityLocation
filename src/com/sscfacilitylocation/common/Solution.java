package com.sscfacilitylocation.common;

import com.sscfacilitylocation.entity.Customer;
import com.sscfacilitylocation.entity.Facility;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

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

    public void performCustomersTransfer(CustomersTransfer customersTransfer) {

        Facility fromFacility = customersTransfer.getFromFacility();
        Facility toFacility = customersTransfer.getToFacility();
        HashSet<Customer> movingCustomers = customersTransfer.getMovingCustomers();

        if (fromFacility != null) {
            // Just transfer customers between opened facilities
            openedFacilities.get(toFacility.getId())
                    .addCustomers(movingCustomers);
            openedFacilities.get(fromFacility.getId())
                    .removeCustomers(movingCustomers);

            if (fromFacility.getServedCustomers().isEmpty()) {
                openedFacilities.remove(fromFacility.getId());
                closedFacilities.put(fromFacility.getId(), fromFacility);
            }
        } else {
            System.out.println("Aggiungo clienti " + Arrays.toString(movingCustomers.toArray()) + " alla facility " + toFacility.getId());
            if (toFacility.getServedCustomers().isEmpty()) {
                openedFacilities.put(toFacility.getId(), toFacility);
                closedFacilities.remove(toFacility.getId());
            }
            openedFacilities.get(toFacility.getId())
                    .addCustomers(movingCustomers);
        }
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
        openedFacilities.get(facility.getId()).addCustomer(customer);
    }

    public void removeCustomerFromFacility(Customer customer, Facility facility) {
        openedFacilities.get(facility.getId()).removeCustomer(customer);
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

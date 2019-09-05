package com.sscfacilitylocation.common;

import com.sscfacilitylocation.entity.Customer;
import com.sscfacilitylocation.entity.Facility;

import java.util.Collection;
import java.util.HashSet;

public class CustomersTransfer {

    private HashSet<Customer> movingCustomers;
    private Facility fromFacility;
    private Facility toFacility;

    public CustomersTransfer(Collection<Customer> movingCustomers, Facility fromFacility, Facility toFacility) {
        this.movingCustomers = (HashSet<Customer>) movingCustomers;
        this.fromFacility = fromFacility;
        this.toFacility = toFacility;
    }

    public HashSet<Customer> getMovingCustomers() {
        return movingCustomers;
    }

    public Facility getFromFacility() {
        return fromFacility;
    }

    public Facility getToFacility() {
        return toFacility;
    }

    public float getSaving() {
        if (fromFacility != null) {
            float saving = fromFacility.getFixedCost() - toFacility.getFixedCost();

            for (Customer customer : movingCustomers) {
                int customerId = customer.getId();
                saving += fromFacility.getCustomerCosts().get(customerId) - toFacility.getCustomerCosts().get(customerId);
            }
            return saving;
        }
        return 0;
    }

    public boolean isBetterThan(CustomersTransfer comparedSolution) {
        return getSaving() > comparedSolution.getSaving();
    }
}

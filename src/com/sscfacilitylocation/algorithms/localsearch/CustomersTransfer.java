package com.sscfacilitylocation.algorithms.localsearch;

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

    public CustomersTransfer(Customer movingCustomer, Facility fromFacility, Facility toFacility) {
        movingCustomers = new HashSet<>();
        movingCustomers.add(movingCustomer);
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

}

package com.sscfacilitylocation.entity;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public class Facility implements Cloneable {

    private int id;
    private float fixedCost;
    private HashMap<Integer, Float> customerCosts;
    private HashSet<Customer> servedCustomers;
    private float capacity;
    private float residualCapacity;

    public Facility(int id, float fixedCost, float capacity) {
        this.id = id;
        this.fixedCost = fixedCost;
        this.capacity = capacity;
        this.residualCapacity = capacity;
        customerCosts = new HashMap<>();
        servedCustomers = new HashSet<>();
    }

    public int getId() {
        return id;
    }

    public float getFixedCost() {
        return fixedCost;
    }

    public HashSet<Customer> getServedCustomers() {
        return servedCustomers;
    }

    public HashMap<Integer, Float> getCustomerCosts() {
        return customerCosts;
    }

    public void setCustomerCosts(HashMap<Integer, Float> customerCosts) {
        this.customerCosts = customerCosts;
    }

    public float getCapacity() {
        return capacity;
    }

    public void addCustomers(Collection<Customer> newCustomers) {
        servedCustomers.addAll(newCustomers);
        newCustomers.forEach(customer -> residualCapacity -= customer.getDemand());
    }

    public void removeCustomers(Collection<Customer> oldCustomers) {
        servedCustomers.removeAll(oldCustomers);
        oldCustomers.forEach(customer -> residualCapacity += customer.getDemand());
    }

    public void addCustomer(Customer newCustomer) {
        servedCustomers.add(newCustomer);
        residualCapacity -= newCustomer.getDemand();
    }

    public void removeCustomer(Customer oldCustomer) {
        servedCustomers.remove(oldCustomer);
        residualCapacity += oldCustomer.getDemand();
    }

    public float getResidualCapacity() {
        return residualCapacity;
    }

    public float getWholeServingCost() {
        return fixedCost + customerCosts.values().stream().reduce(0.0F, Float::sum);
    }

    public Customer getWorstCustomer() {
        Customer worstCustomer = null;
        float worstCost = -1;

        for (Customer c : servedCustomers) {
            float customerCost = customerCosts.get(c.getId());

            if (customerCost > worstCost) {
                worstCost = customerCost;
                worstCustomer = c;
            }
            else if (customerCost == worstCost && c.getDemand() < worstCustomer.getDemand()) {
                worstCustomer = c;
            }
        }

        return worstCustomer;
    }

    public String toString() {
        return String.valueOf(id);
    }
}
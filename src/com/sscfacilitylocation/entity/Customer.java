package com.sscfacilitylocation.entity;


public class Customer {

    private int id;
    private float demand;

    public Customer (int id, float demand) {
        this.id = id;
        this.demand = demand;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getDemand() {
        return demand;
    }

    public void setDemand(float demand) {
        this.demand = demand;
    }

    public String toString() {
        return String.valueOf(id);
    }
}

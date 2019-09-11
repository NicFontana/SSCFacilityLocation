package com.sscfacilitylocation.algorithms.localsearch;

import com.sscfacilitylocation.algorithms.localsearch.improvementgraph.Node;
import com.sscfacilitylocation.algorithms.localsearch.improvementgraph.NodeType;
import com.sscfacilitylocation.entity.Customer;
import com.sscfacilitylocation.entity.Facility;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SolutionImprovement {

    private List<CustomersTransfer> customersTransferList;

    public SolutionImprovement(List<Node> improvementGraphCycle) {
        if (improvementGraphCycle != null) {
            customersTransferList = new ArrayList<>();

            for (int i = 0; i < improvementGraphCycle.size() - 1; i++) {
                int h = i + 1;
                Node losingFacilityNode = improvementGraphCycle.get(h);

                if (losingFacilityNode.getType() != NodeType.SOURCE) { // Source node doesn't lose any customer
                    Facility losingFacility = losingFacilityNode.getFacility();
                    Customer leavingCustomer = losingFacilityNode.getLeavingCustomer();

                    Facility incomeFacility = improvementGraphCycle.get(i).getFacility();

                    CustomersTransfer customersTransfer = new CustomersTransfer(leavingCustomer, losingFacility, incomeFacility);
                    customersTransferList.add(customersTransfer);
                }
            }
        }
    }

    public List<CustomersTransfer> getCustomersTransferList() {
        return customersTransferList;
    }

    public Set<Customer> getInvolvedCustomers() {
        Set<Customer> involvedCustomers = new HashSet<>();

        for (CustomersTransfer transfer : customersTransferList) {
            involvedCustomers.addAll(transfer.getMovingCustomers());
        }

        return involvedCustomers;
    }
}

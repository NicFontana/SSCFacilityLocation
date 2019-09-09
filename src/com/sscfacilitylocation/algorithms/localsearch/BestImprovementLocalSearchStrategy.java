package com.sscfacilitylocation.algorithms.localsearch;

import com.sscfacilitylocation.algorithms.localsearch.improvementgraph.ImprovementGraph;
import com.sscfacilitylocation.algorithms.localsearch.improvementgraph.Node;
import com.sscfacilitylocation.algorithms.localsearch.improvementgraph.NodeType;
import com.sscfacilitylocation.common.Solution;
import com.sscfacilitylocation.entity.Customer;
import com.sscfacilitylocation.entity.Facility;

import java.util.ArrayList;
import java.util.List;

public class BestImprovementLocalSearchStrategy implements LocalSearchStrategy {

    @Override
    public SolutionImprovement getSolutionImprovement(Solution currentSolution) {
        SolutionImprovement solutionImprovement = null;
        ImprovementGraph improvementGraph = new ImprovementGraph(currentSolution);
        List<Node> cycle = improvementGraph.getBestExchangeCycle();

        if (cycle != null) {
            List<CustomersTransfer> customersTransferList = new ArrayList<>();

            for (int i = 0; i < cycle.size() - 1; i++) {
                int h = i + 1;
                Node losingFacilityNode = cycle.get(h);

                if (losingFacilityNode.getType() != NodeType.SOURCE) { // Source node doesn't lose any customer
                    Facility losingFacility = losingFacilityNode.getFacility();
                    Customer leavingCustomer = losingFacilityNode.getLeavingCustomer();

                    Facility incomeFacility = cycle.get(i).getFacility();

                    CustomersTransfer customersTransfer = new CustomersTransfer(leavingCustomer, losingFacility, incomeFacility);
                    customersTransferList.add(customersTransfer);
                }
            }

            solutionImprovement = new SolutionImprovement(customersTransferList);
        }

        return solutionImprovement;
    }

}

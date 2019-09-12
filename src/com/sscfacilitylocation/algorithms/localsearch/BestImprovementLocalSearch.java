package com.sscfacilitylocation.algorithms.localsearch;

import com.sscfacilitylocation.algorithms.localsearch.improvementgraph.ImprovementGraph;
import com.sscfacilitylocation.algorithms.localsearch.improvementgraph.Node;
import com.sscfacilitylocation.algorithms.localsearch.improvementgraph.SolutionImprovement;
import com.sscfacilitylocation.common.Solution;
import java.util.List;

public class BestImprovementLocalSearch extends AbstractLocalSearch {

    public BestImprovementLocalSearch(Solution initialSolution) {
        super(initialSolution);
    }

    @Override
    public SolutionImprovement getSolutionImprovement(Solution currentSolution) {

        ImprovementGraph improvementGraph = new ImprovementGraph(currentSolution);
        List<Node> cycle = improvementGraph.getBestImprovingExchangeCycle();

        if (cycle != null) return new SolutionImprovement(cycle);

        return null;
    }

}

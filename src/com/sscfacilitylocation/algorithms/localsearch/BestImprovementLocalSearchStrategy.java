package com.sscfacilitylocation.algorithms.localsearch;

import com.sscfacilitylocation.algorithms.localsearch.improvementgraph.ImprovementGraph;
import com.sscfacilitylocation.algorithms.localsearch.improvementgraph.Node;
import com.sscfacilitylocation.common.Solution;
import java.util.List;

public class BestImprovementLocalSearchStrategy implements LocalSearchStrategy {

    @Override
    public SolutionImprovement getSolutionImprovement(Solution currentSolution) {

        ImprovementGraph improvementGraph = new ImprovementGraph(currentSolution);
        List<Node> cycle = improvementGraph.getBestImprovingExchangeCycle();

        return new SolutionImprovement(cycle);
    }

}

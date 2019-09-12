package com.sscfacilitylocation.algorithms.localsearch;

import com.sscfacilitylocation.algorithms.localsearch.improvementgraph.SolutionImprovement;
import com.sscfacilitylocation.common.Solution;
import com.sscfacilitylocation.utility.Console;

public abstract class AbstractLocalSearch {

    private Solution bestSolution;

    public AbstractLocalSearch(Solution initialSolution) {
        bestSolution = initialSolution;
    }

    public abstract SolutionImprovement getSolutionImprovement(Solution currentSolution);

    public Solution run() {
        int k = 1;
        Console.println("\nITERATION " + k);
        SolutionImprovement solutionImprovement = getSolutionImprovement(bestSolution);

        while (solutionImprovement != null) {
            bestSolution.applyImprovement(solutionImprovement);
            Console.println("");
            Console.println(bestSolution);
            k += 1;
            Console.println("\nITERATION " + k);
            solutionImprovement = getSolutionImprovement(bestSolution);
        }

        return bestSolution;
    }
}

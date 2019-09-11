package com.sscfacilitylocation.algorithms.metaheuristic;

import com.sscfacilitylocation.algorithms.localsearch.SolutionImprovement;
import com.sscfacilitylocation.algorithms.localsearch.improvementgraph.ImprovementGraph;
import com.sscfacilitylocation.algorithms.localsearch.improvementgraph.Node;
import com.sscfacilitylocation.common.Solution;
import com.sscfacilitylocation.entity.Customer;
import com.sscfacilitylocation.utility.Console;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class CustomerTabuSearch {

    private Solution bestSolution;
    private Queue<Customer> tabuList;
    private int tabuListLength;
    private int numOfIterationWithoutImprovment;
    private int stopWithoutImprovementAfter;
    private boolean aspiration;

    public CustomerTabuSearch(Solution initialSolution, int tabuListLength, int stopWithoutImprovementAfter) {
        bestSolution = initialSolution;
        tabuList = new LinkedList<>();
        this.tabuListLength = tabuListLength;
        this.stopWithoutImprovementAfter = stopWithoutImprovementAfter;
        numOfIterationWithoutImprovment = 0;
        aspiration = false;
    }

    public Solution run() {
        Solution tempSolution = (Solution) bestSolution.clone();
        Solution aspirationSolution;

        int k = 1;
        Console.println("\nITERATION " + k);
        Console.println("TabuList = " + Arrays.toString(tabuList.toArray()));
        Console.println("\nNow exploring neighborhood of");
        Console.println(tempSolution);
        SolutionImprovement solutionImprovement = getSolutionImprovement(tempSolution, tabuList, aspiration);

        while (numOfIterationWithoutImprovment < stopWithoutImprovementAfter) {
            if (aspiration) {
                aspirationSolution = (Solution) tempSolution.clone();
                SolutionImprovement solutionImprovementWithAspiration = getSolutionImprovement(aspirationSolution, tabuList, aspiration);
                updateSituation(aspirationSolution, solutionImprovementWithAspiration);
                if (aspirationSolution.getCost() == bestSolution.getCost()) { // Aspiration take me to a new best solution
                    Console.println("Best solution reached with aspiration.");
                    tempSolution = aspirationSolution;
                } else {
                    numOfIterationWithoutImprovment -= 1; // Decrease the counter increased by the unsuccessful aspiration
                    updateSituation(tempSolution, solutionImprovement);
                }
            } else {
                updateSituation(tempSolution, solutionImprovement);
            }

            k += 1;
            Console.println("\nITERATION " + k);
            Console.println("TabuList = " + Arrays.toString(tabuList.toArray()));
            Console.println("\nNow exploring neighborhood of");
            Console.println(tempSolution);
            solutionImprovement = getSolutionImprovement(tempSolution, tabuList, false);
        }

        Console.println("\nNo more improvement feasible. NIWI: " + numOfIterationWithoutImprovment);

        return bestSolution;
    }

    private SolutionImprovement getSolutionImprovement(Solution currentSolution, Queue<Customer> tabuList, boolean aspiration) {
        ImprovementGraph improvementGraph;

        if (aspiration) improvementGraph = new ImprovementGraph(currentSolution);
        else improvementGraph = new ImprovementGraph(currentSolution, tabuList);

        List<Node> cycle = improvementGraph.getBestExchangeCycle();

        return new SolutionImprovement(cycle);
    }

    private void updateSituation(Solution tempSolution, SolutionImprovement solutionImprovement) {
        tempSolution.applyImprovement(solutionImprovement);

        tabuList.addAll(solutionImprovement.getInvolvedCustomers());
        while (tabuList.size() > tabuListLength) {
            tabuList.poll();
        }

        if (tempSolution.getCost() > bestSolution.getCost()) { // Found a worst solution
            aspiration = true;
            numOfIterationWithoutImprovment += 1;
        } else if (tempSolution.getCost() < bestSolution.getCost()) { // Update best solution
            bestSolution = (Solution) tempSolution.clone();
            numOfIterationWithoutImprovment = 0;
            aspiration = false;
            Console.println("\nNew best solution");
            Console.println(bestSolution);
        }
    }
}

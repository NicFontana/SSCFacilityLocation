package com.sscfacilitylocation;

import com.sscfacilitylocation.algorithms.greedy.LowerFacilityCostLowerCustomerCostGreedyStrategy;
import com.sscfacilitylocation.algorithms.localsearch.BestImprovementLocalSearchStrategy;
import com.sscfacilitylocation.common.Problem;
import com.sscfacilitylocation.common.Solution;
import com.sscfacilitylocation.utility.Console;

public class Main {

    public static void main(String[] args) {
        final String INSTANCE_PATH = "problem_instances/OR-Library_Instances/cap61";
        Problem problem = new Problem(
                INSTANCE_PATH,
                new LowerFacilityCostLowerCustomerCostGreedyStrategy(),
                new BestImprovementLocalSearchStrategy()
        );

        Console.println("Greedy solution: ");

        problem.solveWithGreedy();

        Solution solution = problem.getSolution();

        if (solution != null) {
            Console.println(solution);
            Console.println("\nApplying Local Search: ");
            problem.performLocalSearch();
        } else {
            Console.println("Problem is unsatisfiable.");
        }
    }
}

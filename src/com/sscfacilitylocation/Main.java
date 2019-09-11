package com.sscfacilitylocation;

import com.sscfacilitylocation.algorithms.greedy.LowerFacilityCostLowerCustomerCostGreedyStrategy;
import com.sscfacilitylocation.algorithms.localsearch.BestImprovementLocalSearchStrategy;
import com.sscfacilitylocation.algorithms.metaheuristic.CustomerTabuSearch;
import com.sscfacilitylocation.common.Problem;
import com.sscfacilitylocation.common.Solution;
import com.sscfacilitylocation.utility.Console;

public class Main {

    public static void main(String[] args) {
        final String INSTANCE_PATH = "problem_instances/OR-Library_Instances/cap121";
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
            //problem.performLocalSearch();
            CustomerTabuSearch tabuSearch = new CustomerTabuSearch(solution, problem.getNumOfCustomers() / 3, 2000);
            problem.performTabuSearch(tabuSearch);
        } else {
            Console.println("Problem is unsatisfiable.");
        }
    }
}

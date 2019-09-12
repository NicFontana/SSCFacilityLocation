package com.sscfacilitylocation;

import com.sscfacilitylocation.algorithms.greedy.AbstractGreedy;
import com.sscfacilitylocation.algorithms.greedy.LowerFacilityCostLowerCustomerCostGreedy;
import com.sscfacilitylocation.algorithms.localsearch.AbstractLocalSearch;
import com.sscfacilitylocation.algorithms.localsearch.BestImprovementLocalSearch;
import com.sscfacilitylocation.algorithms.metaheuristic.CustomerTabuSearch;
import com.sscfacilitylocation.common.Problem;
import com.sscfacilitylocation.common.Solution;
import com.sscfacilitylocation.utility.Console;

public class Main {

    public static void main(String[] args) {
        final String INSTANCE_PATH = "problem_instances/OR-Library_Instances/cap61";
        Problem problem = new Problem(INSTANCE_PATH);

        Console.println("Greedy solution: ");
        AbstractGreedy greedy = new LowerFacilityCostLowerCustomerCostGreedy(problem);
        problem.solveWithGreedy(greedy);

        Solution solution = problem.getSolution();

        if (solution != null) {
            Console.println(solution);

            Console.println("\nApplying Local Search: ");
            AbstractLocalSearch localSearch = new BestImprovementLocalSearch(solution);
            problem.performLocalSearch(localSearch);

            Console.println("\nApplying Tabu Search: ");
            CustomerTabuSearch tabuSearch = new CustomerTabuSearch(problem.getSolution(), problem.getNumOfCustomers() / 3, 5000);
            problem.performTabuSearch(tabuSearch);
        } else {
            Console.println("Problem is unsatisfiable.");
        }
    }
}

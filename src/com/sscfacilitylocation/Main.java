package com.sscfacilitylocation;

import com.sscfacilitylocation.algorithms.greedy.AbstractGreedy;
import com.sscfacilitylocation.algorithms.greedy.LowerFacilityCostLowerCustomerCostGreedy;
import com.sscfacilitylocation.algorithms.localsearch.AbstractLocalSearch;
import com.sscfacilitylocation.algorithms.localsearch.BestImprovementLocalSearch;
import com.sscfacilitylocation.algorithms.metaheuristic.CustomerTabuSearch;
import com.sscfacilitylocation.common.Problem;
import com.sscfacilitylocation.common.Solution;
import com.sscfacilitylocation.utility.Console;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        final String INSTANCE_PATH = "problem_instances/OR-Library_Instances/cap61";
        Problem problem = new Problem(INSTANCE_PATH);
        Console.println(problem);

        //BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));
        Scanner userIn = new Scanner(System.in);
        System.out.print("Press enter to apply greedy algorithm...");
        userIn.nextLine();

        Console.println("Greedy solution: ");
        AbstractGreedy greedy = new LowerFacilityCostLowerCustomerCostGreedy(problem);
        problem.solveWithGreedy(greedy);

        Solution solution = problem.getSolution();

        if (solution != null) {
            Console.println(solution);

            System.out.print("Press enter to apply local search...");
            userIn.nextLine();
            Console.println("\nApplying Local Search: ");
            AbstractLocalSearch localSearch = new BestImprovementLocalSearch(solution);
            problem.performLocalSearch(localSearch);
            Console.println(problem.getSolution());

            System.out.print("Press enter to apply tabu search...");
            userIn.nextLine();
            Console.println("\nApplying Tabu Search: ");
            CustomerTabuSearch tabuSearch = new CustomerTabuSearch(problem.getSolution(), problem.getNumOfCustomers() / 3, 2000);
            problem.performTabuSearch(tabuSearch);
            Console.println(problem.getSolution());
        } else {
            Console.println("Problem is unsatisfiable.");
        }
    }
}

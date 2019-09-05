package com.sscfacilitylocation;

import com.sscfacilitylocation.algorithms.MyGreedyStrategy;
import com.sscfacilitylocation.common.Problem;
import com.sscfacilitylocation.common.Solution;
import com.sscfacilitylocation.utility.Console;

public class Main {

    public static void main(String[] args) {
        final String INSTANCE_PATH = "problem_instances/toy";
        Problem problem = new Problem(INSTANCE_PATH, new MyGreedyStrategy());

        Console.println("Greedy solution: ");

        problem.solveWithGreedy();

        Solution solution = problem.getSolution();

        if (solution != null) {
            Console.println(solution);
        } else {
            Console.println("Problem is unsatisfiable.");
        }

    }
}

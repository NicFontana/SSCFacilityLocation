package com.sscfacilitylocation.algorithms.localsearch;

import com.sscfacilitylocation.common.Solution;

public interface LocalSearchStrategy {
    SolutionImprovement getSolutionImprovement(Solution currentSolution);
}

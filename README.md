# Single Source Capacitated Facility Location Problem
Given:
* n potential sites where a facility that provide a service could be installed: J = {1, .., n}
* the fixed cost of opening each facility j: F = {f1, .., fn}
* the capacity of each facility j (i.e. the maximum amount of service that can be delivered by the facility). S = {s1, .., sn}

Given:
* m customers: I = {1, .., m}
* the service demand of each customer i: D = {d1, .., dm}
* the cost of allocating all the demand of customer i to facility j: c_ji for each (j=1,...,|J|), (i=1,...,|I|)

By deciding which facility to open and, for each customer, who serves it, determine the minimum cost solution that satisfies all customers while respecting the capacities of the facilities in the hypothesis of each customer being served by a single facility.

# Implemented Algorithms

### Greedy
The greedy algorithm at each step opens the facility that can serve all the not served customers left at the minimum cost (even considering the fixed cost of opening).
After that it iterates over all these not served customers sorted by increasing serving cost (relatively to the just opened facility) and it assigns them to this facility, one by one only if the facility has the necessary capacity left.

The algorithm terminate after all the customers are served.
This algorithm give us the first rough solution. 

### Local Search
Starting with the solution found by the greedy algorithm, the local search build an improvement graph and makes use of it to find single customer exchanges between facilities (opened and closed) that can lower the cost of the solution.
If a profitable exchange is found then the algorithm makes the customers swaps, opening new facilities if necessary, producing a new better solution.
Otherwise it terminate.

### Tabu Search
The tabu search works similar to the local search, except that the recently swapped customers/involved facilities (both the versions are implemented) are not considered while building the improvement graph.
Also, the tabu search accept not profitable exchange to see if it can find better improvement in a second moment. In this way we can escape the attraction basin of the local optimum found previously by the local search with the aspiration to find a better one.  

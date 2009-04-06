%------------------------------------------------------------------------------%
% multidimknapsack_simple.mzn
% Peter Stuckey
% September 30 2006
% vim: ts=4 sw=4 et tw=0
%------------------------------------------------------------------------------%
%------------------------------------------------------------------------------%
% multidimknapsack.zinc
% Jakob Puchinger <jakobp@cs.mu.oz.au>
% Wed Jun 14
%------------------------------------------------------------------------------%

%------------------------------------------------------------------------------%
% The classical 0/1 multidimensional knapsack problem.
% There is a knapsack with m different Capacity constraints.
% There are n items with Profits and Weights.
% The Goals is to maximise the total Profit of the Items in the 
% Knapsack while  not violating any of the Capacity constraints.

int: n;
int: m;

array[1..n] of int: Profits;
array[1..n,1..m] of int: Weights;
array[1..m] of int: Capacities;

array[1..n] of var 0..1: x;

constraint
    forall(i in 1..m) (
        sum([Weights[j,i] * x[j] | j in 1..n])  <=  Capacities[i]
    );

solve maximize
    sum([x[j] * Profits[j] | j in 1..n]);

var int: tot_profit = sum([x[j] * Profits[j] | j in 1..n]);
output ["Multidimensional Knapsack Problem:\n"] ++
       ["Total Profit: "] ++ [show(tot_profit)] ++ ["\n"] ++
       ["Chosen Items: "] ++ [ show(i) ++ ":" ++ show(x[i]) ++ " " | i in 1..n]; 

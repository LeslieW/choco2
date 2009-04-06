% n-queens example in Zinc using CP techniques
% By Reza Rafeh July 2005
% MiniZinc version
% Peter Stuckey September 30 2006

int: n;

array [1..n] of var 1..n: q;

predicate 
    noattack(int: i, int: j, var int: qi, var int: qj) =
        qi     != qj     /\
        qi + i != qj + j /\
        qi - i != qj - j;

constraint
    forall (i in 1..n, j in i+1..n) (
        noattack(i, j, q[i], q[j])
    );

solve ::
       int_search(
               q,
               "first_fail",
               "indomain_min",
               "complete"
       )
       satisfy;

output  [show(n)] ++ [" queens, CP version:"] ++
        [       if j = 1 then "\n" else "" endif ++
                show_cond(q[i] = j, "Q ", ". ")
        |       i, j in 1..n
        ];

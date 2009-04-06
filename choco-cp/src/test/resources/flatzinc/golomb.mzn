%------------------------------------------------------------------------%
% Golomb rulers
% prob006 in csplib
%------------------------------------------------------------------------%
% From csplib:
% A Golomb ruler may be defined as a set of m integers 0 = a_1 < a_2 <
% ... < a_m such that the m(m-1)/2 differences a_j - a_i, 1 <= i < j
% <= m are distinct. Such a ruler is said to contain m marks and is of
% length a_m. The objective is to find optimal (minimum length) or
% near optimal rulers.
%
% This is the "ternary constraints and an alldifferent" model
%------------------------------------------------------------------------%

include "globals.mzn";

int: m;
int: n = m*m;

array [1..m] of var 0..n: mark;

array[1..(m*(m-1)) div 2] of var 0..n: differences =
    [ mark[j] - mark[i] | i in 1..m, j in i+1..m];

constraint mark[1] = 0;

constraint forall ( i in 1..m-2 ) ( mark[i] < mark[i+1] );

constraint all_different(differences);

constraint mark[2] - mark[1]  <  mark[m] - mark[m-1];

solve minimize mark[m];

output 
    [ "golomb " ] ++
    [ show(mark[i]) ++ " " | i in 1..m ] ++
    [ "\n" ];

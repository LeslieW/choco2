% perfsq.mzn
% vim: ft=zinc ts=4 sw=4 et
% Ralph Becket
% Thu May 31 11:44:33 EST 2007
%
% Perfect squares: find a set of integers the sum of whose squares is
% itself a square.

int: z;

array [0..z] of 0..z*z: sq = array1d(0..z, [x*x | x in 0..z]);

array [0..z] of var 0..z: s;            % Decreasing indices into sq.

var 0..z: k;                            % We are summing to sq[k];

var 1..z: j;                            % We want this many sub-squares.

    % Symmetry breaking: s is an array of indices into sq.  The indices are
    % strictly decreasing until they reach zero, whereupon the remainder are
    % also zero.
    %
constraint forall ( i in 1..z ) ( s[i]  >  0   ->   s[i - 1]  >  s[i] );

    % sq[k], sq[k + 1], ... can't appear in the solution.
    %
constraint s[0] < k;

    % We want the sum of the squares to be square.
    %
constraint sum ( i in 0..z ) ( sq[s[i]] )  =  sq[k];

    % We want the longest such sequence.
    %
constraint s[j] > 0;

solve maximize j;

output 
    [ "perfsq\n" ] ++
    [ show(k) ++ "^2 = " ] ++
    [ show(s[i]) ++ "^2 + " | i in 0 .. z - 1 ] ++
    [ show(s[z]) ++ "^2"];

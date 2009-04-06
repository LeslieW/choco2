%
%simple sudoku for squares of arbitrary size N = (S x S), for +ve integers S.
%
%andrew slater, july 2006
%

% MiniZinc version
% Peter Stuckey September 30 2006

int: S;
int: N = S * S;

set of int: PuzzleRange = 1..N;
set of int: SubSquareRange = 1..S;

array[1..N,1..N] of var PuzzleRange: puzzle;

% All different in rows and all different in columns.
%
constraint 
	forall (i, j in PuzzleRange, k in 1..(j - 1)) (
		puzzle[i,j] != puzzle[i,k]
	/\	puzzle[j,i] != puzzle[k,i]
	);

% All different in sub-squares:
% for the set of global coordinates (abscissa,ordinate) of the sub-squares 
% take a set of local coordinates of each sub-square,
% and for each coordinate, given a second set of local sub-square coordinates,
% if the coordinates differ, the value in the puzzle is different
%
constraint
	forall (
		a, o, a1, o1, a2, o2 in SubSquareRange
		where	a1 != a2
		/\	o1 != o2
	) (
		puzzle[(a - 1) * S + a1, (o - 1) * S + o1] !=
			puzzle[(a - 1) * S + a2, (o - 1) * S + o2]
	);

solve satisfy;

output [ "sudoku:\n\n" ] ++ 
	[ show(puzzle[i,j]) ++ if j mod S = 0 then if j = N then if i mod S = 0 then "\n\n" else "\n" endif else "  " endif else " " endif | i,j in 1..N];

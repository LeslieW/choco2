%% Product example from the OPL book!
%% product.mzn
%% Peter Stuckey September 30 2006

int: NumProducts;
set of int: Products = 1..NumProducts;

int: NumResources;
set of int: Resources = 1..NumResources;

array[Products] of float: demand;
array[Products] of float: insideCost;
array[Products] of float: outsideCost;
array[Products, Resources] of float: consumption;
array[Resources] of float: capacity;

array[Products] of var float: inside;
array[Products] of var float: outside;

constraint
	forall (p in Products) (
		inside[p]  >= 0.0
	/\	outside[p] >= 0.0
	);

constraint   
	forall(r in Resources) (
		sum (p in Products) (
                    consumption[p, r] * inside[p]
                )
		<=
                capacity[r]
	);

constraint
	forall(p in Products) (
		inside[p] + outside[p] >= demand[p]
	);

solve minimize
	sum (p in Products) (
		insideCost[p]*inside[p] + outsideCost[p]*outside[p]
	);

output [
	"production planning\n",
	"make inside: \t",
	show(inside), "\n",
	"make outside: \t",
	show(outside), "\n"
];

grammar FlatZinc2;
tokens{
	COLONCOLON	=	'::';
	COLON		=	':';		
	COMA		=	',';
	DOTDOT		=	'..';
	DOTDOTDOT	=	'...';
	EQUAL		=	'=';
	SEMICOLON	=	';';
	LP		=	'(';
	RP		=	')';
	RBOX		=	']';
	LBOX		=	'[';
	LB		=	'{';
	RB		=	'}';
	
	FALSE		=	'false';
	TRUE		=	'true';
	ARRAY		=	'array';
	BOOL		=	'bool';
	CONSTRAINT		=	'constraint';
	FLOAT		=	'float';
	INT		=	'int';
	MAXIMIZE		=	'maximize';
	MINIMIZE 		=	'minimize';
	OF		=	'of';
	SATISFY		=	'satisfy';
	PREDICATE		=	'predicate';
	SET		=	'set';
	SOLVE		=	'solve';
	VAR		=	'var';
}

INT_CONST	
	:	
	('+'|'-')? DIGIT+
	;
bool_const	:	
	TRUE 
	| FALSE
	;	
FLOAT_CONST	
	:	
	INT_CONST('.' DIGIT+)?('e'|'E' INT_CONST)?
	;		
IDENTIFIER	:	
	LIT (LIT|DIGIT|'_')*
	;	

// ITEMS
flatzinc_model			
	:	
	(pred_decl)*
	(param_decl)* 
	(var_decl)* 
	(constraint)* 
	solve_goal 
	;

pred_decl	:	
	PREDICATE IDENTIFIER LP pred_param (COMA pred_param)* RP SEMICOLON
	;
pred_param 	
	:	
	par_type COLON IDENTIFIER
	| var_type COLON IDENTIFIER 
	;
//type	:	
//	par_type
//	 | var_type
//	;		
par_type	:	
	BOOL
	| FLOAT
	| FLOAT_CONST DOTDOT FLOAT_CONST
	| INT
	| INT_CONST DOTDOT INT_CONST
	| LB INT_CONST (COMA INT_CONST)* RB
	| SET OF INT
	| SET OF INT_CONST DOTDOT INT_CONST
	| SET OF LB INT_CONST (COMA INT_CONST)* RB
	| ARRAY LBOX index_set RBOX OF BOOL
	| ARRAY LBOX index_set RBOX OF FLOAT
	| ARRAY LBOX index_set RBOX OF FLOAT_CONST DOTDOT FLOAT_CONST
	|  ARRAY LBOX index_set RBOX OF INT
	|  ARRAY LBOX index_set RBOX OF INT_CONST DOTDOT INT_CONST
	|  ARRAY LBOX index_set RBOX OF LB INT_CONST (COMA INT_CONST)* RB
	|  ARRAY LBOX index_set RBOX OF SET OF INT 
	| ARRAY LBOX index_set RBOX OF SET OF INT_CONST DOTDOT INT_CONST
	| ARRAY LBOX index_set RBOX OF SET OF LB INT_CONST (COMA INT_CONST)* RB
	;	
var_type	:
	VAR BOOL
	| VAR FLOAT
	| VAR FLOAT_CONST DOTDOT FLOAT_CONST
	| VAR INT
	| VAR INT_CONST DOTDOT INT_CONST
	| VAR LB INT_CONST (COMA INT_CONST)* RB
	| VAR SET OF INT_CONST DOTDOT INT_CONST
	| VAR SET OF LB INT_CONST (COMA INT_CONST)* RB
	| VAR ARRAY LBOX index_set RBOX OF BOOL
	| VAR ARRAY LBOX index_set RBOX OF FLOAT
	| VAR ARRAY LBOX index_set RBOX OF FLOAT_CONST DOTDOT FLOAT_CONST
	|  VAR ARRAY LBOX index_set RBOX OF INT
	|  VAR ARRAY LBOX index_set RBOX OF INT_CONST DOTDOT INT_CONST
	|  VAR ARRAY LBOX index_set RBOX OF LB INT_CONST (COMA INT_CONST)* RB
	| VAR ARRAY LBOX index_set RBOX OF SET OF INT_CONST DOTDOT INT_CONST
	| VAR ARRAY LBOX index_set RBOX OF SET OF LB INT_CONST (COMA INT_CONST)* RB
	;
index_set	:	
	INT_CONST DOTDOT INT_CONST 
	| INT
	;	
expr	:
	bool_const
	| FLOAT_CONST
	| INT_CONST
//	| IDENTIFIER
	| IDENTIFIER LBOX IDENTIFIER RBOX
	| LBOX expr (COMA expr)* RBOX
	| annotation
	| DOTDOTDOT
	;
param_decl	:
	par_type COLON IDENTIFIER EQUAL expr SEMICOLON
	;
var_decl	:
	var_type COLON IDENTIFIER annotations (EQUAL expr)? SEMICOLON
	;	
constraint	:
	CONSTRAINT IDENTIFIER LP expr (COMA expr)? RP annotations SEMICOLON	
	;	
solve_goal	:
	SOLVE annotations SATISFY SEMICOLON
//	| SOLVE annotations MINIMIZE expr SEMICOLON
//	| SOLVE annotations MAXIMIZE expr SEMICOLON
	;	
annotations	:
	(COLONCOLON annotation)*
	;
annotation	:
	IDENTIFIER
	| IDENTIFIER LP expr (COMA expr)* RP
	;
	
	
WS : (' '|'\n')+ {skip();} ;
// FRAGMENT
fragment DIGIT 		:	'0'..'9';
//fragment HEX_DIGIT		:	'0x' (DIGIT | 'A'..'F' | 'a'..'f')+;
//fragment OCT_DIGIT		:	'0o' ('0'..'7')+;
fragment LIT			:	'A'..'Z'|'a'..'z';
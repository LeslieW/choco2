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
	MAXIMIZE	=	'maximize';
	MINIMIZE 	=	'minimize';
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
bool_const	returns[boolean b]:	
	TRUE 		
	{b = true;}
	| FALSE		
	{b = false;}
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
par_type	returns[FZNType typ]:	
	BOOL
	{typ = FZNType.buildPar(TType.tbool, null);}
	| FLOAT
	{typ = FZNType.buildPar(TType.tfloat, null);}
	| f1=FLOAT_CONST DOTDOT f2=FLOAT_CONST
	{FZNType _f1 = FZNType.buildPar(TType.tfloat, f1);
	FZNType _f2 = FZNType.buildPar(TType.tfloat, f2);
	typ = FZNType.buildPar(TType.tbounds, new FZNType[]{_f1, _f2});}
	| INT
	{typ = FZNType.buildPar(TType.int, null);}
	| i1=INT_CONST DOTDOT i2=INT_CONST
	{FZNType _i1 = FZNType.buildPar(TType.tint, i1);
	FZNType _i2 = FZNType.buildPar(TType.tint, i2);
	typ = FZNType.buildPar(TType.tbounds, new FZNType[]{_i1, _i2});}
	| {List<FZNType> l = new ArrayList<FZNType>();}
	LB i=INT_CONST 
	{FZNType _i =  FZNType.buildPar(TType.tint, i);
	l.add(i);}
	(COMA in=INT_CONST
	{FZNType _in =  FZNType.buildPar(TType.tint, in);
	l.add(in);}
	)* RB
	{typ = FZNType.buildPar(TType.tlist, l);}
	| SET OF INT
	{typ = FZNType.buildPar(TType.tset, null);}
	| SET OF i1=INT_CONST DOTDOT i2=INT_CONST
	{FZNType _i1 = FZNType.buildPar(TType.tint, i1);
	FZNType _i2 = FZNType.buildPar(TType.tint, i2);
	FZNType bounds = FZNType.buildPar(TType.tbounds, new FZNType[]{_i1, _i2});
	typ = FZNType.buildPar(TType.tset, bounds);}
	| {List<FZNType> l = new ArrayList<FZNType>();}
	SET OF LB i=INT_CONST 
	{FZNType _i =  FZNType.buildPar(TType.tint, i);
	l.add(i);}
	(COMA in=INT_CONST
	{FZNType _in =  FZNType.buildPar(TType.tint, in);
	l.add(in);}
	)* RB
	{FZNType list = FZNType.buildPar(TType.tlist, l);
	typ = FZNType.buildPar(TType.tset, list);}
	| ARRAY LBOX index_set RBOX OF BOOL
	| ARRAY LBOX index_set RBOX OF FLOAT
	| ARRAY LBOX index_set RBOX OF FLOAT_CONST DOTDOT FLOAT_CONST
	| ARRAY LBOX index_set RBOX OF INT
	| ARRAY LBOX index_set RBOX OF INT_CONST DOTDOT INT_CONST
	| ARRAY LBOX index_set RBOX OF LB INT_CONST (COMA INT_CONST)* RB
	| ARRAY LBOX index_set RBOX OF SET OF INT 
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
	| VAR ARRAY LBOX index_set RBOX OF INT
	| VAR ARRAY LBOX index_set RBOX OF INT_CONST DOTDOT INT_CONST
	| VAR ARRAY LBOX index_set RBOX OF LB INT_CONST (COMA INT_CONST)* RB
	| VAR ARRAY LBOX index_set RBOX OF SET OF INT_CONST DOTDOT INT_CONST
	| VAR ARRAY LBOX index_set RBOX OF SET OF LB INT_CONST (COMA INT_CONST)* RB
	;
index_set	:	
	INT_CONST DOTDOT INT_CONST 
	| INT
	;	
expr	returns[FZNExpression exp]:
	b=bool_const				
	{exp = FZNExpression.build(EType.ebool, b);}
	| f=FLOAT_CONST
	{exp = FZNExpression.build(EType.efloat, f);}
	| i=INT_CONST
	{exp = FZNExpression.build(EType.eint, i);}
//	| IDENTIFIER
	| id=IDENTIFIER LBOX i=INT_CONST RBOX
	{exp = FZNExpression.build(EType.earray, new Object[]{id, i});}
	| {List<FZNExpression> l = new ArrayList<FZNExpression>();}
	LBOX e=expr 
	{l.add(e);}
	(COMA en=expr
	{l.add(en);}
	)* RBOX
	{exp = FZNExpression.build(EType.elist, l);}
	| an=annotation
	{exp = FZNExpression.build(EType.eanno, an);}
	| DOTDOTDOT
	{exp = FZNExpression.build(EType.edot, null);}
	;
var_decl	:
	par_type COLON IDENTIFIER EQUAL expr SEMICOLON
	|var_type COLON IDENTIFIER annotations (EQUAL expr)? SEMICOLON
	;	
constraint	:
	CONSTRAINT IDENTIFIER LP expr (COMA expr)? RP annotations SEMICOLON	
	;	
solve_goal	:
	SOLVE annotations (SATISFY|(MINIMIZE|MAXIMIZE)expr) SEMICOLON
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
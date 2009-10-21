grammar FlatZinc;
tokens{
	COLONCOLON	=	'::';
	COLON		=	':';		
	COMA		=	',';
	DOTDOT		=	'..';
	EQUAL		=	'=';
	SEMICOLON	=	';';
	LP		=	'(';
	RP		=	')';
	RBOX		=	']';
	LBOX		=	'[';
	LB		=	'{';
	RB		=	'}';
	DQUOTE		=	'"';
	FALSE		=	'false';
	TRUE		=	'true';
	ANY		=	'any';
	ARRAY		=	'array';
	BOOL		=	'bool';
	CASE		=	'case';
	CONSTRAINT		=	'constraint';
	ELSE		=	'else';
	ELSEIF		=	'elseif';
	ENDIF		=	'endif';
	ENUM		=	'enum';
	FLOAT		=	'float';
	FUNCTION		=	'function';
	IF		=	'if';
	INCLUDE		=	'include';
	INT		=	'int';
	LET		=	'let';
	MAXIMIZE		=	'maximize';
	MINIMIZE 		=	'minimize';
	OF		=	'of';
	SATISFY		=	'satisfy';
	OUTPUT		=	'output';
	PAR		=	'par';
	PREDICATE		=	'predicate';
	RECORD		=	'record';
	SET		=	'set';
	SHOW		=	'show';
	SHOWCOND		=	'show_cond';
	SOLVE		=	'solve';
	STRING		=	'string';
	TEST		=	'test';
	THEN		=	'then';
	TUPLE		=	'tuple';
	TYPE		=	'type';
	VAR		=	'var';
	VARIANT_RECORD	=	'variant_record';
	WHERE		=	'where';
}
@header{
package parser.chocogen.mzn;

import java.util.HashMap;
import choco.cp.model.CPModel;
import choco.cp.solver.preprocessor.PreProcessCPSolver;
import static choco.Choco.*;
}

@lexer::header{
package parser.chocogen.mzn;
}

@members {
/** Map variable name to Integer object holding value */
static HashMap<String, Object> memory = new HashMap<String, Object>();
CPModel model = new CPModel();
PreProcessCPSolver solver = new PreProcessCPSolver();
private static final int OFFSET = 1;
}
// ITEMS
model			
	:	
		{
		FlatZincHelper.init(memory);
		}
		(pred_decl_item SEMICOLON)* 
		(var_decl_item SEMICOLON)* 
		(constraint_item SEMICOLON)* 
		solve_item SEMICOLON 
		(output_item SEMICOLON)?;
pred_decl_item		
	:	PREDICATE IDENT LP pred_arg (COMA pred_arg)* RP;
pred_arg 	
	:	non_array_ti_expr_tail COLON IDENT	
		| ARRAY LBOX INT_LITERAL DOTDOT INT_LITERAL RBOX OF array_decl_tail COLON  IDENT
		| ARRAY LBOX INT RBOX OF array_decl_tail COLON IDENT
		;
var_decl_item
	:	VAR natet=non_array_ti_expr_tail COLON name=ident_anns (EQUAL nafe=non_array_flat_expr)?
		{
		// CREATE A VARIABLE (there is 'VAR' keyword see 'Specifications of FlatZinc, §5.4
		FlatZincHelper.buildVar(natet, name, nafe);
		}
		| natet=non_array_ti_expr_tail COLON name=ident_anns EQUAL nafe=non_array_flat_expr
		{
		// CREATE A PARAMETER (there is not 'VAR' keyword
		FlatZincHelper.buildPar(natet, name, nafe);
		}
		| ARRAY LBOX f=INT_LITERAL DOTDOT t=INT_LITERAL RBOX OF adt=array_decl_tail
		{
		// CREATE AN ARRAY OF VARIABLES/PARAMETERS
		FlatZincHelper.buildArray(Integer.valueOf(f.getText()), Integer.valueOf(t.getText()), adt);		
		}
		;
array_decl_tail	returns [FlatZincHelper.ArrayDecl adt]	
	:	natet=non_array_ti_expr_tail COLON name=ident_anns EQUAL al=array_literal
		{
		$adt=FlatZincHelper.build(natet, name, al,true);
		}
		| VAR natet=non_array_ti_expr_tail COLON name=ident_anns ( EQUAL al=array_literal)?
		{
		$adt=FlatZincHelper.build(natet, name, al, false);
		};
ident_anns		returns [String value]
	:	IDENT annotations 
		{
		$value = $IDENT.text;
		};
constraint_item	
	:	CONSTRAINT constraint_elem annotations;
//		{model.addConstraint($constraint_elem.value);};
constraint_elem	//returns[Constraint value]
	:	IDENT LP flat_expr (COMA flat_expr)*RP
		| variable_expr
		{System.err.println("constraint_elem : not yet implemented");};
solve_item	
	:	{solver.read(model);}SOLVE annotations solve_kind;
solve_kind			
	:	SATISFY 		
		{solver.solve();}
		| MINIMIZE solve_expr 	
		{solver.minimize(true);}
		| MAXIMIZE solve_expr 	
		{solver.maximize(true);};	
output_item	
	:	OUTPUT LBOX output_elem (COMA output_elem)* RBOX;
output_elem	
	:	SHOW LP flat_expr RP
		| SHOWCOND LP flat_expr COMA flat_expr COMA flat_expr RP
		| STRING_LITERAL;

// TYPE-INST EXPRESSIONS TAILS
non_array_ti_expr_tail	returns [FlatZincHelper.VarType vt]
	:	sc=scalar_ti_expr_tail
		{
		$vt=sc;
		}
		|se=set_ti_expr_tail
		{
		$vt = se;
		}
		;

bool_ti_expr_tail	returns [FlatZincHelper.VarType vt]
	:	BOOL
		{
		$vt = FlatZincHelper.build(FlatZincHelper.EnumVar.bBool, null);
		};
scalar_ti_expr_tail	returns  [FlatZincHelper.VarType vt]
	:	b=bool_ti_expr_tail
		{
		$vt = b;
		}
		|i=int_ti_expr_tail
		{
		$vt=i;
		}
		|f=float_ti_expr_tail
		{
		$vt=f;
		}
		;
int_ti_expr_tail	returns [FlatZincHelper.VarType vt]
	:	INT
		{
		$vt = FlatZincHelper.build(FlatZincHelper.EnumVar.iInt, null);
		}
		| i1=INT_LITERAL DOTDOT i2=INT_LITERAL
		{
		int s=Integer.valueOf($i1.text);
		int e=Integer.valueOf($i2.text);
		$vt = FlatZincHelper.build(FlatZincHelper.EnumVar.iBounds, new int[]{s,e});
		}		
		| {List<Integer> values = new ArrayList<Integer>();}LB e1=INT_LITERAL {values.add(Integer.valueOf($e1.text));}(COMA e2=INT_LITERAL{values.add(Integer.valueOf($e2.text));})* RB
		{
		$vt = FlatZincHelper.build(FlatZincHelper.EnumVar.iValues, values.toArray(new Integer[values.size()]));
		};
float_ti_expr_tail	returns [FlatZincHelper.VarType vt]
	:	FLOAT
		{
		$vt = FlatZincHelper.build(FlatZincHelper.EnumVar.fFloat, null);
		}
		| f1=FLOAT_LITERAL DOTDOT f2=FLOAT_LITERAL
		{
		double s=Double.valueOf($f1.text);
		double e=Double.valueOf($f2.text);
		$vt = FlatZincHelper.build(FlatZincHelper.EnumVar.iBounds, new double[]{s,e});
		};
set_ti_expr_tail	returns[FlatZincHelper.VarType vt]
	:	SET OF so=scalar_ti_expr_tail
		{
		$vt = FlatZincHelper.build(FlatZincHelper.EnumVar.setOf, so);
		};

// EXPRESSIONS
non_array_flat_expr	returns [FlatZincHelper.ValType nafe]
	:	sfe=scalar_flat_expr
		{
		$nafe=sfe;
		}
		| sl=set_literal
		{
		$nafe=sl;
		};
scalar_flat_expr	returns [FlatZincHelper.ValType sfe]
	:	 IDENT
		{
		$sfe=FlatZincHelper.build(FlatZincHelper.EnumVal.sString, $IDENT.text);
		}
		| aae=array_access_expr
		{
		$sfe=aae;
		}
		| bl=bool_literal
		{
		$sfe=FlatZincHelper.build(FlatZincHelper.EnumVal.bBool, bl);
		}
		| INT_LITERAL
		{
		$sfe=FlatZincHelper.build(FlatZincHelper.EnumVal.iInt, new Integer($INT_LITERAL.text));
		}
		| FLOAT_LITERAL
		{
		$sfe=FlatZincHelper.build(FlatZincHelper.EnumVal.fFloat, new Double($FLOAT_LITERAL.text));
		}
		| STRING_LITERAL
		{
		$sfe=FlatZincHelper.build(FlatZincHelper.EnumVal.sString, $STRING_LITERAL.text);
		};
int_flat_expr		returns [FlatZincHelper.ValType ife]
	:	 aae=array_access_expr
		{
		$ife=aae;
		}
		|IDENT
		{
		$ife=FlatZincHelper.build(FlatZincHelper.EnumVal.sString, $IDENT.text);
		}
		| INT_LITERAL
		{
		$ife=FlatZincHelper.build(FlatZincHelper.EnumVal.iInt, $INT_LITERAL.text);
		};
variable_expr		returns[FlatZincHelper.ValType ve]
	:	IDENT
		{
		$ve=FlatZincHelper.build(FlatZincHelper.EnumVal.sString, $IDENT.text);
		}
		| aae=array_access_expr
		{
		$ve=aae;
		};
array_access_expr	returns [FlatZincHelper.ValType aae]
	:	IDENT LBOX i=int_index_expr RBOX
		{
		Object[] tab = (Object[])memory.get($IDENT.text);
		Object val = tab[i-OFFSET];
		FlatZincHelper.EnumVal type = null;
		if(val instanceof Integer){
		type=FlatZincHelper.EnumVal.iInt;
		}else if(val instanceof Boolean){
		type=FlatZincHelper.EnumVal.bBool;
		}else if(val instanceof Double){
		type=FlatZincHelper.EnumVal.fFloat;
		}else if(val instanceof String){
		type=FlatZincHelper.EnumVal.sString;		
		}
		$aae = FlatZincHelper.build(type, tab[i]);
		};
int_index_expr	returns [int value]
	:	INT_LITERAL  
		{
		$value = Integer.parseInt($INT_LITERAL.text);
		}
		| IDENT  
		{
		$value = (Integer)memory.get($IDENT.text);
		};
bool_literal		returns [boolean value]
	:	FALSE 
		{
		$value = false;
		}
		| TRUE 
		{
		$value = true;
		}
		;		
set_literal		returns[FlatZincHelper.ValType sl]
	:	LB {List<FlatZincHelper.ValType> list = new ArrayList<FlatZincHelper.ValType>();}(sfe1=scalar_flat_expr {list.add(sfe1);}(COMA sfe2=scalar_flat_expr{list.add(sfe2);})*)? RB
		{
		FlatZincHelper.ValType[] array = new FlatZincHelper.ValType[list.size()];
		list.toArray(array);
		$sl=FlatZincHelper.build(FlatZincHelper.EnumVal.array, array);
		}
		| i1=int_flat_expr DOTDOT i2=int_flat_expr
		{
		$sl=FlatZincHelper.build(FlatZincHelper.EnumVal.interval, new FlatZincHelper.ValType[]{i1,i2});
		};
array_literal		returns [FlatZincHelper.ValType al]
	:	LBOX {List<FlatZincHelper.ValType> list = new ArrayList<FlatZincHelper.ValType>();}(nafe1=non_array_flat_expr {list.add(nafe1);}(COMA nafe2=non_array_flat_expr {list.add(nafe2);})* )? RBOX
		{
		FlatZincHelper.ValType[] array = new FlatZincHelper.ValType[list.size()];
		list.toArray(array);
		$al=FlatZincHelper.build(FlatZincHelper.EnumVal.array, array);
		};

annotations	
	:	(COLONCOLON annotation)*;
annotation	
	:	IDENT ( LP ann_expr (COMA ann_expr)* RP )?;
ann_expr	
	:	IDENT LP ann_expr (COMA ann_expr)* RP
		|  flat_expr;
flat_expr		returns[FlatZincHelper.ValType vt]
	:	nafe=non_array_flat_expr
		{
		$vt=nafe;
		}
		| al=array_literal
		{
		$vt=al;
		};
solve_expr		returns[FlatZincHelper.ValType vt]
	:	IDENT
		{
		$vt = FlatZincHelper.build(FlatZincHelper.EnumVal.sString, $IDENT.text);
		}
		| aae=array_access_expr
		{
		$vt = aae;
		}
		| IDENT LP flat_expr (COMA flat_expr)* RP
		{
		System.err.println("solve_epxr::IDENT LP flat_expr (COMA flat_expr)* RP:: ERREUR");
		};		


INT_LITERAL		:	('-')? (DIGIT+|HEX_DIGIT+| OCT_DIGIT+);
FLOAT_LITERAL		:	('-')? DIGIT+('.'|('.'DIGIT+)?('E'|'e')('-'|'+')?)DIGIT+;
STRING_LITERAL		:	DQUOTE (~('\n'| '\r'| '\f'))* DQUOTE;
IDENT			:	LIT (LIT|DIGIT|'_')*;
// FRAGMENT
fragment DIGIT 		:	'0'..'9';
fragment HEX_DIGIT		:	'0x' (DIGIT | 'A'..'F' | 'a'..'f')+;
fragment OCT_DIGIT		:	'0o' ('0'..'7')+;
fragment LIT			:	'A'..'Z'|'a'..'z';

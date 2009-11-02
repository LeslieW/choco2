// $ANTLR 3.2 Sep 23, 2009 12:02:23 /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g 2009-11-02 18:49:17

package parser.chocogen.mzn;

import choco.cp.model.CPModel;
import choco.cp.solver.preprocessor.PreProcessCPSolver;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.model.variables.integer.IntegerVariable;
import org.antlr.runtime.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public class FlatZincParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "COLONCOLON", "COLON", "COMA", "DOTDOT", "EQUAL", "SEMICOLON", "LP", "RP", "RBOX", "LBOX", "LB", "RB", "DQUOTE", "FALSE", "TRUE", "ANY", "ARRAY", "BOOL", "CASE", "CONSTRAINT", "ELSE", "ELSEIF", "ENDIF", "ENUM", "FLOAT", "FUNCTION", "IF", "INCLUDE", "INT", "LET", "MAXIMIZE", "MINIMIZE", "OF", "SATISFY", "OUTPUT", "PAR", "PREDICATE", "RECORD", "SET", "SHOW", "SHOWCOND", "SOLVE", "STRING", "TEST", "THEN", "TUPLE", "TYPE", "VAR", "VARIANT_RECORD", "WHERE", "IDENT", "INT_LITERAL", "STRING_LITERAL", "FLOAT_LITERAL", "DIGIT", "HEX_DIGIT", "OCT_DIGIT", "LIT", "WS"
    };
    public static final int FUNCTION=29;
    public static final int WHERE=53;
    public static final int LBOX=13;
    public static final int RB=15;
    public static final int RP=11;
    public static final int LP=10;
    public static final int CASE=22;
    public static final int DQUOTE=16;
    public static final int DOTDOT=7;
    public static final int FLOAT=28;
    public static final int EOF=-1;
    public static final int PREDICATE=40;
    public static final int TYPE=50;
    public static final int IF=30;
    public static final int TUPLE=49;
    public static final int OCT_DIGIT=60;
    public static final int STRING_LITERAL=56;
    public static final int THEN=48;
    public static final int EQUAL=8;
    public static final int INCLUDE=31;
    public static final int MAXIMIZE=34;
    public static final int SATISFY=37;
    public static final int ENDIF=26;
    public static final int IDENT=54;
    public static final int PAR=39;
    public static final int VAR=51;
    public static final int DIGIT=58;
    public static final int SHOW=43;
    public static final int ARRAY=20;
    public static final int INT_LITERAL=55;
    public static final int RECORD=41;
    public static final int RBOX=12;
    public static final int ELSE=24;
    public static final int LIT=61;
    public static final int BOOL=21;
    public static final int HEX_DIGIT=59;
    public static final int VARIANT_RECORD=52;
    public static final int SET=42;
    public static final int SEMICOLON=9;
    public static final int INT=32;
    public static final int MINIMIZE=35;
    public static final int COMA=6;
    public static final int OF=36;
    public static final int TRUE=18;
    public static final int ELSEIF=25;
    public static final int FLOAT_LITERAL=57;
    public static final int COLON=5;
    public static final int SHOWCOND=44;
    public static final int COLONCOLON=4;
    public static final int WS=62;
    public static final int ANY=19;
    public static final int ENUM=27;
    public static final int TEST=47;
    public static final int LB=14;
    public static final int FALSE=17;
    public static final int SOLVE=45;
    public static final int CONSTRAINT=23;
    public static final int OUTPUT=38;
    public static final int LET=33;
    public static final int STRING=46;

    // delegates
    // delegators


        public FlatZincParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public FlatZincParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return FlatZincParser.tokenNames; }
    public String getGrammarFileName() { return "/media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g"; }


    /** Map variable name to Integer object holding value */
    static HashMap<String, Object> memory = new HashMap<String, Object>();
    CPModel model = new CPModel();
    PreProcessCPSolver solver = new PreProcessCPSolver();
    private static final int OFFSET = 1;
    static Logger LOGGER = ChocoLogging.getParserLogger();
    private Boolean isFeasible = null;
        private int nbnode = 0;
        private int nbback = 0;
        private static long[] time = new long[4];

    public void init() {
            time = new long[4];
            isFeasible = null;
            nbback = 0;
            nbnode = 0;
    	time[0] = System.currentTimeMillis();
        }



    // $ANTLR start "model"
    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:92:1: model returns [Boolean isSatisified] : ( pred_decl_item SEMICOLON )* ( var_decl_item SEMICOLON )* ( constraint_item SEMICOLON )* isSatisfied= solve_item SEMICOLON ( output_item SEMICOLON )? ;
    public final Boolean model() throws RecognitionException {
        Boolean isSatisified = null;

        Boolean isSatisfied = null;


        try {
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:93:2: ( ( pred_decl_item SEMICOLON )* ( var_decl_item SEMICOLON )* ( constraint_item SEMICOLON )* isSatisfied= solve_item SEMICOLON ( output_item SEMICOLON )? )
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:94:3: ( pred_decl_item SEMICOLON )* ( var_decl_item SEMICOLON )* ( constraint_item SEMICOLON )* isSatisfied= solve_item SEMICOLON ( output_item SEMICOLON )?
            {

            		FZVariableBuilder.init(memory);
            		FZConstraintBuilder.init(memory, model);
            		init();
            		
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:99:3: ( pred_decl_item SEMICOLON )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==PREDICATE) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:99:4: pred_decl_item SEMICOLON
            	    {
            	    pushFollow(FOLLOW_pred_decl_item_in_model498);
            	    pred_decl_item();

            	    state._fsp--;

            	    match(input,SEMICOLON,FOLLOW_SEMICOLON_in_model500); 

            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);

            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:100:3: ( var_decl_item SEMICOLON )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( (LA2_0==LB||(LA2_0>=ARRAY && LA2_0<=BOOL)||LA2_0==FLOAT||LA2_0==INT||LA2_0==SET||LA2_0==VAR||LA2_0==INT_LITERAL||LA2_0==FLOAT_LITERAL) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:100:4: var_decl_item SEMICOLON
            	    {
            	    pushFollow(FOLLOW_var_decl_item_in_model508);
            	    var_decl_item();

            	    state._fsp--;

            	    match(input,SEMICOLON,FOLLOW_SEMICOLON_in_model510); 

            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);

            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:101:3: ( constraint_item SEMICOLON )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( (LA3_0==CONSTRAINT) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:101:4: constraint_item SEMICOLON
            	    {
            	    pushFollow(FOLLOW_constraint_item_in_model518);
            	    constraint_item();

            	    state._fsp--;

            	    match(input,SEMICOLON,FOLLOW_SEMICOLON_in_model520); 

            	    }
            	    break;

            	default :
            	    break loop3;
                }
            } while (true);

            pushFollow(FOLLOW_solve_item_in_model529);
            isSatisfied=solve_item();

            state._fsp--;

            match(input,SEMICOLON,FOLLOW_SEMICOLON_in_model531); 
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:103:3: ( output_item SEMICOLON )?
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0==OUTPUT) ) {
                alt4=1;
            }
            switch (alt4) {
                case 1 :
                    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:103:4: output_item SEMICOLON
                    {
                    pushFollow(FOLLOW_output_item_in_model537);
                    output_item();

                    state._fsp--;

                    match(input,SEMICOLON,FOLLOW_SEMICOLON_in_model539); 

                    }
                    break;

            }


            		if (isFeasible == Boolean.TRUE
                                && ((!solver.checkDecisionVariables())
                                || solver.checkSolution(false) != Boolean.TRUE)) {
                            isFeasible = null;
                        }
                        StringBuffer res = new StringBuffer("c ");
                        if (isFeasible == null) {
                            res.append("TIMEOUT");
                            LOGGER.info("s UNKNOWN");
                        } else if (!isFeasible) {
                            res.append("UNSAT");
                            LOGGER.info("s UNSATISFIABLE");
                        } else {
                            res.append("SAT");
                            LOGGER.info("s SATISFIABLE");
                            String sol = "v ";
                            LOGGER.info(sol);
                        }
                        double rtime = (double) (time[3] - time[0]) / 1000D;
                        res.append(" ").append(rtime).append(" TIME     ");
                        res.append(" ").append(nbnode).append(" NDS   ");
                        res.append(" ").append(time[1] - time[0]).append(" PARSTIME     ");
                        res.append(" ").append(time[2] - time[1]).append(" BUILDPB      ");
                        res.append(" ").append(time[3] - time[2]).append(" RES      ");
                        LOGGER.info("d RUNTIME " + rtime);
                        LOGGER.info("d NODES " + nbnode);
                        LOGGER.info("d NODES/s " + Math.round((double) nbnode / rtime));
                        LOGGER.info("d BACKTRACKS " + nbback);
                        LOGGER.info("d BACKTRACKS/s " + Math.round((double) nbback / rtime));
                     
                        LOGGER.info("" + res);
                        ChocoLogging.flushLogs();
            		

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return isSatisified;
    }
    // $ANTLR end "model"


    // $ANTLR start "pred_decl_item"
    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:138:1: pred_decl_item : PREDICATE IDENT LP pred_arg ( COMA pred_arg )* RP ;
    public final void pred_decl_item() throws RecognitionException {
        try {
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:139:2: ( PREDICATE IDENT LP pred_arg ( COMA pred_arg )* RP )
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:139:4: PREDICATE IDENT LP pred_arg ( COMA pred_arg )* RP
            {
            match(input,PREDICATE,FOLLOW_PREDICATE_in_pred_decl_item555); 
            match(input,IDENT,FOLLOW_IDENT_in_pred_decl_item557); 
            match(input,LP,FOLLOW_LP_in_pred_decl_item559); 
            pushFollow(FOLLOW_pred_arg_in_pred_decl_item561);
            pred_arg();

            state._fsp--;

            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:139:32: ( COMA pred_arg )*
            loop5:
            do {
                int alt5=2;
                int LA5_0 = input.LA(1);

                if ( (LA5_0==COMA) ) {
                    alt5=1;
                }


                switch (alt5) {
            	case 1 :
            	    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:139:33: COMA pred_arg
            	    {
            	    match(input,COMA,FOLLOW_COMA_in_pred_decl_item564); 
            	    pushFollow(FOLLOW_pred_arg_in_pred_decl_item566);
            	    pred_arg();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop5;
                }
            } while (true);

            match(input,RP,FOLLOW_RP_in_pred_decl_item570); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "pred_decl_item"


    // $ANTLR start "pred_arg"
    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:140:1: pred_arg : ( non_array_ti_expr_tail COLON IDENT | ARRAY LBOX INT_LITERAL DOTDOT INT_LITERAL RBOX OF array_decl_tail COLON IDENT | ARRAY LBOX INT RBOX OF array_decl_tail COLON IDENT );
    public final void pred_arg() throws RecognitionException {
        try {
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:141:2: ( non_array_ti_expr_tail COLON IDENT | ARRAY LBOX INT_LITERAL DOTDOT INT_LITERAL RBOX OF array_decl_tail COLON IDENT | ARRAY LBOX INT RBOX OF array_decl_tail COLON IDENT )
            int alt6=3;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==LB||LA6_0==BOOL||LA6_0==FLOAT||LA6_0==INT||LA6_0==SET||LA6_0==INT_LITERAL||LA6_0==FLOAT_LITERAL) ) {
                alt6=1;
            }
            else if ( (LA6_0==ARRAY) ) {
                int LA6_2 = input.LA(2);

                if ( (LA6_2==LBOX) ) {
                    int LA6_3 = input.LA(3);

                    if ( (LA6_3==INT_LITERAL) ) {
                        alt6=2;
                    }
                    else if ( (LA6_3==INT) ) {
                        alt6=3;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 6, 3, input);

                        throw nvae;
                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 6, 2, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 6, 0, input);

                throw nvae;
            }
            switch (alt6) {
                case 1 :
                    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:141:4: non_array_ti_expr_tail COLON IDENT
                    {
                    pushFollow(FOLLOW_non_array_ti_expr_tail_in_pred_arg580);
                    non_array_ti_expr_tail();

                    state._fsp--;

                    match(input,COLON,FOLLOW_COLON_in_pred_arg582); 
                    match(input,IDENT,FOLLOW_IDENT_in_pred_arg584); 

                    }
                    break;
                case 2 :
                    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:142:5: ARRAY LBOX INT_LITERAL DOTDOT INT_LITERAL RBOX OF array_decl_tail COLON IDENT
                    {
                    match(input,ARRAY,FOLLOW_ARRAY_in_pred_arg591); 
                    match(input,LBOX,FOLLOW_LBOX_in_pred_arg593); 
                    match(input,INT_LITERAL,FOLLOW_INT_LITERAL_in_pred_arg595); 
                    match(input,DOTDOT,FOLLOW_DOTDOT_in_pred_arg597); 
                    match(input,INT_LITERAL,FOLLOW_INT_LITERAL_in_pred_arg599); 
                    match(input,RBOX,FOLLOW_RBOX_in_pred_arg601); 
                    match(input,OF,FOLLOW_OF_in_pred_arg603); 
                    pushFollow(FOLLOW_array_decl_tail_in_pred_arg605);
                    array_decl_tail();

                    state._fsp--;

                    match(input,COLON,FOLLOW_COLON_in_pred_arg607); 
                    match(input,IDENT,FOLLOW_IDENT_in_pred_arg610); 

                    }
                    break;
                case 3 :
                    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:143:5: ARRAY LBOX INT RBOX OF array_decl_tail COLON IDENT
                    {
                    match(input,ARRAY,FOLLOW_ARRAY_in_pred_arg616); 
                    match(input,LBOX,FOLLOW_LBOX_in_pred_arg618); 
                    match(input,INT,FOLLOW_INT_in_pred_arg620); 
                    match(input,RBOX,FOLLOW_RBOX_in_pred_arg622); 
                    match(input,OF,FOLLOW_OF_in_pred_arg624); 
                    pushFollow(FOLLOW_array_decl_tail_in_pred_arg626);
                    array_decl_tail();

                    state._fsp--;

                    match(input,COLON,FOLLOW_COLON_in_pred_arg628); 
                    match(input,IDENT,FOLLOW_IDENT_in_pred_arg630); 

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "pred_arg"


    // $ANTLR start "var_decl_item"
    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:145:1: var_decl_item : ( VAR natet= non_array_ti_expr_tail COLON name= ident_anns ( EQUAL nafe= non_array_flat_expr )? | natet= non_array_ti_expr_tail COLON name= ident_anns EQUAL nafe= non_array_flat_expr | ARRAY LBOX f= INT_LITERAL DOTDOT t= INT_LITERAL RBOX OF adt= array_decl_tail );
    public final void var_decl_item() throws RecognitionException {
        Token f=null;
        Token t=null;
        FZVariableBuilder.VarType natet = null;

        String name = null;

        FZVariableBuilder.ValType nafe = null;

        FZVariableBuilder.ArrayDecl adt = null;


        try {
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:146:2: ( VAR natet= non_array_ti_expr_tail COLON name= ident_anns ( EQUAL nafe= non_array_flat_expr )? | natet= non_array_ti_expr_tail COLON name= ident_anns EQUAL nafe= non_array_flat_expr | ARRAY LBOX f= INT_LITERAL DOTDOT t= INT_LITERAL RBOX OF adt= array_decl_tail )
            int alt8=3;
            switch ( input.LA(1) ) {
            case VAR:
                {
                alt8=1;
                }
                break;
            case LB:
            case BOOL:
            case FLOAT:
            case INT:
            case SET:
            case INT_LITERAL:
            case FLOAT_LITERAL:
                {
                alt8=2;
                }
                break;
            case ARRAY:
                {
                alt8=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 8, 0, input);

                throw nvae;
            }

            switch (alt8) {
                case 1 :
                    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:146:4: VAR natet= non_array_ti_expr_tail COLON name= ident_anns ( EQUAL nafe= non_array_flat_expr )?
                    {
                    match(input,VAR,FOLLOW_VAR_in_var_decl_item641); 
                    pushFollow(FOLLOW_non_array_ti_expr_tail_in_var_decl_item645);
                    natet=non_array_ti_expr_tail();

                    state._fsp--;

                    match(input,COLON,FOLLOW_COLON_in_var_decl_item647); 
                    pushFollow(FOLLOW_ident_anns_in_var_decl_item651);
                    name=ident_anns();

                    state._fsp--;

                    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:146:59: ( EQUAL nafe= non_array_flat_expr )?
                    int alt7=2;
                    int LA7_0 = input.LA(1);

                    if ( (LA7_0==EQUAL) ) {
                        alt7=1;
                    }
                    switch (alt7) {
                        case 1 :
                            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:146:60: EQUAL nafe= non_array_flat_expr
                            {
                            match(input,EQUAL,FOLLOW_EQUAL_in_var_decl_item654); 
                            pushFollow(FOLLOW_non_array_flat_expr_in_var_decl_item658);
                            nafe=non_array_flat_expr();

                            state._fsp--;


                            }
                            break;

                    }


                    		// CREATE A VARIABLE (there is 'VAR' keyword see 'Specifications of FlatZinc, §5.4
                    		FZVariableBuilder.buildVar(natet, name, nafe);
                    		

                    }
                    break;
                case 2 :
                    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:151:5: natet= non_array_ti_expr_tail COLON name= ident_anns EQUAL nafe= non_array_flat_expr
                    {
                    pushFollow(FOLLOW_non_array_ti_expr_tail_in_var_decl_item672);
                    natet=non_array_ti_expr_tail();

                    state._fsp--;

                    match(input,COLON,FOLLOW_COLON_in_var_decl_item674); 
                    pushFollow(FOLLOW_ident_anns_in_var_decl_item678);
                    name=ident_anns();

                    state._fsp--;

                    match(input,EQUAL,FOLLOW_EQUAL_in_var_decl_item680); 
                    pushFollow(FOLLOW_non_array_flat_expr_in_var_decl_item684);
                    nafe=non_array_flat_expr();

                    state._fsp--;


                    		// CREATE A PARAMETER (there is not 'VAR' keyword
                    		FZVariableBuilder.buildPar(natet, name, nafe);
                    		

                    }
                    break;
                case 3 :
                    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:156:5: ARRAY LBOX f= INT_LITERAL DOTDOT t= INT_LITERAL RBOX OF adt= array_decl_tail
                    {
                    match(input,ARRAY,FOLLOW_ARRAY_in_var_decl_item694); 
                    match(input,LBOX,FOLLOW_LBOX_in_var_decl_item696); 
                    f=(Token)match(input,INT_LITERAL,FOLLOW_INT_LITERAL_in_var_decl_item700); 
                    match(input,DOTDOT,FOLLOW_DOTDOT_in_var_decl_item702); 
                    t=(Token)match(input,INT_LITERAL,FOLLOW_INT_LITERAL_in_var_decl_item706); 
                    match(input,RBOX,FOLLOW_RBOX_in_var_decl_item708); 
                    match(input,OF,FOLLOW_OF_in_var_decl_item710); 
                    pushFollow(FOLLOW_array_decl_tail_in_var_decl_item714);
                    adt=array_decl_tail();

                    state._fsp--;


                    		// CREATE AN ARRAY OF VARIABLES/PARAMETERS
                    		FZVariableBuilder.buildArray(Integer.valueOf(f.getText()), Integer.valueOf(t.getText()), adt);		
                    		

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "var_decl_item"


    // $ANTLR start "array_decl_tail"
    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:162:1: array_decl_tail returns [FZVariableBuilder.ArrayDecl adt] : (natet= non_array_ti_expr_tail COLON name= ident_anns EQUAL al= array_literal | VAR natet= non_array_ti_expr_tail COLON name= ident_anns ( EQUAL al= array_literal )? );
    public final FZVariableBuilder.ArrayDecl array_decl_tail() throws RecognitionException {
        FZVariableBuilder.ArrayDecl adt = null;

        FZVariableBuilder.VarType natet = null;

        String name = null;

        FZVariableBuilder.ValType al = null;


        try {
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:163:2: (natet= non_array_ti_expr_tail COLON name= ident_anns EQUAL al= array_literal | VAR natet= non_array_ti_expr_tail COLON name= ident_anns ( EQUAL al= array_literal )? )
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0==LB||LA10_0==BOOL||LA10_0==FLOAT||LA10_0==INT||LA10_0==SET||LA10_0==INT_LITERAL||LA10_0==FLOAT_LITERAL) ) {
                alt10=1;
            }
            else if ( (LA10_0==VAR) ) {
                alt10=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 10, 0, input);

                throw nvae;
            }
            switch (alt10) {
                case 1 :
                    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:163:4: natet= non_array_ti_expr_tail COLON name= ident_anns EQUAL al= array_literal
                    {
                    pushFollow(FOLLOW_non_array_ti_expr_tail_in_array_decl_tail736);
                    natet=non_array_ti_expr_tail();

                    state._fsp--;

                    match(input,COLON,FOLLOW_COLON_in_array_decl_tail738); 
                    pushFollow(FOLLOW_ident_anns_in_array_decl_tail742);
                    name=ident_anns();

                    state._fsp--;

                    match(input,EQUAL,FOLLOW_EQUAL_in_array_decl_tail744); 
                    pushFollow(FOLLOW_array_literal_in_array_decl_tail748);
                    al=array_literal();

                    state._fsp--;


                    		adt =FZVariableBuilder.build(natet, name, al,true);
                    		

                    }
                    break;
                case 2 :
                    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:167:5: VAR natet= non_array_ti_expr_tail COLON name= ident_anns ( EQUAL al= array_literal )?
                    {
                    match(input,VAR,FOLLOW_VAR_in_array_decl_tail758); 
                    pushFollow(FOLLOW_non_array_ti_expr_tail_in_array_decl_tail762);
                    natet=non_array_ti_expr_tail();

                    state._fsp--;

                    match(input,COLON,FOLLOW_COLON_in_array_decl_tail764); 
                    pushFollow(FOLLOW_ident_anns_in_array_decl_tail768);
                    name=ident_anns();

                    state._fsp--;

                    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:167:60: ( EQUAL al= array_literal )?
                    int alt9=2;
                    int LA9_0 = input.LA(1);

                    if ( (LA9_0==EQUAL) ) {
                        alt9=1;
                    }
                    switch (alt9) {
                        case 1 :
                            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:167:62: EQUAL al= array_literal
                            {
                            match(input,EQUAL,FOLLOW_EQUAL_in_array_decl_tail772); 
                            pushFollow(FOLLOW_array_literal_in_array_decl_tail776);
                            al=array_literal();

                            state._fsp--;


                            }
                            break;

                    }


                    		adt =FZVariableBuilder.build(natet, name, al, false);
                    		

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return adt;
    }
    // $ANTLR end "array_decl_tail"


    // $ANTLR start "ident_anns"
    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:171:1: ident_anns returns [String value] : IDENT annotations ;
    public final String ident_anns() throws RecognitionException {
        String value = null;

        Token IDENT1=null;

        try {
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:172:2: ( IDENT annotations )
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:172:4: IDENT annotations
            {
            IDENT1=(Token)match(input,IDENT,FOLLOW_IDENT_in_ident_anns795); 
            pushFollow(FOLLOW_annotations_in_ident_anns797);
            annotations();

            state._fsp--;


            		value = (IDENT1!=null?IDENT1.getText():null);
            		

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return value;
    }
    // $ANTLR end "ident_anns"


    // $ANTLR start "constraint_item"
    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:176:1: constraint_item : CONSTRAINT constraint_elem annotations ;
    public final void constraint_item() throws RecognitionException {
        try {
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:177:2: ( CONSTRAINT constraint_elem annotations )
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:177:4: CONSTRAINT constraint_elem annotations
            {
            match(input,CONSTRAINT,FOLLOW_CONSTRAINT_in_constraint_item811); 
            pushFollow(FOLLOW_constraint_elem_in_constraint_item813);
            constraint_elem();

            state._fsp--;

            pushFollow(FOLLOW_annotations_in_constraint_item815);
            annotations();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "constraint_item"


    // $ANTLR start "constraint_elem"
    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:178:1: constraint_elem : (name= IDENT LP e1= flat_expr ( COMA e2= flat_expr )* RP | variable_expr );
    public final void constraint_elem() throws RecognitionException {
        Token name=null;
        FZVariableBuilder.ValType e1 = null;

        FZVariableBuilder.ValType e2 = null;


        try {
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:179:2: (name= IDENT LP e1= flat_expr ( COMA e2= flat_expr )* RP | variable_expr )
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( (LA12_0==IDENT) ) {
                int LA12_1 = input.LA(2);

                if ( (LA12_1==COLONCOLON||LA12_1==SEMICOLON||LA12_1==LBOX) ) {
                    alt12=2;
                }
                else if ( (LA12_1==LP) ) {
                    alt12=1;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 12, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 12, 0, input);

                throw nvae;
            }
            switch (alt12) {
                case 1 :
                    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:179:4: name= IDENT LP e1= flat_expr ( COMA e2= flat_expr )* RP
                    {
                    name=(Token)match(input,IDENT,FOLLOW_IDENT_in_constraint_elem826); 
                    List<FZVariableBuilder.ValType> list = new ArrayList<FZVariableBuilder.ValType>();
                    match(input,LP,FOLLOW_LP_in_constraint_elem829); 
                    pushFollow(FOLLOW_flat_expr_in_constraint_elem833);
                    e1=flat_expr();

                    state._fsp--;

                    list.add(e1);
                    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:179:130: ( COMA e2= flat_expr )*
                    loop11:
                    do {
                        int alt11=2;
                        int LA11_0 = input.LA(1);

                        if ( (LA11_0==COMA) ) {
                            alt11=1;
                        }


                        switch (alt11) {
                    	case 1 :
                    	    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:179:131: COMA e2= flat_expr
                    	    {
                    	    match(input,COMA,FOLLOW_COMA_in_constraint_elem837); 
                    	    pushFollow(FOLLOW_flat_expr_in_constraint_elem841);
                    	    e2=flat_expr();

                    	    state._fsp--;

                    	    list.add(e2);

                    	    }
                    	    break;

                    	default :
                    	    break loop11;
                        }
                    } while (true);

                    match(input,RP,FOLLOW_RP_in_constraint_elem845); 

                    		FZVariableBuilder.ValType[] arrays = new FZVariableBuilder.ValType[list.size()];
                    	        list.toArray(arrays);
                    		FZConstraintBuilder.build(name.getText(), arrays);
                    		

                    }
                    break;
                case 2 :
                    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:185:5: variable_expr
                    {
                    pushFollow(FOLLOW_variable_expr_in_constraint_elem855);
                    variable_expr();

                    state._fsp--;

                    System.err.println("constraint_elem : not yet implemented");

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "constraint_elem"


    // $ANTLR start "solve_item"
    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:187:1: solve_item returns [Boolean isSatisifed] : SOLVE annotations solve_kind ;
    public final Boolean solve_item() throws RecognitionException {
        Boolean isSatisifed = null;

        try {
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:188:2: ( SOLVE annotations solve_kind )
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:188:4: SOLVE annotations solve_kind
            {

            		time[1] = System.currentTimeMillis();
            		solver.read(model);
            		solver.setTimeLimit(10000);
            		time[2] = System.currentTimeMillis();
            		
            match(input,SOLVE,FOLLOW_SOLVE_in_solve_item874); 
            pushFollow(FOLLOW_annotations_in_solve_item876);
            annotations();

            state._fsp--;

            pushFollow(FOLLOW_solve_kind_in_solve_item878);
            solve_kind();

            state._fsp--;


            		time[3] = System.currentTimeMillis();
            		

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return isSatisifed;
    }
    // $ANTLR end "solve_item"


    // $ANTLR start "solve_kind"
    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:198:1: solve_kind : ( SATISFY | MINIMIZE obj= solve_expr | MAXIMIZE obj= solve_expr );
    public final void solve_kind() throws RecognitionException {
        FZVariableBuilder.ValType obj = null;


        try {
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:199:2: ( SATISFY | MINIMIZE obj= solve_expr | MAXIMIZE obj= solve_expr )
            int alt13=3;
            switch ( input.LA(1) ) {
            case SATISFY:
                {
                alt13=1;
                }
                break;
            case MINIMIZE:
                {
                alt13=2;
                }
                break;
            case MAXIMIZE:
                {
                alt13=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 13, 0, input);

                throw nvae;
            }

            switch (alt13) {
                case 1 :
                    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:199:4: SATISFY
                    {
                    match(input,SATISFY,FOLLOW_SATISFY_in_solve_kind893); 
                    isFeasible = solver.solve();

                    }
                    break;
                case 2 :
                    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:201:5: MINIMIZE obj= solve_expr
                    {
                    match(input,MINIMIZE,FOLLOW_MINIMIZE_in_solve_kind906); 
                    pushFollow(FOLLOW_solve_expr_in_solve_kind910);
                    obj=solve_expr();

                    state._fsp--;


                    		IntegerVariable objective = (IntegerVariable)memory.get((String)obj.obj);
                    		isFeasible = solver.minimize(solver.getVar(objective), true);
                    		

                    }
                    break;
                case 3 :
                    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:206:5: MAXIMIZE obj= solve_expr
                    {
                    match(input,MAXIMIZE,FOLLOW_MAXIMIZE_in_solve_kind922); 
                    pushFollow(FOLLOW_solve_expr_in_solve_kind926);
                    obj=solve_expr();

                    state._fsp--;


                    		IntegerVariable objective = (IntegerVariable)memory.get((String)obj.obj);
                    		isFeasible = solver.maximize(solver.getVar(objective), true);
                    		

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "solve_kind"


    // $ANTLR start "output_item"
    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:211:1: output_item : OUTPUT LBOX output_elem ( COMA output_elem )* RBOX ;
    public final void output_item() throws RecognitionException {
        try {
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:212:2: ( OUTPUT LBOX output_elem ( COMA output_elem )* RBOX )
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:212:4: OUTPUT LBOX output_elem ( COMA output_elem )* RBOX
            {
            match(input,OUTPUT,FOLLOW_OUTPUT_in_output_item942); 
            match(input,LBOX,FOLLOW_LBOX_in_output_item944); 
            pushFollow(FOLLOW_output_elem_in_output_item946);
            output_elem();

            state._fsp--;

            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:212:28: ( COMA output_elem )*
            loop14:
            do {
                int alt14=2;
                int LA14_0 = input.LA(1);

                if ( (LA14_0==COMA) ) {
                    alt14=1;
                }


                switch (alt14) {
            	case 1 :
            	    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:212:29: COMA output_elem
            	    {
            	    match(input,COMA,FOLLOW_COMA_in_output_item949); 
            	    pushFollow(FOLLOW_output_elem_in_output_item951);
            	    output_elem();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop14;
                }
            } while (true);

            match(input,RBOX,FOLLOW_RBOX_in_output_item955); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "output_item"


    // $ANTLR start "output_elem"
    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:213:1: output_elem : ( SHOW LP flat_expr RP | SHOWCOND LP flat_expr COMA flat_expr COMA flat_expr RP | STRING_LITERAL );
    public final void output_elem() throws RecognitionException {
        try {
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:214:2: ( SHOW LP flat_expr RP | SHOWCOND LP flat_expr COMA flat_expr COMA flat_expr RP | STRING_LITERAL )
            int alt15=3;
            switch ( input.LA(1) ) {
            case SHOW:
                {
                alt15=1;
                }
                break;
            case SHOWCOND:
                {
                alt15=2;
                }
                break;
            case STRING_LITERAL:
                {
                alt15=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 15, 0, input);

                throw nvae;
            }

            switch (alt15) {
                case 1 :
                    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:214:4: SHOW LP flat_expr RP
                    {
                    match(input,SHOW,FOLLOW_SHOW_in_output_elem964); 
                    match(input,LP,FOLLOW_LP_in_output_elem966); 
                    pushFollow(FOLLOW_flat_expr_in_output_elem968);
                    flat_expr();

                    state._fsp--;

                    match(input,RP,FOLLOW_RP_in_output_elem970); 

                    }
                    break;
                case 2 :
                    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:215:5: SHOWCOND LP flat_expr COMA flat_expr COMA flat_expr RP
                    {
                    match(input,SHOWCOND,FOLLOW_SHOWCOND_in_output_elem976); 
                    match(input,LP,FOLLOW_LP_in_output_elem978); 
                    pushFollow(FOLLOW_flat_expr_in_output_elem980);
                    flat_expr();

                    state._fsp--;

                    match(input,COMA,FOLLOW_COMA_in_output_elem982); 
                    pushFollow(FOLLOW_flat_expr_in_output_elem984);
                    flat_expr();

                    state._fsp--;

                    match(input,COMA,FOLLOW_COMA_in_output_elem986); 
                    pushFollow(FOLLOW_flat_expr_in_output_elem988);
                    flat_expr();

                    state._fsp--;

                    match(input,RP,FOLLOW_RP_in_output_elem990); 

                    }
                    break;
                case 3 :
                    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:216:5: STRING_LITERAL
                    {
                    match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_output_elem996); 

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "output_elem"


    // $ANTLR start "non_array_ti_expr_tail"
    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:219:1: non_array_ti_expr_tail returns [FZVariableBuilder.VarType vt] : (sc= scalar_ti_expr_tail | se= set_ti_expr_tail );
    public final FZVariableBuilder.VarType non_array_ti_expr_tail() throws RecognitionException {
        FZVariableBuilder.VarType vt = null;

        FZVariableBuilder.VarType sc = null;

        FZVariableBuilder.VarType se = null;


        try {
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:220:2: (sc= scalar_ti_expr_tail | se= set_ti_expr_tail )
            int alt16=2;
            int LA16_0 = input.LA(1);

            if ( (LA16_0==LB||LA16_0==BOOL||LA16_0==FLOAT||LA16_0==INT||LA16_0==INT_LITERAL||LA16_0==FLOAT_LITERAL) ) {
                alt16=1;
            }
            else if ( (LA16_0==SET) ) {
                alt16=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 16, 0, input);

                throw nvae;
            }
            switch (alt16) {
                case 1 :
                    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:220:4: sc= scalar_ti_expr_tail
                    {
                    pushFollow(FOLLOW_scalar_ti_expr_tail_in_non_array_ti_expr_tail1012);
                    sc=scalar_ti_expr_tail();

                    state._fsp--;


                    		vt =sc;
                    		

                    }
                    break;
                case 2 :
                    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:224:4: se= set_ti_expr_tail
                    {
                    pushFollow(FOLLOW_set_ti_expr_tail_in_non_array_ti_expr_tail1023);
                    se=set_ti_expr_tail();

                    state._fsp--;


                    		vt = se;
                    		

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return vt;
    }
    // $ANTLR end "non_array_ti_expr_tail"


    // $ANTLR start "bool_ti_expr_tail"
    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:230:1: bool_ti_expr_tail returns [FZVariableBuilder.VarType vt] : BOOL ;
    public final FZVariableBuilder.VarType bool_ti_expr_tail() throws RecognitionException {
        FZVariableBuilder.VarType vt = null;

        try {
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:231:2: ( BOOL )
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:231:4: BOOL
            {
            match(input,BOOL,FOLLOW_BOOL_in_bool_ti_expr_tail1043); 

            		vt = FZVariableBuilder.build(FZVariableBuilder.EnumVar.bBool, null);
            		

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return vt;
    }
    // $ANTLR end "bool_ti_expr_tail"


    // $ANTLR start "scalar_ti_expr_tail"
    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:235:1: scalar_ti_expr_tail returns [FZVariableBuilder.VarType vt] : (b= bool_ti_expr_tail | i= int_ti_expr_tail | f= float_ti_expr_tail );
    public final FZVariableBuilder.VarType scalar_ti_expr_tail() throws RecognitionException {
        FZVariableBuilder.VarType vt = null;

        FZVariableBuilder.VarType b = null;

        FZVariableBuilder.VarType i = null;

        FZVariableBuilder.VarType f = null;


        try {
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:236:2: (b= bool_ti_expr_tail | i= int_ti_expr_tail | f= float_ti_expr_tail )
            int alt17=3;
            switch ( input.LA(1) ) {
            case BOOL:
                {
                alt17=1;
                }
                break;
            case LB:
            case INT:
            case INT_LITERAL:
                {
                alt17=2;
                }
                break;
            case FLOAT:
            case FLOAT_LITERAL:
                {
                alt17=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 17, 0, input);

                throw nvae;
            }

            switch (alt17) {
                case 1 :
                    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:236:4: b= bool_ti_expr_tail
                    {
                    pushFollow(FOLLOW_bool_ti_expr_tail_in_scalar_ti_expr_tail1062);
                    b=bool_ti_expr_tail();

                    state._fsp--;


                    		vt = b;
                    		

                    }
                    break;
                case 2 :
                    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:240:4: i= int_ti_expr_tail
                    {
                    pushFollow(FOLLOW_int_ti_expr_tail_in_scalar_ti_expr_tail1073);
                    i=int_ti_expr_tail();

                    state._fsp--;


                    		vt =i;
                    		

                    }
                    break;
                case 3 :
                    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:244:4: f= float_ti_expr_tail
                    {
                    pushFollow(FOLLOW_float_ti_expr_tail_in_scalar_ti_expr_tail1084);
                    f=float_ti_expr_tail();

                    state._fsp--;


                    		vt =f;
                    		

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return vt;
    }
    // $ANTLR end "scalar_ti_expr_tail"


    // $ANTLR start "int_ti_expr_tail"
    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:249:1: int_ti_expr_tail returns [FZVariableBuilder.VarType vt] : ( INT | i1= INT_LITERAL DOTDOT i2= INT_LITERAL | LB e1= INT_LITERAL ( COMA e2= INT_LITERAL )* RB );
    public final FZVariableBuilder.VarType int_ti_expr_tail() throws RecognitionException {
        FZVariableBuilder.VarType vt = null;

        Token i1=null;
        Token i2=null;
        Token e1=null;
        Token e2=null;

        try {
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:250:2: ( INT | i1= INT_LITERAL DOTDOT i2= INT_LITERAL | LB e1= INT_LITERAL ( COMA e2= INT_LITERAL )* RB )
            int alt19=3;
            switch ( input.LA(1) ) {
            case INT:
                {
                alt19=1;
                }
                break;
            case INT_LITERAL:
                {
                alt19=2;
                }
                break;
            case LB:
                {
                alt19=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 19, 0, input);

                throw nvae;
            }

            switch (alt19) {
                case 1 :
                    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:250:4: INT
                    {
                    match(input,INT,FOLLOW_INT_in_int_ti_expr_tail1103); 

                    		vt = FZVariableBuilder.build(FZVariableBuilder.EnumVar.iInt, null);
                    		

                    }
                    break;
                case 2 :
                    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:254:5: i1= INT_LITERAL DOTDOT i2= INT_LITERAL
                    {
                    i1=(Token)match(input,INT_LITERAL,FOLLOW_INT_LITERAL_in_int_ti_expr_tail1115); 
                    match(input,DOTDOT,FOLLOW_DOTDOT_in_int_ti_expr_tail1117); 
                    i2=(Token)match(input,INT_LITERAL,FOLLOW_INT_LITERAL_in_int_ti_expr_tail1121); 

                    		int s=Integer.valueOf((i1!=null?i1.getText():null));
                    		int e=Integer.valueOf((i2!=null?i2.getText():null));
                    		vt = FZVariableBuilder.build(FZVariableBuilder.EnumVar.iBounds, new int[]{s,e});
                    		

                    }
                    break;
                case 3 :
                    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:260:5: LB e1= INT_LITERAL ( COMA e2= INT_LITERAL )* RB
                    {
                    List<Integer> values = new ArrayList<Integer>();
                    match(input,LB,FOLLOW_LB_in_int_ti_expr_tail1134); 
                    e1=(Token)match(input,INT_LITERAL,FOLLOW_INT_LITERAL_in_int_ti_expr_tail1138); 
                    values.add(Integer.valueOf((e1!=null?e1.getText():null)));
                    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:260:113: ( COMA e2= INT_LITERAL )*
                    loop18:
                    do {
                        int alt18=2;
                        int LA18_0 = input.LA(1);

                        if ( (LA18_0==COMA) ) {
                            alt18=1;
                        }


                        switch (alt18) {
                    	case 1 :
                    	    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:260:114: COMA e2= INT_LITERAL
                    	    {
                    	    match(input,COMA,FOLLOW_COMA_in_int_ti_expr_tail1142); 
                    	    e2=(Token)match(input,INT_LITERAL,FOLLOW_INT_LITERAL_in_int_ti_expr_tail1146); 
                    	    values.add(Integer.valueOf((e2!=null?e2.getText():null)));

                    	    }
                    	    break;

                    	default :
                    	    break loop18;
                        }
                    } while (true);

                    match(input,RB,FOLLOW_RB_in_int_ti_expr_tail1151); 

                    		vt = FZVariableBuilder.build(FZVariableBuilder.EnumVar.iValues, values.toArray(new Integer[values.size()]));
                    		

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return vt;
    }
    // $ANTLR end "int_ti_expr_tail"


    // $ANTLR start "float_ti_expr_tail"
    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:264:1: float_ti_expr_tail returns [FZVariableBuilder.VarType vt] : ( FLOAT | f1= FLOAT_LITERAL DOTDOT f2= FLOAT_LITERAL );
    public final FZVariableBuilder.VarType float_ti_expr_tail() throws RecognitionException {
        FZVariableBuilder.VarType vt = null;

        Token f1=null;
        Token f2=null;

        try {
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:265:2: ( FLOAT | f1= FLOAT_LITERAL DOTDOT f2= FLOAT_LITERAL )
            int alt20=2;
            int LA20_0 = input.LA(1);

            if ( (LA20_0==FLOAT) ) {
                alt20=1;
            }
            else if ( (LA20_0==FLOAT_LITERAL) ) {
                alt20=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 20, 0, input);

                throw nvae;
            }
            switch (alt20) {
                case 1 :
                    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:265:4: FLOAT
                    {
                    match(input,FLOAT,FOLLOW_FLOAT_in_float_ti_expr_tail1167); 

                    		vt = FZVariableBuilder.build(FZVariableBuilder.EnumVar.fFloat, null);
                    		

                    }
                    break;
                case 2 :
                    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:269:5: f1= FLOAT_LITERAL DOTDOT f2= FLOAT_LITERAL
                    {
                    f1=(Token)match(input,FLOAT_LITERAL,FOLLOW_FLOAT_LITERAL_in_float_ti_expr_tail1179); 
                    match(input,DOTDOT,FOLLOW_DOTDOT_in_float_ti_expr_tail1181); 
                    f2=(Token)match(input,FLOAT_LITERAL,FOLLOW_FLOAT_LITERAL_in_float_ti_expr_tail1185); 

                    		double s=Double.valueOf((f1!=null?f1.getText():null));
                    		double e=Double.valueOf((f2!=null?f2.getText():null));
                    		vt = FZVariableBuilder.build(FZVariableBuilder.EnumVar.iBounds, new double[]{s,e});
                    		

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return vt;
    }
    // $ANTLR end "float_ti_expr_tail"


    // $ANTLR start "set_ti_expr_tail"
    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:275:1: set_ti_expr_tail returns [FZVariableBuilder.VarType vt] : SET OF so= scalar_ti_expr_tail ;
    public final FZVariableBuilder.VarType set_ti_expr_tail() throws RecognitionException {
        FZVariableBuilder.VarType vt = null;

        FZVariableBuilder.VarType so = null;


        try {
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:276:2: ( SET OF so= scalar_ti_expr_tail )
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:276:4: SET OF so= scalar_ti_expr_tail
            {
            match(input,SET,FOLLOW_SET_in_set_ti_expr_tail1200); 
            match(input,OF,FOLLOW_OF_in_set_ti_expr_tail1202); 
            pushFollow(FOLLOW_scalar_ti_expr_tail_in_set_ti_expr_tail1206);
            so=scalar_ti_expr_tail();

            state._fsp--;


            		vt = FZVariableBuilder.build(FZVariableBuilder.EnumVar.setOf, so);
            		

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return vt;
    }
    // $ANTLR end "set_ti_expr_tail"


    // $ANTLR start "non_array_flat_expr"
    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:282:1: non_array_flat_expr returns [FZVariableBuilder.ValType nafe] : (sfe= scalar_flat_expr | sl= set_literal );
    public final FZVariableBuilder.ValType non_array_flat_expr() throws RecognitionException {
        FZVariableBuilder.ValType nafe = null;

        FZVariableBuilder.ValType sfe = null;

        FZVariableBuilder.ValType sl = null;


        try {
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:283:2: (sfe= scalar_flat_expr | sl= set_literal )
            int alt21=2;
            switch ( input.LA(1) ) {
            case IDENT:
                {
                switch ( input.LA(2) ) {
                case LBOX:
                    {
                    int LA21_5 = input.LA(3);

                    if ( (LA21_5==INT_LITERAL) ) {
                        int LA21_6 = input.LA(4);

                        if ( (LA21_6==RBOX) ) {
                            int LA21_8 = input.LA(5);

                            if ( (LA21_8==COMA||LA21_8==SEMICOLON||(LA21_8>=RP && LA21_8<=RBOX)) ) {
                                alt21=1;
                            }
                            else if ( (LA21_8==DOTDOT) ) {
                                alt21=2;
                            }
                            else {
                                NoViableAltException nvae =
                                    new NoViableAltException("", 21, 8, input);

                                throw nvae;
                            }
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("", 21, 6, input);

                            throw nvae;
                        }
                    }
                    else if ( (LA21_5==IDENT) ) {
                        int LA21_7 = input.LA(4);

                        if ( (LA21_7==RBOX) ) {
                            int LA21_8 = input.LA(5);

                            if ( (LA21_8==COMA||LA21_8==SEMICOLON||(LA21_8>=RP && LA21_8<=RBOX)) ) {
                                alt21=1;
                            }
                            else if ( (LA21_8==DOTDOT) ) {
                                alt21=2;
                            }
                            else {
                                NoViableAltException nvae =
                                    new NoViableAltException("", 21, 8, input);

                                throw nvae;
                            }
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("", 21, 7, input);

                            throw nvae;
                        }
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 21, 5, input);

                        throw nvae;
                    }
                    }
                    break;
                case DOTDOT:
                    {
                    alt21=2;
                    }
                    break;
                case COMA:
                case SEMICOLON:
                case RP:
                case RBOX:
                    {
                    alt21=1;
                    }
                    break;
                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 21, 1, input);

                    throw nvae;
                }

                }
                break;
            case FALSE:
            case TRUE:
            case STRING_LITERAL:
            case FLOAT_LITERAL:
                {
                alt21=1;
                }
                break;
            case INT_LITERAL:
                {
                int LA21_3 = input.LA(2);

                if ( (LA21_3==DOTDOT) ) {
                    alt21=2;
                }
                else if ( (LA21_3==COMA||LA21_3==SEMICOLON||(LA21_3>=RP && LA21_3<=RBOX)) ) {
                    alt21=1;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 21, 3, input);

                    throw nvae;
                }
                }
                break;
            case LB:
                {
                alt21=2;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 21, 0, input);

                throw nvae;
            }

            switch (alt21) {
                case 1 :
                    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:283:4: sfe= scalar_flat_expr
                    {
                    pushFollow(FOLLOW_scalar_flat_expr_in_non_array_flat_expr1226);
                    sfe=scalar_flat_expr();

                    state._fsp--;


                    		nafe =sfe;
                    		

                    }
                    break;
                case 2 :
                    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:287:5: sl= set_literal
                    {
                    pushFollow(FOLLOW_set_literal_in_non_array_flat_expr1238);
                    sl=set_literal();

                    state._fsp--;


                    		nafe =sl;
                    		

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return nafe;
    }
    // $ANTLR end "non_array_flat_expr"


    // $ANTLR start "scalar_flat_expr"
    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:291:1: scalar_flat_expr returns [FZVariableBuilder.ValType sfe] : ( IDENT | aae= array_access_expr | bl= bool_literal | INT_LITERAL | FLOAT_LITERAL | STRING_LITERAL );
    public final FZVariableBuilder.ValType scalar_flat_expr() throws RecognitionException {
        FZVariableBuilder.ValType sfe = null;

        Token IDENT2=null;
        Token INT_LITERAL3=null;
        Token FLOAT_LITERAL4=null;
        Token STRING_LITERAL5=null;
        FZVariableBuilder.ValType aae = null;

        boolean bl = false;


        try {
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:292:2: ( IDENT | aae= array_access_expr | bl= bool_literal | INT_LITERAL | FLOAT_LITERAL | STRING_LITERAL )
            int alt22=6;
            switch ( input.LA(1) ) {
            case IDENT:
                {
                int LA22_1 = input.LA(2);

                if ( (LA22_1==LBOX) ) {
                    alt22=2;
                }
                else if ( (LA22_1==COMA||LA22_1==SEMICOLON||(LA22_1>=RP && LA22_1<=RBOX)||LA22_1==RB) ) {
                    alt22=1;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 22, 1, input);

                    throw nvae;
                }
                }
                break;
            case FALSE:
            case TRUE:
                {
                alt22=3;
                }
                break;
            case INT_LITERAL:
                {
                alt22=4;
                }
                break;
            case FLOAT_LITERAL:
                {
                alt22=5;
                }
                break;
            case STRING_LITERAL:
                {
                alt22=6;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 22, 0, input);

                throw nvae;
            }

            switch (alt22) {
                case 1 :
                    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:292:5: IDENT
                    {
                    IDENT2=(Token)match(input,IDENT,FOLLOW_IDENT_in_scalar_flat_expr1255); 

                    		Object val = memory.get((IDENT2!=null?IDENT2.getText():null));
                    		sfe =FZVariableBuilder.build(FZVariableBuilder.getType(val), val);
                    		

                    }
                    break;
                case 2 :
                    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:297:5: aae= array_access_expr
                    {
                    pushFollow(FOLLOW_array_access_expr_in_scalar_flat_expr1267);
                    aae=array_access_expr();

                    state._fsp--;


                    		sfe =aae;
                    		

                    }
                    break;
                case 3 :
                    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:301:5: bl= bool_literal
                    {
                    pushFollow(FOLLOW_bool_literal_in_scalar_flat_expr1279);
                    bl=bool_literal();

                    state._fsp--;


                    		sfe =FZVariableBuilder.build(FZVariableBuilder.EnumVal.bBool, bl);
                    		

                    }
                    break;
                case 4 :
                    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:305:5: INT_LITERAL
                    {
                    INT_LITERAL3=(Token)match(input,INT_LITERAL,FOLLOW_INT_LITERAL_in_scalar_flat_expr1289); 

                    		sfe =FZVariableBuilder.build(FZVariableBuilder.EnumVal.iInt, new Integer((INT_LITERAL3!=null?INT_LITERAL3.getText():null)));
                    		

                    }
                    break;
                case 5 :
                    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:309:5: FLOAT_LITERAL
                    {
                    FLOAT_LITERAL4=(Token)match(input,FLOAT_LITERAL,FOLLOW_FLOAT_LITERAL_in_scalar_flat_expr1299); 

                    		sfe =FZVariableBuilder.build(FZVariableBuilder.EnumVal.fFloat, new Double((FLOAT_LITERAL4!=null?FLOAT_LITERAL4.getText():null)));
                    		

                    }
                    break;
                case 6 :
                    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:313:5: STRING_LITERAL
                    {
                    STRING_LITERAL5=(Token)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_scalar_flat_expr1309); 

                    		sfe =FZVariableBuilder.build(FZVariableBuilder.EnumVal.sString, (STRING_LITERAL5!=null?STRING_LITERAL5.getText():null));
                    		

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return sfe;
    }
    // $ANTLR end "scalar_flat_expr"


    // $ANTLR start "int_flat_expr"
    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:317:1: int_flat_expr returns [FZVariableBuilder.ValType ife] : (aae= array_access_expr | IDENT | INT_LITERAL );
    public final FZVariableBuilder.ValType int_flat_expr() throws RecognitionException {
        FZVariableBuilder.ValType ife = null;

        Token IDENT6=null;
        Token INT_LITERAL7=null;
        FZVariableBuilder.ValType aae = null;


        try {
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:318:2: (aae= array_access_expr | IDENT | INT_LITERAL )
            int alt23=3;
            int LA23_0 = input.LA(1);

            if ( (LA23_0==IDENT) ) {
                int LA23_1 = input.LA(2);

                if ( (LA23_1==LBOX) ) {
                    alt23=1;
                }
                else if ( ((LA23_1>=COMA && LA23_1<=DOTDOT)||LA23_1==SEMICOLON||(LA23_1>=RP && LA23_1<=RBOX)) ) {
                    alt23=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 23, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA23_0==INT_LITERAL) ) {
                alt23=3;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 23, 0, input);

                throw nvae;
            }
            switch (alt23) {
                case 1 :
                    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:318:5: aae= array_access_expr
                    {
                    pushFollow(FOLLOW_array_access_expr_in_int_flat_expr1329);
                    aae=array_access_expr();

                    state._fsp--;


                    		ife =aae;
                    		

                    }
                    break;
                case 2 :
                    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:322:4: IDENT
                    {
                    IDENT6=(Token)match(input,IDENT,FOLLOW_IDENT_in_int_flat_expr1338); 

                    		ife =FZVariableBuilder.build(FZVariableBuilder.EnumVal.sString, (IDENT6!=null?IDENT6.getText():null));
                    		

                    }
                    break;
                case 3 :
                    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:326:5: INT_LITERAL
                    {
                    INT_LITERAL7=(Token)match(input,INT_LITERAL,FOLLOW_INT_LITERAL_in_int_flat_expr1348); 

                    		ife =FZVariableBuilder.build(FZVariableBuilder.EnumVal.iInt, (INT_LITERAL7!=null?INT_LITERAL7.getText():null));
                    		

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ife;
    }
    // $ANTLR end "int_flat_expr"


    // $ANTLR start "variable_expr"
    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:330:1: variable_expr returns [FZVariableBuilder.ValType ve] : ( IDENT | aae= array_access_expr );
    public final FZVariableBuilder.ValType variable_expr() throws RecognitionException {
        FZVariableBuilder.ValType ve = null;

        Token IDENT8=null;
        FZVariableBuilder.ValType aae = null;


        try {
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:331:2: ( IDENT | aae= array_access_expr )
            int alt24=2;
            int LA24_0 = input.LA(1);

            if ( (LA24_0==IDENT) ) {
                int LA24_1 = input.LA(2);

                if ( (LA24_1==LBOX) ) {
                    alt24=2;
                }
                else if ( (LA24_1==COLONCOLON||LA24_1==SEMICOLON) ) {
                    alt24=1;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 24, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 24, 0, input);

                throw nvae;
            }
            switch (alt24) {
                case 1 :
                    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:331:4: IDENT
                    {
                    IDENT8=(Token)match(input,IDENT,FOLLOW_IDENT_in_variable_expr1364); 

                    		ve =FZVariableBuilder.build(FZVariableBuilder.EnumVal.sString, (IDENT8!=null?IDENT8.getText():null));
                    		

                    }
                    break;
                case 2 :
                    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:335:5: aae= array_access_expr
                    {
                    pushFollow(FOLLOW_array_access_expr_in_variable_expr1376);
                    aae=array_access_expr();

                    state._fsp--;


                    		ve =aae;
                    		

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ve;
    }
    // $ANTLR end "variable_expr"


    // $ANTLR start "array_access_expr"
    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:339:1: array_access_expr returns [FZVariableBuilder.ValType aae] : IDENT LBOX i= int_index_expr RBOX ;
    public final FZVariableBuilder.ValType array_access_expr() throws RecognitionException {
        FZVariableBuilder.ValType aae = null;

        Token IDENT9=null;
        int i = 0;


        try {
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:340:2: ( IDENT LBOX i= int_index_expr RBOX )
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:340:4: IDENT LBOX i= int_index_expr RBOX
            {
            IDENT9=(Token)match(input,IDENT,FOLLOW_IDENT_in_array_access_expr1392); 
            match(input,LBOX,FOLLOW_LBOX_in_array_access_expr1394); 
            pushFollow(FOLLOW_int_index_expr_in_array_access_expr1398);
            i=int_index_expr();

            state._fsp--;

            match(input,RBOX,FOLLOW_RBOX_in_array_access_expr1400); 

            		// build name
            		String name = (IDENT9!=null?IDENT9.getText():null)+FZVariableBuilder.NAME_SEPARATOR+i;
            		Object val = memory.get(name);
            		aae = FZVariableBuilder.build(FZVariableBuilder.getType(val), val);
            		

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return aae;
    }
    // $ANTLR end "array_access_expr"


    // $ANTLR start "int_index_expr"
    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:347:1: int_index_expr returns [int value] : ( INT_LITERAL | IDENT );
    public final int int_index_expr() throws RecognitionException {
        int value = 0;

        Token INT_LITERAL10=null;
        Token IDENT11=null;

        try {
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:348:2: ( INT_LITERAL | IDENT )
            int alt25=2;
            int LA25_0 = input.LA(1);

            if ( (LA25_0==INT_LITERAL) ) {
                alt25=1;
            }
            else if ( (LA25_0==IDENT) ) {
                alt25=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 25, 0, input);

                throw nvae;
            }
            switch (alt25) {
                case 1 :
                    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:348:4: INT_LITERAL
                    {
                    INT_LITERAL10=(Token)match(input,INT_LITERAL,FOLLOW_INT_LITERAL_in_int_index_expr1416); 

                    		value = Integer.parseInt((INT_LITERAL10!=null?INT_LITERAL10.getText():null));
                    		

                    }
                    break;
                case 2 :
                    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:352:5: IDENT
                    {
                    IDENT11=(Token)match(input,IDENT,FOLLOW_IDENT_in_int_index_expr1428); 

                    		value = (Integer)memory.get((IDENT11!=null?IDENT11.getText():null));
                    		

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return value;
    }
    // $ANTLR end "int_index_expr"


    // $ANTLR start "bool_literal"
    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:356:1: bool_literal returns [boolean value] : ( FALSE | TRUE );
    public final boolean bool_literal() throws RecognitionException {
        boolean value = false;

        try {
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:357:2: ( FALSE | TRUE )
            int alt26=2;
            int LA26_0 = input.LA(1);

            if ( (LA26_0==FALSE) ) {
                alt26=1;
            }
            else if ( (LA26_0==TRUE) ) {
                alt26=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 26, 0, input);

                throw nvae;
            }
            switch (alt26) {
                case 1 :
                    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:357:4: FALSE
                    {
                    match(input,FALSE,FOLLOW_FALSE_in_bool_literal1447); 

                    		value = false;
                    		

                    }
                    break;
                case 2 :
                    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:361:5: TRUE
                    {
                    match(input,TRUE,FOLLOW_TRUE_in_bool_literal1458); 

                    		value = true;
                    		

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return value;
    }
    // $ANTLR end "bool_literal"


    // $ANTLR start "set_literal"
    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:366:1: set_literal returns [FZVariableBuilder.ValType sl] : ( LB (sfe1= scalar_flat_expr ( COMA sfe2= scalar_flat_expr )* )? RB | i1= int_flat_expr DOTDOT i2= int_flat_expr );
    public final FZVariableBuilder.ValType set_literal() throws RecognitionException {
        FZVariableBuilder.ValType sl = null;

        FZVariableBuilder.ValType sfe1 = null;

        FZVariableBuilder.ValType sfe2 = null;

        FZVariableBuilder.ValType i1 = null;

        FZVariableBuilder.ValType i2 = null;


        try {
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:367:2: ( LB (sfe1= scalar_flat_expr ( COMA sfe2= scalar_flat_expr )* )? RB | i1= int_flat_expr DOTDOT i2= int_flat_expr )
            int alt29=2;
            int LA29_0 = input.LA(1);

            if ( (LA29_0==LB) ) {
                alt29=1;
            }
            else if ( ((LA29_0>=IDENT && LA29_0<=INT_LITERAL)) ) {
                alt29=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 29, 0, input);

                throw nvae;
            }
            switch (alt29) {
                case 1 :
                    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:367:4: LB (sfe1= scalar_flat_expr ( COMA sfe2= scalar_flat_expr )* )? RB
                    {
                    match(input,LB,FOLLOW_LB_in_set_literal1480); 
                    List<FZVariableBuilder.ValType> list = new ArrayList<FZVariableBuilder.ValType>();
                    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:367:91: (sfe1= scalar_flat_expr ( COMA sfe2= scalar_flat_expr )* )?
                    int alt28=2;
                    int LA28_0 = input.LA(1);

                    if ( ((LA28_0>=FALSE && LA28_0<=TRUE)||(LA28_0>=IDENT && LA28_0<=FLOAT_LITERAL)) ) {
                        alt28=1;
                    }
                    switch (alt28) {
                        case 1 :
                            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:367:92: sfe1= scalar_flat_expr ( COMA sfe2= scalar_flat_expr )*
                            {
                            pushFollow(FOLLOW_scalar_flat_expr_in_set_literal1486);
                            sfe1=scalar_flat_expr();

                            state._fsp--;

                            list.add(sfe1);
                            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:367:131: ( COMA sfe2= scalar_flat_expr )*
                            loop27:
                            do {
                                int alt27=2;
                                int LA27_0 = input.LA(1);

                                if ( (LA27_0==COMA) ) {
                                    alt27=1;
                                }


                                switch (alt27) {
                            	case 1 :
                            	    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:367:132: COMA sfe2= scalar_flat_expr
                            	    {
                            	    match(input,COMA,FOLLOW_COMA_in_set_literal1490); 
                            	    pushFollow(FOLLOW_scalar_flat_expr_in_set_literal1494);
                            	    sfe2=scalar_flat_expr();

                            	    state._fsp--;

                            	    list.add(sfe2);

                            	    }
                            	    break;

                            	default :
                            	    break loop27;
                                }
                            } while (true);


                            }
                            break;

                    }

                    match(input,RB,FOLLOW_RB_in_set_literal1501); 

                    		FZVariableBuilder.ValType[] array = new FZVariableBuilder.ValType[list.size()];
                    		list.toArray(array);
                    		sl =FZVariableBuilder.build(FZVariableBuilder.EnumVal.array, array);
                    		

                    }
                    break;
                case 2 :
                    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:373:5: i1= int_flat_expr DOTDOT i2= int_flat_expr
                    {
                    pushFollow(FOLLOW_int_flat_expr_in_set_literal1513);
                    i1=int_flat_expr();

                    state._fsp--;

                    match(input,DOTDOT,FOLLOW_DOTDOT_in_set_literal1515); 
                    pushFollow(FOLLOW_int_flat_expr_in_set_literal1519);
                    i2=int_flat_expr();

                    state._fsp--;


                    		sl =FZVariableBuilder.build(FZVariableBuilder.EnumVal.interval, new FZVariableBuilder.ValType[]{i1,i2});
                    		

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return sl;
    }
    // $ANTLR end "set_literal"


    // $ANTLR start "array_literal"
    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:377:1: array_literal returns [FZVariableBuilder.ValType al] : LBOX (nafe1= non_array_flat_expr ( COMA nafe2= non_array_flat_expr )* )? RBOX ;
    public final FZVariableBuilder.ValType array_literal() throws RecognitionException {
        FZVariableBuilder.ValType al = null;

        FZVariableBuilder.ValType nafe1 = null;

        FZVariableBuilder.ValType nafe2 = null;


        try {
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:378:2: ( LBOX (nafe1= non_array_flat_expr ( COMA nafe2= non_array_flat_expr )* )? RBOX )
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:378:4: LBOX (nafe1= non_array_flat_expr ( COMA nafe2= non_array_flat_expr )* )? RBOX
            {
            match(input,LBOX,FOLLOW_LBOX_in_array_literal1536); 
            List<FZVariableBuilder.ValType> list = new ArrayList<FZVariableBuilder.ValType>();
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:378:93: (nafe1= non_array_flat_expr ( COMA nafe2= non_array_flat_expr )* )?
            int alt31=2;
            int LA31_0 = input.LA(1);

            if ( (LA31_0==LB||(LA31_0>=FALSE && LA31_0<=TRUE)||(LA31_0>=IDENT && LA31_0<=FLOAT_LITERAL)) ) {
                alt31=1;
            }
            switch (alt31) {
                case 1 :
                    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:378:94: nafe1= non_array_flat_expr ( COMA nafe2= non_array_flat_expr )*
                    {
                    pushFollow(FOLLOW_non_array_flat_expr_in_array_literal1542);
                    nafe1=non_array_flat_expr();

                    state._fsp--;

                    list.add(nafe1);
                    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:378:138: ( COMA nafe2= non_array_flat_expr )*
                    loop30:
                    do {
                        int alt30=2;
                        int LA30_0 = input.LA(1);

                        if ( (LA30_0==COMA) ) {
                            alt30=1;
                        }


                        switch (alt30) {
                    	case 1 :
                    	    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:378:139: COMA nafe2= non_array_flat_expr
                    	    {
                    	    match(input,COMA,FOLLOW_COMA_in_array_literal1546); 
                    	    pushFollow(FOLLOW_non_array_flat_expr_in_array_literal1550);
                    	    nafe2=non_array_flat_expr();

                    	    state._fsp--;

                    	    list.add(nafe2);

                    	    }
                    	    break;

                    	default :
                    	    break loop30;
                        }
                    } while (true);


                    }
                    break;

            }

            match(input,RBOX,FOLLOW_RBOX_in_array_literal1559); 

            		FZVariableBuilder.ValType[] array = new FZVariableBuilder.ValType[list.size()];
            		list.toArray(array);
            		al =FZVariableBuilder.build(FZVariableBuilder.EnumVal.array, array);
            		

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return al;
    }
    // $ANTLR end "array_literal"


    // $ANTLR start "annotations"
    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:385:1: annotations : ( COLONCOLON annotation )* ;
    public final void annotations() throws RecognitionException {
        try {
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:386:2: ( ( COLONCOLON annotation )* )
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:386:4: ( COLONCOLON annotation )*
            {
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:386:4: ( COLONCOLON annotation )*
            loop32:
            do {
                int alt32=2;
                int LA32_0 = input.LA(1);

                if ( (LA32_0==COLONCOLON) ) {
                    alt32=1;
                }


                switch (alt32) {
            	case 1 :
            	    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:386:5: COLONCOLON annotation
            	    {
            	    match(input,COLONCOLON,FOLLOW_COLONCOLON_in_annotations1574); 
            	    pushFollow(FOLLOW_annotation_in_annotations1576);
            	    annotation();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop32;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "annotations"


    // $ANTLR start "annotation"
    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:387:1: annotation : IDENT ( LP ann_expr ( COMA ann_expr )* RP )? ;
    public final void annotation() throws RecognitionException {
        try {
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:388:2: ( IDENT ( LP ann_expr ( COMA ann_expr )* RP )? )
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:388:4: IDENT ( LP ann_expr ( COMA ann_expr )* RP )?
            {
            match(input,IDENT,FOLLOW_IDENT_in_annotation1587); 
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:388:10: ( LP ann_expr ( COMA ann_expr )* RP )?
            int alt34=2;
            int LA34_0 = input.LA(1);

            if ( (LA34_0==LP) ) {
                alt34=1;
            }
            switch (alt34) {
                case 1 :
                    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:388:12: LP ann_expr ( COMA ann_expr )* RP
                    {
                    match(input,LP,FOLLOW_LP_in_annotation1591); 
                    pushFollow(FOLLOW_ann_expr_in_annotation1593);
                    ann_expr();

                    state._fsp--;

                    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:388:24: ( COMA ann_expr )*
                    loop33:
                    do {
                        int alt33=2;
                        int LA33_0 = input.LA(1);

                        if ( (LA33_0==COMA) ) {
                            alt33=1;
                        }


                        switch (alt33) {
                    	case 1 :
                    	    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:388:25: COMA ann_expr
                    	    {
                    	    match(input,COMA,FOLLOW_COMA_in_annotation1596); 
                    	    pushFollow(FOLLOW_ann_expr_in_annotation1598);
                    	    ann_expr();

                    	    state._fsp--;


                    	    }
                    	    break;

                    	default :
                    	    break loop33;
                        }
                    } while (true);

                    match(input,RP,FOLLOW_RP_in_annotation1602); 

                    }
                    break;

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "annotation"


    // $ANTLR start "ann_expr"
    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:389:1: ann_expr : ( IDENT LP ann_expr ( COMA ann_expr )* RP | flat_expr );
    public final void ann_expr() throws RecognitionException {
        try {
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:390:2: ( IDENT LP ann_expr ( COMA ann_expr )* RP | flat_expr )
            int alt36=2;
            int LA36_0 = input.LA(1);

            if ( (LA36_0==IDENT) ) {
                int LA36_1 = input.LA(2);

                if ( (LA36_1==LP) ) {
                    alt36=1;
                }
                else if ( ((LA36_1>=COMA && LA36_1<=DOTDOT)||LA36_1==RP||LA36_1==LBOX) ) {
                    alt36=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 36, 1, input);

                    throw nvae;
                }
            }
            else if ( ((LA36_0>=LBOX && LA36_0<=LB)||(LA36_0>=FALSE && LA36_0<=TRUE)||(LA36_0>=INT_LITERAL && LA36_0<=FLOAT_LITERAL)) ) {
                alt36=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 36, 0, input);

                throw nvae;
            }
            switch (alt36) {
                case 1 :
                    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:390:4: IDENT LP ann_expr ( COMA ann_expr )* RP
                    {
                    match(input,IDENT,FOLLOW_IDENT_in_ann_expr1614); 
                    match(input,LP,FOLLOW_LP_in_ann_expr1616); 
                    pushFollow(FOLLOW_ann_expr_in_ann_expr1618);
                    ann_expr();

                    state._fsp--;

                    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:390:22: ( COMA ann_expr )*
                    loop35:
                    do {
                        int alt35=2;
                        int LA35_0 = input.LA(1);

                        if ( (LA35_0==COMA) ) {
                            alt35=1;
                        }


                        switch (alt35) {
                    	case 1 :
                    	    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:390:23: COMA ann_expr
                    	    {
                    	    match(input,COMA,FOLLOW_COMA_in_ann_expr1621); 
                    	    pushFollow(FOLLOW_ann_expr_in_ann_expr1623);
                    	    ann_expr();

                    	    state._fsp--;


                    	    }
                    	    break;

                    	default :
                    	    break loop35;
                        }
                    } while (true);

                    match(input,RP,FOLLOW_RP_in_ann_expr1627); 

                    }
                    break;
                case 2 :
                    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:391:6: flat_expr
                    {
                    pushFollow(FOLLOW_flat_expr_in_ann_expr1634);
                    flat_expr();

                    state._fsp--;


                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "ann_expr"


    // $ANTLR start "flat_expr"
    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:392:1: flat_expr returns [FZVariableBuilder.ValType vt] : (nafe= non_array_flat_expr | al= array_literal );
    public final FZVariableBuilder.ValType flat_expr() throws RecognitionException {
        FZVariableBuilder.ValType vt = null;

        FZVariableBuilder.ValType nafe = null;

        FZVariableBuilder.ValType al = null;


        try {
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:393:2: (nafe= non_array_flat_expr | al= array_literal )
            int alt37=2;
            int LA37_0 = input.LA(1);

            if ( (LA37_0==LB||(LA37_0>=FALSE && LA37_0<=TRUE)||(LA37_0>=IDENT && LA37_0<=FLOAT_LITERAL)) ) {
                alt37=1;
            }
            else if ( (LA37_0==LBOX) ) {
                alt37=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 37, 0, input);

                throw nvae;
            }
            switch (alt37) {
                case 1 :
                    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:393:4: nafe= non_array_flat_expr
                    {
                    pushFollow(FOLLOW_non_array_flat_expr_in_flat_expr1648);
                    nafe=non_array_flat_expr();

                    state._fsp--;


                    		vt =nafe;
                    		

                    }
                    break;
                case 2 :
                    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:397:5: al= array_literal
                    {
                    pushFollow(FOLLOW_array_literal_in_flat_expr1660);
                    al=array_literal();

                    state._fsp--;


                    		vt =al;
                    		

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return vt;
    }
    // $ANTLR end "flat_expr"


    // $ANTLR start "solve_expr"
    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:401:1: solve_expr returns [FZVariableBuilder.ValType vt] : ( IDENT | aae= array_access_expr | IDENT LP flat_expr ( COMA flat_expr )* RP );
    public final FZVariableBuilder.ValType solve_expr() throws RecognitionException {
        FZVariableBuilder.ValType vt = null;

        Token IDENT12=null;
        FZVariableBuilder.ValType aae = null;


        try {
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:402:2: ( IDENT | aae= array_access_expr | IDENT LP flat_expr ( COMA flat_expr )* RP )
            int alt39=3;
            int LA39_0 = input.LA(1);

            if ( (LA39_0==IDENT) ) {
                switch ( input.LA(2) ) {
                case LBOX:
                    {
                    alt39=2;
                    }
                    break;
                case LP:
                    {
                    alt39=3;
                    }
                    break;
                case SEMICOLON:
                    {
                    alt39=1;
                    }
                    break;
                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 39, 1, input);

                    throw nvae;
                }

            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 39, 0, input);

                throw nvae;
            }
            switch (alt39) {
                case 1 :
                    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:402:4: IDENT
                    {
                    IDENT12=(Token)match(input,IDENT,FOLLOW_IDENT_in_solve_expr1676); 

                    		vt = FZVariableBuilder.build(FZVariableBuilder.EnumVal.sString, (IDENT12!=null?IDENT12.getText():null));
                    		

                    }
                    break;
                case 2 :
                    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:406:5: aae= array_access_expr
                    {
                    pushFollow(FOLLOW_array_access_expr_in_solve_expr1688);
                    aae=array_access_expr();

                    state._fsp--;


                    		vt = aae;
                    		

                    }
                    break;
                case 3 :
                    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:410:5: IDENT LP flat_expr ( COMA flat_expr )* RP
                    {
                    match(input,IDENT,FOLLOW_IDENT_in_solve_expr1698); 
                    match(input,LP,FOLLOW_LP_in_solve_expr1700); 
                    pushFollow(FOLLOW_flat_expr_in_solve_expr1702);
                    flat_expr();

                    state._fsp--;

                    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:410:24: ( COMA flat_expr )*
                    loop38:
                    do {
                        int alt38=2;
                        int LA38_0 = input.LA(1);

                        if ( (LA38_0==COMA) ) {
                            alt38=1;
                        }


                        switch (alt38) {
                    	case 1 :
                    	    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:410:25: COMA flat_expr
                    	    {
                    	    match(input,COMA,FOLLOW_COMA_in_solve_expr1705); 
                    	    pushFollow(FOLLOW_flat_expr_in_solve_expr1707);
                    	    flat_expr();

                    	    state._fsp--;


                    	    }
                    	    break;

                    	default :
                    	    break loop38;
                        }
                    } while (true);

                    match(input,RP,FOLLOW_RP_in_solve_expr1711); 

                    		System.err.println("solve_epxr::IDENT LP flat_expr (COMA flat_expr)* RP:: ERREUR");
                    		

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return vt;
    }
    // $ANTLR end "solve_expr"

    // Delegated rules


 

    public static final BitSet FOLLOW_pred_decl_item_in_model498 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_SEMICOLON_in_model500 = new BitSet(new long[]{0x0288250110B04000L});
    public static final BitSet FOLLOW_var_decl_item_in_model508 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_SEMICOLON_in_model510 = new BitSet(new long[]{0x0288240110B04000L});
    public static final BitSet FOLLOW_constraint_item_in_model518 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_SEMICOLON_in_model520 = new BitSet(new long[]{0x0288240110B04000L});
    public static final BitSet FOLLOW_solve_item_in_model529 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_SEMICOLON_in_model531 = new BitSet(new long[]{0x0000004000000002L});
    public static final BitSet FOLLOW_output_item_in_model537 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_SEMICOLON_in_model539 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PREDICATE_in_pred_decl_item555 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_IDENT_in_pred_decl_item557 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_LP_in_pred_decl_item559 = new BitSet(new long[]{0x0280040110304000L});
    public static final BitSet FOLLOW_pred_arg_in_pred_decl_item561 = new BitSet(new long[]{0x0000000000000840L});
    public static final BitSet FOLLOW_COMA_in_pred_decl_item564 = new BitSet(new long[]{0x0280040110304000L});
    public static final BitSet FOLLOW_pred_arg_in_pred_decl_item566 = new BitSet(new long[]{0x0000000000000840L});
    public static final BitSet FOLLOW_RP_in_pred_decl_item570 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_non_array_ti_expr_tail_in_pred_arg580 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_COLON_in_pred_arg582 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_IDENT_in_pred_arg584 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ARRAY_in_pred_arg591 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_LBOX_in_pred_arg593 = new BitSet(new long[]{0x0080000000000000L});
    public static final BitSet FOLLOW_INT_LITERAL_in_pred_arg595 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_DOTDOT_in_pred_arg597 = new BitSet(new long[]{0x0080000000000000L});
    public static final BitSet FOLLOW_INT_LITERAL_in_pred_arg599 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_RBOX_in_pred_arg601 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_OF_in_pred_arg603 = new BitSet(new long[]{0x0288040110204000L});
    public static final BitSet FOLLOW_array_decl_tail_in_pred_arg605 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_COLON_in_pred_arg607 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_IDENT_in_pred_arg610 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ARRAY_in_pred_arg616 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_LBOX_in_pred_arg618 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_INT_in_pred_arg620 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_RBOX_in_pred_arg622 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_OF_in_pred_arg624 = new BitSet(new long[]{0x0288040110204000L});
    public static final BitSet FOLLOW_array_decl_tail_in_pred_arg626 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_COLON_in_pred_arg628 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_IDENT_in_pred_arg630 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VAR_in_var_decl_item641 = new BitSet(new long[]{0x0280040110204000L});
    public static final BitSet FOLLOW_non_array_ti_expr_tail_in_var_decl_item645 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_COLON_in_var_decl_item647 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_ident_anns_in_var_decl_item651 = new BitSet(new long[]{0x0000000000000102L});
    public static final BitSet FOLLOW_EQUAL_in_var_decl_item654 = new BitSet(new long[]{0x03C0000000064000L});
    public static final BitSet FOLLOW_non_array_flat_expr_in_var_decl_item658 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_non_array_ti_expr_tail_in_var_decl_item672 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_COLON_in_var_decl_item674 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_ident_anns_in_var_decl_item678 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_EQUAL_in_var_decl_item680 = new BitSet(new long[]{0x03C0000000064000L});
    public static final BitSet FOLLOW_non_array_flat_expr_in_var_decl_item684 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ARRAY_in_var_decl_item694 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_LBOX_in_var_decl_item696 = new BitSet(new long[]{0x0080000000000000L});
    public static final BitSet FOLLOW_INT_LITERAL_in_var_decl_item700 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_DOTDOT_in_var_decl_item702 = new BitSet(new long[]{0x0080000000000000L});
    public static final BitSet FOLLOW_INT_LITERAL_in_var_decl_item706 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_RBOX_in_var_decl_item708 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_OF_in_var_decl_item710 = new BitSet(new long[]{0x0288040110204000L});
    public static final BitSet FOLLOW_array_decl_tail_in_var_decl_item714 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_non_array_ti_expr_tail_in_array_decl_tail736 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_COLON_in_array_decl_tail738 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_ident_anns_in_array_decl_tail742 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_EQUAL_in_array_decl_tail744 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_array_literal_in_array_decl_tail748 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VAR_in_array_decl_tail758 = new BitSet(new long[]{0x0280040110204000L});
    public static final BitSet FOLLOW_non_array_ti_expr_tail_in_array_decl_tail762 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_COLON_in_array_decl_tail764 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_ident_anns_in_array_decl_tail768 = new BitSet(new long[]{0x0000000000000102L});
    public static final BitSet FOLLOW_EQUAL_in_array_decl_tail772 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_array_literal_in_array_decl_tail776 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENT_in_ident_anns795 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_annotations_in_ident_anns797 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CONSTRAINT_in_constraint_item811 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_constraint_elem_in_constraint_item813 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_annotations_in_constraint_item815 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENT_in_constraint_elem826 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_LP_in_constraint_elem829 = new BitSet(new long[]{0x03C0000000066000L});
    public static final BitSet FOLLOW_flat_expr_in_constraint_elem833 = new BitSet(new long[]{0x0000000000000840L});
    public static final BitSet FOLLOW_COMA_in_constraint_elem837 = new BitSet(new long[]{0x03C0000000066000L});
    public static final BitSet FOLLOW_flat_expr_in_constraint_elem841 = new BitSet(new long[]{0x0000000000000840L});
    public static final BitSet FOLLOW_RP_in_constraint_elem845 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variable_expr_in_constraint_elem855 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SOLVE_in_solve_item874 = new BitSet(new long[]{0x0000002C00000010L});
    public static final BitSet FOLLOW_annotations_in_solve_item876 = new BitSet(new long[]{0x0000002C00000010L});
    public static final BitSet FOLLOW_solve_kind_in_solve_item878 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SATISFY_in_solve_kind893 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MINIMIZE_in_solve_kind906 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_solve_expr_in_solve_kind910 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MAXIMIZE_in_solve_kind922 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_solve_expr_in_solve_kind926 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OUTPUT_in_output_item942 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_LBOX_in_output_item944 = new BitSet(new long[]{0x0100180000000000L});
    public static final BitSet FOLLOW_output_elem_in_output_item946 = new BitSet(new long[]{0x0000000000001040L});
    public static final BitSet FOLLOW_COMA_in_output_item949 = new BitSet(new long[]{0x0100180000000000L});
    public static final BitSet FOLLOW_output_elem_in_output_item951 = new BitSet(new long[]{0x0000000000001040L});
    public static final BitSet FOLLOW_RBOX_in_output_item955 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SHOW_in_output_elem964 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_LP_in_output_elem966 = new BitSet(new long[]{0x03C0000000066000L});
    public static final BitSet FOLLOW_flat_expr_in_output_elem968 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_RP_in_output_elem970 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SHOWCOND_in_output_elem976 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_LP_in_output_elem978 = new BitSet(new long[]{0x03C0000000066000L});
    public static final BitSet FOLLOW_flat_expr_in_output_elem980 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_COMA_in_output_elem982 = new BitSet(new long[]{0x03C0000000066000L});
    public static final BitSet FOLLOW_flat_expr_in_output_elem984 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_COMA_in_output_elem986 = new BitSet(new long[]{0x03C0000000066000L});
    public static final BitSet FOLLOW_flat_expr_in_output_elem988 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_RP_in_output_elem990 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_output_elem996 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_scalar_ti_expr_tail_in_non_array_ti_expr_tail1012 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_ti_expr_tail_in_non_array_ti_expr_tail1023 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BOOL_in_bool_ti_expr_tail1043 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_bool_ti_expr_tail_in_scalar_ti_expr_tail1062 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_int_ti_expr_tail_in_scalar_ti_expr_tail1073 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_float_ti_expr_tail_in_scalar_ti_expr_tail1084 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_int_ti_expr_tail1103 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_LITERAL_in_int_ti_expr_tail1115 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_DOTDOT_in_int_ti_expr_tail1117 = new BitSet(new long[]{0x0080000000000000L});
    public static final BitSet FOLLOW_INT_LITERAL_in_int_ti_expr_tail1121 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LB_in_int_ti_expr_tail1134 = new BitSet(new long[]{0x0080000000000000L});
    public static final BitSet FOLLOW_INT_LITERAL_in_int_ti_expr_tail1138 = new BitSet(new long[]{0x0000000000008040L});
    public static final BitSet FOLLOW_COMA_in_int_ti_expr_tail1142 = new BitSet(new long[]{0x0080000000000000L});
    public static final BitSet FOLLOW_INT_LITERAL_in_int_ti_expr_tail1146 = new BitSet(new long[]{0x0000000000008040L});
    public static final BitSet FOLLOW_RB_in_int_ti_expr_tail1151 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_float_ti_expr_tail1167 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_LITERAL_in_float_ti_expr_tail1179 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_DOTDOT_in_float_ti_expr_tail1181 = new BitSet(new long[]{0x0200000000000000L});
    public static final BitSet FOLLOW_FLOAT_LITERAL_in_float_ti_expr_tail1185 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SET_in_set_ti_expr_tail1200 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_OF_in_set_ti_expr_tail1202 = new BitSet(new long[]{0x0280000110204000L});
    public static final BitSet FOLLOW_scalar_ti_expr_tail_in_set_ti_expr_tail1206 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_scalar_flat_expr_in_non_array_flat_expr1226 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_literal_in_non_array_flat_expr1238 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENT_in_scalar_flat_expr1255 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_array_access_expr_in_scalar_flat_expr1267 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_bool_literal_in_scalar_flat_expr1279 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_LITERAL_in_scalar_flat_expr1289 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_LITERAL_in_scalar_flat_expr1299 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_scalar_flat_expr1309 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_array_access_expr_in_int_flat_expr1329 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENT_in_int_flat_expr1338 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_LITERAL_in_int_flat_expr1348 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENT_in_variable_expr1364 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_array_access_expr_in_variable_expr1376 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENT_in_array_access_expr1392 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_LBOX_in_array_access_expr1394 = new BitSet(new long[]{0x00C0000000000000L});
    public static final BitSet FOLLOW_int_index_expr_in_array_access_expr1398 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_RBOX_in_array_access_expr1400 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_LITERAL_in_int_index_expr1416 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENT_in_int_index_expr1428 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FALSE_in_bool_literal1447 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRUE_in_bool_literal1458 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LB_in_set_literal1480 = new BitSet(new long[]{0x03C0000000068000L});
    public static final BitSet FOLLOW_scalar_flat_expr_in_set_literal1486 = new BitSet(new long[]{0x0000000000008040L});
    public static final BitSet FOLLOW_COMA_in_set_literal1490 = new BitSet(new long[]{0x03C0000000060000L});
    public static final BitSet FOLLOW_scalar_flat_expr_in_set_literal1494 = new BitSet(new long[]{0x0000000000008040L});
    public static final BitSet FOLLOW_RB_in_set_literal1501 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_int_flat_expr_in_set_literal1513 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_DOTDOT_in_set_literal1515 = new BitSet(new long[]{0x03C0000000064000L});
    public static final BitSet FOLLOW_int_flat_expr_in_set_literal1519 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LBOX_in_array_literal1536 = new BitSet(new long[]{0x03C0000000065000L});
    public static final BitSet FOLLOW_non_array_flat_expr_in_array_literal1542 = new BitSet(new long[]{0x0000000000001040L});
    public static final BitSet FOLLOW_COMA_in_array_literal1546 = new BitSet(new long[]{0x03C0000000064000L});
    public static final BitSet FOLLOW_non_array_flat_expr_in_array_literal1550 = new BitSet(new long[]{0x0000000000001040L});
    public static final BitSet FOLLOW_RBOX_in_array_literal1559 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COLONCOLON_in_annotations1574 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_annotation_in_annotations1576 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_IDENT_in_annotation1587 = new BitSet(new long[]{0x0000000000000402L});
    public static final BitSet FOLLOW_LP_in_annotation1591 = new BitSet(new long[]{0x03C0000000066000L});
    public static final BitSet FOLLOW_ann_expr_in_annotation1593 = new BitSet(new long[]{0x0000000000000840L});
    public static final BitSet FOLLOW_COMA_in_annotation1596 = new BitSet(new long[]{0x03C0000000066000L});
    public static final BitSet FOLLOW_ann_expr_in_annotation1598 = new BitSet(new long[]{0x0000000000000840L});
    public static final BitSet FOLLOW_RP_in_annotation1602 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENT_in_ann_expr1614 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_LP_in_ann_expr1616 = new BitSet(new long[]{0x03C0000000066000L});
    public static final BitSet FOLLOW_ann_expr_in_ann_expr1618 = new BitSet(new long[]{0x0000000000000840L});
    public static final BitSet FOLLOW_COMA_in_ann_expr1621 = new BitSet(new long[]{0x03C0000000066000L});
    public static final BitSet FOLLOW_ann_expr_in_ann_expr1623 = new BitSet(new long[]{0x0000000000000840L});
    public static final BitSet FOLLOW_RP_in_ann_expr1627 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_flat_expr_in_ann_expr1634 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_non_array_flat_expr_in_flat_expr1648 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_array_literal_in_flat_expr1660 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENT_in_solve_expr1676 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_array_access_expr_in_solve_expr1688 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENT_in_solve_expr1698 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_LP_in_solve_expr1700 = new BitSet(new long[]{0x03C0000000066000L});
    public static final BitSet FOLLOW_flat_expr_in_solve_expr1702 = new BitSet(new long[]{0x0000000000000840L});
    public static final BitSet FOLLOW_COMA_in_solve_expr1705 = new BitSet(new long[]{0x03C0000000066000L});
    public static final BitSet FOLLOW_flat_expr_in_solve_expr1707 = new BitSet(new long[]{0x0000000000000840L});
    public static final BitSet FOLLOW_RP_in_solve_expr1711 = new BitSet(new long[]{0x0000000000000002L});

}
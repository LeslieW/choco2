// $ANTLR 3.2 Sep 23, 2009 12:02:23 /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g 2009-10-21 15:06:08

package parser.chocogen.mzn;


import org.antlr.runtime.*;

public class FlatZincLexer extends Lexer {
    public static final int FUNCTION=29;
    public static final int WHERE=53;
    public static final int LBOX=13;
    public static final int RB=15;
    public static final int RP=11;
    public static final int CASE=22;
    public static final int LP=10;
    public static final int DQUOTE=16;
    public static final int FLOAT=28;
    public static final int DOTDOT=7;
    public static final int EOF=-1;
    public static final int PREDICATE=40;
    public static final int IF=30;
    public static final int TYPE=50;
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
    public static final int OF=36;
    public static final int COMA=6;
    public static final int TRUE=18;
    public static final int ELSEIF=25;
    public static final int COLON=5;
    public static final int FLOAT_LITERAL=57;
    public static final int COLONCOLON=4;
    public static final int SHOWCOND=44;
    public static final int ANY=19;
    public static final int ENUM=27;
    public static final int TEST=47;
    public static final int LB=14;
    public static final int FALSE=17;
    public static final int CONSTRAINT=23;
    public static final int SOLVE=45;
    public static final int OUTPUT=38;
    public static final int LET=33;
    public static final int STRING=46;

    // delegates
    // delegators

    public FlatZincLexer() {;} 
    public FlatZincLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public FlatZincLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);

    }
    public String getGrammarFileName() { return "/media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g"; }

    // $ANTLR start "COLONCOLON"
    public final void mCOLONCOLON() throws RecognitionException {
        try {
            int _type = COLONCOLON;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:7:12: ( '::' )
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:7:14: '::'
            {
            match("::"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "COLONCOLON"

    // $ANTLR start "COLON"
    public final void mCOLON() throws RecognitionException {
        try {
            int _type = COLON;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:8:7: ( ':' )
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:8:9: ':'
            {
            match(':'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "COLON"

    // $ANTLR start "COMA"
    public final void mCOMA() throws RecognitionException {
        try {
            int _type = COMA;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:9:6: ( ',' )
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:9:8: ','
            {
            match(','); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "COMA"

    // $ANTLR start "DOTDOT"
    public final void mDOTDOT() throws RecognitionException {
        try {
            int _type = DOTDOT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:10:8: ( '..' )
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:10:10: '..'
            {
            match(".."); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DOTDOT"

    // $ANTLR start "EQUAL"
    public final void mEQUAL() throws RecognitionException {
        try {
            int _type = EQUAL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:11:7: ( '=' )
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:11:9: '='
            {
            match('='); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "EQUAL"

    // $ANTLR start "SEMICOLON"
    public final void mSEMICOLON() throws RecognitionException {
        try {
            int _type = SEMICOLON;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:12:11: ( ';' )
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:12:13: ';'
            {
            match(';'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SEMICOLON"

    // $ANTLR start "LP"
    public final void mLP() throws RecognitionException {
        try {
            int _type = LP;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:13:4: ( '(' )
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:13:6: '('
            {
            match('('); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LP"

    // $ANTLR start "RP"
    public final void mRP() throws RecognitionException {
        try {
            int _type = RP;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:14:4: ( ')' )
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:14:6: ')'
            {
            match(')'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RP"

    // $ANTLR start "RBOX"
    public final void mRBOX() throws RecognitionException {
        try {
            int _type = RBOX;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:15:6: ( ']' )
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:15:8: ']'
            {
            match(']'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RBOX"

    // $ANTLR start "LBOX"
    public final void mLBOX() throws RecognitionException {
        try {
            int _type = LBOX;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:16:6: ( '[' )
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:16:8: '['
            {
            match('['); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LBOX"

    // $ANTLR start "LB"
    public final void mLB() throws RecognitionException {
        try {
            int _type = LB;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:17:4: ( '{' )
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:17:6: '{'
            {
            match('{'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LB"

    // $ANTLR start "RB"
    public final void mRB() throws RecognitionException {
        try {
            int _type = RB;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:18:4: ( '}' )
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:18:6: '}'
            {
            match('}'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RB"

    // $ANTLR start "DQUOTE"
    public final void mDQUOTE() throws RecognitionException {
        try {
            int _type = DQUOTE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:19:8: ( '\"' )
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:19:10: '\"'
            {
            match('\"'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DQUOTE"

    // $ANTLR start "FALSE"
    public final void mFALSE() throws RecognitionException {
        try {
            int _type = FALSE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:20:7: ( 'false' )
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:20:9: 'false'
            {
            match("false"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "FALSE"

    // $ANTLR start "TRUE"
    public final void mTRUE() throws RecognitionException {
        try {
            int _type = TRUE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:21:6: ( 'true' )
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:21:8: 'true'
            {
            match("true"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "TRUE"

    // $ANTLR start "ANY"
    public final void mANY() throws RecognitionException {
        try {
            int _type = ANY;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:22:5: ( 'any' )
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:22:7: 'any'
            {
            match("any"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ANY"

    // $ANTLR start "ARRAY"
    public final void mARRAY() throws RecognitionException {
        try {
            int _type = ARRAY;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:23:7: ( 'array' )
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:23:9: 'array'
            {
            match("array"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ARRAY"

    // $ANTLR start "BOOL"
    public final void mBOOL() throws RecognitionException {
        try {
            int _type = BOOL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:24:6: ( 'bool' )
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:24:8: 'bool'
            {
            match("bool"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "BOOL"

    // $ANTLR start "CASE"
    public final void mCASE() throws RecognitionException {
        try {
            int _type = CASE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:25:6: ( 'case' )
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:25:8: 'case'
            {
            match("case"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "CASE"

    // $ANTLR start "CONSTRAINT"
    public final void mCONSTRAINT() throws RecognitionException {
        try {
            int _type = CONSTRAINT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:26:12: ( 'constraint' )
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:26:14: 'constraint'
            {
            match("constraint"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "CONSTRAINT"

    // $ANTLR start "ELSE"
    public final void mELSE() throws RecognitionException {
        try {
            int _type = ELSE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:27:6: ( 'else' )
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:27:8: 'else'
            {
            match("else"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ELSE"

    // $ANTLR start "ELSEIF"
    public final void mELSEIF() throws RecognitionException {
        try {
            int _type = ELSEIF;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:28:8: ( 'elseif' )
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:28:10: 'elseif'
            {
            match("elseif"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ELSEIF"

    // $ANTLR start "ENDIF"
    public final void mENDIF() throws RecognitionException {
        try {
            int _type = ENDIF;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:29:7: ( 'endif' )
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:29:9: 'endif'
            {
            match("endif"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ENDIF"

    // $ANTLR start "ENUM"
    public final void mENUM() throws RecognitionException {
        try {
            int _type = ENUM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:30:6: ( 'enum' )
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:30:8: 'enum'
            {
            match("enum"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ENUM"

    // $ANTLR start "FLOAT"
    public final void mFLOAT() throws RecognitionException {
        try {
            int _type = FLOAT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:31:7: ( 'float' )
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:31:9: 'float'
            {
            match("float"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "FLOAT"

    // $ANTLR start "FUNCTION"
    public final void mFUNCTION() throws RecognitionException {
        try {
            int _type = FUNCTION;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:32:10: ( 'function' )
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:32:12: 'function'
            {
            match("function"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "FUNCTION"

    // $ANTLR start "IF"
    public final void mIF() throws RecognitionException {
        try {
            int _type = IF;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:33:4: ( 'if' )
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:33:6: 'if'
            {
            match("if"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "IF"

    // $ANTLR start "INCLUDE"
    public final void mINCLUDE() throws RecognitionException {
        try {
            int _type = INCLUDE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:34:9: ( 'include' )
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:34:11: 'include'
            {
            match("include"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "INCLUDE"

    // $ANTLR start "INT"
    public final void mINT() throws RecognitionException {
        try {
            int _type = INT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:35:5: ( 'int' )
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:35:7: 'int'
            {
            match("int"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "INT"

    // $ANTLR start "LET"
    public final void mLET() throws RecognitionException {
        try {
            int _type = LET;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:36:5: ( 'let' )
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:36:7: 'let'
            {
            match("let"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LET"

    // $ANTLR start "MAXIMIZE"
    public final void mMAXIMIZE() throws RecognitionException {
        try {
            int _type = MAXIMIZE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:37:10: ( 'maximize' )
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:37:12: 'maximize'
            {
            match("maximize"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "MAXIMIZE"

    // $ANTLR start "MINIMIZE"
    public final void mMINIMIZE() throws RecognitionException {
        try {
            int _type = MINIMIZE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:38:10: ( 'minimize' )
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:38:12: 'minimize'
            {
            match("minimize"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "MINIMIZE"

    // $ANTLR start "OF"
    public final void mOF() throws RecognitionException {
        try {
            int _type = OF;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:39:4: ( 'of' )
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:39:6: 'of'
            {
            match("of"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "OF"

    // $ANTLR start "SATISFY"
    public final void mSATISFY() throws RecognitionException {
        try {
            int _type = SATISFY;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:40:9: ( 'satisfy' )
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:40:11: 'satisfy'
            {
            match("satisfy"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SATISFY"

    // $ANTLR start "OUTPUT"
    public final void mOUTPUT() throws RecognitionException {
        try {
            int _type = OUTPUT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:41:8: ( 'output' )
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:41:10: 'output'
            {
            match("output"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "OUTPUT"

    // $ANTLR start "PAR"
    public final void mPAR() throws RecognitionException {
        try {
            int _type = PAR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:42:5: ( 'par' )
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:42:7: 'par'
            {
            match("par"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "PAR"

    // $ANTLR start "PREDICATE"
    public final void mPREDICATE() throws RecognitionException {
        try {
            int _type = PREDICATE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:43:11: ( 'predicate' )
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:43:13: 'predicate'
            {
            match("predicate"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "PREDICATE"

    // $ANTLR start "RECORD"
    public final void mRECORD() throws RecognitionException {
        try {
            int _type = RECORD;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:44:8: ( 'record' )
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:44:10: 'record'
            {
            match("record"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RECORD"

    // $ANTLR start "SET"
    public final void mSET() throws RecognitionException {
        try {
            int _type = SET;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:45:5: ( 'set' )
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:45:7: 'set'
            {
            match("set"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SET"

    // $ANTLR start "SHOW"
    public final void mSHOW() throws RecognitionException {
        try {
            int _type = SHOW;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:46:6: ( 'show' )
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:46:8: 'show'
            {
            match("show"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SHOW"

    // $ANTLR start "SHOWCOND"
    public final void mSHOWCOND() throws RecognitionException {
        try {
            int _type = SHOWCOND;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:47:10: ( 'show_cond' )
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:47:12: 'show_cond'
            {
            match("show_cond"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SHOWCOND"

    // $ANTLR start "SOLVE"
    public final void mSOLVE() throws RecognitionException {
        try {
            int _type = SOLVE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:48:7: ( 'solve' )
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:48:9: 'solve'
            {
            match("solve"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SOLVE"

    // $ANTLR start "STRING"
    public final void mSTRING() throws RecognitionException {
        try {
            int _type = STRING;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:49:8: ( 'string' )
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:49:10: 'string'
            {
            match("string"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "STRING"

    // $ANTLR start "TEST"
    public final void mTEST() throws RecognitionException {
        try {
            int _type = TEST;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:50:6: ( 'test' )
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:50:8: 'test'
            {
            match("test"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "TEST"

    // $ANTLR start "THEN"
    public final void mTHEN() throws RecognitionException {
        try {
            int _type = THEN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:51:6: ( 'then' )
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:51:8: 'then'
            {
            match("then"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "THEN"

    // $ANTLR start "TUPLE"
    public final void mTUPLE() throws RecognitionException {
        try {
            int _type = TUPLE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:52:7: ( 'tuple' )
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:52:9: 'tuple'
            {
            match("tuple"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "TUPLE"

    // $ANTLR start "TYPE"
    public final void mTYPE() throws RecognitionException {
        try {
            int _type = TYPE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:53:6: ( 'type' )
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:53:8: 'type'
            {
            match("type"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "TYPE"

    // $ANTLR start "VAR"
    public final void mVAR() throws RecognitionException {
        try {
            int _type = VAR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:54:5: ( 'var' )
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:54:7: 'var'
            {
            match("var"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "VAR"

    // $ANTLR start "VARIANT_RECORD"
    public final void mVARIANT_RECORD() throws RecognitionException {
        try {
            int _type = VARIANT_RECORD;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:55:16: ( 'variant_record' )
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:55:18: 'variant_record'
            {
            match("variant_record"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "VARIANT_RECORD"

    // $ANTLR start "WHERE"
    public final void mWHERE() throws RecognitionException {
        try {
            int _type = WHERE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:56:7: ( 'where' )
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:56:9: 'where'
            {
            match("where"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "WHERE"

    // $ANTLR start "INT_LITERAL"
    public final void mINT_LITERAL() throws RecognitionException {
        try {
            int _type = INT_LITERAL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:352:14: ( ( '-' )? ( ( DIGIT )+ | ( HEX_DIGIT )+ | ( OCT_DIGIT )+ ) )
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:352:16: ( '-' )? ( ( DIGIT )+ | ( HEX_DIGIT )+ | ( OCT_DIGIT )+ )
            {
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:352:16: ( '-' )?
            int alt1=2;
            int LA1_0 = input.LA(1);

            if ( (LA1_0=='-') ) {
                alt1=1;
            }
            switch (alt1) {
                case 1 :
                    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:352:17: '-'
                    {
                    match('-'); 

                    }
                    break;

            }

            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:352:23: ( ( DIGIT )+ | ( HEX_DIGIT )+ | ( OCT_DIGIT )+ )
            int alt5=3;
            int LA5_0 = input.LA(1);

            if ( (LA5_0=='0') ) {
                switch ( input.LA(2) ) {
                case 'x':
                    {
                    alt5=2;
                    }
                    break;
                case 'o':
                    {
                    alt5=3;
                    }
                    break;
                default:
                    alt5=1;}

            }
            else if ( ((LA5_0>='1' && LA5_0<='9')) ) {
                alt5=1;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 5, 0, input);

                throw nvae;
            }
            switch (alt5) {
                case 1 :
                    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:352:24: ( DIGIT )+
                    {
                    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:352:24: ( DIGIT )+
                    int cnt2=0;
                    loop2:
                    do {
                        int alt2=2;
                        int LA2_0 = input.LA(1);

                        if ( ((LA2_0>='0' && LA2_0<='9')) ) {
                            alt2=1;
                        }


                        switch (alt2) {
                    	case 1 :
                    	    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:352:24: DIGIT
                    	    {
                    	    mDIGIT(); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt2 >= 1 ) break loop2;
                                EarlyExitException eee =
                                    new EarlyExitException(2, input);
                                throw eee;
                        }
                        cnt2++;
                    } while (true);


                    }
                    break;
                case 2 :
                    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:352:31: ( HEX_DIGIT )+
                    {
                    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:352:31: ( HEX_DIGIT )+
                    int cnt3=0;
                    loop3:
                    do {
                        int alt3=2;
                        int LA3_0 = input.LA(1);

                        if ( (LA3_0=='0') ) {
                            alt3=1;
                        }


                        switch (alt3) {
                    	case 1 :
                    	    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:352:31: HEX_DIGIT
                    	    {
                    	    mHEX_DIGIT(); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt3 >= 1 ) break loop3;
                                EarlyExitException eee =
                                    new EarlyExitException(3, input);
                                throw eee;
                        }
                        cnt3++;
                    } while (true);


                    }
                    break;
                case 3 :
                    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:352:43: ( OCT_DIGIT )+
                    {
                    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:352:43: ( OCT_DIGIT )+
                    int cnt4=0;
                    loop4:
                    do {
                        int alt4=2;
                        int LA4_0 = input.LA(1);

                        if ( (LA4_0=='0') ) {
                            alt4=1;
                        }


                        switch (alt4) {
                    	case 1 :
                    	    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:352:43: OCT_DIGIT
                    	    {
                    	    mOCT_DIGIT(); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt4 >= 1 ) break loop4;
                                EarlyExitException eee =
                                    new EarlyExitException(4, input);
                                throw eee;
                        }
                        cnt4++;
                    } while (true);


                    }
                    break;

            }


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "INT_LITERAL"

    // $ANTLR start "FLOAT_LITERAL"
    public final void mFLOAT_LITERAL() throws RecognitionException {
        try {
            int _type = FLOAT_LITERAL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:353:16: ( ( '-' )? ( DIGIT )+ ( '.' | ( '.' ( DIGIT )+ )? ( 'E' | 'e' ) ( '-' | '+' )? ) ( DIGIT )+ )
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:353:18: ( '-' )? ( DIGIT )+ ( '.' | ( '.' ( DIGIT )+ )? ( 'E' | 'e' ) ( '-' | '+' )? ) ( DIGIT )+
            {
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:353:18: ( '-' )?
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0=='-') ) {
                alt6=1;
            }
            switch (alt6) {
                case 1 :
                    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:353:19: '-'
                    {
                    match('-'); 

                    }
                    break;

            }

            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:353:25: ( DIGIT )+
            int cnt7=0;
            loop7:
            do {
                int alt7=2;
                int LA7_0 = input.LA(1);

                if ( ((LA7_0>='0' && LA7_0<='9')) ) {
                    alt7=1;
                }


                switch (alt7) {
            	case 1 :
            	    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:353:25: DIGIT
            	    {
            	    mDIGIT(); 

            	    }
            	    break;

            	default :
            	    if ( cnt7 >= 1 ) break loop7;
                        EarlyExitException eee =
                            new EarlyExitException(7, input);
                        throw eee;
                }
                cnt7++;
            } while (true);

            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:353:31: ( '.' | ( '.' ( DIGIT )+ )? ( 'E' | 'e' ) ( '-' | '+' )? )
            int alt11=2;
            alt11 = dfa11.predict(input);
            switch (alt11) {
                case 1 :
                    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:353:32: '.'
                    {
                    match('.'); 

                    }
                    break;
                case 2 :
                    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:353:36: ( '.' ( DIGIT )+ )? ( 'E' | 'e' ) ( '-' | '+' )?
                    {
                    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:353:36: ( '.' ( DIGIT )+ )?
                    int alt9=2;
                    int LA9_0 = input.LA(1);

                    if ( (LA9_0=='.') ) {
                        alt9=1;
                    }
                    switch (alt9) {
                        case 1 :
                            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:353:37: '.' ( DIGIT )+
                            {
                            match('.'); 
                            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:353:40: ( DIGIT )+
                            int cnt8=0;
                            loop8:
                            do {
                                int alt8=2;
                                int LA8_0 = input.LA(1);

                                if ( ((LA8_0>='0' && LA8_0<='9')) ) {
                                    alt8=1;
                                }


                                switch (alt8) {
                            	case 1 :
                            	    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:353:40: DIGIT
                            	    {
                            	    mDIGIT(); 

                            	    }
                            	    break;

                            	default :
                            	    if ( cnt8 >= 1 ) break loop8;
                                        EarlyExitException eee =
                                            new EarlyExitException(8, input);
                                        throw eee;
                                }
                                cnt8++;
                            } while (true);


                            }
                            break;

                    }

                    if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                        input.consume();

                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;}

                    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:353:57: ( '-' | '+' )?
                    int alt10=2;
                    int LA10_0 = input.LA(1);

                    if ( (LA10_0=='+'||LA10_0=='-') ) {
                        alt10=1;
                    }
                    switch (alt10) {
                        case 1 :
                            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:
                            {
                            if ( input.LA(1)=='+'||input.LA(1)=='-' ) {
                                input.consume();

                            }
                            else {
                                MismatchedSetException mse = new MismatchedSetException(null,input);
                                recover(mse);
                                throw mse;}


                            }
                            break;

                    }


                    }
                    break;

            }

            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:353:68: ( DIGIT )+
            int cnt12=0;
            loop12:
            do {
                int alt12=2;
                int LA12_0 = input.LA(1);

                if ( ((LA12_0>='0' && LA12_0<='9')) ) {
                    alt12=1;
                }


                switch (alt12) {
            	case 1 :
            	    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:353:68: DIGIT
            	    {
            	    mDIGIT(); 

            	    }
            	    break;

            	default :
            	    if ( cnt12 >= 1 ) break loop12;
                        EarlyExitException eee =
                            new EarlyExitException(12, input);
                        throw eee;
                }
                cnt12++;
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "FLOAT_LITERAL"

    // $ANTLR start "STRING_LITERAL"
    public final void mSTRING_LITERAL() throws RecognitionException {
        try {
            int _type = STRING_LITERAL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:354:17: ( DQUOTE (~ ( '\\n' | '\\r' | '\\f' ) )* DQUOTE )
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:354:19: DQUOTE (~ ( '\\n' | '\\r' | '\\f' ) )* DQUOTE
            {
            mDQUOTE(); 
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:354:26: (~ ( '\\n' | '\\r' | '\\f' ) )*
            loop13:
            do {
                int alt13=2;
                int LA13_0 = input.LA(1);

                if ( (LA13_0=='\"') ) {
                    int LA13_1 = input.LA(2);

                    if ( ((LA13_1>='\u0000' && LA13_1<='\t')||LA13_1=='\u000B'||(LA13_1>='\u000E' && LA13_1<='\uFFFF')) ) {
                        alt13=1;
                    }


                }
                else if ( ((LA13_0>='\u0000' && LA13_0<='\t')||LA13_0=='\u000B'||(LA13_0>='\u000E' && LA13_0<='!')||(LA13_0>='#' && LA13_0<='\uFFFF')) ) {
                    alt13=1;
                }


                switch (alt13) {
            	case 1 :
            	    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:354:27: ~ ( '\\n' | '\\r' | '\\f' )
            	    {
            	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='\t')||input.LA(1)=='\u000B'||(input.LA(1)>='\u000E' && input.LA(1)<='\uFFFF') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    break loop13;
                }
            } while (true);

            mDQUOTE(); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "STRING_LITERAL"

    // $ANTLR start "IDENT"
    public final void mIDENT() throws RecognitionException {
        try {
            int _type = IDENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:355:9: ( LIT ( LIT | DIGIT | '_' )* )
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:355:11: LIT ( LIT | DIGIT | '_' )*
            {
            mLIT(); 
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:355:15: ( LIT | DIGIT | '_' )*
            loop14:
            do {
                int alt14=2;
                int LA14_0 = input.LA(1);

                if ( ((LA14_0>='0' && LA14_0<='9')||(LA14_0>='A' && LA14_0<='Z')||LA14_0=='_'||(LA14_0>='a' && LA14_0<='z')) ) {
                    alt14=1;
                }


                switch (alt14) {
            	case 1 :
            	    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:
            	    {
            	    if ( (input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    break loop14;
                }
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "IDENT"

    // $ANTLR start "DIGIT"
    public final void mDIGIT() throws RecognitionException {
        try {
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:357:18: ( '0' .. '9' )
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:357:20: '0' .. '9'
            {
            matchRange('0','9'); 

            }

        }
        finally {
        }
    }
    // $ANTLR end "DIGIT"

    // $ANTLR start "HEX_DIGIT"
    public final void mHEX_DIGIT() throws RecognitionException {
        try {
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:358:21: ( '0x' ( DIGIT | 'A' .. 'F' | 'a' .. 'f' )+ )
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:358:23: '0x' ( DIGIT | 'A' .. 'F' | 'a' .. 'f' )+
            {
            match("0x"); 

            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:358:28: ( DIGIT | 'A' .. 'F' | 'a' .. 'f' )+
            int cnt15=0;
            loop15:
            do {
                int alt15=2;
                int LA15_0 = input.LA(1);

                if ( ((LA15_0>='0' && LA15_0<='9')||(LA15_0>='A' && LA15_0<='F')||(LA15_0>='a' && LA15_0<='f')) ) {
                    alt15=1;
                }


                switch (alt15) {
            	case 1 :
            	    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:
            	    {
            	    if ( (input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='F')||(input.LA(1)>='a' && input.LA(1)<='f') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    if ( cnt15 >= 1 ) break loop15;
                        EarlyExitException eee =
                            new EarlyExitException(15, input);
                        throw eee;
                }
                cnt15++;
            } while (true);


            }

        }
        finally {
        }
    }
    // $ANTLR end "HEX_DIGIT"

    // $ANTLR start "OCT_DIGIT"
    public final void mOCT_DIGIT() throws RecognitionException {
        try {
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:359:21: ( '0o' ( '0' .. '7' )+ )
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:359:23: '0o' ( '0' .. '7' )+
            {
            match("0o"); 

            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:359:28: ( '0' .. '7' )+
            int cnt16=0;
            loop16:
            do {
                int alt16=2;
                int LA16_0 = input.LA(1);

                if ( ((LA16_0>='0' && LA16_0<='7')) ) {
                    alt16=1;
                }


                switch (alt16) {
            	case 1 :
            	    // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:359:29: '0' .. '7'
            	    {
            	    matchRange('0','7'); 

            	    }
            	    break;

            	default :
            	    if ( cnt16 >= 1 ) break loop16;
                        EarlyExitException eee =
                            new EarlyExitException(16, input);
                        throw eee;
                }
                cnt16++;
            } while (true);


            }

        }
        finally {
        }
    }
    // $ANTLR end "OCT_DIGIT"

    // $ANTLR start "LIT"
    public final void mLIT() throws RecognitionException {
        try {
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:360:16: ( 'A' .. 'Z' | 'a' .. 'z' )
            // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:
            {
            if ( (input.LA(1)>='A' && input.LA(1)<='Z')||(input.LA(1)>='a' && input.LA(1)<='z') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

        }
        finally {
        }
    }
    // $ANTLR end "LIT"

    public void mTokens() throws RecognitionException {
        // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:1:8: ( COLONCOLON | COLON | COMA | DOTDOT | EQUAL | SEMICOLON | LP | RP | RBOX | LBOX | LB | RB | DQUOTE | FALSE | TRUE | ANY | ARRAY | BOOL | CASE | CONSTRAINT | ELSE | ELSEIF | ENDIF | ENUM | FLOAT | FUNCTION | IF | INCLUDE | INT | LET | MAXIMIZE | MINIMIZE | OF | SATISFY | OUTPUT | PAR | PREDICATE | RECORD | SET | SHOW | SHOWCOND | SOLVE | STRING | TEST | THEN | TUPLE | TYPE | VAR | VARIANT_RECORD | WHERE | INT_LITERAL | FLOAT_LITERAL | STRING_LITERAL | IDENT )
        int alt17=54;
        alt17 = dfa17.predict(input);
        switch (alt17) {
            case 1 :
                // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:1:10: COLONCOLON
                {
                mCOLONCOLON(); 

                }
                break;
            case 2 :
                // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:1:21: COLON
                {
                mCOLON(); 

                }
                break;
            case 3 :
                // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:1:27: COMA
                {
                mCOMA(); 

                }
                break;
            case 4 :
                // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:1:32: DOTDOT
                {
                mDOTDOT(); 

                }
                break;
            case 5 :
                // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:1:39: EQUAL
                {
                mEQUAL(); 

                }
                break;
            case 6 :
                // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:1:45: SEMICOLON
                {
                mSEMICOLON(); 

                }
                break;
            case 7 :
                // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:1:55: LP
                {
                mLP(); 

                }
                break;
            case 8 :
                // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:1:58: RP
                {
                mRP(); 

                }
                break;
            case 9 :
                // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:1:61: RBOX
                {
                mRBOX(); 

                }
                break;
            case 10 :
                // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:1:66: LBOX
                {
                mLBOX(); 

                }
                break;
            case 11 :
                // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:1:71: LB
                {
                mLB(); 

                }
                break;
            case 12 :
                // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:1:74: RB
                {
                mRB(); 

                }
                break;
            case 13 :
                // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:1:77: DQUOTE
                {
                mDQUOTE(); 

                }
                break;
            case 14 :
                // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:1:84: FALSE
                {
                mFALSE(); 

                }
                break;
            case 15 :
                // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:1:90: TRUE
                {
                mTRUE(); 

                }
                break;
            case 16 :
                // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:1:95: ANY
                {
                mANY(); 

                }
                break;
            case 17 :
                // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:1:99: ARRAY
                {
                mARRAY(); 

                }
                break;
            case 18 :
                // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:1:105: BOOL
                {
                mBOOL(); 

                }
                break;
            case 19 :
                // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:1:110: CASE
                {
                mCASE(); 

                }
                break;
            case 20 :
                // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:1:115: CONSTRAINT
                {
                mCONSTRAINT(); 

                }
                break;
            case 21 :
                // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:1:126: ELSE
                {
                mELSE(); 

                }
                break;
            case 22 :
                // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:1:131: ELSEIF
                {
                mELSEIF(); 

                }
                break;
            case 23 :
                // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:1:138: ENDIF
                {
                mENDIF(); 

                }
                break;
            case 24 :
                // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:1:144: ENUM
                {
                mENUM(); 

                }
                break;
            case 25 :
                // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:1:149: FLOAT
                {
                mFLOAT(); 

                }
                break;
            case 26 :
                // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:1:155: FUNCTION
                {
                mFUNCTION(); 

                }
                break;
            case 27 :
                // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:1:164: IF
                {
                mIF(); 

                }
                break;
            case 28 :
                // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:1:167: INCLUDE
                {
                mINCLUDE(); 

                }
                break;
            case 29 :
                // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:1:175: INT
                {
                mINT(); 

                }
                break;
            case 30 :
                // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:1:179: LET
                {
                mLET(); 

                }
                break;
            case 31 :
                // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:1:183: MAXIMIZE
                {
                mMAXIMIZE(); 

                }
                break;
            case 32 :
                // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:1:192: MINIMIZE
                {
                mMINIMIZE(); 

                }
                break;
            case 33 :
                // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:1:201: OF
                {
                mOF(); 

                }
                break;
            case 34 :
                // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:1:204: SATISFY
                {
                mSATISFY(); 

                }
                break;
            case 35 :
                // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:1:212: OUTPUT
                {
                mOUTPUT(); 

                }
                break;
            case 36 :
                // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:1:219: PAR
                {
                mPAR(); 

                }
                break;
            case 37 :
                // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:1:223: PREDICATE
                {
                mPREDICATE(); 

                }
                break;
            case 38 :
                // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:1:233: RECORD
                {
                mRECORD(); 

                }
                break;
            case 39 :
                // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:1:240: SET
                {
                mSET(); 

                }
                break;
            case 40 :
                // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:1:244: SHOW
                {
                mSHOW(); 

                }
                break;
            case 41 :
                // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:1:249: SHOWCOND
                {
                mSHOWCOND(); 

                }
                break;
            case 42 :
                // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:1:258: SOLVE
                {
                mSOLVE(); 

                }
                break;
            case 43 :
                // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:1:264: STRING
                {
                mSTRING(); 

                }
                break;
            case 44 :
                // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:1:271: TEST
                {
                mTEST(); 

                }
                break;
            case 45 :
                // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:1:276: THEN
                {
                mTHEN(); 

                }
                break;
            case 46 :
                // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:1:281: TUPLE
                {
                mTUPLE(); 

                }
                break;
            case 47 :
                // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:1:287: TYPE
                {
                mTYPE(); 

                }
                break;
            case 48 :
                // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:1:292: VAR
                {
                mVAR(); 

                }
                break;
            case 49 :
                // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:1:296: VARIANT_RECORD
                {
                mVARIANT_RECORD(); 

                }
                break;
            case 50 :
                // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:1:311: WHERE
                {
                mWHERE(); 

                }
                break;
            case 51 :
                // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:1:317: INT_LITERAL
                {
                mINT_LITERAL(); 

                }
                break;
            case 52 :
                // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:1:329: FLOAT_LITERAL
                {
                mFLOAT_LITERAL(); 

                }
                break;
            case 53 :
                // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:1:343: STRING_LITERAL
                {
                mSTRING_LITERAL(); 

                }
                break;
            case 54 :
                // /media/Documents/c-choco/sources/choco/trunk/choco-contribs/src/main/resources/mzn/FlatZinc.g:1:358: IDENT
                {
                mIDENT(); 

                }
                break;

        }

    }


    protected DFA11 dfa11 = new DFA11(this);
    protected DFA17 dfa17 = new DFA17(this);
    static final String DFA11_eotS =
        "\3\uffff\1\4\1\uffff";
    static final String DFA11_eofS =
        "\5\uffff";
    static final String DFA11_minS =
        "\1\56\1\60\1\uffff\1\60\1\uffff";
    static final String DFA11_maxS =
        "\1\145\1\71\1\uffff\1\145\1\uffff";
    static final String DFA11_acceptS =
        "\2\uffff\1\2\1\uffff\1\1";
    static final String DFA11_specialS =
        "\5\uffff}>";
    static final String[] DFA11_transitionS = {
            "\1\1\26\uffff\1\2\37\uffff\1\2",
            "\12\3",
            "",
            "\12\3\13\uffff\1\2\37\uffff\1\2",
            ""
    };

    static final short[] DFA11_eot = DFA.unpackEncodedString(DFA11_eotS);
    static final short[] DFA11_eof = DFA.unpackEncodedString(DFA11_eofS);
    static final char[] DFA11_min = DFA.unpackEncodedStringToUnsignedChars(DFA11_minS);
    static final char[] DFA11_max = DFA.unpackEncodedStringToUnsignedChars(DFA11_maxS);
    static final short[] DFA11_accept = DFA.unpackEncodedString(DFA11_acceptS);
    static final short[] DFA11_special = DFA.unpackEncodedString(DFA11_specialS);
    static final short[][] DFA11_transition;

    static {
        int numStates = DFA11_transitionS.length;
        DFA11_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA11_transition[i] = DFA.unpackEncodedString(DFA11_transitionS[i]);
        }
    }

    class DFA11 extends DFA {

        public DFA11(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 11;
            this.eot = DFA11_eot;
            this.eof = DFA11_eof;
            this.min = DFA11_min;
            this.max = DFA11_max;
            this.accept = DFA11_accept;
            this.special = DFA11_special;
            this.transition = DFA11_transition;
        }
        public String getDescription() {
            return "353:31: ( '.' | ( '.' ( DIGIT )+ )? ( 'E' | 'e' ) ( '-' | '+' )? )";
        }
    }
    static final String DFA17_eotS =
        "\1\uffff\1\41\12\uffff\1\42\17\37\1\uffff\2\104\5\uffff\17\37\1"+
        "\126\4\37\1\134\13\37\2\uffff\10\37\1\160\7\37\1\uffff\1\37\1\171"+
        "\1\172\2\37\1\uffff\2\37\1\177\3\37\1\u0083\2\37\1\u0087\4\37\1"+
        "\u008c\1\u008d\1\u008e\1\37\1\u0090\1\uffff\1\37\1\u0092\1\u0093"+
        "\1\37\1\u0096\1\37\1\u0098\1\37\2\uffff\4\37\1\uffff\1\u009f\2\37"+
        "\1\uffff\3\37\1\uffff\1\37\1\u00a6\1\u00a7\1\37\3\uffff\1\u00a9"+
        "\1\uffff\1\u00aa\2\uffff\2\37\1\uffff\1\u00ad\1\uffff\6\37\1\uffff"+
        "\1\u00b4\4\37\1\u00b9\2\uffff\1\37\2\uffff\1\37\1\u00bc\1\uffff"+
        "\3\37\1\u00c0\2\37\1\uffff\1\u00c3\1\37\1\u00c5\1\37\1\uffff\2\37"+
        "\1\uffff\1\u00c9\2\37\1\uffff\1\u00cc\1\37\1\uffff\1\37\1\uffff"+
        "\1\37\1\u00d0\1\37\1\uffff\1\u00d2\1\u00d3\1\uffff\3\37\1\uffff"+
        "\1\37\2\uffff\1\u00d8\1\u00d9\1\37\1\u00db\2\uffff\1\37\1\uffff"+
        "\3\37\1\u00e0\1\uffff";
    static final String DFA17_eofS =
        "\u00e1\uffff";
    static final String DFA17_minS =
        "\1\42\1\72\12\uffff\1\0\1\141\1\145\1\156\1\157\1\141\1\154\1\146"+
        "\1\145\1\141\1\146\2\141\1\145\1\141\1\150\1\60\2\56\5\uffff\1\154"+
        "\1\157\1\156\1\165\1\163\1\145\2\160\1\171\1\162\1\157\1\163\1\156"+
        "\1\163\1\144\1\60\1\143\1\164\1\170\1\156\1\60\3\164\1\157\1\154"+
        "\2\162\1\145\1\143\1\162\1\145\2\uffff\1\163\1\141\1\143\1\145\1"+
        "\164\1\156\1\154\1\145\1\60\1\141\1\154\1\145\1\163\1\145\1\151"+
        "\1\155\1\uffff\1\154\2\60\2\151\1\uffff\1\160\1\151\1\60\1\167\1"+
        "\166\1\151\1\60\1\144\1\157\1\60\1\162\1\145\2\164\3\60\1\145\1"+
        "\60\1\uffff\1\171\2\60\1\164\1\60\1\146\1\60\1\165\2\uffff\2\155"+
        "\1\165\1\163\1\uffff\1\60\1\145\1\156\1\uffff\1\151\1\162\1\141"+
        "\1\uffff\1\145\2\60\1\151\3\uffff\1\60\1\uffff\1\60\2\uffff\1\162"+
        "\1\146\1\uffff\1\60\1\uffff\1\144\2\151\1\164\1\146\1\143\1\uffff"+
        "\1\60\1\147\1\143\1\144\1\156\1\60\2\uffff\1\157\2\uffff\1\141\1"+
        "\60\1\uffff\1\145\2\172\1\60\1\171\1\157\1\uffff\1\60\1\141\1\60"+
        "\1\164\1\uffff\1\156\1\151\1\uffff\1\60\2\145\1\uffff\1\60\1\156"+
        "\1\uffff\1\164\1\uffff\1\137\1\60\1\156\1\uffff\2\60\1\uffff\1\144"+
        "\1\145\1\162\1\uffff\1\164\2\uffff\2\60\1\145\1\60\2\uffff\1\143"+
        "\1\uffff\1\157\1\162\1\144\1\60\1\uffff";
    static final String DFA17_maxS =
        "\1\175\1\72\12\uffff\1\uffff\1\165\1\171\1\162\2\157\2\156\1\145"+
        "\1\151\1\165\1\164\1\162\1\145\1\141\1\150\1\71\2\145\5\uffff\1"+
        "\154\1\157\1\156\1\165\1\163\1\145\2\160\1\171\1\162\1\157\1\163"+
        "\1\156\1\163\1\165\1\172\2\164\1\170\1\156\1\172\3\164\1\157\1\154"+
        "\2\162\1\145\1\143\1\162\1\145\2\uffff\1\163\1\141\1\143\1\145\1"+
        "\164\1\156\1\154\1\145\1\172\1\141\1\154\1\145\1\163\1\145\1\151"+
        "\1\155\1\uffff\1\154\2\172\2\151\1\uffff\1\160\1\151\1\172\1\167"+
        "\1\166\1\151\1\172\1\144\1\157\1\172\1\162\1\145\2\164\3\172\1\145"+
        "\1\172\1\uffff\1\171\2\172\1\164\1\172\1\146\1\172\1\165\2\uffff"+
        "\2\155\1\165\1\163\1\uffff\1\172\1\145\1\156\1\uffff\1\151\1\162"+
        "\1\141\1\uffff\1\145\2\172\1\151\3\uffff\1\172\1\uffff\1\172\2\uffff"+
        "\1\162\1\146\1\uffff\1\172\1\uffff\1\144\2\151\1\164\1\146\1\143"+
        "\1\uffff\1\172\1\147\1\143\1\144\1\156\1\172\2\uffff\1\157\2\uffff"+
        "\1\141\1\172\1\uffff\1\145\3\172\1\171\1\157\1\uffff\1\172\1\141"+
        "\1\172\1\164\1\uffff\1\156\1\151\1\uffff\1\172\2\145\1\uffff\1\172"+
        "\1\156\1\uffff\1\164\1\uffff\1\137\1\172\1\156\1\uffff\2\172\1\uffff"+
        "\1\144\1\145\1\162\1\uffff\1\164\2\uffff\2\172\1\145\1\172\2\uffff"+
        "\1\143\1\uffff\1\157\1\162\1\144\1\172\1\uffff";
    static final String DFA17_acceptS =
        "\2\uffff\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\1\13\1\14\23\uffff\1"+
        "\66\1\1\1\2\1\15\1\65\40\uffff\1\63\1\64\20\uffff\1\33\5\uffff\1"+
        "\41\23\uffff\1\20\10\uffff\1\35\1\36\4\uffff\1\47\3\uffff\1\44\3"+
        "\uffff\1\60\4\uffff\1\17\1\54\1\55\1\uffff\1\57\1\uffff\1\22\1\23"+
        "\2\uffff\1\25\1\uffff\1\30\6\uffff\1\50\6\uffff\1\16\1\31\1\uffff"+
        "\1\56\1\21\2\uffff\1\27\6\uffff\1\52\4\uffff\1\62\2\uffff\1\26\3"+
        "\uffff\1\43\2\uffff\1\53\1\uffff\1\46\3\uffff\1\34\2\uffff\1\42"+
        "\3\uffff\1\32\1\uffff\1\37\1\40\4\uffff\1\51\1\45\1\uffff\1\24\4"+
        "\uffff\1\61";
    static final String DFA17_specialS =
        "\14\uffff\1\0\u00d4\uffff}>";
    static final String[] DFA17_transitionS = {
            "\1\14\5\uffff\1\6\1\7\2\uffff\1\2\1\34\1\3\1\uffff\1\35\11\36"+
            "\1\1\1\5\1\uffff\1\4\3\uffff\32\37\1\11\1\uffff\1\10\3\uffff"+
            "\1\17\1\20\1\21\1\37\1\22\1\15\2\37\1\23\2\37\1\24\1\25\1\37"+
            "\1\26\1\30\1\37\1\31\1\27\1\16\1\37\1\32\1\33\3\37\1\12\1\uffff"+
            "\1\13",
            "\1\40",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\12\43\1\uffff\1\43\2\uffff\ufff2\43",
            "\1\44\12\uffff\1\45\10\uffff\1\46",
            "\1\50\2\uffff\1\51\11\uffff\1\47\2\uffff\1\52\3\uffff\1\53",
            "\1\54\3\uffff\1\55",
            "\1\56",
            "\1\57\15\uffff\1\60",
            "\1\61\1\uffff\1\62",
            "\1\63\7\uffff\1\64",
            "\1\65",
            "\1\66\7\uffff\1\67",
            "\1\70\16\uffff\1\71",
            "\1\72\3\uffff\1\73\2\uffff\1\74\6\uffff\1\75\4\uffff\1\76",
            "\1\77\20\uffff\1\100",
            "\1\101",
            "\1\102",
            "\1\103",
            "\1\35\11\36",
            "\1\105\1\uffff\12\36\13\uffff\1\105\37\uffff\1\105",
            "\1\105\1\uffff\12\36\13\uffff\1\105\37\uffff\1\105",
            "",
            "",
            "",
            "",
            "",
            "\1\106",
            "\1\107",
            "\1\110",
            "\1\111",
            "\1\112",
            "\1\113",
            "\1\114",
            "\1\115",
            "\1\116",
            "\1\117",
            "\1\120",
            "\1\121",
            "\1\122",
            "\1\123",
            "\1\124\20\uffff\1\125",
            "\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff\32\37",
            "\1\127\20\uffff\1\130",
            "\1\131",
            "\1\132",
            "\1\133",
            "\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff\32\37",
            "\1\135",
            "\1\136",
            "\1\137",
            "\1\140",
            "\1\141",
            "\1\142",
            "\1\143",
            "\1\144",
            "\1\145",
            "\1\146",
            "\1\147",
            "",
            "",
            "\1\150",
            "\1\151",
            "\1\152",
            "\1\153",
            "\1\154",
            "\1\155",
            "\1\156",
            "\1\157",
            "\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff\32\37",
            "\1\161",
            "\1\162",
            "\1\163",
            "\1\164",
            "\1\165",
            "\1\166",
            "\1\167",
            "",
            "\1\170",
            "\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff\32\37",
            "\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff\32\37",
            "\1\173",
            "\1\174",
            "",
            "\1\175",
            "\1\176",
            "\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff\32\37",
            "\1\u0080",
            "\1\u0081",
            "\1\u0082",
            "\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff\32\37",
            "\1\u0084",
            "\1\u0085",
            "\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff\10\37\1\u0086\21"+
            "\37",
            "\1\u0088",
            "\1\u0089",
            "\1\u008a",
            "\1\u008b",
            "\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff\32\37",
            "\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff\32\37",
            "\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff\32\37",
            "\1\u008f",
            "\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff\32\37",
            "",
            "\1\u0091",
            "\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff\32\37",
            "\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff\32\37",
            "\1\u0094",
            "\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff\10\37\1\u0095\21"+
            "\37",
            "\1\u0097",
            "\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff\32\37",
            "\1\u0099",
            "",
            "",
            "\1\u009a",
            "\1\u009b",
            "\1\u009c",
            "\1\u009d",
            "",
            "\12\37\7\uffff\32\37\4\uffff\1\u009e\1\uffff\32\37",
            "\1\u00a0",
            "\1\u00a1",
            "",
            "\1\u00a2",
            "\1\u00a3",
            "\1\u00a4",
            "",
            "\1\u00a5",
            "\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff\32\37",
            "\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff\32\37",
            "\1\u00a8",
            "",
            "",
            "",
            "\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff\32\37",
            "",
            "\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff\32\37",
            "",
            "",
            "\1\u00ab",
            "\1\u00ac",
            "",
            "\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff\32\37",
            "",
            "\1\u00ae",
            "\1\u00af",
            "\1\u00b0",
            "\1\u00b1",
            "\1\u00b2",
            "\1\u00b3",
            "",
            "\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff\32\37",
            "\1\u00b5",
            "\1\u00b6",
            "\1\u00b7",
            "\1\u00b8",
            "\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff\32\37",
            "",
            "",
            "\1\u00ba",
            "",
            "",
            "\1\u00bb",
            "\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff\32\37",
            "",
            "\1\u00bd",
            "\1\u00be",
            "\1\u00bf",
            "\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff\32\37",
            "\1\u00c1",
            "\1\u00c2",
            "",
            "\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff\32\37",
            "\1\u00c4",
            "\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff\32\37",
            "\1\u00c6",
            "",
            "\1\u00c7",
            "\1\u00c8",
            "",
            "\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff\32\37",
            "\1\u00ca",
            "\1\u00cb",
            "",
            "\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff\32\37",
            "\1\u00cd",
            "",
            "\1\u00ce",
            "",
            "\1\u00cf",
            "\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff\32\37",
            "\1\u00d1",
            "",
            "\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff\32\37",
            "\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff\32\37",
            "",
            "\1\u00d4",
            "\1\u00d5",
            "\1\u00d6",
            "",
            "\1\u00d7",
            "",
            "",
            "\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff\32\37",
            "\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff\32\37",
            "\1\u00da",
            "\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff\32\37",
            "",
            "",
            "\1\u00dc",
            "",
            "\1\u00dd",
            "\1\u00de",
            "\1\u00df",
            "\12\37\7\uffff\32\37\4\uffff\1\37\1\uffff\32\37",
            ""
    };

    static final short[] DFA17_eot = DFA.unpackEncodedString(DFA17_eotS);
    static final short[] DFA17_eof = DFA.unpackEncodedString(DFA17_eofS);
    static final char[] DFA17_min = DFA.unpackEncodedStringToUnsignedChars(DFA17_minS);
    static final char[] DFA17_max = DFA.unpackEncodedStringToUnsignedChars(DFA17_maxS);
    static final short[] DFA17_accept = DFA.unpackEncodedString(DFA17_acceptS);
    static final short[] DFA17_special = DFA.unpackEncodedString(DFA17_specialS);
    static final short[][] DFA17_transition;

    static {
        int numStates = DFA17_transitionS.length;
        DFA17_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA17_transition[i] = DFA.unpackEncodedString(DFA17_transitionS[i]);
        }
    }

    class DFA17 extends DFA {

        public DFA17(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 17;
            this.eot = DFA17_eot;
            this.eof = DFA17_eof;
            this.min = DFA17_min;
            this.max = DFA17_max;
            this.accept = DFA17_accept;
            this.special = DFA17_special;
            this.transition = DFA17_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( COLONCOLON | COLON | COMA | DOTDOT | EQUAL | SEMICOLON | LP | RP | RBOX | LBOX | LB | RB | DQUOTE | FALSE | TRUE | ANY | ARRAY | BOOL | CASE | CONSTRAINT | ELSE | ELSEIF | ENDIF | ENUM | FLOAT | FUNCTION | IF | INCLUDE | INT | LET | MAXIMIZE | MINIMIZE | OF | SATISFY | OUTPUT | PAR | PREDICATE | RECORD | SET | SHOW | SHOWCOND | SOLVE | STRING | TEST | THEN | TUPLE | TYPE | VAR | VARIANT_RECORD | WHERE | INT_LITERAL | FLOAT_LITERAL | STRING_LITERAL | IDENT );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            IntStream input = _input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA17_12 = input.LA(1);

                        s = -1;
                        if ( ((LA17_12>='\u0000' && LA17_12<='\t')||LA17_12=='\u000B'||(LA17_12>='\u000E' && LA17_12<='\uFFFF')) ) {s = 35;}

                        else s = 34;

                        if ( s>=0 ) return s;
                        break;
            }
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 17, _s, input);
            error(nvae);
            throw nvae;
        }
    }
 

}
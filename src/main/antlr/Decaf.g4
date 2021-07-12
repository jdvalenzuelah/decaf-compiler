grammar Decaf;


/*
  LEXER RULES
  -----------
  Lexer rules define the basic syntax of individual words and symbols of a
  valid Decaf program. Lexer rules follow regular expression syntax.
  Complete the lexer rules following the Decaf Language Specification.
*/



CLASS : 'class';

INT : 'int';

RETURN : 'return';

VOID : 'void';

IF : 'if';

ELSE : 'else';

FOR : 'for';

BREAK : 'break';

CONTINUE : 'continue';

CALLOUT : 'callout';

TRUE : 'True' ;

FALSE : 'False' ;

BOOLEAN : 'boolean';

LCURLY : '{';

RCURLY : '}';

LBRACE : '(';

RBRACE : ')';


LSQUARE : '[';

RSQUARE : ']';
ADD : '+';

SUB : '-';

MUL : '*';

DIV : '/';

EQ : '=';

SEMI : ';';

COMMA : ',';

AND : '&&';

LESS : '<';

GREATER : '>';

LESSEQUAL : '<=' ;

GREATEREQUAL : '>=' ;

EQUALTO : '==' ;

NOTEQUAL : '!=' ;

EXCLAMATION : '!';



fragment CHAR : (' '..'!') | ('#'..'&') | ('('..'[') | (']'..'~') | ('\\'[']) | ('\\"') | ('\\') | ('\t') | ('\n');

CHAR_LITERAL : '\'' CHAR '\'';

//STRING_LITERAL : '"' CHAR+ '"' ;


HEXMARK : '0x';

fragment HEXA : [a-fA-F];

fragment HEXDIGIT : DIGIT | HEXA ;

HEX_LITERAL : HEXMARK HEXDIGIT+;


STRING : '"' (ESC|.)*? '"';

fragment ESC : '\\"' | '\\\\';




fragment DIGIT : [0-9];

DECIMAL_LITERAL : DIGIT(DIGIT)*;



COMMENT : '//' ~('\n')* '\n' -> skip;

WS : (' ' | '\n' | '\t' | '\r') + -> skip;

fragment ALPHA : [a-zA-Z] | '_';

fragment ALPHA_NUM : ALPHA | DIGIT;



ID : ALPHA ALPHA_NUM*;

INT_LITERAL : DECIMAL_LITERAL | HEX_LITERAL;

BOOL_LITERAL : TRUE | FALSE;

/*
  PARSER RULES
  ------------
  Parser rules are all lower case, and make use of lexer rules defined above
  and other parser rules defined below. Parser rules also follow regular
  expression syntax. Complete the parser rules following the Decaf Language
  Specification.
*/




program : CLASS ID LCURLY field_decl* method_decl* RCURLY EOF;

field_name : ID | ID LSQUARE INT_LITERAL RSQUARE;

field_decl : datatype field_name (COMMA field_name)* SEMI;

method_decl : (datatype | VOID) ID LBRACE ((datatype ID) (COMMA datatype ID)*)? RBRACE block;

block : LCURLY var_decl* statement* RCURLY;

var_decl : datatype ID (COMMA ID)* SEMI;


datatype : INT | BOOLEAN;

statement : location assign_op expr SEMI
        | method_call SEMI
        | IF LBRACE expr RBRACE block (ELSE block)?
        | FOR ID EQ expr COMMA expr block
        | RETURN (expr)? SEMI
        | BREAK SEMI
        | CONTINUE SEMI
        | block;

assign_op : EQ
          | ADD EQ
          | SUB EQ;


method_call : method_name LBRACE (expr (COMMA expr)*)? RBRACE
            | CALLOUT LBRACE STRING(COMMA callout_arg (COMMA callout_arg)*) RBRACE;


method_name : ID;

location : ID | ID LSQUARE expr RSQUARE;


expr : location
     | method_call
     | literal
     | expr bin_op expr
     | SUB expr
     | EXCLAMATION expr
     | LBRACE expr RBRACE;

 callout_arg : expr
            | STRING ;

bin_op : arith_op
      | rel_op
      | eq_op
      | cond_op;


arith_op : ADD | SUB | MUL | DIV | '%' ;

rel_op : LESS | GREATER | LESSEQUAL | GREATEREQUAL ;

eq_op : EQUALTO | NOTEQUAL ;

cond_op : AND | '||' ;

literal : INT_LITERAL | CHAR_LITERAL | BOOL_LITERAL ;
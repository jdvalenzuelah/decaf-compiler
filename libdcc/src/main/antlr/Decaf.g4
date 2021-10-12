grammar Decaf;

/* Lexer tokens */


CLASS: 'class';

STRUCT: 'struct';

VOID: 'void';

IF: 'if';

ELSE: 'else';

WHILE: 'while';

RETURN: 'return';

INT: 'int';

CHAR: 'char';

BOOLEAN: 'boolean';

PROGRAM: 'Program'; // Just class named Program allowed?

OBRACKET: '[';

CBRACKET: ']';

OCURLY: '{';

CCURLY: '}';

OPARENTHESIS: '(';

CPARENTHESIS: ')';

EQ: '=';

SUB: '-';

ADD: '+';

MUL: '*';

DIV: '/';

MOD: '%';

GT: '>';

LT: '<';

GTE: '>=';

LTE: '<=';

EQUALTO:'==';

NOTEQUAL:'!=';

AND: '&&';

OR: '||';

SEMICOLON: ';';

COMMA: ',';

//SQUOTE: '\'';

DOT: '.'; // needed?

EXCL: '!';

COMMENT : '//' ~('\n')* '\n' -> skip;

WS : (' ' | '\n' | '\t' | '\r') + -> skip;

fragment ESC: '\\\\' | '\\\'' |  '\\"' | '\\t' | '\\n';

fragment LETTER: [a-zA-Z];

fragment DIGIT: [0-9];

fragment NUM: DIGIT (DIGIT)*;

fragment CHARACTER: LETTER | ESC;

fragment TRUE: 'true';

fragment FALSE: 'false';

INT_LITERAL: NUM;

CHAR_LITERAL: '\'' CHARACTER '\'';

BOOL_LITERAL: TRUE | FALSE;

ID: LETTER (LETTER|DIGIT)*;

/* Parser rules */

program: CLASS PROGRAM OCURLY (var_decl | struct_decl | method_decl)* CCURLY;

var_decl: prop_decl SEMICOLON | array_decl SEMICOLON;

prop_decl: var_type ID;

array_decl: var_type ID OBRACKET INT_LITERAL CBRACKET;

/* array_decl_no_size: var_type ID OBRACKET CBRACKET; */

struct_decl: STRUCT ID OCURLY (var_decl)* CCURLY SEMICOLON?;

var_type: INT | CHAR | BOOLEAN | (STRUCT ID) | VOID /* | struct_decl */;

method_decl: method_sign block;

method_sign: method_type ID OPARENTHESIS (((parameter) (COMMA parameter)*)? | VOID) CPARENTHESIS;

method_type: INT | CHAR | BOOLEAN | VOID;

parameter: simple_param | array_param;

simple_param: parameter_type ID;

array_param: parameter_type ID OBRACKET CBRACKET;

parameter_type: INT | CHAR | BOOLEAN;

block: OCURLY (var_decl | statement)* CCURLY;

statement: if_expr | while_expr | return_expr | method_call SEMICOLON | block | assignment SEMICOLON | expression SEMICOLON;

if_expr: if_block else_block?;

if_block: IF OPARENTHESIS expression CPARENTHESIS block;

else_block: ELSE block;

while_expr: WHILE OPARENTHESIS expression CPARENTHESIS block;

return_expr: RETURN (expression)? SEMICOLON;

assignment: location EQ expression;

location: var_location sub_location?;

var_location: (ID | location_array);

location_array: ID OBRACKET expression CBRACKET;

sub_location: DOT location;

method_call: ID OPARENTHESIS ((arg) (COMMA arg)*)? CPARENTHESIS;

arg: expression;

expression: method_call | location | literal
            | expression arith_op_mul expression
            | expression arith_op_sub expression
            | expression rel_op expression
            | expression eq_op expression
            | expression cond_op expression
            | unary_op expression
            | OPARENTHESIS expression CPARENTHESIS;

rel_op: GT | LT | GTE | LTE;

eq_op: EQUALTO | NOTEQUAL;

cond_op: AND | OR;

unary_op: SUB | EXCL;

arith_op_sub: ADD | SUB;

arith_op_mul: MUL | DIV | MOD;

literal: BOOL_LITERAL | INT_LITERAL | CHAR_LITERAL;

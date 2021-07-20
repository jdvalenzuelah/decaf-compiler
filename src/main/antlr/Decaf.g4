grammar Decaf;

/* Lexer tokens */


CLASS: 'class';

STRUCT: 'struct';

TRUE: 'true';

FALSE: 'false';

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

ID: LETTER (LETTER|DIGIT)*;

NUM: DIGIT (DIGIT)*;

fragment CHARACTER: LETTER | ESC;

INT_LITERAL: NUM;

CHAR_LITERAL: '\'' CHARACTER '\'';

BOOL_LITERAL: TRUE | FALSE;

/* Parser rules */

program: CLASS PROGRAM OCURLY (decl)* CCURLY;

decl: var_decl | struct_decl | method_decl;

var_decl: (var_type ID SEMICOLON) | (var_type ID OBRACKET NUM CBRACKET SEMICOLON);

struct_decl: STRUCT ID OCURLY (var_decl)* CCURLY;

var_type: INT | CHAR | BOOLEAN | (STRUCT ID) | struct_decl | VOID;

method_decl: method_type ID OPARENTHESIS ((parameter) (COMMA parameter)*)? CPARENTHESIS block;

method_type: INT | CHAR | BOOLEAN | VOID;

parameter: (parameter_type ID) | (parameter_type ID OCURLY CCURLY);

parameter_type: INT | CHAR | BOOLEAN;

block: OCURLY (var_decl)* (statement)* CCURLY;

statement:  (IF OPARENTHESIS expression CPARENTHESIS block (ELSE block)?)
          | (WHILE OPARENTHESIS expression CPARENTHESIS block)
          | (RETURN (expression)? SEMICOLON)
          | (method_call SEMICOLON)
          | (block)
          | (location EQ expression)
          | (expression? SEMICOLON);

location: (ID | (ID OBRACKET expression CBRACKET)) (DOT (ID | (ID OBRACKET expression CBRACKET)))?;

method_call: ID OPARENTHESIS ((arg) (COMMA arg)*)? CPARENTHESIS;

arg: expression;

expression: location
          | method_call
          | literal
          | expression op expression
          | SUB expression
          | EXCL expression
          | OPARENTHESIS expression CPARENTHESIS;

op: arith_op | rel_op | eq_op | cond_op;

arith_op: ADD | SUB | MUL | DIV | MOD;

rel_op: GT | LT | GTE | LTE;

eq_op: EQUALTO | NOTEQUAL;

cond_op: AND | OR;

literal: INT_LITERAL | CHAR_LITERAL | BOOL_LITERAL;

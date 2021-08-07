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

program: CLASS PROGRAM OCURLY (decl)* CCURLY;

decl: var_decl | struct_decl | method_decl;

var_decl: (prop_decl | array_decl) SEMICOLON;

prop_decl: var_type ID;

array_decl: var_type ID OBRACKET INT_LITERAL CBRACKET;

array_decl_no_size: var_type ID OBRACKET CBRACKET;

struct_decl: STRUCT ID OCURLY (var_decl)* CCURLY;

var_type: INT | CHAR | BOOLEAN | (STRUCT ID) | VOID /* | struct_decl */;

method_decl: method_sign block;

method_sign: method_type ID OPARENTHESIS ((parameter) (COMMA parameter)*)? CPARENTHESIS;

method_type: INT | CHAR | BOOLEAN | VOID;

parameter: simple_param | array_param;

simple_param: parameter_type ID;

array_param: parameter_type ID OBRACKET CBRACKET;

parameter_type: INT | CHAR | BOOLEAN;

block: OCURLY (var_decl | statement)* CCURLY;

statement: if_expr | while_expr | return_expr | method_call SEMICOLON | block | assignment | (expression? SEMICOLON);

if_expr: if_block else_block;

if_block: IF OPARENTHESIS expression CPARENTHESIS block;

else_block:(ELSE block)?;

while_expr: WHILE OPARENTHESIS expression CPARENTHESIS block;

return_expr: RETURN (expression)? SEMICOLON;

assignment: location EQ expression;

location: (ID | ID OBRACKET expression CBRACKET) (DOT location)?;

method_call: ID OPARENTHESIS ((arg) (COMMA arg)*)? CPARENTHESIS;

arg: array_decl_no_size | ID;

expression: equality | location | method_call | literal;

equality: comparison ( eq_op comparison )* (cond_op comparison)*;

comparison: term ((rel_op | eq_op)  term)*;

term: factor ((ADD | SUB) factor)*;

unary: (SUB | EXCL) unary | primary;

factor: unary ((DIV | MUL) unary)*;

primary: (literal | location) | OPARENTHESIS expression CPARENTHESIS;

rel_op: GT | LT | GTE | LTE;

eq_op: EQUALTO | NOTEQUAL;

cond_op: AND | OR;

literal: BOOL_LITERAL | INT_LITERAL | CHAR_LITERAL;

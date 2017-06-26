lexer grammar Step1LexerRules;

WORD        : ([A-z]|[0-9])+ ;
WS          : [ \t\r\n]+ -> skip ;
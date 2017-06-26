lexer grammar Step2LexerRules;

WORD    : ([A-z]|[0-9])+ ;
WS      : [ \t\r\n]+ -> skip ;
QUOTE   : ["];

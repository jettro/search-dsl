lexer grammar Step3LexerRules;

OR      : 'OR' | '||' ;
WORD    : ([A-z]|[0-9])+ ;
WS      : [ \t\r\n]+ -> skip ;
QUOTE   : ["];

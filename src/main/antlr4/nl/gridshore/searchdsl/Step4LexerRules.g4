lexer grammar Step4LexerRules;

AND     : 'AND' | '&&' ;
OR      : 'OR' | '||' ;
WORD    : ([A-z]|[0-9])+ ;
WS      : [ \t\r\n]+ -> skip ;
QUOTE   : ["];

lexer grammar CommonLexerRules;

OR      : 'OR' | '||' ;
AND     : 'AND' | '&&' ;

WORD                : ([A-z]|[0-9])+;
WS                  : [ \t\r\n]+ -> skip ;
QUOTE               : ["];
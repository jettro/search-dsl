lexer grammar CommonLexerRules;

OR      : 'OR' | '||' ;
AND     : 'AND' | '&&' ;
NOT     : 'NOT';

WORD                : ([A-z]|[0-9])+;
WS                  : [ \t\r\n]+ -> skip ;
QUOTE               : ["];
LEFTBRACKET         : [(];
RIGHTBRACKET        : [)];
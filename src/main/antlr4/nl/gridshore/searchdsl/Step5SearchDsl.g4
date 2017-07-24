grammar Step5SearchDsl;
import Step5LexerRules;

query               : expr | subQuery ;

subQuery            : LEFTBRACKET subQuery RIGHTBRACKET
                    | expr (AND expr)+
                    | expr (OR expr)+
                    | subQuery (AND subQuery)+
                    | subQuery (OR subQuery)+
                    | subQuery AND expr
                    | subQuery OR expr
                    | expr AND subQuery
                    | expr OR subQuery
                    | NOT subQuery;

expr                : term | notTerm ;
notTerm             : NOT term ;
term                : WORD+|quotedTerm ;
quotedTerm          : QUOTE WORD+ QUOTE ;

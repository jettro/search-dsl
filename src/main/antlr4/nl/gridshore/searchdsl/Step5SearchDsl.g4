grammar Step5SearchDsl;
import Step5LexerRules;

query               : anyTerm | subQuery ;

subQuery            : LEFTBRACKET subQuery RIGHTBRACKET
                    | anyTerm (AND anyTerm)+
                    | anyTerm (OR anyTerm)+
                    | subQuery (AND subQuery)+
                    | subQuery (OR subQuery)+
                    | subQuery AND anyTerm
                    | subQuery OR anyTerm
                    | anyTerm AND subQuery
                    | anyTerm OR subQuery;

anyTerm             : term | notTerm ;
notTerm             : NOT term ;
term                : WORD+|quotedTerm ;
quotedTerm          : QUOTE WORD+ QUOTE ;

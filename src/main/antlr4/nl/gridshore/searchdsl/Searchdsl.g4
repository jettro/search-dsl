grammar Searchdsl;
import CommonLexerRules;

/*
 * Grammer rules
 *
 *  term
 *  term OR term
 *  term AND term
 *  term AND term OR term
 *  term term AND term || term
 */

query               : expr | subQuery ;


subQuery            : LEFTBRACKET subQuery RIGHTBRACKET
                    | NOT subQuery
                    | expr (AND expr)+
                    | expr (OR expr)+
                    | subQuery (AND subQuery)+
                    | subQuery (OR subQuery)+
                    | subQuery AND expr
                    | subQuery OR expr
                    | expr AND subQuery
                    | expr OR subQuery;

expr                : term | notTerm ;
notTerm             : NOT term ;
term                : WORD+|quotedTerm ;
quotedTerm          : QUOTE WORD+ QUOTE ;

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

query               : andQuery | orQuery | term ;

andQuery            : term (AND term)+ ;
orQuery             : orExpr (OR orExpr)+ ;
orExpr              : term|andQuery;
term                : WORD+|quotedTerm;
quotedTerm          : QUOTE WORD+ QUOTE ;

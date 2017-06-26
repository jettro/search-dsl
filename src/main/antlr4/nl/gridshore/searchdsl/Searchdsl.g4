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

orQuery             : orExpr (OR orExpr)+ ;
orExpr              : term|andQuery;

andQuery            : term (AND term)+ ;
term                : WORD+|quotedTerm;
quotedTerm          : QUOTE WORD+ QUOTE ;

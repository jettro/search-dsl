grammar Step4SearchDsl;
import Step4LexerRules;

query               : term | orQuery | andQuery ;

orQuery             : orExpr (OR orExpr)+ ;
orExpr              : term|andQuery;

andQuery            : term (AND term)+ ;
term                : WORD+|quotedTerm;
quotedTerm          : QUOTE WORD+ QUOTE ;

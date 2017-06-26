grammar Step3SearchDsl;
import Step3LexerRules;

query               : term | orQuery ;

orQuery             : term (OR term)+ ;

term                : WORD+|quotedTerm;
quotedTerm          : QUOTE WORD+ QUOTE ;

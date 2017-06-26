grammar Step2SearchDsl;
import Step2LexerRules;

query               : term ;

term                : WORD+|quotedTerm;
quotedTerm          : QUOTE WORD+ QUOTE ;

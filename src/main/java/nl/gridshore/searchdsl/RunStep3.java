package nl.gridshore.searchdsl;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;

import java.util.List;

/**
 * In this Step we add the option to do an OR query, have multiple terms separated by OR. In the Lexer we add a type
 * for OR which can be 'OR' or '||'
 */
public class RunStep3 extends AbstractRunStep {

    public RunStep3(String phrase) {
        printSource(phrase);

        Lexer lexer = new Step3LexerRules(CharStreams.fromString(phrase));
        CommonTokenStream commonTokenStream = new CommonTokenStream(lexer);

        Step3SearchDslParser parser = new Step3SearchDslParser(commonTokenStream);
        Step3SearchDslParser.QueryContext queryContext = parser.query();

        if (queryContext.orQuery() != null) {
            handleOrContext(queryContext.orQuery());
        } else {
            handleTermContext(queryContext.term());
        }
    }

    public static void main(String[] args) {
        new RunStep3("apple");
        new RunStep3("apple OR juice");
        new RunStep3("\"apple juice\" OR applejuice");
    }

    private void handleOrContext(Step3SearchDslParser.OrQueryContext orQueryContext) {
        System.out.println("Or query: ");
        List<Step3SearchDslParser.TermContext> termContexts = orQueryContext.term();
        termContexts.forEach(this::handleTermContext);
    }

    private void handleTermContext(Step3SearchDslParser.TermContext termContext) {
        if (null != termContext.quotedTerm()) {
            handleQuotedTerm(termContext.quotedTerm());
        } else {
            handleWordTokens(termContext.WORD());
        }
    }

    private void handleQuotedTerm(Step3SearchDslParser.QuotedTermContext quotedTermContext) {
        System.out.print("QUOTED ");
        handleWordTokens(quotedTermContext.WORD());
    }

}

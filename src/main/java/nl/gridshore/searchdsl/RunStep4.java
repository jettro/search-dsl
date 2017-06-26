package nl.gridshore.searchdsl;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;

import java.util.List;

/**
 * In step for we make it a bit more complicated. We add the option to do AND for two terms. With AND and OR it becomes
 * a bit more complicated. The Lexer just contains the type AND. The Grammar now has an improved version of the orQuery.
 * The orQuery has two or more expressions with OR between two expressions. An expression can be a term or an andQuery.
 */
public class RunStep4 extends AbstractRunStep {

    private RunStep4(String phrase) {
        printSource(phrase);

        Lexer lexer = new Step4LexerRules(CharStreams.fromString(phrase));
        CommonTokenStream commonTokenStream = new CommonTokenStream(lexer);

        Step4SearchDslParser parser = new Step4SearchDslParser(commonTokenStream);
        Step4SearchDslParser.QueryContext queryContext = parser.query();

        if (queryContext.orQuery() != null) {
            handleOrQueryContext(queryContext.orQuery());
        } else if (queryContext.andQuery() != null) {
            handleAndQuery(queryContext.andQuery());
        } else {
            handleTermContext(queryContext.term());
        }
    }

    public static void main(String[] args) {
        new RunStep4("apple");
        new RunStep4("apple OR juice");
        new RunStep4("apple raspberry OR juice");
        new RunStep4("apple AND raspberry AND juice OR Cola");
        new RunStep4("\"apple juice\" OR applejuice");
    }

    private void handleOrQueryContext(Step4SearchDslParser.OrQueryContext orQueryContext) {
        System.out.println("Or query: ");
        List<Step4SearchDslParser.OrExprContext> termContexts = orQueryContext.orExpr();
        termContexts.forEach(orExprContext -> {
            if (null != orExprContext.andQuery()) {
                Step4SearchDslParser.AndQueryContext andQueryContext = orExprContext.andQuery();
                handleAndQuery(andQueryContext);
            } else {
                handleTermContext(orExprContext.term());
            }
        });
    }

    private void handleAndQuery(Step4SearchDslParser.AndQueryContext andQueryContext) {
        System.out.println("And Query: ");
        List<Step4SearchDslParser.TermContext> termContextList = andQueryContext.term();
        termContextList.forEach(this::handleTermContext);
    }

    private void handleTermContext(Step4SearchDslParser.TermContext termContext) {
        if (null != termContext.quotedTerm()) {
            handleQuotedTerm(termContext.quotedTerm());
        } else {
            handleWordTokens(termContext.WORD());
        }
    }

    private void handleQuotedTerm(Step4SearchDslParser.QuotedTermContext quotedTermContext) {
        System.out.print("QUOTED ");
        handleWordTokens(quotedTermContext.WORD());
    }

}

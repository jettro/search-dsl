package nl.gridshore.searchdsl;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;

import java.util.Scanner;

/**
 * In this step we extend the previous step with an option to use a quoted term. Therefore we
 * extend the Lexer with a QUOTE and the query parser now has two options, a term or a quoted term.
 */
public class RunStep2 extends AbstractRunStep {

    private RunStep2(String phrase) {
        printSource(phrase);

        Lexer lexer = new Step2LexerRules(CharStreams.fromString(phrase));
        CommonTokenStream commonTokenStream = new CommonTokenStream(lexer);

        Step2SearchDslParser parser = new Step2SearchDslParser(commonTokenStream);
        Step2SearchDslParser.QueryContext queryContext = parser.query();

        Step2SearchDslParser.TermContext termContext = queryContext.term();

        handleTermOrQuotedTerm(termContext);
    }

    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        System.out.println("Press enter to continue.....");

        new RunStep2("apple");
        new RunStep2("\"apple juice\"");

        s.nextLine();
    }

    private void handleQuotedTerm(Step2SearchDslParser.QuotedTermContext quotedTermContext) {
        System.out.print("QUOTED ");
        handleWordTokens(quotedTermContext.WORD());
    }

    private void handleTermOrQuotedTerm(Step2SearchDslParser.TermContext termContext) {
        if (null != termContext.quotedTerm()) {
            handleQuotedTerm(termContext.quotedTerm());
        } else {
            handleWordTokens(termContext.WORD());
        }
    }

}

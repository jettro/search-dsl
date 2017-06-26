package nl.gridshore.searchdsl;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.List;
import java.util.Scanner;

/**
 * <p>In step 1 we have just two lexer token types:</p>
 * <ul>
 * <li>WORD - contains only characters and numbers</li>
 * <li>WS - Contains whitespace characters and throws them away</li>
 * </ul>
 * Using the Lexer we generate tokens for each WORD and the Grammar is a query that accepts
 * one or more WORDs.
 */
public class RunStep1 extends AbstractRunStep {

    private RunStep1(String phrase) {
        printSource(phrase);

        Lexer lexer = new Step1LexerRules(CharStreams.fromString(phrase));
        CommonTokenStream commonTokenStream = new CommonTokenStream(lexer);

        Step1SearchDslParser parser = new Step1SearchDslParser(commonTokenStream);
        Step1SearchDslParser.QueryContext queryContext = parser.query();

        List<TerminalNode> words = queryContext.WORD();
        handleWordTokens(words);
    }

    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        System.out.println("Press enter to continue.....");

        new RunStep1("apple");
        new RunStep1("apple juice");
        new RunStep1("apple1 juice");

        s.nextLine();
    }
}

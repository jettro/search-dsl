package nl.gridshore.searchdsl;

import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.List;

/**
 * Utility methods for the other run classes
 */
public abstract class AbstractRunStep {
    void handleWordTokens(List<TerminalNode> words) {
        System.out.print("WORDS (" + words.size() + "): ");
        words.forEach(terminalNode -> System.out.print(terminalNode.getSymbol().getText() + ","));
        System.out.println();
    }

    void printSource(String source) {
        System.out.println("Source: " + source);
    }

}

package nl.gridshore.searchdsl;

import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Visitor creating a json string containing the complete query.
 */
public class JsonQueryVisitor extends SearchdslBaseVisitor<String> {
    @Override
    public String visitQuery(SearchdslParser.QueryContext ctx) {
        String query = visitChildren(ctx);

        // @formatter:off
        return
                "{" +
                    "\"query\":" + query +
                "}";
        // @formatter:on
    }

    @Override
    public String visitTerm(SearchdslParser.TermContext ctx) {
        if (ctx.quotedTerm() != null) {
            return visit(ctx.quotedTerm());
        }
        String wordsAsText = obtainWords(ctx.WORD());

        // @formatter:off
        return
                "{" +
                        "\"match\": {" +
                            "\"_all\":\"" + wordsAsText + "\"" +
                        "}" +
                "}";
        // @formatter:on
    }

    @Override
    public String visitQuotedTerm(SearchdslParser.QuotedTermContext ctx) {
        String termsAsText = obtainWords(ctx.WORD());

        // @formatter:off
        return
                "{" +
                        "\"match_phrase\": {" +
                            "\"_all\":\"" + termsAsText + "\"" +
                        "}" +
                "}";
        // @formatter:on
    }

    @Override
    public String visitAndQuery(SearchdslParser.AndQueryContext ctx) {
        List<String> mustQueries = ctx.term().stream().map(this::visit).collect(Collectors.toList());
        String query = String.join(",", mustQueries);

        // @formatter:off
        return
                "{" +
                        "\"bool\": {" +
                            "\"must\": [" +
                                query +
                            "]" +
                        "}" +
                "}";
        // @formatter:on
    }

    @Override
    public String visitOrQuery(SearchdslParser.OrQueryContext ctx) {
        List<String> shouldQueries = ctx.orExpr().stream().map(this::visit).collect(Collectors.toList());
        String query = String.join(",", shouldQueries);

        // @formatter:off
        return
                "{\"bool\": {" +
                        "\"should\": [" +
                            query +
                        "]" +
                "}}";
        // @formatter:on
    }

    private String obtainWords(List<TerminalNode> words) {
        if (words == null || words.isEmpty()) {
            return "";
        }
        List<String> foundWords = words.stream().map(TerminalNode::getText).collect(Collectors.toList());

        return String.join(" ", foundWords);
    }
}

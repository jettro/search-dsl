package nl.gridshore.searchdsl;

import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static nl.gridshore.searchdsl.RunStep5.BoolQueryType.MUST;
import static nl.gridshore.searchdsl.RunStep5.BoolQueryType.MUST_NOT;
import static nl.gridshore.searchdsl.RunStep5.BoolQueryType.SHOULD;

/**
 * Visitor creating a json string containing the complete query.
 */
public class JsonQueryVisitor extends SearchdslBaseVisitor<String> {

    @Override
    public String visitNotTerm(SearchdslParser.NotTermContext ctx) {
        String termQuery = visit(ctx.term());

        return boolQuery(MUST_NOT, Collections.singletonList(termQuery));
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
    public String visitSubQuery(SearchdslParser.SubQueryContext ctx) {
        List<String> items = new ArrayList<>();
        if (ctx.subQuery() != null) {
            ctx.subQuery().forEach(subQueryContext -> items.add(visit(subQueryContext)));
        }
        if (ctx.expr() != null) {
            ctx.expr().forEach(anyTermContext -> items.add(visit(anyTermContext)));
        }

        String query;
        if (!ctx.AND().isEmpty()) {
            if (ctx.NOT() != null) {
                query = boolQuery(MUST_NOT, items);
            } else {
                query = boolQuery(MUST, items);
            }
        } else if (!ctx.OR().isEmpty()) {
            query = boolQuery(SHOULD, items);
        } else {
            query = String.join(",", items);
        }

        if (ctx.NOT() != null && ctx.AND().isEmpty()) {
            query = boolQuery(MUST_NOT, Collections.singletonList(query));
        }

        return query;
    }

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

    private String obtainWords(List<TerminalNode> words) {
        if (words == null || words.isEmpty()) {
            return "";
        }
        List<String> foundWords = words.stream().map(TerminalNode::getText).collect(Collectors.toList());

        return String.join(" ", foundWords);
    }

    private String boolQuery(RunStep5.BoolQueryType type, List<String> queries) {
        String boolQueries = String.join(",", queries);

        return String.format(
                // @formatter:off
                    "{" +
                        "\"bool\": {" +
                            "\"%s\": [" +
                                "%s" +
                            "]" +
                        "}" +
                    "}"
                    // @formatter:on
                , type.type(), boolQueries);
    }

}

package nl.gridshore.searchdsl;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static nl.gridshore.searchdsl.RunStep5.BoolQueryType.MUST;
import static nl.gridshore.searchdsl.RunStep5.BoolQueryType.MUST_NOT;
import static nl.gridshore.searchdsl.RunStep5.BoolQueryType.SHOULD;

public class RunStep5 extends AbstractRunStep {

    private RunStep5(String phrase) {
        printSource(phrase);

        Lexer lexer = new Step5LexerRules(CharStreams.fromString(phrase));
        CommonTokenStream commonTokenStream = new CommonTokenStream(lexer);

        Step5SearchDslParser parser = new Step5SearchDslParser(commonTokenStream);
        Step5SearchDslParser.QueryContext queryContext = parser.query();

        Step5Visitor visitor = new Step5Visitor();
        String result = visitor.visit(queryContext);

        System.out.println(result);
    }

    public static void main(String[] args) {
        new RunStep5("apple");
        new RunStep5("NOT juice");
        new RunStep5("apple OR juice");
        new RunStep5("apple AND NOT juice");
        new RunStep5("apple raspberry OR juice");
        new RunStep5("apple AND raspberry AND juice OR Cola");
        new RunStep5("apple AND (raspberry OR juice) AND Cola");
        new RunStep5("(apple AND raspberry) OR (juice AND fruit)");
        new RunStep5("(apple AND (raspberry OR mango)) OR juice");
        new RunStep5("\"apple juice\" OR applejuice");
        new RunStep5("apple AND NOT (raspberry OR NOT mango) AND juice");
    }

    public enum BoolQueryType {
        MUST("must"), MUST_NOT("must_not"), SHOULD("should");

        private String type;

        BoolQueryType(String type) {
            this.type = type;
        }

        public String type() {
            return this.type;
        }
    }

    public class Step5Visitor extends Step5SearchDslBaseVisitor<String> {

        @Override
        public String visitNotTerm(Step5SearchDslParser.NotTermContext ctx) {
            String termQuery = visit(ctx.term());

            return boolQuery(MUST_NOT, Collections.singletonList(termQuery));
        }

        @Override
        public String visitTerm(Step5SearchDslParser.TermContext ctx) {
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
        public String visitQuotedTerm(Step5SearchDslParser.QuotedTermContext ctx) {
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
        public String visitSubQuery(Step5SearchDslParser.SubQueryContext ctx) {
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

        private String obtainWords(List<TerminalNode> words) {
            if (words == null || words.isEmpty()) {
                return "";
            }
            List<String> foundWords = words.stream().map(TerminalNode::getText).collect(Collectors.toList());

            return String.join(" ", foundWords);
        }

        private String boolQuery(BoolQueryType type, List<String> queries) {
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
}

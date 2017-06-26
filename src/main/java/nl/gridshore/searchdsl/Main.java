package nl.gridshore.searchdsl;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.IOException;

/**
 * Parse the query string
 *
 * search && find || succeed
 * query_1
 *   bool
 *     must
 *       search
 *       find
 * query_2
 *   bool
 *     should
 *       query_1
 *       succeed
 */
public class Main {
    public static void main(String[] args) throws IOException {
        CharStream charStream = CharStreams.fromString("\"multi search\" && find && doit OR succeed && nothing");
//        CharStream charStream = CharStreams.fromString("multi search && find && doit OR succeed && nothing");
//        CharStream charStream = CharStreams.fromString("search");
//        CharStream charStream = CharStreams.fromString("search && find AND me");
//        CharStream charStream = CharStreams.fromString("search || find OR me");

        SearchdslLexer lexer = new SearchdslLexer(charStream);

        CommonTokenStream commonTokenStream = new CommonTokenStream(lexer);

        SearchdslParser parser = new SearchdslParser(commonTokenStream);

        SearchdslParser.QueryContext tree = parser.query();

        System.out.println(tree.toStringTree());

        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(new QueryListener(),tree);
        System.out.println("Done!");

        // Visitor
//        System.out.println("*** Start Visiting");
//        QueryVisitor visitor = new QueryVisitor();
//        visitor.visit(tree);
//
//        System.out.println("Implement the query parser");
//        JsonQueryVisitor jsonQueryVisitor = new JsonQueryVisitor();
//        String query = jsonQueryVisitor.visit(tree);
//        System.out.println(query);

        System.out.println("Implementation with the jackson visitor");
        JacksonQueryVisitor jacksonQueryVisitor = new JacksonQueryVisitor();
        JsonNode queryNode = jacksonQueryVisitor.visit(tree);

        JsonFactory jsonFactory = new JsonFactory();
        JsonGenerator generator = jsonFactory.createGenerator(System.out);
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeTree(generator,queryNode);
    }

    public static class QueryVisitor extends SearchdslBaseVisitor<String> {
        @Override
        public String visitQuery(SearchdslParser.QueryContext ctx) {
            if (ctx.term() != null && !ctx.term().isEmpty()) {
                System.out.println("Create a normal query:" + ctx.term().getText());
            } else if (ctx.andQuery() != null && !ctx.andQuery().isEmpty()) {
                System.out.println("Found an AND Query");
                visitAndQuery(ctx.andQuery());
            } else if (ctx.orQuery() != null && !ctx.orQuery().isEmpty()) {
                System.out.println("Found an OR Query");
                visitOrQuery(ctx.orQuery());
            }

            return null;
        }

        @Override
        public String visitOrQuery(SearchdslParser.OrQueryContext ctx) {
            System.out.println("Visit OR query");
            if (!ctx.orExpr().isEmpty()) {
                ctx.orExpr().forEach(this::visitOrExpr);
            }
            return null;
        }

        @Override
        public String visitOrExpr(SearchdslParser.OrExprContext ctx) {
            System.out.println("Visit Or expression");
            if (ctx.andQuery() != null) {
                visitAndQuery(ctx.andQuery());
            } else if (ctx.term() != null) {
                visitTerm(ctx.term());
            }
            return null;
        }

        @Override
        public String visitAndQuery(SearchdslParser.AndQueryContext ctx) {
            System.out.println("Visit AND query");
            if (ctx.term() != null) {
                ctx.term().forEach(this::visitTerm);
            }
            return null;
        }

        @Override
        public String visitTerm(SearchdslParser.TermContext ctx) {
            System.out.println("Visit Term");
            return null;
        }
    }

    public static class QueryListener extends SearchdslBaseListener {
        @Override
        public void enterQuery(SearchdslParser.QueryContext ctx) {
            System.out.println("Start new Query");
        }

        @Override
        public void exitQuery(SearchdslParser.QueryContext ctx) {
            System.out.println("Finished Query");
        }

        @Override
        public void enterTerm(SearchdslParser.TermContext ctx) {
            System.out.println("Create new Term: " + ctx.getText());
        }

        @Override
        public void enterAndQuery(SearchdslParser.AndQueryContext ctx) {
            System.out.println("Create new AND Query");
        }

        @Override
        public void enterOrQuery(SearchdslParser.OrQueryContext ctx) {
            System.out.println("Create new OR Query");
        }

    }
}

package nl.gridshore.searchdsl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jettrocoenradie on 18/06/2017.
 */
public class JsonQueryVisitor extends SearchdslBaseVisitor<String> {
    @Override
    public String visitQuery(SearchdslParser.QueryContext ctx) {
        String query = "";
        if (ctx.term() != null && !ctx.term().isEmpty()) {
            query = visitTerm(ctx.term());
        } else if (ctx.andQuery() != null && !ctx.andQuery().isEmpty()) {
            query = visitAndQuery(ctx.andQuery());
        } else if (ctx.orQuery() != null && !ctx.orQuery().isEmpty()) {
            query = visitOrQuery(ctx.orQuery());
        }

        return "{\"query\":" + query + "}";
    }

    @Override
    public String visitTerm(SearchdslParser.TermContext ctx) {
        String termsAsText = "";
        if (ctx.WORD() != null && !ctx.WORD().isEmpty()) {
            List<String> terms = new ArrayList<>();
            ctx.WORD().forEach(searchTermContext -> {
                terms.add(searchTermContext.getText());
            });
            termsAsText = String.join(" ", terms);
        }
        return "{\"match\": {\"_all\":\"" + termsAsText + "\"}}";
    }

    @Override
    public String visitAndQuery(SearchdslParser.AndQueryContext ctx) {
        List<String> mustQueries = new ArrayList<>();

        if (ctx.term() != null) {
            ctx.term().forEach(termContext -> {
                mustQueries.add(visitTerm(termContext));
            });
        }

        String query = String.join(",", mustQueries);
        return
                "{\"bool\": {" +
                        "\"must\": [" +
                            query +
                        "]" +
                "}}";
    }

    @Override
    public String visitOrQuery(SearchdslParser.OrQueryContext ctx) {
        List<String> shouldQueries = new ArrayList<>();
        if (!ctx.orExpr().isEmpty()) {
            ctx.orExpr().forEach(orExprContext -> {
                shouldQueries.add(visitOrExpr(orExprContext));
            });
        }
        String query = String.join(",", shouldQueries);
        return
                "{\"bool\": {" +
                        "\"should\": [" +
                            query +
                        "]" +
                "}}";
    }

    @Override
    public String visitOrExpr(SearchdslParser.OrExprContext ctx) {
        if (ctx.andQuery() != null) {
            return visitAndQuery(ctx.andQuery());
        } else if (ctx.term() != null) {
            return visitTerm(ctx.term());
        }
        return "";
    }

}

package nl.gridshore.searchdsl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import javax.json.Json;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jettrocoenradie on 18/06/2017.
 */
public class JacksonQueryVisitor extends SearchdslBaseVisitor<JsonNode> {
    private JsonNodeFactory nodeFactory = new JsonNodeFactory(false);


    @Override
    public JsonNode visitQuery(SearchdslParser.QueryContext ctx) {
        JsonNode queryNode;
        if (ctx.term() != null && !ctx.term().isEmpty()) {
            queryNode = visitTerm(ctx.term());
        } else if (ctx.andQuery() != null && !ctx.andQuery().isEmpty()) {
            queryNode = visitAndQuery(ctx.andQuery());
        } else if (ctx.orQuery() != null && !ctx.orQuery().isEmpty()) {
            queryNode = visitOrQuery(ctx.orQuery());
        } else {
            throw new IllegalArgumentException("One of term, andQuery or orQuery expected");
        }
        ObjectNode query = nodeFactory.objectNode();
        query.set("query", queryNode);

        return query;
    }

    @Override
    public JsonNode visitTerm(SearchdslParser.TermContext ctx) {
        if (ctx.quotedTerm() != null && !ctx.quotedTerm().isEmpty()) {
            List<TerminalNode> words = ctx.quotedTerm().WORD();
            if (words != null && !words.isEmpty()) {
                ObjectNode allNode = nodeFactory.objectNode();
                allNode.set("_all", extractTextNodeFromWords(words));

                ObjectNode matchNode = nodeFactory.objectNode();
                matchNode.set("match_phrase", allNode);

                return matchNode;
            }
        } else {
            List<TerminalNode> words = ctx.WORD();
            if (words != null && !words.isEmpty()) {
                ObjectNode allNode = nodeFactory.objectNode();
                allNode.set("_all", extractTextNodeFromWords(words));

                ObjectNode matchNode = nodeFactory.objectNode();
                matchNode.set("match", allNode);

                return matchNode;
            }
        }
        throw new IllegalArgumentException("Term should have words or a quoted term");
    }

    private TextNode extractTextNodeFromWords(List<TerminalNode> words) {
        List<String> terms = new ArrayList<>();
        words.forEach(searchTermContext -> {
            terms.add(searchTermContext.getText());
        });
        return nodeFactory.textNode(String.join(" ", terms));
    }

    @Override
    public JsonNode visitAndQuery(SearchdslParser.AndQueryContext ctx) {
        ArrayNode mustNodes = nodeFactory.arrayNode();

        if (ctx.term() != null) {
            ctx.term().forEach(termContext -> {
                mustNodes.add(visitTerm(termContext));
            });
        }

        ObjectNode mustNode = nodeFactory.objectNode();
        mustNode.set("must", mustNodes);

        ObjectNode boolNode = nodeFactory.objectNode();
        boolNode.set("bool", mustNode);
        return boolNode;
    }

    @Override
    public JsonNode visitOrQuery(SearchdslParser.OrQueryContext ctx) {
        ArrayNode shouldNodes = nodeFactory.arrayNode();
        if (!ctx.orExpr().isEmpty()) {
            ctx.orExpr().forEach(orExprContext -> {
                shouldNodes.add(visitOrExpr(orExprContext));
            });
        }

        ObjectNode shouldNode = nodeFactory.objectNode();
        shouldNode.set("should", shouldNodes);

        ObjectNode boolNode = nodeFactory.objectNode();
        boolNode.set("bool", shouldNode);
        return boolNode;
    }

    @Override
    public JsonNode visitOrExpr(SearchdslParser.OrExprContext ctx) {
        if (ctx.andQuery() != null) {
            return visitAndQuery(ctx.andQuery());
        } else if (ctx.term() != null) {
            return visitTerm(ctx.term());
        } else {
            throw new IllegalArgumentException("AndQuery or Term query expected");
        }
    }

}

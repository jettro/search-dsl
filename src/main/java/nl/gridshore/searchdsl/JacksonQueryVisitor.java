package nl.gridshore.searchdsl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.TextNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Visitor that makes use of Jackson to create a JsonNode containing the complete query.
 */
public class JacksonQueryVisitor extends SearchdslBaseVisitor<JsonNode> {
    private JsonNodeFactory nodeFactory = new JsonNodeFactory(false);


    @Override
    public JsonNode visitQuery(SearchdslParser.QueryContext ctx) {
        JsonNode queryNode = visitChildren(ctx);

        return nodeFactory.objectNode().set("query", queryNode);
    }

    @Override
    public JsonNode visitTerm(SearchdslParser.TermContext ctx) {
        if (ctx.quotedTerm() != null) {
            return visit(ctx.quotedTerm());
        }

        JsonNode allNode = nodeFactory.objectNode().set("_all", extractTextNodeFromWords(ctx.WORD()));

        return nodeFactory.objectNode().set("match", allNode);
    }

    @Override
    public JsonNode visitQuotedTerm(SearchdslParser.QuotedTermContext ctx) {
        JsonNode allNode = nodeFactory.objectNode().set("_all", extractTextNodeFromWords(ctx.WORD()));

        return nodeFactory.objectNode().set("match_phrase", allNode);
    }

    @Override
    public JsonNode visitAndQuery(SearchdslParser.AndQueryContext ctx) {
        ArrayNode mustNodes = nodeFactory.arrayNode();
        ctx.term().forEach(termContext -> mustNodes.add(visitTerm(termContext)));

        JsonNode mustNode = nodeFactory.objectNode().set("must", mustNodes);
        return nodeFactory.objectNode().set("bool", mustNode);
    }

    @Override
    public JsonNode visitOrQuery(SearchdslParser.OrQueryContext ctx) {
        ArrayNode shouldNodes = nodeFactory.arrayNode();
        ctx.orExpr().forEach(orExprContext -> shouldNodes.add(visitOrExpr(orExprContext)));

        JsonNode shouldNode = nodeFactory.objectNode().set("should", shouldNodes);
        return nodeFactory.objectNode().set("bool", shouldNode);
    }

    private TextNode extractTextNodeFromWords(List<TerminalNode> words) {
        List<String> terms = new ArrayList<>();
        words.forEach(searchTermContext -> terms.add(searchTermContext.getText()));
        return nodeFactory.textNode(String.join(" ", terms));
    }
}

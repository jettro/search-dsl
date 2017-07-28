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
    public JsonNode visitNotTerm(SearchdslParser.NotTermContext ctx) {
        ArrayNode mustNotNodes = nodeFactory.arrayNode();
        mustNotNodes.add(visitTerm(ctx.term()));

        return createBoolQueryNode(mustNotNodes, BoolQueryType.MUST_NOT);
    }

    private JsonNode createBoolQueryNode(ArrayNode boolItems, BoolQueryType boolQueryType) {
        JsonNode mustNotNode = nodeFactory.objectNode().set(boolQueryType.type(), boolItems);

        return nodeFactory.objectNode().set("bool", mustNotNode);
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
    public JsonNode visitSubQuery(SearchdslParser.SubQueryContext ctx) {
        ArrayNode boolNodes = nodeFactory.arrayNode();

        if (ctx.subQuery() != null) {
            ctx.subQuery().forEach(subQueryContext -> boolNodes.add(visit(subQueryContext)));
        }
        if (ctx.expr() != null) {
            ctx.expr().forEach(exprContext -> boolNodes.add(visit(exprContext)));
        }

        JsonNode query;
        if (!ctx.AND().isEmpty()) {
            if (ctx.NOT() != null) {
                query = createBoolQueryNode(boolNodes, BoolQueryType.MUST_NOT);
            } else {
                query = createBoolQueryNode(boolNodes, BoolQueryType.MUST);
            }
        } else if (!ctx.OR().isEmpty()) {
            query = createBoolQueryNode(boolNodes, BoolQueryType.SHOULD);
        } else {
            query = boolNodes.get(0);
        }

        if (ctx.NOT() != null && ctx.AND().isEmpty()) {
            ArrayNode contained = nodeFactory.arrayNode();
            contained.add(query);
            query = createBoolQueryNode(contained, BoolQueryType.MUST_NOT);
        }

        return query;
    }

    @Override
    public JsonNode visitQuotedTerm(SearchdslParser.QuotedTermContext ctx) {
        JsonNode allNode = nodeFactory.objectNode().set("_all", extractTextNodeFromWords(ctx.WORD()));

        return nodeFactory.objectNode().set("match_phrase", allNode);
    }

    @Override
    public JsonNode visitQuery(SearchdslParser.QueryContext ctx) {
        JsonNode queryNode = visitChildren(ctx);

        return nodeFactory.objectNode().set("query", queryNode);
    }

    private TextNode extractTextNodeFromWords(List<TerminalNode> words) {
        List<String> terms = new ArrayList<>();
        words.forEach(searchTermContext -> terms.add(searchTermContext.getText()));
        return nodeFactory.textNode(String.join(" ", terms));
    }
}

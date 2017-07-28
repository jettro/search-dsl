package nl.gridshore.searchdsl;

import org.junit.Before;
import org.junit.Test;

import static nl.gridshore.searchdsl.RunVisitor.createTreeFromString;

public class JsonQueryVisitorTest extends QueryVisitorParent {


    private JsonQueryVisitor jsonQueryVisitor;

    @Before
    public void setUp() throws Exception {
        jsonQueryVisitor = new JsonQueryVisitor();
    }

    @Test
    public void testBasicQuery() {
        basicQuery();
    }

    @Test
    public void testNotQuery() {
        notQuery();
    }

    @Test
    public void testAndQuery() {
        andQuery();
    }

    @Test
    public void testOrQuery() {
        orQuery();
    }

    @Test
    public void testAndNotQuery() {
        andNotQuery();
    }

    @Test
    public void testAndOrPhraseQuery() {
        andOrPhraseQuery();
    }

    @Test
    public void testMultipleOperatorsAndNested() {
        multipleOperatorsAndNested();
    }

    @Test
    public void testAndNotNested() {
        andNotNested();
    }

    public String createQueryString(String query) {
        SearchdslParser.QueryContext queryContext = createTreeFromString(query);
        return jsonQueryVisitor.visit(queryContext);
    }
}
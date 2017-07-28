package nl.gridshore.searchdsl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import static nl.gridshore.searchdsl.RunVisitor.createTreeFromString;
import static org.junit.Assert.fail;

public class JacksonQueryVisitorTest extends QueryVisitorParent {

    private JacksonQueryVisitor jacksonQueryVisitor;

    @Before
    public void setUp() throws Exception {
        jacksonQueryVisitor = new JacksonQueryVisitor();
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
        JsonNode jsonNode = jacksonQueryVisitor.visit(queryContext);

        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writer().withDefaultPrettyPrinter().writeValueAsString(jsonNode);
        } catch (JsonProcessingException e) {
            fail(e.getMessage());
            return null;
        }

    }
}
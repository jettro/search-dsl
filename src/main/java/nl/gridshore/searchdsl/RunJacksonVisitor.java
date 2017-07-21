package nl.gridshore.searchdsl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.logging.Logger;

public class RunJacksonVisitor extends RunVisitor {
    private static final Logger LOGGER = Logger.getLogger(RunJacksonVisitor.class.getName());

    public static void main(String[] args) throws IOException {
        String searchString = "\"multi search\" && find && doit OR succeed && nothing";
        SearchdslParser.QueryContext tree = createTreeFromString(searchString);

        LOGGER.info("** Call the jackson Query Visitor");
        JacksonQueryVisitor jacksonQueryVisitor = new JacksonQueryVisitor();
        JsonNode query = jacksonQueryVisitor.visit(tree);

        ObjectMapper mapper = new ObjectMapper();
        LOGGER.info(mapper.writer().withDefaultPrettyPrinter().writeValueAsString(query));

    }
}

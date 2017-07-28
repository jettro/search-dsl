package nl.gridshore.searchdsl;

import java.util.logging.Logger;

/**
 * Execute the parsing a string using the JsonVisitor
 */
public class RunJsonVisitor extends RunVisitor {
    private static final Logger LOGGER = Logger.getLogger(RunJacksonVisitor.class.getName());

    public static void main(String[] args) {
//        String searchString = "\"multi search\" && find && doit OR succeed && nothing";
        String searchString = "(apple AND (raspberry OR mango)) OR juice";
        SearchdslParser.QueryContext tree = createTreeFromString(searchString);

        LOGGER.info("** Call the json Query Visitor");

        JsonQueryVisitor jsonQueryVisitor = new JsonQueryVisitor();
        String query = jsonQueryVisitor.visit(tree);

        LOGGER.info(query);

    }
}

package nl.gridshore.searchdsl;

public class RunJsonVisitor extends RunVisitor {
    public static void main(String[] args) {
        String searchString = "\"multi search\" && find && doit OR succeed && nothing";
        SearchdslParser.QueryContext tree = createTreeFromString(searchString);

        System.out.println("** Call the json Query Visitor");
        JsonQueryVisitor jsonQueryVisitor = new JsonQueryVisitor();
        String query = jsonQueryVisitor.visit(tree);
        System.out.println(query);

    }
}

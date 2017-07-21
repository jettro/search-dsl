package nl.gridshore.searchdsl;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

public class RunVisitor {
    static SearchdslParser.QueryContext createTreeFromString(String searchString) {
        CharStream charStream = CharStreams.fromString(searchString);
        SearchdslLexer lexer = new SearchdslLexer(charStream);
        CommonTokenStream commonTokenStream = new CommonTokenStream(lexer);
        SearchdslParser parser = new SearchdslParser(commonTokenStream);

        return parser.query();
    }

}

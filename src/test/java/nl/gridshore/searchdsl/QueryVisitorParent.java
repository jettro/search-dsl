package nl.gridshore.searchdsl;

import org.hamcrest.Matchers;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

public abstract class QueryVisitorParent {
    protected abstract String createQueryString(String queryString);

    void basicQuery() {
        String query = createQueryString("basic");

        assertThat(query, isJson());
        assertThat(query, hasJsonPath("$.query"));
        assertThat(query, hasJsonPath("$.query.match._all", Matchers.equalTo("basic")));
    }

    void notQuery() {
        String query = createQueryString("NOT juice");

        assertThat(query, isJson());
        assertThat(query, hasJsonPath("$.query"));
        assertThat(query, hasJsonPath("$.query.bool.must_not", hasSize(1)));
        assertThat(query, hasJsonPath("$.query.bool.must_not.[0].match._all", Matchers.equalTo("juice")));
    }

    void andQuery() {
        String query = createQueryString("basic AND advanced");

        assertThat(query, isJson());
        assertThat(query, hasJsonPath("$.query"));
        assertThat(query, hasJsonPath("$.query.bool.must", hasSize(2)));
        assertThat(query, hasJsonPath("$.query.bool.must.[0].match._all", Matchers.equalTo("basic")));
        assertThat(query, hasJsonPath("$.query.bool.must.[1].match._all", Matchers.equalTo("advanced")));
    }

    void orQuery() {
        String query = createQueryString("basic OR advanced");

        assertThat(query, isJson());
        assertThat(query, hasJsonPath("$.query"));
        assertThat(query, hasJsonPath("$.query.bool.should", hasSize(2)));
        assertThat(query, hasJsonPath("$.query.bool.should.[0].match._all", Matchers.equalTo("basic")));
        assertThat(query, hasJsonPath("$.query.bool.should.[1].match._all", Matchers.equalTo("advanced")));
    }

    void andNotQuery() {
        String query = createQueryString("basic AND NOT advanced");

        assertThat(query, isJson());
        assertThat(query, hasJsonPath("$.query"));
        assertThat(query, hasJsonPath("$.query.bool.must", hasSize(2)));
        assertThat(query, hasJsonPath("$.query.bool.must.[0].match._all", Matchers.equalTo("basic")));
        assertThat(query, hasJsonPath("$.query.bool.must.[1].bool.must_not", hasSize(1)));
        assertThat(query, hasJsonPath("$.query.bool.must.[1].bool.must_not.[0].match._all", Matchers.equalTo("advanced")));
    }

    void multipleOperatorsAndNested() {
        String query = createQueryString("(apple AND raspberry) OR (juice AND fruit)");

        assertThat(query, isJson());
        assertThat(query, hasJsonPath("$.query"));
        assertThat(query, hasJsonPath("$.query.bool.should", hasSize(2)));
        assertThat(query, hasJsonPath("$.query.bool.should.[0].bool.must", hasSize(2)));
        assertThat(query, hasJsonPath("$.query.bool.should.[0].bool.must.[0].match._all", Matchers.equalTo("apple")));
        assertThat(query, hasJsonPath("$.query.bool.should.[0].bool.must.[1].match._all", Matchers.equalTo("raspberry")));
        assertThat(query, hasJsonPath("$.query.bool.should.[1].bool.must", hasSize(2)));
        assertThat(query, hasJsonPath("$.query.bool.should.[1].bool.must.[0].match._all", Matchers.equalTo("juice")));
        assertThat(query, hasJsonPath("$.query.bool.should.[1].bool.must.[1].match._all", Matchers.equalTo("fruit")));

    }

    void andOrPhraseQuery() {
        String query = createQueryString("\"basic search\" AND maybe OR advanced search");

        assertThat(query, isJson());
        assertThat(query, hasJsonPath("$.query"));
        assertThat(query, hasJsonPath("$.query.bool.should", hasSize(2)));
        assertThat(query, hasJsonPath("$.query.bool.should.[0].bool.must", hasSize(2)));
        assertThat(query, hasJsonPath("$.query.bool.should.[0].bool.must.[0].match_phrase._all", Matchers.equalTo("basic search")));
        assertThat(query, hasJsonPath("$.query.bool.should.[0].bool.must.[1].match._all", Matchers.equalTo("maybe")));
        assertThat(query, hasJsonPath("$.query.bool.should.[1].match._all", Matchers.equalTo("advanced search")));
    }

    void andNotNested() {
        String query = createQueryString("apple AND (NOT (raspberry OR NOT mango)) AND juice");

        assertThat(query, isJson());
        assertThat(query, hasJsonPath("$.query"));
        assertThat(query, hasJsonPath("$.query.bool.must", hasSize(2)));
        assertThat(query, hasJsonPath("$.query.bool.must.[0].bool.must", hasSize(2)));
        assertThat(query, hasJsonPath("$.query.bool.must.[0].bool.must.[0].bool.must_not", hasSize(1)));
        assertThat(query, hasJsonPath("$.query.bool.must.[0].bool.must.[0].bool.must_not.[0].bool.should", hasSize(2)));
        assertThat(query, hasJsonPath("$.query.bool.must.[0].bool.must.[0].bool.must_not.[0].bool.should.[0].match._all", Matchers.equalTo("raspberry")));
        assertThat(query, hasJsonPath("$.query.bool.must.[0].bool.must.[0].bool.must_not.[0].bool.should.[1].bool.must_not", hasSize(1)));
        assertThat(query, hasJsonPath("$.query.bool.must.[0].bool.must.[0].bool.must_not.[0].bool.should.[1].bool.must_not.[0].match._all", Matchers.equalTo("mango")));
        assertThat(query, hasJsonPath("$.query.bool.must.[0].bool.must.[1].match._all", Matchers.equalTo("juice")));
        assertThat(query, hasJsonPath("$.query.bool.must.[1].match._all", Matchers.equalTo("apple")));

    }
}

package nl.gridshore.searchdsl;

public enum BoolQueryType {
    MUST("must"), MUST_NOT("must_not"), SHOULD("should");

    private String type;

    BoolQueryType(String type) {
        this.type = type;
    }

    public String type() {
        return this.type;
    }

}

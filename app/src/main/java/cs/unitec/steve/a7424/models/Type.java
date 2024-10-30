package cs.unitec.steve.a7424.models;

public enum Type {
    ANY("Any", ""),
    MULTIPLE("Multiple Choice", "multiple"),
    BOOLEAN("True / False", "boolean");

    private final String text;
    private final String value;

    Type(String text, String value) {
        this.text = text;
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public String getValue() {
        return value;
    }
}

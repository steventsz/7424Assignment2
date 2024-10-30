package cs.unitec.steve.a7424.models;

public enum Difficulty {
    ANY("Any", ""),
    EASY("Easy", "easy"),
    MEDIUM("Medium", "medium"),
    HARD("Hard", "hard");

    private final String text;
    private final String value;

    Difficulty(String text, String value) {
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

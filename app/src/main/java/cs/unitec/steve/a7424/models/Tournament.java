package cs.unitec.steve.a7424.models;

import java.util.Date;
import java.util.List;

public class Tournament {
    private String id;
    private String name;
    private Category category;
    private Difficulty difficulty;
    private Type type;
    private Date startDate;
    private Date endDate;
    private int amount;
    private List<String> likes;
    private List<Question> questions;

    public Tournament() {
        this.id = "";
        this.name = "";
        this.category = Category.ANY;
        this.difficulty = Difficulty.ANY;
        this.type = Type.BOOLEAN;
        this.startDate = new Date();
        this.endDate = new Date();
        this.amount = 10;
        this.likes = List.of();
        this.questions = List.of();
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public List<String> getLikes() {
        return likes;
    }

    public void setLikes(List<String> likes) {
        this.likes = likes;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }
}

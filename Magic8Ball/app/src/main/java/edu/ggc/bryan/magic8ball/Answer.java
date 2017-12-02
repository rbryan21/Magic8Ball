package edu.ggc.bryan.magic8ball;

public class Answer {

    private String text;

    public Answer(String text) {
        this.text = text;
    }

    public String getAnswerText() {
        return this.text;
    }

    public void setAnswerText(String text) {
        this.text = text;
    }

}

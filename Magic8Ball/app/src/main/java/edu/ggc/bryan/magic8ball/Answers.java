package edu.ggc.bryan.magic8ball;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Robert on 12/1/17.
 */

public class Answers {

    private static Answer[] answers = {
            new Answer("My sources say no"),
            new Answer("My sources say yes"),
            new Answer("My sources say maybe"),
            new Answer("How about no"),
            new Answer("Definitely"),
            new Answer("Google it"),
            new Answer("Nope"),
            new Answer("Yep"),
            new Answer("Ask yourself"),
            new Answer("I'm honestly not sure"),
            new Answer("Idk"),
            new Answer("Sure, why not"),
            new Answer("I think so."),
            new Answer("I'm leaning toward no"),
            new Answer("I'm feeling a bit of a maybe"),
            new Answer("How about no, actually wait yes"),
            new Answer("The answer is yes"),
            new Answer("I cannot answer that question"),
            new Answer("Ya"),
            new Answer("Nah")
    };

    private static int minAnswerIndex = 0;
    private static int maxAnswerIndex = Answers.answers.length - 19;

    private static Answer currentAnswer;

    private static void setCurrentAnswer(Answer answer) {
        Answers.currentAnswer = answer;
    }

    public static Answer getCurrentAnswer() {
        return Answers.currentAnswer;
    }

    public static Answer getRandomAnswer() {
        Answer randomAnswer = Answers.answers[ThreadLocalRandom.current().nextInt(Answers.minAnswerIndex, Answers.maxAnswerIndex + 1)];
        Answers.setCurrentAnswer(randomAnswer);
        return Answers.currentAnswer;
    }

}

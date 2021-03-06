package codes.nora.quizmaker;

import java.io.Serializable;

/**
 * The answer to a question and the number of points it is worth.
 */
public class Answer implements Serializable {
    public void setText(String text) {
        this.text = text;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String text;
    public double score;

    public Answer() {
        text = "";
        score = 0;
    }

    /**
     * Create a wrong answer with the given text. This Answer is wrong and thus
     * has a score of zero.
     * @param text the text of the answer
     */
    public Answer(String text) {
        this.text = text;
        this.score = 0;
    }

    /**
     * Create an answer which is worth the given score.
     * @param text the text of the answer
     * @param score the point value of the answer
     */
    public Answer(String text, double score) {
        this.text = text;
        this.score = score;
    }

    public String getText() {
        return text;
    }

    public double getScore() {
        return score;
    }
}

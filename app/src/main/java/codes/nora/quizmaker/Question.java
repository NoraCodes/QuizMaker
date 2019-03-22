package codes.nora.quizmaker;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Represents an answerable question.
 */
public class Question implements Serializable {
    public String title = "";
    public String description = "";
    public ArrayList<Answer> answers = new ArrayList<Answer>();
    private double max_score = 0;
    private boolean is_free_response = false;

    /**
     * Create a new Question with the given title and description.
     * @param title the question's title
     * @param description the question
     */
    public Question(String title, String description) {
        this.title = title;
        this.description = description;
    }

    /**
     * Currently does nothing. Eventually will load questions, either from SQLite
     * or from XML.
     * @return nothing
     */
    public static ArrayList<Question> load() {
        return new ArrayList<>();
    }

    /**
     * Set this question as a free response question.
     */
    public void set_free_response() {
        this.is_free_response = true;
    }

    /**
     * Check whether this question is a free response question.
     * @return true if it is free response and false if not
     */
    public boolean is_free_response() {
        return this.is_free_response;
    }

    /**
     * Add the given answer to this list of answers, updating the maximum score
     * if necessary.
     * @param answer the answer to add
     */
    public void add_answer(Answer answer) {
        this.answers.add(answer);
        if (answer.score > this.max_score) {
            this.max_score = answer.score;
        }
    }

    /**
     * Check the given string as an answer, case-sensitive.
     * @param answer the string to check
     * @return an Answer representing the answer given and the score it recieved
     */
    public Answer check_answer(String answer) {
        for (Answer a: this.answers) {
            if (a.text.toLowerCase().equals(answer.toLowerCase())) { return a; }
        }
        return new Answer(answer);
    }

    /**
     * Get the best (highest scoring) answer. Returns the first if there is a tie.
     * @return the best answer or null if there are no answers
     */
    public Answer best_answer() {
        if (this.answers.isEmpty()) {
            return null;
        }
        Answer best = this.answers.get(0);

        for (Answer a: this.answers) {
            if (a.score > best.score) {
                best = a;
            }
        }

        return best;
    }
}

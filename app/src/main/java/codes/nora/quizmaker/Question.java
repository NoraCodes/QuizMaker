package codes.nora.quizmaker;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Represents an answerable question.
 */
public class Question implements Serializable {
    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public ArrayList<Answer> getAnswers() {
        return answers;
    }

    public double getMax_score() {
        return max_score;
    }

    public boolean isIs_free_response() {
        return is_free_response;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAnswers(ArrayList<Answer> answers) {
        this.answers = answers;
    }

    public void setMax_score(double max_score) {
        this.max_score = max_score;
    }

    public void setIs_free_response(boolean is_free_response) {
        this.is_free_response = is_free_response;
    }

    public String title = "";
    public String description = "";
    public ArrayList<Answer> answers = new ArrayList<Answer>();
    public double max_score = 0;
    public boolean is_free_response = false;

    public Question() {}

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
     * Create a new Question representing a true/false decision.
     * @param title the question's title
     * @param description the question
     * @param correct_answer the right answer to the question
     * @param score how much the right answer is worth
     */
    public Question(String title, String description, boolean correct_answer, double score) {
        this.title = title;
        this.description = description;
        if (correct_answer == true) {
            this.answers.add(new Answer("True", score));
            this.answers.add(new Answer("False", 0.0));
        } else {
            this.answers.add(new Answer("True", 0.0));
            this.answers.add(new Answer("False", score));
        }
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

    public void add_answer(String answer, double score) {
        this.add_answer(new Answer(answer, score));
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

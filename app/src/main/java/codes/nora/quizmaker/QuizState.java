package codes.nora.quizmaker;

import android.os.Bundle;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Represents the state of a quiz in progress.
 */
public class QuizState implements Serializable {
    private int current_question_number = 0;
    /**
     * The questions for the quiz being tracked.
     */
    public ArrayList<Question> questions;
    /**
     * The answers given on the current quiz. Nulls mean no answer has been
     * entered.
     */
    public Answer[] answers;

    /**
     * Create a new QuizState for a quiz with the given questions.
     * @param questions the questions in the quiz being taken
     */
    public QuizState(ArrayList<Question> questions) {
        this.questions = questions;
        this.answers = new Answer[questions.size()];
    }

    /**
     * Submit the given string as an answer to the current question and
     * auto-advances to the next question.
     * @param answer the string corresponding to the answer given
     * @return the canonical answer string and corresponding score
     * @throws ArrayIndexOutOfBoundsException when trying to submit an answer when off
     * the end of the answer array.
     */
    public Answer submit_answer(String answer) {
        // Preemptively check if we are off the end of the array
        if (questions == null || current_question_number >= questions.size()) {
            throw new ArrayIndexOutOfBoundsException();
        }

        Question current_question = questions.get(current_question_number);

        if (current_question == null) {
            throw new ArrayIndexOutOfBoundsException();
        } else {
            Answer a = current_question.check_answer(answer);
            answers[current_question_number] = a;
            current_question_number += 1;
            return a;
        }
    }

    /**
     * Return the current question, or null if there are no more questions to
     * be answered.
     */
    public Question current_question() {
        if (!is_at_end()) {
            return questions.get(current_question_number);
        } else {
            return null;
        }
    }

    /**
     * Check whether the quiz is over or not.
     * @return true if there are no more questions to be answered, false if there are
     */
    public boolean is_at_end() {
        return questions == null || current_question_number >= questions.size();
    }

    /**
     * Save this QuizState into a Bundle.
     * @param b the bundle to use
     */
    public void into_bundle(Bundle b) {
        b.putSerializable("quizState", this);
    }

    /**
     * Reconstruct a QuizState from a Bundle.
     * @param b the bundle to use
     * @return The reconstructed QuizState or null if no QuizState was saved
     * into the bundle
     */
    public static QuizState from_bundle(Bundle b) {
        return (QuizState) b.getSerializable("quizState");
    }
}

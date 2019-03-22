package codes.nora.quizmaker;

import android.content.Intent;
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
     * Go back one question.
     * @throws ArrayIndexOutOfBoundsException when trying to backtrack when off
     * the beginning of the answer array.
     */
    public void backtrack() {
        // Preemptively check if we are off the beginning of the array
        if (questions == null || current_question_number >= questions.size()) {
            throw new ArrayIndexOutOfBoundsException();
        }
        answers[current_question_number] = null;
        current_question_number -= 1;
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
     * Check whether the quiz is at the beginning or not.
     * @return true if there are no questions prior to this one
     */
    public boolean is_at_start() {
        return questions == null || current_question_number == 0;
    }

    /**
     * Save this QuizState into a Bundle.
     * @param b the Bundle to use
     * @param key the key to save into
     */
    public void into_bundle(Bundle b, String key) {
        b.putSerializable(key, this);
    }

    /**
     * Save this QuizState into an Intent.
     * @param i the Intent to use
     * @param key the key to save into
     */
    public void into_intent(Intent i, String key) {
        i.putExtra(key, this);
    }

    /**
     * Reconstruct a QuizState from a Bundle.
     * @param b the bundle to use
     * @param k the key to reconstitute from
     * @return The reconstructed QuizState or null if no QuizState was saved
     * into the bundle
     */
    public static QuizState from_bundle(Bundle b, String k) {
        return (QuizState) b.getSerializable(k);
    }

    /**
     * Reconstruct a QuizState from an Intent.
     * @param i the Intent to use
     * @param k the key to reconstitute from
     * @return The reconstructed QuizState or null if no QuizState was saved
     * into the Intent
     */
    public static QuizState from_intent(Intent i, String k) {
        return (QuizState) i.getSerializableExtra(k);
    }
}

package codes.nora.quizmaker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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

    public QuizState() {
        this.questions = new ArrayList<>();
        Question q = new Question("Question 1", "This sentence is false.", true, 0.5);
        q.add_answer("a", 0);
        q.add_answer("d", 0);
        q.add_answer("b", 0);

        questions.add(q);
    }

    /**
     * Create a new QuizState for a quiz with the given questions.
     * @param questions the questions in the quiz being taken
     */
    public QuizState(ArrayList<Question> questions) {
        this.questions = questions;
    }

    public void init_for_editing() {
        if (this.questions == null) {
            this.questions = new ArrayList<Question>();
        }

        if (this.questions.size() == 0) {
            this.questions.add(new Question("", ""));
        }

        this.current_question_number = 0;
    }

    public void init_for_taking() {
        this.answers = new Answer[this.questions.size()];
        this.current_question_number = 0;
    }

    /**
     * Compute the total possible score for this quiz.
     */
    public double max_score() {
        double score = 0;
        for (Question q: questions) {
            score += q.best_answer().score;
        }
        return score;
    }

    /**
     * Compute the current score, counting unanswered questions as zeros.
     */
    public double current_score() {
        double score = 0;
        for (Answer a: answers) {
            if (a != null) {
                score += a.score;
            }
        }
        return score;
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
        } else if (answer == null) {
            current_question_number += 1;
            return null;
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
        if (questions == null || current_question_number == 0) {
            throw new ArrayIndexOutOfBoundsException();
        }
        if (answers != null) {
            answers[current_question_number] = null;
        }
        current_question_number -= 1;
    }

    public void skip() {
        if (questions == null || current_question_number >= questions.size()) {
            throw new ArrayIndexOutOfBoundsException();
        }
        if (answers != null) {
            answers[current_question_number] = null;
        }
        current_question_number += 1;
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

    public static QuizState from_stream(FileInputStream f) {
        try {
            ObjectInputStream i = new ObjectInputStream(f);
            return (QuizState) i.readObject();
        } catch (Exception e) {
            Log.e("QuizState deser", e.getMessage());
            return null;
        }
    }

    public void to_stream(FileOutputStream f) throws IOException {
        ObjectOutputStream o = new ObjectOutputStream(f);
        o.writeObject(this);
    }

    public void swap_question(Question q) {
        questions.set(current_question_number, q);
    }
}

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

    public QuizState() {
        this.questions = new ArrayList<Question>();

        {
            Question q = new Question("Question 1", "An Android device created by Samsung may have a different UI than an Android device created by LG", true, 0.25);
            this.questions.add(q);
        }

        {
            Question q = new Question("Question 2", "The three approaches exist to creating mobile apps are native app, web app, and ____ app.");
            q.add_answer("hybrid", 0.5);
            q.set_free_response();
            this.questions.add(q);
        }

        {
            Question q = new Question("Question 3", "A developer is wise to create an app that runs only on the latest Android platform.", false, 0.25);
            this.questions.add(q);
        }

        {
            Question q = new Question("Question 4", "A/an _____ represents a single screen and handles interaction with the user.");
            q.add_answer("Activity", 0.3);
            q.set_free_response();
            this.questions.add(q);
        }

        {
            Question q = new Question("Question 5", "The _____ method is the first method called when the activity starts, loads the activity's XML layout and performs other initialization logic.");
            q.add_answer("onCreate", 0.35);
            q.add_answer("onCreate()", 0.35);
            q.set_free_response();
            this.questions.add(q);
        }

        {
            Question q = new Question("Question 6", "Color resources may only be defined in res/values/colors.xml.", false, 0.25);
            this.questions.add(q);
        }

        {
            Question q = new Question("Question 7", "The app's name can be modified by changing a string in strings.xml..", true, 0.25);
            this.questions.add(q);
        }

        {
            Question q = new Question("Question 8", "User input from the view goes directly to the _____.");
            q.add_answer("controller", 0.25);
            q.set_free_response();
            this.questions.add(q);
        }

        {
            Question q = new Question("Question 9", "The _____ is responsible for presenting information in the UI");
            q.add_answer("view", 0.25);
            q.set_free_response();
            this.questions.add(q);
        }

        {
            Question q = new Question("Question 10", "The _____ is a ViewGroup that arranges child Views in relation to each other.");
            q.add_answer("ConstraintLayout", 0.35);
            q.add_answer("RelativeLayout", 0.35);
            q.set_free_response();
            this.questions.add(q);
        }

        {
            Question q = new Question("Question 11", "Which layput arranges views in grid?");
            q.add_answer("GridLayout", 0.35);
            q.add_answer("TableLayout", 0.25);
            q.set_free_response();
            this.questions.add(q);
        }

        {
            Question q = new Question("Question 12", "A/an _____ is a collection of attributes like width, color, padding, font size, etc. that modify the visual appearance of a View.");
            q.add_answer("style", 0.35);
            q.set_free_response();
            this.questions.add(q);
        }

        {
            Question q = new Question("Question 13", "What method is NOT called if a stopped activity that was not destroyed is restarted?");
            q.add_answer("onCreate()", 0.5);
            q.add_answer("onResume()", 0.0);
            q.add_answer("onStart()", 0.0);
            this.questions.add(q);
        }

        {
            Question q = new Question("Question 14", "When AndroidManifest.xml locks an activity's orientation, the activity does not restart when the device orientation changes.", true, 0.25);
            this.questions.add(q);
        }

        {
            Question q = new Question("Question 15", "Which file is loaded when MainActivity is in portrait orientation? ");
            q.add_answer("res/layout/activity_main.xml", 0.25);
            q.add_answer("res/layout-land/ activity_main.xml", 0.0);
            this.questions.add(q);
        }

        {
            Question q = new Question("Question 16", "getIntent() returns the intent used to start the activity. ", true, 0.3);
            this.questions.add(q);
        }

        this.answers = new Answer[questions.size()];
    }

    /**
     * Create a new QuizState for a quiz with the given questions.
     * @param questions the questions in the quiz being taken
     */
    public QuizState(ArrayList<Question> questions) {
        this.questions = questions;
        this.answers = new Answer[questions.size()];
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

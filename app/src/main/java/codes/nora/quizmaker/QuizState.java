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
import java.util.List;

import com.google.firebase.database.DatabaseReference;

/**
 * Represents the state of a quiz in progress.
 */
public class QuizState implements Serializable {
    public int currentQuestionNumber = 0;
    /**
     * The questions for the quiz being tracked.
     */
    public ArrayList<Question> questions;
    /**
     * The answers given on the current quiz. Nulls mean no answer has been
     * entered.
     */
    public Answer[] answers;

    public String code;

    private boolean editingMode = false;

    public QuizState() {
        this.code = "";
        this.questions = new ArrayList<>();
    }

    public QuizState(String code) {
        this.code = code;
        this.answers = new Answer[1];
        this.questions = new ArrayList<>();
        Question q = new Question("Question 1", "This sentence is false.", true, 0.5);
        questions.add(q);
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public List<Answer> getAnswers() {
        ArrayList<Answer> a = new ArrayList<Answer>();
        if (!editingMode && answers != null) {
            for (Answer ans : answers) {
                a.add(ans);
            }
        }
        return a;
    }

    public int getCurrentQuestionNumber() {
        return currentQuestionNumber;
    }

    /**
     * Create a new QuizState for a quiz with the given questions.
     * @param questions the questions in the quiz being taken
     */
    public QuizState(ArrayList<Question> questions) {
        this.questions = questions;
    }

    public void initForEditing() {
        if (this.questions == null) {
            this.questions = new ArrayList<Question>();
        }

        if (this.questions.size() == 0) {
            this.questions.add(new Question("", ""));
        }

        this.currentQuestionNumber = 0;
        this.editingMode = true;
    }

    public void initForTaking() {
        this.answers = new Answer[this.questions.size()];
        this.currentQuestionNumber = 0;
        this.editingMode = false;
    }

    /**
     * Compute the total possible score for this quiz.
     */
    public double max_score() {
        double score = 0;
        if (!editingMode)
        for (Question q: questions) {
            Answer bestAnswer = q.best_answer();
            if (bestAnswer != null) {
                score += q.best_answer().score;
            }
        }
        return score;
    }

    /**
     * Compute the current score, counting unanswered questions as zeros.
     */
    public double current_score() {
        if (this.editingMode || this.answers == null) {
            return 0;
        }
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
        if (editingMode || this.answers == null) {
            return null;
        }
        // Preemptively check if we are off the end of the array
        if (questions == null || currentQuestionNumber >= questions.size()) {
            throw new ArrayIndexOutOfBoundsException();
        }

        Question current_question = questions.get(currentQuestionNumber);

        if (current_question == null) {
            throw new ArrayIndexOutOfBoundsException();
        } else if (answer == null) {
            currentQuestionNumber += 1;
            return null;
        } else {
            Answer a = current_question.check_answer(answer);
            answers[currentQuestionNumber] = a;
            currentQuestionNumber += 1;
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
        if (questions == null || currentQuestionNumber == 0) {
            throw new ArrayIndexOutOfBoundsException();
        }
        if (!this.editingMode && answers != null) {
            answers[currentQuestionNumber] = null;
        }
        currentQuestionNumber -= 1;
    }

    public void skip() {
        if (atEnd()) {
            throw new ArrayIndexOutOfBoundsException();
        }
        if (!this.editingMode && answers != null) {
            answers[currentQuestionNumber] = null;
        }
        currentQuestionNumber += 1;
    }

    /**
     * Return the current question, or null if there are no more questions to
     * be answered.
     */
    public Question current_question() {
        if (!atEnd()) {
            return questions.get(currentQuestionNumber);
        } else {
            return null;
        }
    }

    /**
     * Check whether the quiz is over or not.
     * @return true if there are no more questions to be answered, false if there are
     */
    public boolean atEnd() {
        return questions == null || currentQuestionNumber >= (questions.size() - 1);
    }

    /**
     * Check whether the quiz is at the beginning or not.
     * @return true if there are no questions prior to this one
     */
    public boolean atStart() {
        return questions == null || currentQuestionNumber == 0;
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
     * Reconstruct a QuizState from an Intent.
     * @param i the Intent to use
     * @param k the key to reconstitute from
     * @return The reconstructed QuizState or null if no QuizState was saved
     * into the Intent
     */
    public static QuizState from_intent(Intent i, String k) {
        return (QuizState) i.getSerializableExtra(k);
    }

    public void swap_question(Question q) {
        questions.set(currentQuestionNumber, q);
    }
}

package codes.nora.quizmaker;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Validation tests for the programmatic question creation API.
 */
public class QuestionUnitTests {
    @Test
    public void checking_answers() {
        Question q = new Question("Test Question", "This is a test.");
        q.add_answer(new Answer("Wrong answer one"));
        q.add_answer(new Answer("Right answer!", 1.0));
        q.add_answer(new Answer("Partial credit.", 0.5));

        assert(q.check_answer("Right answer!").score == 1.0);
        assert(q.check_answer("Wrong answer!").score == 0);
        assert(q.check_answer("Partial credit.").score == 0.5);
        assert(q.check_answer("Something else.").score == 0);
    }

    @Test
    public void sample_quiz() {
        // Make an arraylist to hold loaded questions.
        ArrayList<Question> questions = new ArrayList();

        // "Load" questions. Really, these should be loaded from XML (or eventually SQL)
        Question q1 = new Question("Test Question 1", "This is a test.");
        q1.add_answer(new Answer("Wrong answer one"));
        q1.add_answer(new Answer("Right answer!", 1.0));
        q1.add_answer(new Answer("Partial credit.", 0.5));
        questions.add(q1);

        Question q2 = new Question("Test Question 2", "This is a test.");
        q2.add_answer(new Answer("Wrong answer one"));
        q2.add_answer(new Answer("Right answer!", 1.0));
        q2.add_answer(new Answer("Partial credit.", 0.5));
        questions.add(q2);

        // Start a new quiz with the questions made above.
        QuizState s = new QuizState(questions);
        assert(!s.is_at_end());
        assert(s.answers[0] == null);
        assert(s.answers[1] == null);

        // Get a question right.
        Question tq1 = s.current_question();
        assert(tq1.equals(q1));
        Answer a1 = s.submit_answer("right answer!");
        assert(!s.is_at_end());
        assert(s.answers[0].text.equals("Right answer!"));
        assert(s.answers[0].score == 1.0);
        assert(a1.equals(s.answers[0]));

        // Get a question partly right.
        Question tq2 = s.current_question();
        assert(tq2.equals(q2));
        Answer a2 = s.submit_answer("Partial Credit.");
        assert(s.is_at_end());
        assert(a2.equals(s.answers[1]));
    }

    @Test
    public void free_response() {
        Question q = new Question("Test Question", "This is a test.");
        assert(!q.is_free_response());
        q.set_free_response();
        assert(q.is_free_response());
    }
}
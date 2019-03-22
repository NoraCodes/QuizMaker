package codes.nora.quizmaker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class StartingPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting_page);
        Button startbutton =(Button)findViewById(R.id.startBtn);

        ArrayList<Question> questions = new ArrayList<>();

        Question q1 = new Question("Test Question 1", "This is a test.");
        q1.add_answer(new Answer("Wrong answer one"));
        q1.add_answer(new Answer("Right answer!", 1.0));
        q1.add_answer(new Answer("Partial credit.", 0.5));
        questions.add(q1);

        Question q2 = new Question("Test Question 2", "This is a test.\nwrong, right, part.");
        q2.add_answer(new Answer("wrong"));
        q2.add_answer(new Answer("right", 1.0));
        q2.add_answer(new Answer("part", 0.5));
        questions.add(q2);

        Question q3 = new Question("Test Question 3", "This is a test.\nwrong, right, part.");
        q3.add_answer(new Answer("wrong"));
        q3.add_answer(new Answer("right", 1.0));
        q3.add_answer(new Answer("part", 0.5));
        questions.add(q3);

        QuizState state = new QuizState(questions);

        final Intent i = new Intent(this, QuestionActivity.class);
        state.into_intent(i, QuestionActivity.KEY_EXTRA);

        startbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.getContext().startActivity(i);
            }
        });

    }
}

package codes.nora.quizmaker;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

public class QuestionActivity extends AppCompatActivity {

    private Button btnSubmit;
    private TextView questionName;
    private TextView questionText;
    private TextView questionScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        this.btnSubmit = findViewById(R.id.submit_btn);
        this.questionName = findViewById(R.id.question_name);
        this.questionText = findViewById(R.id.question_info);
        this.questionScore = findViewById(R.id.score_txt);

        Question q1 = new Question("Test Question 1", "This is a test.");
        q1.add_answer(new Answer("Wrong answer one"));
        q1.add_answer(new Answer("Right answer!", 1.0));
        q1.add_answer(new Answer("Partial credit.", 0.5));
        populateViewFrom(q1);
    }

    private void populateViewFrom(Question q) {
        this.questionName.setText(q.title);
        this.questionText.setText(q.description);
        this.questionScore.setText(String.format(Locale.ENGLISH, "%s points max.", fmt(q.best_answer().score)));
    }

    private static String fmt(double d)
    {
        if(d == (long) d)
            return String.format(Locale.ENGLISH, "%d",(long)d);
        else
            return String.format(Locale.ENGLISH, "%s",d);
    }
}

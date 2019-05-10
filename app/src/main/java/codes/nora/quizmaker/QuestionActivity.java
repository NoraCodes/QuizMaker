package codes.nora.quizmaker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.Locale;

public class QuestionActivity extends AppCompatActivity {
    public static final String KEY_EXTRA = "codes.nora.quizmaker.QUESTION_DATA";

    private Button btnSubmit;
    private TextView questionName;
    private TextView questionText;
    private TextView questionScore;
    private EditText answerEditText;
    private RadioGroup answerRadioGroup;
    private QuizState s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        this.btnSubmit = findViewById(R.id.submit_btn);
        this.questionName = findViewById(R.id.question_name);
        this.questionText = findViewById(R.id.question_info);
        this.questionScore = findViewById(R.id.score_txt);
        this.answerEditText = findViewById(R.id.answerEditText);
        this.answerRadioGroup = findViewById(R.id.typeRadioGroup);

        s = QuizState.from_intent(getIntent(), KEY_EXTRA);
        final Question q = s.current_question();
        if (q == null) {
            Log.e("onCreate Question", String.format("Null current_question"));
            return;
        }
        populateViewFrom(q);
    }

    public void onSubmitPress(View v) {
        Question q = this.s.current_question();
        if (q.is_free_response()) {
            s.submit_answer(answerEditText.getText().toString());
        } else {
            if (answerRadioGroup.getCheckedRadioButtonId() != -1) {
                RadioButton r = findViewById(answerRadioGroup.getCheckedRadioButtonId());
                s.submit_answer(r.getText().toString());
            } else {
                s.submit_answer("");
            }
        }

        if (s.is_at_end()) {
            Intent i = new Intent(v.getContext(), EndPage.class);
            s.into_intent(i, EndPage.KEY_EXTRA);
            v.getContext().startActivity(i);
        } else {
            Intent i = new Intent(v.getContext(), QuestionActivity.class);
            s.into_intent(i, QuestionActivity.KEY_EXTRA);
            v.getContext().startActivity(i);
        }
    }

    @Override
    public void onBackPressed() {
        if (s.is_at_start()) {
            Intent i = new Intent(this, StartingPage.class);
            s.into_intent(i, StartingPage.KEY_EXTRA);
            startActivity(i);
        } else {
            s.backtrack();
            Intent i = new Intent(this, QuestionActivity.class);
            s.into_intent(i, QuestionActivity.KEY_EXTRA);
            startActivity(i);
        }
    }

    private void populateViewFrom(Question q) {
        this.questionName.setText(q.title);
        this.questionText.setText(q.description);
        this.questionScore.setText(String.format(Locale.ENGLISH, "%s points max.", fmt(q.best_answer().score)));

        if (q.is_free_response()) {
            answerEditText.setVisibility(View.VISIBLE);
            answerRadioGroup.setVisibility(View.GONE);
        } else {
            answerRadioGroup.setVisibility(View.VISIBLE);
            answerEditText.setVisibility(View.GONE);

            for (Answer a: q.answers) {
                RadioButton r = new RadioButton(this);
                r.setText(a.text);
                answerRadioGroup.addView(r);
            }
        }
    }

    private static String fmt(double d)
    {
        if(d == (long) d)
            return String.format(Locale.ENGLISH, "%d",(long)d);
        else
            return String.format(Locale.ENGLISH, "%s",d);
    }
}

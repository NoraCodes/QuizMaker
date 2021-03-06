package codes.nora.quizmaker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Locale;

public class EditQuestionActivity extends AppCompatActivity {
    public static final String KEY_EXTRA = "codes.nora.quizmaker.QUESTION_DATA";

    RadioGroup typeRadioGroup;
    LinearLayout answersView;
    Button prevButton;
    Button nextButton;
    Button addAnswerButton;
    Button clearBtn;
    EditText titleEditText;
    EditText questionEditText;
    QuizState s;
    ArrayList<EditText> answerEditTexts;
    ArrayList<EditText> scoreEditTexts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_question);


        typeRadioGroup = findViewById(R.id.typeRadioGroup);
        answersView = findViewById(R.id.answersViewLL);
        prevButton = findViewById(R.id.prevQuestionButton);
        nextButton = findViewById(R.id.nextQuestionButton);
        addAnswerButton = findViewById(R.id.addAnswerButton);
        clearBtn = findViewById(R.id.clear_Btn);
        titleEditText = findViewById(R.id.questionTitleEditText);
        questionEditText = findViewById(R.id.questionTextEditText);
        final View answerText = findViewById(R.id.answersView);
        answerEditTexts = new ArrayList<>();
        scoreEditTexts = new ArrayList<>();

        s = QuizState.from_intent(getIntent(), KEY_EXTRA);
        Question q = s.current_question();
        if (q == null) {
            Log.e("onCreate Question", "Null current_question");
            q = new Question("", "");
            s.questions.add(q);
        }
        populateViewFrom(q);
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QuizState new_state = new QuizState(s.code);
                new_state.initForEditing();

            }
        });
    }


    @Override
    public void onBackPressed() {
        saveQuestionState();
        if (s.atStart()) {
            if (writeQuizState()) {
                Intent i = new Intent(this, StartingPage.class);
                s.into_intent(i, StartingPage.KEY_EXTRA);
                startActivity(i);
            }
        } else {
            s.backtrack();
            Intent i = new Intent(this, EditQuestionActivity.class);
            s.into_intent(i, EditQuestionActivity.KEY_EXTRA);
            startActivity(i);
        }
    }

    public void onNextButton(View v) {
        try {
            s.skip();
        } catch (ArrayIndexOutOfBoundsException e) {
            s.questions.add(new Question("", ""));
            s.skip();
        }


        Intent i = new Intent(this, EditQuestionActivity.class);
        s.into_intent(i, EditQuestionActivity.KEY_EXTRA);
        startActivity(i);
    }

    public void onPrevPressed(View v) {
        onBackPressed();
    }

    private void populateViewFrom(Question q) {
        if (q.is_free_response()) {
            typeRadioGroup.check(R.id.shortAnswerRadioButton);
        } else {
            typeRadioGroup.check(R.id.multipleChoiceRadioButton);
        }

        titleEditText.setText(q.title);
        questionEditText.setText(q.description);
        for (Answer a: q.answers) {
            addAnswerToView(a.text, a.score);
        }

        if (s.atEnd()) {
            nextButton.setText("New Question");
        }
        if (s.atStart()) {
            prevButton.setText("Save & Exit");
        }
    }

    private Question questionFromUI() {
        Question q = new Question(titleEditText.getText().toString(), questionEditText.getText().toString());
        for (int i = 0; i < answerEditTexts.size(); i++) {
            String answer_text = answerEditTexts.get(i).getText().toString();
            double answer_value;
            try {
                answer_value = Double.parseDouble(scoreEditTexts.get(i).getText().toString());
            } catch (NumberFormatException e) {
                answer_value = 0;
            }
            if (!answer_text.equals("")) {
                q.add_answer(answer_text, answer_value);
            }
        }
        return q;
    }

    private void saveQuestionState() {
        Question q = questionFromUI();
        s.swap_question(q);
    }

    private boolean writeQuizState() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference rootReference = db.getReference();
        rootReference.child("quizzes").child(s.code).setValue(s);
        Toast.makeText(this, "Wrote quiz to Firebase.", Toast.LENGTH_SHORT).show();
        return true;
    }

    public void onAddAnswer(View v) {
        addAnswerToView("New Answer", 0);
    }

    private void addAnswerToView(String answer, double value) {
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        EditText text = new EditText(this);
        EditText score = new EditText(this);
        text.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_CLASS_TEXT);
        score.setInputType(InputType.TYPE_CLASS_NUMBER);

        text.setText(answer);
        score.setText(String.format(Locale.ENGLISH, "%.02f", value));

        ll.addView(text);
        ll.addView(score);
        answerEditTexts.add(text);
        scoreEditTexts.add(score);
        answersView.addView(ll);
    }
}

package codes.nora.quizmaker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class EditQuestionActivity extends AppCompatActivity {
    public static final String KEY_EXTRA = "codes.nora.quizmaker.QUESTION_DATA";
    public static final String FILENAME = StartingPage.FILENAME;

    RadioGroup typeRadioGroup;
    LinearLayout answersView;
    Button prevButton;
    Button nextButton;
    Button addAnswerButton;
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
        titleEditText = findViewById(R.id.questionTitleEditText);
        questionEditText = findViewById(R.id.questionTextEditText);
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
    }

    @Override
    public void onBackPressed() {
        saveQuestionState();
        if (s.is_at_start()) {
            if (writeQuizState()) {
                Intent i = new Intent(this, StartingPage.class);
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
        if (s.is_at_end()) {
            s.questions.add(new Question("", ""));
        }

        s.skip();
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

        if (s.is_at_end()) {
            nextButton.setText("New Question");
        }
        if (s.is_at_start()) {
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
// Attempt to load the QuizState from a file.
        File file = new File(this.getFilesDir(), "quizzes");
        if (!file.exists()) {
            file.mkdirs();
        }

        try {
            FileOutputStream f = openFileOutput(FILENAME, Context.MODE_PRIVATE);
            s.to_stream(f);
            return true;
        } catch (FileNotFoundException e) {
            Toast.makeText(this, "Unable to open quiz data file.", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "Unable to write to quiz data file.", Toast.LENGTH_SHORT).show();
        }
        return false;
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

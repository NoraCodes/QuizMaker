package codes.nora.quizmaker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class EditQuestionActivity extends AppCompatActivity {
    public static final String KEY_EXTRA = "codes.nora.quizmaker.QUESTION_DATA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_question);
    }
}

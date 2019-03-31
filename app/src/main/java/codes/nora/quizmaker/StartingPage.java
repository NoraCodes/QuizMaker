package codes.nora.quizmaker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class StartingPage extends AppCompatActivity {
    public static final String FILENAME = "saved-quiz";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting_page);
        Button startbutton = findViewById(R.id.startBtn);
        Button editbutton = findViewById(R.id.editBtn);
        Button clearbutton = findViewById(R.id.clearBtn);

        // Attempt to load the QuizState from a file.
        File file = new File(this.getFilesDir(), "quizzes");
        if (!file.exists()) {
            file.mkdirs();
        }

        QuizState temp_state;
        try {
            FileInputStream f = openFileInput(FILENAME);
            temp_state = QuizState.from_stream(f);
        } catch (FileNotFoundException e) {
            temp_state = new QuizState();
        }

        if (temp_state == null) {
            Toast.makeText(this, "Unable to understand the saved quiz data.", Toast.LENGTH_SHORT).show();
            temp_state = new QuizState();
        }

        final QuizState state = temp_state;
        final Intent start_quiz = new Intent(this, QuestionActivity.class);
        final Intent edit_quiz = new Intent(this, EditQuestionActivity.class);

        startbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (state.is_at_end()) {
                    Toast.makeText(v.getContext(), "There are no questions in the quiz.", Toast.LENGTH_SHORT).show();
                } else {
                    state.init_for_taking();
                    state.into_intent(start_quiz, QuestionActivity.KEY_EXTRA);
                    v.getContext().startActivity(start_quiz);
                }
            }
        });

        editbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                state.init_for_editing();

                state.into_intent(edit_quiz, EditQuestionActivity.KEY_EXTRA);
                v.getContext().startActivity(edit_quiz);
            }
        });

        clearbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QuizState new_state = new QuizState();
                new_state.init_for_editing();
                state.into_intent(edit_quiz, EditQuestionActivity.KEY_EXTRA);
                v.getContext().startActivity(edit_quiz);
            }
        });

    }
}

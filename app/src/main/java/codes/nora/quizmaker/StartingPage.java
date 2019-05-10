package codes.nora.quizmaker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class StartingPage extends AppCompatActivity {
    public static final String FILENAME = "saved-quiz";
    public static final String KEY_EXTRA = "codes.nora.quizmaker.QUESTION_DATA";
    TextView loadingText;
    Button startButton;
    Button editButton;
    EditText quizCodeEditText;
    QuizState state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting_page);
        loadingText = findViewById(R.id.loadingText);
        startButton = findViewById(R.id.startBtn);
        editButton = findViewById(R.id.editBtn);
        quizCodeEditText = findViewById(R.id.quizCode);

        state = QuizState.from_intent(getIntent(), KEY_EXTRA);
        if (state != null) {
            loadingText.setText(state.code);
            quizCodeEditText.setText(state.code);
            loadingText.setVisibility(View.VISIBLE);
            editButton.setVisibility(View.VISIBLE);
            startButton.setVisibility(View.VISIBLE);
            editButton.setEnabled(true);
            startButton.setEnabled(true);
        }
    }

    public void onClickStart(View v) {
        if (state.is_at_end()) {
            Toast.makeText(v.getContext(), "There are no questions in the quiz.", Toast.LENGTH_SHORT).show();
        } else {
            state.init_for_taking();
            final Intent start_quiz = new Intent(this, QuestionActivity.class);
            state.into_intent(start_quiz, QuestionActivity.KEY_EXTRA);
            v.getContext().startActivity(start_quiz);
        }
    }

    public void onClickEdit(View v) {
        state.init_for_editing();
        Intent edit_quiz = new Intent(this, EditQuestionActivity.class);
        state.into_intent(edit_quiz, EditQuestionActivity.KEY_EXTRA);
        v.getContext().startActivity(edit_quiz);
    }

    public void onClickLoad(View v) {
        // Create the query for the FirebaseDatabase, selecting just the list of codes
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference rootReference = db.getReference();

        // Twiddle UI bits for the user
        startButton.setVisibility(View.INVISIBLE);
        startButton.setEnabled(false);
        editButton.setVisibility(View.INVISIBLE);
        editButton.setEnabled(false);

        // Get the code we want to edit or create
        final String userSelectedCode = quizCodeEditText.getText().toString();
        if (userSelectedCode.isEmpty()) {
            Toast.makeText(v.getContext(), "No code provided.", Toast.LENGTH_SHORT).show();
            return;
        }
        loadingText.setText("Loading...");
        loadingText.setVisibility(View.VISIBLE);


        final Context ctx = this.getApplicationContext();
        rootReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot data) {
                // Twiddle UI bits for the user
                startButton.setVisibility(View.VISIBLE);
                startButton.setEnabled(true);
                editButton.setVisibility(View.VISIBLE);
                editButton.setEnabled(true);
                loadingText.setText(userSelectedCode);

                // decide how to get the data, or create it if needed
                if (data.child("codes").hasChild(userSelectedCode)) {
                    // we'll load the quiz
                    state = data.child("quizzes").child(userSelectedCode).getValue(QuizState.class);
                    Toast.makeText(ctx, "Loaded the quiz.", Toast.LENGTH_SHORT).show();
                } else {
                    // create a new quiz
                    data.child("codes").getRef().child(userSelectedCode).setValue("exists");
                    Toast.makeText(ctx, "Created the quiz.", Toast.LENGTH_SHORT).show();
                    state = new QuizState(userSelectedCode);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                loadingText.setVisibility(View.VISIBLE);
                loadingText.setText("FAILED");
                Toast.makeText(ctx, "Read cancelled.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}


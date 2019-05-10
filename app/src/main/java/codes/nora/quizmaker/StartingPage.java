package codes.nora.quizmaker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
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

public class StartingPage extends AppCompatActivity {
    public static final String FILENAME = "saved-quiz";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting_page);
        final TextView loadingText = findViewById(R.id.loadingText);
        final Button startbutton = findViewById(R.id.startBtn);
        final Button editbutton = findViewById(R.id.editBtn);

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference mrootreference = db.getReference();
        final DatabaseReference mquizreference = mrootreference.child("Quiz");

        final QuizState temp_state = new QuizState();
        final Context ctx = this.getApplicationContext();

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

        mquizreference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                startbutton.setVisibility(View.VISIBLE);
                startbutton.setEnabled(true);
                editbutton.setVisibility(View.VISIBLE);
                editbutton.setEnabled(true);
                loadingText.setText("");
                Toast.makeText(ctx, "Read from the Firebase DB.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ctx, "Read cancelled.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}


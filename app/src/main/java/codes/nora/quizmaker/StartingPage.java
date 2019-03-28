package codes.nora.quizmaker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class StartingPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting_page);
        Button startbutton = findViewById(R.id.startBtn);

        final QuizState state = new QuizState();

        final Intent i = new Intent(this, QuestionActivity.class);
        state.into_intent(i, QuestionActivity.KEY_EXTRA);

        startbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (state.is_at_end()) {
                    Toast.makeText(v.getContext(), "There are no questions in the quiz.", Toast.LENGTH_SHORT).show();
                } else {
                    v.getContext().startActivity(i);
                }
            }
        });

    }
}

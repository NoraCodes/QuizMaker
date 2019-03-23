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

        QuizState state = new QuizState();

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

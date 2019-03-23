package codes.nora.quizmaker;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

public class EndPage extends AppCompatActivity {
    public static final String KEY_EXTRA = "codes.nora.quizmaker.QUESTION_DATA";

    private TextView percentView;
    private TextView detailView;
    private Button retry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_page);
        percentView = findViewById(R.id.scorePercentView);
        detailView = findViewById(R.id.scoreDetailView);
        retry = findViewById(R.id.retryButton);
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), StartingPage.class);
                startActivity(i);
            }
        });

        QuizState s = QuizState.from_intent(getIntent(), KEY_EXTRA);
        double percent = (s.current_score() / s.max_score()) * 100;
        percentView.setText(String.format(Locale.ENGLISH, "%.2f%%", percent));
        detailView.setText(String.format(Locale.ENGLISH, "%s out of a maximum possible %s points", fmt(s.current_score()), fmt(s.max_score())));
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, StartingPage.class);
        startActivity(i);
    }

    private static String fmt(double d)
    {
        if(d == (long) d)
            return String.format(Locale.ENGLISH, "%d",(long)d);
        else
            return String.format(Locale.ENGLISH, "%s",d);
    }
}

package cs.unitec.steve.a7424;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.List;

import cs.unitec.steve.a7424.adapters.AnswerAdapter;
import cs.unitec.steve.a7424.adapters.NoScrollRecyclerView;
import cs.unitec.steve.a7424.models.Question;
import cs.unitec.steve.a7424.models.Tournament;

public class TournamentAnswerActivity extends AppCompatActivity {

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore fireStore = FirebaseFirestore.getInstance();

    private LinearProgressIndicator indicator;
    private Toolbar toolbar;
    private TextView tournamentNameTxt;
    private TextView tournamentScoreTxt;
    private Button btnNext;
    private Button btnPrevious;
    private Button btnShowScore;

    private NoScrollRecyclerView recyclerView;
    private List<Question> listData;
    private AnswerAdapter adapter;

    private int pageNumber = 0;
    private int score = 0;
    private HashMap<Integer, Boolean> scoreMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tournament_answer);

        String tournamentId = getIntent().getStringExtra("id");
        if (tournamentId == null) return;

        btnNext = findViewById(R.id.answer_next_btn);
        btnPrevious = findViewById(R.id.answer_previous_btn);
        btnShowScore = findViewById(R.id.answer_show_score_btn);
        indicator = findViewById(R.id.progress);
        toolbar = findViewById(R.id.toolbar);
        tournamentNameTxt = findViewById(R.id.tournament_name_txt);
        tournamentScoreTxt = findViewById(R.id.tournament_score_txt);
        recyclerView = findViewById(R.id.question_recycler);

        adapter = new AnswerAdapter(this, listData, (index, isCorrect) -> {
            if (isCorrect) {
                scoreMap.put(index, isCorrect);
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        toolbar.setNavigationOnClickListener(v -> finish());

        indicator.setIndeterminate(true);
        fireStore.collection("tournaments").document(tournamentId).get()
                .addOnSuccessListener(doc -> {
                    if (doc != null) {
                        indicator.setIndeterminate(false);
                        Tournament tournament = doc.toObject(Tournament.class);
                        if (tournament != null) {
                            adapter.update(tournament.getQuestions());
                            pageNumber = tournament.getAmount();
                            tournamentNameTxt.setText(tournament.getName());
                        }
                    }
                });

        btnNext.setOnClickListener(v -> {
            answerScroll(1);
            pageNumber -= 1;
            changePageButtonEnable();
        });

        btnPrevious.setVisibility(View.GONE);
        btnPrevious.setOnClickListener(v -> {
            answerScroll(-1);
            pageNumber += 1;
            changePageButtonEnable();
        });

        btnShowScore.setOnClickListener(v -> showScore());
    }

    private void changePageButtonEnable() {
        if (pageNumber == 1) {
            btnNext.setEnabled(false);
            btnShowScore.setEnabled(true);
        } else {
            btnNext.setEnabled(true);
        }
        if (pageNumber == 10) {
            btnPrevious.setEnabled(false);
        } else {
            btnPrevious.setEnabled(true);
        }
    }

    private void showScore() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Total Score")
                .setMessage("Your score is " + scoreMap.size())
                .setPositiveButton("Finish", (dialog, which) -> {
                    dialog.dismiss();
                    finish();
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void answerScroll(int i) {
        int recyclerViewHeight = recyclerView.getHeight();
        int scrollDistance = recyclerViewHeight * i;
        recyclerView.smoothScrollBy(0, scrollDistance);
    }
}

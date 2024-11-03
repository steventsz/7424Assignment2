package cs.unitec.steve.a7424;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;
import cs.unitec.steve.a7424.adapters.TournamentAdapter;
import cs.unitec.steve.a7424.models.Tournament;

public class PlayerHomeActivity extends AppCompatActivity {

    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseFirestore fireStore = FirebaseFirestore.getInstance();

    private LinearProgressIndicator indicator;
    private TextView txtUserEmail;
    private Button btnLogout;

    private RecyclerView recyclerView;
    private List<Tournament> listData;
    private TournamentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_home);

        indicator = findViewById(R.id.player_home_progress);
        txtUserEmail = findViewById(R.id.player_home_user_email_txt);
        btnLogout = findViewById(R.id.player_home_logout_btn);

        if (auth.getCurrentUser() != null) {
            txtUserEmail.setText(auth.getCurrentUser().getEmail());
        }

        btnLogout.setOnClickListener(v -> {
            auth.signOut();
            Intent intent = new Intent(PlayerHomeActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        recyclerView = findViewById(R.id.player_home_recycler);
        adapter = new TournamentAdapter(auth.getCurrentUser().getUid(), listData, false, new TournamentAdapter.OnAnswerListener() {
            @Override
            public void onAnswer(String id) {
                Intent intent = new Intent(PlayerHomeActivity.this, TournamentAnswerActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        }, null, null, new TournamentAdapter.OnLikeListener() {
            @Override
            public void onLike(String id, boolean isLiked) {
                toggleLike(id, isLiked);
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        refreshTournaments();
    }

    private void refreshTournaments() {
        indicator.setIndeterminate(true);
        fireStore.collection("tournaments").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Tournament> list = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Tournament tournament = doc.toObject(Tournament.class);
                        if (tournament != null) {
                            list.add(tournament);
                        }
                    }
                    adapter.update(list);
                    indicator.setIndeterminate(false);
                });
    }

    private void toggleLike(String id, boolean isLiked) {
        indicator.setIndeterminate(true);
        if (!isLiked) {
            fireStore.collection("tournaments")
                    .document(id)
                    .update("likes", FieldValue.arrayUnion(auth.getCurrentUser().getUid()))
                    .addOnSuccessListener(aVoid -> {
                        indicator.setIndeterminate(false);
                        refreshTournaments();
                    });
        } else {
            fireStore.collection("tournaments")
                    .document(id)
                    .update("likes", FieldValue.arrayRemove(auth.getCurrentUser().getUid()))
                    .addOnSuccessListener(aVoid -> {
                        indicator.setIndeterminate(false);
                        refreshTournaments();
                    });
        }
    }
}

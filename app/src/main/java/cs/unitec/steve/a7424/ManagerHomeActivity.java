package cs.unitec.steve.a7424;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import cs.unitec.steve.a7424.adapters.TournamentAdapter;
import cs.unitec.steve.a7424.models.Tournament;

public class ManagerHomeActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore fireStore;

    private LinearProgressIndicator indicator;
    private TextView txtUserEmail;
    private Button btnLogout;
    private Button btnAdd;

    private RecyclerView recyclerView;
    private List<Tournament> listData;
    private TournamentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_home);

        auth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();

        indicator = findViewById(R.id.manager_home_progress);
        txtUserEmail = findViewById(R.id.manager_home_user_email_txt);
        btnLogout = findViewById(R.id.manager_home_logout_btn);
        btnAdd = findViewById(R.id.manager_home_add_btn);

        if (auth.getCurrentUser() != null) {
            txtUserEmail.setText(auth.getCurrentUser().getEmail());
        }

        btnLogout.setOnClickListener(v -> logout());
        btnAdd.setOnClickListener(v -> gotoEditOrAdd());

        recyclerView = findViewById(R.id.manager_home_recycler);
        adapter = new TournamentAdapter(listData, true, null, new TournamentAdapter.OnEditListener() {
            @Override
            public void onEdit(String id) {
                // 管理者编辑比赛的逻辑
                Intent intent = new Intent(ManagerHomeActivity.this, TournamentFormActivity.class);
                intent.putExtra("isEdit", true);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        refreshTournaments();
    }

    @Override
    protected void onResume() {
        super.onResume();
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

    private void logout() {
        auth.signOut();
        Intent intent = new Intent(ManagerHomeActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void gotoEditOrAdd() {
        Intent intent = new Intent(ManagerHomeActivity.this, TournamentFormActivity.class);
        intent.putExtra("isEdit", false);
        startActivity(intent);
    }
}

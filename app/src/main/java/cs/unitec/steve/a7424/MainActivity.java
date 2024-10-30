package cs.unitec.steve.a7424;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private PlayerManage playerManage;
    private LinearProgressIndicator progressIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressIndicator = findViewById(R.id.main_progress);

        playerManage = new PlayerManage();
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            progressIndicator.setIndeterminate(true);
            playerManage.findPlayer(auth.getCurrentUser().getUid(), () -> {
                Intent intent = new Intent(MainActivity.this, PlayerHomeActivity.class);
                startActivity(intent);
                finish();
            }, () -> {
                Intent intent = new Intent(MainActivity.this, ManagerHomeActivity.class);
                startActivity(intent);
                finish();
            });
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
}

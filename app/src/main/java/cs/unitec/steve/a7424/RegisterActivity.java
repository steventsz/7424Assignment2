package cs.unitec.steve.a7424;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private PlayerManage playerManage;
    private LinearProgressIndicator progress;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private Button btnLogin;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();
        playerManage = new PlayerManage();

        progress = findViewById(R.id.register_progress);
        etEmail = findViewById(R.id.register_email_et);
        etPassword = findViewById(R.id.register_password_et);
        etConfirmPassword = findViewById(R.id.register_confirm_password_et);
        btnLogin = findViewById(R.id.register_login_btn);
        btnRegister = findViewById(R.id.register_register_btn);

        btnRegister.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Email and Password cannot be empty.", Toast.LENGTH_LONG).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Please check password", Toast.LENGTH_LONG).show();
                return;
            }

            progress.setIndeterminate(true);
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener(authResult -> {
                        String uid = authResult.getUser().getUid();
                        playerManage.savePlayer(uid, () -> {
                            progress.setIndeterminate(false);
                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        });
                    })
                    .addOnFailureListener(e -> {
                        progress.setIndeterminate(false);
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        });

        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}

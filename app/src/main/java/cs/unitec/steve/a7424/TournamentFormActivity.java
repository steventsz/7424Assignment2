package cs.unitec.steve.a7424;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

import cs.unitec.steve.a7424.models.Category;
import cs.unitec.steve.a7424.models.Difficulty;
import cs.unitec.steve.a7424.models.Question;
import cs.unitec.steve.a7424.models.QuestionResponse;
import cs.unitec.steve.a7424.models.Tournament;


public class TournamentFormActivity extends AppCompatActivity {

    private FirebaseFirestore fireStore;
    private final String collectionName = "tournaments";

    private Tournament currentData = new Tournament();

    private LinearProgressIndicator progressIndicator;
    private Toolbar toolbar;
    private TextInputEditText txtName;
    private AutoCompleteTextView selectorCategory;
    private AutoCompleteTextView selectorDifficulty;
    private TextInputEditText txtStartDate;
    private TextInputEditText txtEndDate;

    private Button btnSave;
    private Button btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tournament_form);

        boolean isEdit = getIntent().getBooleanExtra("isEdit", false);
        String tournamentId = getIntent().getStringExtra("id");

        fireStore = FirebaseFirestore.getInstance();

        progressIndicator = findViewById(R.id.tournament_form_progress);
        toolbar = findViewById(R.id.tournament_form_toolbar);
        txtName = findViewById(R.id.tournament_form_name);
        selectorCategory = findViewById(R.id.tournament_form_category);
        selectorDifficulty = findViewById(R.id.tournament_form_difficulty);
        txtStartDate = findViewById(R.id.tournament_form_start_date);
        txtEndDate = findViewById(R.id.tournament_form_end_date);
        btnSave = findViewById(R.id.tournament_form_save_btn);
        btnCancel = findViewById(R.id.tournament_form_cancel_btn);

        toolbar.setNavigationOnClickListener(v -> finish());
        toolbar.setTitle(isEdit ? "Edit Tournament" : "Add Tournament");

        AtomicReference<Category> currentCate = new AtomicReference<>(Category.ANY);
        List<String> categoryTexts = new ArrayList<>();
        for (Category category : Category.values()) {
            categoryTexts.add(category.getText());
        }
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, categoryTexts);
        selectorCategory.setAdapter(categoryAdapter);
        selectorCategory.setOnItemClickListener((parent, view, position, id) -> currentCate.set(Category.values()[position]));

        AtomicReference<Difficulty> currentDifficulty = new AtomicReference<>(Difficulty.ANY);
        List<String> difficultyTexts = new ArrayList<>();
        for (Difficulty difficulty : Difficulty.values()) {
            difficultyTexts.add(difficulty.getText());
        }
        ArrayAdapter<String> difficultyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, difficultyTexts);
        selectorDifficulty.setAdapter(difficultyAdapter);
        selectorDifficulty.setOnItemClickListener((parent, view, position, id) -> currentDifficulty.set(Difficulty.values()[position]));

        txtStartDate.setOnClickListener(v -> showDatePicker(txtStartDate, currentData.getStartDate()));
        txtEndDate.setOnClickListener(v -> showDatePicker(txtEndDate, currentData.getEndDate()));

        btnSave.setOnClickListener(v -> save(isEdit, currentCate.get(), currentDifficulty.get()));
        btnCancel.setOnClickListener(v -> finish());

        if (isEdit && tournamentId != null) {
            loadTournament(tournamentId, tournament -> {
                currentData = tournament;
                txtName.setText(tournament.getName());
                selectorCategory.setText(tournament.getCategory().getText(), false);
                selectorDifficulty.setText(tournament.getDifficulty().getText(), false);
                txtStartDate.setText(getDateString(currentData.getStartDate()));
                txtEndDate.setText(getDateString(currentData.getEndDate()));
            });
        }
    }

    private void showDatePicker(TextInputEditText view, Date d) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(d);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (picker, y, m, d1) -> {
                    String selectedDate = String.format("%04d-%02d-%02d", y, m + 1, d1);
                    view.setText(selectedDate);
                },
                year,
                month,
                day
        );
        datePickerDialog.show();
    }

    private void loadTournament(String id, TournamentCallback callback) {
        progressIndicator.setIndeterminate(true);
        fireStore.collection(collectionName).document(id).get()
                .addOnSuccessListener(doc -> {
                    if (doc != null) {
                        Tournament tournament = doc.toObject(Tournament.class);
                        if (tournament != null) {
                            callback.onTournamentLoaded(tournament);
                        }
                    }
                    progressIndicator.setIndeterminate(false);
                });
    }

    private String getDateString(Date d) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return formatter.format(d);
    }

    private void save(boolean isEdit, Category cate, Difficulty difficulty) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date startDate = new Date();
        Date endDate = new Date();
        try {
            startDate = dateFormat.parse(txtStartDate.getText().toString());
            endDate = dateFormat.parse(txtEndDate.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (isEdit) {
            saveEditTournament(cate, difficulty, startDate, endDate);
        } else {
            saveNewTournament(cate, difficulty, startDate, endDate);
        }
    }

    private void saveEditTournament(Category cate, Difficulty difficulty, Date startDate, Date endDate) {
        progressIndicator.setIndeterminate(true);
        String tournamentId = currentData.getId();
        if (tournamentId == null) {
            progressIndicator.setIndeterminate(false);
            Toast.makeText(this, "Invalid tournament ID", Toast.LENGTH_SHORT).show();
            return;
        }

        fireStore.collection(collectionName).document(tournamentId)
                .update(
                        "name", txtName.getText().toString().trim(),
                        "category", cate,
                        "difficulty", difficulty,
                        "startDate", startDate,
                        "endDate", endDate
                )
                .addOnSuccessListener(aVoid -> {
                    progressIndicator.setIndeterminate(false);
                    Toast.makeText(this, "Tournament updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressIndicator.setIndeterminate(false);
                    Toast.makeText(this, "Failed to update tournament", Toast.LENGTH_SHORT).show();
                });
    }

    private void saveNewTournament(Category cate, Difficulty difficulty, Date startDate, Date endDate) {
        Tournament data = new Tournament();
        data.setName(txtName.getText().toString().trim());
        data.setCategory(cate);
        data.setDifficulty(difficulty);
        data.setStartDate(startDate);
        data.setEndDate(endDate);

        progressIndicator.setIndeterminate(true);
        fireStore.collection(collectionName).add(data)
                .addOnSuccessListener(docRef -> {
                    docRef.update("id", docRef.getId())
                            .addOnSuccessListener(unused -> getQuestions(questionList -> {
                                docRef.update("questions", questionList)
                                        .addOnSuccessListener(aVoid -> {
                                            progressIndicator.setIndeterminate(false);
                                            finish();
                                        });
                            }));
                });
    }

    private void getQuestions(QuestionsCallback callback) {
        int amount = currentData.getAmount();
        int categoryId = currentData.getCategory().getId();
        String difficultyValue = currentData.getDifficulty().getValue();
        String type = currentData.getType().getValue();
        String url = "https://opentdb.com/api.php?amount=" + amount + "&category=" + categoryId + "&difficulty=" + difficultyValue + "&type=" + type;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    Gson gson = new Gson();
                    QuestionResponse questionResponse = gson.fromJson(response, new TypeToken<QuestionResponse>() {}.getType());
                    callback.onQuestionsReceived(questionResponse.getResults());
                },
                error -> {
                    // Error handling
                });

        Volley.newRequestQueue(this).add(stringRequest);
    }

    private interface TournamentCallback {
        void onTournamentLoaded(Tournament tournament);
    }

    private interface QuestionsCallback {
        void onQuestionsReceived(List<Question> questions);
    }
}

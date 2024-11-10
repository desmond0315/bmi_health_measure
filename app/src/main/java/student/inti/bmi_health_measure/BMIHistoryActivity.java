package student.inti.bmi_health_measure;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import android.content.Intent; // Add this if redirecting to LoginActivity
import android.widget.Toast; // Add this if using Toasts for feedback


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BMIHistoryActivity extends AppCompatActivity {
    private ListView historyListView;
    private TextView noDataText;
    private DatabaseReference databaseReference;
    private List<String> bmiHistoryList;
    private ArrayAdapter<String> adapter;
    private static final String TAG = "BMIHistoryActivity";
    private TextView welcomeText; // Add reference for welcome message


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi_history);

        // Initialize views
        historyListView = findViewById(R.id.historyListView);
        noDataText = findViewById(R.id.noDataText);
        welcomeText = findViewById(R.id.welcomeText); // Initialize welcomeText


        // Get current user email from SharedPreferences
        String currentUserEmail = getSharedPreferences("BMIApp", MODE_PRIVATE)
                .getString("current_user", "");

        Log.d("BMIHistoryActivity", "Retrieved email from SharedPreferences: " + currentUserEmail);


        if (currentUserEmail.isEmpty()) {
            // Redirect to Login if no session found
            Toast.makeText(this, "Session expired. Please log in again.", Toast.LENGTH_SHORT).show();
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
            return;
        }

        // Set welcome message
        welcomeText.setText(String.format("Welcome, %s!", currentUserEmail));

        Log.d(TAG, "Current user email: " + currentUserEmail);

        // Initialize Firebase
        databaseReference = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://bmi-health-measure-default-rtdb.firebaseio.com/")
                .child("bmidata");

        // Initialize list and adapter
        bmiHistoryList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, bmiHistoryList);
        historyListView.setAdapter(adapter);

        // Load BMI history
        loadBMIHistory(currentUserEmail);
        Button viewGraphButton = findViewById(R.id.viewGraphButton);
        viewGraphButton.setOnClickListener(v -> {
            Intent graphIntent = new Intent(BMIHistoryActivity.this, BMIGraphActivity.class);
            startActivity(graphIntent);
        });

    }

    private void loadBMIHistory(final String userEmail) {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bmiHistoryList.clear();

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String email = userSnapshot.child("email").getValue(String.class);

                    if (email != null && email.equals(userEmail)) {
                        Double bmi = userSnapshot.child("bmi").getValue(Double.class);

                        if (bmi != null) {
                            String category = getBMICategory(bmi);
                            String userId = userSnapshot.getKey();

                            // Get date from users node
                            DatabaseReference userRef = FirebaseDatabase.getInstance()
                                    .getReferenceFromUrl("https://bmi-health-measure-default-rtdb.firebaseio.com/")
                                    .child("users")
                                    .child(userId);

                            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot userDataSnapshot) {
                                    String dateCreated = userDataSnapshot.child("dateCreated").getValue(String.class);

                                    @SuppressLint("DefaultLocale") String historyEntry = String.format("Date: %s\nBMI: %.1f\nCategory: %s",
                                            dateCreated != null ? dateCreated : "N/A",
                                            bmi,
                                            category);

                                    bmiHistoryList.add(historyEntry);

                                    // Update UI
                                    if (bmiHistoryList.isEmpty()) {
                                        noDataText.setVisibility(TextView.VISIBLE);
                                        historyListView.setVisibility(ListView.GONE);
                                    } else {
                                        noDataText.setVisibility(TextView.GONE);
                                        historyListView.setVisibility(ListView.VISIBLE);
                                    }

                                    adapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Log.e(TAG, "Error fetching user data: " + error.getMessage());
                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Database error: " + databaseError.getMessage());
            }
        });
    }

    private String getBMICategory(double bmi) {
        if (bmi < 18.5) return "Underweight";
        else if (bmi < 24.9) return "Normal";
        else if (bmi < 29.9) return "Overweight";
        else return "Obese";
    }
}
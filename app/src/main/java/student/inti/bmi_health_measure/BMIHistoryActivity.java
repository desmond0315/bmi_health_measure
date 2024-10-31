package student.inti.bmi_health_measure;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BMIHistoryActivity extends AppCompatActivity {
    private RecyclerView bmiHistoryRecyclerView;
    private BMIHistoryAdapter adapter;
    private List<BMIRecord> bmiRecordList;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmihistory);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference("bmidata");

        // Setup RecyclerView
        bmiHistoryRecyclerView = findViewById(R.id.bmiHistoryRecyclerView);
        bmiHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        bmiRecordList = new ArrayList<>();
        adapter = new BMIHistoryAdapter(bmiRecordList);
        bmiHistoryRecyclerView.setAdapter(adapter);

        // Back button
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        // Fetch BMI history
        fetchBMIHistory();
    }

    private void fetchBMIHistory() {
        String userId = mAuth.getCurrentUser().getUid();

        databaseRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bmiRecordList.clear();

                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    Double bmiValue = childSnapshot.child("bmi").getValue(Double.class);
                    Long timestamp = childSnapshot.child("timestamp").getValue(Long.class);

                    if (bmiValue != null && timestamp != null) {
                        BMIRecord record = new BMIRecord(bmiValue, timestamp);
                        bmiRecordList.add(record);
                    }
                }

                adapter.notifyDataSetChanged();

                if (bmiRecordList.isEmpty()) {
                    Toast.makeText(BMIHistoryActivity.this, "No BMI records found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(BMIHistoryActivity.this, "Failed to load BMI history", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // BMI Record class to hold BMI data
    public static class BMIRecord {
        private double bmiValue;
        private long timestamp;

        public BMIRecord(double bmiValue, long timestamp) {
            this.bmiValue = bmiValue;
            this.timestamp = timestamp;
        }

        public double getBmiValue() {
            return bmiValue;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public String getBMICategory() {
            if (bmiValue < 18.5) return "Underweight";
            if (bmiValue < 25) return "Normal weight";
            if (bmiValue < 30) return "Overweight";
            return "Obese";
        }
    }

    // RecyclerView Adapter
    public static class BMIHistoryAdapter extends RecyclerView.Adapter<BMIHistoryAdapter.BMIHistoryViewHolder> {
        private List<BMIRecord> bmiRecordList;

        public BMIHistoryAdapter(List<BMIRecord> bmiRecordList) {
            this.bmiRecordList = bmiRecordList;
        }

        @NonNull
        @Override
        public BMIHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.activity_bmihistory_item, parent, false);
            return new BMIHistoryViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull BMIHistoryViewHolder holder, int position) {
            BMIRecord record = bmiRecordList.get(position);

            // Format BMI value
            holder.bmiValueTextView.setText(String.format("BMI: %.2f", record.getBmiValue()));

            // Format date
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            String formattedDate = sdf.format(new Date(record.getTimestamp()));
            holder.dateTextView.setText(formattedDate);

            // BMI Category
            holder.bmiCategoryTextView.setText("Category: " + record.getBMICategory());
        }

        @Override
        public int getItemCount() {
            return bmiRecordList.size();
        }

        public static class BMIHistoryViewHolder extends RecyclerView.ViewHolder {
            TextView bmiValueTextView;
            TextView dateTextView;
            TextView bmiCategoryTextView;

            public BMIHistoryViewHolder(@NonNull View itemView) {
                super(itemView);
                bmiValueTextView = itemView.findViewById(R.id.bmiValueTextView);
                dateTextView = itemView.findViewById(R.id.dateTextView);
                bmiCategoryTextView = itemView.findViewById(R.id.bmiCategoryTextView);
            }
        }
    }
}
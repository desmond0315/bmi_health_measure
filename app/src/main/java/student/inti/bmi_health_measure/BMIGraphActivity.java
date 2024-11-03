package student.inti.bmi_health_measure;

import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BMIGraphActivity extends AppCompatActivity {
    private LineChart bmiLineChart;
    private DatabaseReference databaseReference;
    private static final String TAG = "BMIGraphActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmigraph);

        // Initialize chart
        bmiLineChart = findViewById(R.id.bmiLineChart);
        setupChart();

        // Get current user email
        String currentUserEmail = getSharedPreferences("BMIApp", MODE_PRIVATE)
                .getString("current_user", "");

        if (!currentUserEmail.isEmpty()) {
            loadBMIDataForGraph(currentUserEmail);
        } else {
            Log.e(TAG, "User email not found. Unable to load BMI data.");
        }
    }

    private void setupChart() {
        bmiLineChart.getDescription().setEnabled(false);
        XAxis xAxis = bmiLineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        YAxis leftAxis = bmiLineChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);

        YAxis rightAxis = bmiLineChart.getAxisRight();
        rightAxis.setEnabled(false);
    }

    private void loadBMIDataForGraph(final String userEmail) {
        databaseReference = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://bmi-health-measure-default-rtdb.firebaseio.com/")
                .child("bmidata");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Entry> entries = new ArrayList<>();
                int index = 0;

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String email = userSnapshot.child("email").getValue(String.class);

                    if (email != null && email.equals(userEmail)) {
                        Double bmi = userSnapshot.child("bmi").getValue(Double.class);

                        if (bmi != null) {
                            entries.add(new Entry(index++, bmi.floatValue()));
                        }
                    }
                }

                // Populate the chart with BMI data entries
                if (!entries.isEmpty()) {
                    LineDataSet dataSet = new LineDataSet(entries, "BMI History");
                    dataSet.setColor(getResources().getColor(R.color.teal_200));
                    dataSet.setValueTextColor(getResources().getColor(R.color.black));

                    dataSet.setLineWidth(2f);

                    LineData lineData = new LineData(dataSet);
                    bmiLineChart.setData(lineData);
                    bmiLineChart.invalidate(); // Refresh chart
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Database error: " + error.getMessage());
            }
        });
    }
}

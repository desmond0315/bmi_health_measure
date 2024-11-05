package student.inti.bmi_health_measure;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class BodyFatCalculatorActivity extends AppCompatActivity {
    private TextInputEditText ageInput, weightInput, heightInput, neckInput, waistInput, hipInput;
    private TextInputLayout hipLayout;
    private RadioGroup genderGroup;
    private Button calculateButton;
    private MaterialCardView resultCard;
    private TextView bodyFatPercentage, bodyFatCategory;

    private DatabaseReference mDatabase;
    private SharedPreferences sharedPreferences;
    private String currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_body_fat_calculator);

        // Initialize Firebase
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize SharedPreferences to get current user
        sharedPreferences = getSharedPreferences("BMIApp", MODE_PRIVATE);
        currentUser = sharedPreferences.getString("current_user", "");

        // Initialize views
        initializeViews();
        setupListeners();
    }

    private void initializeViews() {
        ageInput = findViewById(R.id.ageInput);
        weightInput = findViewById(R.id.weightInput);
        heightInput = findViewById(R.id.heightInput);
        neckInput = findViewById(R.id.neckInput);
        waistInput = findViewById(R.id.waistInput);
        hipInput = findViewById(R.id.hipInput);
        hipLayout = findViewById(R.id.hipLayout);
        genderGroup = findViewById(R.id.genderGroup);
        calculateButton = findViewById(R.id.calculateButton);
        resultCard = findViewById(R.id.resultCard);
        bodyFatPercentage = findViewById(R.id.bodyFatPercentage);
        bodyFatCategory = findViewById(R.id.bodyFatCategory);
    }

    private void setupListeners() {
        genderGroup.setOnCheckedChangeListener((group, checkedId) -> {
            hipLayout.setVisibility(checkedId == R.id.femaleRadio ? View.VISIBLE : View.GONE);
        });

        calculateButton.setOnClickListener(v -> calculateBodyFat());
    }

    private void calculateBodyFat() {
        // Validate inputs
        if (!validateInputs()) {
            return;
        }

        // Get values
        double height = Double.parseDouble(heightInput.getText().toString());
        double neck = Double.parseDouble(neckInput.getText().toString());
        double waist = Double.parseDouble(waistInput.getText().toString());
        boolean isMale = genderGroup.getCheckedRadioButtonId() == R.id.maleRadio;
        double hip = isMale ? 0 : Double.parseDouble(hipInput.getText().toString());

        double bodyFatValue;
        if (isMale) {
            bodyFatValue = calculateMaleBodyFat(height, neck, waist);
        } else {
            bodyFatValue = calculateFemaleBodyFat(height, neck, waist, hip);
        }

        String category = getBodyFatCategory(bodyFatValue, isMale);

        // Save to Firebase
        saveFatMeasurement(bodyFatValue, category, neck, waist, hip, isMale ? "Male" : "Female");

        // Display results
        displayResults(bodyFatValue, category);
    }

    private void saveFatMeasurement(double bodyFatValue, String category,
                                    double neck, double waist, double hip, String gender) {
        if (currentUser.isEmpty()) {
            Toast.makeText(this, "Error: User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create new measurement object
        FatMeasurement measurement = new FatMeasurement(
                currentUser,
                bodyFatValue,
                category,
                neck,
                waist,
                hip,
                gender
        );

        // Generate a unique key for the measurement
        String measurementKey = mDatabase.child("fat_measurements").push().getKey();

        // Save the measurement
        mDatabase.child("fat_measurements")
                .child(measurementKey)
                .setValue(measurement)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(BodyFatCalculatorActivity.this,
                            "Measurement saved successfully",
                            Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(BodyFatCalculatorActivity.this,
                            "Failed to save measurement: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private boolean validateInputs() {
        if (genderGroup.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Please select gender", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (isEmpty(ageInput) || isEmpty(weightInput) || isEmpty(heightInput) ||
                isEmpty(neckInput) || isEmpty(waistInput)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (genderGroup.getCheckedRadioButtonId() == R.id.femaleRadio && isEmpty(hipInput)) {
            Toast.makeText(this, "Please enter hip measurement", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private boolean isEmpty(TextInputEditText input) {
        return input.getText().toString().trim().isEmpty();
    }

    private double calculateMaleBodyFat(double height, double neck, double waist) {
        // U.S. Navy Method for men
        return 495 / (1.0324 - 0.19077 * Math.log10(waist - neck) + 0.15456 * Math.log10(height)) - 450;
    }

    private double calculateFemaleBodyFat(double height, double neck, double waist, double hip) {
        // U.S. Navy Method for women
        return 495 / (1.29579 - 0.35004 * Math.log10(waist + hip - neck) + 0.22100 * Math.log10(height)) - 450;
    }

    private void displayResults(double bodyFat, String category) {
        bodyFatPercentage.setText(String.format("%.1f%%", bodyFat));
        bodyFatCategory.setText(category);
        resultCard.setVisibility(View.VISIBLE);
    }

    private String getBodyFatCategory(double bodyFat, boolean isMale) {
        if (isMale) {
            if (bodyFat < 6) return "Essential Fat";
            if (bodyFat < 14) return "Athletes";
            if (bodyFat < 18) return "Fitness";
            if (bodyFat < 25) return "Average";
            return "Obese";
        } else {
            if (bodyFat < 14) return "Essential Fat";
            if (bodyFat < 21) return "Athletes";
            if (bodyFat < 25) return "Fitness";
            if (bodyFat < 32) return "Average";
            return "Obese";
        }
    }
}
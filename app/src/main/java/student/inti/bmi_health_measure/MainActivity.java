package student.inti.bmi_health_measure;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;
// Add these new imports
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private EditText weightInput, heightInput;
    private Button calculateBMIButton, backButton;
    private TextView bmiResultText;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference("bmidata");

        weightInput = findViewById(R.id.weightInput);
        heightInput = findViewById(R.id.heightInput);
        calculateBMIButton = findViewById(R.id.calculateBMIButton);
        bmiResultText = findViewById(R.id.bmiResultText);
        backButton = findViewById(R.id.backButton);

        calculateBMIButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String weightStr = weightInput.getText().toString();
                String heightStr = heightInput.getText().toString();

                if (!weightStr.isEmpty() && !heightStr.isEmpty()) {
                    double weight = Double.parseDouble(weightStr);
                    double height = Double.parseDouble(heightStr);

                    double bmi = weight / (height * height);
                    bmiResultText.setText(String.format("Your BMI: %.2f", bmi));

                    // Call the new method instead of saveUserData
                    String userEmail = mAuth.getCurrentUser().getEmail();
                    saveBMIToFirebase(bmi, userEmail);
                } else {
                    Toast.makeText(MainActivity.this, "Please enter both weight and height", Toast.LENGTH_SHORT).show();
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void saveBMIToFirebase(double bmi, String userEmail) {
        DatabaseReference bmiDataRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://bmi-health-measure-default-rtdb.firebaseio.com/")
                .child("bmidata")
                .push(); // This creates a new unique key

        Map<String, Object> bmiData = new HashMap<>();
        bmiData.put("bmi", bmi);
        bmiData.put("email", userEmail);

        bmiDataRef.setValue(bmiData)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "BMI data saved successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Failed to save BMI data", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        // Get the generated key
        String userId = bmiDataRef.getKey();

        // Also save user data
        DatabaseReference userRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://bmi-health-measure-default-rtdb.firebaseio.com/")
                .child("users")
                .child(userId);

        Map<String, Object> userData = new HashMap<>();
        userData.put("email", userEmail);
        userData.put("userId", userId);
        userData.put("dateCreated", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .format(new Date()));

        userRef.setValue(userData)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Failed to save user data", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
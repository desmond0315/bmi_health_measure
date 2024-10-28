package student.inti.bmi_health_measure;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private EditText weightInput, heightInput;
    private Button calculateBMIButton, backButton;
    private TextView bmiResultText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        weightInput = findViewById(R.id.weightInput);
        heightInput = findViewById(R.id.heightInput);
        calculateBMIButton = findViewById(R.id.calculateBMIButton);
        bmiResultText = findViewById(R.id.bmiResultText);
        backButton = findViewById(R.id.backButton); // Make sure you have a button with id backButton in your XML layout

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
                } else {
                    Toast.makeText(MainActivity.this, "Please enter both weight and height", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Back Button Intent to HomePageActivity
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HomePageActivity.class);
                startActivity(intent);
            }
        });
    }
}

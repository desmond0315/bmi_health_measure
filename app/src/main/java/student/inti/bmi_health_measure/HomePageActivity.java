package student.inti.bmi_health_measure;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

public class HomePageActivity extends AppCompatActivity {
    private Button bmiCalcButton, bmiHistoryButton;
    private TextView welcomeText;
    private SharedPreferences sharedPreferences;
    private String currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        // Initialize SharedPreferences to get current user
        sharedPreferences = getSharedPreferences("BMIApp", MODE_PRIVATE);
        currentUser = sharedPreferences.getString("current_user", "");

        // Initialize views
        welcomeText = findViewById(R.id.welcomeText);
        bmiCalcButton = findViewById(R.id.bmiCalcButton);
        bmiHistoryButton = findViewById(R.id.bmiHistoryButton);

        // Set welcome message
        welcomeText.setText("Welcome Back " + currentUser + "!");

        // Set up button click listeners
        bmiCalcButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to navigate to MainActivity
                Intent intent = new Intent(HomePageActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        bmiHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Temporarily show a toast until BMIHistoryActivity is created
                Toast.makeText(HomePageActivity.this, "BMI History coming soon!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            // Clear shared preferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            // Return to login activity
            Intent intent = new Intent(HomePageActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
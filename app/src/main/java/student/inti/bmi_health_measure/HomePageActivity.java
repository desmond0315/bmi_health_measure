package student.inti.bmi_health_measure;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.DatePicker;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;

import java.util.Calendar;

public class HomePageActivity extends AppCompatActivity {
    private MaterialCardView bmiCalcButton, bmiHistoryButton, logoutButton, bodyFatButton, calendarButton;
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
        logoutButton = findViewById(R.id.logoutButton);
        bodyFatButton = findViewById(R.id.bodyFatButton);
        calendarButton = findViewById(R.id.calendarButton);

        // Set welcome message
        welcomeText.setText(!currentUser.isEmpty() ? "Welcome Back, " + currentUser + "!" : "Welcome Back!");

        // Set up button click listeners
        bmiCalcButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePageActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        bmiHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePageActivity.this, BMIHistoryActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        bodyFatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePageActivity.this, BodyFatCalculatorActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        // Set up logout button click listener
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear shared preferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();

                // Show logout message
                Toast.makeText(HomePageActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();

                // Return to login activity
                Intent intent = new Intent(HomePageActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        // Set up calendar button click listener
        calendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the current date
                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                // Create DatePickerDialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        HomePageActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                                // Show the selected date (you can use it as needed)
                                String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                                Toast.makeText(HomePageActivity.this, "Selected Date: " + selectedDate, Toast.LENGTH_LONG).show();
                            }
                        }, year, month, day);

                // Show the date picker dialog
                datePickerDialog.show();
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
        // Handle action bar item clicks here.
        return super.onOptionsItemSelected(item);
    }
}

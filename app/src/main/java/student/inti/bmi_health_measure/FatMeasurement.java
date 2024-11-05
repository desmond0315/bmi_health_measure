package student.inti.bmi_health_measure;

import com.google.firebase.database.IgnoreExtraProperties;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@IgnoreExtraProperties
public class FatMeasurement {
    private String userId;
    private double bodyFatPercentage;
    private String category;
    private String date;
    private double neck;
    private double waist;
    private double hip;
    private String gender;

    // Required empty constructor for Firebase
    public FatMeasurement() {
    }

    public FatMeasurement(String userId, double bodyFatPercentage, String category,
                          double neck, double waist, double hip, String gender) {
        this.userId = userId;
        this.bodyFatPercentage = bodyFatPercentage;
        this.category = category;
        this.neck = neck;
        this.waist = waist;
        this.hip = hip;
        this.gender = gender;

        // Set current date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        this.date = sdf.format(new Date());
    }

    // Getters and setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public double getBodyFatPercentage() {
        return bodyFatPercentage;
    }

    public void setBodyFatPercentage(double bodyFatPercentage) {
        this.bodyFatPercentage = bodyFatPercentage;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getNeck() {
        return neck;
    }

    public void setNeck(double neck) {
        this.neck = neck;
    }

    public double getWaist() {
        return waist;
    }

    public void setWaist(double waist) {
        this.waist = waist;
    }

    public double getHip() {
        return hip;
    }

    public void setHip(double hip) {
        this.hip = hip;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
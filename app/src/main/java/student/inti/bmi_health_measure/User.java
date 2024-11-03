package student.inti.bmi_health_measure.model;

public class User {
    private String userId;
    private String email;
    private String password;    // WARNING: Storing passwords is a security risk!
    private String dateCreated;

    // Default constructor required for Firebase
    public User() {
    }

    public User(String userId, String email, String password, String dateCreated) {
        this.userId = userId;
        this.email = email;
        this.password = password;  // WARNING: This is unsafe!
        this.dateCreated = dateCreated;
    }

    // Original getters and setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getDateCreated() { return dateCreated; }
    public void setDateCreated(String dateCreated) { this.dateCreated = dateCreated; }

    // Password getters and setters
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
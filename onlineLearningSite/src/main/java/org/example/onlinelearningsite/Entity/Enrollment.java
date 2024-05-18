package org.example.onlinelearningsite.Entity;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
public class Enrollment  {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EnrollmentID")
    private int enrollmentID;

    @Column(name = "CourseID")
    private int courseID;

    @Column(name = "StudentID")
    private int studentID;

    @Column(name = "Status")
    private String status;

    @Column(name = "CreatedAt")
    private Timestamp createdAt;
    private String courseName;

    // Constructors
    public Enrollment() {
    }

    public Enrollment(int courseID, int studentID) {
        this.courseID = courseID;
        this.studentID = studentID;
        this.status = "Pending"; // Default status
        this.createdAt = new Timestamp(System.currentTimeMillis()); // Default creation timestamp
    }

    // Getters and setters
    public int getEnrollmentID() {
        return enrollmentID;
    }

    public void setEnrollmentID(int enrollmentID) {
        this.enrollmentID = enrollmentID;
    }

    public int getCourseID() {
        return courseID;
    }

    public void setCourseID(int courseID) {
        this.courseID = courseID;
    }

    @Column(name = "StudentEmail") // Changed from studentID
    private String studentEmail;

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }
}


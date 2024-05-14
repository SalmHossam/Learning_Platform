package org.example.onlinelearningsite.Entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "Courses")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int courseID;

    @Column(name="Name")
    private String name;
    @Column(name="Duration")
    private int duration;
    @Column(name="Category")
    private String category;
    @Column(name="Rating")
    private double rating;
    @Column(name="Capacity")
    private int capacity;
    @Column(name="EnrolledStudents")
    private int enrolledStudents;
    @Column(name="Description")
    private String description;

    @Column(name = "InstructorEmail") // Changed from instructorID
    private String instructorEmail;

    private Date createdAt;
    private Date updatedAt;

    // New instructor fields
    @Transient
    private String instructorName;

    public Course() {}

    public Course(String token, String name, int duration, String category, double rating, int capacity, int enrolledStudents, String description, Date createdAt, Date updatedAt, String instructorEmail, String instructorName) {
        this.name = name;
        this.duration = duration;
        this.category = category;
        this.rating = rating;
        this.capacity = capacity;
        this.enrolledStudents = enrolledStudents;
        this.description = description;
        this.instructorEmail = instructorEmail;
        this.instructorName = instructorName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getter and setter methods...

    public int getCourseID() {
        return courseID;
    }

    public void setCourseID(int courseID) {
        this.courseID = courseID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getEnrolledStudents() {
        return enrolledStudents;
    }

    public void setEnrolledStudents(int enrolledStudents) {
        this.enrolledStudents = enrolledStudents;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInstructorEmail() {
        return instructorEmail;
    }

    public void setInstructorEmail(String instructorEmail) {
        this.instructorEmail = instructorEmail;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getInstructorName() {
        return instructorName;
    }

    public void setInstructorName(String instructorName) {
        this.instructorName = instructorName;
    }
}

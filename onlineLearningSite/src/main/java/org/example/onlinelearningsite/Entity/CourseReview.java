package org.example.onlinelearningsite.Entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "CourseReviews")
public class CourseReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ReviewID")
    private int reviewID;

    @ManyToOne
    @JoinColumn(name = "CourseID")
    private Course course;

    @Column(name = "UserEmail")
    private String userEmail;

    @Column(name = "Rating")
    private int rating;

    @Column(name = "Review")
    private String review;

    @Column(name = "CreatedAt")
    private Date createdAt;

    public CourseReview() {
    }

    public CourseReview(Course course, String userEmail, int rating, String review) {
        this.course = course;
        this.userEmail = userEmail;
        this.rating = rating;
        this.review = review;
        this.createdAt = new Date();
    }

    public int getReviewID() {
        return reviewID;
    }

    public void setReviewID(int reviewID) {
        this.reviewID = reviewID;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}

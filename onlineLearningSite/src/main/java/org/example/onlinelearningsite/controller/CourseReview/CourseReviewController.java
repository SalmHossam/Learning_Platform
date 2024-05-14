package org.example.onlinelearningsite.controller.CourseReview;

import org.example.onlinelearningsite.Entity.CourseReview;

import javax.ejb.Stateless;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Stateless
public class CourseReviewController {
    private static final String JDBC_URL = "jdbc:mysql://localhost/onlinelearning?user=root&password=Salma@2001&useUnicode=true&characterEncoding=UTF-8";

    public boolean createReview(CourseReview courseReview) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO CourseReviews (CourseID, UserEmail, Rating, Review) VALUES (?, ?, ?, ?)");
        ) {
            // Set course data in the prepared statement
            statement.setInt(1, courseReview.getCourse().getCourseID());
            statement.setInt(3,courseReview.getRating());
            statement.setString(2,courseReview.getUserEmail());
            statement.setString(4, courseReview.getReview());
            // Execute the INSERT statement
            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}

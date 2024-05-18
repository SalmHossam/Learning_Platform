package org.example.onlinelearningsite.controller.CourseReview;

import org.example.onlinelearningsite.Entity.CourseReview;

import javax.ejb.Stateless;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Stateless
public class CourseReviewController {
    private static final String JDBC_URL = "jdbc:mysql://localhost/onlinelearning?user=root&password=Salma@2001&useUnicode=true&characterEncoding=UTF-8";

    public boolean createReview(CourseReview courseReview) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL);
             PreparedStatement selectStatement = connection.prepareStatement("SELECT CourseID FROM Courses WHERE name = ?");
             PreparedStatement insertStatement = connection.prepareStatement("INSERT INTO CourseReviews (CourseID, UserEmail, Rating, Review) VALUES (?, ?, ?, ?)");
        ) {
            // Set CourseName parameter for the select statement
            selectStatement.setString(1, courseReview.getCourse().getName());

            // Execute the SELECT statement to retrieve the CourseID
            ResultSet resultSet = selectStatement.executeQuery();
            if (resultSet.next()) {
                int courseId = resultSet.getInt("CourseID");

                // Set review data and courseId in the prepared statement for review insertion
                insertStatement.setInt(1, courseId);
                insertStatement.setString(2, courseReview.getUserEmail());
                insertStatement.setInt(3, courseReview.getRating());
                insertStatement.setString(4, courseReview.getReview());

                // Execute the INSERT statement
                int rowsInserted = insertStatement.executeUpdate();
                return rowsInserted > 0;
            } else {
                // No course found with the specified name
                return false;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public boolean deleteCourseReview(int courseId) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL);
             PreparedStatement statement = connection.prepareStatement("DELETE FROM CourseReviews WHERE courseId = ?");
        ) {
            statement.setInt(1, courseId);
            int deletedCourse=statement.executeUpdate();
            return deletedCourse>0;

        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
}


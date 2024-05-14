package org.example.onlinelearningsite.controller.course;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.example.onlinelearningsite.Entity.Course;

import javax.ejb.Stateless;
import java.security.Key;
import java.sql.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;


@Stateless
public class CourseController {

    private static final String JDBC_URL ="jdbc:mysql://localhost/onlinelearning?user=root&password=Salma@2001&useUnicode=true&characterEncoding=UTF-8";
    // Generate a secure key for HS256 algorithm
    //private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final String SECRET_KEY = "userToken";

    private static String generateSecretKey() {
        Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }
    public static int extractUserIdFromToken(String token) {
        String secretKey = "userToken";
        try {
            if (token == null || token.isEmpty()) {
                System.err.println("Token is missing or empty");
                return -1;
            }

            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey.getBytes())
                    .parseClaimsJws(token)
                    .getBody();

            if (claims.containsKey("user_id")) {
                return (int) claims.get("user_id");
            } else {
                System.err.println("Token does not contain user_id claim");
                return -1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to extract user ID from token: " + e.getMessage());
            return -1;
        }
    }


    public boolean createCourse(Course course) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO Courses (name, duration, category, rating, capacity, enrolledStudents, description, InstructorEmail, createdAt, updatedAt) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        ) {
            // Set course data in the prepared statement
            statement.setString(1, course.getName());
            statement.setInt(2, course.getDuration());
            statement.setString(3, course.getCategory());
            statement.setDouble(4, course.getRating());
            statement.setInt(5, course.getCapacity());
            statement.setInt(6, course.getEnrolledStudents());
            statement.setString(7, course.getDescription());
            statement.setString(8, course.getInstructorEmail()); // Use instructor's email
            statement.setTimestamp(9, new Timestamp(System.currentTimeMillis()));
            statement.setTimestamp(10, new Timestamp(System.currentTimeMillis()));

            // Execute the INSERT statement
            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Failed to create course: " + e.getMessage());
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("An unexpected error occurred: " + e.getMessage());
            return false;
        }
    }


    public List<Course> getCourses(String searchTerm, String searchCriteria) {
        List<Course> courses = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(JDBC_URL)) {
            String query = "SELECT * FROM Courses WHERE ";

            switch (searchCriteria.toLowerCase()) {
                case "name":
                case "Name":
                    query += "name LIKE ?";
                    break;
                case "category":
                case "Category":
                    query += "category LIKE ?";
                    break;
                case "rating":
                case "Rating":
                    query += "rating >= ?";
                    break;
                default:
                    throw new IllegalArgumentException("Invalid search criteria");
            }


            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, "%" + searchTerm + "%");

                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        Course course = new Course();
                        course.setCourseID(resultSet.getInt("courseID"));
                        course.setName(resultSet.getString("name"));
                        course.setDuration(resultSet.getInt("duration"));
                        course.setCategory(resultSet.getString("category"));
                        course.setRating(resultSet.getDouble("rating"));
                        course.setCapacity(resultSet.getInt("capacity"));
                        course.setEnrolledStudents(resultSet.getInt("enrolledStudents"));
                        course.setDescription(resultSet.getString("description"));
                        course.setCreatedAt(resultSet.getTimestamp("createdAt"));
                        course.setUpdatedAt(resultSet.getTimestamp("updatedAt"));
                        courses.add(course);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }

    public Course getCourse(int courseId) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM Courses WHERE courseId = ?");
        ) {
            statement.setInt(1, courseId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Course course = new Course();
                    course.setCourseID(resultSet.getInt("courseID"));
                    course.setName(resultSet.getString("name"));
                    course.setDuration(resultSet.getInt("duration"));
                    course.setCategory(resultSet.getString("category"));
                    course.setRating(resultSet.getDouble("rating"));
                    course.setCapacity(resultSet.getInt("capacity"));
                    course.setEnrolledStudents(resultSet.getInt("enrolledStudents"));
                    course.setDescription(resultSet.getString("description"));
                    course.setCreatedAt(resultSet.getTimestamp("createdAt"));
                    course.setUpdatedAt(resultSet.getTimestamp("updatedAt"));
                    return course;
                } else {
                    return null; // Course not found
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean updateCourse(int courseId, String updatedName, String updatedCategory, double updatedRating, int updatedCapacity, String updatedEmail) {
        boolean updated = false;
        try (Connection connection = DriverManager.getConnection(JDBC_URL)) {
            String query = "UPDATE Courses SET name = ?, category = ?, rating = ?, capacity = ?, InstructorEmail = ? WHERE courseId = ?";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                // Set the updated values
                statement.setString(1, updatedName);
                statement.setString(2, updatedCategory);
                statement.setDouble(3, updatedRating);
                statement.setInt(4, updatedCapacity);
                statement.setString(8, updatedEmail); // Use instructor's email
                statement.setInt(6, courseId);

                int rowsUpdated = statement.executeUpdate();
                updated = rowsUpdated > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return updated;
    }


    public boolean deleteCourse(int courseId) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL);
             PreparedStatement statement = connection.prepareStatement("DELETE FROM Courses WHERE courseId = ?");
        ) {
            statement.setInt(1, courseId);
            int deletedCourse=statement.executeUpdate();
            return deletedCourse>0;

        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
}

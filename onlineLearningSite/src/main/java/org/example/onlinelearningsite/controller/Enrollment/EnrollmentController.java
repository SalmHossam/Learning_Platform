package org.example.onlinelearningsite.controller.Enrollment;

import org.example.onlinelearningsite.Entity.Enrollment;

import javax.ejb.Stateful;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Stateful
public class EnrollmentController {
    // JDBC connection URL
    private static final String JDBC_URL = "jdbc:mysql://localhost/onlinelearning?user=root&password=Salma@2001&useUnicode=true&characterEncoding=UTF-8";

    // Establishing connection method
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL);
    }

    public void enrollStudent(Enrollment enrollment) {
        try (Connection connection = getConnection();
             PreparedStatement pstmt = connection.prepareStatement(
                     "INSERT INTO studentenrollments (CourseID, StudentEmail, Status, CreatedAt) VALUES (?, ?, ?, ?)"
             )) {

            pstmt.setLong(1, enrollment.getCourseID());
            pstmt.setString(2, enrollment.getStudentEmail());
            pstmt.setString(3, "Pending");
            pstmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void cancelEnrollment(Long enrollmentId) {
        try (Connection connection = getConnection();
             PreparedStatement pstmt = connection.prepareStatement(
                     "DELETE FROM studentenrollments WHERE EnrollmentID = ?")) {

            pstmt.setLong(1, enrollmentId);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Enrollment> getEnrollmentsForStudent(String studentEmail) {
        List<Enrollment> enrollments = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement pstmt = connection.prepareStatement(
                     "SELECT * FROM studentenrollments WHERE StudentEmail = ?")) {

            pstmt.setString(1, studentEmail);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Enrollment enrollment = new Enrollment();
                enrollment.setEnrollmentID(rs.getInt("EnrollmentID"));
                enrollment.setCourseID(rs.getInt("CourseID"));
                enrollment.setStudentEmail(rs.getString("StudentEmail"));
                enrollment.setStatus(rs.getString("Status")); // Retrieve and set status from the result set
                enrollment.setCreatedAt(rs.getTimestamp("CreatedAt"));
                enrollments.add(enrollment);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return enrollments;
    }

    public void approveEnrollment(Long enrollmentId) {
        try (Connection connection = getConnection();
             PreparedStatement pstmt = connection.prepareStatement(
                     "UPDATE studentenrollments SET Status = 'Approved' WHERE EnrollmentID = ?")) {

            pstmt.setLong(1, enrollmentId);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void rejectEnrollment(Long enrollmentId) {
        try (Connection connection = getConnection();
             PreparedStatement pstmt = connection.prepareStatement(
                     "UPDATE studentenrollments SET Status = 'Rejected' WHERE EnrollmentID = ?")) {

            pstmt.setLong(1, enrollmentId);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

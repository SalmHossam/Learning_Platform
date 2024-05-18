package org.example.onlinelearningsite.controller.Enrollment;

import org.example.onlinelearningsite.Entity.Enrollment;

import javax.ejb.Stateless;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class EnrollmentController {
    // JDBC connection URL
    private static final String JDBC_URL = "jdbc:mysql://localhost/onlinelearning?user=root&password=Salma@2001&useUnicode=true&characterEncoding=UTF-8";

    // Establishing connection method
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL);
    }


    public void enrollStudent(Enrollment enrollment) {
        try (Connection connection = getConnection();
             PreparedStatement selectStmt = connection.prepareStatement(
                     "SELECT CourseID FROM Courses WHERE name = ?"
             );
             PreparedStatement insertStmt = connection.prepareStatement(
                     "INSERT INTO studentenrollments (CourseID, StudentEmail, Status, CreatedAt) VALUES (?, ?, ?, ?)"
             )) {

            // Set parameters for the SELECT statement
            selectStmt.setString(1, enrollment.getCourseName());

            // Execute the SELECT statement
            try (ResultSet resultSet = selectStmt.executeQuery()) {
                if (resultSet.next()) {
                    // CourseID found, proceed with enrollment
                    long courseID = resultSet.getLong("CourseID");

                    // Set parameters for the INSERT statement
                    insertStmt.setLong(1, courseID);
                    insertStmt.setString(2, enrollment.getStudentEmail());
                    insertStmt.setString(3, "Pending");
                    insertStmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));

                    // Execute the INSERT statement
                    insertStmt.executeUpdate();
                } else {
                    // Course not found, handle the error
                    System.out.println("Course not found.");
                    // You can throw an exception or handle it according to your application's logic
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void cancelEnrollment(String studentEmail) {
        try (Connection connection = getConnection();
             PreparedStatement pstmt = connection.prepareStatement(
                     "DELETE FROM studentenrollments WHERE studentEmail = ?")) {

            pstmt.setString(1, studentEmail);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public List<Enrollment> getPastEnrollmentsForStudent(String studentEmail) {
        List<Enrollment> enrollments = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement pstmt = connection.prepareStatement(
                     "SELECT * FROM studentenrollments WHERE StudentEmail = ? AND (Status = 'Approved' OR Status = 'Rejected')")) {

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


    public List<Enrollment> getCurrentEnrollmentsForStudent(String studentEmail) {
        List<Enrollment> enrollments = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement pstmt = connection.prepareStatement(
                     "SELECT * FROM studentenrollments WHERE StudentEmail = ? AND (Status = 'Pending')")) {

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
    public int getCourseIdByEnrollment(long enrollmentId) {
        int courseId = -1; // Default value if no course ID is found

        try (Connection connection = getConnection();
             PreparedStatement pstmt = connection.prepareStatement(
                     "SELECT CourseId FROM studentenrollments WHERE EnrollmentId = ?")) {

            pstmt.setLong(1, enrollmentId);

            try (ResultSet resultSet = pstmt.executeQuery()) {
                if (resultSet.next()) {
                    courseId = resultSet.getInt("CourseId");
                } else {
                    System.err.println("No course ID found for enrollment ID: " + enrollmentId);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return courseId;
    }
    public List<Enrollment> getEnrollmentsForInstructor(String instructorEmail) {
        List<Enrollment> enrollments = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement pstmt = connection.prepareStatement(
                     "SELECT e.EnrollmentID, e.CourseID, e.StudentEmail, e.Status, e.CreatedAt " +
                             "FROM studentenrollments e " +
                             "JOIN Courses c ON e.CourseID = c.CourseID " +
                             "WHERE c.InstructorEmail = ?"
             )) {

            pstmt.setString(1, instructorEmail);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Enrollment enrollment = new Enrollment();
                enrollment.setEnrollmentID(rs.getInt("EnrollmentID"));
                enrollment.setCourseID(rs.getInt("CourseID"));
                enrollment.setStudentEmail(rs.getString("StudentEmail"));
                enrollment.setStatus(rs.getString("Status"));
                enrollment.setCreatedAt(rs.getTimestamp("CreatedAt"));

                // Retrieve course name using another query
                String courseName = getCourseName(connection, rs.getInt("CourseID"));
                enrollment.setCourseName(courseName);

                enrollments.add(enrollment);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return enrollments;
    }

    private String getCourseName(Connection connection, int courseID) {
        String courseName = null;
        try (PreparedStatement pstmt = connection.prepareStatement(
                "SELECT name FROM Courses WHERE CourseID = ?")) {

            pstmt.setInt(1, courseID);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                courseName = rs.getString("name");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courseName;
    }

    public void approveEnrollment(Long enrollmentId) {
        int courseId = getCourseIdByEnrollment(enrollmentId); // Retrieve course ID

        if (courseId != -1) { // Ensure course ID is valid
            try (Connection connection = getConnection();
                 PreparedStatement pstmt = connection.prepareStatement(
                         "UPDATE studentenrollments SET Status = 'Approved' WHERE EnrollmentID = ?")) {

                pstmt.setLong(1, enrollmentId);
                pstmt.executeUpdate(); // Update enrollment status

            } catch (SQLException e) {
                e.printStackTrace();
                return; // Exit the method if an SQL exception occurs
            }

            try (Connection connection = getConnection();
                 PreparedStatement pstmt = connection.prepareStatement(
                         "UPDATE Courses SET enrolledStudents = enrolledStudents + 1 WHERE CourseID = ?")) {

                pstmt.setInt(1, courseId);
                pstmt.executeUpdate(); // Update enrolledStudents count

            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("Course ID not found for enrollment ID: " + enrollmentId);
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

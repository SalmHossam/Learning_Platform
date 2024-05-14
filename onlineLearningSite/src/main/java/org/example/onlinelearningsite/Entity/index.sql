-- Table for courses
CREATE TABLE Courses (
                         CourseID INT PRIMARY KEY AUTO_INCREMENT,
                         Name VARCHAR(255) NOT NULL,
                         Duration INT, -- Duration in days or hours, depending on the platform
                         Category VARCHAR(100),
                         Rating DECIMAL(3, 2) DEFAULT 0,
                         Capacity INT,
                         EnrolledStudents INT DEFAULT 0,
                         Description TEXT,
                         InstructorEmail VARCHAR(100),
                         CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         UpdatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Table for student enrollments
CREATE TABLE StudentEnrollments (
                                    EnrollmentID INT PRIMARY KEY AUTO_INCREMENT,
                                    CourseID INT,
                                    StudentEmail VARCHAR(100),
                                    Status ENUM('Pending', 'Approved', 'Rejected') DEFAULT 'Pending',
                                    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                    FOREIGN KEY (CourseID) REFERENCES Courses(CourseID)
);


-- Table for course reviews
CREATE TABLE CourseReviews (
                               ReviewID INT PRIMARY KEY AUTO_INCREMENT,
                               CourseID INT,
                               UserEmail VARCHAR(100),
                               Rating INT,
                               Review TEXT,
                               CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               FOREIGN KEY (CourseID) REFERENCES Courses(CourseID)
);

-- Table for notifications
CREATE TABLE Notifications (
                               NotificationID INT PRIMARY KEY AUTO_INCREMENT,
                               UserEmail VARCHAR(100),
                               Message TEXT,
                               CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP

);

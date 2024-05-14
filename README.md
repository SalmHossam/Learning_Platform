# Learning_Platform
## functioning online learning platform

### Objective:
The objective of this assignment is to familiarize ourselves with the concepts provided by EJBs and microservices. We will implement an online learning application using EJBs.

### Requirements:
#### Functional Requirements:
##### Admins:
1. View and manage user accounts, including students and instructors.
2. Review course content before it's published to ensure quality and compliance with platform guidelines.
3. Have the authority to edit or remove courses that violate policies or are deemed inappropriate.
4. Track platform usage by students and instructors, check the courses popularity, ratings, reviews, etc.
##### Instructors:
1. Register and login into the system. Registration should collect information like name, email, password, affiliation, years of experience, and bio.
2. Create courses, where each course has a name, duration, category, rating, capacity, number of enrolled students, and list of reviews.
3. View detailed information about each course and search courses by name, category, or sort by ratings.
4. Accept/Reject student enrollments.
##### Students:
1. Register and login into the system. Registration should collect information like name, email, password, affiliation, and bio.
2. View current and past course enrollments.
3. View detailed information about each course and search courses by name, category, or sort by ratings.
4. Make or cancel course enrollment. Enrollments should be handled in a special way to avoid situations of server failure.
5. Get notified for course enrollments updates.
6. Make a review and rating for a course.

#### Additional Features [For teams of 3]:
1. Admins can create accounts for test centers representatives.
2. Test center representatives can:
   - Login into the system using the generated credentials sent by the admin.
   - Update center information like name, email, address, location, list of branches, and bio.
   - Create exams where an exam has a name, duration, list of available dates, list of scheduled dates, grades, etc.
   - Set grade for a student’s exam.
   - View exams and view student grades of the exams.
3. Students can:
   - Search for available test centers by their nearby geographic location.
   - Register for an exam by specifying the test center, date, and time of the exam.
   - View history for the exam grades.

#### Technical Requirements:
1. Using EJBs:
   - Use any two of these 4 different bean types: Stateless, Stateful, Singleton, Message Driven.
   - Interface should be a web-based interface using any technology of choice to simulate a functioning online learning platform with different users.
   - Expose services as REST APIs.
   - Submission should have a functioning UI and the database can be centralized or in-memory.
2. Using Microservices:
   - Design the system to follow the microservice architectural style, with at least 2 services supporting the same functional requirements.
   - Each service should be implemented as its own project with its own codebase and DB.
   - Have at least 2 complete services, all developed in Java or one service in a different programming language/framework while applying the two EJB types within the remaining services.
   - Submission should have a functioning UI.

### Implementation:
- Some Functionalties in EJBs Java ee and other in python microservices 

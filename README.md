# Learning Management System (LMS)
A learning management system backend API designed to manage and organize courses, assessments (quizzes, assignments), student enrollment, performance tracking, and grading. Built using Java Spring Boot, it implements a layered architecture for scalability and separation of concerns. It provides robust and efficient user authentication, course content management, and progress monitoring.

## Features

### 1. User Authentication
- Registration & Login: Using JWT tokens.
- Role-based access: Restrict access permissions so that they are granted based on role type.


### 2. User Management
- User types: Admin, Instructor, Student.
- Profile management: View and update profile details.
- Admin Roles:
    - Manage overall system settings.
    - Create and manage users.
    - Create and manage courses.
- Instructor Roles:
    - Create and manage courses and course content.
    - Add assignments and quizzes.
    - Grade students and provide feedback.
    - Remove students from courses.
- Student Roles:
    - Enroll in courses.
    - Access course materials.
    - Submit assignments and take quizzes.
    - View grades for assignments and quizzes.


### 3. Course Management
- Course creation: Courses can be created with details such as title, description, and duration.
- Course retrieval: Courses can be browsed and accessed by instructors and students.
- Course Enrollment: A student can enroll in any of the available courses.
- Lesson management: Each course can have multiple lessons that course instructors and enrolled students can access.
- Lesson resources: A lesson can have multiple resources (videos, PDFs, audio).
- Lesson Attendance: The course instructor generates OTPs for each lesson to track attendance. Students confirm attendance by entering the OTP.


### 4. Assessment & Grading
- Question bank: Each course has a question bank in which instructors can add questions of different types of questions (MCQs, true/false, short answers).
- Assessment creation: Instructors can create announcements for quizzes and assignments.
- Quiz submission: Each student gets randomized questions from the question bank.
- Assignment submission: Students upload assignment files to be graded.
- Grading: Automated grading for quizzes, while assignments require manual review
- Performance Tracking: Students can monitor their quiz scores, assignment submissions, and attendance.


### 5. Notifications
- Student notifications: Receive notifications and email alerts for enrollment confirmations, graded assignments, and course updates.
- Instructor notifications: Receive notifications and email alerts for new enrollments and course-related updates.

---

## API Endpoints

### Authentication
- **Register:** `POST /register` - Register a new user with role, name, email, and password.
- **Login:** `POST /login` - Authenticate a user and return a JWT token.

### Users
- **Get Profile**  `GET /profile` - Retrieve the profile of the currently authenticated user.
- **Update Profile** `PUT /profile` - Update the profile of the currently authenticated user.
- **Get all Users** `GET /users` - Retrieve a list of all users.
- **Get User** `GET /users/{id}` - Retrieve the details of a specific user by their ID.
- **Create User** `POST /users/create` - Creates a new user with the provided details.
- **Delete User** `DELETE /users/delete/{id}` - Delete a user by their ID.
- **User Notifications**  `GET /notifications` - Retrieves all notifications for the currently authenticated user.

### Courses
- **Create Course:** `POST /create-course` - Create a new course (Instructor only).
- **Search Course:** `GET /search-course/{courseName}` - Search by course name.
- **Get Course by ID:** `GET /course/{id}` - Retrieve course details by ID.
- **Get All Courses:** `GET /get-all-courses` - List all courses.
- **Get My Courses:** `GET /get-my-courses` - List courses created/enrolled by the user.

### Lessons
- **Add Lesson:** `POST /course/{courseName}/add-lesson` - Add a lesson to a course (Instructor only).
- **Get All Lessons:** `GET /course/{courseName}/lessons` - List lessons in a course.
- **Get Lesson:** `GET /course/{courseName}/lessons/{lessonId}` - Retrieve specific lesson details.
- **Add Resource:** `POST /course/{courseName}/lessons/{lessonId}/add-resource` - Add a file to a lesson (Instructor only).
- **Get All Resources:** `GET /course/{courseName}/lessons/{lessonId}/resources` - List lesson resources.
- **Get Resource:** `GET /course/{courseName}/lessons/{lessonId}/resources/{resourceId}` - Download a specific resource.

### Enrollment
- **Enroll:** `POST /course/{courseName}/enroll` - Enroll in a course (Student only).
- **Get Enrolled Students:** `GET /course/{courseName}/enrolled` - List enrolled students (Instructor only).
- **Remove Student:** `DELETE /course/{courseName}/remove-student/{studentId}` - Remove a student (Instructor only).

### Quizzes
- **Create Question:** `POST /course/{course-name}/create-question` - Create a new question for a specific course.
- **Get Questions:** `GET /course/{course-name}/get-questions` - Retrieve all questions for a specific course.
- **Get Question by ID:** `GET /course/{course-name}/get-question-by-id` - Retrieve a specific question by its ID for a course.
- **Create Quiz:** `POST /course/{course-name}/create-quiz` - Create a new quiz for a specific course.
- **Take Quiz:** `GET /course/{course-name}/{quizName}/take-quiz` - Start a quiz for a specific course as a student.
- **Submit Quiz:** `POST /course/{course-name}/{quizName}/submit-quiz` - Submit quiz answers for a specific course.

### Assignments
- **Create Assignment:** `POST /course/{course-name}/create-assignment` - Create a new assignment for a specific course.
- **Get Assignments:** `GET /course/{course-name}/assignments` - Retrieve all assignments for a specific course.
- **View Assignment:** `GET /course/{course-name}/assignment/{assignment_id}/view` - Retrieve details of a specific assignment in a course.
- **Submit Assignment:** `POST /course/{course-name}/assignment/{assignment_id}/submit` - Submit an assignment for a specific course.
- **Get Assignment Submissions:** `GET /course/{course-name}/assignment/{assignment_id}/submissions` - Retrieve all submissions for a specific assignment.
- **Get Assignment Submission by ID:** `GET /course/{course-name}/assignment/{assignment_id}/submission/{submission_id}` - Retrieve a specific student's assignment submission.

### Grading and Performance
- **Get Quiz Grade:** `GET /course/{course-name}/{quizName}/grade` - Retrieve the grade for a specific quiz.
- **Grade Assignment Submission:** `PUT /course/{course-name}/assignment/{assignment_id}/submission/{submission_id}/grade` - Grade a student's assignment submission.
- **Get Assignment Grade:** `GET /course/{course-name}/assignment/{assignment_id}/get-grade` - Retrieve a student's grade for a specific assignment.

---

## Getting Started

### **Prerequisites**
- Java 17+
- Spring Boot
- Maven
- PostgreSQL

### **Installation**
### 1. Clone the repository:
```bash
git clone https://github.com/omarse7a/Learning-Management-System.git
cd Learning-Management-System
```
### 2. Configure application.yml:
- add your database info in the datasource property.
- add your email info in the mail property

### 3. Build the project:
```bash
mvn clean install
```

### 4. Run the application:
```bash
mvn spring-boot:run
```
or run Application.java from the IDE you're using



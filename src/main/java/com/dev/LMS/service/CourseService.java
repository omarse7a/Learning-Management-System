package com.dev.LMS.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

import com.dev.LMS.dto.CourseDto;
import com.dev.LMS.dto.LessonDto;
import com.dev.LMS.dto.LessonResourceDto;
import com.dev.LMS.dto.StudentDto;
import com.dev.LMS.model.*;
import com.dev.LMS.repository.CourseRepository;
import com.dev.LMS.repository.UserRepository;
import org.aspectj.weaver.ast.Not;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CourseService {
    private final CourseRepository courseRepository;
    private final UserService userService;
    private final EmailService emailService;
    private final NotificationService notificationService;
    @Value("${file.upload.base-path.lesson-resources}") //check application.yml
    private Path resourcesPath ;

    public CourseService(CourseRepository courseRepository, UserService userService, EmailService emailService,NotificationService notificationService)  {
        this.courseRepository = courseRepository;
        this.userService = userService;
        this.emailService = emailService;
        this.notificationService = notificationService;

    }

    public Course createCourse(Course course, Instructor instructor){
        course.setInstructor(instructor);
        courseRepository.save(course);
        return course;
    }

    public Course getCourse(String courseName) {
        Optional<Course> course = courseRepository.findByName(courseName);
        return course.orElse(null);
    }

    public Course getCourseById(int courseId) {
        Optional<Course> course = courseRepository.findById(courseId);
        return course.orElse(null);
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }


    public Set<Course> getCreatedCourses(Instructor instructor) {
        return instructor.getCreatedCourses();

    }

    public Set<Course> getEnrolledCourses(Student student) {
        return student.getEnrolled_courses();
    }

    public Lesson addLesson(Course course, Lesson lesson) {
        course.addLesson(lesson);
        Notification notificationMessage = notificationService.createNotification("New Lesson Added By Instructor: " + course.getInstructor().getName());
        Set<Student> enrolledStudent = course.getEnrolled_students();

        String subject = "New Lesson";
        String content = "A new lesson added " + lesson.getTitle() + "in Course: " + course.getName();

        for (Student s : enrolledStudent) {
            notificationService.addNotifcationStudent(notificationMessage, s);
            emailService.sendEmail(
                    s.getEmail(),
                    s.getName(),
                    subject,
                    content,
                    course.getInstructor().getName()
            );
        }
        courseRepository.save(course);
        return course.getLessons().getLast();
    }

    public Lesson getLessonbyId(Course course, int lessonId) {
        List<Lesson> lessonList = course.getLessons();
        for (Lesson l : lessonList)
        {
            if (l.getLesson_id() == lessonId)
                return l;
        }
        return null;

    }


    public String addLessonResource(Course course, int lessonId, MultipartFile file) {
        String fileName = file.getOriginalFilename();
        String fileType = file.getContentType();
        try{
            if(fileName.contains("..")){
                throw new RuntimeException("Invalid file name");
            }
            Files.copy(file.getInputStream(), this.resourcesPath.resolve(file.getOriginalFilename()));

        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }

        Lesson lesson = getLessonbyId(course, lessonId);
        if (lesson != null) {
            LessonResource lessonResource = new LessonResource(fileName, fileType);
            lesson.addLessonResource(lessonResource);
            courseRepository.save(course);
            return "file added successfully: " + fileName;
        }
        else throw new IllegalStateException("Lesson not found");
    }

    public List<LessonResourceDto> getLessonResources(Course course, User user, int lessonId) {
        Lesson lesson = getLessonbyId(course, lessonId);
        if (lesson == null) throw new IllegalStateException("Lesson not found");
        if (user instanceof Instructor) {
           Instructor instructor = (Instructor) user;
           if (instructor.getId() != course.getInstructor().getId())
               throw new IllegalStateException("You are not authorized to access this resource");
        }
        if (user instanceof Student) {
            Student student = (Student) user;
            if (!student.getEnrolled_courses().contains(course) || !lesson.getAttendees().contains(student) )
                throw new IllegalStateException("You are not authorized to access this resource");
        }
        List<LessonResource> lessonResources = lesson.getLessonResources();
        List<LessonResourceDto> lessonResourceDtos = new ArrayList<>();
        for (LessonResource lessonResource : lessonResources) {
            lessonResourceDtos.add(new LessonResourceDto(lessonResource));
        }
        return lessonResourceDtos;
    }

    public byte[] getFileResources(Course course, User user, int lessonId, int resourceId) {
        Lesson lesson = getLessonbyId(course, lessonId);
        if (lesson == null) throw new IllegalStateException("Lesson not found");
        if (user instanceof Instructor) {
            Instructor instructor = (Instructor) user;
            if (instructor.getId() != course.getInstructor().getId())
                throw new IllegalStateException("You are not authorized to access this resource(I)");
        }
        if (user instanceof Student) {
            Student student = (Student) user;
            if (!student.getEnrolled_courses().contains(course) || !lesson.getAttendees().contains(student) )
                throw new IllegalStateException("You are not authorized to access this resource");
        }
        List<LessonResource> resources = lesson.getLessonResources();
        LessonResource resource = null;
        for (LessonResource lessonResource : resources) {
            if (lessonResource.getResource_id() == resourceId) {
                resource = lessonResource;
                break;
            }
        }
        if (resource == null)
            throw new IllegalStateException("Resource not found");

        // storing the file into a byte array
        String fileName = resource.getFile_name();
        try {
            byte[] resourceData = Files.readAllBytes(resourcesPath.resolve(fileName));
            return resourceData;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public Set<CourseDto> enrollCourse(String courseName, User user) {
        Student student = (Student) user;
        Course course = getCourse(courseName);
        if (course == null) throw new IllegalStateException("Course not found");
        if (course.getEnrolled_students().contains(student))
            throw new IllegalStateException("You are already enrolled in this course");
        course.addStudent(student);

        Instructor instructor = course.getInstructor();
        Notification notificationMessage1 = notificationService.createNotification("You have successfully enrolled in Course: " + courseName);
        notificationService.addNotifcationStudent(notificationMessage1, student);
        String subject = "Enrollment Confirmation";
        String content = "You have successfully enrolled in Course: " + courseName;
        emailService.sendEmail(
                student.getEmail(),
                student.getName(),
                subject,
                content,
                instructor.getName()
        );


        Notification notificationMessage2 = notificationService.createNotification("Student:" +student.getName() + " Enrolled in Course: " + courseName);

        notificationService.addNotificationInstructor(notificationMessage2, instructor);

        courseRepository.save(course);
        Set<Course> enrolledCourses = student.getEnrolled_courses();
        Set<CourseDto> enrolledCoursesDto = new HashSet<>();
        for (Course c : enrolledCourses) {
            enrolledCoursesDto.add(new CourseDto(c));
        }
        return enrolledCoursesDto;


    }

    public Set<StudentDto> getEnrolledStd(String courseName) {
        Course course = getCourse(courseName);
        if (course == null) throw new IllegalStateException("Course not found");
        Set<Student> students = course.getEnrolled_students();
        Set<StudentDto> studentDtos = new HashSet<>();
        for (Student s : students) {
            StudentDto studentDto = new StudentDto(s);
            studentDtos.add(studentDto);
        }
        return studentDtos;
    }

    public Set<Student> getEnrolledStd(Course course){
        return course.getEnrolled_students();
    }

    public void removeEnrolledstd(Course course, Instructor instructor, int studentId) {
        User user = userService.getUserById(studentId);
        if (user == null || !(user instanceof  Student))  throw new IllegalStateException("Student not found");
        Student student = (Student) user;
        course.removeStudent(student);
        courseRepository.save(course);
     }

    public int generateOTP(Course course, Set<Student> students, Instructor instructor,  Lesson lesson, int duration) {
        Random random = new Random();
        int otp = random.nextInt(900000) + 100000;
        LessonOTP lessonOTP = new LessonOTP();
        lessonOTP.setOtpValue(otp);
        lessonOTP.setExpireAt(LocalDateTime.now().plusDays(duration));
        lesson.addLessonOTP(lessonOTP);
        for (Student student : students) {
            emailService.sendOTP(
                    student.getEmail(),
                    student.getName(),
                    lesson.getTitle(),
                    lesson.getDescription(),
                    duration,
                    instructor.getName(),
                    otp,
                    course.getName()
            );
        }
        courseRepository.save(course);

        return otp;
    }

    public LessonDto attendLesson(Course course, Student student, Lesson lesson, int givenOtp) {
        LessonOTP lessonOTP = lesson.getLessonOTP();
        if (lessonOTP.getOtpValue() == givenOtp) {
            if (lessonOTP.getExpireAt().isAfter(LocalDateTime.now())) {
                lesson.addAttendee(student);

                Notification notificationMessage = notificationService.createNotification("Student:" +student.getName() + " Attends Lesson: " + lesson.getTitle());
                notificationService.addNotificationInstructor(notificationMessage, course.getInstructor());

                courseRepository.save(course);
                return new LessonDto(lesson);
            }
            else{
                throw new IllegalStateException("OTP expired");
            }
        }
        else {
            throw new IllegalStateException("Incorrect OTP");
        }
    }

    public Set<LessonDto> getLessonAttended(Course course,  Student student) {
        Set<Lesson> lessons =  student.getLessonAttended();
        Set<LessonDto> lessonDtos = new HashSet<>();
        for (Lesson lesson : lessons) {
            if (lesson.getCourse().getCourseId() == course.getCourseId()) {
                lessonDtos.add(new LessonDto(lesson));
            }
        }
        return lessonDtos;
    }

    public List<StudentDto> getAttendance(Lesson lesson) {
        Set<Student> students = lesson.getAttendees();
        List<StudentDto> studentDtos = new ArrayList<>();
        for (Student student : students) {
            studentDtos.add(new StudentDto(student));
        }
        return studentDtos;
    }
}

package com.dev.LMS.service;

import com.dev.LMS.dto.NotificationDto;
import com.dev.LMS.model.Instructor;
import com.dev.LMS.model.Notification;
import com.dev.LMS.model.Student;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class NotificationService {

    public Notification createNotification(String notificationMessage) {
        Notification notification = new Notification(notificationMessage, LocalDateTime.now(),false);
        return notification;
    }

    public void addNotifcationStudent(Notification notification, Student student) {
        student.addNotification(notification);
    }

    public void removeNotifcationStudent(Notification notification, Student student) {
        student.removeNotification(notification);
    }

    public void addNotificationInstructor(Notification notification, Instructor instructor) {
        instructor.addNotification(notification);
    }
    public void removeNotificationInstructor(Notification notification, Instructor instructor) {
        instructor.removeNotification(notification);
    }

    public List<NotificationDto> getStudentNotification(Student student) {
        List<Notification> notifications = student.getNotifications();
        List<NotificationDto> notificationDtos = new ArrayList<>();
        for (Notification notification : notifications) {
            notificationDtos.add(new NotificationDto(notification));
        }
        return notificationDtos;
    }

    public List<NotificationDto> getInstructorNotification(Instructor instructor) {
        List<Notification> notifications = instructor.getNotifications();
        List<NotificationDto> notificationDtos = new ArrayList<>();
        for (Notification notification : notifications) {
            notificationDtos.add(new NotificationDto(notification));
        }
        return notificationDtos;
    }
}

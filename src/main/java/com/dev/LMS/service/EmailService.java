package com.dev.LMS.service;


import com.dev.LMS.model.Course;
import com.dev.LMS.model.Instructor;
import com.dev.LMS.model.Student;
import com.dev.LMS.model.User;
import jakarta.mail.internet.MimeMessage;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.List;

@Data
@NoArgsConstructor
@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;



    public Boolean sendOTP(String studentEmail,String studentName,String lessonTitle ,String description,int duration,String instructorName, int OTP,String courseName) {
        try{
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,true);
            String html = """
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <style>
                            body {
                                font-family: Arial, sans-serif;
                                line-height: 1.6;
                            }
                            .container {
                                max-width: 600px;
                                margin: 0 auto;
                                padding: 20px;
                                border: 1px solid #ddd;
                                border-radius: 8px;
                                background-color: #f9f9f9;
                            }
                            .header {
                                font-size: 18px;
                                font-weight: bold;
                                margin-bottom: 20px;
                            }
                            .footer {
                                font-size: 12px;
                                color: #888;
                                margin-top: 20px;
                            }
                        </style>
                    </head>
                    <body>
                        <div class="container">
                            <div class="header">Lesson Attendance OTP</div>
                            <p>Dear <strong>${studentName}</strong>,</p>
                            <p>We are pleased to provide you with the details of your upcoming lesson in the course you have enrolled in: <strong>${courseName}</strong>.</p>
                            <ul>
                                <li><strong>Lesson Title:</strong> ${lessonTitle}</li>
                                <li><strong>Description:</strong> ${description}</li>
                            </ul>
                            <p>Your One-Time Password (OTP) for attendance is: <strong>${otp}</strong></p>
                            <p>Please note that this OTP will expire after <strong>${duration}</strong> day from time of sending this email.</p>
                            <p>Thank you,<br>
                            ${instructorName}</p>
                            <div class="footer">If you have any questions, please contact us.</div>
                        </div>
                    </body>
                    </html>
                    
                    """;
            html = html.replace("${studentName}", studentName)
                    .replace("${courseName}", courseName)
                    .replace("${lessonTitle}", lessonTitle)
                    .replace("${description}", description)
                    .replace("${otp}", String.valueOf(OTP))
                    .replace("${duration}", String.valueOf(duration))
                    .replace("${instructorName}", instructorName);
            helper.setTo(studentEmail);
            helper.setSubject("OTP For Attending:"+ lessonTitle);
            helper.setText(html, true);
            mailSender.send(mimeMessage);
            return true;

        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }

    }

    public Boolean sendEmail(String studentEmail, String studentName, String subject, String content, String instructorName) {
        try{
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,true);
            String html = """
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <style>
                            body {
                                font-family: Arial, sans-serif;
                                line-height: 1.6;
                            }
                            .container {
                                max-width: 600px;
                                margin: 0 auto;
                                padding: 20px;
                                border: 1px solid #ddd;
                                border-radius: 8px;
                                background-color: #f9f9f9;
                            }
                            .header {
                                font-size: 18px;
                                font-weight: bold;
                                margin-bottom: 20px;
                            }
                            .footer {
                                font-size: 12px;
                                color: #888;
                                margin-top: 20px;
                            }
                        </style>
                    </head>
                    <body>
                        <div class="container">
                            <p>Dear <strong>${studentName}</strong>,</p>
                            <p>We are pleased to provide you with updates: <strong>${subject}</strong>.</p>
   
                            <p>${content}</p>
                             
                            <p>Thank you,<br>
                            ${instructorName}</p>
                            <div class="footer">If you have any questions, please contact us.</div>
                        </div>
                    </body>
                    </html>
                    
                    """;
            html = html.replace("${studentName}", studentName)
                    .replace("${subject}", subject)
                    .replace("${content}", content)
                    .replace("${instructorName}", instructorName);
            helper.setTo(studentEmail);
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(mimeMessage);
            return true;

        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }

    }



}

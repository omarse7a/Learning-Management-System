package com.dev.LMS.dto;

import com.dev.LMS.model.Notification;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDto {
    private String Message;
    private String time;

    public NotificationDto(Notification notification) {
        this.Message = notification.getMessage();
        this.time = notification.getTime().toString();
    }

}

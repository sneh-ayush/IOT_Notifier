package com.sneha.iotnotifier.model;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationPayload {
    private Long tenantId;
    private String title;
    private String message;
    private String clientId;
    private String type;
    private String priority;
}

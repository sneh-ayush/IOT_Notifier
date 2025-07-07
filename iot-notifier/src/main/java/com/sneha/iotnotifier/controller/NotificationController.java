package com.sneha.iotnotifier.controller;

import com.sneha.iotnotifier.model.NotificationPayload;
import com.sneha.iotnotifier.model.Tenant;
import com.sneha.iotnotifier.repository.TenantRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/notify")
public class NotificationController {

    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    @Qualifier("mqttOutboundChannel")
    private MessageChannel mqttOutboundChannel;

    @PostConstruct
    public void asd() {
        System.out.println("asdasdasdasd");
    }

    @PostMapping("/v1/hi")
    public ResponseEntity<String> sendNotification(@RequestBody NotificationPayload payload) {
        try {
            logger.info(" Entered /notify/v1/hi POST controller");

            if (payload == null) {
                logger.error("Payload is null");
                return ResponseEntity.badRequest().body("Payload is null - bad request");
            }

            logger.info(" Payload: tenantId={}, title={}, message={}, clientId={}, type={}, priority={}",
                    payload.getTenantId(),
                    payload.getTitle(),
                    payload.getMessage(),
                    payload.getClientId(),
                    payload.getType(),
                    payload.getPriority());

            // Get tenant from database
            Tenant tenant = tenantRepository.findById(payload.getTenantId())
                    .orElseThrow(() -> {
                        logger.error(" Tenant with ID {} not found", payload.getTenantId());
                        return new RuntimeException("Tenant not found");
                    });

            // Build topic
            String topic = tenant.getTopicPrefix();
            if (payload.getClientId() != null && !payload.getClientId().isEmpty()) {
                topic += "/client/" + payload.getClientId();
            } else {
                topic += "/all";
            }

            // Create JSON payload
            String jsonPayload = String.format(
                    "{ \"title\": \"%s\", \"message\": \"%s\", \"type\": \"%s\", \"priority\": \"%s\", \"timestamp\": \"%s\" }",
                    payload.getTitle(),
                    payload.getMessage(),
                    payload.getType(),
                    payload.getPriority(),
                    Instant.now().toString()
            );

            logger.info(" Sending to topic: {}", topic);
            logger.info("MQTT Payload: {}", jsonPayload);

            // Send the message to MQTT
            mqttOutboundChannel.send(
                    MessageBuilder.withPayload(jsonPayload)
                            .setHeader(MqttHeaders.TOPIC, topic)
                            .build()
            );

            return ResponseEntity.ok("Notification sent to topic: " + topic);

        } catch (Exception e) {
            logger.error(" Exception while sending notification", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error processing request: " + e.getMessage());
        }
    }

    @GetMapping("/ping")
    public String testConnection() {
        System.out.println("/ping endpoint hit from Postman");
        return " Postman can reach Spring Boot";
    }
}

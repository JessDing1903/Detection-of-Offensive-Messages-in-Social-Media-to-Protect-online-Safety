package com.oms.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Document(collection = "messages")
public class Message {

    @Id
    private String id;

    private String text;
    private String author;
    private String platform;          // twitter, facebook, instagram …
    private String status;            // PENDING, ANALYZED, FLAGGED, CLEARED

    // ML result
    private String label;             // offensive / normal
    private boolean isOffensive;
    private double confidence;
    private String severity;          // none, low, medium, high

    // Moderation
    private boolean reviewed;
    private String moderatorNote;

    private Instant createdAt;
    private Instant analyzedAt;
}

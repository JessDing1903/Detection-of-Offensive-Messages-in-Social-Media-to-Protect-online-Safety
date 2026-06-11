package com.oms.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Document(collection = "reports")
public class Report {

    @Id
    private String id;

    private String messageId;
    private String reportedBy;
    private String reason;           // HATE_SPEECH, HARASSMENT, THREATS, SPAM, OTHER
    private String status;           // OPEN, UNDER_REVIEW, RESOLVED, DISMISSED
    private String resolution;

    private Instant reportedAt;
    private Instant resolvedAt;
}

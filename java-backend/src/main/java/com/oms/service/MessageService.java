package com.oms.service;

import com.oms.dto.MessageRequest;
import com.oms.dto.MlAnalysisResult;
import com.oms.model.Message;
import com.oms.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final MlService mlService;

    public Message analyzeAndSave(MessageRequest request) {
        Message message = new Message();
        message.setText(request.getText());
        message.setAuthor(request.getAuthor());
        message.setPlatform(request.getPlatform());
        message.setStatus("PENDING");
        message.setCreatedAt(Instant.now());

        MlAnalysisResult result = mlService.analyze(request.getText());

        message.setLabel(result.getLabel());
        message.setOffensive(result.isOffensive());
        message.setConfidence(result.getConfidence());
        message.setSeverity(result.getSeverity());
        message.setStatus(result.isOffensive() ? "FLAGGED" : "CLEARED");
        message.setAnalyzedAt(Instant.now());

        Message saved = messageRepository.save(message);
        log.info("Message [{}] analyzed — label={} severity={} confidence={}",
                saved.getId(), saved.getLabel(), saved.getSeverity(), saved.getConfidence());
        return saved;
    }

    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    public List<Message> getOffensiveMessages() {
        return messageRepository.findByIsOffensiveTrue();
    }

    public List<Message> getMessagesByPlatform(String platform) {
        return messageRepository.findByPlatform(platform);
    }

    public List<Message> getMessagesByAuthor(String author) {
        return messageRepository.findByAuthor(author);
    }

    public List<Message> getMessagesBySeverity(String severity) {
        return messageRepository.findBySeverityOrderByConfidenceDesc(severity);
    }

    public Message markReviewed(String id, String note) {
        return messageRepository.findById(id).map(msg -> {
            msg.setReviewed(true);
            msg.setModeratorNote(note);
            msg.setStatus("REVIEWED");
            return messageRepository.save(msg);
        }).orElseThrow(() -> new RuntimeException("Message not found: " + id));
    }

    public Map<String, Object> getStats() {
        long total     = messageRepository.count();
        long offensive = messageRepository.countByIsOffensiveTrue();
        long normal    = total - offensive;
        long high      = messageRepository.countBySeverity("high");
        long medium    = messageRepository.countBySeverity("medium");
        long low       = messageRepository.countBySeverity("low");

        return Map.of(
                "total",          total,
                "offensive",      offensive,
                "normal",         normal,
                "offensiveRate",  total > 0 ? (double) offensive / total : 0.0,
                "bySeverity",     Map.of("high", high, "medium", medium, "low", low)
        );
    }
}

package com.oms.controller;

import com.oms.dto.MessageRequest;
import com.oms.model.Message;
import com.oms.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MessageController {

    private final MessageService messageService;

    /** Submit a message for offensive-content analysis */
    @PostMapping("/analyze")
    public ResponseEntity<Message> analyze(@Valid @RequestBody MessageRequest request) {
        return ResponseEntity.ok(messageService.analyzeAndSave(request));
    }

    /** Get all stored messages */
    @GetMapping
    public ResponseEntity<List<Message>> getAll() {
        return ResponseEntity.ok(messageService.getAllMessages());
    }

    /** Get only flagged (offensive) messages */
    @GetMapping("/offensive")
    public ResponseEntity<List<Message>> getOffensive() {
        return ResponseEntity.ok(messageService.getOffensiveMessages());
    }

    /** Filter messages by social media platform */
    @GetMapping("/platform/{platform}")
    public ResponseEntity<List<Message>> getByPlatform(@PathVariable String platform) {
        return ResponseEntity.ok(messageService.getMessagesByPlatform(platform));
    }

    /** Filter messages by author */
    @GetMapping("/author/{author}")
    public ResponseEntity<List<Message>> getByAuthor(@PathVariable String author) {
        return ResponseEntity.ok(messageService.getMessagesByAuthor(author));
    }

    /** Filter messages by severity: high | medium | low */
    @GetMapping("/severity/{severity}")
    public ResponseEntity<List<Message>> getBySeverity(@PathVariable String severity) {
        return ResponseEntity.ok(messageService.getMessagesBySeverity(severity));
    }

    /** Mark a message as manually reviewed */
    @PatchMapping("/{id}/review")
    public ResponseEntity<Message> review(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(messageService.markReviewed(id, body.getOrDefault("note", "")));
    }

    /** Dashboard statistics */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> stats() {
        return ResponseEntity.ok(messageService.getStats());
    }
}

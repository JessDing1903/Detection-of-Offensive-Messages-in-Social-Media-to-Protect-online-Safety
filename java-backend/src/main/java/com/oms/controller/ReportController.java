package com.oms.controller;

import com.oms.model.Report;
import com.oms.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReportController {

    private final ReportService reportService;

    /** User reports a message */
    @PostMapping
    public ResponseEntity<Report> create(@RequestBody Map<String, String> body) {
        Report report = reportService.createReport(
                body.get("messageId"),
                body.get("reportedBy"),
                body.getOrDefault("reason", "OTHER")
        );
        return ResponseEntity.ok(report);
    }

    /** Moderator resolves a report */
    @PatchMapping("/{id}/resolve")
    public ResponseEntity<Report> resolve(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(reportService.resolveReport(id, body.getOrDefault("resolution", "")));
    }

    /** List all open reports (moderator queue) */
    @GetMapping("/open")
    public ResponseEntity<List<Report>> getOpen() {
        return ResponseEntity.ok(reportService.getOpenReports());
    }

    /** Reports linked to a specific message */
    @GetMapping("/message/{messageId}")
    public ResponseEntity<List<Report>> getByMessage(@PathVariable String messageId) {
        return ResponseEntity.ok(reportService.getReportsByMessageId(messageId));
    }
}

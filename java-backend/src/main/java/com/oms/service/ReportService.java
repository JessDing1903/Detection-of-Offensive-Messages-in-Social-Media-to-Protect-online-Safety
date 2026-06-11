package com.oms.service;

import com.oms.model.Report;
import com.oms.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;

    public Report createReport(String messageId, String reportedBy, String reason) {
        Report report = new Report();
        report.setMessageId(messageId);
        report.setReportedBy(reportedBy);
        report.setReason(reason);
        report.setStatus("OPEN");
        report.setReportedAt(Instant.now());
        Report saved = reportRepository.save(report);
        log.info("Report [{}] created for message [{}]", saved.getId(), messageId);
        return saved;
    }

    public Report resolveReport(String reportId, String resolution) {
        return reportRepository.findById(reportId).map(r -> {
            r.setStatus("RESOLVED");
            r.setResolution(resolution);
            r.setResolvedAt(Instant.now());
            return reportRepository.save(r);
        }).orElseThrow(() -> new RuntimeException("Report not found: " + reportId));
    }

    public List<Report> getOpenReports() {
        return reportRepository.findByStatus("OPEN");
    }

    public List<Report> getReportsByMessageId(String messageId) {
        return reportRepository.findByMessageId(messageId);
    }
}

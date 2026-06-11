package com.oms.repository;

import com.oms.model.Report;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends MongoRepository<Report, String> {

    List<Report> findByMessageId(String messageId);

    List<Report> findByStatus(String status);

    long countByStatus(String status);
}

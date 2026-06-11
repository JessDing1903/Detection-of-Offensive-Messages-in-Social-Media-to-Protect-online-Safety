package com.oms.repository;

import com.oms.model.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends MongoRepository<Message, String> {

    List<Message> findByIsOffensiveTrue();

    List<Message> findBySeverityOrderByConfidenceDesc(String severity);

    List<Message> findByAuthor(String author);

    List<Message> findByPlatform(String platform);

    List<Message> findByStatusOrderByCreatedAtDesc(String status);

    long countByIsOffensiveTrue();

    long countBySeverity(String severity);
}

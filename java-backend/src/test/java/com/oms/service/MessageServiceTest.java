package com.oms.service;

import com.oms.dto.MessageRequest;
import com.oms.dto.MlAnalysisResult;
import com.oms.model.Message;
import com.oms.repository.MessageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock MessageRepository messageRepository;
    @Mock MlService mlService;
    @InjectMocks MessageService messageService;

    @Test
    void offensiveMessageGetsStatusFlagged() {
        MlAnalysisResult mlResult = new MlAnalysisResult();
        mlResult.setLabel("offensive");
        mlResult.setOffensive(true);
        mlResult.setConfidence(0.92);
        mlResult.setSeverity("high");

        when(mlService.analyze(anyString())).thenReturn(mlResult);
        when(messageRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        MessageRequest req = new MessageRequest();
        req.setText("I hate you");
        req.setAuthor("user1");
        req.setPlatform("twitter");

        Message result = messageService.analyzeAndSave(req);

        assertThat(result.isOffensive()).isTrue();
        assertThat(result.getStatus()).isEqualTo("FLAGGED");
        assertThat(result.getSeverity()).isEqualTo("high");
    }

    @Test
    void normalMessageGetsStatusCleared() {
        MlAnalysisResult mlResult = new MlAnalysisResult();
        mlResult.setLabel("normal");
        mlResult.setOffensive(false);
        mlResult.setConfidence(0.88);
        mlResult.setSeverity("none");

        when(mlService.analyze(anyString())).thenReturn(mlResult);
        when(messageRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        MessageRequest req = new MessageRequest();
        req.setText("Have a great day!");
        req.setAuthor("user2");
        req.setPlatform("instagram");

        Message result = messageService.analyzeAndSave(req);

        assertThat(result.isOffensive()).isFalse();
        assertThat(result.getStatus()).isEqualTo("CLEARED");
    }
}

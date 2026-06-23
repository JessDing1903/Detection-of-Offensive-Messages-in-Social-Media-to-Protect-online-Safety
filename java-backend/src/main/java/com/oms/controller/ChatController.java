package com.oms.controller;

import com.oms.dto.MlAnalysisResult;
import com.oms.service.MessageService;
import com.oms.service.MlService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ChatController {

    private final MessageService messageService;
    private final MlService mlService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> chat(@RequestBody Map<String, String> body) {
        String userMsg = body.getOrDefault("message", "").trim();
        Map<String, Object> response = new HashMap<>();

        if (userMsg.isEmpty()) {
            response.put("reply", "Hello! I am your AI assistant. How can I help you today?");
            response.put("suggestions", List.of("Show stats", "How it works", "Analyze custom message"));
            return ResponseEntity.ok(response);
        }

        String lower = userMsg.toLowerCase();

        // 1. Analyze / Check request
        if (lower.startsWith("analyze ") || lower.startsWith("check ") || lower.startsWith("scan ")) {
            int spaceIdx = userMsg.indexOf(" ");
            String textToAnalyze = userMsg.substring(spaceIdx + 1).trim();
            if (textToAnalyze.isEmpty()) {
                response.put("reply", "Please provide a message text after the command. Example: *analyze you are a loser*");
                return ResponseEntity.ok(response);
            }
            MlAnalysisResult result = mlService.analyze(textToAnalyze);
            String reply = String.format("🔍 **Analysis result for:** \"%s\"\n\n" +
                    "• **Label:** %s\n" +
                    "• **Offensive:** %s\n" +
                    "• **Confidence:** %.1f%%\n" +
                    "• **Severity:** %s",
                    textToAnalyze,
                    result.getLabel().toUpperCase(),
                    result.isOffensive() ? "🚨 YES" : "✅ NO",
                    result.getConfidence() * 100,
                    result.getSeverity().toUpperCase()
            );
            response.put("reply", reply);
            return ResponseEntity.ok(response);
        }

        // 2. Stats request
        if (lower.contains("stat") || lower.contains("count") || lower.contains("metric") || lower.contains("flagged") || lower.contains("severity")) {
            Map<String, Object> stats = messageService.getStats();
            long total = (long) stats.getOrDefault("total", 0L);
            long offensive = (long) stats.getOrDefault("offensive", 0L);
            long normal = (long) stats.getOrDefault("normal", 0L);
            double rate = (double) stats.getOrDefault("offensiveRate", 0.0);
            @SuppressWarnings("unchecked")
            Map<String, Object> bySev = (Map<String, Object>) stats.getOrDefault("bySeverity", Map.of());
            long high = (long) bySev.getOrDefault("high", 0L);
            long medium = (long) bySev.getOrDefault("medium", 0L);
            long low = (long) bySev.getOrDefault("low", 0L);

            String reply = String.format("📊 **Current System Statistics:**\n\n" +
                    "• **Total Messages Analyzed:** %d\n" +
                    "• **Flagged (Offensive):** %d (%.1f%% of total)\n" +
                    "• **Cleared (Normal):** %d\n\n" +
                    "🚨 **Flagged Messages by Severity:**\n" +
                    "• High: %d\n" +
                    "• Medium: %d\n" +
                    "• Low: %d",
                    total, offensive, rate * 100, normal, high, medium, low
            );
            response.put("reply", reply);
            return ResponseEntity.ok(response);
        }

        // 3. Platform information
        if (lower.contains("platform") || lower.contains("twitter") || lower.contains("facebook") || lower.contains("instagram") || lower.contains("youtube") || lower.contains("tiktok")) {
            String reply = "🌐 **Supported Platforms:**\n\n" +
                    "We monitor and ingest messages from the following main platforms:\n" +
                    "• **Twitter/X**\n" +
                    "• **Facebook**\n" +
                    "• **Instagram**\n" +
                    "• **YouTube**\n" +
                    "• **TikTok**\n\n" +
                    "You can select any of these platforms when submitting messages via the main panel!";
            response.put("reply", reply);
            return ResponseEntity.ok(response);
        }

        // 4. How it works
        if (lower.contains("how it works") || lower.contains("how does") || lower.contains("tech") || lower.contains("architecture") || lower.contains("model") || lower.contains("stack")) {
            String reply = "⚙️ **How it Works:**\n\n" +
                    "1. **Frontend:** The dashboard (HTML/JS) communicates with a Spring Boot API backend.\n" +
                    "2. **Backend:** The Spring Boot backend exposes REST endpoints, orchestrates message processing, and stores all messages/reports in MongoDB.\n" +
                    "3. **ML Service:** A Python microservice (Flask) runs a custom Classifier (scikit-learn with text pre-processing and NLTK) to analyze offensive language probability, label it, and evaluate severity.\n" +
                    "4. **Moderation:** Flagged messages appear on the dashboard for review and manual actions.";
            response.put("reply", reply);
            return ResponseEntity.ok(response);
        }

        // 5. Help / Guide
        if (lower.contains("help") || lower.contains("what can you") || lower.contains("guide") || lower.contains("do for me")) {
            String reply = "💡 **Here is what I can do for you:**\n\n" +
                    "• **Check System Metrics:** Ask me \"show stats\" or \"how many messages are flagged\".\n" +
                    "• **Analyze Custom Messages:** Ask me to check something by typing \"analyze [your text]\" (e.g. *analyze this is terrible*).\n" +
                    "• **App Architecture:** Ask \"how does this app work?\" or \"what is the stack?\".\n" +
                    "• **Platforms:** Ask \"what platforms are supported?\".";
            response.put("reply", reply);
            response.put("suggestions", List.of("Show stats", "How it works", "Analyze custom message"));
            return ResponseEntity.ok(response);
        }

        // 6. Greetings
        if (lower.contains("hello") || lower.contains("hi ") || lower.contains("hey") || lower.equals("hi")) {
            response.put("reply", "Hello! I am the Offensive Message Detector Assistant. I can check stats, explain the system, or analyze custom messages for you. What would you like to do?");
            response.put("suggestions", List.of("Show stats", "How it works", "What can you do?"));
            return ResponseEntity.ok(response);
        }

        // Fallback
        response.put("reply", "I'm not sure how to answer that request. I can show system stats, explain how the app works, list supported platforms, or analyze any text you want.\n\n" +
                "Try asking:\n" +
                "• *Show stats*\n" +
                "• *How does it work?*\n" +
                "• *Analyze [some text]*");
        response.put("suggestions", List.of("Show stats", "How it works", "Analyze custom message"));
        return ResponseEntity.ok(response);
    }
}

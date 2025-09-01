// OutboxEventPublisher.java
package com.tradesettlement.trade_service.service;

import com.tradesettlement.trade_service.entities.OutboxEvent;
import com.tradesettlement.trade_service.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class OutboxEventPublisher {

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    @Value("${trade.events.topic}")
    private String tradeEventsTopic;

    @Value("${app.kafka.outbox-polling-interval}")
    private long pollingInterval;

    @Scheduled(fixedDelayString = "${app.kafka.outbox-polling-interval}")
    @Transactional
    public void publishPendingEvents() {
        try {
            List<OutboxEvent> pendingEvents = outboxEventRepository.findPendingEvents(
                Arrays.asList(OutboxEvent.EventStatus.PENDING, OutboxEvent.EventStatus.FAILED)
            );

            log.debug("Found {} pending outbox events", pendingEvents.size());

            for (OutboxEvent event : pendingEvents) {
                    publishEvent(event);
            }
        } catch (Exception e) {
            log.error("Error during outbox event publishing", e);
        }
    }

    private void publishEvent(OutboxEvent event) {
        try {
            // Mark as processing
            event.setStatus(OutboxEvent.EventStatus.PROCESSING);
            outboxEventRepository.save(event);

            // Publish to Kafka
            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(
                tradeEventsTopic,
                event
            );

            future.whenComplete((result, throwable) -> {
                if (throwable == null) {
                    markEventAsSent(event);
                    log.info("Successfully published event {} to topic {}", 
                        event.getId(), tradeEventsTopic);
                } else {
                    markEventAsFailed(event, throwable.getMessage());
                    log.error("Failed to publish event {} to topic {}", 
                        event.getId(), tradeEventsTopic, throwable);
                }
            });

        } catch (Exception e) {
            markEventAsFailed(event, e.getMessage());
            log.error("Error publishing outbox event {}", event.getId(), e);
        }
    }

    private void markEventAsSent(OutboxEvent event) {
        event.setStatus(OutboxEvent.EventStatus.SENT);
        event.setProcessedAt(LocalDateTime.now());
        outboxEventRepository.save(event);
    }

    private void markEventAsFailed(OutboxEvent event, String errorMessage) {
        event.setStatus(OutboxEvent.EventStatus.FAILED);
        outboxEventRepository.save(event);
    }
}
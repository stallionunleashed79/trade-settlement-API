package com.tradesettlement.trade_service.service;

import com.tradesettlement.trade_service.entities.IdempotencyKeyEntity;
import com.tradesettlement.trade_service.entities.TradeEntity;
import com.tradesettlement.trade_service.exceptions.DuplicateFileException;
import com.tradesettlement.trade_service.mapper.TradeMapper;
import com.tradesettlement.trade_service.models.ErrorResponse;
import com.tradesettlement.trade_service.models.Trade;
import com.tradesettlement.trade_service.models.TradeRequest;
import com.tradesettlement.trade_service.repository.IdempotencyKeyRepository;
import com.tradesettlement.trade_service.repository.TradeRepository;
import com.tradesettlement.trade_service.util.TradeServiceUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TradeService {

    @Value("${trade.events.topic}")
    private String tradeEventsTopic;
    private final TradeServiceUtil tradeServiceUtil;
    private final KafkaTemplate<String, Object> kafkaProducer;
    private final IdempotencyKeyRepository idempotencyKeyRepository;
    private final TradeMapper tradeMapper;
    private final TradeRepository tradeRepository;

    @Transactional
    public List<Trade> uploadCsvFile(final MultipartFile file) {
        final List<Trade> trades = tradeServiceUtil.buildTradeEvents(file);
        final String correlationId = UUID.randomUUID().toString();
        // Check for duplicate using Redis
        String idempotencyKey = generateIdempotencyKey(trades);
        if (isDuplicateRequest(idempotencyKey)) {
            log.warn("Duplicate request detected for key: {}", idempotencyKey);
            throw new DuplicateFileException("Duplicate file upload");
        }
        final IdempotencyKeyEntity idempotencyKeyEntity = IdempotencyKeyEntity.builder()
                        .idempotencyKey(idempotencyKey)
                        .correlationId(correlationId)
                        .build();
        idempotencyKeyRepository.save(idempotencyKeyEntity);
        final List<TradeEntity> tradeEntities = trades.stream().map(
                tradeMapper::toEntity).toList();
        tradeRepository.saveAll(tradeEntities);
        // Mark request as processing in Redis
        trades.forEach(trade -> {
            final TradeRequest tradeRequest = TradeRequest.builder()
                    .correlationId(correlationId)
                    .payload(trade)
                    .timestamp(LocalDateTime.now())
                    .build();
            final CompletableFuture<SendResult<String, Object>> future = kafkaProducer.send(tradeEventsTopic, tradeRequest);
            future.whenComplete((result, throwable) -> {
                if (throwable == null) {
                    log.info("Successfully published event {} to topic {}",
                            correlationId, tradeEventsTopic);
                } else {
                    log.error("Failed to publish event {} to topic {}",
                            correlationId, tradeEventsTopic, throwable);
                }
            });
        });
        return trades;
    }

    public List<Trade> getAllTrades() {
        return tradeRepository.findAll().stream().map(tradeMapper::toDto).collect(Collectors.toList());
    }

    private ErrorResponse createErrorResponse(String correlationId) {
        return ErrorResponse.builder()
                .correlationId(correlationId)
                .message("Duplicate file upload")
                .error(HttpStatus.INTERNAL_SERVER_ERROR.toString())
                .timestamp(LocalDateTime.now())
                .build();
    }

    private boolean isDuplicateRequest(String idempotencyKey) {
        return idempotencyKeyRepository.findByIdempotencyKey(idempotencyKey) != null;
    }

    private String generateIdempotencyKey(final List<Trade> trades) {
        final List<String> idempotencyIds = trades.stream().map(trade -> String.format("req:%s:%s:%s:%s:%s",
                trade.getTradeId(),
                trade.getPrice(),
                trade.getSide(),
                trade.getStatus(),
                trade.getSymbol())
        ).toList();
        return String.join("-", idempotencyIds);
    }
}

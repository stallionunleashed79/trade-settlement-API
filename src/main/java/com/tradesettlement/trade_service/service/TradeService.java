package com.tradesettlement.trade_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tradesettlement.trade_service.entities.IdempotencyKeyEntity;
import com.tradesettlement.trade_service.entities.OutboxEvent;
import com.tradesettlement.trade_service.entities.TradeEntity;
import com.tradesettlement.trade_service.exceptions.DuplicateFileException;
import com.tradesettlement.trade_service.mapper.TradeMapper;
import com.tradesettlement.trade_service.models.ErrorResponse;
import com.tradesettlement.trade_service.models.Trade;
import com.tradesettlement.trade_service.models.TradeSearchRequest;
import com.tradesettlement.trade_service.repository.IdempotencyKeyRepository;
import com.tradesettlement.trade_service.repository.OutboxEventRepository;
import com.tradesettlement.trade_service.repository.TradeRepository;
import com.tradesettlement.trade_service.util.TradeServiceUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TradeService {

    private final TradeServiceUtil tradeServiceUtil;
    private final IdempotencyKeyRepository idempotencyKeyRepository;
    private final TradeMapper tradeMapper;
    private final TradeRepository tradeRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final TradeFilterService tradeFilterService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${pagination.start.row.default}")
    private int startRow;

    @Value("${pagination.end.row.default}")
    private int endRow;

    @Transactional
    public List<Trade> uploadCsvFile(final MultipartFile file) {
        objectMapper.registerModule(new JavaTimeModule());
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
        final List<OutboxEvent> outboxEvents = trades.stream().map(
                trade -> createOutboxEvent(trade, correlationId)).toList();
        outboxEventRepository.saveAll(outboxEvents);
        return trades;
    }

    private OutboxEvent createOutboxEvent(final Trade request, final String correlationId) {
        try {
            OutboxEvent outboxEvent = new OutboxEvent();
            outboxEvent.setAggregateId(correlationId);
            outboxEvent.setEventType("USER_REQUEST");
            outboxEvent.setEventData(objectMapper.writeValueAsString(request));
            outboxEvent.setStatus(OutboxEvent.EventStatus.PENDING);
            log.info("Outbox event created for correlationId: {}", correlationId);
            return outboxEvent;
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize request for outbox event", e);
            throw new RuntimeException("Failed to create outbox event", e);
        }
    }

    public List<Trade> getAllTrades(final TradeSearchRequest tradeSearchRequest) {
        final Integer startRowValue = tradeSearchRequest.getStartRow() == null ? startRow : tradeSearchRequest.getStartRow();
        final Integer endRowValue = tradeSearchRequest.getEndRow() == null ? endRow : tradeSearchRequest.getEndRow();
        final Pageable pageable = PageRequest.of(startRowValue / (endRowValue - startRowValue), endRowValue - startRowValue); // Calculate page number and size
        return tradeFilterService.searchTrades(tradeSearchRequest, pageable);
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
        final List<String> idempotencyIds = trades.stream().limit(3).map(trade -> String.format("req:%s",
                trade.getTradeId())
        ).toList();
        return String.join("-", idempotencyIds);
    }
}

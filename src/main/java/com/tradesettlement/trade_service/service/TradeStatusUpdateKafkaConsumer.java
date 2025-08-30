package com.tradesettlement.trade_service.service;

import com.tradesettlement.trade_service.models.Trade;
import com.tradesettlement.trade_service.models.TradeKafkaRequest;
import com.tradesettlement.trade_service.models.TradeStatusUpdate;
import com.tradesettlement.trade_service.repository.TradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class TradeStatusUpdateKafkaConsumer {

    private final StatusBroadcastService statusBroadcastService;
    private final TradeRepository tradeRepository;

    @KafkaListener(topics = "${settlement.events.topic}", groupId = "${settlement.events.group.id}")
    public void handleStatusUpdate(final TradeKafkaRequest tradeKafkaRequest) {
        final Trade trade = tradeKafkaRequest.getPayload();
        TradeStatusUpdate update = TradeStatusUpdate.builder()
                .tradeId(trade.getTradeId())
                .status(trade.getStatus())
                .timestamp(LocalDateTime.now())
                .correlationId(tradeKafkaRequest.getCorrelationId())
                .build();
        tradeRepository.updateTradeStatusByTradeId(trade.getStatus(),
                trade.getTradeId());

        // Broadcast to all connected web clients
        statusBroadcastService.broadcastStatusUpdate(update);
    }
}
